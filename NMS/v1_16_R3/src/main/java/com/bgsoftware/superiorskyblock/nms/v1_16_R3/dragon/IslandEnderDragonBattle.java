package com.bgsoftware.superiorskyblock.nms.v1_16_R3.dragon;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.BlockPredicate;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.DragonControllerLanding;
import net.minecraft.server.v1_16_R3.DragonControllerPhase;
import net.minecraft.server.v1_16_R3.EnderDragonBattle;
import net.minecraft.server.v1_16_R3.EntityEnderDragon;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.HeightMap;
import net.minecraft.server.v1_16_R3.IDragonController;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.ShapeDetector;
import net.minecraft.server.v1_16_R3.ShapeDetectorBlock;
import net.minecraft.server.v1_16_R3.ShapeDetectorBuilder;
import net.minecraft.server.v1_16_R3.TileEntity;
import net.minecraft.server.v1_16_R3.TileEntityEnderPortal;
import net.minecraft.server.v1_16_R3.Vec3D;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotEnderDragonBattle extends EnderDragonBattle {

    private static final ReflectField<EnderDragonBattle> DRAGON_BATTLE = new ReflectField<EnderDragonBattle>(
            EntityEnderDragon.class, EnderDragonBattle.class, Modifier.PRIVATE | Modifier.FINAL, 1)
            .removeFinal();

    private static final ReflectField<Boolean> SCAN_FOR_LEGACY_PORTALS = new ReflectField<>(
            EnderDragonBattle.class, boolean.class, Modifier.PRIVATE, 3);

    private static final ReflectField<Boolean> WAS_DRAGON_KILLED = new ReflectField<>(
            EnderDragonBattle.class, boolean.class, Modifier.PRIVATE, 1);

    private static final ReflectField<Vec3D> LANDING_TARGET_POSITION = new ReflectField<>(
            DragonControllerLanding.class, Vec3D.class, Modifier.PRIVATE, 1);

    private static final ShapeDetector EXIT_PORTAL_PATTERN = ShapeDetectorBuilder.a()
            .a(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "})
            .a(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "})
            .a(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "})
            .a(new String[]{"  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  "})
            .a(new String[]{"       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       "})
            .a('#', ShapeDetectorBlock.a(BlockPredicate.a(Blocks.BEDROCK)))
            .b();

    private final Plot plot;
    private final BlockPosition plotBlockPosition;
    private final Vec3D plotBlockVectored;

    private final PlotEntityEnderDragon entityEnderDragon;

    private byte currentTick = 0;

    public PlotEnderDragonBattle(Plot plot, WorldServer worldServer, Location location) {
        this(plot, worldServer, new BlockPosition(location.getX(), location.getY(), location.getZ()),
                null);
    }

    public PlotEnderDragonBattle(Plot plot, WorldServer worldServer, BlockPosition plotBlockPosition,
                                   @Nullable PlotEntityEnderDragon plotEntityEnderDragon) {
        super(worldServer, worldServer.getSeed(), new NBTTagCompound());
        SCAN_FOR_LEGACY_PORTALS.set(this, false);
        WAS_DRAGON_KILLED.set(this, false);
        this.plot = plot;
        this.plotBlockPosition = plotBlockPosition;
        this.plotBlockVectored = Vec3D.c(plotBlockPosition);
        this.entityEnderDragon = plotEntityEnderDragon == null ? spawnEnderDragon() : plotEntityEnderDragon;
        DRAGON_BATTLE.set(this.entityEnderDragon, this);
    }

    @Override
    public void b() {
        // doServerTick

        DragonUtils.runWithPodiumPosition(this.plotBlockPosition, super::b);

        IDragonController currentController = this.entityEnderDragon.getDragonControllerManager().a();
        if (currentController instanceof DragonControllerLanding && !this.plotBlockVectored.equals(currentController.g())) {
            LANDING_TARGET_POSITION.set(currentController, this.plotBlockVectored);
        }

        if (++currentTick >= 20) {
            updateBattlePlayers();
            currentTick = 0;
        }
    }

    @Nullable
    @Override
    public ShapeDetector.ShapeDetectorCollection getExitPortalShape() {
        // findExitPortal

        int chunkX = this.plotBlockPosition.getX() >> 4;
        int chunkZ = this.plotBlockPosition.getZ() >> 4;

        for (int x = -8; x <= 8; ++x) {
            for (int z = -8; z <= 8; ++z) {
                Chunk chunk = this.world.getChunkAt(chunkX + x, chunkZ + z);

                for (TileEntity tileEntity : chunk.getTileEntities().values()) {
                    if (tileEntity instanceof TileEntityEnderPortal) {
                        ShapeDetector.ShapeDetectorCollection shapeDetectorCollection = EXIT_PORTAL_PATTERN.a(this.world, tileEntity.getPosition());
                        if (shapeDetectorCollection != null) {
                            if (this.exitPortalLocation == null)
                                this.exitPortalLocation = shapeDetectorCollection.a(3, 3, 3).getPosition();

                            return shapeDetectorCollection;
                        }
                    }
                }
            }
        }

        int highestBlock = this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, this.plotBlockPosition).getY();

        for (int y = highestBlock; y >= 0; --y) {
            BlockPosition currentPosition = new BlockPosition(this.plotBlockPosition.getX(), y, this.plotBlockPosition.getZ());

            ShapeDetector.ShapeDetectorCollection shapeDetectorCollection = EXIT_PORTAL_PATTERN.a(this.world, currentPosition);

            if (shapeDetectorCollection != null) {
                if (this.exitPortalLocation == null)
                    this.exitPortalLocation = shapeDetectorCollection.a(3, 3, 3).getPosition();

                return shapeDetectorCollection;
            }
        }

        return null;
    }

    @Override
    public void resetCrystals() {
        // resetSpikeCrystals

        DragonUtils.runWithPodiumPosition(this.plotBlockPosition, super::resetCrystals);
    }

    public void removeBattlePlayers() {
        this.bossBattle.getPlayers().forEach(this.bossBattle::removePlayer);
    }

    public PlotEntityEnderDragon getEnderDragon() {
        return this.entityEnderDragon;
    }

    private void updateBattlePlayers() {
        Set<UUID> nearbyPlayers = new HashSet<>();

        for (SuperiorPlayer superiorPlayer : plot.getAllPlayersInside()) {
            Player bukkitPlayer = superiorPlayer.asPlayer();
            assert bukkitPlayer != null;

            EntityPlayer entityPlayer = ((CraftPlayer) bukkitPlayer).getHandle();

            if (entityPlayer.getWorld().equals(this.world)) {
                this.bossBattle.addPlayer(entityPlayer);
                nearbyPlayers.add(entityPlayer.getUniqueID());
            }
        }

        this.bossBattle.getPlayers().stream()
                .filter(entityPlayer -> !nearbyPlayers.contains(entityPlayer.getUniqueID()))
                .forEach(this.bossBattle::removePlayer);
    }

    private PlotEntityEnderDragon spawnEnderDragon() {
        PlotEntityEnderDragon entityEnderDragon = new PlotEntityEnderDragon(this.world, plotBlockPosition);
        entityEnderDragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
        entityEnderDragon.setPositionRotation(plotBlockPosition.getX(), 128,
                plotBlockPosition.getZ(), this.world.getRandom().nextFloat() * 360.0F, 0.0F);

        this.world.addEntity(entityEnderDragon, CreatureSpawnEvent.SpawnReason.NATURAL);

        this.dragonUUID = entityEnderDragon.getUniqueID();
        this.resetCrystals(); // scan for crystals

        return entityEnderDragon;
    }

}

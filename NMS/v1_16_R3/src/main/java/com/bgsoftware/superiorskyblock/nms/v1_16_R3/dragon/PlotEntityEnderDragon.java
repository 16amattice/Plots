package com.bgsoftware.superiorskyblock.nms.v1_16_R3.dragon;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityEnderDragon;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.World;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEnderDragon;

public class PlotEntityEnderDragon extends EntityEnderDragon {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    public static EntityEnderDragon fromEntityTypes(EntityTypes<? extends EntityEnderDragon> entityTypes, World world) {
        return plugin.getGrid().isPlotsWorld(world.getWorld()) ? new PlotEntityEnderDragon(world) :
                new EntityEnderDragon(entityTypes, world);
    }

    private BlockPosition plotBlockPosition;

    public PlotEntityEnderDragon(WorldServer worldServer, BlockPosition plotBlockPosition) {
        this(worldServer);
        this.plotBlockPosition = plotBlockPosition;
    }

    private PlotEntityEnderDragon(World world) {
        super(EntityTypes.ENDER_DRAGON, world);
    }

    @Override
    public void loadData(NBTTagCompound nbtTagCompound) {
        super.loadData(nbtTagCompound);

        if (!(world instanceof WorldServer) || !(((WorldServer) world).getDragonBattle() instanceof EndWorldEnderDragonBattleHandler))
            return;

        EndWorldEnderDragonBattleHandler dragonBattleHandler = (EndWorldEnderDragonBattleHandler) ((WorldServer) world).getDragonBattle();

        Location entityLocation = getBukkitEntity().getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);

        if (plot == null)
            return;

        Location middleBlock = plugin.getSettings().getWorlds().getEnd().getPortalOffset()
                .applyToLocation(plot.getCenter(org.bukkit.World.Environment.THE_END));
        this.plotBlockPosition = new BlockPosition(middleBlock.getX(), middleBlock.getY(), middleBlock.getZ());

        dragonBattleHandler.addDragonBattle(plot.getUniqueId(), new PlotEnderDragonBattle(plot,
                (WorldServer) world, this.plotBlockPosition, this));
    }

    @Override
    public void movementTick() {
        DragonUtils.runWithPodiumPosition(this.plotBlockPosition, super::movementTick);
    }

    @Override
    public CraftEnderDragon getBukkitEntity() {
        return (CraftEnderDragon) super.getBukkitEntity();
    }

}

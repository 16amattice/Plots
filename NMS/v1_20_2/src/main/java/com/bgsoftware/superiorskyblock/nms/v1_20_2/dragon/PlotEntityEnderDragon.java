package com.bgsoftware.superiorskyblock.nms.v1_20_2.dragon;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEnderDragon;

public class PlotEntityEnderDragon extends EnderDragon {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    @NotNull
    public static EnderDragon fromEntityTypes(EntityType<? extends EnderDragon> entityTypes, Level level) {
        return plugin.getGrid().isPlotsWorld(level.getWorld()) ? new PlotEntityEnderDragon(level) :
                new EnderDragon(entityTypes, level);
    }

    private final ServerLevel serverLevel;
    private BlockPos plotBlockPos;

    public PlotEntityEnderDragon(Level level, BlockPos plotBlockPos) {
        this(level);
        this.plotBlockPos = plotBlockPos;
    }

    private PlotEntityEnderDragon(Level level) {
        super(EntityType.ENDER_DRAGON, level);
        this.serverLevel = (ServerLevel) level;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        // loadData

        super.readAdditionalSaveData(compoundTag);

        if (!(this.serverLevel.getDragonFight() instanceof EndWorldEndDragonFightHandler dragonBattleHandler))
            return;

        Location entityLocation = getBukkitEntity().getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);

        if (plot == null)
            return;

        Location middleBlock = plugin.getSettings().getWorlds().getEnd().getPortalOffset()
                .applyToLocation(plot.getCenter(org.bukkit.World.Environment.THE_END));
        this.plotBlockPos = new BlockPos(middleBlock.getBlockX(), middleBlock.getBlockY(), middleBlock.getBlockZ());

        PlotEndDragonFight dragonBattle = new PlotEndDragonFight(plot, this.serverLevel, this.plotBlockPos, this);
        dragonBattleHandler.addDragonFight(plot.getUniqueId(), dragonBattle);
    }

    @Override
    public void aiStep() {
        DragonUtils.runWithPodiumPosition(this.plotBlockPos, super::aiStep);
    }

    @Override
    @NotNull
    public CraftEnderDragon getBukkitEntity() {
        return (CraftEnderDragon) super.getBukkitEntity();
    }

}

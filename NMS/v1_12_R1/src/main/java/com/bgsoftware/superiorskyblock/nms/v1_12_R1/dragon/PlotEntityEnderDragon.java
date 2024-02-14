package com.bgsoftware.superiorskyblock.nms.v1_12_R1.dragon;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EnderDragonBattle;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.WorldProviderTheEnd;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEnderDragon;

public class PlotEntityEnderDragon extends EntityEnderDragon {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private BlockPosition plotBlockPosition;

    public PlotEntityEnderDragon(World world) {
        // Used when loading entities to the world.
        super(world);
        this.plotBlockPosition = BlockPosition.ZERO;
    }

    public PlotEntityEnderDragon(WorldServer worldServer, BlockPosition plotBlockPosition) {
        super(worldServer);
        this.plotBlockPosition = plotBlockPosition;
    }

    @Override
    public void a(NBTTagCompound nbtTagCompound) {
        super.a(nbtTagCompound);

        if (!(world.worldProvider instanceof WorldProviderTheEnd) || !plugin.getGrid().isPlotsWorld(world.getWorld()))
            return;

        EnderDragonBattle enderDragonBattle = ((WorldProviderTheEnd) world.worldProvider).t();

        if (!(enderDragonBattle instanceof EndWorldEnderDragonBattleHandler))
            return;

        EndWorldEnderDragonBattleHandler dragonBattleHandler = (EndWorldEnderDragonBattleHandler) enderDragonBattle;

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
    public void n() {
        DragonUtils.runWithPodiumPosition(this.plotBlockPosition, super::n);
    }

    @Override
    public CraftEnderDragon getBukkitEntity() {
        return (CraftEnderDragon) super.getBukkitEntity();
    }

}

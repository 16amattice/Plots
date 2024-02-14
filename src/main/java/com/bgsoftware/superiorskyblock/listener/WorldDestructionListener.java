package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.LinkedList;
import java.util.List;

public class WorldDestructionListener implements Listener {

    private final SuperiorSkyblockPlugin plugin;

    public WorldDestructionListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    //Checking for structures growing outside plot.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onStructureGrow(StructureGrowEvent e) {
        Plot plot = plugin.getGrid().getPlotAt(e.getLocation());
        if (plot != null && plugin.getGrid().isPlotsWorld(e.getLocation().getWorld()))
            e.getBlocks().removeIf(blockState -> !plot.isInsideRange(blockState.getLocation()));
    }

    //Checking for chorus flower spread outside plot.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockSpread(BlockSpreadEvent e) {
        if (preventDestruction(e.getSource().getLocation()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        List<Location> blockLocations = new LinkedList<>();
        e.getBlocks().forEach(block -> blockLocations.add(block.getRelative(e.getDirection()).getLocation()));
        if (preventMultiDestruction(e.getBlock().getLocation(), blockLocations))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        List<Location> blockLocations = new LinkedList<>();
        e.getBlocks().forEach(block -> blockLocations.add(block.getRelative(e.getDirection()).getLocation()));
        if (preventMultiDestruction(e.getBlock().getLocation(), blockLocations))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFlow(BlockFromToEvent e) {
        Location blockLocation = e.getBlock().getRelative(e.getFace()).getLocation();
        if (preventDestruction(blockLocation))
            e.setCancelled(true);
    }

    /* INTERNAL */

    private boolean preventDestruction(Location location) {
        Plot plot = plugin.getGrid().getPlotAt(location);
        return plot == null ? plugin.getGrid().isPlotsWorld(location.getWorld()) : !plot.isInsideRange(location);
    }

    private boolean preventMultiDestruction(Location plotLocation, List<Location> blockLocations) {
        Plot plot = plugin.getGrid().getPlotAt(plotLocation);

        if (plot == null)
            return plugin.getGrid().isPlotsWorld(plotLocation.getWorld());

        for (Location blockLocation : blockLocations) {
            if (!plot.isInsideRange(blockLocation))
                return true;
        }

        return false;
    }

}

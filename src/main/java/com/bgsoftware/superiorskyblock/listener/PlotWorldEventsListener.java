package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.service.hologram.HologramsService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PlotWorldEventsListener implements Listener {

    private final SuperiorSkyblockPlugin plugin;
    private final LazyReference<HologramsService> hologramsService = new LazyReference<HologramsService>() {
        @Override
        protected HologramsService create() {
            return plugin.getServices().getService(HologramsService.class);
        }
    };

    public PlotWorldEventsListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockRedstone(BlockRedstoneEvent e) {
        if (!plugin.getSettings().isDisableRedstoneOffline() && !plugin.getSettings().getAFKIntegrations().isDisableRedstone())
            return;

        Plot plot = plugin.getGrid().getPlotAt(e.getBlock().getLocation());

        if (plot == null || plot.isSpawn())
            return;

        if ((plugin.getSettings().isDisableRedstoneOffline() && !plot.isCurrentlyActive()) ||
                (plugin.getSettings().getAFKIntegrations().isDisableRedstone() &&
                        plot.getAllPlayersInside().stream().allMatch(SuperiorPlayer::isAFK))) {
            e.setNewCurrent(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntitySpawn(CreatureSpawnEvent e) {
        if (!plugin.getSettings().getAFKIntegrations().isDisableSpawning() ||
                hologramsService.get().isHologram(e.getEntity()))
            return;

        Plot plot = plugin.getGrid().getPlotAt(e.getEntity().getLocation());

        if (plot == null || plot.isSpawn() || !plot.getAllPlayersInside().stream().allMatch(SuperiorPlayer::isAFK))
            return;

        e.setCancelled(true);
    }

}

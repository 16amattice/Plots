package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.world.EntityTeleports;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class PlotOutsideListener implements Listener {

    private final SuperiorSkyblockPlugin plugin;

    public PlotOutsideListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onMinecartRightClick(PlayerInteractAtEntityEvent e) {
        if (!plugin.getSettings().isStopLeaving())
            return;

        Plot playerPlot = plugin.getGrid().getPlotAt(e.getPlayer().getLocation());
        Plot entityPlot = plugin.getGrid().getPlotAt(e.getRightClicked().getLocation());

        if (plugin.getPlayers().getSuperiorPlayer(e.getPlayer()).hasBypassModeEnabled())
            return;

        if (playerPlot != null && (entityPlot == null || entityPlot.equals(playerPlot)) &&
                !playerPlot.isInsideRange(e.getRightClicked().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onMinecartRightClick(VehicleEnterEvent e) {
        if (!plugin.getSettings().isStopLeaving())
            return;

        Plot playerPlot = plugin.getGrid().getPlotAt(e.getEntered().getLocation());
        Plot entityPlot = plugin.getGrid().getPlotAt(e.getVehicle().getLocation());

        if (e.getEntered() instanceof Player && plugin.getPlayers().getSuperiorPlayer(e.getEntered()).hasBypassModeEnabled())
            return;

        if (playerPlot != null && (entityPlot == null || entityPlot.equals(playerPlot)) &&
                !playerPlot.isInsideRange(e.getVehicle().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVehicleRide(VehicleMoveEvent e) {
        if (!plugin.getSettings().isStopLeaving())
            return;

        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;

        Plot toPlot = plugin.getGrid().getPlotAt(e.getTo());
        Plot fromPlot = plugin.getGrid().getPlotAt(e.getFrom());

        if (fromPlot != null && e.getVehicle().getWorld().equals(e.getTo().getWorld()) &&
                (toPlot == null || toPlot.equals(fromPlot)) && !fromPlot.isInsideRange(e.getTo())) {
            Entity passenger = e.getVehicle().getPassenger();
            SuperiorPlayer superiorPlayer = passenger instanceof Player ? plugin.getPlayers().getSuperiorPlayer(passenger) : null;
            if (passenger != null && (superiorPlayer == null || !superiorPlayer.hasBypassModeEnabled())) {
                e.getVehicle().setPassenger(null);
                EntityTeleports.teleport(passenger, e.getFrom());
            }
        }
    }

}

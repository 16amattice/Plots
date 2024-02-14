package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.player.SuperiorNPCPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlotPreviewListener implements Listener {

    private final SuperiorSkyblockPlugin plugin;

    public PlotPreviewListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        // Cancelling plot preview mode
        if (plugin.getGrid().getPlotPreview(superiorPlayer) != null) {
            plugin.getGrid().cancelPlotPreview(superiorPlayer);
            /* cancelPlotPreview changes the GameMode and teleports the player later.
            In this case tho, we want the things to be instant - no async, no nothing. */
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().teleport(plugin.getGrid().getSpawnPlot().getCenter(plugin.getSettings().getWorlds().getDefaultWorld()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerTeleport(PlayerTeleportEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR &&
                plugin.getGrid().getPlotPreview(superiorPlayer) != null)
            e.setCancelled(true);
    }

}

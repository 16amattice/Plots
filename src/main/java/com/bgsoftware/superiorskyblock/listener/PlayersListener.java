package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.HitActionResult;
import com.bgsoftware.superiorskyblock.api.events.PlotUncoopPlayerEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.player.PlayerStatus;
import com.bgsoftware.superiorskyblock.api.player.respawn.RespawnAction;
import com.bgsoftware.superiorskyblock.api.service.region.MoveResult;
import com.bgsoftware.superiorskyblock.api.service.region.RegionManagerService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.Materials;
import com.bgsoftware.superiorskyblock.core.Mutable;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.SPlotChest;
import com.bgsoftware.superiorskyblock.plot.notifications.PlotNotifications;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.plot.top.SortingTypes;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;
import com.bgsoftware.superiorskyblock.player.SuperiorNPCPlayer;
import com.bgsoftware.superiorskyblock.player.chat.PlayerChat;
import com.bgsoftware.superiorskyblock.player.respawn.RespawnActions;
import com.bgsoftware.superiorskyblock.world.BukkitEntities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlayersListener implements Listener {

    private final LazyReference<RegionManagerService> regionManagerService = new LazyReference<RegionManagerService>() {
        @Override
        protected RegionManagerService create() {
            return plugin.getServices().getService(RegionManagerService.class);
        }
    };
    private final SuperiorSkyblockPlugin plugin;

    public PlayersListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    /* PLAYER NOTIFIERS */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerLogin(PlayerLoginEvent e) {
        List<SuperiorPlayer> duplicatedPlayers = plugin.getPlayers().matchAllPlayers(superiorPlayer ->
                superiorPlayer.getName().equalsIgnoreCase(e.getPlayer().getName()) &&
                        !superiorPlayer.getUniqueId().equals(e.getPlayer().getUniqueId()));

        if (!duplicatedPlayers.isEmpty()) {
            Log.info("Changing UUID of " + e.getPlayer().getName() + " to " + e.getPlayer().getUniqueId());

            SuperiorPlayer playerWithNewUUID = plugin.getPlayers().getSuperiorPlayer(e.getPlayer().getUniqueId(), false);

            if (playerWithNewUUID != null) {
                // Even tho we have duplicates, there's already a record for the new player.
                // Therefore, we just want to delete the old records from DB and cache.
                Log.info("Detected a record for the new player uuid already - deleting old ones...");
                // Delete all records
                duplicatedPlayers.forEach(duplicatedPlayer -> {
                    plugin.getPlayers().replacePlayers(duplicatedPlayer, null);
                    plugin.getPlayers().getPlayersContainer().removePlayer(duplicatedPlayer);
                });
                // We make sure the new player is correctly set in all caches by removing it and adding it.
                plugin.getPlayers().getPlayersContainer().removePlayer(playerWithNewUUID);
                plugin.getPlayers().getPlayersContainer().addPlayer(playerWithNewUUID);
            } else {
                // We first want to remove all original players.
                duplicatedPlayers.forEach(plugin.getPlayers().getPlayersContainer()::removePlayer);

                // We now want to create the new player.
                SuperiorPlayer newPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer().getUniqueId(), true, false);

                // We now want to replace all existing players
                duplicatedPlayers.forEach(originalPlayer -> {
                    if (originalPlayer != newPlayer)
                        plugin.getPlayers().replacePlayers(originalPlayer, newPlayer);
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        // Updating the name of the player.
        if (!superiorPlayer.getName().equals(e.getPlayer().getName())) {
            plugin.getEventsBus().callPlayerChangeNameEvent(superiorPlayer, e.getPlayer().getName());
            superiorPlayer.updateName();
        }

        // Handling player join
        if (superiorPlayer.isShownAsOnline())
            PlotNotifications.notifyPlayerJoin(superiorPlayer);

        Mutable<Boolean> teleportToSpawn = new Mutable<>(false);

        Location playerLocation = e.getPlayer().getLocation();
        Plot plot = plugin.getGrid().getPlotAt(playerLocation);

        MoveResult moveResult = this.regionManagerService.get().handlePlayerJoin(superiorPlayer, playerLocation);
        if (moveResult != MoveResult.SUCCESS) {
            teleportToSpawn.setValue(true);
        }

        BukkitExecutor.sync(() -> {
            if (!e.getPlayer().isOnline())
                return;

            // Updating skin of the player
            if (!plugin.getProviders().notifySkinsListeners(superiorPlayer))
                plugin.getNMSPlayers().setSkinTexture(superiorPlayer);

            if (!superiorPlayer.hasBypassModeEnabled()) {
                Plot delayedPlot = plugin.getGrid().getPlotAt(e.getPlayer().getLocation());
                // Checking if the player is in the plots world, not inside an plot.
                if ((delayedPlot == plot && teleportToSpawn.getValue()) ||
                        (plugin.getGrid().isPlotsWorld(superiorPlayer.getWorld()) && delayedPlot == null)) {
                    superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
                    if (!teleportToSpawn.getValue())
                        Message.PLOT_GOT_DELETED_WHILE_INSIDE.send(superiorPlayer);
                }
            }

            // Checking auto language detection
            if (plugin.getSettings().isAutoLanguageDetection() && !e.getPlayer().hasPlayedBefore()) {
                Locale playerLocale = plugin.getNMSPlayers().getPlayerLocale(e.getPlayer());
                if (playerLocale != null && PlayerLocales.isValidLocale(playerLocale) &&
                        !superiorPlayer.getUserLocale().equals(playerLocale)) {
                    if (plugin.getEventsBus().callPlayerChangeLanguageEvent(superiorPlayer, playerLocale))
                        superiorPlayer.setUserLocale(playerLocale);
                }
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerQuit(PlayerQuitEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        // Removing coop status from other plots.
        for (Plot _plot : plugin.getGrid().getPlots()) {
            if (_plot.isCoop(superiorPlayer)) {
                if (plugin.getEventsBus().callPlotUncoopPlayerEvent(_plot, null, superiorPlayer, PlotUncoopPlayerEvent.UncoopReason.SERVER_LEAVE)) {
                    _plot.removeCoop(superiorPlayer);
                    PlotUtils.sendMessage(_plot, Message.UNCOOP_LEFT_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());
                }
            }
        }

        // Handling player quit
        if (superiorPlayer.isShownAsOnline())
            PlotNotifications.notifyPlayerQuit(superiorPlayer);

        // Remove coop players
        Plot plot = superiorPlayer.getPlot();
        if (plot != null && plugin.getSettings().isAutoUncoopWhenAlone() && !plot.getCoopPlayers().isEmpty()) {
            boolean shouldRemoveCoops = plot.getPlotMembers(true).stream().noneMatch(plotMember ->
                    plotMember != superiorPlayer && plot.hasPermission(plotMember, PlotPrivileges.UNCOOP_MEMBER) && plotMember.isOnline());

            if (shouldRemoveCoops) {
                for (SuperiorPlayer coopPlayer : plot.getCoopPlayers()) {
                    plot.removeCoop(coopPlayer);
                    Message.UNCOOP_AUTO_ANNOUNCEMENT.send(coopPlayer);
                }
            }
        }

        this.regionManagerService.get().handlePlayerQuit(superiorPlayer, e.getPlayer().getLocation());

        // Remove all player chat-listeners
        PlayerChat.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            PlotNotifications.notifyPlayerQuit(plugin.getPlayers().getSuperiorPlayer(e.getPlayer()));
        } else if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            PlotNotifications.notifyPlayerJoin(plugin.getPlayers().getSuperiorPlayer(e.getPlayer()));
        }
    }

    /* PLAYER MOVES */

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();

        boolean movedFullBlock = from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() ||
                from.getBlockY() != to.getBlockY();

        if (!movedFullBlock)
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer instanceof SuperiorNPCPlayer || superiorPlayer.getPlayerStatus() == PlayerStatus.VOID_TELEPORT)
            return;

        MoveResult moveResult = this.regionManagerService.get().handlePlayerMove(superiorPlayer, from, to);
        switch (moveResult) {
            case VOID_TELEPORT:
            case SUCCESS:
                break;
            default:
                e.setCancelled(true);
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerTeleport(PlayerTeleportEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer == null || superiorPlayer instanceof SuperiorNPCPlayer)
            return;

        MoveResult moveResult = this.regionManagerService.get().handlePlayerTeleport(superiorPlayer, e.getFrom(), e.getTo());
        if (moveResult != MoveResult.SUCCESS)
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Plot plot = plugin.getGrid().getPlotAt(e.getPlayer().getLocation());
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (plot != null && superiorPlayer.hasPlotFlyEnabled() && !e.getPlayer().getAllowFlight() &&
                plot.hasPermission(superiorPlayer, PlotPrivileges.FLY))
            BukkitExecutor.sync(() -> {
                e.getPlayer().setAllowFlight(true);
                e.getPlayer().setFlying(true);
            }, 1L);
    }

    /* PVP */

    @EventHandler(priority = EventPriority.NORMAL /* Set to NORMAL, so it doesn't conflict with vanish plugins */, ignoreCancelled = true)
    private void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer((Player) e.getEntity());

        if (targetPlayer instanceof SuperiorNPCPlayer)
            return;

        Plot plot = plugin.getGrid().getPlotAt(e.getEntity().getLocation());

        SuperiorPlayer damagerPlayer = !(e instanceof EntityDamageByEntityEvent) ? null :
                BukkitEntities.getPlayerSource(((EntityDamageByEntityEvent) e).getDamager())
                        .map(plugin.getPlayers()::getSuperiorPlayer).orElse(null);

        // Some plugins, such as Sentinel, may actually cause a NPC to attack.
        if (damagerPlayer instanceof SuperiorNPCPlayer)
            return;

        if (damagerPlayer == null) {
            if (plot != null) {
                if (plot.isSpawn() ? (plugin.getSettings().getSpawn().isProtected() && !plugin.getSettings().getSpawn().isPlayersDamage()) :
                        ((!plugin.getSettings().isVisitorsDamage() && plot.isVisitor(targetPlayer, false)) ||
                                (!plugin.getSettings().isCoopDamage() && plot.isCoop(targetPlayer))))
                    e.setCancelled(true);
            }

            return;
        }

        boolean cancelFlames = false;
        boolean cancelEvent = false;
        Message messageToSend = null;

        HitActionResult hitActionResult = damagerPlayer.canHit(targetPlayer);

        switch (hitActionResult) {
            case PLOT_TEAM_PVP:
                messageToSend = Message.HIT_PLOT_MEMBER;
                break;
            case PLOT_PVP_DISABLE:
            case TARGET_PLOT_PVP_DISABLE:
                messageToSend = Message.HIT_PLAYER_IN_PLOT;
                break;
        }

        if (hitActionResult != HitActionResult.SUCCESS) {
            cancelFlames = true;
            cancelEvent = true;
        }

        if (cancelEvent)
            e.setCancelled(true);

        if (messageToSend != null)
            messageToSend.send(damagerPlayer);

        Player target = targetPlayer.asPlayer();

        if (target != null && cancelFlames && ((EntityDamageByEntityEvent) e).getDamager() instanceof Arrow &&
                target.getFireTicks() > 0)
            target.setFireTicks(0);
    }

    /* CHAT */

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlayerAsyncChatLowest(AsyncPlayerChatEvent e) {
        // PlayerChat should be on LOWEST priority so other chat plugins don't conflict.
        PlayerChat playerChat = PlayerChat.getChatListener(e.getPlayer());
        if (playerChat != null && playerChat.supply(e.getMessage())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerAsyncChat(AsyncPlayerChatEvent e) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());
        Plot plot = superiorPlayer.getPlot();

        if (superiorPlayer.hasTeamChatEnabled()) {
            if (plot == null) {
                if (!plugin.getEventsBus().callPlayerToggleTeamChatEvent(superiorPlayer))
                    return;

                superiorPlayer.toggleTeamChat();
                return;
            }

            e.setCancelled(true);

            EventResult<String> eventResult = plugin.getEventsBus().callPlotChatEvent(plot, superiorPlayer,
                    superiorPlayer.hasPermissionWithoutOP("superior.chat.color") ?
                            Formatters.COLOR_FORMATTER.format(e.getMessage()) : e.getMessage());

            if (eventResult.isCancelled())
                return;

            PlotUtils.sendMessage(plot, Message.TEAM_CHAT_FORMAT, Collections.emptyList(),
                    superiorPlayer.getPlayerRole(), superiorPlayer.getName(), eventResult.getResult());

            Message.SPY_TEAM_CHAT_FORMAT.send(Bukkit.getConsoleSender(), superiorPlayer.getPlayerRole().getDisplayName(),
                    superiorPlayer.getName(), eventResult.getResult());
            for (Player _onlinePlayer : Bukkit.getOnlinePlayers()) {
                SuperiorPlayer onlinePlayer = plugin.getPlayers().getSuperiorPlayer(_onlinePlayer);
                if (onlinePlayer.hasAdminSpyEnabled())
                    Message.SPY_TEAM_CHAT_FORMAT.send(onlinePlayer, superiorPlayer.getPlayerRole().getDisplayName(),
                            superiorPlayer.getName(), eventResult.getResult());
            }
        } else {
            String plotNameFormat = Message.NAME_CHAT_FORMAT.getMessage(PlayerLocales.getDefaultLocale(),
                    plot == null ? "" : plugin.getSettings().getPlotNames().isColorSupport() ?
                            Formatters.COLOR_FORMATTER.format(plot.getName()) : plot.getName());

            e.setFormat(e.getFormat()
                    .replace("{plot-level}", String.valueOf(plot == null ? 0 : plot.getPlotLevel()))
                    .replace("{plot-level-format}", String.valueOf(plot == null ? 0 :
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getPlotLevel(), superiorPlayer.getUserLocale())))
                    .replace("{plot-worth}", String.valueOf(plot == null ? 0 : plot.getWorth()))
                    .replace("{plot-worth-format}", String.valueOf(plot == null ? 0 :
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getWorth(), superiorPlayer.getUserLocale())))
                    .replace("{plot-name}", plotNameFormat == null ? "" : plotNameFormat)
                    .replace("{plot-role}", superiorPlayer.getPlayerRole().getDisplayName())
                    .replace("{plot-position-worth}", plot == null ? "" : (plugin.getGrid().getPlotPosition(plot, SortingTypes.BY_WORTH) + 1) + "")
                    .replace("{plot-position-level}", plot == null ? "" : (plugin.getGrid().getPlotPosition(plot, SortingTypes.BY_LEVEL) + 1) + "")
                    .replace("{plot-position-rating}", plot == null ? "" : (plugin.getGrid().getPlotPosition(plot, SortingTypes.BY_RATING) + 1) + "")
                    .replace("{plot-position-players}", plot == null ? "" : (plugin.getGrid().getPlotPosition(plot, SortingTypes.BY_PLAYERS) + 1) + "")
            );
        }
    }

    /* SCHEMATICS */

    @EventHandler
    private void onSchematicSelection(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Materials.GOLDEN_AXE.toBukkitType() ||
                !(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK))
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (!superiorPlayer.hasSchematicModeEnabled())
            return;

        e.setCancelled(true);

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            Message.SCHEMATIC_RIGHT_SELECT.send(superiorPlayer, Formatters.LOCATION_FORMATTER.format(e.getClickedBlock().getLocation()));
            superiorPlayer.setSchematicPos1(e.getClickedBlock());
        } else {
            Message.SCHEMATIC_LEFT_SELECT.send(superiorPlayer, Formatters.LOCATION_FORMATTER.format(e.getClickedBlock().getLocation()));
            superiorPlayer.setSchematicPos2(e.getClickedBlock());
        }

        if (superiorPlayer.getSchematicPos1() != null && superiorPlayer.getSchematicPos2() != null)
            Message.SCHEMATIC_READY_TO_CREATE.send(superiorPlayer);
    }

    /* PLOT CHESTS */

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPlotChestInteract(InventoryClickEvent e) {
        InventoryHolder inventoryHolder = e.getView().getTopInventory() == null ? null : e.getView().getTopInventory().getHolder();

        if (!(inventoryHolder instanceof PlotChest))
            return;

        SPlotChest plotChest = (SPlotChest) inventoryHolder;

        if (plotChest.isUpdating()) {
            e.setCancelled(true);
        } else {
            plotChest.updateContents();
        }
    }

    /* VOID TELEPORT */

    @EventHandler
    private void onPlayerFall(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL || !(e.getEntity() instanceof Player))
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getEntity());
        if (superiorPlayer.getPlayerStatus() == PlayerStatus.VOID_TELEPORT)
            e.setCancelled(true);
    }

    /* PLAYER DEATH */

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        for (RespawnAction respawnAction : plugin.getSettings().getPlayerRespawn()) {
            if (respawnAction == RespawnActions.VANILLA || respawnAction.canPerform(event)) {
                respawnAction.perform(event);
                return;
            }
        }

    }

}

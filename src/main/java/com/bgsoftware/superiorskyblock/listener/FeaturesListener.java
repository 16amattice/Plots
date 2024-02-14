package com.bgsoftware.superiorskyblock.listener;

import com.bgsoftware.common.reflection.ReflectMethod;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotCreateEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotEnterEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotLeaveEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotTransferEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotWorthCalculatedEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.service.region.InteractionResult;
import com.bgsoftware.superiorskyblock.api.service.region.RegionManagerService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.PlayerHand;
import com.bgsoftware.superiorskyblock.core.key.ConstantKeys;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.service.region.ProtectionHelper;
import com.bgsoftware.superiorskyblock.world.BukkitItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class FeaturesListener implements Listener {

    private final Map<Class<? extends Event>, EventMethods> CACHED_EVENT_METHODS = new HashMap<>();

    private final SuperiorSkyblockPlugin plugin;
    private final LazyReference<RegionManagerService> protectionManager = new LazyReference<RegionManagerService>() {
        @Override
        protected RegionManagerService create() {
            return plugin.getServices().getService(RegionManagerService.class);
        }
    };

    public FeaturesListener(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    /* EVENT COMMANDS */

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlotEvent(PlotEvent event) {
        List<String> commands = plugin.getSettings().getEventCommands().get(event.getClass().getSimpleName().toLowerCase(Locale.ENGLISH));

        if (commands == null)
            return;

        EventMethods eventMethods = CACHED_EVENT_METHODS.computeIfAbsent(event.getClass(), EventMethods::new);

        Map<String, String> placeholdersReplaces = new HashMap<>();

        placeholdersReplaces.put("%plot%", event.getPlot().getName());
        eventMethods.getPlayer(event).ifPresent(playerName -> placeholdersReplaces.put("%player%", playerName));
        eventMethods.getTarget(event).ifPresent(targetName -> placeholdersReplaces.put("%target%", targetName));

        if (event instanceof PlotCreateEvent) {
            placeholdersReplaces.put("%schematic%", ((PlotCreateEvent) event).getSchematic());
        } else if (event instanceof PlotEnterEvent) {
            placeholdersReplaces.put("%enter-cause%", ((PlotEnterEvent) event).getCause().name());
        } else if (event instanceof PlotLeaveEvent) {
            placeholdersReplaces.put("%leave-cause%", ((PlotLeaveEvent) event).getCause().name());
        } else if (event instanceof PlotTransferEvent) {
            placeholdersReplaces.put("%old-owner%", ((PlotTransferEvent) event).getOldOwner().getName());
            placeholdersReplaces.put("%new-owner%", ((PlotTransferEvent) event).getNewOwner().getName());
        } else if (event instanceof PlotWorthCalculatedEvent) {
            placeholdersReplaces.put("%worth%", ((PlotWorthCalculatedEvent) event).getWorth().toString());
            placeholdersReplaces.put("%level%", ((PlotWorthCalculatedEvent) event).getLevel().toString());
        }

        for (String command : commands) {
            for (Map.Entry<String, String> replaceEntry : placeholdersReplaces.entrySet())
                command = command.replace(replaceEntry.getKey(), replaceEntry.getValue());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    /* OBSIDIAN TO LAVA */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onObsidianClick(PlayerInteractEvent e) {
        if (!plugin.getSettings().isObsidianToLava() || e.getItem() == null || e.getClickedBlock() == null ||
                e.getItem().getType() != Material.BUCKET || e.getClickedBlock().getType() != Material.OBSIDIAN)
            return;

        if (plugin.getStackedBlocks().getStackedBlockAmount(e.getClickedBlock()) != 1)
            return;

        if (!plugin.getGrid().isPlotsWorld(e.getClickedBlock().getWorld()))
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());
        InteractionResult interactionResult = this.protectionManager.get().handleBlockBreak(superiorPlayer, e.getClickedBlock());
        if (ProtectionHelper.shouldPreventInteraction(interactionResult, superiorPlayer, true))
            return;

        Plot plot = plugin.getGrid().getPlotAt(e.getClickedBlock().getLocation());
        if (plot == null)
            return;

        e.setCancelled(true);


        ItemStack inHandItem = e.getItem().clone();
        inHandItem.setAmount(inHandItem.getAmount() - 1);

        PlayerHand usedItem = BukkitItems.getHand(e);
        BukkitItems.setHandItem(e.getPlayer(), usedItem, inHandItem.getAmount() == 0 ? null : inHandItem);

        BukkitItems.addItem(new ItemStack(Material.LAVA_BUCKET), e.getPlayer().getInventory(),
                e.getPlayer().getLocation());

        plot.handleBlockBreak(ConstantKeys.OBSIDIAN, 1);

        e.getClickedBlock().setType(Material.AIR);
    }

    /* VISITORS BLOCKED COMMANDS */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (plugin.getSettings().getBlockedVisitorsCommands().isEmpty())
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(e.getPlayer());

        if (superiorPlayer.hasBypassModeEnabled())
            return;

        Plot plot = plugin.getGrid().getPlotAt(e.getPlayer().getLocation());

        if (plot == null || plot.isSpawn() || !plot.isVisitor(superiorPlayer, true))
            return;

        String[] message = e.getMessage().toLowerCase(Locale.ENGLISH).split(" ");

        String commandLabel = message[0].toCharArray()[0] == '/' ? message[0].substring(1) : message[0];

        if (plugin.getSettings().getBlockedVisitorsCommands().stream().anyMatch(commandLabel::contains)) {
            e.setCancelled(true);
            Message.VISITOR_BLOCK_COMMAND.send(superiorPlayer);
        }
    }

    /* INTERNAL */

    private static class EventMethods {

        private final ReflectMethod<SuperiorPlayer> getPlayerMethod;
        private final ReflectMethod<SuperiorPlayer> getTargetMethod;

        EventMethods(Class<? extends Event> eventClass) {
            getPlayerMethod = new ReflectMethod<>(eventClass, "getPlayer");
            getTargetMethod = new ReflectMethod<>(eventClass, "getTarget");
        }

        Optional<String> getPlayer(Event event) {
            return getPlayerMethod.isValid() ? Optional.ofNullable(getPlayerMethod.invoke(event)).map(SuperiorPlayer::getName) : Optional.empty();
        }

        Optional<String> getTarget(Event event) {
            return getTargetMethod.isValid() ? Optional.ofNullable(getTargetMethod.invoke(event)).map(SuperiorPlayer::getName) : Optional.empty();
        }

    }

}

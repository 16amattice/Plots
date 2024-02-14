package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.enums.TopPlotMembersSorting;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.service.placeholders.PlaceholdersService;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuTopPlots;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TopPlotsSelfPlotButton extends AbstractMenuViewButton<MenuTopPlots.View> {

    private static final LazyReference<PlaceholdersService> placeholdersService = new LazyReference<PlaceholdersService>() {
        @Override
        protected PlaceholdersService create() {
            return plugin.getServices().getService(PlaceholdersService.class);
        }
    };

    private TopPlotsSelfPlotButton(MenuTemplateButton<MenuTopPlots.View> templateButton, MenuTopPlots.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public ItemStack createViewItem() {
        Plot plot = menuView.getInventoryViewer().getPlot();
        return plot == null ? getTemplate().noPlotItem.build() :
                modifyViewItem(menuView, plot, getTemplate().plotItem);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        onButtonClick(clickEvent, menuView, menuView.getInventoryViewer().getPlot(), getTemplate().plotSound,
                getTemplate().plotCommands, getTemplate().noPlotSound, getTemplate().noPlotCommands);
    }

    public static void onButtonClick(InventoryClickEvent clickEvent, MenuTopPlots.View menuView,
                                     @Nullable Plot plot, @Nullable GameSound plotSound,
                                     List<String> plotCommands, @Nullable GameSound noPlotSound,
                                     List<String> noPlotCommands) {
        Player player = (Player) clickEvent.getWhoClicked();

        if (plot != null) {
            GameSoundImpl.playSound(player, plotSound);

            if (plotCommands != null) {
                plotCommands.forEach(command -> Bukkit.dispatchCommand(command.startsWith("PLAYER:") ?
                                player : Bukkit.getConsoleSender(),
                        command.replace("PLAYER:", "").replace("%player%", player.getName())));
            }

            menuView.setPreviousMove(false);

            if (clickEvent.getClick().isRightClick()) {
                if (Menus.MENU_GLOBAL_WARPS.isVisitorWarps()) {
                    plugin.getCommands().dispatchSubCommand(player, "visit", plot.getOwner().getName());
                } else {
                    Menus.MENU_WARP_CATEGORIES.openMenu(menuView.getInventoryViewer(), menuView, plot);
                }
            } else if (plugin.getSettings().isValuesMenu()) {
                plugin.getMenus().openValues(menuView.getInventoryViewer(), MenuViewWrapper.fromView(menuView), plot);
            }

            return;
        }

        GameSoundImpl.playSound(player, noPlotSound);

        if (noPlotCommands != null)
            noPlotCommands.forEach(command -> Bukkit.dispatchCommand(command.startsWith("PLAYER:") ?
                            player : Bukkit.getConsoleSender(),
                    command.replace("PLAYER:", "").replace("%player%", player.getName())));
    }

    public static ItemStack modifyViewItem(MenuTopPlots.View menuView, Plot plot, @Nullable TemplateItem plotItem) {
        if (plotItem == null)
            return null;

        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();

        SuperiorPlayer plotOwner = plot.getOwner();
        int place = plugin.getGrid().getPlotPosition(plot, menuView.getSortingType()) + 1;
        ItemBuilder itemBuilder = plotItem.getBuilder().asSkullOf(plotOwner);

        String plotName = !plugin.getSettings().getPlotNames().isPlotTop() ||
                plot.getName().isEmpty() ? plotOwner.getName() :
                plugin.getSettings().getPlotNames().isColorSupport() ?
                        Formatters.COLOR_FORMATTER.format(plot.getName()) : plot.getName();

        itemBuilder.replaceName("{0}", plotName)
                .replaceName("{1}", String.valueOf(place))
                .replaceName("{2}", Formatters.NUMBER_FORMATTER.format(plot.getPlotLevel()))
                .replaceName("{3}", Formatters.NUMBER_FORMATTER.format(plot.getWorth()))
                .replaceName("{5}", Formatters.FANCY_NUMBER_FORMATTER.format(plot.getPlotLevel(), inventoryViewer.getUserLocale()))
                .replaceName("{6}", Formatters.FANCY_NUMBER_FORMATTER.format(plot.getWorth(), inventoryViewer.getUserLocale()))
                .replaceName("{7}", Formatters.NUMBER_FORMATTER.format(plot.getTotalRating()))
                .replaceName("{8}", Formatters.RATING_FORMATTER.format(plot.getTotalRating(), inventoryViewer.getUserLocale()))
                .replaceName("{9}", Formatters.NUMBER_FORMATTER.format(plot.getRatingAmount()))
                .replaceName("{10}", Formatters.NUMBER_FORMATTER.format(plot.getAllPlayersInside().size()));

        ItemMeta itemMeta = itemBuilder.getItemMeta();

        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> lore = new LinkedList<>();

            for (String line : itemMeta.getLore()) {
                if (line.contains("{4}")) {
                    List<SuperiorPlayer> members = new LinkedList<>(plot.getPlotMembers(plugin.getSettings().isPlotTopIncludeLeader()));
                    String memberFormat = line.split("\\{4}:")[1];
                    if (members.size() == 0) {
                        lore.add(memberFormat.replace("{}", "None"));
                    } else {
                        if (plugin.getSettings().getTopPlotMembersSorting() != TopPlotMembersSorting.NAMES)
                            members.sort(plugin.getSettings().getTopPlotMembersSorting().getComparator());

                        members.forEach(member -> {
                            String onlineMessage = member.isOnline() ?
                                    Message.PLOT_TOP_STATUS_ONLINE.getMessage(inventoryViewer.getUserLocale()) :
                                    Message.PLOT_TOP_STATUS_OFFLINE.getMessage(inventoryViewer.getUserLocale());

                            lore.add(placeholdersService.get().parsePlaceholders(member.asOfflinePlayer(), memberFormat
                                    .replace("{}", member.getName())
                                    .replace("{0}", member.getName())
                                    .replace("{1}", onlineMessage == null ? "" : onlineMessage)
                                    .replace("{2}", member.getPlayerRole().getDisplayName()))
                            );
                        });
                    }
                } else {
                    lore.add(line
                            .replace("{0}", plot.getOwner().getName())
                            .replace("{1}", String.valueOf(place))
                            .replace("{2}", Formatters.NUMBER_FORMATTER.format(plot.getPlotLevel()))
                            .replace("{3}", Formatters.NUMBER_FORMATTER.format(plot.getWorth()))
                            .replace("{5}", Formatters.FANCY_NUMBER_FORMATTER.format(plot.getPlotLevel(), inventoryViewer.getUserLocale()))
                            .replace("{6}", Formatters.FANCY_NUMBER_FORMATTER.format(plot.getWorth(), inventoryViewer.getUserLocale()))
                            .replace("{7}", Formatters.NUMBER_FORMATTER.format(plot.getTotalRating()))
                            .replace("{8}", Formatters.RATING_FORMATTER.format(plot.getTotalRating(), inventoryViewer.getUserLocale()))
                            .replace("{9}", Formatters.NUMBER_FORMATTER.format(plot.getRatingAmount()))
                            .replace("{10}", Formatters.NUMBER_FORMATTER.format(plot.getAllPlayersInside().size())));
                }
            }

            itemBuilder.withLore(lore);
        }

        return itemBuilder.build(plotOwner);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuTopPlots.View> {

        private TemplateItem noPlotItem;
        private GameSound noPlotSound;
        private List<String> noPlotCommands;

        public void setPlotItem(TemplateItem plotItem) {
            this.buttonItem = plotItem;
        }

        public void setNoPlotItem(TemplateItem noPlotItem) {
            this.noPlotItem = noPlotItem;
        }

        public void setPlotSound(GameSound plotSound) {
            this.clickSound = plotSound;
        }

        public void setNoPlotSound(GameSound noPlotSound) {
            this.noPlotSound = noPlotSound;
        }

        public void setPlotCommands(List<String> plotCommands) {
            this.commands = plotCommands;
        }

        public void setNoPlotCommands(List<String> noPlotCommands) {
            this.noPlotCommands = noPlotCommands;
        }

        @Override
        public MenuTemplateButton<MenuTopPlots.View> build() {
            return new Template(requiredPermission, lackPermissionSound, buttonItem,
                    clickSound, commands, noPlotItem, noPlotSound, noPlotCommands);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<MenuTopPlots.View> {

        private final TemplateItem plotItem;
        private final TemplateItem noPlotItem;
        @Nullable
        private final GameSound plotSound;
        @Nullable
        private final GameSound noPlotSound;
        private final List<String> plotCommands;
        private final List<String> noPlotCommands;

        Template(@Nullable String requiredPermission, @Nullable GameSound lackPermissionSound,
                 @Nullable TemplateItem plotItem, @Nullable GameSound plotSound, @Nullable List<String> plotCommands,
                 @Nullable TemplateItem noPlotItem, @Nullable GameSound noPlotSound,
                 @Nullable List<String> noPlotCommands) {
            super(null, null, null, requiredPermission, lackPermissionSound,
                    TopPlotsSelfPlotButton.class, TopPlotsSelfPlotButton::new);
            this.plotItem = plotItem == null ? TemplateItem.AIR : plotItem;
            this.noPlotItem = noPlotItem == null ? TemplateItem.AIR : noPlotItem;
            this.plotSound = plotSound;
            this.plotCommands = plotCommands == null ? Collections.emptyList() : plotCommands;
            this.noPlotSound = noPlotSound;
            this.noPlotCommands = noPlotCommands == null ? Collections.emptyList() : noPlotCommands;
            if (noPlotItem != null)
                noPlotItem.getEditableBuilder().asSkullOf((SuperiorPlayer) null);
        }

    }

}

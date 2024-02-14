package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.itemstack.GlowEnchantment;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BiomeButton extends AbstractMenuViewButton<PlotMenuView> {

    private BiomeButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Nullable
    @Override
    public ItemStack createViewItem() {
        ItemStack buttonItem = null;

        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();

        String requiredPermission = getTemplate().getRequiredPermission();

        if (requiredPermission == null || inventoryViewer.hasPermission(requiredPermission)) {
            buttonItem = super.createViewItem();
        } else if (getTemplate().lackPermissionItem != null) {
            buttonItem = getTemplate().lackPermissionItem.build(inventoryViewer);
        }

        if (buttonItem == null || !Menus.MENU_BIOMES.isCurrentBiomeGlow())
            return buttonItem;

        Plot plot = inventoryViewer.getPlot();

        if (plot == null || plot.getBiome() != getTemplate().biome)
            return buttonItem;

        return new ItemBuilder(buttonItem).withEnchant(GlowEnchantment.getGlowEnchant(), 1).build();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Player player = inventoryViewer.asPlayer();

        EventResult<Biome> event = plugin.getEventsBus().callPlotBiomeChangeEvent(inventoryViewer,
                menuView.getPlot(), getTemplate().biome);

        if (event.isCancelled()) {
            GameSoundImpl.playSound(player, getTemplate().getLackPermissionSound());
            return;
        }

        GameSoundImpl.playSound(player, getTemplate().accessSound);

        getTemplate().accessCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", inventoryViewer.getName())));

        menuView.getPlot().setBiome(event.getResult());
        Message.CHANGED_BIOME.send(inventoryViewer, event.getResult().name().toLowerCase(Locale.ENGLISH));

        BukkitExecutor.sync(menuView::closeView, 1L);
    }

    @Override
    public void onButtonClickLackPermission(InventoryClickEvent clickEvent) {
        super.onButtonClickLackPermission(clickEvent);
        getTemplate().lackPermissionCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", menuView.getInventoryViewer().getName())));
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        private final Biome biome;
        private TemplateItem noAccessItem = null;
        private List<String> noAccessCommands = null;

        public Builder(Biome biome) {
            this.biome = biome;
        }

        public void setAccessItem(TemplateItem accessItem) {
            this.buttonItem = accessItem;
        }

        public void setNoAccessItem(TemplateItem noAccessItem) {
            this.noAccessItem = noAccessItem;
        }

        public void setAccessSound(GameSound accessSound) {
            this.clickSound = accessSound;
        }

        public void setNoAccessSound(GameSound noAccessSound) {
            this.lackPermissionSound = noAccessSound;
        }

        public void setAccessCommands(List<String> accessCommands) {
            this.commands = accessCommands;
        }

        public void setNoAccessCommands(List<String> noAccessCommands) {
            this.noAccessCommands = noAccessCommands;
        }

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new Template(buttonItem, requiredPermission, lackPermissionSound,
                    clickSound, commands, noAccessItem, noAccessCommands, biome);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<PlotMenuView> {

        @Nullable
        private final GameSound accessSound;
        private final List<String> accessCommands;
        @Nullable
        private final TemplateItem lackPermissionItem;
        private final List<String> lackPermissionCommands;
        private final Biome biome;

        Template(@Nullable TemplateItem buttonItem, @Nullable String requiredPermission,
                 @Nullable GameSound lackPermissionSound, @Nullable GameSound accessSound,
                 @Nullable List<String> accessCommands, @Nullable TemplateItem lackPermissionItem,
                 @Nullable List<String> lackPermissionCommands, Biome biome) {
            super(buttonItem, null, null, requiredPermission, lackPermissionSound,
                    BiomeButton.class, BiomeButton::new);
            this.accessSound = accessSound;
            this.accessCommands = accessCommands == null ? Collections.emptyList() : accessCommands;
            this.lackPermissionItem = lackPermissionItem;
            this.lackPermissionCommands = lackPermissionCommands == null ? Collections.emptyList() : lackPermissionCommands;
            this.biome = Objects.requireNonNull(biome, "biome cannot be null");
        }

    }

}

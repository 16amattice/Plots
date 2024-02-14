package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuTopPlots;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class TopPlotsPagedObjectButton extends AbstractPagedMenuButton<MenuTopPlots.View, Plot> {

    private TopPlotsPagedObjectButton(MenuTemplateButton<MenuTopPlots.View> templateButton, MenuTopPlots.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        TopPlotsSelfPlotButton.onButtonClick(clickEvent, menuView, pagedObject, getTemplate().plotSound, getTemplate().plotCommands,
                getTemplate().noPlotSound, getTemplate().noPlotCommands);
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        if (pagedObject == null) {
            return getTemplate().getNullTemplateItem().build();
        } else {
            return TopPlotsSelfPlotButton.modifyViewItem(menuView, pagedObject, getTemplate().plotItem);
        }
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuTopPlots.View, Plot> {

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
        public PagedMenuTemplateButton<MenuTopPlots.View, Plot> build() {
            return new Template(requiredPermission, lackPermissionSound, buttonItem,
                    clickSound, commands, noPlotItem, noPlotSound, noPlotCommands, getButtonIndex());
        }

    }

    public static class Template extends PagedMenuTemplateButtonImpl<MenuTopPlots.View, Plot> {

        private final TemplateItem plotItem;
        private final GameSound plotSound;
        private final GameSound noPlotSound;
        private final List<String> plotCommands;
        private final List<String> noPlotCommands;

        Template(String requiredPermission, GameSound lackPermissionSound,
                 TemplateItem plotItem, GameSound plotSound, List<String> plotCommands,
                 TemplateItem noPlotItem, GameSound noPlotSound, List<String> noPlotCommands, int buttonIndex) {
            super(null, null, null, requiredPermission, lackPermissionSound, noPlotItem,
                    buttonIndex, TopPlotsPagedObjectButton.class, TopPlotsPagedObjectButton::new);
            this.plotItem = plotItem;
            this.plotSound = plotSound;
            this.plotCommands = plotCommands == null ? Collections.emptyList() : plotCommands;
            this.noPlotSound = noPlotSound;
            this.noPlotCommands = noPlotCommands == null ? Collections.emptyList() : noPlotCommands;
            if (this.getNullTemplateItem() != null)
                this.getNullTemplateItem().getEditableBuilder().asSkullOf((SuperiorPlayer) null);
        }

    }

}

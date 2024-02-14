package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Objects;

public class ControlPanelButton extends AbstractMenuViewButton<PlotMenuView> {

    private ControlPanelButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Plot targetPlot = menuView.getPlot();

        switch (getTemplate().controlPanelAction) {
            case OPEN_MEMBERS:
                plugin.getMenus().openMembers(inventoryViewer, MenuViewWrapper.fromView(menuView), targetPlot);
                break;
            case OPEN_SETTINGS:
                if (inventoryViewer.hasPermission("superior.plot.settings")) {
                    if (!inventoryViewer.hasPermission(PlotPrivileges.SET_SETTINGS)) {
                        Message.NO_SET_SETTINGS_PERMISSION.send(inventoryViewer,
                                targetPlot.getRequiredPlayerRole(PlotPrivileges.SET_SETTINGS));
                        return;
                    }

                    plugin.getMenus().openSettings(inventoryViewer, MenuViewWrapper.fromView(menuView), targetPlot);
                }
                break;
            case OPEN_VISITORS:
                plugin.getMenus().openVisitors(inventoryViewer, MenuViewWrapper.fromView(menuView), targetPlot);
                break;
        }

        BukkitExecutor.sync(menuView::closeView, 1L);
    }

    public enum ControlPanelAction {

        OPEN_MEMBERS,
        OPEN_SETTINGS,
        OPEN_VISITORS

    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        private ControlPanelAction controlPanelAction;

        public Builder setAction(ControlPanelAction controlPanelAction) {
            this.controlPanelAction = controlPanelAction;
            return this;
        }

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, controlPanelAction);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<PlotMenuView> {

        private final ControlPanelAction controlPanelAction;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound,
                 ControlPanelAction controlPanelAction) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    ControlPanelButton.class, ControlPanelButton::new);
            this.controlPanelAction = Objects.requireNonNull(controlPanelAction, "controlPanelAction cannot be null");
        }

    }

}

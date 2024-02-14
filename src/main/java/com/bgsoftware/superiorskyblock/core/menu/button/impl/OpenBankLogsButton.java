package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OpenBankLogsButton extends AbstractMenuViewButton<PlotMenuView> {

    private OpenBankLogsButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        plugin.getMenus().openBankLogs(menuView.getInventoryViewer(), MenuViewWrapper.fromView(menuView), menuView.getPlot());
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, OpenBankLogsButton.class, OpenBankLogsButton::new);
        }

    }

}

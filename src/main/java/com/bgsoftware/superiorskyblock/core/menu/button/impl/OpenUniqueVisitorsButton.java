package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotVisitors;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OpenUniqueVisitorsButton extends AbstractMenuViewButton<MenuPlotVisitors.View> {

    private OpenUniqueVisitorsButton(AbstractMenuTemplateButton<MenuPlotVisitors.View> templateButton, MenuPlotVisitors.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        plugin.getMenus().openUniqueVisitors(menuView.getInventoryViewer(), MenuViewWrapper.fromView(menuView), menuView.getPlot());
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuPlotVisitors.View> {

        @Override
        public MenuTemplateButton<MenuPlotVisitors.View> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, OpenUniqueVisitorsButton.class, OpenUniqueVisitorsButton::new);
        }

    }

}

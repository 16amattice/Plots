package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractIconProviderMenu;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WarpIconEditConfirmButton extends AbstractMenuViewButton<AbstractIconProviderMenu.View<PlotWarp>> {

    private WarpIconEditConfirmButton(AbstractMenuTemplateButton<AbstractIconProviderMenu.View<PlotWarp>> templateButton,
                                      AbstractIconProviderMenu.View<PlotWarp> menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();

        PlotWarp plotWarp = menuView.getIconProvider();

        EventResult<ItemStack> eventResult = plugin.getEventsBus().callPlotChangeWarpIconEvent(inventoryViewer,
                plotWarp.getPlot(), plotWarp, menuView.getIconTemplate().build());

        if (eventResult.isCancelled())
            return;

        clickEvent.getWhoClicked().closeInventory();

        Message.WARP_ICON_UPDATED.send(inventoryViewer);

        plotWarp.setIcon(eventResult.getResult());
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<AbstractIconProviderMenu.View<PlotWarp>> {

        @Override
        public MenuTemplateButton<AbstractIconProviderMenu.View<PlotWarp>> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, WarpIconEditConfirmButton.class, WarpIconEditConfirmButton::new);
        }

    }

}

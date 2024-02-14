package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpManage;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WarpManagePrivateButton extends AbstractMenuViewButton<MenuWarpManage.View> {

    private WarpManagePrivateButton(AbstractMenuTemplateButton<MenuWarpManage.View> templateButton, MenuWarpManage.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        PlotWarp plotWarp = menuView.getPlotWarp();

        boolean openToPublic = plotWarp.hasPrivateFlag();

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(clickEvent.getWhoClicked());

        if (openToPublic ? !plugin.getEventsBus().callPlotOpenWarpEvent(plotWarp.getPlot(), superiorPlayer, plotWarp) :
                !plugin.getEventsBus().callPlotCloseWarpEvent(plotWarp.getPlot(), superiorPlayer, plotWarp))
            return;

        plotWarp.setPrivateFlag(!openToPublic);

        if (openToPublic)
            Message.WARP_PUBLIC_UPDATE.send(superiorPlayer);
        else
            Message.WARP_PRIVATE_UPDATE.send(superiorPlayer);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuWarpManage.View> {

        @Override
        public MenuTemplateButton<MenuWarpManage.View> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, WarpManagePrivateButton.class, WarpManagePrivateButton::new);
        }

    }

}

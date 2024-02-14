package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpManage;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.player.chat.PlayerChat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WarpManageRenameButton extends AbstractMenuViewButton<MenuWarpManage.View> {

    private WarpManageRenameButton(AbstractMenuTemplateButton<MenuWarpManage.View> templateButton, MenuWarpManage.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();

        Message.WARP_RENAME.send(player);

        menuView.closeView();

        PlayerChat.listen(player, newName -> {
            PlotWarp plotWarp = menuView.getPlotWarp();

            if (!newName.equalsIgnoreCase("-cancel")) {
                if (plotWarp.getPlot().getWarp(newName) != null) {
                    Message.WARP_RENAME_ALREADY_EXIST.send(player);
                    return true;
                }

                if (!PlotUtils.isWarpNameLengthValid(newName)) {
                    Message.WARP_NAME_TOO_LONG.send(player);
                    return true;
                }

                EventResult<String> eventResult = plugin.getEventsBus().callPlotRenameWarpEvent(
                        plotWarp.getPlot(), plugin.getPlayers().getSuperiorPlayer(player), plotWarp, newName);

                if (!eventResult.isCancelled()) {
                    plotWarp.getPlot().renameWarp(plotWarp, eventResult.getResult());

                    Message.WARP_RENAME_SUCCESS.send(player, eventResult.getResult());

                    GameSoundImpl.playSound(player, Menus.MENU_WARP_MANAGE.getSuccessUpdateSound());
                }
            }

            PlayerChat.remove(player);

            menuView.refreshView();

            return true;
        });
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuWarpManage.View> {

        @Override
        public MenuTemplateButton<MenuWarpManage.View> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, WarpManageRenameButton.class, WarpManageRenameButton::new);
        }

    }

}

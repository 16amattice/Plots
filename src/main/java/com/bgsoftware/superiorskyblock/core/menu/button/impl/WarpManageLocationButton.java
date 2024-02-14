package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpManage;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.warp.SignWarp;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WarpManageLocationButton extends AbstractMenuViewButton<MenuWarpManage.View> {

    private WarpManageLocationButton(AbstractMenuTemplateButton<MenuWarpManage.View> templateButton, MenuWarpManage.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        PlotWarp plotWarp = menuView.getPlotWarp();

        Location playerLocation = player.getLocation();

        if (!plotWarp.getPlot().isInsideRange(playerLocation)) {
            Message.SET_WARP_OUTSIDE.send(player);
            return;
        }

        EventResult<Location> eventResult = plugin.getEventsBus().callPlotChangeWarpLocationEvent(
                plugin.getPlayers().getSuperiorPlayer(player), plotWarp.getPlot(), plotWarp, playerLocation);

        if (eventResult.isCancelled())
            return;


        Message.WARP_LOCATION_UPDATE.send(player);

        Location warpLocation = plotWarp.getLocation();

        if (!warpLocation.equals(eventResult.getResult())) {
            ChunksProvider.loadChunk(ChunkPosition.of(warpLocation), ChunkLoadReason.WARP_SIGN_BREAK, chunk -> {
                SignWarp.trySignWarpBreak(plotWarp, player);
            });
        }

        plotWarp.setLocation(eventResult.getResult());

        GameSoundImpl.playSound(player, Menus.MENU_WARP_MANAGE.getSuccessUpdateSound());
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<MenuWarpManage.View> {

        @Override
        public MenuTemplateButton<MenuWarpManage.View> build() {
            return new MenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, WarpManageLocationButton.class, WarpManageLocationButton::new);
        }

    }

}

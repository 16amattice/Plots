package com.bgsoftware.superiorskyblock.plot.preview;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.player.chat.PlayerChat;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SPlotPreview implements PlotPreview {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private final SuperiorPlayer superiorPlayer;
    private final Location previewLocation;
    private final String schematic;
    private final String plotName;

    public SPlotPreview(SuperiorPlayer superiorPlayer, Location previewLocation, String schematic, String plotName) {
        this.superiorPlayer = superiorPlayer;
        this.previewLocation = previewLocation;
        this.schematic = schematic;
        this.plotName = plotName;

        Player player = superiorPlayer.asPlayer();
        Preconditions.checkNotNull(player, "Cannot start plot preview to an offline player.");

        PlayerChat.listen(player, message -> {
            if (message.equalsIgnoreCase(Message.PLOT_PREVIEW_CONFIRM_TEXT.getMessage(superiorPlayer.getUserLocale()))) {
                handleConfirm();
                return true;
            } else if (message.equalsIgnoreCase(Message.PLOT_PREVIEW_CANCEL_TEXT.getMessage(superiorPlayer.getUserLocale()))) {
                handleCancel();
                return true;
            }

            return false;
        });
    }

    @Override
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    @Override
    public Location getLocation() {
        return previewLocation;
    }

    @Override
    public String getSchematic() {
        return schematic;
    }

    @Override
    public String getPlotName() {
        return plotName;
    }

    @Override
    public void handleConfirm() {
        Menus.MENU_PLOT_CREATION.simulateClick(superiorPlayer, plotName, schematic, false, superiorPlayer.getOpenedView());
        Player player = superiorPlayer.asPlayer();
        assert player != null;
        PlayerChat.remove(player);
    }

    @Override
    public void handleCancel() {
        plugin.getGrid().cancelPlotPreview(superiorPlayer);
        Message.PLOT_PREVIEW_CANCEL.send(superiorPlayer);
    }

    @Override
    public void handleEscape() {
        plugin.getGrid().cancelPlotPreview(superiorPlayer);
        Message.PLOT_PREVIEW_CANCEL_DISTANCE.send(superiorPlayer);
    }

}

package com.bgsoftware.superiorskyblock.plot.notifications;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;

import java.util.Collections;

public class PlotNotifications {

    private PlotNotifications() {

    }

    public static void notifyPlayerQuit(SuperiorPlayer superiorPlayer) {
        superiorPlayer.updateLastTimeStatus();

        Plot plot = superiorPlayer.getPlot();

        if (plot == null)
            return;

        PlotUtils.sendMessage(plot, Message.PLAYER_QUIT_ANNOUNCEMENT, Collections.singletonList(superiorPlayer.getUniqueId()), superiorPlayer.getName());

        boolean anyOnline = plot.getPlotMembers(true).stream().anyMatch(plotMember ->
                plotMember != superiorPlayer && plotMember.isOnline());

        if (!anyOnline) {
            plot.setLastTimeUpdate(System.currentTimeMillis() / 1000);
            plot.setCurrentlyActive(false);
        }
    }

    public static void notifyPlayerJoin(SuperiorPlayer superiorPlayer) {
        superiorPlayer.updateLastTimeStatus();

        Plot plot = superiorPlayer.getPlot();
        if (plot == null)
            return;

        PlotUtils.sendMessage(plot, Message.PLAYER_JOIN_ANNOUNCEMENT, Collections.singletonList(superiorPlayer.getUniqueId()), superiorPlayer.getName());
        plot.updateLastTime();
        plot.setCurrentlyActive(true);
    }

}

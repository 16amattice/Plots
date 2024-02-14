package com.bgsoftware.superiorskyblock.plot.signs;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.Materials;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.List;

public class PlotSigns {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private PlotSigns() {

    }

    public static Result handleSignPlace(SuperiorPlayer superiorPlayer, Location warpLocation, String[] warpLines,
                                         boolean sendMessage) {
        Plot plot = plugin.getGrid().getPlotAt(warpLocation);
        if (plot == null)
            return new Result(Reason.NOT_IN_PLOT, false);

        Location playerLocation = superiorPlayer.getLocation();
        if (playerLocation != null)
            warpLocation.setYaw(playerLocation.getYaw());

        if (isWarpSign(warpLines[0])) {
            Reason reason = handleWarpSignPlace(superiorPlayer, plot, warpLocation, warpLines, sendMessage);
            return new Result(reason, true);
        } else if (isVisitorsSign(warpLines[0])) {
            return handleVisitorsSignPlace(superiorPlayer, plot, warpLocation, warpLines, sendMessage);
        }

        return new Result(Reason.SUCCESS, false);
    }

    public static Result handleSignBreak(SuperiorPlayer superiorPlayer, Sign sign) {
        Plot plot = plugin.getGrid().getPlotAt(sign.getLocation());

        if (plot == null)
            return new Result(Reason.NOT_IN_PLOT, false);

        PlotWarp plotWarp = plot.getWarp(sign.getLocation());

        if (plotWarp != null) {
            if (!plugin.getEventsBus().callPlotDeleteWarpEvent(superiorPlayer, plot, plotWarp))
                return new Result(Reason.EVENT_CANCELLED, true);

            plot.deleteWarp(superiorPlayer, sign.getLocation());
        } else {
            if (sign.getLine(0).equalsIgnoreCase(plugin.getSettings().getVisitorsSign().getActive())) {
                if (!plugin.getEventsBus().callPlotRemoveVisitorHomeEvent(superiorPlayer, plot))
                    return new Result(Reason.EVENT_CANCELLED, true);

                plot.setVisitorsLocation(null);
            }
        }

        return new Result(Reason.SUCCESS, false);
    }

    private static Reason handleWarpSignPlace(SuperiorPlayer superiorPlayer, Plot plot, Location warpLocation,
                                              String[] signLines, boolean sendMessage) {
        if (plot.getPlotWarps().size() >= plot.getWarpsLimit()) {
            if (sendMessage)
                Message.NO_MORE_WARPS.send(superiorPlayer);

            return Reason.LIMIT_REACHED;
        }

        String warpName = Formatters.STRIP_COLOR_FORMATTER.format(signLines[1].trim());
        boolean privateFlag = signLines[2].equalsIgnoreCase("private");

        Reason result = Reason.SUCCESS;

        if (warpName.isEmpty()) {
            if (sendMessage)
                Message.WARP_ILLEGAL_NAME.send(superiorPlayer);
            result = Reason.ILLEGAL_NAME;
        } else if (plot.getWarp(warpName) != null) {
            if (sendMessage)
                Message.WARP_ALREADY_EXIST.send(superiorPlayer);
            result = Reason.ALREADY_EXIST;
        } else if (!PlotUtils.isWarpNameLengthValid(warpName)) {
            if (sendMessage)
                Message.WARP_NAME_TOO_LONG.send(superiorPlayer);
            result = Reason.NAME_TOO_LONG;
        }

        if (!plugin.getEventsBus().callPlotCreateWarpEvent(superiorPlayer, plot, warpName, warpLocation, !privateFlag, null))
            result = Reason.EVENT_CANCELLED;

        if (result != Reason.SUCCESS)
            return result;

        List<String> signWarp = plugin.getSettings().getSignWarp();

        for (int i = 0; i < signWarp.size(); i++)
            signLines[i] = signWarp.get(i).replace("{0}", warpName);

        PlotWarp plotWarp = plot.createWarp(warpName, warpLocation, null);
        plotWarp.setPrivateFlag(privateFlag);
        if (sendMessage)
            Message.SET_WARP.send(superiorPlayer, Formatters.LOCATION_FORMATTER.format(warpLocation));

        return Reason.SUCCESS;
    }

    private static Result handleVisitorsSignPlace(SuperiorPlayer superiorPlayer, Plot plot, Location visitorsLocation,
                                                  String[] warpLines, boolean sendMessage) {
        if (plot.getPlotWarps().size() >= plot.getWarpsLimit()) {
            if (sendMessage)
                Message.NO_MORE_WARPS.send(superiorPlayer);

            return new Result(Reason.LIMIT_REACHED, true);
        }

        EventResult<Location> eventResult = plugin.getEventsBus().callPlotSetVisitorHomeEvent(superiorPlayer, plot, visitorsLocation);

        if (eventResult.isCancelled())
            return new Result(Reason.EVENT_CANCELLED, false);

        StringBuilder descriptionBuilder = new StringBuilder();

        for (int i = 1; i < 4; i++) {
            String line = warpLines[i];
            if (!line.isEmpty())
                descriptionBuilder.append("\n").append(ChatColor.RESET).append(line);
        }

        String description = descriptionBuilder.length() < 1 ? "" : descriptionBuilder.substring(1);

        warpLines[0] = plugin.getSettings().getVisitorsSign().getActive();

        for (int i = 1; i <= 3; i++)
            warpLines[i] = Formatters.COLOR_FORMATTER.format(warpLines[i]);

        Location plotVisitorsLocation = plot.getVisitorsLocation(null /* unused */);
        Block oldWelcomeSignBlock = plotVisitorsLocation == null ? null : plotVisitorsLocation.getBlock();

        if (oldWelcomeSignBlock != null && Materials.isSign(oldWelcomeSignBlock.getType())) {
            Sign oldWelcomeSign = (Sign) oldWelcomeSignBlock.getState();
            oldWelcomeSign.setLine(0, plugin.getSettings().getVisitorsSign().getInactive());
            oldWelcomeSign.update();
        }

        plot.setVisitorsLocation(eventResult.getResult());

        EventResult<String> descriptionEventResult = plugin.getEventsBus().callPlotChangeDescriptionEvent(superiorPlayer, plot, description);

        if (!descriptionEventResult.isCancelled())
            plot.setDescription(descriptionEventResult.getResult());

        if (sendMessage)
            Message.SET_WARP.send(superiorPlayer, Formatters.LOCATION_FORMATTER.format(visitorsLocation));

        return new Result(Reason.SUCCESS, true);
    }

    private static boolean isWarpSign(String firstSignLine) {
        return firstSignLine.equalsIgnoreCase(plugin.getSettings().getSignWarpLine());
    }

    private static boolean isVisitorsSign(String firstSignLine) {
        return firstSignLine.equalsIgnoreCase(plugin.getSettings().getVisitorsSign().getLine());
    }

    public enum Reason {

        NOT_IN_PLOT,
        ILLEGAL_NAME,
        ALREADY_EXIST,
        NAME_TOO_LONG,
        LIMIT_REACHED,
        EVENT_CANCELLED,
        SUCCESS

    }

    public static class Result {

        private final Reason reason;
        private final boolean cancelEvent;

        public Result(Reason reason, boolean cancelEvent) {
            this.reason = reason;
            this.cancelEvent = cancelEvent;
        }

        public Reason getReason() {
            return reason;
        }

        public boolean isCancelEvent() {
            return cancelEvent;
        }

    }

}

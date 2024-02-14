package com.bgsoftware.superiorskyblock.plot;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class PlotNames {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private PlotNames() {

    }

    public static boolean isValidName(SuperiorPlayer superiorPlayer, Plot currentPlot, String plotName) {
        return isValidName(superiorPlayer.asPlayer(), currentPlot, plotName);
    }

    public static boolean isValidName(CommandSender sender, Plot currentPlot, String plotName) {
        String coloredName = plugin.getSettings().getPlotNames().isColorSupport() ?
                Formatters.COLOR_FORMATTER.format(plotName) : plotName;
        String strippedName = plugin.getSettings().getPlotNames().isColorSupport() ?
                Formatters.STRIP_COLOR_FORMATTER.format(coloredName) : plotName;

        if (strippedName.length() > plugin.getSettings().getPlotNames().getMaxLength()) {
            Message.NAME_TOO_LONG.send(sender);
            return false;
        }

        if (strippedName.length() < plugin.getSettings().getPlotNames().getMinLength()) {
            Message.NAME_TOO_SHORT.send(sender);
            return false;
        }

        if (plugin.getSettings().getPlotNames().isPreventPlayerNames() && plugin.getPlayers().getSuperiorPlayer(strippedName) != null) {
            Message.NAME_SAME_AS_PLAYER.send(sender);
            return false;
        }

        String lookupName = plotName.toLowerCase(Locale.ENGLISH);
        if (plugin.getSettings().getPlotNames().getFilteredNames().stream().anyMatch(lookupName::contains)) {
            Message.NAME_BLACKLISTED.send(sender);
            return false;
        }

        if (currentPlot != null && currentPlot.getName().equalsIgnoreCase(plotName)) {
            Message.SAME_NAME_CHANGE.send(sender);
            return false;
        }

        if (plugin.getGrid().getPlot(plotName) != null) {
            Message.PLOT_ALREADY_EXIST.send(sender);
            return false;
        }

        return true;
    }

}

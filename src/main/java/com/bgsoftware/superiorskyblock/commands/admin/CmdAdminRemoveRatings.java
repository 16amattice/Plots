package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CmdAdminRemoveRatings implements IAdminPlotCommand {
    @Override
    public List<String> getAliases() {
        return Arrays.asList("removeratings", "rratings", "rr");
    }

    @Override
    public String getPermission() {
        return "superior.admin.removeratings";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin removeratings <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_REMOVE_RATINGS.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, List<Plot> plots, String[] args) {
        boolean removingAllRatings = targetPlayer == null;
        Collection<Plot> iterPlots = removingAllRatings ? plots : plugin.getGrid().getPlots();

        boolean anyPlotChanged = false;

        for (Plot plot : iterPlots) {
            if (removingAllRatings) {
                if (plugin.getEventsBus().callPlotClearRatingsEvent(sender, plot)) {
                    anyPlotChanged = true;
                    plot.removeRatings();
                }
            } else if (plugin.getEventsBus().callPlotRemoveRatingEvent(sender, targetPlayer, plot)) {
                anyPlotChanged = true;
                plot.removeRating(targetPlayer);
            }
        }

        if (!anyPlotChanged)
            return;

        if (!removingAllRatings)
            Message.RATE_REMOVE_ALL.send(sender, targetPlayer.getName());
        else if (plots.size() == 1)
            Message.RATE_REMOVE_ALL.send(sender, plots.get(0).getName());
        else
            Message.RATE_REMOVE_ALL_PLOTS.send(sender);
    }

}

package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetRate implements IAdminPlotCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setrate");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setrate";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setrate <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_RATING.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_RATE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 5;
    }

    @Override
    public int getMaxArgs() {
        return 5;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, sender, args[3]);

        if (targetPlayer == null)
            return;

        Rating rating = CommandArguments.getRating(sender, args[4]);

        if (rating == null)
            return;

        if (rating == Rating.UNKNOWN) {
            if (plugin.getEventsBus().callPlotRemoveRatingEvent(sender, superiorPlayer, plot)) {
                plot.removeRating(targetPlayer);
                Message.RATE_REMOVE_ALL.send(sender, targetPlayer.getName());
            }
        } else if (plugin.getEventsBus().callPlotRateEvent(sender, superiorPlayer, plot, rating)) {
            plot.setRating(targetPlayer, rating);
            Message.RATE_CHANGE_OTHER.send(sender, targetPlayer.getName(), Formatters.CAPITALIZED_FORMATTER.format(rating.name()));
        }
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getRatedPlayers(plugin, plot, args[3]) :
                args.length == 5 ? CommandTabCompletes.getRatings(args[4]) : Collections.emptyList();
    }

}

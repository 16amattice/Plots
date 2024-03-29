package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CmdAdminTitleAll implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("titleall");
    }

    @Override
    public String getPermission() {
        return "superior.admin.titleall";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin titleall <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_TITLE_FADE_IN.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_TITLE_DURATION.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_TITLE_FADE_OUT.getMessage(locale) + "> " +
                "-title [" + Message.COMMAND_ARGUMENT_MESSAGE.getMessage(locale) + "] " +
                "-subtitle [" + Message.COMMAND_ARGUMENT_MESSAGE.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_TITLE_ALL.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 8;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
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
        NumberArgument<Integer> fadeIn = CommandArguments.getInterval(sender, args[3]);

        if (!fadeIn.isSucceed())
            return;

        NumberArgument<Integer> duration = CommandArguments.getInterval(sender, args[4]);

        if (!duration.isSucceed())
            return;

        NumberArgument<Integer> fadeOut = CommandArguments.getInterval(sender, args[5]);

        if (!fadeOut.isSucceed())
            return;

        Map<String, String> parsedArguments = CommandArguments.parseArguments(args);

        String title = parsedArguments.get("title");
        String subtitle = parsedArguments.get("subtitle");

        if (title == null && subtitle == null) {
            Message.INVALID_TITLE.send(sender);
            return;
        }

        plots.forEach(plot -> plot.sendTitle(title == null ? null : Formatters.COLOR_FORMATTER.format(title),
                subtitle == null ? null : Formatters.COLOR_FORMATTER.format(subtitle),
                fadeIn.getNumber(), duration.getNumber(), fadeOut.getNumber()));

        if (targetPlayer == null)
            Message.GLOBAL_TITLE_SENT_NAME.send(sender, plots.size() == 1 ? plots.get(0).getName() : "all");
        else
            Message.GLOBAL_TITLE_SENT.send(sender, targetPlayer.getName());
    }

}

package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminAddTeamLimit implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("addteamlimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.addteamlimit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin addteamlimit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LIMIT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_ADD_TEAM_LIMIT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 4;
    }

    @Override
    public int getMaxArgs() {
        return 4;
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
        NumberArgument<Integer> arguments = CommandArguments.getLimit(sender, args[3]);

        if (!arguments.isSucceed())
            return;

        int limit = arguments.getNumber();

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeMembersLimitEvent(sender,
                    plot, plot.getTeamLimit() + limit);
            anyPlotChanged |= !eventResult.isCancelled();
            if (!eventResult.isCancelled())
                plot.setTeamLimit(eventResult.getResult());
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_TEAM_LIMIT_ALL.send(sender);
        else if (targetPlayer == null)
            Message.CHANGED_TEAM_LIMIT_NAME.send(sender, plots.get(0).getName());
        else
            Message.CHANGED_TEAM_LIMIT.send(sender, targetPlayer.getName());
    }

}

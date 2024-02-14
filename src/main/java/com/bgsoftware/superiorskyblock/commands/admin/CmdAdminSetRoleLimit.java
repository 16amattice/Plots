package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetRoleLimit implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setrolelimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setrolelimit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setrolelimit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_PLOT_ROLE.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LIMIT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_ROLE_LIMIT.getMessage(locale);
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
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, List<Plot> plots, String[] args) {
        PlayerRole playerRole = CommandArguments.getPlayerRole(sender, args[3]);

        if (playerRole == null)
            return;

        if (!PlotUtils.isValidRoleForLimit(playerRole)) {
            Message.INVALID_ROLE.send(sender, args[3], SPlayerRole.getValuesString());
            return;
        }

        NumberArgument<Integer> arguments = CommandArguments.getLimit(sender, args[4]);

        if (!arguments.isSucceed())
            return;

        int limit = arguments.getNumber();

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            if (limit <= 0) {
                if (plugin.getEventsBus().callPlotRemoveRoleLimitEvent(sender, plot, playerRole)) {
                    anyPlotChanged = true;
                    plot.removeRoleLimit(playerRole);
                }
            } else {
                EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeRoleLimitEvent(sender, plot, playerRole, limit);
                anyPlotChanged |= !eventResult.isCancelled();
                if (!eventResult.isCancelled())
                    plot.setRoleLimit(playerRole, eventResult.getResult());
            }
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_ROLE_LIMIT_ALL.send(sender, playerRole);
        else if (targetPlayer == null)
            Message.CHANGED_ROLE_LIMIT_NAME.send(sender, playerRole, plots.get(0).getName());
        else
            Message.CHANGED_ROLE_LIMIT.send(sender, playerRole, targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getPlayerRoles(plugin, args[3], PlotUtils::isValidRoleForLimit)
                : Collections.emptyList();
    }

}

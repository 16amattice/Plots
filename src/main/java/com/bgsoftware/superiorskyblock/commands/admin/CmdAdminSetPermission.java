package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdAdminSetPermission implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("setpermission", "setperm");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setpermission";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setpermission <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_PERMISSION.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_PLOT_ROLE.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_PERMISSION.getMessage(locale);
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
        PlotPrivilege plotPrivilege = CommandArguments.getPlotPrivilege(sender, args[3]);

        if (plotPrivilege == null)
            return;

        PlayerRole playerRole = CommandArguments.getPlayerRole(sender, args[4]);

        if (playerRole == null)
            return;

        boolean anyPrivilegesChanged = false;

        for (Plot plot : plots) {
            if (!plugin.getEventsBus().callPlotChangeRolePrivilegeEvent(plot, playerRole))
                continue;

            anyPrivilegesChanged = true;
            plot.setPermission(playerRole, plotPrivilege);
        }

        if (!anyPrivilegesChanged)
            return;

        if (plots.size() > 1)
            Message.PERMISSION_CHANGED_ALL.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotPrivilege.getName()));
        else if (targetPlayer == null)
            Message.PERMISSION_CHANGED_NAME.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotPrivilege.getName()), plots.get(0).getName());
        else
            Message.PERMISSION_CHANGED.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotPrivilege.getName()), targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getPlotPrivileges(args[3]) :
                args.length == 5 ? CommandTabCompletes.getPlayerRoles(plugin, args[4]) : Collections.emptyList();
    }

}

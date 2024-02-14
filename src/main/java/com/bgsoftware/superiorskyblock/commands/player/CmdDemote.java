package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Collections;
import java.util.List;

public class CmdDemote implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("demote");
    }

    @Override
    public String getPermission() {
        return "superior.plot.demote";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "demote <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_DEMOTE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.DEMOTE_MEMBERS;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_DEMOTE_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if (targetPlayer == null)
            return;

        if (!plot.isMember(targetPlayer)) {
            Message.PLAYER_NOT_INSIDE_PLOT.send(superiorPlayer);
            return;
        }

        if (!targetPlayer.getPlayerRole().isLessThan(superiorPlayer.getPlayerRole())) {
            Message.DEMOTE_PLAYERS_WITH_LOWER_ROLE.send(superiorPlayer);
            return;
        }

        PlayerRole previousRole = targetPlayer.getPlayerRole();
        int roleLimit;

        do {
            previousRole = previousRole.getPreviousRole();
            roleLimit = previousRole == null ? -1 : plot.getRoleLimit(previousRole);
        } while (previousRole != null && !previousRole.isFirstRole() && roleLimit >= 0 && roleLimit >= plot.getPlotMembers(previousRole).size());

        if (previousRole == null) {
            Message.LAST_ROLE_DEMOTE.send(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlayerChangeRoleEvent(targetPlayer, previousRole))
            return;

        targetPlayer.setPlayerRole(previousRole);

        Message.DEMOTED_MEMBER.send(superiorPlayer, targetPlayer.getName(), targetPlayer.getPlayerRole());
        Message.GOT_DEMOTED.send(targetPlayer, targetPlayer.getPlayerRole());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getPlotMembers(plot, args[1], plotMember ->
                plotMember.getPlayerRole().isLessThan(superiorPlayer.getPlayerRole()) &&
                        plotMember.getPlayerRole().getPreviousRole() != null) : Collections.emptyList();
    }

}

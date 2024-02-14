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

public class CmdPromote implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("promote");
    }

    @Override
    public String getPermission() {
        return "superior.plot.promote";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "promote <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_PROMOTE.getMessage(locale);
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
        return PlotPrivileges.PROMOTE_MEMBERS;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_PROMOTE_PERMISSION;
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

        PlayerRole playerRole = targetPlayer.getPlayerRole();

        if (playerRole.isLastRole()) {
            Message.LAST_ROLE_PROMOTE.send(superiorPlayer);
            return;
        }

        if (!playerRole.isLessThan(superiorPlayer.getPlayerRole())) {
            Message.PROMOTE_PLAYERS_WITH_LOWER_ROLE.send(superiorPlayer);
            return;
        }

        PlayerRole nextRole = playerRole;
        int roleLimit;

        do {
            nextRole = nextRole.getNextRole();
            roleLimit = nextRole == null ? -1 : plot.getRoleLimit(nextRole);
        } while (nextRole != null && !nextRole.isLastRole() && !nextRole.isHigherThan(superiorPlayer.getPlayerRole()) &&
                roleLimit >= 0 && plot.getPlotMembers(nextRole).size() >= roleLimit);

        if (nextRole == null || nextRole.isLastRole()) {
            Message.LAST_ROLE_PROMOTE.send(superiorPlayer);
            return;
        }

        if (nextRole.isHigherThan(superiorPlayer.getPlayerRole())) {
            Message.PROMOTE_PLAYERS_WITH_LOWER_ROLE.send(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlayerChangeRoleEvent(targetPlayer, nextRole))
            return;

        targetPlayer.setPlayerRole(nextRole);

        Message.PROMOTED_MEMBER.send(superiorPlayer, targetPlayer.getName(), targetPlayer.getPlayerRole());
        Message.GOT_PROMOTED.send(targetPlayer, targetPlayer.getPlayerRole());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length != 2 ? Collections.emptyList() : CommandTabCompletes.getPlotMembers(plot, args[1], plotMember -> {
            PlayerRole playerRole = plotMember.getPlayerRole();
            PlayerRole nextRole = playerRole.getNextRole();
            return nextRole != null && !nextRole.isLastRole() && playerRole.isLessThan(superiorPlayer.getPlayerRole()) &&
                    !nextRole.isHigherThan(superiorPlayer.getPlayerRole());
        });
    }

}

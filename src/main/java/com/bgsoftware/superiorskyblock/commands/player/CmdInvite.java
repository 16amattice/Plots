package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdInvite implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("invite", "add");
    }

    @Override
    public String getPermission() {
        return "superior.plot.invite";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "invite <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_INVITE.getMessage(locale);
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
        return PlotPrivileges.INVITE_MEMBER;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_INVITE_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if (targetPlayer == null)
            return;

        if (plot.isMember(targetPlayer)) {
            Message.ALREADY_IN_PLOT_OTHER.send(superiorPlayer);
            return;
        }

        if (plot.isBanned(targetPlayer)) {
            Message.INVITE_BANNED_PLAYER.send(superiorPlayer);
            return;
        }

        Message announcementMessage;

        if (plot.isInvited(targetPlayer)) {
            plot.revokeInvite(targetPlayer);
            announcementMessage = Message.REVOKE_INVITE_ANNOUNCEMENT;
            Message.GOT_REVOKED.send(targetPlayer, superiorPlayer.getName());
        } else {
            if (plot.getTeamLimit() >= 0 && plot.getPlotMembers(true).size() >= plot.getTeamLimit()) {
                Message.INVITE_TO_FULL_PLOT.send(superiorPlayer);
                return;
            }

            if (!plugin.getEventsBus().callPlotInviteEvent(superiorPlayer, targetPlayer, plot))
                return;

            plot.inviteMember(targetPlayer);
            announcementMessage = Message.INVITE_ANNOUNCEMENT;

            Message.GOT_INVITE.send(targetPlayer, superiorPlayer.getName());
        }

        PlotUtils.sendMessage(plot, announcementMessage, Collections.emptyList(), superiorPlayer.getName(), targetPlayer.getName());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayers(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(), onlinePlayer -> !plot.isMember(onlinePlayer)) :
                Collections.emptyList();
    }

}

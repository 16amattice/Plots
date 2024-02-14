package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotJoinEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdAccept implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("accept", "join");
    }

    @Override
    public String getPermission() {
        return "superior.plot.accept";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "accept [" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ACCEPT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
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
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        SuperiorPlayer targetPlayer;
        Plot plot;

        if (args.length == 1) {
            List<Plot> playerPendingInvites = superiorPlayer.getInvites();
            plot = playerPendingInvites.isEmpty() ? null : playerPendingInvites.get(0);
            targetPlayer = null;
        } else {
            targetPlayer = plugin.getPlayers().getSuperiorPlayer(args[1]);
            plot = targetPlayer == null ? plugin.getGrid().getPlot(args[1]) : targetPlayer.getPlot();
        }

        if (plot == null || !plot.isInvited(superiorPlayer)) {
            Message.NO_PLOT_INVITE.send(superiorPlayer);
            return;
        }

        if (superiorPlayer.getPlot() != null) {
            Message.JOIN_WHILE_IN_PLOT.send(superiorPlayer);
            return;
        }

        if (plot.getTeamLimit() >= 0 && plot.getPlotMembers(true).size() >= plot.getTeamLimit()) {
            Message.JOIN_FULL_PLOT.send(superiorPlayer);
            plot.revokeInvite(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlotJoinEvent(superiorPlayer, plot, PlotJoinEvent.Cause.INVITE))
            return;

        PlotUtils.sendMessage(plot, Message.JOIN_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());

        plot.revokeInvite(superiorPlayer);
        plot.addMember(superiorPlayer, SPlayerRole.defaultRole());

        if (targetPlayer == null)
            Message.JOINED_PLOT_NAME.send(superiorPlayer, plot.getName());
        else
            Message.JOINED_PLOT.send(superiorPlayer, targetPlayer.getName());

        if (plugin.getSettings().isTeleportOnJoin())
            superiorPlayer.teleport(plot);
        if (plugin.getSettings().isClearOnJoin())
            plugin.getNMSPlayers().clearInventory(superiorPlayer.asPlayer());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayersWithPlots(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(), (onlinePlayer, onlinePlot) ->
                        onlinePlot != null && onlinePlot.isInvited(superiorPlayer)) : Collections.emptyList();
    }

}

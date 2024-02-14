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

public class CmdCoop implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("coop", "trust");
    }

    @Override
    public String getPermission() {
        return "superior.plot.coop";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "coop <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_COOP.getMessage(locale);
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
        return PlotPrivileges.COOP_MEMBER;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_COOP_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if (targetPlayer == null)
            return;

        if (!targetPlayer.isOnline()) {
            Message.INVALID_PLAYER.send(superiorPlayer, args[1]);
            return;
        }

        if (plot.isMember(targetPlayer)) {
            Message.ALREADY_IN_PLOT_OTHER.send(superiorPlayer);
            return;
        }

        if (plot.isCoop(targetPlayer)) {
            Message.PLAYER_ALREADY_COOP.send(superiorPlayer);
            return;
        }

        if (plot.isBanned(targetPlayer)) {
            Message.COOP_BANNED_PLAYER.send(superiorPlayer);
            return;
        }

        if (plot.getCoopPlayers().size() >= plot.getCoopLimit()) {
            Message.COOP_LIMIT_EXCEED.send(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlotCoopPlayerEvent(plot, superiorPlayer, targetPlayer))
            return;

        plot.addCoop(targetPlayer);

        PlotUtils.sendMessage(plot, Message.COOP_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName(), targetPlayer.getName());

        if (plot.getName().isEmpty())
            Message.JOINED_PLOT_AS_COOP.send(targetPlayer, superiorPlayer.getName());
        else
            Message.JOINED_PLOT_AS_COOP_NAME.send(targetPlayer, plot.getName());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayers(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(), onlinePlayer ->
                        !plot.isMember(onlinePlayer) && !plot.isBanned(onlinePlayer) && !plot.isCoop(onlinePlayer))
                : Collections.emptyList();
    }

}

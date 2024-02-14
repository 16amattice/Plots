package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlayerCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetLeader implements IAdminPlayerCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setleader");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setleader";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setleader <" +
                Message.COMMAND_ARGUMENT_LEADER.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_NEW_LEADER.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_LEADER.getMessage(locale);
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
    public boolean supportMultiplePlayers() {
        return false;
    }

    @Override
    public boolean requirePlot() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer leader, String[] args) {
        SuperiorPlayer newLeader = CommandArguments.getPlayer(plugin, sender, args[3]);

        if (newLeader == null)
            return;

        Plot plot = leader.getPlot();

        assert plot != null; // requirePlot is true

        if (!plot.getOwner().getUniqueId().equals(leader.getUniqueId())) {
            Message.TRANSFER_ADMIN_NOT_LEADER.send(sender);
            return;
        }

        if (leader.getUniqueId().equals(newLeader.getUniqueId())) {
            Message.TRANSFER_ADMIN_ALREADY_LEADER.send(sender, newLeader.getName());
            return;
        }

        if (!plot.isMember(newLeader)) {
            Message.TRANSFER_ADMIN_DIFFERENT_PLOT.send(sender);
            return;
        }

        if (plot.transferPlot(newLeader)) {
            Message.TRANSFER_ADMIN.send(sender, leader.getName(), newLeader.getName());
            PlotUtils.sendMessage(plot, Message.TRANSFER_BROADCAST, Collections.emptyList(), newLeader.getName());
        }
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer targetPlayer, String[] args) {
        Plot playerPlot = targetPlayer.getPlot();
        return args.length != 4 ? Collections.emptyList() : CommandTabCompletes.getOnlinePlayers(plugin, args[2], false, onlinePlayer -> {
            Plot onlinePlot = onlinePlayer.getPlot();
            return !onlinePlayer.equals(targetPlayer) && onlinePlot != null && !onlinePlot.equals(playerPlot) &&
                    onlinePlot.getOwner().equals(onlinePlayer);
        });
    }

}

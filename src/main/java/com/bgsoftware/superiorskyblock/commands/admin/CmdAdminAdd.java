package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotJoinEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminAdd implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("add");
    }

    @Override
    public String getPermission() {
        return "superior.admin.add";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin add <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_ADD.getMessage(locale);
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
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, sender, args[3]);

        if (targetPlayer == null)
            return;

        if (targetPlayer.getPlot() != null) {
            Message.PLAYER_ALREADY_IN_PLOT.send(sender);
            return;
        }

        if (!plugin.getEventsBus().callPlotJoinEvent(targetPlayer, plot, PlotJoinEvent.Cause.ADMIN))
            return;

        PlotUtils.sendMessage(plot, Message.JOIN_ANNOUNCEMENT, Collections.emptyList(), targetPlayer.getName());

        plot.revokeInvite(targetPlayer);
        plot.addMember(targetPlayer, SPlayerRole.defaultRole());

        if (superiorPlayer == null) {
            Message.JOINED_PLOT_NAME.send(targetPlayer, plot.getName());
            Message.ADMIN_ADD_PLAYER_NAME.send(sender, targetPlayer.getName(), plot.getName());
        } else {
            Message.JOINED_PLOT.send(targetPlayer, superiorPlayer.getName());
            Message.ADMIN_ADD_PLAYER.send(sender, targetPlayer.getName(), superiorPlayer.getName());
        }

        if (plugin.getSettings().isTeleportOnJoin() && targetPlayer.isOnline())
            targetPlayer.teleport(plot);
        if (plugin.getSettings().isClearOnJoin())
            plugin.getNMSPlayers().clearInventory(targetPlayer.asOfflinePlayer());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getOnlinePlayers(plugin, args[2], false,
                superiorPlayer -> superiorPlayer.getPlot() == null) : Collections.emptyList();
    }

}

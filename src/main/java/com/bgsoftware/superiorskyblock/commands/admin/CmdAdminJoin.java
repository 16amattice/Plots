package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotJoinEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminJoin implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("join");
    }

    @Override
    public String getPermission() {
        return "superior.admin.join";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin join <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_JOIN.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public boolean supportMultiplePlots() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, Plot plot, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        if (superiorPlayer.getPlot() != null) {
            Message.ALREADY_IN_PLOT.send(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlotJoinEvent(superiorPlayer, plot, PlotJoinEvent.Cause.ADMIN))
            return;

        PlotUtils.sendMessage(plot, Message.JOIN_ADMIN_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());

        plot.addMember(superiorPlayer, SPlayerRole.defaultRole());

        if (targetPlayer == null)
            Message.JOINED_PLOT_NAME.send(superiorPlayer, plot.getName());
        else
            Message.JOINED_PLOT.send(superiorPlayer, targetPlayer.getName());

        if (plugin.getSettings().isTeleportOnJoin())
            superiorPlayer.teleport(plot);
    }

}

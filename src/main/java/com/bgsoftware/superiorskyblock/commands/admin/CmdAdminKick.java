package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlayerCommand;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdAdminKick implements IAdminPlayerCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("kick");
    }

    @Override
    public String getPermission() {
        return "superior.admin.kick";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin kick <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_KICK.getMessage(locale);
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
        return true;
    }

    @Override
    public boolean supportMultiplePlayers() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, SuperiorPlayer targetPlayer, String[] args) {
        Plot targetPlot = targetPlayer.getPlot();

        if (targetPlot == null) {
            Message.INVALID_PLOT_OTHER.send(sender, targetPlayer.getName());
            return;
        }

        if (targetPlot.getOwner() == targetPlayer) {
            Message.KICK_PLOT_LEADER.send(sender);
            return;
        }

        PlotUtils.handleKickPlayer(sender instanceof Player ? plugin.getPlayers().getSuperiorPlayer(sender) : null,
                sender.getName(), targetPlot, targetPlayer);
    }

}

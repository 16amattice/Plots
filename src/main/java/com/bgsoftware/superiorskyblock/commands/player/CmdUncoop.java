package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotUncoopPlayerEvent;
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

public class CmdUncoop implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("uncoop", "untrust");
    }

    @Override
    public String getPermission() {
        return "superior.plot.uncoop";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "uncoop <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_UNCOOP.getMessage(locale);
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
        return PlotPrivileges.UNCOOP_MEMBER;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_UNCOOP_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if (targetPlayer == null)
            return;

        if (!plot.isCoop(targetPlayer)) {
            Message.PLAYER_NOT_COOP.send(superiorPlayer);
            return;
        }

        if (!plugin.getEventsBus().callPlotUncoopPlayerEvent(plot, superiorPlayer, targetPlayer, PlotUncoopPlayerEvent.UncoopReason.PLAYER))
            return;

        plot.removeCoop(targetPlayer);

        PlotUtils.sendMessage(plot, Message.UNCOOP_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName(), targetPlayer.getName());

        if (plot.getName().isEmpty())
            Message.LEFT_PLOT_COOP.send(targetPlayer, superiorPlayer.getName());
        else
            Message.LEFT_PLOT_COOP_NAME.send(targetPlayer, plot.getName());
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayers(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(), plot::isCoop) : Collections.emptyList();
    }

}

package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.core.events.EventResult;

import java.util.Collections;
import java.util.List;

public class CmdSetDiscord implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setdiscord");
    }

    @Override
    public String getPermission() {
        return "superior.plot.setdiscord";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "setdiscord <" + Message.COMMAND_ARGUMENT_DISCORD.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_SET_DISCORD.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.SET_DISCORD;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_DISCORD_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        String discord = CommandArguments.buildLongString(args, 1, false);

        EventResult<String> eventResult = plugin.getEventsBus().callPlotChangeDiscordEvent(superiorPlayer, plot, discord);

        if (eventResult.isCancelled())
            return;

        plot.setDiscord(eventResult.getResult());
        Message.CHANGED_DISCORD.send(superiorPlayer, eventResult.getResult());
    }

}

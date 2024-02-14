package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Arrays;
import java.util.List;

public class CmdClose implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("close", "lock");
    }

    @Override
    public String getPermission() {
        return "superior.plot.close";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "close";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_CLOSE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.CLOSE_PLOT;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_CLOSE_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        if (plugin.getEventsBus().callPlotCloseEvent(plot, superiorPlayer)) {
            plot.setLocked(true);
            Message.PLOT_CLOSED.send(superiorPlayer);
        }
    }

}

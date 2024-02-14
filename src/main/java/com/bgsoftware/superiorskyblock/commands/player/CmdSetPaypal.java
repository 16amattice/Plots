package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.core.events.EventResult;

import java.util.Collections;
import java.util.List;

public class CmdSetPaypal implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setpaypal");
    }

    @Override
    public String getPermission() {
        return "superior.plot.setpaypal";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "setpaypal <" + Message.COMMAND_ARGUMENT_EMAIL.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_SET_PAYPAL.getMessage(locale);
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
        return PlotPrivileges.SET_PAYPAL;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_PAYPAL_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        EventResult<String> eventResult = plugin.getEventsBus().callPlotChangePaypalEvent(superiorPlayer, plot, args[1]);

        if (eventResult.isCancelled())
            return;

        plot.setPaypal(eventResult.getResult());
        Message.CHANGED_PAYPAL.send(superiorPlayer, eventResult.getResult());
    }

}

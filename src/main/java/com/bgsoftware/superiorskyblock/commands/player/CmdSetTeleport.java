package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotSetHomeEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public class CmdSetTeleport implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("settp", "setteleport", "setgo", "sethome");
    }

    @Override
    public String getPermission() {
        return "superior.plot.setteleport";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "setteleport";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_SET_TELEPORT.getMessage(locale);
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
        return PlotPrivileges.SET_HOME;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_HOME_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        Location newLocation = superiorPlayer.getLocation();

        if (!plot.isInsideRange(newLocation)) {
            Message.TELEPORT_LOCATION_OUTSIDE_PLOT.send(superiorPlayer);
            return;
        }

        EventResult<Location> eventResult = plugin.getEventsBus().callPlotSetHomeEvent(plot, newLocation,
                PlotSetHomeEvent.Reason.SET_HOME_COMMAND, superiorPlayer);

        if (eventResult.isCancelled())
            return;

        plot.setPlotHome(eventResult.getResult());
        Message.CHANGED_TELEPORT_LOCATION.send(superiorPlayer);
    }

}

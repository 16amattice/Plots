package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.plot.PlotNames;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdName implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("name", "setname", "rename");
    }

    @Override
    public String getPermission() {
        return "superior.plot.name";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "name <" + Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_NAME.getMessage(locale);
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
        return PlotPrivileges.CHANGE_NAME;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_NAME_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        EventResult<String> eventResult = plugin.getEventsBus().callPlotRenameEvent(plot, superiorPlayer, args[1]);

        if (eventResult.isCancelled())
            return;

        String plotName = eventResult.getResult();

        if (!PlotNames.isValidName(superiorPlayer, plot, plotName))
            return;

        plot.setName(plotName);

        String coloredName = plugin.getSettings().getPlotNames().isColorSupport() ?
                Formatters.COLOR_FORMATTER.format(plotName) : plotName;

        for (Player player : Bukkit.getOnlinePlayers())
            Message.NAME_ANNOUNCEMENT.send(player, superiorPlayer.getName(), coloredName);

        Message.CHANGED_NAME.send(superiorPlayer, coloredName);
    }

}

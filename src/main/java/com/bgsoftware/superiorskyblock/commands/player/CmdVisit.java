package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdVisit implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("visit");
    }

    @Override
    public String getPermission() {
        return "superior.plot.visit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "visit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_VISIT.getMessage(locale);
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
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        Plot targetPlot = CommandArguments.getPlot(plugin, sender, args[1]).getPlot();

        if (targetPlot == null)
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        Location visitLocation = plugin.getSettings().getVisitorsSign().isRequiredForVisit() ?
                targetPlot.getVisitorsLocation(null /* unused */) :
                targetPlot.getPlotHome(plugin.getSettings().getWorlds().getDefaultWorld());

        if (visitLocation == null) {
            Message.INVALID_VISIT_LOCATION.send(sender);

            if (!superiorPlayer.hasBypassModeEnabled())
                return;

            visitLocation = targetPlot.getPlotHome(plugin.getSettings().getWorlds().getDefaultWorld());
            Message.INVALID_VISIT_LOCATION_BYPASS.send(sender);
        }

        if (targetPlot.isLocked() && !targetPlot.hasPermission(superiorPlayer, PlotPrivileges.CLOSE_BYPASS)) {
            Message.NO_CLOSE_BYPASS.send(sender);
            return;
        }

        superiorPlayer.teleport(visitLocation);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayersWithPlots(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(),
                (onlinePlayer, onlinePlot) -> onlinePlot != null && (
                        (!plugin.getSettings().getVisitorsSign().isRequiredForVisit() || onlinePlot.getVisitorsLocation(null /* unused */) != null) ||
                                superiorPlayer.hasBypassModeEnabled()) && (!onlinePlot.isLocked() ||
                        onlinePlot.hasPermission(superiorPlayer, PlotPrivileges.CLOSE_BYPASS))) : Collections.emptyList();
    }

}

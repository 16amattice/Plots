package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdRate implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("rate");
    }

    @Override
    public String getPermission() {
        return "superior.plot.rate";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "rate [" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_RATE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
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
        PlotArgument arguments = args.length == 1 ? CommandArguments.getPlotWhereStanding(plugin, sender) :
                CommandArguments.getPlot(plugin, sender, args[1]);

        Plot plot = arguments.getPlot();

        if (plot == null)
            return;

        if (plot.isSpawn()) {
            Message.INVALID_PLOT_LOCATION.send(sender);
            return;
        }

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        if (!plugin.getSettings().isRateOwnPlot() && plot.equals(superiorPlayer.getPlot())) {
            Message.RATE_OWN_PLOT.send(superiorPlayer);
            return;
        }

        plugin.getMenus().openPlotRate(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), plot);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        Plot plot = superiorPlayer.getPlot();
        return args.length == 2 ? CommandTabCompletes.getOnlinePlayersWithPlots(plugin, args[1],
                plugin.getSettings().isTabCompleteHideVanished(),
                (onlinePlayer, onlinePlot) -> onlinePlot != null &&
                        (plugin.getSettings().isRateOwnPlot() || !onlinePlot.equals(plot))) : Collections.emptyList();
    }

}

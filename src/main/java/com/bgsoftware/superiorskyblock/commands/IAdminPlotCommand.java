package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotsListArgument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public interface IAdminPlotCommand extends ISuperiorCommand {

    @Override
    default void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (!supportMultiplePlots()) {
            PlotArgument arguments = CommandArguments.getPlot(plugin, sender, args[2]);
            if (arguments.getPlot() != null)
                execute(plugin, sender, arguments.getSuperiorPlayer(), arguments.getPlot(), args);
        } else {
            PlotsListArgument arguments = CommandArguments.getMultiplePlots(plugin, sender, args[2]);
            if (!arguments.getPlots().isEmpty())
                execute(plugin, sender, arguments.getSuperiorPlayer(), arguments.getPlots(), args);
        }
    }

    @Override
    default List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        List<String> tabVariables = new LinkedList<>();

        if (args.length == 3) {
            if (supportMultiplePlots() && "*".contains(args[2]))
                tabVariables.add("*");
            tabVariables.addAll(CommandTabCompletes.getOnlinePlayersWithPlots(plugin, args[2], false, null));
        } else if (args.length > 3) {
            if (supportMultiplePlots()) {
                tabVariables = adminTabComplete(plugin, sender, null, args);
            } else {
                SuperiorPlayer targetPlayer = plugin.getPlayers().getSuperiorPlayer(args[2]);
                Plot plot = targetPlayer == null ? plugin.getGrid().getPlot(args[2]) : targetPlayer.getPlot();
                if (plot != null) {
                    tabVariables = adminTabComplete(plugin, sender, plot, args);
                    if (tabVariables.isEmpty())
                        tabVariables = adminTabComplete(plugin, sender, plot, args);
                }
            }
        }

        return tabVariables == null ? Collections.emptyList() : Collections.unmodifiableList(tabVariables);
    }

    boolean supportMultiplePlots();

    default void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer,
                         Plot plot, String[] args) {
        // Not all commands should implement this method.
    }

    default void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer,
                         List<Plot> plots, String[] args) {
        // Not all commands should implement this method.
    }

    default List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return Collections.emptyList();
    }

}

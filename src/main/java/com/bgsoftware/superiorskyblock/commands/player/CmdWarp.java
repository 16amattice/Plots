package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CmdWarp implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("warp");
    }

    @Override
    public String getPermission() {
        return "superior.plot.warp";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "warp [" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "] [" +
                Message.COMMAND_ARGUMENT_WARP_NAME.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_WARP.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        Plot targetPlot = null;
        String targetWarpName = null;

        switch (args.length) {
            case 1: {
                PlotArgument arguments = CommandArguments.getSenderPlot(plugin, sender);
                targetPlot = arguments.getPlot();
                break;
            }
            case 2: {
                PlotArgument arguments = CommandArguments.getSenderPlot(plugin, sender);
                targetPlot = arguments.getPlot();
                targetWarpName = args[1];
                break;
            }
            case 3: {
                PlotArgument arguments = CommandArguments.getPlot(plugin, sender, args[1]);
                targetPlot = arguments.getPlot();
                targetWarpName = args[2];
                break;
            }
        }

        if (targetPlot == null)
            return;

        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        PlotWarp plotWarp = targetWarpName == null ? null : targetPlot.getWarp(targetWarpName);

        if (plotWarp == null) {
            switch (args.length) {
                case 1:
                    Menus.MENU_WARP_CATEGORIES.openMenu(superiorPlayer, superiorPlayer.getOpenedView(), targetPlot);
                    break;
                case 2:
                    PlotArgument arguments = CommandArguments.getPlot(plugin, sender, args[1]);
                    targetPlot = arguments.getPlot();
                    if (targetPlot != null) {
                        Menus.MENU_WARP_CATEGORIES.openMenu(superiorPlayer, superiorPlayer.getOpenedView(), targetPlot);
                    }
                    break;
                case 3:
                    Message.INVALID_WARP.send(superiorPlayer, targetWarpName);
                    break;
            }

            return;
        }

        if (!targetPlot.isMember(superiorPlayer) && plotWarp.hasPrivateFlag()) {
            Message.INVALID_WARP.send(superiorPlayer, targetWarpName);
            return;
        }

        targetPlot.warpPlayer(superiorPlayer, targetWarpName);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
        Plot playerPlot = superiorPlayer.getPlot();

        switch (args.length) {
            case 2: {
                List<String> tabCompletes = new LinkedList<>(CommandTabCompletes.getPlayerPlotsExceptSender(plugin, sender, args[1], true,
                        (player, plot) -> plot.getPlotWarps().values().stream().anyMatch(plotWarp ->
                                plot.isMember(superiorPlayer) || !plotWarp.hasPrivateFlag())));

                if (playerPlot != null) {
                    for (String warpName : playerPlot.getPlotWarps().keySet()) {
                        if (warpName.startsWith(args[1]))
                            tabCompletes.add(warpName);
                    }
                }

                return tabCompletes.isEmpty() ? Collections.emptyList() : tabCompletes;
            }
            case 3: {
                Plot targetPlot = plugin.getGrid().getPlot(args[1]);
                if (targetPlot != null) {
                    return new SequentialListBuilder<Map.Entry<String, PlotWarp>>()
                            .filter(plotWarpEntry -> targetPlot.isMember(superiorPlayer) || !plotWarpEntry.getValue().hasPrivateFlag())
                            .map(targetPlot.getPlotWarps().entrySet(), Map.Entry::getKey);
                }
                break;
            }
        }

        return Collections.emptyList();
    }

}

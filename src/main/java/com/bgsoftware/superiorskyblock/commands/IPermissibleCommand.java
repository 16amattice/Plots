package com.bgsoftware.superiorskyblock.commands;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface IPermissibleCommand extends ISuperiorCommand {

    @Override
    default void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        Plot plot = null;
        SuperiorPlayer superiorPlayer = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            PlotArgument arguments = CommandArguments.getSenderPlot(plugin, sender);

            plot = arguments.getPlot();

            if (plot == null)
                return;

            superiorPlayer = arguments.getSuperiorPlayer();

            if (!superiorPlayer.hasPermission(getPrivilege())) {
                getPermissionLackMessage().send(superiorPlayer, plot.getRequiredPlayerRole(getPrivilege()));
                return;
            }
        }

        execute(plugin, superiorPlayer, plot, args);
    }

    @Override
    default List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        Plot plot = null;
        SuperiorPlayer superiorPlayer = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);
            plot = superiorPlayer.getPlot();
        }

        return superiorPlayer == null || (plot != null && superiorPlayer.hasPermission(getPrivilege())) ?
                tabComplete(plugin, superiorPlayer, plot, args) : Collections.emptyList();
    }

    PlotPrivilege getPrivilege();

    Message getPermissionLackMessage();

    void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args);

    default List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return Collections.emptyList();
    }

}

package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CmdAdminRecalc implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("recalc", "recalculate", "level");
    }

    @Override
    public String getPermission() {
        return "superior.admin.recalc";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin recalc <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_RECALC.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, List<Plot> plots, String[] args) {
        if (plots.size() > 1) {
            Message.RECALC_ALL_PLOTS.send(sender);
            plugin.getGrid().calcAllPlots(() -> Message.RECALC_ALL_PLOTS_DONE.send(sender));
        } else {
            Plot plot = plots.get(0);

            if (plot.isBeingRecalculated()) {
                Message.RECALC_ALREADY_RUNNING_OTHER.send(sender);
                return;
            }

            Message.RECALC_PROCCESS_REQUEST.send(sender);
            plot.calcPlotWorth(sender instanceof Player ? plugin.getPlayers().getSuperiorPlayer(sender) : null);
        }
    }

}

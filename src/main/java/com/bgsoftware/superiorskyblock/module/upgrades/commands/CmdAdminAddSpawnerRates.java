package com.bgsoftware.superiorskyblock.module.upgrades.commands;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminAddSpawnerRates implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("addspawnerrates");
    }

    @Override
    public String getPermission() {
        return "superior.admin.addspawnerrates";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin addspawnerrates <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_MULTIPLIER.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_ADD_SPAWNER_RATES.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 4;
    }

    @Override
    public int getMaxArgs() {
        return 4;
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
        NumberArgument<Double> arguments = CommandArguments.getMultiplier(sender, args[3]);

        if (!arguments.isSucceed())
            return;

        double multiplier = arguments.getNumber();

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            EventResult<Double> eventResult = plugin.getEventsBus().callPlotChangeSpawnerRatesEvent(sender,
                    plot, plot.getSpawnerRatesMultiplier() + multiplier);
            anyPlotChanged |= !eventResult.isCancelled();
            if (!eventResult.isCancelled())
                plot.setSpawnerRatesMultiplier(eventResult.getResult());
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_SPAWNER_RATES_ALL.send(sender);
        else if (targetPlayer == null)
            Message.CHANGED_SPAWNER_RATES_NAME.send(sender, plots.get(0).getName());
        else
            Message.CHANGED_SPAWNER_RATES.send(sender, targetPlayer.getName());
    }

}

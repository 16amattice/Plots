package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotChangeLevelBonusEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotChangeWorthBonusEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CmdAdminAddBonus implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("addbonus");
    }

    @Override
    public String getPermission() {
        return "superior.admin.addbonus";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin addbonus <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <worth/level> <" +
                Message.COMMAND_ARGUMENT_AMOUNT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_ADD_BONUS.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 5;
    }

    @Override
    public int getMaxArgs() {
        return 5;
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
        boolean isWorthBonus = !args[3].equalsIgnoreCase("level");

        BigDecimal bonus = CommandArguments.getBigDecimalAmount(sender, args[4]);

        if (bonus == null)
            return;

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            if (isWorthBonus) {
                EventResult<BigDecimal> eventResult = plugin.getEventsBus().callPlotChangeWorthBonusEvent(sender, plot,
                        PlotChangeWorthBonusEvent.Reason.COMMAND, plot.getBonusWorth().add(bonus));
                if (!eventResult.isCancelled()) {
                    plot.setBonusWorth(eventResult.getResult());
                    anyPlotChanged = true;
                }
            } else {
                EventResult<BigDecimal> eventResult = plugin.getEventsBus().callPlotChangeLevelBonusEvent(sender, plot,
                        PlotChangeLevelBonusEvent.Reason.COMMAND, plot.getBonusLevel().add(bonus));
                if (!eventResult.isCancelled()) {
                    plot.setBonusLevel(eventResult.getResult());
                    anyPlotChanged = true;
                }
            }
        }

        if (!anyPlotChanged)
            return;

        Message.BONUS_SET_SUCCESS.send(sender, bonus.toString());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getCustomComplete(args[3], "worth", "level") : Collections.emptyList();
    }

}

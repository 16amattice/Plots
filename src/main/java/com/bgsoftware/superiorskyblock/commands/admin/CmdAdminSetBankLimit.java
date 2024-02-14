package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CmdAdminSetBankLimit implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setbanklimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setbanklimit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setbanklimit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LIMIT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_BANK_LIMIT.getMessage(locale);
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
        BigDecimal limit = CommandArguments.getBigDecimalAmount(sender, args[3]);

        if (limit == null)
            return;

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            EventResult<BigDecimal> eventResult = plugin.getEventsBus().callPlotChangeBankLimitEvent(sender, plot, limit);

            if (eventResult.isCancelled())
                continue;

            anyPlotChanged = true;
            plot.setBankLimit(eventResult.getResult());
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_BANK_LIMIT_ALL.send(sender);
        else if (targetPlayer == null)
            Message.CHANGED_BANK_LIMIT_NAME.send(sender, plots.get(0).getName());
        else
            Message.CHANGED_BANK_LIMIT.send(sender, targetPlayer.getName());
    }

}

package com.bgsoftware.superiorskyblock.module.bank.commands;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CmdDeposit implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("deposit");
    }

    @Override
    public String getPermission() {
        return "superior.plot.deposit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "deposit <" + Message.COMMAND_ARGUMENT_AMOUNT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_DEPOSIT.getMessage(locale);
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
        PlotArgument arguments = CommandArguments.getSenderPlot(plugin, sender);

        Plot plot = arguments.getPlot();

        if (plot == null)
            return;

        SuperiorPlayer superiorPlayer = arguments.getSuperiorPlayer();

        BigDecimal moneyInBank = plugin.getProviders().getBankEconomyProvider().getBalance(superiorPlayer);
        BigDecimal amount = BigDecimal.valueOf(-1);

        if (args[1].equalsIgnoreCase("all") || args[1].equals("*")) {
            amount = moneyInBank;
        } else try {
            amount = BigDecimal.valueOf(Double.parseDouble(args[1]));
        } catch (IllegalArgumentException ignored) {
        }

        BankTransaction transaction = plot.getPlotBank().depositMoney(superiorPlayer, amount);
        Menus.MENU_PLOT_BANK.handleDeposit(superiorPlayer, plot, transaction, null, null, amount);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}

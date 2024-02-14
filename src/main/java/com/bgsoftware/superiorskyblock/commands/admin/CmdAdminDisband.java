package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CmdAdminDisband implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("disband");
    }

    @Override
    public String getPermission() {
        return "superior.admin.disband";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin disband <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_DISBAND.getMessage(locale);
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
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, Plot plot, String[] args) {
        if (plugin.getEventsBus().callPlotDisbandEvent(targetPlayer, plot)) {
            PlotUtils.sendMessage(plot, Message.DISBAND_ANNOUNCEMENT, Collections.emptyList(), sender.getName());

            if (targetPlayer == null)
                Message.DISBANDED_PLOT_OTHER_NAME.send(sender, plot.getName());
            else
                Message.DISBANDED_PLOT_OTHER.send(sender, targetPlayer.getName());

            if (BuiltinModules.BANK.disbandRefund > 0) {
                Message.DISBAND_PLOT_BALANCE_REFUND.send(plot.getOwner(),
                        Formatters.NUMBER_FORMATTER.format(plot.getPlotBank()
                                .getBalance().multiply(BigDecimal.valueOf(BuiltinModules.BANK.disbandRefund))));
            }

            plot.disbandPlot();
        }
    }

}

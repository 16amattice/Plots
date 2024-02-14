package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdDisband implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("disband", "reset", "delete");
    }

    @Override
    public String getPermission() {
        return "superior.plot.disband";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "disband";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_DISBAND.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.DISBAND_PLOT;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_DISBAND_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        if (!superiorPlayer.hasDisbands() && plugin.getSettings().getDisbandCount() > 0) {
            Message.NO_MORE_DISBANDS.send(superiorPlayer);
            return;
        }

        if (plugin.getSettings().isDisbandConfirm()) {
            plugin.getMenus().openConfirmDisband(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), plot);
        } else if (plugin.getEventsBus().callPlotDisbandEvent(superiorPlayer, plot)) {
            PlotUtils.sendMessage(plot, Message.DISBAND_ANNOUNCEMENT, Collections.emptyList(), superiorPlayer.getName());

            Message.DISBANDED_PLOT.send(superiorPlayer);

            if (BuiltinModules.BANK.disbandRefund > 0) {
                Message.DISBAND_PLOT_BALANCE_REFUND.send(plot.getOwner(), Formatters.NUMBER_FORMATTER.format(
                        plot.getPlotBank().getBalance().multiply(BigDecimal.valueOf(BuiltinModules.BANK.disbandRefund))));
            }

            superiorPlayer.setDisbands(superiorPlayer.getDisbands() - 1);
            plot.disbandPlot();
        }

    }

}

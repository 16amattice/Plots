package com.bgsoftware.superiorskyblock.module.upgrades.commands;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotUpgradeEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.events.EventsBus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetUpgrade implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setupgrade");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setupgrade";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setupgrade <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_UPGRADE_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LEVEL.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_UPGRADE.getMessage(locale);
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
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, Plot plot, String[] args) {
        Upgrade upgrade = CommandArguments.getUpgrade(plugin, sender, args[3]);

        if (upgrade == null)
            return;

        NumberArgument<Integer> arguments = CommandArguments.getLevel(sender, args[4]);

        if (!arguments.isSucceed())
            return;

        int level = arguments.getNumber();
        int maxLevel = upgrade.getMaxUpgradeLevel();

        if (level > maxLevel) {
            Message.MAXIMUM_LEVEL.send(sender, maxLevel);
            return;
        }

        EventResult<EventsBus.UpgradeResult> eventResult = plugin.getEventsBus().callPlotUpgradeEvent(
                sender, plot, upgrade, upgrade.getUpgradeLevel(level), PlotUpgradeEvent.Cause.ADMIN_SET_UPGRADE);

        if (eventResult.isCancelled())
            return;

        plot.setUpgradeLevel(upgrade, level);

        if (targetPlayer == null)
            Message.SET_UPGRADE_LEVEL_NAME.send(sender, upgrade.getName(), plot.getName());
        else
            Message.SET_UPGRADE_LEVEL.send(sender, upgrade.getName(), targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getUpgrades(plugin, args[3]) : Collections.emptyList();
    }

}

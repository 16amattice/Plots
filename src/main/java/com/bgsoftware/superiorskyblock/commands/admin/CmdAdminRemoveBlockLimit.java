package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CmdAdminRemoveBlockLimit implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("removeblocklimit", "remblocklimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.removeblocklimit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin removeblocklimit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_MATERIAL.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_REMOVE_BLOCK_LIMIT.getMessage(locale);
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
        Key key = Keys.ofMaterialAndData(args[3]);

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            if (plugin.getEventsBus().callPlotRemoveBlockLimitEvent(sender, plot, key)) {
                anyPlotChanged = true;
                plot.removeBlockLimit(key);
            }
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_BLOCK_LIMIT_ALL.send(sender, Formatters.CAPITALIZED_FORMATTER.format(key.getGlobalKey()));
        else if (targetPlayer == null)
            Message.CHANGED_BLOCK_LIMIT_NAME.send(sender, Formatters.CAPITALIZED_FORMATTER.format(key.getGlobalKey()), plots.get(0).getName());
        else
            Message.CHANGED_BLOCK_LIMIT.send(sender, Formatters.CAPITALIZED_FORMATTER.format(key.getGlobalKey()), targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getMaterials(args[3]) : Collections.emptyList();
    }

}

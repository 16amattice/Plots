package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetBlockLimit implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setblocklimit");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setblocklimit";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setblocklimit <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_MATERIAL.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LIMIT.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_BLOCK_LIMIT.getMessage(locale);
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
        Key key = Keys.ofMaterialAndData(args[3]);

        NumberArgument<Integer> arguments = CommandArguments.getLimit(sender, args[4]);

        if (!arguments.isSucceed())
            return;

        int limit = arguments.getNumber();

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeBlockLimitEvent(sender, plot, key, limit);
            anyPlotChanged |= !eventResult.isCancelled();
            if (!eventResult.isCancelled())
                plot.setBlockLimit(key, eventResult.getResult());
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

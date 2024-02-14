package com.bgsoftware.superiorskyblock.commands.admin;

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

import java.util.Arrays;
import java.util.List;

public class CmdAdminSetSize implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("setsize", "setplotsize", "setbordersize");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setsize";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setsize <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_SIZE.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_SIZE.getMessage(locale);
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
        NumberArgument<Integer> arguments = CommandArguments.getSize(sender, args[3]);

        if (!arguments.isSucceed())
            return;

        int size = arguments.getNumber();

        if (size > plugin.getSettings().getMaxPlotSize()) {
            Message.SIZE_BIGGER_MAX.send(sender);
            return;
        }

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeBorderSizeEvent(sender, plot, size);
            anyPlotChanged |= !eventResult.isCancelled();
            if (!eventResult.isCancelled())
                plot.setPlotSize(eventResult.getResult());
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_PLOT_SIZE_ALL.send(sender);
        else if (targetPlayer == null)
            Message.CHANGED_PLOT_SIZE_NAME.send(sender, plots.get(0).getName());
        else
            Message.CHANGED_PLOT_SIZE.send(sender, targetPlayer.getName());

        if (plugin.getSettings().isBuildOutsidePlot())
            Message.CHANGED_PLOT_SIZE_BUILD_OUTSIDE.send(sender);
    }

}

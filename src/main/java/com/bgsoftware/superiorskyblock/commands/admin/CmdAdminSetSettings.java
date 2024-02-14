package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetSettings implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setsettings");
    }

    @Override
    public String getPermission() {
        return "superior.admin.setsettings";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin setsettings <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_SETTINGS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_VALUE.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_SETTINGS.getMessage(locale);
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
        PlotFlag plotFlag = CommandArguments.getPlotFlag(sender, args[3]);

        if (plotFlag == null)
            return;

        boolean value = args[4].equalsIgnoreCase("true");

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            if (plot.hasSettingsEnabled(plotFlag) == value) {
                anyPlotChanged = true;
                continue;
            }

            if (value) {
                if (plugin.getEventsBus().callPlotEnableFlagEvent(sender, plot, plotFlag)) {
                    anyPlotChanged = true;
                    plot.enableSettings(plotFlag);
                }
            } else if (plugin.getEventsBus().callPlotDisableFlagEvent(sender, plot, plotFlag)) {
                anyPlotChanged = true;
                plot.disableSettings(plotFlag);
            }
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() != 1)
            Message.SETTINGS_UPDATED_ALL.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotFlag.getName()));
        else if (targetPlayer == null)
            Message.SETTINGS_UPDATED_NAME.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotFlag.getName()), plots.get(0).getName());
        else
            Message.SETTINGS_UPDATED.send(sender, Formatters.CAPITALIZED_FORMATTER.format(plotFlag.getName()), targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getPlotFlags(args[3]) :
                args.length == 5 ? CommandTabCompletes.getCustomComplete(args[4], "true", "false") :
                        Collections.emptyList();
    }

}

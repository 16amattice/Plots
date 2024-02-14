package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdVisitors implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("visitors");
    }

    @Override
    public String getPermission() {
        return "superior.plot.panel";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "visitors";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_VISITORS.getMessage(locale);
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
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        PlotArgument arguments = CommandArguments.getSenderPlot(plugin, sender);

        Plot plot = arguments.getPlot();

        if (plot == null)
            return;

        SuperiorPlayer superiorPlayer = arguments.getSuperiorPlayer();

        plugin.getMenus().openVisitors(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), plot);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}

package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.PlotArgument;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdMembers implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("members");
    }

    @Override
    public String getPermission() {
        return "superior.plot.panel";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "members";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_MEMBERS.getMessage(locale);
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

        plugin.getMenus().openMembers(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), plot);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}

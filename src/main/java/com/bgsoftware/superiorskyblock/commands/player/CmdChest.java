package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CmdChest implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("chest", "vault");
    }

    @Override
    public String getPermission() {
        return "superior.plot.chest";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "chest [" + Message.COMMAND_ARGUMENT_PAGE.getMessage(locale) + "]";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_CHEST.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
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
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.PLOT_CHEST;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_PLOT_CHEST_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        if (args.length == 2) {
            NumberArgument<Integer> pageArguments = CommandArguments.getPage(superiorPlayer.asPlayer(), args[1]);

            if (!pageArguments.isSucceed())
                return;

            int page = pageArguments.getNumber() - 1;
            PlotChest[] plotChests = plot.getChest();

            if (page < 0 || page >= plotChests.length) {
                Message.INVALID_PAGE.send(superiorPlayer, args[1]);
                return;
            }

            plotChests[page].openChest(superiorPlayer);
        } else {
            Menus.MENU_PLOT_CHEST.openMenu(superiorPlayer, superiorPlayer.getOpenedView(), plot);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        PlotChest[] plotChests = plot.getChest();
        return args.length == 1 || plotChests.length == 0 ? Collections.emptyList() :
                CommandTabCompletes.getCustomComplete(args[1], IntStream.range(1, plotChests.length + 1).boxed()
                        .map(Object::toString).toArray(String[]::new));
    }

}

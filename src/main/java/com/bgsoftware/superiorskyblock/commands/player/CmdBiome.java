package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.Arrays;
import java.util.List;

public class CmdBiome implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("biome", "setbiome");
    }

    @Override
    public String getPermission() {
        return "superior.plot.biome";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "biome";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_BIOME.getMessage(locale);
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
        return PlotPrivileges.SET_BIOME;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_BIOME_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        plugin.getMenus().openBiomes(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), superiorPlayer.getPlot());
    }

}

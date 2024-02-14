package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Collections;
import java.util.List;

public class CmdSettings implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("settings");
    }

    @Override
    public String getPermission() {
        return "superior.plot.settings";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "settings";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_SETTINGS.getMessage(locale);
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
        return PlotPrivileges.SET_SETTINGS;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_SETTINGS_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        plugin.getMenus().openSettings(superiorPlayer, MenuViewWrapper.fromView(superiorPlayer.getOpenedView()), plot);
    }

}

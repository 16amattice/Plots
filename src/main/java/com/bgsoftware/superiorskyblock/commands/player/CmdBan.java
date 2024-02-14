package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;

import java.util.Collections;
import java.util.List;

public class CmdBan implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("ban");
    }

    @Override
    public String getPermission() {
        return "superior.plot.ban";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "ban <" + Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_BAN.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
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
        return PlotPrivileges.BAN_MEMBER;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_BAN_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        SuperiorPlayer targetPlayer = CommandArguments.getPlayer(plugin, superiorPlayer, args[1]);

        if (targetPlayer == null)
            return;

        if (!PlotUtils.checkBanRestrictions(superiorPlayer, plot, targetPlayer))
            return;

        if (plugin.getSettings().isBanConfirm()) {
            plugin.getMenus().openConfirmBan(superiorPlayer, null, plot, targetPlayer);
        } else {
            PlotUtils.handleBanPlayer(superiorPlayer, plot, targetPlayer);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length != 2 ? Collections.emptyList() : CommandTabCompletes.getOnlinePlayers(plugin, args[1], true,
                onlinePlayer -> !plot.isBanned(onlinePlayer) && (!plot.isMember(onlinePlayer) ||
                        onlinePlayer.getPlayerRole().isLessThan(superiorPlayer.getPlayerRole())));
    }

}

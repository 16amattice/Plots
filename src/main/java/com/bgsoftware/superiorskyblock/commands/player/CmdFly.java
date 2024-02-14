package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdFly implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("fly");
    }

    @Override
    public String getPermission() {
        return "superior.plot.fly";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "fly";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_FLY.getMessage(locale);
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
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        if (!plugin.getEventsBus().callPlayerToggleFlyEvent(superiorPlayer))
            return;

        Plot plot = plugin.getGrid().getPlotAt(superiorPlayer.getLocation());
        Player player = (Player) sender;

        if (superiorPlayer.hasPlotFlyEnabled()) {
            player.setAllowFlight(false);
            player.setFlying(false);

            Message.TOGGLED_FLY_OFF.send(superiorPlayer);
        } else {
            if (plot != null && plot.hasPermission(superiorPlayer, PlotPrivileges.FLY)) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }

            Message.TOGGLED_FLY_ON.send(superiorPlayer);
        }

        superiorPlayer.togglePlotFly();
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}

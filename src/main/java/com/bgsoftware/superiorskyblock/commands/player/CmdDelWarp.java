package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.plot.warp.SignWarp;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CmdDelWarp implements IPermissibleCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("delwarp");
    }

    @Override
    public String getPermission() {
        return "superior.plot.delwarp";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "delwarp <" + Message.COMMAND_ARGUMENT_WARP_NAME.getMessage(locale) + "...>";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_DEL_WARP.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.DELETE_WARP;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_DELETE_WARP_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        Player player = superiorPlayer.asPlayer();
        PlotWarp plotWarp = CommandArguments.getWarp(player, plot, args, 1);

        if (plotWarp == null)
            return;

        if (!plugin.getEventsBus().callPlotDeleteWarpEvent(superiorPlayer, plot, plotWarp))
            return;

        plot.deleteWarp(plotWarp.getName());
        Message.DELETE_WARP.send(superiorPlayer, plotWarp.getName());

        ChunksProvider.loadChunk(ChunkPosition.of(plotWarp.getLocation()), ChunkLoadReason.WARP_SIGN_BREAK, chunk -> {
            SignWarp.trySignWarpBreak(plotWarp, player);
        });
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        return args.length == 2 ? CommandTabCompletes.getPlotWarps(plot, args[1]) : Collections.emptyList();
    }

}

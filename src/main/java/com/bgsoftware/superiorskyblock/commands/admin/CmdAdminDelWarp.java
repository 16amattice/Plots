package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.warp.SignWarp;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdAdminDelWarp implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("delwarp");
    }

    @Override
    public String getPermission() {
        return "superior.admin.delwarp";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin delwarp <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_WARP_NAME.getMessage(locale) + "...>";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_DEL_WARP.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 4;
    }

    @Override
    public int getMaxArgs() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, Plot plot, String[] args) {
        PlotWarp plotWarp = CommandArguments.getWarp(sender, plot, args, 3);

        if (plotWarp == null)
            return;

        if (!plugin.getEventsBus().callPlotDeleteWarpEvent(sender, plotWarp.getPlot(), plotWarp))
            return;

        plot.deleteWarp(plotWarp.getName());
        Message.DELETE_WARP.send(sender, plotWarp.getName());

        ChunksProvider.loadChunk(ChunkPosition.of(plotWarp.getLocation()), ChunkLoadReason.WARP_SIGN_BREAK, chunk -> {
            SignWarp.trySignWarpBreak(plotWarp, sender);
        });
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getPlotWarps(plot, args[3]) : Collections.emptyList();
    }

}

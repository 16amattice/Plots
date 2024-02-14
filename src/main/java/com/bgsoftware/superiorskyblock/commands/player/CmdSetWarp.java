package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.commands.IPermissibleCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.google.common.base.Preconditions;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

public class CmdSetWarp implements IPermissibleCommand {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setwarp");
    }

    @Override
    public String getPermission() {
        return "superior.plot.setwarp";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        StringBuilder usage = new StringBuilder("setwarp <")
                .append(Message.COMMAND_ARGUMENT_WARP_NAME.getMessage(locale)).append(">");

        if (plugin.getSettings().isWarpCategories())
            usage.append(" [").append(Message.COMMAND_ARGUMENT_WARP_CATEGORY.getMessage(locale)).append("]");

        return usage.toString();
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_SET_WARP.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return plugin.getSettings().isWarpCategories() ? 3 : 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public PlotPrivilege getPrivilege() {
        return PlotPrivileges.SET_WARP;
    }

    @Override
    public Message getPermissionLackMessage() {
        return Message.NO_SET_WARP_PERMISSION;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, SuperiorPlayer superiorPlayer, Plot plot, String[] args) {
        if (plot.getPlotWarps().size() >= plot.getWarpsLimit()) {
            Message.NO_MORE_WARPS.send(superiorPlayer);
            return;
        }

        String warpName = args[1];

        if (warpName.isEmpty()) {
            Message.WARP_ILLEGAL_NAME.send(superiorPlayer);
            return;
        }

        if (!PlotUtils.isWarpNameLengthValid(warpName)) {
            Message.WARP_NAME_TOO_LONG.send(superiorPlayer);
            return;
        }

        if (plot.getWarp(warpName) != null) {
            Message.WARP_ALREADY_EXIST.send(superiorPlayer);
            return;
        }

        if (!plot.isInsideRange(superiorPlayer.getLocation())) {
            Message.SET_WARP_OUTSIDE.send(superiorPlayer);
            return;
        }

        String categoryName = null;

        if (args.length == 3) {
            categoryName = args[2];

            if (categoryName.isEmpty()) {
                Message.WARP_CATEGORY_ILLEGAL_NAME.send(superiorPlayer);
                return;
            }

            if (!PlotUtils.isWarpNameLengthValid(categoryName)) {
                Message.WARP_CATEGORY_NAME_TOO_LONG.send(superiorPlayer);
                return;
            }

            if (plot.getWarpCategory(categoryName) == null &&
                    !plugin.getEventsBus().callPlotCreateWarpCategoryEvent(superiorPlayer, plot, categoryName))
                return;
        }

        WarpCategory warpCategory = categoryName == null ? null : plot.createWarpCategory(categoryName);

        Location warpLocation = superiorPlayer.getLocation();

        Preconditions.checkState(warpLocation != null, "Null location for a warp.");

        if (!plugin.getEventsBus().callPlotCreateWarpEvent(superiorPlayer, plot, warpName, warpLocation, warpCategory))
            return;

        plot.createWarp(warpName, warpLocation, warpCategory);

        Message.SET_WARP.send(superiorPlayer, Formatters.LOCATION_FORMATTER.format(warpLocation));
    }

}

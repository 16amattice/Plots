package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotNames;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CmdCreate implements ISuperiorCommand {

    private final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("create");
    }

    @Override
    public String getPermission() {
        return "superior.plot.create";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        StringBuilder usage = new StringBuilder("create");

        if (plugin.getSettings().getPlotNames().isRequiredForCreation())
            usage.append(" <").append(Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale)).append(">");

        if (plugin.getSettings().isSchematicNameArgument())
            usage.append(" [").append(Message.COMMAND_ARGUMENT_SCHEMATIC_NAME.getMessage(locale)).append("]");

        return usage.toString();
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_CREATE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return plugin.getSettings().getPlotNames().isRequiredForCreation() ? 2 : 1;
    }

    @Override
    public int getMaxArgs() {
        int args = 3;

        if (!plugin.getSettings().getPlotNames().isRequiredForCreation())
            args--;

        if (!plugin.getSettings().isSchematicNameArgument())
            args--;

        return args;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(sender);

        if (superiorPlayer.getPlot() != null) {
            Message.ALREADY_IN_PLOT.send(superiorPlayer);
            return;
        }

        if (plugin.getGrid().hasActiveCreateRequest(superiorPlayer)) {
            Message.PLOT_CREATE_PROCESS_FAIL.send(superiorPlayer);
            return;
        }

        String plotName = "";
        String schematicName = null;

        if (plugin.getSettings().getPlotNames().isRequiredForCreation()) {
            if (args.length >= 2) {
                plotName = args[1];
                if (!PlotNames.isValidName(sender, null, plotName))
                    return;
            }
        }

        if (plugin.getSettings().isSchematicNameArgument() &&
                args.length == (plugin.getSettings().getPlotNames().isRequiredForCreation() ? 3 : 2)) {
            schematicName = args[plugin.getSettings().getPlotNames().isRequiredForCreation() ? 2 : 1];
            Schematic schematic = plugin.getSchematics().getSchematic(schematicName);
            if (schematic == null || schematicName.endsWith("_nether") || schematicName.endsWith("_the_end")) {
                Message.INVALID_SCHEMATIC.send(sender, schematicName);
                return;
            }
        }

        if (schematicName == null) {
            Menus.MENU_PLOT_CREATION.openMenu(superiorPlayer, superiorPlayer.getOpenedView(), plotName);
        } else {
            Menus.MENU_PLOT_CREATION.simulateClick(superiorPlayer, plotName, schematicName, false, superiorPlayer.getOpenedView());
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        int argumentLength = plugin.getSettings().getPlotNames().isRequiredForCreation() ? 3 : 2;
        return plugin.getSettings().isSchematicNameArgument() && args.length == argumentLength ?
                CommandTabCompletes.getSchematics(plugin, args[argumentLength - 1]) : Collections.emptyList();
    }

}

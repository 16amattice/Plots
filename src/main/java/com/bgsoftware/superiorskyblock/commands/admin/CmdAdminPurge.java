package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdAdminPurge implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("purge");
    }

    @Override
    public String getPermission() {
        return "superior.admin.purge";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin purge <cancel/" + Message.COMMAND_ARGUMENT_TIME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_PURGE.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        if (args[2].equalsIgnoreCase("cancel")) {
            plugin.getGrid().getPlotsToPurge().forEach(plot -> plugin.getGrid().removePlotFromPurge(plot));
            Message.PURGE_CLEAR.send(sender);
        } else {
            long timeToPurge = parseLongSafe(args[2]);
            long currentTime = System.currentTimeMillis() / 1000;

            List<Plot> plots = new SequentialListBuilder<Plot>().filter(plot -> {
                long lastTimeUpdate = plot.getLastTimeUpdate();
                return lastTimeUpdate != -1 && currentTime - lastTimeUpdate >= timeToPurge;
            }).build(plugin.getGrid().getPlots());

            if (plots.isEmpty()) {
                Message.NO_PLOTS_TO_PURGE.send(sender);
            } else {
                BukkitExecutor.async(() -> plots.forEach(plot -> plugin.getGrid().addPlotToPurge(plot)));
                Message.PURGED_PLOTS.send(sender, plots.size());
            }
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return args.length == 3 ? CommandTabCompletes.getCustomComplete(args[2], "cancel") : Collections.emptyList();
    }

    private static long parseLongSafe(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception error) {
            return 0;
        }
    }

}

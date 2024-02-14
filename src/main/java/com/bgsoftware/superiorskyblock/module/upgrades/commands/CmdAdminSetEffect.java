package com.bgsoftware.superiorskyblock.module.upgrades.commands;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.CommandTabCompletes;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.commands.arguments.CommandArguments;
import com.bgsoftware.superiorskyblock.commands.arguments.NumberArgument;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class CmdAdminSetEffect implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("seteffect");
    }

    @Override
    public String getPermission() {
        return "superior.admin.seteffect";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin seteffect <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_ALL_PLOTS.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_EFFECT.getMessage(locale) + "> <" +
                Message.COMMAND_ARGUMENT_LEVEL.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SET_EFFECT.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 5;
    }

    @Override
    public int getMaxArgs() {
        return 5;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return true;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, List<Plot> plots, String[] args) {
        PotionEffectType effectType = CommandArguments.getPotionEffect(sender, args[3]);

        if (effectType == null)
            return;

        NumberArgument<Integer> arguments = CommandArguments.getLevel(sender, args[4]);

        if (!arguments.isSucceed())
            return;

        int level = arguments.getNumber();

        boolean anyPlotChanged = false;

        for (Plot plot : plots) {
            if (level <= 0) {
                if (plugin.getEventsBus().callPlotRemoveEffectEvent(sender, plot, effectType)) {
                    anyPlotChanged = true;
                    plot.removePotionEffect(effectType);
                }
            } else {
                EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeEffectLevelEvent(sender, plot, effectType, level);
                anyPlotChanged |= !eventResult.isCancelled();
                if (!eventResult.isCancelled())
                    plot.setPotionEffect(effectType, eventResult.getResult());
            }
        }

        if (!anyPlotChanged)
            return;

        if (plots.size() > 1)
            Message.CHANGED_PLOT_EFFECT_LEVEL_ALL.send(sender, Formatters.CAPITALIZED_FORMATTER.format(effectType.getName()));
        else if (targetPlayer == null)
            Message.CHANGED_PLOT_EFFECT_LEVEL_NAME.send(sender, Formatters.CAPITALIZED_FORMATTER.format(effectType.getName()), plots.get(0).getName());
        else
            Message.CHANGED_PLOT_EFFECT_LEVEL.send(sender, Formatters.CAPITALIZED_FORMATTER.format(effectType.getName()), targetPlayer.getName());
    }

    @Override
    public List<String> adminTabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, Plot plot, String[] args) {
        return args.length == 4 ? CommandTabCompletes.getPotionEffects(args[3]) : Collections.emptyList();
    }

}

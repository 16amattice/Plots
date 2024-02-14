package com.bgsoftware.superiorskyblock.module.upgrades.type;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.module.upgrades.commands.CmdAdminAddEffect;
import com.bgsoftware.superiorskyblock.module.upgrades.commands.CmdAdminSetEffect;
import com.bgsoftware.superiorskyblock.core.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import java.util.Arrays;
import java.util.List;

public class UpgradeTypePlotEffects implements IUpgradeType {

    private static final List<ISuperiorCommand> commands = Arrays.asList(new CmdAdminAddEffect(),
            new CmdAdminSetEffect());

    private final SuperiorSkyblockPlugin plugin;

    public UpgradeTypePlotEffects(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Listener getListener() {
        return ServerVersion.isAtLeast(ServerVersion.v1_15) ? new PlotEffectsListener() : null;
    }

    @Override
    public List<ISuperiorCommand> getCommands() {
        return commands;
    }

    private class PlotEffectsListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onPlayerEffect(EntityPotionEffectEvent e) {
            if (e.getAction() == EntityPotionEffectEvent.Action.ADDED || !(e.getEntity() instanceof Player) ||
                    e.getCause() == EntityPotionEffectEvent.Cause.PLUGIN || e.getCause() == EntityPotionEffectEvent.Cause.BEACON)
                return;

            Plot plot = plugin.getGrid().getPlotAt(e.getEntity().getLocation());

            if (plot == null)
                return;

            int plotEffectLevel = plot.getPotionEffectLevel(e.getModifiedType());

            if (plotEffectLevel > 0 && (e.getOldEffect() == null || e.getOldEffect().getAmplifier() == plotEffectLevel)) {
                e.setCancelled(true);
            }
        }

    }

}

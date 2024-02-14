package com.bgsoftware.superiorskyblock.service.dragon;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.service.dragon.DragonBattleResetResult;
import com.bgsoftware.superiorskyblock.api.service.dragon.DragonBattleService;
import com.bgsoftware.superiorskyblock.service.IService;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;

public class DragonBattleServiceImpl implements DragonBattleService, IService {

    private final SuperiorSkyblockPlugin plugin;

    public DragonBattleServiceImpl(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<?> getAPIClass() {
        return DragonBattleService.class;
    }

    @Override
    public void prepareEndWorld(World bukkitWorld) {
        Preconditions.checkState(bukkitWorld.getEnvironment() == World.Environment.THE_END, "world must be the_end environment.");
        plugin.getNMSDragonFight().prepareEndWorld(bukkitWorld);
    }

    @Nullable
    @Override
    public EnderDragon getEnderDragon(Plot plot) {
        return plugin.getNMSDragonFight().getEnderDragon(plot);
    }

    @Override
    public void stopEnderDragonBattle(Plot plot) {
        plugin.getNMSDragonFight().removeDragonBattle(plot);
    }

    @Override
    public DragonBattleResetResult resetEnderDragonBattle(Plot plot) {
        if (!plugin.getSettings().getWorlds().getEnd().isDragonFight())
            return DragonBattleResetResult.DRAGON_BATTLES_DISABLED;

        if (!plot.isEndEnabled())
            return DragonBattleResetResult.WORLD_NOT_UNLOCKED;

        if (!plot.wasSchematicGenerated(World.Environment.THE_END))
            return DragonBattleResetResult.WORLD_NOT_GENERATED;

        stopEnderDragonBattle(plot);

        Location plotCenter = plot.getCenter(World.Environment.THE_END);

        plugin.getNMSDragonFight().startDragonBattle(plot, plugin.getSettings().getWorlds().getEnd()
                .getPortalOffset().applyToLocation(plotCenter));

        return DragonBattleResetResult.SUCCESS;
    }

}

package com.bgsoftware.superiorskyblock.nms;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

public class NMSDragonFightImpl implements NMSDragonFight {

    @Override
    public void prepareEndWorld(World bukkitWorld) {
        // Do nothing.
    }

    @Nullable
    @Override
    public EnderDragon getEnderDragon(Plot plot) {
        return null;
    }

    @Override
    public void startDragonBattle(Plot plot, Location location) {
        // Do nothing.
    }

    @Override
    public void removeDragonBattle(Plot plot) {
        // Do nothing.
    }

    @Override
    public void awardTheEndAchievement(Player player) {
        // Do nothing.
    }

}

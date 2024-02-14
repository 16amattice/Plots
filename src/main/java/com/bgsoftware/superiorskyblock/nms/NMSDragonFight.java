package com.bgsoftware.superiorskyblock.nms;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

public interface NMSDragonFight {

    void prepareEndWorld(World bukkitWorld);

    @Nullable
    EnderDragon getEnderDragon(Plot plot);

    void startDragonBattle(Plot plot, Location location);

    void removeDragonBattle(Plot plot);

    void awardTheEndAchievement(Player player);

}

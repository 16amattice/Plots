package com.bgsoftware.superiorskyblock.api.service.dragon;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;

public interface DragonBattleService {

    /**
     * Prepare an end world for dragon fights.
     *
     * @param world The world to prepare.
     */
    void prepareEndWorld(World world);

    /**
     * Get the current active ender dragon of an plot.
     * If there is no active fight, null is returned.
     *
     * @param plot The plot to get the dragon for.
     */
    @Nullable
    EnderDragon getEnderDragon(Plot plot);

    /**
     * Stop the dragon battle fight for an plot.
     * The dragon will be killed and {@link #getEnderDragon(Plot)} will return null.
     */
    void stopEnderDragonBattle(Plot plot);

    /**
     * Reset the dragon battle fight for an plot.
     *
     * @param plot The plot to reset the fight for.
     */
    DragonBattleResetResult resetEnderDragonBattle(Plot plot);


}

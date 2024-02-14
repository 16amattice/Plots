package com.bgsoftware.superiorskyblock.api.hooks;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import org.bukkit.World;

public interface LazyWorldsProvider extends WorldsProvider {

    /**
     * Get the {@link WorldInfo} of the world of an plot by the environment.
     * The world does not have to be loaded.
     *
     * @param plot      The plot to check.
     * @param environment The world environment.
     * @return The world info for the given environment, or null if this environment is not enabled.
     */
    @Nullable
    WorldInfo getPlotsWorldInfo(Plot plot, World.Environment environment);

    /**
     * Get the {@link WorldInfo} of the world of an plot by its name.
     * The world does not have to be loaded.
     *
     * @param plot    The plot to check.
     * @param worldName The name of the world.
     * @return The world info for the given name, or null if this name is not an plots world.
     */
    @Nullable
    WorldInfo getPlotsWorldInfo(Plot plot, String worldName);

}

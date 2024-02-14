package com.bgsoftware.superiorskyblock.api.hooks;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public interface WorldsProvider {

    /**
     * Prepare all the plot worlds on startup.
     */
    void prepareWorlds();

    /**
     * Get the world of an plot by the environment.
     * If the world is not loaded, this method should load the world before returning.
     *
     * @param environment The world environment.
     * @param plot      The plot to check.
     */
    @Nullable
    World getPlotsWorld(Plot plot, World.Environment environment);

    /**
     * Checks if the given world is an plots world.
     *
     * @param world The world to check.
     */
    boolean isPlotsWorld(World world);

    /**
     * Get the location for a new plot that is created.
     *
     * @param previousLocation The location of the previous plot that was created.
     * @param plotsHeight    The default plots height.
     * @param maxPlotSize    The default maximum plot size.
     * @param plotOwner      The owner of the plot.
     * @param plotUUID       The UUID of the plot.
     */
    Location getNextLocation(Location previousLocation, int plotsHeight, int maxPlotSize, UUID plotOwner, UUID plotUUID);

    /**
     * Callback upon finishing of creation of plots.
     *
     * @param plotLocation The location of the new plot.
     * @param plotOwner    The owner of the plot.
     * @param plotUUID     The UUID of the plot.
     */
    void finishPlotCreation(Location plotLocation, UUID plotOwner, UUID plotUUID);

    /**
     * Prepare teleportation of an entity to an plot.
     *
     * @param plot         The target plot.
     * @param location       The location that the entity will be teleported to.
     * @param finishCallback Callback function after the preparation is finished.
     */
    void prepareTeleport(Plot plot, Location location, Runnable finishCallback);

    /**
     * Check whether or not normal worlds are enabled.
     */
    boolean isNormalEnabled();

    /**
     * Check whether or not normal worlds are unlocked for plots by default.
     */
    boolean isNormalUnlocked();

    /**
     * Check whether or not nether worlds are enabled.
     */
    boolean isNetherEnabled();

    /**
     * Check whether or not nether worlds are unlocked for plots by default.
     */
    boolean isNetherUnlocked();

    /**
     * Check whether or not end worlds are enabled.
     */
    boolean isEndEnabled();

    /**
     * Check whether or not end worlds are unlocked for plots by default.
     */
    boolean isEndUnlocked();

}

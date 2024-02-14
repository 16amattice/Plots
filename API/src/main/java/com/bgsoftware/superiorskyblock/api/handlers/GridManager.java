package com.bgsoftware.superiorskyblock.api.handlers;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.data.IDatabaseBridgeHolder;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.container.PlotsContainer;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.world.algorithm.PlotCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GridManager extends IDatabaseBridgeHolder {

    /**
     * Create a new plot.
     *
     * @param superiorPlayer The new owner for the plot.
     * @param schemName      The schematic that should be used.
     * @param bonus          A starting worth for the plot.
     * @param biome          A starting biome for the plot.
     * @param plotName     The name of the new plot.
     */
    void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName);

    /**
     * Create a new plot.
     *
     * @param superiorPlayer The new owner for the plot.
     * @param schemName      The schematic that should be used.
     * @param bonus          A starting worth for the plot.
     * @param biome          A starting biome for the plot.
     * @param plotName     The name of the new plot.
     * @param offset         Should the plot have an offset for it's values? If disabled, the bonus will be given.
     */
    void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName, boolean offset);

    /**
     * Create a new plot.
     *
     * @param superiorPlayer The new owner for the plot.
     * @param schemName      The schematic that should be used.
     * @param bonusWorth     A starting worth for the plot.
     * @param bonusLevel     A starting level for the plot.
     * @param biome          A starting biome for the plot.
     * @param plotName     The name of the new plot.
     * @param offset         Should the plot have an offset for it's values? If disabled, the bonus will be given.
     */
    void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonusWorth, BigDecimal bonusLevel,
                      Biome biome, String plotName, boolean offset);

    /**
     * Create a new plot.
     *
     * @param builder The builder for the plot.
     * @param biome   A starting biome for the plot.
     * @param offset  Should the plot have an offset for its values? If disabled, the bonus will be given.
     */
    void createPlot(Plot.Builder builder, Biome biome, boolean offset);

    /**
     * Set the creation algorithm of plots.
     *
     * @param plotCreationAlgorithm The new algorithm to set.
     *                                If null, the default one will be used.
     */
    void setPlotCreationAlgorithm(@Nullable PlotCreationAlgorithm plotCreationAlgorithm);

    /**
     * Get the currently used plot creation algorithm.
     */
    PlotCreationAlgorithm getPlotCreationAlgorithm();

    /**
     * Checks if a player has an active request for creating an plot.
     *
     * @param superiorPlayer The player to check.
     */
    boolean hasActiveCreateRequest(SuperiorPlayer superiorPlayer);

    /**
     * Start the plot preview task for a specific player.
     *
     * @param superiorPlayer The player to start preview for.
     * @param schemName      The schematic to preview.
     * @param plotName     The requested plot name by the player.
     */
    void startPlotPreview(SuperiorPlayer superiorPlayer, String schemName, String plotName);

    /**
     * Cancel the plot preview for a specific player.
     *
     * @param superiorPlayer The player to cancel preview for.
     */
    void cancelPlotPreview(SuperiorPlayer superiorPlayer);

    /**
     * Cancel all active plot previews.
     */
    void cancelAllPlotPreviews();

    /**
     * Check if a player has an ongoing plot preview task.
     *
     * @param superiorPlayer The player to check.
     */
    @Nullable
    PlotPreview getPlotPreview(SuperiorPlayer superiorPlayer);

    /**
     * Delete an plot.
     *
     * @param plot The plot to delete.
     */
    void deletePlot(Plot plot);

    /**
     * Get the plot of a specific player.
     *
     * @param superiorPlayer The player to check.
     * @return The plot of the player. May be null.
     * @deprecated See {@link SuperiorPlayer#getPlot()}
     */
    @Nullable
    @Deprecated
    Plot getPlot(SuperiorPlayer superiorPlayer);

    /**
     * Get the plot in a specific position from one of the top lists.
     * Positions are starting from 0.
     *
     * @param position    The position to check.
     * @param sortingType The sorting type that should be considered.
     * @return The plot in that position. May be null.
     */
    @Nullable
    Plot getPlot(int position, SortingType sortingType);

    /**
     * Get the position of an plot.
     * Positions are starting from 0.
     *
     * @param plot      The plot to check.
     * @param sortingType The sorting type that should be considered.
     * @return The position of the plot.
     */
    int getPlotPosition(Plot plot, SortingType sortingType);

    /**
     * Get an plot by it's owner uuid.
     *
     * @param uuid The uuid of the owner.
     * @return The plot of the owner. May be null.
     * @deprecated See {@link SuperiorPlayer#getPlot()}
     */
    @Nullable
    @Deprecated
    Plot getPlot(UUID uuid);

    /**
     * Get an plot by it's uuid.
     *
     * @param uuid The uuid of the plot.
     * @return The plot with that UUID. May be null.
     */
    @Nullable
    Plot getPlotByUUID(UUID uuid);

    /**
     * Get an plot by it's name.
     *
     * @param plotName The name to check.
     * @return The plot with that name. May be null.
     */
    @Nullable
    Plot getPlot(String plotName);

    /**
     * Get an plot at an exact position in the world.
     *
     * @param location The position to check.
     * @return The plot at that position. May be null.
     */
    @Nullable
    Plot getPlotAt(@Nullable Location location);

    /**
     * Get an plot from a chunk.
     *
     * @param chunk The chunk to check.
     * @return The plot at that position. May be null.
     */
    @Nullable
    Plot getPlotAt(@Nullable Chunk chunk);

    /**
     * Transfer an plot's leadership to another owner.
     *
     * @param oldOwner The old owner of the plot.
     * @param newOwner The new owner of the plot.
     */
    void transferPlot(UUID oldOwner, UUID newOwner);

    /**
     * Get the amount of plots.
     */
    int getSize();

    /**
     * Sort the plots.
     *
     * @param sortingType The sorting type to use.
     */
    void sortPlots(SortingType sortingType);

    /**
     * Sort the plots.
     *
     * @param sortingType The sorting type to use.
     * @param onFinish    Callback runnable.
     */
    void sortPlots(SortingType sortingType, @Nullable Runnable onFinish);

    /**
     * Get the spawn plot object.
     */
    Plot getSpawnPlot();

    /**
     * Get the world of an plot by the environment.
     * If the environment is not the normal and that environment is disabled in config, null will be returned.
     *
     * @param environment The world environment.
     * @param plot      The plot to check.
     */
    @Nullable
    World getPlotsWorld(Plot plot, World.Environment environment);

    /**
     * Get the {@link WorldInfo} of the world of an plot by the environment.
     * The world might not be loaded at the time of calling this method.
     *
     * @param plot      The plot to check.
     * @param environment The world environment.
     * @return The world info for the given environment, or null if this environment is not enabled.
     */
    @Nullable
    WorldInfo getPlotsWorldInfo(Plot plot, World.Environment environment);

    /**
     * Get the {@link WorldInfo} of the world of an plot by its name.
     * The world might not be loaded at the time of calling this method.
     *
     * @param plot    The plot to check.
     * @param worldName The name of the world.
     * @return The world info for the given name, or null if this name is not an plots world.
     */
    @Nullable
    WorldInfo getPlotsWorldInfo(Plot plot, String worldName);

    /**
     * Checks if the given world is an plots world.
     * Can be the normal world, the nether world (if enabled in config) or the end world (if enabled in config)
     */
    boolean isPlotsWorld(World world);

    /**
     * Register a world as a plots world.
     * This will add all protections to that world, however - no plots will by physically there.
     *
     * @param world The world to register as an plots world.
     */
    void registerPlotWorld(World world);

    /**
     * Get all registered worlds.
     */
    List<World> getRegisteredWorlds();

    /**
     * Get all the plots ordered by a specific sorting type.
     *
     * @param sortingType The sorting type to order the list by.
     * @return A list of uuids of the plot owners.
     * @deprecated See {@link #getPlots(SortingType)}
     */
    @Deprecated
    List<UUID> getAllPlots(SortingType sortingType);

    /**
     * Get all the plots unordered.
     */
    List<Plot> getPlots();

    /**
     * Get all the plots ordered by a specific sorting type.
     *
     * @param sortingType The sorting type to order the list by.
     * @return A list of uuids of the plot owners.
     */
    List<Plot> getPlots(SortingType sortingType);

    /**
     * Get the block amount of a specific block.
     *
     * @param block The block to check.
     * @deprecated see {@link StackedBlocksManager}
     */
    @Deprecated
    int getBlockAmount(Block block);

    /**
     * Get the block amount of a specific location.
     *
     * @param location The location to check.
     * @deprecated see {@link StackedBlocksManager}
     */
    @Deprecated
    int getBlockAmount(Location location);

    /**
     * Set a new amount for a specific block.
     *
     * @param block  The block to set the amount to.
     * @param amount The new amount of the block.
     * @deprecated see {@link StackedBlocksManager}
     */
    @Deprecated
    void setBlockAmount(Block block, int amount);

    /**
     * Get all the stacked blocks on the server.
     *
     * @deprecated see {@link StackedBlocksManager}
     */
    @Deprecated
    List<Location> getStackedBlocks();

    /**
     * Calculate the worth of all the plots on the server.
     */
    void calcAllPlots();

    /**
     * Calculate the worth of all the plots on the server.
     *
     * @param callback Runnable that will be ran when process is finished.
     */
    void calcAllPlots(@Nullable Runnable callback);

    /**
     * Make the plot to be deleted when server stops.
     *
     * @param plot The plot to delete.
     */
    void addPlotToPurge(Plot plot);

    /**
     * Remove the plot from being deleted when server stops.
     *
     * @param plot The plot to keep.
     */
    void removePlotFromPurge(Plot plot);

    /**
     * Check if the plot will be deleted when the server stops?
     */
    boolean isPlotPurge(Plot plot);

    /**
     * Get all the plots that will be deleted when the server stops.
     */
    List<Plot> getPlotsToPurge();

    /**
     * Add a new sorting type to the registry of plots.
     *
     * @param sortingType The new sorting type to register.
     */
    void registerSortingType(SortingType sortingType);

    /**
     * Get the total worth of all the plots.
     * This value is updated every minute, so it might not be 100% accurate.
     */
    BigDecimal getTotalWorth();

    /**
     * Get the total level of all the plots.
     * This value is updated every minute, so it might not be 100% accurate.
     */
    BigDecimal getTotalLevel();

    /**
     * Get the location of the last plot that was generated.
     */
    Location getLastPlotLocation();

    /**
     * Set the location of the last plot.
     * Warning: Do not use this method unless you know what you're doing
     *
     * @param location The location to set.
     */
    void setLastPlotLocation(Location location);

    /**
     * Get the plots container.
     */
    PlotsContainer getPlotsContainer();

}

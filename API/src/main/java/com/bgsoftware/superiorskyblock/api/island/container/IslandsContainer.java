package com.bgsoftware.superiorskyblock.api.plot.container;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public interface PlotsContainer {

    /**
     * Add an plot to the plots container.
     *
     * @param plot The plot to add.
     */
    void addPlot(Plot plot);

    /**
     * Remove an plot from the plots container.
     *
     * @param plot The plot to remove.
     */
    void removePlot(Plot plot);

    /**
     * Get an plot by its uuid.
     *
     * @param uuid The uuid of the plot.
     */
    @Nullable
    Plot getPlotByUUID(UUID uuid);

    /**
     * Get an plot by its leader's uuid.
     *
     * @param uuid The uuid of the plot's leader.
     * @deprecated Not supported anymore.
     */
    @Nullable
    @Deprecated
    default Plot getPlotByLeader(UUID uuid) {
        return SuperiorSkyblockAPI.getGrid().getPlot(uuid);
    }

    /**
     * Get an plot by its position in the top-plots.
     *
     * @param position    The position of the plot.
     * @param sortingType The sorting-type to get plots from.
     */
    @Nullable
    Plot getPlotAtPosition(int position, SortingType sortingType);

    /**
     * Get the position of an plot in the top-plots.
     *
     * @param plot      The plot to get position of.
     * @param sortingType The sorting-type to get plots from.
     */
    int getPlotPosition(Plot plot, SortingType sortingType);

    /**
     * Get the amount of plots on the server.
     */
    int getPlotsAmount();

    /**
     * Get an plot at a location.
     *
     * @param location The location to get plot in.
     */
    @Nullable
    Plot getPlotAt(Location location);

    /**
     * Transfer an plot from a player to another one.
     * Warning: If you don't know what you're doing, do not use this method.
     * Instead, use {@link Plot#transferPlot(SuperiorPlayer)}.
     *
     * @param oldLeader The uuid of the current leader.
     * @param newLeader The uuid of the new leader.
     * @deprecated Not supported anymore.
     */
    @Deprecated
    default void transferPlot(UUID oldLeader, UUID newLeader) {

    }

    /**
     * Sort plots for the top-plots.
     * The plots will not get sorted if only one plot exists, or no changes
     * were tracked by {@link #notifyChange(SortingType, Plot)}
     *
     * @param sortingType The type of sorting to use.
     * @param onFinish    Callback method
     */
    void sortPlots(SortingType sortingType, @Nullable Runnable onFinish);

    /**
     * Sort plots for the top-plots.
     *
     * @param sortingType The type of sorting to use.
     * @param forceSort   Whether to force-sort the plots.
     *                    When true, plots will get sorted even if only one plot exists.
     * @param onFinish    Callback method
     */
    void sortPlots(SortingType sortingType, boolean forceSort, @Nullable Runnable onFinish);

    /**
     * Notify about a change of a value for a specific sorting type for an plot.
     *
     * @param sortingType The sorting-type.
     * @param plot      The plot that had its value changed.
     */
    void notifyChange(SortingType sortingType, Plot plot);

    /**
     * Get all plots sorted by a specific sorting-type.
     *
     * @param sortingType The type of sorting to use.
     */
    List<Plot> getSortedPlots(SortingType sortingType);

    /**
     * Get all plots.
     */
    List<Plot> getPlotsUnsorted();

    /**
     * Add a new sorting-type.
     *
     * @param sortingType The sorting-type to add.
     * @param sort        Whether to sort the plots or not when the sorting-type is added.
     */
    void addSortingType(SortingType sortingType, boolean sort);

}

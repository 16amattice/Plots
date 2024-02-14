package com.bgsoftware.superiorskyblock.plot.container;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.container.PlotsContainer;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.core.PlotPosition;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.collections.EnumerateSet;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.core.threads.Synchronized;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DefaultPlotsContainer implements PlotsContainer {

    private final Map<PlotPosition, Plot> plotsByPositions = new ConcurrentHashMap<>();
    private final Map<UUID, Plot> plotsByUUID = new ConcurrentHashMap<>();

    private final Map<SortingType, Synchronized<List<Plot>>> sortedPlots = new ConcurrentHashMap<>();

    private final EnumerateSet<SortingType> notifiedValues = new EnumerateSet<>(SortingType.values());

    private final SuperiorSkyblockPlugin plugin;

    public DefaultPlotsContainer(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;

    }

    @Override
    public void addPlot(Plot plot) {
        BlockPosition center = plot.getCenterPosition();
        WorldInfo defaultWorld = plugin.getGrid().getPlotsWorldInfo(plot, plugin.getSettings().getWorlds().getDefaultWorld());

        Preconditions.checkNotNull(defaultWorld, "Default world information cannot be null!");

        this.plotsByPositions.put(PlotPosition.of(defaultWorld.getName(), center.getX(), center.getZ()), plot);

        if (plugin.getProviders().hasCustomWorldsSupport()) {
            // We don't know the logic of the custom worlds support, therefore we add a position
            // for every possible world, so there won't be issues with detecting plots later.
            if (plugin.getProviders().getWorldsProvider().isNormalEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.NORMAL,
                        plotPosition -> this.plotsByPositions.put(plotPosition, plot));
            }
            if (plugin.getProviders().getWorldsProvider().isNetherEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.NETHER,
                        plotPosition -> this.plotsByPositions.put(plotPosition, plot));
            }
            if (plugin.getProviders().getWorldsProvider().isEndEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.THE_END,
                        plotPosition -> this.plotsByPositions.put(plotPosition, plot));
            }
        }

        this.plotsByUUID.put(plot.getUniqueId(), plot);

        sortedPlots.values().forEach(sortedPlots -> {
            sortedPlots.write(_sortedPlots -> _sortedPlots.add(plot));
        });
    }

    @Override
    public void removePlot(Plot plot) {
        BlockPosition center = plot.getCenterPosition();
        WorldInfo defaultWorld = plugin.getGrid().getPlotsWorldInfo(plot, plugin.getSettings().getWorlds().getDefaultWorld());

        Preconditions.checkNotNull(defaultWorld, "Default world information cannot be null!");

        this.plotsByPositions.remove(PlotPosition.of(defaultWorld.getName(), center.getX(), center.getZ()), plot);

        if (plugin.getProviders().hasCustomWorldsSupport()) {
            if (plugin.getProviders().getWorldsProvider().isNormalEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.NORMAL,
                        plotPosition -> this.plotsByPositions.remove(plotPosition, plot));
            }
            if (plugin.getProviders().getWorldsProvider().isNetherEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.NETHER,
                        plotPosition -> this.plotsByPositions.remove(plotPosition, plot));
            }
            if (plugin.getProviders().getWorldsProvider().isEndEnabled()) {
                runWithCustomWorld(defaultWorld, center, plot, World.Environment.THE_END,
                        plotPosition -> this.plotsByPositions.remove(plotPosition, plot));
            }
        }

        plotsByUUID.remove(plot.getUniqueId());

        sortedPlots.values().forEach(sortedPlots -> {
            sortedPlots.write(_sortedPlots -> _sortedPlots.remove(plot));
        });
    }

    @Nullable
    @Override
    public Plot getPlotByUUID(UUID uuid) {
        return this.plotsByUUID.get(uuid);
    }

    @Nullable
    @Override
    public Plot getPlotAtPosition(int position, SortingType sortingType) {
        ensureSortingType(sortingType);
        return this.sortedPlots.get(sortingType).readAndGet(sortedPlots -> {
            return position < 0 || position >= sortedPlots.size() ? null : sortedPlots.get(position);
        });
    }

    @Override
    public int getPlotPosition(Plot plot, SortingType sortingType) {
        ensureSortingType(sortingType);
        return this.sortedPlots.get(sortingType).readAndGet(sortedPlots -> {
            return sortedPlots.indexOf(plot);
        });
    }

    @Override
    public int getPlotsAmount() {
        return this.plotsByUUID.size();
    }

    @Nullable
    @Override
    public Plot getPlotAt(Location location) {
        Plot plot = this.plotsByPositions.get(PlotPosition.of(location));
        return plot == null || !plot.isInside(location) ? null : plot;
    }

    @Override
    public void sortPlots(SortingType sortingType, Runnable onFinish) {
        this.sortPlots(sortingType, false, onFinish);
    }

    @Override
    public void sortPlots(SortingType sortingType, boolean forceSort, Runnable onFinish) {
        ensureSortingType(sortingType);

        Synchronized<List<Plot>> sortedPlots = this.sortedPlots.get(sortingType);

        if (!forceSort && (sortedPlots.readAndGet(List::size) <= 1 || !notifiedValues.remove(sortingType))) {
            if (onFinish != null)
                onFinish.run();
            return;
        }

        if (Bukkit.isPrimaryThread()) {
            BukkitExecutor.async(() -> sortPlotsInternal(sortingType, onFinish));
        } else {
            sortPlotsInternal(sortingType, onFinish);
        }
    }

    @Override
    public void notifyChange(SortingType sortingType, Plot plot) {
        notifiedValues.add(sortingType);
    }

    @Override
    public List<Plot> getSortedPlots(SortingType sortingType) {
        ensureSortingType(sortingType);
        return this.sortedPlots.get(sortingType).readAndGet(sortedPlots ->
                new SequentialListBuilder<Plot>().build(sortedPlots));
    }

    @Override
    public List<Plot> getPlotsUnsorted() {
        return new SequentialListBuilder<Plot>().build(this.plotsByUUID.values());
    }

    @Override
    public void addSortingType(SortingType sortingType, boolean sort) {
        Preconditions.checkArgument(!sortedPlots.containsKey(sortingType), "You cannot register an existing sorting type to the database.");
        sortPlotsInternal(sortingType, null);
    }

    private void ensureSortingType(SortingType sortingType) {
        Preconditions.checkState(sortedPlots.containsKey(sortingType), "The sorting-type " + sortingType + " doesn't exist in the database. Please contact author!");
    }

    private void sortPlotsInternal(SortingType sortingType, Runnable onFinish) {
        List<Plot> newPlotsList = new ArrayList<>(plotsByUUID.values());
        newPlotsList.removeIf(Plot::isIgnored);

        newPlotsList.sort(sortingType);

        this.sortedPlots.put(sortingType, Synchronized.of(newPlotsList));

        if (onFinish != null)
            onFinish.run();
    }

    private void runWithCustomWorld(WorldInfo defaultWorld, BlockPosition center, Plot plot,
                                    World.Environment environment, Consumer<PlotPosition> consumer) {
        WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(plot, environment);
        if (worldInfo != null && !worldInfo.equals(defaultWorld))
            consumer.accept(PlotPosition.of(worldInfo.getName(), center.getX(), center.getZ()));
    }

}

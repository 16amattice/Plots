package com.bgsoftware.superiorskyblock.api.plot.container;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class DelegatePlotsContainer implements PlotsContainer {

    protected final PlotsContainer handle;

    protected DelegatePlotsContainer(PlotsContainer handle) {
        this.handle = handle;
    }

    @Override
    public void addPlot(Plot plot) {
        this.handle.addPlot(plot);
    }

    @Override
    public void removePlot(Plot plot) {
        this.handle.removePlot(plot);
    }

    @Nullable
    @Override
    public Plot getPlotByUUID(UUID uuid) {
        return this.handle.getPlotByUUID(uuid);
    }

    @Nullable
    @Override
    @Deprecated
    public Plot getPlotByLeader(UUID uuid) {
        return this.handle.getPlotByLeader(uuid);
    }

    @Nullable
    @Override
    public Plot getPlotAtPosition(int position, SortingType sortingType) {
        return this.handle.getPlotAtPosition(position, sortingType);
    }

    @Override
    public int getPlotPosition(Plot plot, SortingType sortingType) {
        return this.handle.getPlotPosition(plot, sortingType);
    }

    @Override
    public int getPlotsAmount() {
        return this.handle.getPlotsAmount();
    }

    @Nullable
    @Override
    public Plot getPlotAt(Location location) {
        return this.handle.getPlotAt(location);
    }

    @Override
    @Deprecated
    public void transferPlot(UUID oldLeader, UUID newLeader) {
        this.handle.transferPlot(oldLeader, newLeader);
    }

    @Override
    public void sortPlots(SortingType sortingType, @Nullable Runnable onFinish) {
        this.handle.sortPlots(sortingType, onFinish);
    }

    @Override
    public void sortPlots(SortingType sortingType, boolean forceSort, @Nullable Runnable onFinish) {
        this.handle.sortPlots(sortingType, forceSort, onFinish);
    }

    @Override
    public void notifyChange(SortingType sortingType, Plot plot) {
        this.handle.notifyChange(sortingType, plot);
    }

    @Override
    public List<Plot> getSortedPlots(SortingType sortingType) {
        return this.handle.getSortedPlots(sortingType);
    }

    @Override
    public List<Plot> getPlotsUnsorted() {
        return this.handle.getPlotsUnsorted();
    }

    @Override
    public void addSortingType(SortingType sortingType, boolean sort) {
        this.handle.addSortingType(sortingType, sort);
    }

}

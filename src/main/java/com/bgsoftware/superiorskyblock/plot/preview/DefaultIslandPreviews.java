package com.bgsoftware.superiorskyblock.plot.preview;

import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPlotPreviews implements PlotPreviews {

    private final Map<UUID, PlotPreview> plotPreviews = new ConcurrentHashMap<>();

    @Override
    public void startPlotPreview(PlotPreview plotPreview) {
        this.plotPreviews.put(plotPreview.getPlayer().getUniqueId(), plotPreview);
    }

    @Override
    public PlotPreview endPlotPreview(SuperiorPlayer superiorPlayer) {
        return this.plotPreviews.remove(superiorPlayer.getUniqueId());
    }

    @Override
    public PlotPreview getPlotPreview(SuperiorPlayer superiorPlayer) {
        return this.plotPreviews.get(superiorPlayer.getUniqueId());
    }

    @Override
    public List<PlotPreview> getActivePreviews() {
        return new SequentialListBuilder<PlotPreview>().build(this.plotPreviews.values());
    }

}

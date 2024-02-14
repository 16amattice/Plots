package com.bgsoftware.superiorskyblock.plot.preview;

import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.List;

public interface PlotPreviews {

    void startPlotPreview(PlotPreview plotPreview);

    PlotPreview endPlotPreview(SuperiorPlayer superiorPlayer);

    PlotPreview getPlotPreview(SuperiorPlayer superiorPlayer);

    List<PlotPreview> getActivePreviews();

}

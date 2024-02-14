package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;

/**
 * Object that handles the data of the plot preview task.
 */
public interface PlotPreview {

    /**
     * Get the player that is inside the preview.
     */
    SuperiorPlayer getPlayer();

    /**
     * Get the location of the plot preview.
     */
    Location getLocation();

    /**
     * Get the requested schematic.
     */
    String getSchematic();

    /**
     * Get the plot name that was requested.
     */
    String getPlotName();

    /**
     * Handle confirmation of creation of the plot.
     */
    void handleConfirm();

    /**
     * Handle cancellation of the creation of the plot.
     */
    void handleCancel();

    /**
     * Handle escaping from the area of the preview.
     */
    void handleEscape();

}

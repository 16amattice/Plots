package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Location;

/**
 * PlotSchematicPasteEvent is called when a schematic is placed.
 */
public class PlotSchematicPasteEvent extends PlotEvent {

    private final String schematic;
    private final Location location;

    /**
     * The constructor for the event.
     *
     * @param plot    The plot object that was created.
     * @param schematic The schematic that was used.
     * @param location  The location the schematic was pasted at.
     */
    public PlotSchematicPasteEvent(Plot plot, String schematic, Location location) {
        super(plot);
        this.schematic = schematic;
        this.location = location.clone();
    }

    /**
     * Get the schematic that was used.
     */
    public String getSchematic() {
        return schematic;
    }

    /**
     * Get the location that the schematic was pasted at.
     */
    public Location getLocation() {
        return location;
    }
}

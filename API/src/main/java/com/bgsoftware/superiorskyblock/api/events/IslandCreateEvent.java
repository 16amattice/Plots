package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotCreateEvent is called when a new plot is created.
 */
public class PlotCreateEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final String schematic;
    private boolean teleport = true;
    private boolean cancelled = false;

    /**
     * The constructor for the event.
     *
     * @param superiorPlayer The player who created the plot.
     * @param plot         The plot object that was created.
     * @deprecated See PlotCreateEvent(SuperiorPlayer, Plot, String)
     */
    @Deprecated
    public PlotCreateEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        this(superiorPlayer, plot, "");
    }

    /**
     * The constructor for the event.
     *
     * @param superiorPlayer The player who created the plot.
     * @param plot         The plot object that was created.
     * @param schematic      The schematic that was used.
     */
    public PlotCreateEvent(SuperiorPlayer superiorPlayer, Plot plot, String schematic) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.schematic = schematic;
    }

    /**
     * Get the player who created the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the schematic that was used.
     */
    public String getSchematic() {
        return schematic;
    }

    /**
     * Check if the player should get teleported when the process finishes.
     */
    public boolean canTeleport() {
        return teleport;
    }

    /**
     * Set whether or not the player should be teleported to the plot when the process finishes.
     */
    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

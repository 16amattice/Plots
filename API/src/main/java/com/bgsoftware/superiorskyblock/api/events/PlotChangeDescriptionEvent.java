package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeDescriptionEvent is called when an plot changes its description.
 */
public class PlotChangeDescriptionEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private String description;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot         The plot that its description was changed.
     * @param superiorPlayer The player that changed the description of the plot.
     * @param description    The new description of the plot.
     */
    public PlotChangeDescriptionEvent(Plot plot, SuperiorPlayer superiorPlayer, String description) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.description = description;
    }

    /**
     * Get the player that changed the description of the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new description of the plot.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the new description of the plot.
     *
     * @param description The new description to set.
     */
    public void setPlotName(String description) {
        Preconditions.checkNotNull(description, "Plot descriptions cannot be null.");
        this.description = description;
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

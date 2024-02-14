package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

/**
 * PlotSetVisitorHomeEvent is called when a new visitor home is set to the plot.
 */
public class PlotSetVisitorHomeEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;

    private Location plotVisitorHome;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer    The player that changed the plot visitor home.
     * @param plot            The plot that the visitor home was changed for.
     * @param plotVisitorHome The new plot visitor home of the plot.
     */
    public PlotSetVisitorHomeEvent(SuperiorPlayer superiorPlayer, Plot plot, Location plotVisitorHome) {
        super(plot);
        this.plotVisitorHome = plotVisitorHome.clone();
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player who changed the plot home, if exists.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new plot visitor home location of the plot.
     */
    public Location getPlotVisitorHome() {
        return plotVisitorHome.clone();
    }

    /**
     * Set the new visitor home location of the plot.
     * Setting the visitor home location outside the plot's area may lead to undefined behaviors.
     *
     * @param plotVisitorHome The new home visitor location for the plot.
     */
    public void setPlotHome(Location plotVisitorHome) {
        Preconditions.checkNotNull(plotVisitorHome.getWorld(), "Cannot set plot visitor home with null world");
        this.plotVisitorHome = plotVisitorHome.clone();
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

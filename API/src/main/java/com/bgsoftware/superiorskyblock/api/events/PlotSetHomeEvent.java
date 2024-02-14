package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;


/**
 * PlotSetHomeEvent is called when a new home is set to the plot.
 */
public class PlotSetHomeEvent extends PlotEvent implements Cancellable {

    private final Reason reason;
    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private Location plotHome;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot         The plot that the home was changed for.
     * @param plotHome     The new plot home of the plot.
     * @param reason         The reason the home was changed.
     * @param superiorPlayer The player that changed the plot home, if exists
     */
    public PlotSetHomeEvent(Plot plot, Location plotHome, Reason reason, @Nullable SuperiorPlayer superiorPlayer) {
        super(plot);
        this.plotHome = plotHome.clone();
        this.reason = reason;
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the new plot home location of the plot.
     */
    public Location getPlotHome() {
        return plotHome.clone();
    }

    /**
     * Set the new home location of the plot.
     *
     * @param plotHome The home location for the plot.
     */
    public void setPlotHome(Location plotHome) {
        Preconditions.checkNotNull(plotHome.getWorld(), "Cannot set plot home with null world");
        this.plotHome = plotHome.clone();
    }

    /**
     * Get the reason the home was changed.
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Get the player who changed the plot home, if exists.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * The reason the home was changed.
     */
    public enum Reason {

        /**
         * The home was changed through a command.
         */
        SET_HOME_COMMAND,

        /**
         * The home was changed because the old home was not safe.
         */
        SAFE_HOME

    }

}

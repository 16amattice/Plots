package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotRenameEvent is called when an plot changes its name.
 */
public class PlotRenameEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private String plotName;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot         The plot that was renamed.
     * @param superiorPlayer The player that renamed the plot.
     *                       If null, the plot was renamed by console.
     * @param plotName     The new name of the plot.
     */
    public PlotRenameEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer, String plotName) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotName = plotName;
    }

    /**
     * Get the player that changed the privilege to the other player.
     * If null, the privilege was changed by the console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new name of the plot.
     */
    public String getPlotName() {
        return plotName;
    }

    /**
     * Set the new name of the plot.
     *
     * @param plotName The new name to set.
     */
    public void setPlotName(String plotName) {
        Preconditions.checkNotNull(plotName, "Plot names cannot be null.");
        this.plotName = plotName;
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

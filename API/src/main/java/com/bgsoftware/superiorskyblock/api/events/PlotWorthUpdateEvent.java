package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;

import java.math.BigDecimal;

/**
 * PlotWorthUpdateEvent is called when the worth of the plot is updated.
 */
public class PlotWorthUpdateEvent extends PlotEvent {

    private final BigDecimal oldWorth;
    private final BigDecimal oldLevel;
    private final BigDecimal newWorth;
    private final BigDecimal newLevel;

    /**
     * The constructor of the event.
     *
     * @param plot   The plot that the leadership of it is transferred.
     * @param oldWorth The old worth of the plot.
     * @param oldLevel The old level of the plot.
     * @param newWorth The new worth of the plot.
     * @param newLevel The new level of the plot.
     */
    public PlotWorthUpdateEvent(Plot plot, BigDecimal oldWorth, BigDecimal oldLevel, BigDecimal newWorth, BigDecimal newLevel) {
        super(plot);
        this.oldWorth = oldWorth;
        this.oldLevel = oldLevel;
        this.newWorth = newWorth;
        this.newLevel = newLevel;
    }

    /**
     * Get the old worth of the plot.
     */
    public BigDecimal getOldWorth() {
        return oldWorth;
    }

    /**
     * Get the old level of the plot.
     */
    public BigDecimal getOldLevel() {
        return oldLevel;
    }

    /**
     * Get the new worth of the plot.
     */
    public BigDecimal getNewWorth() {
        return newWorth;
    }

    /**
     * Get the new level of the plot.
     */
    public BigDecimal getNewLevel() {
        return newLevel;
    }
}

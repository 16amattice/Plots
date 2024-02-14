package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeMobDropsEvent is called when the mob-drops multiplier of the plot is changed.
 */
public class PlotChangeMobDropsEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private double mobDrops;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the mob-drops multiplier of the plot.
     *                       If set to null, it means the mob-drops multiplier was changed via the console.
     * @param plot         The plot that the mob-drops multiplier was changed for.
     * @param mobDrops       The new mob drops of the plot
     */
    public PlotChangeMobDropsEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, double mobDrops) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.mobDrops = mobDrops;
    }

    /**
     * Get the player that changed the mob-drops multiplier.
     * If null, it means the mob-drops multiplier was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new mob-drops multiplier of the plot.
     */
    public double getMobDrops() {
        return mobDrops;
    }

    /**
     * Set the new mob-drops multiplier for the plot.
     *
     * @param mobDrops The mob-drops multiplier to set.
     */
    public void setMobDrops(double mobDrops) {
        Preconditions.checkArgument(mobDrops >= 0, "Cannot set the mob-drops to a negative multiplier.");
        this.mobDrops = mobDrops;
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

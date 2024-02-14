package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;


/**
 * PlotChangeSpawnerRatesEvent is called when the spawner-rates multiplier of the plot is changed.
 */
public class PlotChangeSpawnerRatesEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private double spawnerRates;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the spawner-rates multiplier of the plot.
     *                       If set to null, it means the spawner-rates multiplier was changed via the console.
     * @param plot         The plot that the spawner-rates multiplier was changed for.
     * @param spawnerRates   The new spawner-rates multiplier of the plot
     */
    public PlotChangeSpawnerRatesEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, double spawnerRates) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.spawnerRates = spawnerRates;
    }

    /**
     * Get the player that changed the spawner-rates multiplier.
     * If null, it means the spawner-rates multiplier was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new spawner-rates multiplier of the plot.
     */
    public double getSpawnerRates() {
        return spawnerRates;
    }

    /**
     * Set the new spawner-rates multiplier for the plot.
     *
     * @param spawnerRates The spawner-rates multiplier to set.
     */
    public void setSpawnerRates(double spawnerRates) {
        Preconditions.checkArgument(spawnerRates >= 0, "Cannot set the spawner rate to a negative multiplier.");
        this.spawnerRates = spawnerRates;
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

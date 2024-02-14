package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.block.Biome;
import org.bukkit.event.Cancellable;

/**
 * PlotCreateEvent is called when a new plot is created.
 */
public class PlotBiomeChangeEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private Biome biome;
    private boolean cancelled = false;

    /**
     * The constructor for the event.
     *
     * @param superiorPlayer The player who changed the biome of the plot.
     * @param plot         The plot object that was changed.
     * @param biome          The name of the new biome.
     */
    public PlotBiomeChangeEvent(SuperiorPlayer superiorPlayer, Plot plot, Biome biome) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.biome = biome;
    }

    /**
     * Get the player who upgraded the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new biome.
     */
    public Biome getBiome() {
        return biome;
    }

    /**
     * Set the new biome.
     */
    public void setBiome(Biome biome) {
        this.biome = biome;
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

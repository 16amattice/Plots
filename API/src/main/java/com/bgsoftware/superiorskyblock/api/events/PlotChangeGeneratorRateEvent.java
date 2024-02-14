package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.World;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeGeneratorRateEvent is called when a generator-rate of an plot is changed.
 */
public class PlotChangeGeneratorRateEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final Key block;
    private final World.Environment environment;

    private int generatorRate;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the generator-rate of an plot.
     *                       If set to null, it means the rate was changed via the console.
     * @param plot         The plot that the generator-rate was changed for.
     * @param block          The block that the rate was changed for.
     * @param environment    The environment of the world that the rate was changed for.
     * @param generatorRate  The new generator-rate of the block.
     */
    public PlotChangeGeneratorRateEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, Key block,
                                          World.Environment environment, int generatorRate) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.block = block;
        this.environment = environment;
        this.generatorRate = generatorRate;
    }

    /**
     * Get the player that changed the generator-rate.
     * If null, it means the level was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the block that the generator-rate was changed for.
     */
    public Key getBlock() {
        return block;
    }

    /**
     * Get the environment of the world that the rate was changed for.
     */
    public World.Environment getEnvironment() {
        return environment;
    }

    /**
     * Get the new generator-rate of the block.
     */
    public int getGeneratorRate() {
        return generatorRate;
    }

    /**
     * Set the new generator-rate of the block.
     *
     * @param generatorRate The new generator-rate to set.
     */
    public void setGeneratorRate(int generatorRate) {
        Preconditions.checkArgument(generatorRate >= 0, "Cannot set the generator-rate to a negative rate.");
        this.generatorRate = generatorRate;
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

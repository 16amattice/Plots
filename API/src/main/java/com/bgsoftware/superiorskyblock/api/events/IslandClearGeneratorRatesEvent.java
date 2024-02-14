package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.World;
import org.bukkit.event.Cancellable;


/**
 * PlotClearGeneratorRatesEvent is called when clearing generator-rates of an plot.
 */
public class PlotClearGeneratorRatesEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final World.Environment environment;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that cleared the generator-rates of an plot.
     *                       If set to null, it means the rates were cleared via the console.
     * @param plot         The plot that the generator-rates were cleared for.
     * @param environment    The environment of the world that the rates were cleared for.
     */
    public PlotClearGeneratorRatesEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, World.Environment environment) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.environment = environment;
    }

    /**
     * Get the player that cleared the generator-rates.
     * If null, it means the rates were cleared by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the environment of the world that the rates were cleared for.
     */
    public World.Environment getEnvironment() {
        return environment;
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

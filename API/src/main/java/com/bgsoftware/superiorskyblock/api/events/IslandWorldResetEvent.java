package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.World;
import org.bukkit.event.Cancellable;

/**
 * PlotWorldResetEvent is called when a world is reset of an plot.
 */
public class PlotWorldResetEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final World.Environment environment;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that reset the world of the plot.
     *                       If null, the world was reset by console.
     * @param plot         The plot that the world was reset for.
     * @param environment    The environment of the world that was reset.
     */
    public PlotWorldResetEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, World.Environment environment) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.environment = environment;
    }

    /**
     * Get the player that reset the world of the plot.
     * If null, the world was reset by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the environment of the world that was reset.
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

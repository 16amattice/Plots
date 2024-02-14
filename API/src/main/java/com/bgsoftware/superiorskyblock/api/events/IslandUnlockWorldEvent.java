package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.World;
import org.bukkit.event.Cancellable;

/**
 * PlotUnlockWorldEvent is called when a world is unlocked to an plot.
 */
public class PlotUnlockWorldEvent extends PlotEvent implements Cancellable {

    private final World.Environment environment;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot      The plot that the world was unlocked for.
     * @param environment The environment of the world that is unlocked.
     */
    public PlotUnlockWorldEvent(Plot plot, World.Environment environment) {
        super(plot);
        this.environment = environment;
    }

    /**
     * Get the environment of the world that is being unlocked.
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

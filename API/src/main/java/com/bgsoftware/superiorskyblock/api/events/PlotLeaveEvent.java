package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

/**
 * PlotLeaveEvent is called when a player is walking out from the plot's area.
 */
public class PlotLeaveEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final LeaveCause leaveCause;
    @Nullable
    private final Location toLocation;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who left the plot's area.
     * @param plot         The plot that the player left.
     * @param leaveCause     The cause of leaving the plot.
     * @param toLocation     The location the player will be at after leaving.
     */
    public PlotLeaveEvent(SuperiorPlayer superiorPlayer, Plot plot, LeaveCause leaveCause, @Nullable Location toLocation) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.leaveCause = leaveCause;
        this.toLocation = toLocation;
    }

    /**
     * Get the player who left the plot's area.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the cause of leaving the plot.
     */
    public LeaveCause getCause() {
        return leaveCause;
    }

    /**
     * Get the location the player will be after he's leaving.
     * If the location is null, it means the player left the game.
     */
    @Nullable
    public Location getTo() {
        return toLocation;
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
     * Used to determine the cause of leaving.
     */
    public enum LeaveCause {

        PLAYER_MOVE,
        PLAYER_TELEPORT,
        PLAYER_QUIT,
        INVALID

    }

}

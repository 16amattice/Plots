package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

/**
 * PlotEnterEvent is called when a player is walking into an plot's area.
 */
public class PlotEnterEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final EnterCause enterCause;
    private boolean cancelled = false;
    private Location cancelTeleport = null;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who entered to the plot's area.
     * @param plot         The plot that the player entered into.
     * @param enterCause     The cause of entering into the plot.
     */
    public PlotEnterEvent(SuperiorPlayer superiorPlayer, Plot plot, EnterCause enterCause) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.enterCause = enterCause;
    }

    /**
     * Get the player who entered to the plot's area.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the cause of entering into the plot.
     */
    public EnterCause getCause() {
        return enterCause;
    }

    /**
     * Get the location the player would be teleported if the event is cancelled.
     */
    public Location getCancelTeleport() {
        return cancelTeleport == null ? null : cancelTeleport.clone();
    }

    /**
     * Set the location the player would be teleported if the event is cancelled.
     */
    public void setCancelTeleport(Location cancelTeleport) {
        this.cancelTeleport = cancelTeleport.clone();
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
     * Used to determine the cause of entering.
     */
    public enum EnterCause {

        PLAYER_MOVE,
        PLAYER_TELEPORT,
        PLAYER_JOIN,
        PORTAL,
        INVALID

    }

}

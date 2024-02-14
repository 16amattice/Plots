package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotChangePlayerPrivilegeEvent is called when a privilege is changed for a player on an plot.
 */
public class PlotChangePlayerPrivilegeEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer privilegedPlayer;
    private final boolean privilegeEnabled;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot           The plot that the privilege was changed in.
     * @param superiorPlayer   The player that changed the privilege to the other player.
     * @param privilegedPlayer The player that the privilege was changed for.
     * @param privilegeEnabled Whether the privilege was enabled or disabled for the player.
     */
    public PlotChangePlayerPrivilegeEvent(Plot plot, SuperiorPlayer superiorPlayer, SuperiorPlayer privilegedPlayer, boolean privilegeEnabled) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.privilegedPlayer = privilegedPlayer;
        this.privilegeEnabled = privilegeEnabled;
    }

    /**
     * Get the player that changed the privilege to the other player.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player that the privilege was changed for.
     */
    public SuperiorPlayer getPrivilegedPlayer() {
        return privilegedPlayer;
    }

    /**
     * Check whether the privilege was enabled to {@link #getPrivilegedPlayer()}
     */
    public boolean isPrivilegeEnabled() {
        return privilegeEnabled;
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

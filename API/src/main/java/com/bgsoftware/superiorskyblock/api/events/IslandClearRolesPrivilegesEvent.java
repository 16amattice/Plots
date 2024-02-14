package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotClearRolesPrivilegesEvent is called when privileges of roles are cleared on an plot.
 */
public class PlotClearRolesPrivilegesEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot         The plot that the privileges were cleared in.
     * @param superiorPlayer The player that cleared the privileges.
     */
    public PlotClearRolesPrivilegesEvent(Plot plot, SuperiorPlayer superiorPlayer) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player that cleared the privileges to the other player.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
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

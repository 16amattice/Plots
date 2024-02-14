package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeRolePrivilegeEvent is called when a privilege is changed for a role on an plot.
 */
public class PlotChangeRolePrivilegeEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final PlayerRole playerRole;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot         The plot that the privilege was changed in.
     * @param superiorPlayer The player that changed the privilege to the other role.
     *                       If null, the privilege was changed by the console.
     * @param playerRole     The role that the privilege was changed for.
     */
    public PlotChangeRolePrivilegeEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer, PlayerRole playerRole) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.playerRole = playerRole;
    }

    /**
     * Get the player that changed the privilege to the other player.
     * If null, the privilege was changed by the console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the role that the privilege was changed for.
     */
    public PlayerRole getPlayerRole() {
        return playerRole;
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

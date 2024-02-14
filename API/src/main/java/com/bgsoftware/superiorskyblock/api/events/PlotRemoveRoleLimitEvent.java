package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;


/**
 * PlotRemoveRoleLimitEvent is called when a role-limit of an plot is removed.
 */
public class PlotRemoveRoleLimitEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final PlayerRole playerRole;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that removed a role-limit from an plot.
     *                       If set to null, it means the limit was removed via the console.
     * @param plot         The plot that the role-limit was removed from.
     * @param playerRole     The role that its limit was removed.
     */
    public PlotRemoveRoleLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlayerRole playerRole) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.playerRole = playerRole;
    }

    /**
     * Get the player that removed the role-limit.
     * If null, it means the limit was removed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the role that its limit was removed.
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

package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotRemoveRatingEvent is called when a rating of a player is removed from an plot.
 */
public class PlotRemoveRatingEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer ratingPlayer;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that removed the rating of the other player.
     *                       If null, the rating was removed by console.
     * @param ratingPlayer   The player that its rating was removed.
     * @param plot         The plot that was rated.
     */
    public PlotRemoveRatingEvent(@Nullable SuperiorPlayer superiorPlayer, SuperiorPlayer ratingPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.ratingPlayer = ratingPlayer;
    }

    /**
     * Get the player that removed the rating of the other player.
     * If null, the rating was removed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player that its rating was removed.
     */
    public SuperiorPlayer getRatingPlayer() {
        return ratingPlayer;
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

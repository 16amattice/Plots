package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotCreateWarpCategoryEvent is called when a new warp-category is created on an plot.
 */
public class PlotCreateWarpCategoryEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final String categoryName;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that created the warp-category.
     * @param plot         The plot that the warp-category was created on.
     * @param categoryName   The name of the new warp-category.
     */
    public PlotCreateWarpCategoryEvent(SuperiorPlayer superiorPlayer, Plot plot, String categoryName) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.categoryName = categoryName;
    }

    /**
     * Get the player that created the warp-category.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the name of the new warp-category.
     */
    public String getCategoryName() {
        return categoryName;
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

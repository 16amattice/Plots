package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotRenameWarpCategoryEvent is called when renaming a warp-category.
 */
public class PlotRenameWarpCategoryEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final WarpCategory warpCategory;

    private String categoryName;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that renamed the warp-category.
     * @param plot         The plot of the warp-category.
     * @param warpCategory   The warp-category that was renamed.
     * @param categoryName   The new name of the warp-category.
     */
    public PlotRenameWarpCategoryEvent(SuperiorPlayer superiorPlayer, Plot plot, WarpCategory warpCategory, String categoryName) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.warpCategory = warpCategory;
        this.categoryName = categoryName;
    }

    /**
     * Get the player that renamed the warp-category.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp-category that was renamed.
     */
    public WarpCategory getWarpCategory() {
        return warpCategory;
    }

    /**
     * Get the new name of the warp-category.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Set the new name for the warp-category.
     *
     * @param categoryName The new warp-category name to set.
     */
    public void setCategoryName(String categoryName) {
        Preconditions.checkNotNull(categoryName, "Cannot set warp-category name to null.");
        Preconditions.checkArgument(categoryName.length() <= 255, "Category names cannot be longer than 255 chars.");
        Preconditions.checkState(plot.getWarpCategory(categoryName) == null, "Cannot rename warp-categories to an already existing categories.");

        this.categoryName = categoryName;
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

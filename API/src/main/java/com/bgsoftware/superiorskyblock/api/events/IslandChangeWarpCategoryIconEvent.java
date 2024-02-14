package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * PlotChangeWarpCategoryIconEvent is called when the icon of a warp-category was changed.
 */
public class PlotChangeWarpCategoryIconEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final WarpCategory warpCategory;

    @Nullable
    private ItemStack icon;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the icon of the warp-category.
     * @param plot         The plot of the warp-category.
     * @param warpCategory   The warp-category that its icon was changed.
     * @param icon           The new icon of the warp-category.
     *                       If null, default icon will be set.
     */
    public PlotChangeWarpCategoryIconEvent(SuperiorPlayer superiorPlayer, Plot plot, WarpCategory warpCategory, @Nullable ItemStack icon) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.warpCategory = warpCategory;
        this.icon = icon == null ? null : icon.clone();
    }

    /**
     * Get the player that changed the icon of the warp-category.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp-category that its icon was changed.
     */
    public WarpCategory getWarpCategory() {
        return warpCategory;
    }

    /**
     * Get the new icon of the warp-category.
     */
    @Nullable
    public ItemStack getIcon() {
        return icon == null ? null : icon.clone();
    }

    /**
     * Set the new icon for the warp-category.
     * If set to null, default icon will be set.
     *
     * @param icon The new icon to set.
     */
    public void setIcon(@Nullable ItemStack icon) {
        this.icon = icon == null ? null : icon.clone();
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

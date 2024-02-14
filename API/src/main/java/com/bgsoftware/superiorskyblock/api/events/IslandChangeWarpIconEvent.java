package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * PlotChangeWarpIconEvent is called when the icon of a warp was changed.
 */
public class PlotChangeWarpIconEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final PlotWarp plotWarp;

    @Nullable
    private ItemStack icon;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the icon of the warp.
     * @param plot         The plot of the warp.
     * @param plotWarp     The warp that its icon was changed.
     * @param icon           The new icon of the warp.
     *                       If null, default icon will be set.
     */
    public PlotChangeWarpIconEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp, @Nullable ItemStack icon) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotWarp = plotWarp;
        this.icon = icon == null ? null : icon.clone();
    }

    /**
     * Get the player that changed the icon of the warp.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp that its icon was changed.
     */
    public PlotWarp getPlotWarp() {
        return plotWarp;
    }

    /**
     * Get the new icon of the warp.
     */
    @Nullable
    public ItemStack getIcon() {
        return icon == null ? null : icon.clone();
    }

    /**
     * Set the new icon for the warp.
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

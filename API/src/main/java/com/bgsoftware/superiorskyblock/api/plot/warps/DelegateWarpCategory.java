package com.bgsoftware.superiorskyblock.api.plot.warps;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DelegateWarpCategory implements WarpCategory {

    protected final WarpCategory handle;

    protected DelegateWarpCategory(WarpCategory handle) {
        this.handle = handle;
    }

    @Override
    public Plot getPlot() {
        return this.handle.getPlot();
    }

    @Override
    public String getName() {
        return this.handle.getName();
    }

    @Override
    public void setName(String name) {
        this.handle.setName(name);
    }

    @Override
    public List<PlotWarp> getWarps() {
        return this.handle.getWarps();
    }

    @Override
    public int getSlot() {
        return this.handle.getSlot();
    }

    @Override
    public void setSlot(int slot) {
        this.handle.setSlot(slot);
    }

    @Override
    public ItemStack getRawIcon() {
        return this.handle.getRawIcon();
    }

    @Override
    public ItemStack getIcon(@Nullable SuperiorPlayer superiorPlayer) {
        return this.handle.getIcon(superiorPlayer);
    }

    @Override
    public void setIcon(@Nullable ItemStack icon) {
        this.handle.setIcon(icon);
    }

}

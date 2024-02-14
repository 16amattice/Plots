package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DelegatePlotChest implements PlotChest {

    protected final PlotChest handle;

    protected DelegatePlotChest(PlotChest handle) {
        this.handle = handle;
    }

    @Override
    public Plot getPlot() {
        return this.handle.getPlot();
    }

    @Override
    public int getIndex() {
        return this.handle.getIndex();
    }

    @Override
    public int getRows() {
        return this.handle.getRows();
    }

    @Override
    public void setRows(int rows) {
        this.handle.setRows(rows);
    }

    @Override
    public ItemStack[] getContents() {
        return this.handle.getContents();
    }

    @Override
    public void openChest(SuperiorPlayer superiorPlayer) {
        this.handle.openChest(superiorPlayer);
    }

    @Override
    public Inventory getInventory() {
        return this.handle.getInventory();
    }

}

package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface PlotChest extends InventoryHolder {

    /**
     * Get the plot of the chest.
     */
    Plot getPlot();

    /**
     * Get the index of this chest.
     */
    int getIndex();

    /**
     * Get the amount of rows for this chest.
     */
    int getRows();

    /**
     * Set the amount of rows for this chest.
     *
     * @param rows The new amount of rows.
     */
    void setRows(int rows);

    /**
     * Get the contents of the chest.
     */
    ItemStack[] getContents();

    /**
     * Open this chest for a player.
     */
    void openChest(SuperiorPlayer superiorPlayer);

}

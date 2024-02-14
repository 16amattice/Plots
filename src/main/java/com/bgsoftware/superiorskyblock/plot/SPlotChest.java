package com.bgsoftware.superiorskyblock.plot;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlotsDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class SPlotChest implements PlotChest {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();
    private final AtomicBoolean updateFlag = new AtomicBoolean(false);
    private final Plot plot;
    private final int index;
    private Inventory inventory = Bukkit.createInventory(this, 9, plugin.getSettings().getPlotChests().getChestTitle());
    private int contentsUpdateCounter = 0;

    public SPlotChest(Plot plot, int index) {
        this.plot = plot;
        this.index = index;
    }

    public static SPlotChest createChest(Plot plot, int index, ItemStack[] contents) {
        SPlotChest plotChest = new SPlotChest(plot, index);
        plotChest.inventory = Bukkit.createInventory(plotChest, contents.length, plugin.getSettings().getPlotChests().getChestTitle());
        plotChest.inventory.setContents(contents);
        return plotChest;
    }

    @Override
    public Plot getPlot() {
        return plot;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getRows() {
        return inventory.getSize() / 9;
    }

    @Override
    public void setRows(int rows) {
        BukkitExecutor.ensureMain(() -> {
            try {
                updateFlag.set(true);
                ItemStack[] oldContents = inventory.getContents();
                Inventory oldInventory = inventory;
                inventory = Bukkit.createInventory(this, 9 * rows, plugin.getSettings().getPlotChests().getChestTitle());
                inventory.setContents(Arrays.copyOf(oldContents, 9 * rows));
                inventory.getViewers().forEach(humanEntity -> {
                    if (humanEntity.getOpenInventory().getTopInventory().equals(oldInventory))
                        humanEntity.openInventory(inventory);
                });
            } finally {
                updateFlag.set(false);
            }
        });
    }

    @Override
    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    @Override
    public void openChest(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        superiorPlayer.runIfOnline(player -> player.openInventory(getInventory()));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean isUpdating() {
        return updateFlag.get();
    }

    public void updateContents() {
        if (++contentsUpdateCounter >= 50) {
            contentsUpdateCounter = 0;
            PlotsDatabaseBridge.savePlotChest(plot, this);
        } else {
            PlotsDatabaseBridge.markPlotChestsToBeSaved(plot, this);
        }
    }

}

package com.bgsoftware.superiorskyblock.plot.warp;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlotsDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SWarpCategory implements WarpCategory {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();


    private final List<PlotWarp> plotWarps = new LinkedList<>();
    private final UUID plotUUID;
    private Plot cachedPlot;

    private String name;
    private int slot;
    private ItemStack icon;

    public SWarpCategory(UUID plotUUID, String name, int slot, @Nullable ItemStack icon) {
        this.plotUUID = plotUUID;
        this.name = name;
        this.slot = slot;
        this.icon = icon == null ? WarpIcons.DEFAULT_WARP_CATEGORY_ICON.build() : icon;
    }

    @Override
    public Plot getPlot() {
        return cachedPlot == null ? (cachedPlot = plugin.getGrid().getPlotByUUID(plotUUID)) : cachedPlot;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        Log.debug(Debug.SET_WARP_CATEGORY_NAME, getOwnerName(), this.name, name);

        String oldName = this.name;
        this.name = name;

        for (PlotWarp plotWarp : plotWarps)
            PlotsDatabaseBridge.updateWarpCategory(getPlot(), plotWarp, oldName);

        PlotsDatabaseBridge.updateWarpCategoryName(getPlot(), this, oldName);
    }

    @Override
    public List<PlotWarp> getWarps() {
        return plotWarps;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void setSlot(int slot) {
        Log.debug(Debug.SET_WARP_CATEGORY_SLOT, getOwnerName(), this.name, slot);

        this.slot = slot;

        PlotsDatabaseBridge.updateWarpCategorySlot(getPlot(), this);
    }

    @Override
    public ItemStack getRawIcon() {
        return icon.clone();
    }

    @Override
    public ItemStack getIcon(@Nullable SuperiorPlayer superiorPlayer) {
        ItemBuilder itemBuilder = new ItemBuilder(icon)
                .replaceAll("{0}", name);
        return superiorPlayer == null ? itemBuilder.build() : itemBuilder.build(superiorPlayer);
    }

    @Override
    public void setIcon(@Nullable ItemStack icon) {
        Log.debug(Debug.SET_WARP_CATEGORY_ICON, getOwnerName(), this.name, icon);

        this.icon = icon == null ? WarpIcons.DEFAULT_WARP_CATEGORY_ICON.build() : icon.clone();

        PlotsDatabaseBridge.updateWarpCategoryIcon(getPlot(), this);
    }

    private String getOwnerName() {
        SuperiorPlayer superiorPlayer = getPlot().getOwner();
        return superiorPlayer == null ? "None" : superiorPlayer.getName();
    }

}

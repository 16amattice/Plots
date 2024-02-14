package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;

public class DelegatePlotPreview implements PlotPreview {

    protected final PlotPreview handle;

    protected DelegatePlotPreview(PlotPreview handle) {
        this.handle = handle;
    }

    @Override
    public SuperiorPlayer getPlayer() {
        return this.handle.getPlayer();
    }

    @Override
    public Location getLocation() {
        return this.handle.getLocation();
    }

    @Override
    public String getSchematic() {
        return this.handle.getSchematic();
    }

    @Override
    public String getPlotName() {
        return this.handle.getPlotName();
    }

    @Override
    public void handleConfirm() {
        this.handle.handleConfirm();
    }

    @Override
    public void handleCancel() {
        this.handle.handleCancel();
    }

    @Override
    public void handleEscape() {
        this.handle.handleEscape();
    }

}

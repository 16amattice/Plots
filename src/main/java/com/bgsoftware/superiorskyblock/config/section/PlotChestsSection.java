package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.config.SettingsContainerHolder;

public class PlotChestsSection extends SettingsContainerHolder implements SettingsManager.PlotChests {

    @Override
    public String getChestTitle() {
        return getContainer().plotChestTitle;
    }

    @Override
    public int getDefaultPages() {
        return getContainer().plotChestsDefaultPage;
    }

    @Override
    public int getDefaultSize() {
        return getContainer().plotChestsDefaultSize;
    }

}

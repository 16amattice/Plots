package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.config.SettingsContainerHolder;

import java.util.List;

public class PlotNamesSection extends SettingsContainerHolder implements SettingsManager.PlotNames {

    @Override
    public boolean isRequiredForCreation() {
        return getContainer().plotNamesRequiredForCreation;
    }

    @Override
    public int getMaxLength() {
        return getContainer().plotNamesMaxLength;
    }

    @Override
    public int getMinLength() {
        return getContainer().plotNamesMinLength;
    }

    @Override
    public List<String> getFilteredNames() {
        return getContainer().filteredPlotNames;
    }

    @Override
    public boolean isColorSupport() {
        return getContainer().plotNamesColorSupport;
    }

    @Override
    public boolean isPlotTop() {
        return getContainer().plotNamesPlotTop;
    }

    @Override
    public boolean isPreventPlayerNames() {
        return getContainer().plotNamesPreventPlayerNames;
    }

}

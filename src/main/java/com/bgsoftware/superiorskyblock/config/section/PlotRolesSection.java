package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.config.SettingsContainerHolder;
import org.bukkit.configuration.ConfigurationSection;

public class PlotRolesSection extends SettingsContainerHolder implements SettingsManager.PlotRoles {

    @Override
    public ConfigurationSection getSection() {
        return getContainer().plotRolesSection;
    }

}

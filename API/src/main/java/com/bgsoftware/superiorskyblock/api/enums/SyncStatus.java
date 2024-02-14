package com.bgsoftware.superiorskyblock.api.enums;

import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;

/**
 * Sync status for {@link PlotFlag} and {@link PlotPrivilege} in plots.
 */
public enum SyncStatus {

    /**
     * The target is enabled and is not synced.
     */
    ENABLED,

    /**
     * The target is disabled and is not synced.
     */
    DISABLED,

    /**
     * The target is synced with upgrades and default config values.
     */
    SYNCED

}

package com.bgsoftware.superiorskyblock.api.service.region;

import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;

public enum InteractionResult {

    /**
     * The interaction was made outside an plot.
     */
    OUTSIDE_PLOT,

    /**
     * The player is missing an {@link PlotPrivilege} for doing the interaction.
     */
    MISSING_PRIVILEGE,

    /**
     * The interaction that was made cannot be done while the plot is being recalculated.
     */
    PLOT_RECALCULATE,

    /**
     * The interaction can be done.
     */
    SUCCESS

}

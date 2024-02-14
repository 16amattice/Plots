package com.bgsoftware.superiorskyblock.api.service.region;

import com.bgsoftware.superiorskyblock.api.events.PlotEnterEvent;

public enum MoveResult {

    /**
     * The player cannot do the movement as he is banned from the plot.
     */
    BANNED_FROM_PLOT,

    /**
     * The player cannot do the movement as the plot is locked to the public.
     */
    PLOT_LOCKED,

    /**
     * The {@link PlotEnterEvent} event was cancelled.
     */
    ENTER_EVENT_CANCELLED,

    /**
     * The player cannot move out of an plot into the wilderness.
     */
    LEAVE_PLOT_TO_OUTSIDE,

    /**
     * The player was moved too far away while being in plot-preview mode.
     */
    PLOT_PREVIEW_MOVED_TOO_FAR,

    /**
     * The player was teleported due to void-teleport.
     */
    VOID_TELEPORT,

    /**
     * The player can do the movement.
     */
    SUCCESS

}

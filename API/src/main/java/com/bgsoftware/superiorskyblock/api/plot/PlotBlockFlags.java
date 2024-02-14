package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.common.annotations.IntType;

/**
 * The integer value element annotated with {@link PlotBlockFlags} represents flags related to what to do
 * when block change is recorded. It is mainly used within the {@link Plot} interface and its methods.
 */
@IntType({PlotBlockFlags.SAVE_BLOCK_COUNTS, PlotBlockFlags.UPDATE_LAST_TIME_STATUS})
public @interface PlotBlockFlags {

    /**
     * Indicates to save block counts into the DB after the block count change.
     */
    int SAVE_BLOCK_COUNTS = (1 << 0);

    /**
     * Indicates to update the last time the plot was updated due to the block count change.
     */
    int UPDATE_LAST_TIME_STATUS = (1 << 1);

}

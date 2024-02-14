package com.bgsoftware.superiorskyblock.api.plot;

/**
 * Result of one of the block change methods of {@link Plot}
 */
public enum BlockChangeResult {

    /**
     * No blocks were available in the block counts map provided.
     */
    NO_AVAILABLE_BLOCKS,

    /**
     * The block provided had no value configured for it and therefore was not tracked.
     */
    MISSING_BLOCK_VALUE,

    /**
     * Tried to track a block change for the spawn plot.
     */
    SPAWN_PLOT,

    /**
     * The block change was tracked successfully.
     */
    SUCCESS

}

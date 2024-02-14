package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.common.annotations.IntType;

/**
 * The integer value element annotated with {@link PlotChunkFlags} represents flags related to which chunks
 * to do the action on. It is mainly used within the {@link Plot} interface and its methods.
 */
@IntType({PlotChunkFlags.ONLY_PROTECTED, PlotChunkFlags.NO_EMPTY_CHUNKS})
public @interface PlotChunkFlags {

    /**
     * Indicates to only do the action on chunks within the protected-radius of the plot.
     */
    int ONLY_PROTECTED = (1 << 0);

    /**
     * Indicates to only do the action on chunks that have blocks inside them.
     * It is generally a good practice to use this flag whenever possible to reduce performance impact.
     */
    int NO_EMPTY_CHUNKS = (1 << 1);

}

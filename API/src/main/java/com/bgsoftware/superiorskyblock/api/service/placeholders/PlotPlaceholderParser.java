package com.bgsoftware.superiorskyblock.api.service.placeholders;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.function.BiFunction;

/**
 * This class represents an plot placeholder parser.
 * It should give an output of the parsed value for an plot.
 */
public interface PlotPlaceholderParser extends BiFunction<Plot, SuperiorPlayer, String> {

    /**
     * Get the result of this placeholder for the given plot.
     *
     * @param plot         The plot to parse.
     * @param superiorPlayer The player that requested the placeholder.
     * @return The parsed result.
     */
    @Override
    @Nullable
    String apply(@Nullable Plot plot, SuperiorPlayer superiorPlayer);

}

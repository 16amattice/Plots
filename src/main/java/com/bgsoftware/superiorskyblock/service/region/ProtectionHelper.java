package com.bgsoftware.superiorskyblock.service.region;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.service.region.InteractionResult;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.messages.Message;

public class ProtectionHelper {

    private ProtectionHelper() {

    }

    public static boolean shouldPreventInteraction(InteractionResult interactionResult,
                                                   @Nullable SuperiorPlayer superiorPlayer, boolean sendMessages) {
        switch (interactionResult) {
            case PLOT_RECALCULATE:
                if (sendMessages && superiorPlayer != null)
                    Message.PLOT_BEING_CALCULATED.send(superiorPlayer);
                return true;
            case MISSING_PRIVILEGE:
                if (sendMessages && superiorPlayer != null)
                    Message.PROTECTION.send(superiorPlayer);
                return true;
            case OUTSIDE_PLOT:
                if (sendMessages && superiorPlayer != null)
                    Message.BUILD_OUTSIDE_PLOT.send(superiorPlayer);
                return true;
            case SUCCESS:
                return false;
        }

        throw new IllegalStateException("No handling for result " + interactionResult);
    }

}

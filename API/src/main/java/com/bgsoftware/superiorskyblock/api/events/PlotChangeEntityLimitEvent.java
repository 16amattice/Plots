package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeEntityLimitEvent is called when an entity-limit of an plot is changed.
 */
public class PlotChangeEntityLimitEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final Key entity;

    private int entityLimit;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the entity limit of an plot.
     *                       If set to null, it means the limit was changed via the console.
     * @param plot         The plot that the entity limit was changed for.
     * @param entity         The entity that the limit was changed for.
     * @param entityLimit    The new entity limit of the entity.
     */
    public PlotChangeEntityLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, Key entity, int entityLimit) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.entity = entity;
        this.entityLimit = entityLimit;
    }

    /**
     * Get the player that changed the entity limit.
     * If null, it means the limit was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the entity that the limit was changed for.
     */
    public Key getEntity() {
        return entity;
    }

    /**
     * Get the new entity limit of the entity.
     */
    public int getEntityLimit() {
        return entityLimit;
    }

    /**
     * Set the new entity limit of the entity..
     *
     * @param entityLimit The new entity limit to set.
     */
    public void setEntityLimit(int entityLimit) {
        Preconditions.checkArgument(entityLimit >= 0, "Cannot set the entity limit to a negative limit.");
        this.entityLimit = entityLimit;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}

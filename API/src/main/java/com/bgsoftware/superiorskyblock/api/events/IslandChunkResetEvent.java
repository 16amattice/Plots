package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.World;

/**
 * PlotChunkResetEvent is called when a chunk is reset inside an plot.
 */
public class PlotChunkResetEvent extends PlotEvent {

    private final World world;
    private final int chunkX;
    private final int chunkZ;

    /**
     * The constructor of the event.
     *
     * @param plot The plot that the chunk was reset in.
     * @param world  The world of the chunk.
     * @param chunkX The x-coords of the chunk.
     * @param chunkZ The z-coords of the chunk.
     */
    public PlotChunkResetEvent(Plot plot, World world, int chunkX, int chunkZ) {
        super(plot);
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    /**
     * Get the world of the chunk.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the x-coords of the chunk.
     */
    public int getChunkX() {
        return chunkX;
    }

    /**
     * Get the z-coords of the chunk.
     */
    public int getChunkZ() {
        return chunkZ;
    }

}

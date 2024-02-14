package com.bgsoftware.superiorskyblock.plot.chunk;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlotsDatabaseBridge;
import org.bukkit.World;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class DirtyChunksContainer {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private final EnumMap<World.Environment, BitSet> dirtyChunks = new EnumMap<>(World.Environment.class);

    private final Plot plot;
    private final int minChunkX;
    private final int minChunkZ;
    private final int chunksInXAxis;
    private final int totalChunksCount;
    private final boolean shouldSave;

    public DirtyChunksContainer(Plot plot) {
        this.plot = plot;

        BlockPosition minimum = plot.getMinimumPosition();
        this.minChunkX = minimum.getX() >> 4;
        this.minChunkZ = minimum.getZ() >> 4;

        BlockPosition maximum = plot.getMaximumPosition();
        int maxChunkX = maximum.getX() >> 4;
        int maxChunkZ = maximum.getZ() >> 4;
        int chunksInZAxis = maxChunkZ - this.minChunkZ;

        this.chunksInXAxis = maxChunkX - this.minChunkX;
        this.totalChunksCount = this.chunksInXAxis * chunksInZAxis;

        this.shouldSave = !plot.isSpawn();
    }

    public Plot getPlot() {
        return plot;
    }

    public boolean isMarkedDirty(ChunkPosition chunkPosition) {
        int chunkIndex = getChunkIndex(chunkPosition);

        if (chunkIndex < 0)
            throw new IllegalStateException("Chunk is not inside plot boundaries: " + chunkPosition);

        BitSet dirtyChunksBitset = this.dirtyChunks.get(chunkPosition.getWorldsInfo().getEnvironment());

        return dirtyChunksBitset != null && !dirtyChunksBitset.isEmpty() && dirtyChunksBitset.get(chunkIndex);
    }

    public void markEmpty(ChunkPosition chunkPosition, boolean save) {
        int chunkIndex = getChunkIndex(chunkPosition);

        if (chunkIndex < 0)
            throw new IllegalStateException("Chunk is not inside plot boundaries: " + chunkPosition);

        BitSet dirtyChunksBitset = this.dirtyChunks.get(chunkPosition.getWorldsInfo().getEnvironment());

        boolean isMarkedDirty = dirtyChunksBitset != null && !dirtyChunksBitset.isEmpty() && dirtyChunksBitset.get(chunkIndex);

        if (isMarkedDirty) {
            dirtyChunksBitset.clear(chunkIndex);
            if (this.shouldSave && save)
                PlotsDatabaseBridge.saveDirtyChunks(this);
        }
    }

    public void markDirty(ChunkPosition chunkPosition, boolean save) {
        int chunkIndex = getChunkIndex(chunkPosition);

        if (chunkIndex < 0)
            throw new IllegalStateException("Chunk is not inside plot boundaries: " + chunkPosition);

        BitSet dirtyChunksBitset = this.dirtyChunks.computeIfAbsent(chunkPosition.getWorldsInfo().getEnvironment(),
                e -> new BitSet(this.totalChunksCount));

        boolean isMarkedDirty = !dirtyChunksBitset.isEmpty() && dirtyChunksBitset.get(chunkIndex);

        if (!isMarkedDirty) {
            dirtyChunksBitset.set(chunkIndex);
            if (this.shouldSave && save)
                PlotsDatabaseBridge.saveDirtyChunks(this);
        }
    }

    public List<ChunkPosition> getDirtyChunks() {
        if (this.dirtyChunks.isEmpty())
            return Collections.emptyList();

        List<ChunkPosition> dirtyChunkPositions = new LinkedList<>();

        this.dirtyChunks.forEach(((environment, dirtyChunks) -> {
            if (!dirtyChunks.isEmpty()) {
                WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(plot, environment);
                if (worldInfo != null) {
                    for (int j = dirtyChunks.nextSetBit(0); j >= 0; j = dirtyChunks.nextSetBit(j + 1)) {
                        int deltaX = j / this.chunksInXAxis;
                        int deltaZ = j % this.chunksInXAxis;
                        dirtyChunkPositions.add(ChunkPosition.of(worldInfo, deltaX + this.minChunkX, deltaZ + this.minChunkZ));
                    }
                }
            }
        }));

        return dirtyChunkPositions;
    }

    private int getChunkIndex(ChunkPosition chunkPosition) {
        int deltaX = chunkPosition.getX() - this.minChunkX;
        int deltaZ = chunkPosition.getZ() - this.minChunkZ;
        return deltaX * this.chunksInXAxis + deltaZ;
    }

}

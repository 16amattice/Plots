package com.bgsoftware.superiorskyblock.world.event;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.service.world.WorldRecordService;
import com.bgsoftware.superiorskyblock.api.world.event.WorldEventsManager;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.Mutable;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.algorithm.DefaultPlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeCropGrowth;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeEntityLimits;
import com.google.common.base.Preconditions;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Deprecated
public class WorldEventsManagerImpl implements WorldEventsManager {

    private final LazyReference<WorldRecordService> worldRecordService = new LazyReference<WorldRecordService>() {
        @Override
        protected WorldRecordService create() {
            return plugin.getServices().getService(WorldRecordService.class);
        }
    };
    private final SuperiorSkyblockPlugin plugin;
    private final Map<UUID, Set<Chunk>> pendingLoadedChunks = new HashMap<>();

    public WorldEventsManagerImpl(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    private static boolean isHologram(ArmorStand armorStand) {
        return !armorStand.hasGravity() && armorStand.isSmall() && !armorStand.isVisible() &&
                armorStand.isCustomNameVisible() && armorStand.isMarker() && armorStand.getCustomName() != null;
    }

    @Override
    public void loadChunk(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk parameter cannot be null.");

        Location firstBlock = chunk.getBlock(0, 100, 0).getLocation();
        Plot plot = plugin.getGrid().getPlotAt(firstBlock);

        if (plot == null || plot.isSpawn())
            return;

        plugin.getNMSChunks().injectChunkSections(chunk);

        Set<Chunk> pendingLoadedChunksForPlot = this.pendingLoadedChunks.computeIfAbsent(plot.getUniqueId(), u -> new LinkedHashSet<>());
        pendingLoadedChunksForPlot.add(chunk);

        boolean cropGrowthEnabled = BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeCropGrowth.class);
        if (cropGrowthEnabled && plot.isInsideRange(chunk))
            plugin.getNMSChunks().startTickingChunk(plot, chunk, false);

        if (!plugin.getNMSChunks().isChunkEmpty(chunk))
            plot.markChunkDirty(chunk.getWorld(), chunk.getX(), chunk.getZ(), true);

        Location plotCenter = plot.getCenter(chunk.getWorld().getEnvironment());

        boolean entityLimitsEnabled = BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeEntityLimits.class);
        Mutable<Boolean> recalculateEntities = new Mutable<>(false);

        if (chunk.getX() == (plotCenter.getBlockX() >> 4) && chunk.getZ() == (plotCenter.getBlockZ() >> 4)) {
            if (chunk.getWorld().getEnvironment() == plugin.getSettings().getWorlds().getDefaultWorld()) {
                plot.setBiome(firstBlock.getWorld().getBiome(firstBlock.getBlockX(), firstBlock.getBlockZ()), false);
            }

            if (entityLimitsEnabled)
                recalculateEntities.setValue(true);
        }

        BukkitExecutor.sync(() -> {
            if (!pendingLoadedChunksForPlot.remove(chunk) || !chunk.isLoaded())
                return;

            // If we cannot recalculate entities at this moment, we want to track entities normally.
            if (!plot.getEntitiesTracker().canRecalculateEntityCounts())
                recalculateEntities.setValue(false);

            for (Entity entity : chunk.getEntities()) {
                // We want to delete old holograms of stacked blocks + count entities for the chunk
                if (entity instanceof ArmorStand && isHologram((ArmorStand) entity) &&
                        plugin.getStackedBlocks().getStackedBlockAmount(entity.getLocation().subtract(0, 1, 0)) > 1) {
                    entity.remove();
                }
            }

            if (recalculateEntities.getValue()) {
                plot.getEntitiesTracker().recalculateEntityCounts();
                pendingLoadedChunksForPlot.clear();
                this.pendingLoadedChunks.remove(plot.getUniqueId());
            }
        }, 2L);

        ChunkPosition chunkPosition = ChunkPosition.of(chunk);
        DefaultPlotCalculationAlgorithm.CACHED_CALCULATED_CHUNKS.remove(chunkPosition);

        plugin.getStackedBlocks().updateStackedBlockHolograms(chunk);
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk parameter cannot be null.");

        if (!plugin.getGrid().isPlotsWorld(chunk.getWorld()))
            return;

        plugin.getStackedBlocks().removeStackedBlockHolograms(chunk);

        Plot plot = plugin.getGrid().getPlotAt(chunk);

        if (plot == null)
            return;

        if (BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeCropGrowth.class))
            plugin.getNMSChunks().startTickingChunk(plot, chunk, true);

        if (!plot.isSpawn() && !plugin.getNMSChunks().isChunkEmpty(chunk))
            plot.markChunkDirty(chunk.getWorld(), chunk.getX(), chunk.getZ(), true);

        Arrays.stream(chunk.getEntities()).forEach(this.worldRecordService.get()::recordEntityDespawn);
    }

}

package com.bgsoftware.superiorskyblock.world;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotSetHomeEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChunkFlags;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import com.google.common.base.Preconditions;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EntityTeleports {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private EntityTeleports() {
    }

    public static void teleport(Entity entity, Location location) {
        teleport(entity, location, null);
    }

    public static void teleport(Entity entity, Location location, @Nullable Consumer<Boolean> teleportResult) {
        Plot plot = plugin.getGrid().getPlotAt(location);

        if (plot != null) {
            plugin.getProviders().getWorldsProvider().prepareTeleport(plot, location.clone(),
                    () -> teleportEntity(entity, location, teleportResult));
        } else {
            teleportEntity(entity, location, teleportResult);
        }
    }

    public static void teleportUntilSuccess(Entity entity, Location location, long cooldown, @Nullable Runnable onFinish) {
        teleport(entity, location, succeed -> {
            if (!succeed) {
                if (cooldown > 0) {
                    BukkitExecutor.sync(() -> teleportUntilSuccess(entity, location, cooldown, onFinish), cooldown);
                } else {
                    teleportUntilSuccess(entity, location, cooldown, onFinish);
                }
            } else if (onFinish != null) {
                onFinish.run();
            }
        });
    }

    public static CompletableFuture<Location> findPlotSafeLocation(Plot plot, World.Environment environment) {
        Location homeLocation = plot.getPlotHome(environment);

        Preconditions.checkNotNull(homeLocation, "Cannot find a suitable home location for plot " +
                plot.getUniqueId());

        World plotsWorld = Objects.requireNonNull(plugin.getGrid().getPlotsWorld(plot, environment), "world is null");
        float rotationYaw = homeLocation.getYaw();
        float rotationPitch = homeLocation.getPitch();

        Log.debug(Debug.FIND_SAFE_TELEPORT, plot.getOwner().getName(), environment);

        // We first check that the home location is safe. If it is, we can return.
        {
            Block homeLocationBlock = homeLocation.getBlock();
            if (plot.isSpawn() || WorldBlocks.isSafeBlock(homeLocationBlock)) {
                Log.debugResult(Debug.FIND_SAFE_TELEPORT, "Result Location", homeLocation);
                return CompletableFuture.completedFuture(homeLocation);
            }
        }

        // In case it is not safe anymore, we check in the same location if the highest block is safe.
        {
            Block teleportLocationHighestBlock = plotsWorld.getHighestBlockAt(homeLocation).getRelative(BlockFace.UP);
            if (WorldBlocks.isSafeBlock(teleportLocationHighestBlock)) {
                return CompletableFuture.completedFuture(adjustLocationToHome(plot,
                        teleportLocationHighestBlock, rotationYaw, rotationPitch));
            }
        }

        CompletableFuture<Location> result = new CompletableFuture<>();

        // The teleport location is not safe. We check for a safe spot in the center of the plot.

        Location plotCenterLocation = plot.getCenter(environment);

        if (!plotCenterLocation.equals(homeLocation)) {
            ChunksProvider.loadChunk(ChunkPosition.of(plotCenterLocation), ChunkLoadReason.FIND_SAFE_SPOT, chunk -> {
                {
                    Block plotCenterBlock = plotCenterLocation.getBlock().getRelative(BlockFace.UP);
                    if (WorldBlocks.isSafeBlock(plotCenterBlock)) {
                        result.complete(adjustLocationToHome(plot, plotCenterBlock, rotationYaw, rotationPitch));
                        return;
                    }
                }

                // The center is not safe, we check in the same location if the highest block is safe.
                {
                    Block plotCenterHighestBlock = plotsWorld.getHighestBlockAt(plotCenterLocation).getRelative(BlockFace.UP);
                    if (WorldBlocks.isSafeBlock(plotCenterHighestBlock)) {
                        result.complete(adjustLocationToHome(plot, plotCenterHighestBlock, rotationYaw, rotationPitch));
                        return;
                    }
                }

                // The center is not safe; we look for a new spot on the plot.
                findNewSafeSpotOnPlot(plot, plotsWorld, homeLocation, rotationYaw, rotationPitch, result);
            });
        } else {
            findNewSafeSpotOnPlot(plot, plotsWorld, homeLocation, rotationYaw, rotationPitch, result);
        }

        return result;
    }

    private static void findNewSafeSpotOnPlot(Plot plot, World plotsWorld, Location homeLocation,
                                                float rotationYaw, float rotationPitch, CompletableFuture<Location> result) {
        ChunkPosition homeChunk = ChunkPosition.of(homeLocation);

        List<ChunkPosition> plotChunks = new ArrayList<>(PlotUtils.getChunkCoords(plot,
                WorldInfo.of(plotsWorld), PlotChunkFlags.ONLY_PROTECTED | PlotChunkFlags.NO_EMPTY_CHUNKS));
        plotChunks.sort(Comparator.comparingInt(o -> o.distanceSquared(homeChunk)));

        findSafeSpotInChunk(plot, plotChunks, 0, plotsWorld, homeLocation, safeSpot -> {
            if (safeSpot != null) {
                result.complete(adjustLocationToHome(plot, safeSpot.getBlock(), rotationYaw, rotationPitch));
            } else {
                result.complete(null);
            }
        });
    }

    private static void findSafeSpotInChunk(Plot plot, List<ChunkPosition> plotChunks, int index,
                                            World plotsWorld, Location homeLocation, Consumer<Location> onResult) {
        if (index >= plotChunks.size()) {
            onResult.accept(null);
            return;
        }

        ChunkPosition chunkPosition = plotChunks.get(index);

        ChunksProvider.loadChunk(chunkPosition, ChunkLoadReason.FIND_SAFE_SPOT, null).whenComplete((chunk, err) -> {
            ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();

            if (WorldBlocks.isChunkEmpty(plot, chunkSnapshot)) {
                findSafeSpotInChunk(plot, plotChunks, index + 1, plotsWorld, homeLocation, onResult);
                return;
            }

            BukkitExecutor.createTask().runAsync(v -> {
                Location closestSafeSpot = null;
                double closestSafeSpotDistance = 0;

                int worldBuildLimit = plotsWorld.getMaxHeight() - 1;
                int worldMinLimit = plugin.getNMSWorld().getMinHeight(plotsWorld);

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int y = chunkSnapshot.getHighestBlockYAt(x, z);

                        if (y < worldMinLimit || y + 2 > worldBuildLimit)
                            continue;

                        int worldX = chunkSnapshot.getX() * 16 + x;
                        int worldZ = chunkSnapshot.getZ() * 16 + z;

                        // In some versions, the ChunkSnapshot#getHighestBlockYAt seems to return
                        // one block above the actual highest block. Therefore, the check is on the
                        // returned block and the block below it.
                        Location safeSpot;
                        if (WorldBlocks.isSafeBlock(chunkSnapshot, x, y, z)) {
                            safeSpot = new Location(plotsWorld, worldX, y + 1, worldZ);
                        } else if (y - 1 >= worldMinLimit && WorldBlocks.isSafeBlock(chunkSnapshot, x, y - 1, z)) {
                            safeSpot = new Location(plotsWorld, worldX, y, worldZ);
                        } else {
                            continue;
                        }

                        double distanceFromHome = safeSpot.distanceSquared(homeLocation);
                        if (closestSafeSpot == null || distanceFromHome < closestSafeSpotDistance) {
                            closestSafeSpotDistance = distanceFromHome;
                            closestSafeSpot = safeSpot;
                        }
                    }
                }

                return closestSafeSpot;
            }).runSync(location -> {
                if (location != null) {
                    onResult.accept(location);
                } else {
                    findSafeSpotInChunk(plot, plotChunks, index + 1, plotsWorld, homeLocation, onResult);
                }
            });

        });
    }

    private static void teleportEntity(Entity entity, Location location, @Nullable Consumer<Boolean> teleportResult) {
        entity.eject();
        plugin.getProviders().getAsyncProvider().teleport(entity, location, teleportResult);
    }

    private static Location adjustLocationToHome(Plot plot, Block block, float yaw, float pitch) {
        Location newHomeLocation;

        {
            Location location = block.getLocation().add(0.5, 0, 0.5);
            location.setYaw(yaw);
            location.setPitch(pitch);
            EventResult<Location> eventResult = plugin.getEventsBus().callPlotSetHomeEvent(plot, location,
                    PlotSetHomeEvent.Reason.SAFE_HOME, null);

            if (eventResult.isCancelled()) {
                newHomeLocation = location;
            } else {
                newHomeLocation = eventResult.getResult();
                plot.setPlotHome(newHomeLocation);
            }
        }

        Log.debugResult(Debug.FIND_SAFE_TELEPORT, "Result Location", newHomeLocation);

        return newHomeLocation;
    }

}

package com.bgsoftware.superiorskyblock.plot;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.BorderColor;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChunkFlags;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.EnumHelper;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PlotUtils {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();
    private static final EnumMap<World.Environment, Biome> DEFAULT_WORLD_BIOMES = new EnumMap<>(World.Environment.class);

    static {
        {
            Biome biome = Optional.ofNullable(EnumHelper.getEnum(Biome.class,
                    plugin.getSettings().getWorlds().getNormal().getBiome())).orElse(Biome.PLAINS);
            DEFAULT_WORLD_BIOMES.put(World.Environment.NORMAL, biome);
        }

        {
            Biome biome = Optional.ofNullable(EnumHelper.getEnum(Biome.class,
                            plugin.getSettings().getWorlds().getNether().getBiome(), "NETHER_WASTES", "NETHER", "HELL"))
                    .orElseThrow(IllegalArgumentException::new);
            DEFAULT_WORLD_BIOMES.put(World.Environment.NETHER, biome);
        }

        {
            Biome biome = Optional.ofNullable(EnumHelper.getEnum(Biome.class,
                            plugin.getSettings().getWorlds().getEnd().getBiome(), "THE_END", "SKY"))
                    .orElseThrow(IllegalArgumentException::new);
            DEFAULT_WORLD_BIOMES.put(World.Environment.THE_END, biome);
        }
    }

    private PlotUtils() {

    }

    public static List<ChunkPosition> getChunkCoords(Plot plot, WorldInfo worldInfo, @PlotChunkFlags int flags) {
        List<ChunkPosition> chunkCoords = new LinkedList<>();

        boolean onlyProtected = (flags & PlotChunkFlags.ONLY_PROTECTED) != 0;
        boolean noEmptyChunks = (flags & PlotChunkFlags.NO_EMPTY_CHUNKS) != 0;

        BlockPosition min = onlyProtected ? plot.getMinimumProtectedPosition() : plot.getMinimumPosition();
        BlockPosition max = onlyProtected ? plot.getMaximumProtectedPosition() : plot.getMaximumPosition();

        for (int x = min.getX() >> 4; x <= max.getX() >> 4; x++) {
            for (int z = min.getZ() >> 4; z <= max.getZ() >> 4; z++) {
                if (!noEmptyChunks || plot.isChunkDirty(worldInfo.getName(), x, z)) {
                    chunkCoords.add(ChunkPosition.of(worldInfo, x, z));
                }
            }
        }

        return chunkCoords;
    }

    public static Map<WorldInfo, List<ChunkPosition>> getChunkCoords(Plot plot, @PlotChunkFlags int flags) {
        Map<WorldInfo, List<ChunkPosition>> chunkCoords = new HashMap<>();

        {
            if (plugin.getProviders().getWorldsProvider().isNormalEnabled() && plot.wasSchematicGenerated(World.Environment.NORMAL)) {
                WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(plot, World.Environment.NORMAL);
                List<ChunkPosition> chunkPositions = getChunkCoords(plot, worldInfo, flags);
                if (!chunkPositions.isEmpty())
                    chunkCoords.put(worldInfo, chunkPositions);
            }
        }

        if (plugin.getProviders().getWorldsProvider().isNetherEnabled() && plot.wasSchematicGenerated(World.Environment.NETHER)) {
            WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(plot, World.Environment.NETHER);
            List<ChunkPosition> chunkPositions = getChunkCoords(plot, worldInfo, flags);
            if (!chunkPositions.isEmpty())
                chunkCoords.put(worldInfo, chunkPositions);
        }

        if (plugin.getProviders().getWorldsProvider().isEndEnabled() && plot.wasSchematicGenerated(World.Environment.THE_END)) {
            WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(plot, World.Environment.THE_END);
            List<ChunkPosition> chunkPositions = getChunkCoords(plot, worldInfo, flags);
            if (!chunkPositions.isEmpty())
                chunkCoords.put(worldInfo, chunkPositions);
        }

        for (World registeredWorld : plugin.getGrid().getRegisteredWorlds()) {
            WorldInfo worldInfo = WorldInfo.of(registeredWorld);
            List<ChunkPosition> chunkPositions = getChunkCoords(plot, worldInfo, flags);
            if (!chunkPositions.isEmpty())
                chunkCoords.put(worldInfo, chunkPositions);
        }

        return chunkCoords;
    }

    public static List<CompletableFuture<Chunk>> getAllChunksAsync(Plot plot, World world, @PlotChunkFlags int flags,
                                                                   ChunkLoadReason chunkLoadReason,
                                                                   Consumer<Chunk> onChunkLoad) {
        return new SequentialListBuilder<CompletableFuture<Chunk>>()
                .mutable()
                .build(PlotUtils.getChunkCoords(plot, WorldInfo.of(world), flags), chunkPosition ->
                        ChunksProvider.loadChunk(chunkPosition, chunkLoadReason, onChunkLoad));
    }

    public static List<CompletableFuture<Chunk>> getAllChunksAsync(Plot plot, @PlotChunkFlags int flags,
                                                                   ChunkLoadReason chunkLoadReason,
                                                                   Consumer<Chunk> onChunkLoad) {
        List<CompletableFuture<Chunk>> chunkCoords = new LinkedList<>();

        {
            if (plugin.getProviders().getWorldsProvider().isNormalEnabled() && plot.wasSchematicGenerated(World.Environment.NORMAL)) {
                World normalWorld = plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).getWorld();
                chunkCoords.addAll(getAllChunksAsync(plot, normalWorld, flags, chunkLoadReason, onChunkLoad));
            }
        }

        if (plugin.getProviders().getWorldsProvider().isNetherEnabled() && plot.wasSchematicGenerated(World.Environment.NETHER)) {
            World netherWorld = plot.getCenter(World.Environment.NETHER).getWorld();
            chunkCoords.addAll(getAllChunksAsync(plot, netherWorld, flags, chunkLoadReason, onChunkLoad));
        }

        if (plugin.getProviders().getWorldsProvider().isEndEnabled() && plot.wasSchematicGenerated(World.Environment.THE_END)) {
            World endWorld = plot.getCenter(World.Environment.THE_END).getWorld();
            chunkCoords.addAll(getAllChunksAsync(plot, endWorld, flags, chunkLoadReason, onChunkLoad));
        }

        for (World registeredWorld : plugin.getGrid().getRegisteredWorlds()) {
            chunkCoords.addAll(getAllChunksAsync(plot, registeredWorld, flags, chunkLoadReason, onChunkLoad));
        }

        return chunkCoords;
    }

    public static void updatePlotFly(Plot plot, SuperiorPlayer superiorPlayer) {
        superiorPlayer.runIfOnline(player -> {
            if (!player.getAllowFlight() && superiorPlayer.hasPlotFlyEnabled() && plot.hasPermission(superiorPlayer, PlotPrivileges.FLY)) {
                player.setAllowFlight(true);
                player.setFlying(true);
                Message.PLOT_FLY_ENABLED.send(player);
            } else if (player.getAllowFlight() && !plot.hasPermission(superiorPlayer, PlotPrivileges.FLY)) {
                player.setAllowFlight(false);
                player.setFlying(false);
                Message.PLOT_FLY_DISABLED.send(player);
            }
        });
    }

    public static void updateTradingMenus(Plot plot, SuperiorPlayer superiorPlayer) {
        superiorPlayer.runIfOnline(player -> {
            Inventory openInventory = player.getOpenInventory().getTopInventory();
            if (openInventory != null && openInventory.getType() == InventoryType.MERCHANT && !plot.hasPermission(superiorPlayer, PlotPrivileges.VILLAGER_TRADING))
                player.closeInventory();
        });
    }

    public static void resetChunksExcludedFromList(Plot plot, Collection<ChunkPosition> excludedChunkPositions) {
        Map<WorldInfo, List<ChunkPosition>> chunksToDelete = PlotUtils.getChunkCoords(plot, 0);
        chunksToDelete.values().forEach(chunkPositions -> {
            List<ChunkPosition> clonedChunkPositions = new LinkedList<>(chunkPositions);
            clonedChunkPositions.removeAll(excludedChunkPositions);
            deleteChunks(plot, clonedChunkPositions, null);
        });
    }

    public static void sendMessage(Plot plot, Message message, List<UUID> ignoredMembers, Object... args) {
        for (SuperiorPlayer plotMember : plot.getPlotMembers(true)) {
            if (!ignoredMembers.contains(plotMember.getUniqueId()))
                message.send(plotMember, args);
        }
    }

    public static double getGeneratorPercentageDecimal(Plot plot, Key key, World.Environment environment) {
        int totalAmount = plot.getGeneratorTotalAmount(environment);
        return totalAmount == 0 ? 0 : (plot.getGeneratorAmount(key, environment) * 100D) / totalAmount;
    }

    public static boolean checkKickRestrictions(SuperiorPlayer superiorPlayer, Plot plot, SuperiorPlayer targetPlayer) {
        if (!plot.isMember(targetPlayer)) {
            Message.PLAYER_NOT_INSIDE_PLOT.send(superiorPlayer);
            return false;
        }

        if (!targetPlayer.getPlayerRole().isLessThan(superiorPlayer.getPlayerRole())) {
            Message.KICK_PLAYERS_WITH_LOWER_ROLE.send(superiorPlayer);
            return false;
        }

        return true;
    }

    public static void handleKickPlayer(SuperiorPlayer caller, Plot plot, SuperiorPlayer target) {
        handleKickPlayer(caller, caller.getName(), plot, target);
    }

    public static void handleKickPlayer(SuperiorPlayer caller, String callerName, Plot plot, SuperiorPlayer target) {
        if (!plugin.getEventsBus().callPlotKickEvent(caller, target, plot))
            return;

        plot.kickMember(target);

        PlotUtils.sendMessage(plot, Message.KICK_ANNOUNCEMENT, Collections.emptyList(), target.getName(), callerName);

        Message.GOT_KICKED.send(target, callerName);
    }

    public static boolean checkBanRestrictions(SuperiorPlayer superiorPlayer, Plot plot, SuperiorPlayer targetPlayer) {
        Plot playerPlot = superiorPlayer.getPlot();
        if (playerPlot != null && playerPlot.isMember(targetPlayer) &&
                !targetPlayer.getPlayerRole().isLessThan(superiorPlayer.getPlayerRole())) {
            Message.BAN_PLAYERS_WITH_LOWER_ROLE.send(superiorPlayer);
            return false;
        }

        if (plot.isBanned(targetPlayer)) {
            Message.PLAYER_ALREADY_BANNED.send(superiorPlayer);
            return false;
        }

        return true;
    }

    public static void handleBanPlayer(SuperiorPlayer caller, Plot plot, SuperiorPlayer target) {
        if (!plugin.getEventsBus().callPlotBanEvent(caller, target, plot))
            return;

        plot.banMember(target, caller);

        PlotUtils.sendMessage(plot, Message.BAN_ANNOUNCEMENT, Collections.emptyList(), target.getName(), caller.getName());

        Message.GOT_BANNED.send(target, plot.getOwner().getName());
    }

    public static void deleteChunks(Plot plot, List<ChunkPosition> chunkPositions, Runnable onFinish) {
        plugin.getNMSChunks().deleteChunks(plot, chunkPositions, onFinish);
        chunkPositions.forEach(chunkPosition -> {
            plugin.getStackedBlocks().removeStackedBlocks(chunkPosition);
            plugin.getEventsBus().callPlotChunkResetEvent(plot, chunkPosition);
        });
    }

    public static boolean isValidRoleForLimit(PlayerRole playerRole) {
        return playerRole.isRoleLadder() && !playerRole.isFirstRole() && !playerRole.isLastRole();
    }

    public static boolean isWarpNameLengthValid(String warpName) {
        return warpName.length() <= getMaxWarpNameLength();
    }

    public static int getMaxWarpNameLength() {
        return 255;
    }

    public static boolean handleBorderColorUpdate(SuperiorPlayer superiorPlayer, BorderColor borderColor) {
        if (!superiorPlayer.hasWorldBorderEnabled()) {
            if (!plugin.getEventsBus().callPlayerToggleBorderEvent(superiorPlayer))
                return false;

            superiorPlayer.toggleWorldBorder();
        }

        if (!plugin.getEventsBus().callPlayerChangeBorderColorEvent(superiorPlayer, borderColor))
            return false;

        superiorPlayer.setBorderColor(borderColor);
        plugin.getNMSWorld().setWorldBorder(superiorPlayer,
                plugin.getGrid().getPlotAt(superiorPlayer.getLocation()));

        Message.BORDER_PLAYER_COLOR_UPDATED.send(superiorPlayer,
                Formatters.BORDER_COLOR_FORMATTER.format(borderColor, superiorPlayer.getUserLocale()));

        return true;
    }

    public static Biome getDefaultWorldBiome(World.Environment environment) {
        return Objects.requireNonNull(DEFAULT_WORLD_BIOMES.get(environment));
    }

    public static List<Biome> getDefaultWorldBiomes() {
        return new SequentialListBuilder<Biome>().build(DEFAULT_WORLD_BIOMES.values());
    }

}

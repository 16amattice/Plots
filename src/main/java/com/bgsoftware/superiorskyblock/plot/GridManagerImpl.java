package com.bgsoftware.superiorskyblock.plot;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridgeMode;
import com.bgsoftware.superiorskyblock.api.handlers.GridManager;
import com.bgsoftware.superiorskyblock.api.hooks.LazyWorldsProvider;
import com.bgsoftware.superiorskyblock.api.hooks.WorldsProvider;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.container.PlotsContainer;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.service.dragon.DragonBattleService;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.world.algorithm.PlotCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.LazyWorldLocation;
import com.bgsoftware.superiorskyblock.core.Manager;
import com.bgsoftware.superiorskyblock.core.SBlockPosition;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.database.DatabaseResult;
import com.bgsoftware.superiorskyblock.core.database.bridge.GridDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlotsDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.algorithm.DefaultPlotCreationAlgorithm;
import com.bgsoftware.superiorskyblock.plot.builder.PlotBuilderImpl;
import com.bgsoftware.superiorskyblock.plot.preview.PlotPreviews;
import com.bgsoftware.superiorskyblock.plot.preview.SPlotPreview;
import com.bgsoftware.superiorskyblock.plot.purge.PlotsPurger;
import com.bgsoftware.superiorskyblock.player.chat.PlayerChat;
import com.bgsoftware.superiorskyblock.world.schematic.BaseSchematic;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class GridManagerImpl extends Manager implements GridManager {

    private static final Function<Plot, UUID> PLOT_OWNERS_MAPPER = plot -> plot.getOwner().getUniqueId();

    private final Set<UUID> pendingCreationTasks = Sets.newHashSet();
    private final Set<UUID> customWorlds = Sets.newHashSet();

    private final LazyReference<DragonBattleService> dragonBattleService = new LazyReference<DragonBattleService>() {
        @Override
        protected DragonBattleService create() {
            return plugin.getServices().getService(DragonBattleService.class);
        }
    };

    private final PlotsPurger plotsPurger;
    private final PlotPreviews plotPreviews;
    private PlotsContainer plotsContainer;
    private DatabaseBridge databaseBridge;
    private PlotCreationAlgorithm plotCreationAlgorithm;

    private Plot spawnPlot;
    private SBlockPosition lastPlot;

    private BigDecimal totalWorth = BigDecimal.ZERO;
    private long lastTimeWorthUpdate = 0;
    private BigDecimal totalLevel = BigDecimal.ZERO;
    private long lastTimeLevelUpdate = 0;

    private boolean pluginDisable = false;

    private boolean forceSort = false;

    private final List<SortingType> pendingSortingTypes = new LinkedList<>();

    public GridManagerImpl(SuperiorSkyblockPlugin plugin, PlotsPurger plotsPurger, PlotPreviews plotPreviews) {
        super(plugin);
        this.plotsPurger = plotsPurger;
        this.plotPreviews = plotPreviews;
    }

    public void setPlotsContainer(@NotNull PlotsContainer plotsContainer) {
        this.plotsContainer = plotsContainer;
        pendingSortingTypes.forEach(sortingType -> plotsContainer.addSortingType(sortingType, false));
        pendingSortingTypes.clear();
    }

    @Override
    public void loadData() {
        if (this.plotsContainer == null)
            throw new RuntimeException("GridManager was not initialized correctly. Contact Ome_R regarding this!");

        initializeDatabaseBridge();
        if (this.plotCreationAlgorithm == null)
            this.plotCreationAlgorithm = DefaultPlotCreationAlgorithm.getInstance();

        this.lastPlot = new SBlockPosition(plugin.getSettings().getWorlds().getDefaultWorldName(), 0, 100, 0);
        BukkitExecutor.sync(this::updateSpawn);
    }

    public void updateSpawn() {
        try {
            this.spawnPlot = new SpawnPlot();
        } catch (ManagerLoadException error) {
            ManagerLoadException.handle(error);
        }
    }

    public void syncUpgrades() {
        getPlots().forEach(Plot::updateUpgrades);
    }

    @Override
    public void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(schemName, "schemName parameter cannot be null.");
        Preconditions.checkNotNull(bonus, "bonus parameter cannot be null.");
        Preconditions.checkNotNull(biome, "biome parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        createPlot(superiorPlayer, schemName, bonus, biome, plotName, false);
    }

    @Override
    public void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName, boolean offset) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(schemName, "schemName parameter cannot be null.");
        Preconditions.checkNotNull(bonus, "bonus parameter cannot be null.");
        Preconditions.checkNotNull(biome, "biome parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        createPlot(superiorPlayer, schemName, bonus, BigDecimal.ZERO, biome, plotName, false);
    }

    @Override
    public void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonusWorth,
                             BigDecimal bonusLevel, Biome biome, String plotName, boolean offset) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(schemName, "schemName parameter cannot be null.");
        Preconditions.checkNotNull(bonusWorth, "bonusWorth parameter cannot be null.");
        Preconditions.checkNotNull(bonusLevel, "bonusLevel parameter cannot be null.");
        Preconditions.checkNotNull(biome, "biome parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        Plot.Builder builder = Plot.newBuilder()
                .setOwner(superiorPlayer)
                .setSchematicName(schemName)
                .setName(plotName);

        if (!offset) {
            builder.setBonusWorth(bonusWorth)
                    .setBonusLevel(bonusLevel);
        }

        createPlot(builder, biome, offset);
    }

    @Override
    public void createPlot(Plot.Builder builderParam, Biome biome, boolean offset) {
        Preconditions.checkNotNull(builderParam, "builder parameter cannot be null.");
        Preconditions.checkNotNull(biome, "biome parameter cannot be null.");
        Preconditions.checkArgument(builderParam instanceof PlotBuilderImpl, "Cannot create plots out of a custom builder.");

        PlotBuilderImpl builder = (PlotBuilderImpl) builderParam;

        Preconditions.checkArgument(builder.owner != null, "Cannot create an plot with an invalid owner.");

        Schematic schematic = builder.plotType == null ? null : plugin.getSchematics().getSchematic(builder.plotType);

        Preconditions.checkArgument(schematic != null, "Cannot create an plot with an invalid schematic.");

        try {
            if (!Bukkit.isPrimaryThread()) {
                BukkitExecutor.sync(() -> createPlotInternalAsync(builder, biome, offset, schematic));
            } else {
                createPlotInternalAsync(builder, biome, offset, schematic);
            }
        } catch (Throwable error) {
            Log.entering("ENTER", builder.owner.getName(), builder.plotType, biome, offset);
            Log.error(error, "An unexpected error occurred while creating an plot:");
            builder.owner.setPlot(null);
            Message.CREATE_PLOT_FAILURE.send(builder.owner);
        }
    }

    private void createPlotInternalAsync(PlotBuilderImpl builder, Biome biome, boolean offset, Schematic schematic) {
        assert builder.owner != null;

        Log.debug(Debug.CREATE_PLOT, builder.owner.getName(), builder.bonusWorth, builder.bonusLevel,
                builder.plotName, offset, biome, schematic.getName());

        // Removing any active previews for the player.
        boolean updateGamemode = this.plotPreviews.endPlotPreview(builder.owner) != null;

        if (!plugin.getEventsBus().callPrePlotCreateEvent(builder.owner, builder.plotName))
            return;

        builder.setUniqueId(generatePlotUUID());

        long startTime = System.currentTimeMillis();

        pendingCreationTasks.add(builder.owner.getUniqueId());

        this.plotCreationAlgorithm.createPlot(builder, this.lastPlot).whenComplete((plotCreationResult, error) -> {
            pendingCreationTasks.remove(builder.owner.getUniqueId());

            switch (plotCreationResult.getStatus()) {
                case NAME_OCCUPIED:
                    builder.owner.setPlot(null);
                    Message.PLOT_ALREADY_EXIST.send(builder.owner);
                    return;
                case SUCCESS:
                    break;
                default:
                    Log.warn("Cannot handle creation status: " + plotCreationResult.getStatus());
                    builder.owner.setPlot(null);
                    Message.CREATE_PLOT_FAILURE.send(builder.owner);
                    return;
            }

            if (error == null) {
                try {
                    Plot plot = plotCreationResult.getPlot();
                    Location plotLocation = plotCreationResult.getPlotLocation();
                    boolean teleportPlayer = plotCreationResult.shouldTeleportPlayer();

                    List<ChunkPosition> affectedChunks = schematic instanceof BaseSchematic ?
                            ((BaseSchematic) schematic).getAffectedChunks() : null;

                    this.plotsContainer.addPlot(plot);
                    setLastPlot(new SBlockPosition(plotLocation));

                    try {
                        plot.getDatabaseBridge().setDatabaseBridgeMode(DatabaseBridgeMode.IDLE);

                        plot.setBiome(biome);
                        plot.setSchematicGenerate(plugin.getSettings().getWorlds().getDefaultWorld());
                        plot.setCurrentlyActive(true);

                        if (offset) {
                            plot.setBonusWorth(plot.getRawWorth().negate());
                            plot.setBonusLevel(plot.getRawLevel().negate());
                        }
                    } finally {
                        plot.getDatabaseBridge().setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
                    }

                    PlotsDatabaseBridge.insertPlot(plot, affectedChunks);

                    plot.setPlotHome(schematic.adjustRotation(plotLocation));

                    BukkitExecutor.sync(() -> builder.owner.runIfOnline(player -> {
                        if (updateGamemode)
                            player.setGameMode(GameMode.SURVIVAL);

                        if (!teleportPlayer) {
                            Message.CREATE_PLOT.send(builder.owner, Formatters.LOCATION_FORMATTER.format(
                                    plotLocation), System.currentTimeMillis() - startTime);
                        } else {
                            builder.owner.teleport(plot, result -> {
                                Message.CREATE_PLOT.send(builder.owner, Formatters.LOCATION_FORMATTER.format(
                                        plotLocation), System.currentTimeMillis() - startTime);

                                if (result) {
                                    if (affectedChunks != null)
                                        BukkitExecutor.sync(() -> PlotUtils.resetChunksExcludedFromList(plot, affectedChunks), 10L);
                                    if (plugin.getSettings().getWorlds().getDefaultWorld() == World.Environment.THE_END) {
                                        plugin.getNMSDragonFight().awardTheEndAchievement(player);
                                        this.dragonBattleService.get().resetEnderDragonBattle(plot);
                                    }
                                }
                            });
                        }
                    }), 1L);

                    return;
                } catch (Throwable runtimeError) {
                    error = runtimeError;
                }
            }

            Log.entering(builder.owner.getName(), builder.bonusWorth, builder.bonusLevel, builder.plotName,
                    offset, biome, schematic.getName());
            Log.error(error, "An unexpected error occurred while creating an plot:");

            builder.owner.setPlot(null);

            Message.CREATE_PLOT_FAILURE.send(builder.owner);
        });
    }

    @Override
    public void setPlotCreationAlgorithm(@Nullable PlotCreationAlgorithm plotCreationAlgorithm) {
        this.plotCreationAlgorithm = plotCreationAlgorithm != null ? plotCreationAlgorithm :
                DefaultPlotCreationAlgorithm.getInstance();
    }

    @Override
    public PlotCreationAlgorithm getPlotCreationAlgorithm() {
        return Optional.ofNullable(this.plotCreationAlgorithm).orElse(DefaultPlotCreationAlgorithm.getInstance());
    }

    @Override
    public boolean hasActiveCreateRequest(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return pendingCreationTasks.contains(superiorPlayer.getUniqueId());
    }

    @Override
    public void startPlotPreview(SuperiorPlayer superiorPlayer, String schemName, String plotName) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(schemName, "schemName parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");

        Location previewLocation = plugin.getSettings().getPreviewPlots().get(schemName.toLowerCase(Locale.ENGLISH));
        if (previewLocation != null && previewLocation.getWorld() != null) {
            superiorPlayer.teleport(previewLocation, result -> {
                if (result) {
                    this.plotPreviews.startPlotPreview(new SPlotPreview(superiorPlayer, previewLocation, schemName, plotName));
                    BukkitExecutor.ensureMain(() -> superiorPlayer.runIfOnline(player -> player.setGameMode(GameMode.SPECTATOR)));
                    Message.PLOT_PREVIEW_START.send(superiorPlayer, schemName);
                }
            });
        }
    }

    @Override
    public void cancelPlotPreview(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        PlotPreview plotPreview = this.plotPreviews.endPlotPreview(superiorPlayer);
        if (plotPreview != null) {
            superiorPlayer.runIfOnline(player -> {
                BukkitExecutor.ensureMain(() -> superiorPlayer.teleport(plugin.getGrid().getSpawnPlot(), teleportResult -> {
                    if (teleportResult && superiorPlayer.isOnline())
                        player.setGameMode(GameMode.SURVIVAL);
                }));
                PlayerChat.remove(player);
            });
        }
    }

    @Override
    public void cancelAllPlotPreviews() {
        if (!Bukkit.isPrimaryThread()) {
            BukkitExecutor.sync(this::cancelAllPlotPreviewsSync);
        } else {
            cancelAllPlotPreviewsSync();
        }
    }

    private void cancelAllPlotPreviewsSync() {
        if (!Bukkit.isPrimaryThread()) {
            Log.warn("Trying to cancel all plot previews asynchronous. Stack trace:");
            new Exception().printStackTrace();
        }

        this.plotPreviews.getActivePreviews().forEach(plotPreview -> {
            SuperiorPlayer superiorPlayer = plotPreview.getPlayer();
            superiorPlayer.runIfOnline(player -> {
                superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
                // We don't wait for the teleport to happen, as this method is called when the server is disabled.
                // Therefore, we can't wait for the async task to occur.
                player.setGameMode(GameMode.SURVIVAL);
                PlayerChat.remove(player);
            });
        });
    }

    @Override
    public PlotPreview getPlotPreview(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return this.plotPreviews.getPlotPreview(superiorPlayer);
    }

    @Override
    public void deletePlot(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");

        Log.debug(Debug.DELETE_PLOT, plot.getOwner().getName());

        plot.getAllPlayersInside().forEach(superiorPlayer -> {
            MenuView<?, ?> openedView = superiorPlayer.getOpenedView();
            if (openedView != null)
                openedView.closeView();

            plot.removeEffects(superiorPlayer);

            superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
            Message.PLOT_GOT_DELETED_WHILE_INSIDE.send(superiorPlayer);
        });

        this.plotsContainer.removePlot(plot);

        // Delete plot from database
        if (pluginDisable) {
            PlotsDatabaseBridge.deletePlot(plot);
        } else {
            BukkitExecutor.data(() -> PlotsDatabaseBridge.deletePlot(plot));
        }

        this.dragonBattleService.get().stopEnderDragonBattle(plot);
    }

    @Override
    public Plot getPlot(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return superiorPlayer.getPlot();
    }

    @Override
    public Plot getPlot(int index, SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        return this.plotsContainer.getPlotAtPosition(index, sortingType);
    }

    @Override
    public int getPlotPosition(Plot plot, SortingType sortingType) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        return this.plotsContainer.getPlotPosition(plot, sortingType);
    }

    @Override
    public Plot getPlot(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid parameter cannot be null.");
        SuperiorPlayer superiorPlayer = plugin.getPlayers().getPlayersContainer().getSuperiorPlayer(uuid);
        return superiorPlayer == null ? null : getPlot(superiorPlayer);
    }

    @Override
    public Plot getPlotByUUID(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid parameter cannot be null.");
        return this.plotsContainer.getPlotByUUID(uuid);
    }

    @Override
    public Plot getPlot(String plotName) {
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        String inputName = Formatters.STRIP_COLOR_FORMATTER.format(plotName);
        return getPlots().stream().filter(plot -> plot.getRawName().equalsIgnoreCase(inputName)).findFirst().orElse(null);
    }

    @Override
    public Plot getPlotAt(Location location) {
        if (location == null)
            return null;

        if (spawnPlot != null && spawnPlot.isInside(location))
            return spawnPlot;

        return this.plotsContainer.getPlotAt(location);
    }

    @Override
    public Plot getPlotAt(Chunk chunk) {
        if (chunk == null)
            return null;

        Plot plot;

        Location corner = chunk.getBlock(0, 100, 0).getLocation();
        if ((plot = getPlotAt(corner)) != null)
            return plot;

        corner = chunk.getBlock(15, 100, 0).getLocation();
        if ((plot = getPlotAt(corner)) != null)
            return plot;

        corner = chunk.getBlock(0, 100, 15).getLocation();
        if ((plot = getPlotAt(corner)) != null)
            return plot;

        corner = chunk.getBlock(15, 100, 15).getLocation();
        if ((plot = getPlotAt(corner)) != null)
            return plot;

        return null;
    }

    @Override
    public void transferPlot(UUID oldOwner, UUID newOwner) {
        // Do nothing.
    }

    @Override
    public int getSize() {
        return this.plotsContainer.getPlotsAmount();
    }

    @Override
    public void sortPlots(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        sortPlots(sortingType, null);
    }

    public void forceSortPlots(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        setForceSort(true);
        sortPlots(sortingType, null);
    }

    @Override
    public void sortPlots(SortingType sortingType, Runnable onFinish) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");

        Log.debug(Debug.SORT_PLOTS, sortingType.getName());

        this.plotsContainer.sortPlots(sortingType, forceSort, () -> {
            plugin.getMenus().refreshTopPlots(sortingType);
            if (onFinish != null)
                onFinish.run();
        });

        forceSort = false;
    }

    public void setForceSort(boolean forceSort) {
        this.forceSort = forceSort;
    }

    @Override
    public Plot getSpawnPlot() {
        if (spawnPlot == null)
            updateSpawn();

        return spawnPlot;
    }

    @Override
    public World getPlotsWorld(Plot plot, World.Environment environment) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        return plugin.getProviders().getWorldsProvider().getPlotsWorld(plot, environment);
    }

    @Override
    public WorldInfo getPlotsWorldInfo(Plot plot, World.Environment environment) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        WorldsProvider worldsProvider = plugin.getProviders().getWorldsProvider();

        if (worldsProvider instanceof LazyWorldsProvider)
            return ((LazyWorldsProvider) worldsProvider).getPlotsWorldInfo(plot, environment);

        World world = this.getPlotsWorld(plot, environment);
        return world == null ? null : WorldInfo.of(world);
    }

    @Nullable
    @Override
    public WorldInfo getPlotsWorldInfo(Plot plot, String worldName) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(worldName, "worldName parameter cannot be null.");

        WorldsProvider worldsProvider = plugin.getProviders().getWorldsProvider();

        if (worldsProvider instanceof LazyWorldsProvider)
            return ((LazyWorldsProvider) worldsProvider).getPlotsWorldInfo(plot, worldName);

        World world = Bukkit.getWorld(worldName);
        return world == null || !isPlotsWorld(world) ? null : WorldInfo.of(world);
    }

    @Override
    public boolean isPlotsWorld(World world) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        return customWorlds.contains(world.getUID()) || plugin.getProviders().getWorldsProvider().isPlotsWorld(world);
    }

    @Override
    public void registerPlotWorld(World world) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        customWorlds.add(world.getUID());
    }

    @Override
    public List<World> getRegisteredWorlds() {
        return new SequentialListBuilder<World>().build(customWorlds, Bukkit::getWorld);
    }

    @Override
    @Deprecated
    public List<UUID> getAllPlots(SortingType sortingType) {
        return new SequentialListBuilder<UUID>()
                .build(getPlots(sortingType), PLOT_OWNERS_MAPPER);
    }

    @Override
    public List<Plot> getPlots() {
        return this.plotsContainer.getPlotsUnsorted();
    }

    @Override
    public List<Plot> getPlots(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        return this.plotsContainer.getSortedPlots(sortingType);
    }

    @Override
    @Deprecated
    public int getBlockAmount(Block block) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        return getBlockAmount(block.getLocation());
    }

    @Override
    @Deprecated
    public int getBlockAmount(Location location) {
        return plugin.getStackedBlocks().getStackedBlockAmount(location);
    }

    @Override
    @Deprecated
    public void setBlockAmount(Block block, int amount) {
        plugin.getStackedBlocks().setStackedBlock(block, amount);
    }

    @Override
    public List<Location> getStackedBlocks() {
        return new SequentialListBuilder<Location>().build(plugin.getStackedBlocks().getStackedBlocks().keySet());
    }

    @Override
    public void calcAllPlots() {
        calcAllPlots(null);
    }

    @Override
    public void calcAllPlots(Runnable callback) {
        Log.debug(Debug.CALCULATE_ALL_PLOTS);

        List<Plot> plots = new ArrayList<>();

        {
            for (Plot plot : this.plotsContainer.getPlotsUnsorted()) {
                if (!plot.isBeingRecalculated())
                    plots.add(plot);
            }
        }

        for (int i = 0; i < plots.size(); i++) {
            plots.get(i).calcPlotWorth(null, i + 1 < plots.size() ? null : callback);
        }
    }

    @Override
    public void addPlotToPurge(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(plot.getOwner(), "plot's owner cannot be null.");

        Log.debug(Debug.PURGE_PLOT, plot.getOwner().getName());

        this.plotsPurger.schedulePlotPurge(plot);
    }

    @Override
    public void removePlotFromPurge(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(plot.getOwner(), "plot's owner cannot be null.");

        Log.debug(Debug.UNPURGE_PLOT, plot.getOwner().getName());

        this.plotsPurger.unschedulePlotPurge(plot);
    }

    @Override
    public boolean isPlotPurge(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(plot.getOwner(), "plot's owner cannot be null.");
        return this.plotsPurger.isPlotPurgeScheduled(plot);
    }

    @Override
    public List<Plot> getPlotsToPurge() {
        return this.plotsPurger.getScheduledPurgedPlots();
    }

    @Override
    public void registerSortingType(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");

        Log.debug(Debug.REGISTER_SORTING_TYPE, sortingType.getName());

        if (this.plotsContainer == null) {
            pendingSortingTypes.add(sortingType);
        } else {
            this.plotsContainer.addSortingType(sortingType, true);
        }
    }

    @Override
    public BigDecimal getTotalWorth() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTimeWorthUpdate > 60000) {
            lastTimeWorthUpdate = currentTime;
            totalWorth = BigDecimal.ZERO;
            for (Plot plot : getPlots())
                totalWorth = totalWorth.add(plot.getWorth());
        }

        return totalWorth;
    }

    @Override
    public BigDecimal getTotalLevel() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTimeLevelUpdate > 60000) {
            lastTimeLevelUpdate = currentTime;
            totalLevel = BigDecimal.ZERO;
            for (Plot plot : getPlots())
                totalLevel = totalLevel.add(plot.getPlotLevel());
        }

        return totalLevel;
    }

    @Override
    public Location getLastPlotLocation() {
        return lastPlot.parse();
    }

    @Override
    public void setLastPlotLocation(Location location) {
        this.setLastPlot(new SBlockPosition(location));
    }

    @Override
    public PlotsContainer getPlotsContainer() {
        return this.plotsContainer;
    }

    @Override
    public DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }

    public UUID generatePlotUUID() {
        UUID uuid;

        do {
            uuid = UUID.randomUUID();
        } while (getPlotByUUID(uuid) != null || plugin.getPlayers().getPlayersContainer().getSuperiorPlayer(uuid) != null);

        return uuid;
    }

    public void disablePlugin() {
        this.pluginDisable = true;
        cancelAllPlotPreviews();
    }

    public boolean wasPluginDisabled() {
        return this.pluginDisable;
    }

    public void loadGrid(DatabaseResult resultSet) {
        resultSet.getString("last_plot").map(Serializers.LOCATION_SPACED_SERIALIZER::deserialize)
                .ifPresent(lastPlot -> this.lastPlot = new SBlockPosition((LazyWorldLocation) lastPlot));

        if (!lastPlot.getWorldName().equalsIgnoreCase(plugin.getSettings().getWorlds().getDefaultWorldName())) {
            lastPlot = new SBlockPosition(plugin.getSettings().getWorlds().getDefaultWorldName(),
                    lastPlot.getX(), lastPlot.getY(), lastPlot.getZ());
        }

        int maxPlotSize = resultSet.getInt("max_plot_size").orElse(plugin.getSettings().getMaxPlotSize());
        String world = resultSet.getString("world").orElse(plugin.getSettings().getWorlds().getDefaultWorldName());

        try {
            if (plugin.getSettings().getMaxPlotSize() != maxPlotSize) {
                Log.warn("You have changed the max-plot-size value without deleting database.");
                Log.warn("Restoring it to the old value...");
                plugin.getSettings().updateValue("max-plot-size", maxPlotSize);
            }

            if (!plugin.getSettings().getWorlds().getDefaultWorldName().equals(world)) {
                Log.warn("You have changed the plot-world value without deleting database.");
                Log.warn("Restoring it to the old value...");
                plugin.getSettings().updateValue("worlds.normal-world", world);
            }
        } catch (IOException error) {
            Log.error(error, "An unexpected error occurred while loading grid:");
            Bukkit.shutdown();
        }
    }

    public void savePlots() {
        List<Plot> onlinePlots = new SequentialListBuilder<Plot>()
                .filter(Objects::nonNull)
                .build(Bukkit.getOnlinePlayers(), player -> plugin.getPlayers().getSuperiorPlayer(player).getPlot());

        List<Plot> modifiedPlots = new SequentialListBuilder<Plot>()
                .filter(PlotsDatabaseBridge::isModified)
                .build(getPlots());

        if (!onlinePlots.isEmpty())
            onlinePlots.forEach(Plot::updateLastTime);

        if (!modifiedPlots.isEmpty())
            modifiedPlots.forEach(PlotsDatabaseBridge::executeFutureSaves);

        getPlots().forEach(Plot::removeEffects);
    }

    private void setLastPlot(SBlockPosition lastPlot) {
        Log.debug(Debug.SET_LAST_PLOT, lastPlot);
        this.lastPlot = lastPlot;
        GridDatabaseBridge.saveLastPlot(this, lastPlot);
    }

    private void initializeDatabaseBridge() {
        databaseBridge = plugin.getFactory().createDatabaseBridge(this);
        databaseBridge.setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
    }

}

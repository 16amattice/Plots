package com.bgsoftware.superiorskyblock.core.database;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.handlers.GridManager;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.Manager;
import com.bgsoftware.superiorskyblock.core.database.bridge.GridDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlayersDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.database.cache.DatabaseCache;
import com.bgsoftware.superiorskyblock.core.database.loader.DatabaseLoader;
import com.bgsoftware.superiorskyblock.core.database.loader.backup.BackupDatabase;
import com.bgsoftware.superiorskyblock.core.database.loader.sql.SQLDatabaseLoader;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.DatabaseLoader_V1;
import com.bgsoftware.superiorskyblock.core.database.serialization.PlotsDeserializer;
import com.bgsoftware.superiorskyblock.core.database.serialization.PlayersDeserializer;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.builder.PlotBuilderImpl;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
public class DataManager extends Manager {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private final List<DatabaseLoader> databaseLoaders = new LinkedList<>();

    public DataManager(SuperiorSkyblockPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadData() throws ManagerLoadException {
        loadDatabaseLoaders();

        runState(DatabaseLoader.State.INITIALIZE);

        runState(DatabaseLoader.State.POST_INITIALIZE);

        runState(DatabaseLoader.State.PRE_LOAD_DATA);

        if (plugin.getEventsBus().callPluginLoadDataEvent(plugin)) {
            loadPlayers();
            loadPlots();
            loadGrid();
        }

        runState(DatabaseLoader.State.POST_LOAD_DATA);

        /*
         *  Because of a bug caused leaders to be guests, I am looping through all the players and trying to fix it here.
         */

        for (SuperiorPlayer superiorPlayer : plugin.getPlayers().getAllPlayers()) {
            if (superiorPlayer.getPlotLeader().getUniqueId().equals(superiorPlayer.getUniqueId()) && superiorPlayer.getPlot() != null && !superiorPlayer.getPlayerRole().isLastRole()) {
                Log.warn("Seems like ", superiorPlayer.getName(), " is an plot leader, but have a guest role - fixing it...");
                superiorPlayer.setPlayerRole(SPlayerRole.lastRole());
            }
        }
    }

    public void addDatabaseLoader(DatabaseLoader databaseLoader) {
        this.databaseLoaders.add(databaseLoader);
    }

    public void saveDatabase(boolean async) {
        if (async && Bukkit.isPrimaryThread()) {
            BukkitExecutor.async(() -> saveDatabase(false));
            return;
        }

        try {
            //Saving grid
            GridDatabaseBridge.deleteGrid(plugin.getGrid());
            GridDatabaseBridge.insertGrid(plugin.getGrid());
        } catch (Exception error) {
            Log.error(error, "An unexpected error occurred while saving database:");
        }
    }

    public void closeConnection() {
        for (DatabaseLoader databaseLoader : databaseLoaders) {
            try {
                databaseLoader.setState(DatabaseLoader.State.SHUTDOWN);
            } catch (Throwable ignored) {
            }
        }
    }

    private void loadDatabaseLoaders() {
        addDatabaseLoader(new CopyOldDatabase());
        addDatabaseLoader(new DatabaseLoader_V1());
        addDatabaseLoader(new BackupDatabase(plugin));
        addDatabaseLoader(new SQLDatabaseLoader(plugin));
    }

    private void loadPlayers() {
        Log.info("Starting to load players...");

        DatabaseBridge playersLoader = PlayersDatabaseBridge.getGlobalPlayersBridge();

        DatabaseCache<SuperiorPlayer.Builder> databaseCache = new DatabaseCache<>();
        AtomicInteger playersCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        PlayersDeserializer.deserializeMissions(playersLoader, databaseCache);
        PlayersDeserializer.deserializePlayerSettings(playersLoader, databaseCache);
        PlayersDeserializer.deserializePersistentDataContainer(playersLoader, databaseCache);

        playersLoader.loadAllObjects("players", resultSetRaw -> {
            DatabaseResult databaseResult = new DatabaseResult(resultSetRaw);

            Optional<UUID> uuid = databaseResult.getUUID("uuid");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load player with null uuid, skipping...");
                return;
            }

            if (uuid.get().equals(CONSOLE_UUID)) {
                Log.warn("Cannot load player with uuid 0 (it is reserved to CONSOLE), skipping...");
                return;
            }

            plugin.getPlayers().getPlayersContainer().addPlayer(databaseCache.computeIfAbsentInfo(uuid.get(), SuperiorPlayer::newBuilder)
                    .setUniqueId(uuid.get())
                    .setName(databaseResult.getString("last_used_name").orElse("null"))
                    .setDisbands(databaseResult.getInt("disbands").orElse(0))
                    .setTextureValue(databaseResult.getString("last_used_skin").orElse(""))
                    .setLastTimeUpdated(databaseResult.getLong("last_time_updated").orElse(System.currentTimeMillis() / 1000))
                    .build());

            playersCount.incrementAndGet();
        });

        long endTime = System.currentTimeMillis();

        Log.info("Finished loading " + playersCount.get() + " players (Took " + (endTime - startTime) + "ms)");
    }

    private void loadPlots() {
        Log.info("Starting to load plots...");

        DatabaseBridge plotsLoader = plugin.getFactory().createDatabaseBridge((Plot) null);

        DatabaseCache<Plot.Builder> databaseCache = new DatabaseCache<>();
        AtomicInteger plotsCount = new AtomicInteger();
        long startTime = System.currentTimeMillis();

        PlotsDeserializer.deserializePlotHomes(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeMembers(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeBanned(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePlayerPermissions(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeRolePermissions(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeUpgrades(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeWarps(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeBlockLimits(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeRatings(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeMissions(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePlotFlags(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeGenerators(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeVisitors(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeEntityLimits(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeEffects(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePlotChest(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeRoleLimits(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeWarpCategories(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePlotBank(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeVisitorHomes(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePlotSettings(plotsLoader, databaseCache);
        PlotsDeserializer.deserializeBankTransactions(plotsLoader, databaseCache);
        PlotsDeserializer.deserializePersistentDataContainer(plotsLoader, databaseCache);

        plotsLoader.loadAllObjects("plots", resultSetRaw -> {
            DatabaseResult databaseResult = new DatabaseResult(resultSetRaw);

            Optional<UUID> uuid = databaseResult.getUUID("uuid");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot with invalid uuid, skipping...");
                return;
            }

            Optional<UUID> ownerUUID = databaseResult.getUUID("owner");
            if (!ownerUUID.isPresent()) {
                Log.warn("Cannot load plot with invalid owner uuid, skipping...");
                return;
            }

            SuperiorPlayer owner = plugin.getPlayers().getSuperiorPlayer(ownerUUID.get(), false);
            if (owner == null) {
                Log.warn("Cannot load plot with unrecognized owner uuid: " + ownerUUID.get() + ", skipping...");
                return;
            }

            Optional<Location> center = databaseResult.getString("center").map(Serializers.LOCATION_SERIALIZER::deserialize);
            if (!center.isPresent()) {
                Log.warn("Cannot load plot with invalid center, skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new)
                    .setOwner(owner)
                    .setUniqueId(uuid.get())
                    .setCenter(center.get())
                    .setName(databaseResult.getString("name").orElse(""))
                    .setSchematicName(databaseResult.getString("plot_type").orElse(null))
                    .setCreationTime(databaseResult.getLong("creation_time").orElse(System.currentTimeMillis() / 1000L))
                    .setDiscord(databaseResult.getString("discord").orElse("None"))
                    .setPaypal(databaseResult.getString("paypal").orElse("None"))
                    .setBonusWorth(databaseResult.getBigDecimal("worth_bonus").orElse(BigDecimal.ZERO))
                    .setBonusLevel(databaseResult.getBigDecimal("levels_bonus").orElse(BigDecimal.ZERO))
                    .setLocked(databaseResult.getBoolean("locked").orElse(false))
                    .setIgnored(databaseResult.getBoolean("ignored").orElse(false))
                    .setDescription(databaseResult.getString("description").orElse(""))
                    .setGeneratedSchematics(databaseResult.getInt("generated_schematics").orElse(0))
                    .setUnlockedWorlds(databaseResult.getInt("unlocked_worlds").orElse(0))
                    .setLastTimeUpdated(databaseResult.getLong("last_time_updated").orElse(System.currentTimeMillis() / 1000L));

            databaseResult.getString("dirty_chunks").ifPresent(dirtyChunks -> {
                PlotsDeserializer.deserializeDirtyChunks(builder, dirtyChunks);
            });

            databaseResult.getString("block_counts").ifPresent(blockCounts -> {
                PlotsDeserializer.deserializeBlockCounts(builder, blockCounts);
            });

            plugin.getGrid().getPlotsContainer().addPlot(builder.build());

            plotsCount.incrementAndGet();
        });

        long endTime = System.currentTimeMillis();

        Log.info("Finished loading " + plotsCount.get() + " plots (Took " + (endTime - startTime) + "ms)");
    }


    private void loadGrid() {
        Log.info("Starting to load grid...");

        DatabaseBridge gridLoader = plugin.getFactory().createDatabaseBridge((GridManager) null);

        gridLoader.loadAllObjects("grid",
                resultSet -> plugin.getGrid().loadGrid(new DatabaseResult(resultSet)));

        Log.info("Finished grid!");
    }

    private void runState(DatabaseLoader.State state) throws ManagerLoadException {
        for (DatabaseLoader databaseLoader : databaseLoaders) {
            databaseLoader.setState(state);
        }
    }

    private class CopyOldDatabase implements DatabaseLoader {

        @Override
        public void setState(State state) throws ManagerLoadException {
            if (state == State.INITIALIZE) {
                File oldDataFile = new File(plugin.getDataFolder(), "database.db");
                if (oldDataFile.exists()) {
                    File newDataFile = new File(plugin.getDataFolder(), "datastore/database.db");
                    newDataFile.getParentFile().mkdirs();
                    if (!oldDataFile.renameTo(newDataFile))
                        throw new ManagerLoadException("Failed to move old database file", ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);
                }
            }

        }
    }

}

package com.bgsoftware.superiorskyblock.core.database.loader.v1;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.BorderColor;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.core.Mutable;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.database.loader.MachineStateDatabaseLoader;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.BankTransactionsAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.GridAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotChestAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotWarpAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlayerAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.StackedBlockAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.WarpCategoryAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.EmptyParameterGuardDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.IDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.JsonDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.MultipleDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.RawDeserializer;
import com.bgsoftware.superiorskyblock.core.database.sql.ResultSetMapBridge;
import com.bgsoftware.superiorskyblock.core.database.sql.SQLHelper;
import com.bgsoftware.superiorskyblock.core.database.sql.StatementHolder;
import com.bgsoftware.superiorskyblock.core.database.sql.session.QueryResult;
import com.bgsoftware.superiorskyblock.core.database.sql.session.SQLSession;
import com.bgsoftware.superiorskyblock.core.database.sql.session.impl.SQLiteSession;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.plot.privilege.PlayerPrivilegeNode;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class DatabaseLoader_V1 extends MachineStateDatabaseLoader {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();
    private static final UUID CONSOLE_UUID = new UUID(0, 0);

    private static File databaseFile;
    private static SQLSession session;
    private static boolean isRemoteDatabase;

    private final List<PlayerAttributes> loadedPlayers = new ArrayList<>();
    private final List<PlotAttributes> loadedPlots = new ArrayList<>();
    private final List<StackedBlockAttributes> loadedBlocks = new ArrayList<>();
    private final List<BankTransactionsAttributes> loadedBankTransactions = new ArrayList<>();
    private final IDeserializer deserializer = new MultipleDeserializer(
            EmptyParameterGuardDeserializer.getInstance(),
            new JsonDeserializer(this),
            new RawDeserializer(this, plugin)
    );
    private GridAttributes gridAttributes;

    private boolean isOldDatabaseFormat;

    @Override
    public void setState(State state) throws ManagerLoadException {
        if (state == State.INITIALIZE)
            isOldDatabaseFormat = isDatabaseOldFormat();

        if (isOldDatabaseFormat)
            super.setState(state);
    }

    @Override
    protected void handleInitialize() {
        Log.info("[Database-Converter] Detected old database - starting to convert data...");

        session.select("players", "", new QueryResult<ResultSet>().onSuccess(resultSet -> {
            while (resultSet.next()) {
                loadedPlayers.add(loadPlayer(new ResultSetMapBridge(resultSet)));
            }
        }).onFail(QueryResult.PRINT_ERROR));

        Log.info("[Database-Converter] Found ", loadedPlayers.size(), " players in the database.");

        session.select("plots", "", new QueryResult<ResultSet>().onSuccess(resultSet -> {
            while (resultSet.next()) {
                loadedPlots.add(loadPlot(new ResultSetMapBridge(resultSet)));
            }
        }).onFail(QueryResult.PRINT_ERROR));

        Log.info("[Database-Converter] Found ", loadedPlots.size(), " plots in the database.");

        session.select("stackedBlocks", "", new QueryResult<ResultSet>().onSuccess(resultSet -> {
            while (resultSet.next()) {
                loadedBlocks.add(loadStackedBlock(new ResultSetMapBridge(resultSet)));
            }
        }).onFail(QueryResult.PRINT_ERROR));

        Log.info("[Database-Converter] Found ", loadedBlocks.size(), " stacked blocks in the database.");

        // Ignoring errors as the bankTransactions table may not exist.
        AtomicBoolean foundBankTransaction = new AtomicBoolean(false);
        session.select("bankTransactions", "", new QueryResult<ResultSet>().onSuccess(resultSet -> {
            foundBankTransaction.set(true);
            while (resultSet.next()) {
                loadedBankTransactions.add(loadBankTransaction(new ResultSetMapBridge(resultSet)));
            }
        }));

        if (foundBankTransaction.get()) {
            Log.info("[Database-Converter] Found ", loadedBankTransactions.size(), " bank transactions in the database.");
        }

        session.select("grid", "", new QueryResult<ResultSet>().onSuccess(resultSet -> {
            if (resultSet.next()) {
                gridAttributes = new GridAttributes()
                        .setValue(GridAttributes.Field.LAST_PLOT, resultSet.getString("lastPlot"))
                        .setValue(GridAttributes.Field.MAX_PLOT_SIZE, resultSet.getString("maxPlotSize"))
                        .setValue(GridAttributes.Field.WORLD, resultSet.getString("world"));
            }
        }).onFail(QueryResult.PRINT_ERROR));

        Mutable<Throwable> failedBackupError = new Mutable<>(null);

        if (!isRemoteDatabase) {
            session.closeConnection();
            if (databaseFile.renameTo(new File(databaseFile.getParentFile(), "database-bkp.db"))) {
                failedBackupError.setValue(new RuntimeException("Failed to rename file to database-bkp.db"));
            }
        }

        if (failedBackupError.getValue() != null) {
            if (!isRemoteDatabase) {
                session = new SQLiteSession(plugin, false);
                session.createConnection();
            }

            failedBackupError.setValue(null);

            session.renameTable("plots", "bkp_plots", new QueryResult<Void>()
                    .onFail(failedBackupError::setValue));

            session.renameTable("players", "bkp_players", new QueryResult<Void>()
                    .onFail(failedBackupError::setValue));

            session.renameTable("grid", "bkp_grid", new QueryResult<Void>()
                    .onFail(failedBackupError::setValue));

            session.renameTable("stackedBlocks", "bkp_stackedBlocks", new QueryResult<Void>()
                    .onFail(failedBackupError::setValue));

            session.renameTable("bankTransactions", "bkp_bankTransactions", new QueryResult<Void>()
                    .onFail(failedBackupError::setValue));
        }

        if (isRemoteDatabase)
            session.closeConnection();

        if (failedBackupError.getValue() != null) {
            Log.error(failedBackupError.getValue(), "[Database-Converter] Failed to create a backup for the database file:");
        } else {
            Log.info("[Database-Converter] Successfully created a backup for the database.");
        }
    }

    @Override
    protected void handlePostInitialize() {
        savePlayers();
        savePlots();
        saveStackedBlocks();
        saveBankTransactions();
        saveGrid();
    }

    @Override
    protected void handlePreLoadData() {
        // Do nothing.
    }

    @Override
    protected void handlePostLoadData() {
        // Do nothing.
    }

    @Override
    protected void handleShutdown() {
        // Do nothing.
    }

    private static boolean isDatabaseOldFormat() {
        isRemoteDatabase = isRemoteDatabase();

        if (!isRemoteDatabase) {
            databaseFile = new File(plugin.getDataFolder(), "datastore/database.db");

            if (!databaseFile.exists())
                return false;
        }

        session = SQLHelper.createSession(plugin, false);

        if (!session.createConnection()) {
            return false;
        }

        AtomicBoolean isOldFormat = new AtomicBoolean(true);

        session.select("stackedBlocks", "", new QueryResult<ResultSet>().onFail(error -> {
            session.closeConnection();
            isOldFormat.set(false);
        }));

        return isOldFormat.get();
    }

    private static boolean isRemoteDatabase() {
        switch (plugin.getSettings().getDatabase().getType()) {
            case "MYSQL":
            case "MARIADB":
            case "POSTGRESQL":
                return true;
            default:
                return false;
        }
    }

    private void savePlayers() {
        Log.info("[Database-Converter] Converting players...");

        StatementHolder playersQuery = new StatementHolder("REPLACE INTO {prefix}players VALUES(?,?,?,?,?)");
        StatementHolder playersMissionsQuery = new StatementHolder("REPLACE INTO {prefix}players_missions VALUES(?,?,?)");
        StatementHolder playersSettingsQuery = new StatementHolder("REPLACE INTO {prefix}players_settings VALUES(?,?,?,?,?,?)");

        for (PlayerAttributes playerAttributes : loadedPlayers) {
            insertPlayer(playerAttributes, playersQuery, playersMissionsQuery, playersSettingsQuery);
        }

        playersQuery.executeBatch(false);
        playersMissionsQuery.executeBatch(false);
        playersSettingsQuery.executeBatch(false);
    }

    private void savePlots() {
        long currentTime = System.currentTimeMillis();

        Log.info("[Database-Converter] Converting plots...");

        StatementHolder plotsQuery = new StatementHolder("REPLACE INTO {prefix}plots VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        StatementHolder plotsBanksQuery = new StatementHolder("REPLACE INTO {prefix}plots_banks VALUES(?,?,?)");
        StatementHolder plotsBansQuery = new StatementHolder("REPLACE INTO {prefix}plots_bans VALUES(?,?,?,?)");
        StatementHolder plotsBlockLimitsQuery = new StatementHolder("REPLACE INTO {prefix}plots_block_limits VALUES(?,?,?)");
        StatementHolder plotsChestsQuery = new StatementHolder("REPLACE INTO {prefix}plots_chests VALUES(?,?,?)");
        StatementHolder plotsEffectsQuery = new StatementHolder("REPLACE INTO {prefix}plots_effects VALUES(?,?,?)");
        StatementHolder plotsEntityLimitsQuery = new StatementHolder("REPLACE INTO {prefix}plots_entity_limits VALUES(?,?,?)");
        StatementHolder plotsFlagsQuery = new StatementHolder("REPLACE INTO {prefix}plots_flags VALUES(?,?,?)");
        StatementHolder plotsGeneratorsQuery = new StatementHolder("REPLACE INTO {prefix}plots_generators VALUES(?,?,?,?)");
        StatementHolder plotsHomesQuery = new StatementHolder("REPLACE INTO {prefix}plots_homes VALUES(?,?,?)");
        StatementHolder plotsMembersQuery = new StatementHolder("REPLACE INTO {prefix}plots_members VALUES(?,?,?,?)");
        StatementHolder plotsMissionsQuery = new StatementHolder("REPLACE INTO {prefix}plots_missions VALUES(?,?,?)");
        StatementHolder plotsPlayerPermissionsQuery = new StatementHolder("REPLACE INTO {prefix}plots_player_permissions VALUES(?,?,?,?)");
        StatementHolder plotsRatingsQuery = new StatementHolder("REPLACE INTO {prefix}plots_ratings VALUES(?,?,?,?)");
        StatementHolder plotsRoleLimitsQuery = new StatementHolder("REPLACE INTO {prefix}plots_role_limits VALUES(?,?,?)");
        StatementHolder plotsRolePermissionsQuery = new StatementHolder("REPLACE INTO {prefix}plots_role_permissions VALUES(?,?,?)");
        StatementHolder plotsSettingsQuery = new StatementHolder("REPLACE INTO {prefix}plots_settings VALUES(?,?,?,?,?,?,?,?,?)");
        StatementHolder plotsUpgradesQuery = new StatementHolder("REPLACE INTO {prefix}plots_upgrades VALUES(?,?,?)");
        StatementHolder plotsVisitorHomesQuery = new StatementHolder("REPLACE INTO {prefix}plots_visitor_homes VALUES(?,?,?)");
        StatementHolder plotsVisitorsQuery = new StatementHolder("REPLACE INTO {prefix}plots_visitors VALUES(?,?,?)");
        StatementHolder plotsWarpCategoriesQuery = new StatementHolder("REPLACE INTO {prefix}plots_warp_categories VALUES(?,?,?,?)");
        StatementHolder plotsWarpsQuery = new StatementHolder("REPLACE INTO {prefix}plots_warps VALUES(?,?,?,?,?,?)");

        for (PlotAttributes plotAttributes : loadedPlots) {
            insertPlot(plotAttributes, currentTime, plotsQuery, plotsBanksQuery, plotsBansQuery,
                    plotsBlockLimitsQuery, plotsChestsQuery, plotsEffectsQuery, plotsEntityLimitsQuery,
                    plotsFlagsQuery, plotsGeneratorsQuery, plotsHomesQuery, plotsMembersQuery,
                    plotsMissionsQuery, plotsPlayerPermissionsQuery, plotsRatingsQuery, plotsRoleLimitsQuery,
                    plotsRolePermissionsQuery, plotsSettingsQuery, plotsUpgradesQuery, plotsVisitorHomesQuery,
                    plotsVisitorsQuery, plotsWarpCategoriesQuery, plotsWarpsQuery);
        }

        plotsQuery.executeBatch(false);
        plotsBanksQuery.executeBatch(false);
        plotsBansQuery.executeBatch(false);
        plotsBlockLimitsQuery.executeBatch(false);
        plotsChestsQuery.executeBatch(false);
        plotsEffectsQuery.executeBatch(false);
        plotsEntityLimitsQuery.executeBatch(false);
        plotsFlagsQuery.executeBatch(false);
        plotsGeneratorsQuery.executeBatch(false);
        plotsHomesQuery.executeBatch(false);
        plotsMembersQuery.executeBatch(false);
        plotsMissionsQuery.executeBatch(false);
        plotsPlayerPermissionsQuery.executeBatch(false);
        plotsRatingsQuery.executeBatch(false);
        plotsRoleLimitsQuery.executeBatch(false);
        plotsRolePermissionsQuery.executeBatch(false);
        plotsSettingsQuery.executeBatch(false);
        plotsUpgradesQuery.executeBatch(false);
        plotsVisitorHomesQuery.executeBatch(false);
        plotsVisitorsQuery.executeBatch(false);
        plotsWarpCategoriesQuery.executeBatch(false);
        plotsWarpsQuery.executeBatch(false);
    }

    private void saveStackedBlocks() {
        Log.info("[Database-Converter] Converting stacked blocks...");

        StatementHolder insertQuery = new StatementHolder("REPLACE INTO {prefix}stacked_blocks VALUES(?,?,?)");

        for (StackedBlockAttributes stackedBlockAttributes : loadedBlocks) {
            insertQuery
                    .setObject(stackedBlockAttributes.getValue(StackedBlockAttributes.Field.LOCATION))
                    .setObject(stackedBlockAttributes.getValue(StackedBlockAttributes.Field.BLOCK_TYPE))
                    .setObject(stackedBlockAttributes.getValue(StackedBlockAttributes.Field.AMOUNT))
                    .addBatch();
        }

        insertQuery.executeBatch(false);
    }

    private void saveBankTransactions() {
        Log.info("[Database-Converter] Converting bank transactions...");

        StatementHolder insertQuery = new StatementHolder("REPLACE INTO {prefix}bank_transactions VALUES(?,?,?,?,?,?,?)");

        for (BankTransactionsAttributes bankTransactionsAttributes : loadedBankTransactions) {
            insertQuery
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.PLOT))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.PLAYER))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.BANK_ACTION))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.POSITION))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.TIME))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.FAILURE_REASON))
                    .setObject(bankTransactionsAttributes.getValue(BankTransactionsAttributes.Field.AMOUNT))
                    .addBatch();
        }

        insertQuery.executeBatch(false);
    }

    private void saveGrid() {
        if (gridAttributes == null)
            return;

        Log.info("[Database-Converter] Converting grid data...");

        new StatementHolder("DELETE FROM {prefix}grid;").execute(false);
        new StatementHolder("REPLACE INTO {prefix}grid VALUES(?,?,?)")
                .setObject(gridAttributes.getValue(GridAttributes.Field.LAST_PLOT))
                .setObject(gridAttributes.getValue(GridAttributes.Field.MAX_PLOT_SIZE))
                .setObject(gridAttributes.getValue(GridAttributes.Field.WORLD))
                .execute(false);
    }

    @SuppressWarnings("unchecked")
    private void insertPlayer(PlayerAttributes playerAttributes,
                              StatementHolder playersQuery,
                              StatementHolder playersMissionsQuery,
                              StatementHolder playersSettingsQuery) {
        String playerUUID = playerAttributes.getValue(PlayerAttributes.Field.UUID);
        playersQuery.setObject(playerUUID)
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.LAST_USED_NAME))
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.LAST_USED_SKIN))
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.DISBANDS))
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.LAST_TIME_UPDATED))
                .addBatch();
        ((Map<String, Integer>) playerAttributes.getValue(PlayerAttributes.Field.COMPLETED_MISSIONS)).forEach((missionName, finishCount) ->
                playersMissionsQuery.setObject(playerUUID)
                        .setObject(missionName.toLowerCase(Locale.ENGLISH))
                        .setObject(finishCount)
                        .addBatch());
        playersSettingsQuery.setObject(playerUUID)
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.LANGUAGE))
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.TOGGLED_PANEL))
                .setObject(((BorderColor) playerAttributes.getValue(PlayerAttributes.Field.BORDER_COLOR)).name())
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.TOGGLED_BORDER))
                .setObject(playerAttributes.getValue(PlayerAttributes.Field.PLOT_FLY))
                .addBatch();
    }

    @SuppressWarnings({"unchecked"})
    private void insertPlot(PlotAttributes plotAttributes, long currentTime,
                              StatementHolder plotsQuery, StatementHolder plotsBanksQuery,
                              StatementHolder plotsBansQuery, StatementHolder plotsBlockLimitsQuery,
                              StatementHolder plotsChestsQuery, StatementHolder plotsEffectsQuery,
                              StatementHolder plotsEntityLimitsQuery, StatementHolder plotsFlagsQuery,
                              StatementHolder plotsGeneratorsQuery, StatementHolder plotsHomesQuery,
                              StatementHolder plotsMembersQuery, StatementHolder plotsMissionsQuery,
                              StatementHolder plotsPlayerPermissionsQuery, StatementHolder plotsRatingsQuery,
                              StatementHolder plotsRoleLimitsQuery, StatementHolder plotsRolePermissionsQuery,
                              StatementHolder plotsSettingsQuery, StatementHolder plotsUpgradesQuery,
                              StatementHolder plotsVisitorHomesQuery, StatementHolder plotsVisitorsQuery,
                              StatementHolder plotsWarpCategoriesQuery, StatementHolder plotsWarpsQuery) {
        String plotUUID = plotAttributes.getValue(PlotAttributes.Field.UUID);
        plotsQuery.setObject(plotUUID)
                .setObject(plotAttributes.getValue(PlotAttributes.Field.OWNER))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.CENTER))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.CREATION_TIME))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.PLOT_TYPE))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.DISCORD))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.PAYPAL))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.WORTH_BONUS))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.LEVELS_BONUS))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.LOCKED))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.IGNORED))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.NAME))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.DESCRIPTION))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.GENERATED_SCHEMATICS))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.UNLOCKED_WORLDS))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.LAST_TIME_UPDATED))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.DIRTY_CHUNKS))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.BLOCK_COUNTS))
                .addBatch();
        plotsBanksQuery.setObject(plotUUID)
                .setObject(plotAttributes.getValue(PlotAttributes.Field.BANK_BALANCE))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.BANK_LAST_INTEREST))
                .addBatch();
        ((List<PlayerAttributes>) plotAttributes.getValue(PlotAttributes.Field.BANS)).forEach(playerAttributes ->
                plotsBansQuery.setObject(plotUUID)
                        .setObject(playerAttributes.getValue(PlayerAttributes.Field.UUID))
                        .setObject(CONSOLE_UUID.toString())
                        .setObject(currentTime)
                        .addBatch());
        ((KeyMap<Integer>) plotAttributes.getValue(PlotAttributes.Field.BLOCK_LIMITS)).forEach((key, limit) ->
                plotsBlockLimitsQuery.setObject(plotUUID)
                        .setObject(key.toString())
                        .setObject(limit)
                        .addBatch());
        ((List<PlotChestAttributes>) plotAttributes.getValue(PlotAttributes.Field.PLOT_CHESTS)).forEach(plotChestAttributes ->
                plotsChestsQuery.setObject(plotUUID)
                        .setObject(plotChestAttributes.getValue(PlotChestAttributes.Field.INDEX))
                        .setObject(plotChestAttributes.getValue(PlotChestAttributes.Field.CONTENTS))
                        .addBatch());
        ((Map<PotionEffectType, Integer>) plotAttributes.getValue(PlotAttributes.Field.EFFECTS)).forEach((type, level) ->
                plotsEffectsQuery.setObject(plotUUID)
                        .setObject(type.getName())
                        .setObject(level)
                        .addBatch());
        ((KeyMap<Integer>) plotAttributes.getValue(PlotAttributes.Field.ENTITY_LIMITS)).forEach((entity, limit) ->
                plotsEntityLimitsQuery.setObject(plotUUID)
                        .setObject(entity.toString())
                        .setObject(limit)
                        .addBatch());
        ((Map<PlotFlag, Byte>) plotAttributes.getValue(PlotAttributes.Field.PLOT_FLAGS)).forEach((plotFlag, status) ->
                plotsFlagsQuery.setObject(plotUUID)
                        .setObject(plotFlag.getName())
                        .setObject(status)
                        .addBatch());
        runOnEnvironments((KeyMap<Integer>[]) plotAttributes.getValue(PlotAttributes.Field.GENERATORS), (generatorRates, environment) ->
                generatorRates.forEach((block, rate) ->
                        plotsGeneratorsQuery.setObject(plotUUID)
                                .setObject(environment.name())
                                .setObject(block.toString())
                                .setObject(rate)
                                .addBatch()));
        runOnEnvironments((String[]) plotAttributes.getValue(PlotAttributes.Field.HOMES), (plotHome, environment) ->
                plotsHomesQuery.setObject(plotUUID)
                        .setObject(environment.name())
                        .setObject(plotHome)
                        .addBatch());
        ((List<PlayerAttributes>) plotAttributes.getValue(PlotAttributes.Field.MEMBERS)).forEach(playerAttributes ->
                plotsMembersQuery.setObject(plotUUID)
                        .setObject(playerAttributes.getValue(PlayerAttributes.Field.UUID))
                        .setObject(((PlayerRole) playerAttributes.getValue(PlayerAttributes.Field.PLOT_ROLE)).getId())
                        .setObject(currentTime)
                        .addBatch());
        ((Map<String, Integer>) plotAttributes.getValue(PlotAttributes.Field.MISSIONS)).forEach((mission, finishCount) ->
                plotsMissionsQuery.setObject(plotUUID)
                        .setObject(mission)
                        .setObject(finishCount)
                        .addBatch());
        ((Map<UUID, PlayerPrivilegeNode>) plotAttributes.getValue(PlotAttributes.Field.PLAYER_PERMISSIONS)).forEach((playerUUID, node) -> {
            for (Map.Entry<PlotPrivilege, Boolean> playerPermission : node.getCustomPermissions().entrySet())
                plotsPlayerPermissionsQuery.setObject(plotUUID)
                        .setObject(playerUUID.toString())
                        .setObject(playerPermission.getKey().getName())
                        .setObject(playerPermission.getValue())
                        .addBatch();
        });
        ((Map<UUID, Rating>) plotAttributes.getValue(PlotAttributes.Field.RATINGS)).forEach((playerUUID, rating) ->
                plotsRatingsQuery.setObject(plotUUID)
                        .setObject(playerUUID.toString())
                        .setObject(rating.getValue())
                        .setObject(currentTime)
                        .addBatch());
        ((Map<PlayerRole, Integer>) plotAttributes.getValue(PlotAttributes.Field.ROLE_LIMITS)).forEach((role, limit) ->
                plotsRoleLimitsQuery.setObject(plotUUID)
                        .setObject(role.getId())
                        .setObject(limit)
                        .addBatch());
        ((Map<PlotPrivilege, PlayerRole>) plotAttributes.getValue(PlotAttributes.Field.ROLE_PERMISSIONS)).forEach((privilege, role) ->
                plotsRolePermissionsQuery.setObject(plotUUID)
                        .setObject(role.getId())
                        .setObject(privilege.getName())
                        .addBatch());
        plotsSettingsQuery.setObject(plotUUID)
                .setObject(plotAttributes.getValue(PlotAttributes.Field.PLOT_SIZE))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.BANK_LIMIT))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.COOP_LIMIT))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.TEAM_LIMIT))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.WARPS_LIMIT))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.CROP_GROWTH_MULTIPLIER))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.SPAWNER_RATES_MULTIPLIER))
                .setObject(plotAttributes.getValue(PlotAttributes.Field.MOB_DROPS_MULTIPLIER))
                .addBatch();
        ((Map<String, Integer>) plotAttributes.getValue(PlotAttributes.Field.UPGRADES)).forEach((upgradeName, level) ->
                plotsUpgradesQuery.setObject(plotUUID)
                        .setObject(upgradeName)
                        .setObject(level)
                        .addBatch());
        String visitorHome = plotAttributes.getValue(PlotAttributes.Field.VISITOR_HOMES);
        if (visitorHome != null && !visitorHome.isEmpty())
            plotsVisitorHomesQuery.setObject(plotUUID)
                    .setObject(World.Environment.NORMAL.name())
                    .setObject(visitorHome)
                    .addBatch();
        ((List<Pair<UUID, Long>>) plotAttributes.getValue(PlotAttributes.Field.VISITORS)).forEach(visitor ->
                plotsVisitorsQuery.setObject(plotUUID)
                        .setObject(visitor.getKey().toString())
                        .setObject(visitor.getValue())
                        .addBatch());
        ((List<WarpCategoryAttributes>) plotAttributes.getValue(PlotAttributes.Field.WARP_CATEGORIES)).forEach(warpCategoryAttributes ->
                plotsWarpCategoriesQuery.setObject(plotUUID)
                        .setObject(warpCategoryAttributes.getValue(WarpCategoryAttributes.Field.NAME))
                        .setObject(warpCategoryAttributes.getValue(WarpCategoryAttributes.Field.SLOT))
                        .setObject(warpCategoryAttributes.getValue(WarpCategoryAttributes.Field.ICON))
                        .addBatch());
        ((List<PlotWarpAttributes>) plotAttributes.getValue(PlotAttributes.Field.WARPS)).forEach(plotWarpAttributes ->
                plotsWarpsQuery.setObject(plotUUID)
                        .setObject(plotWarpAttributes.getValue(PlotWarpAttributes.Field.NAME))
                        .setObject(plotWarpAttributes.getValue(PlotWarpAttributes.Field.CATEGORY))
                        .setObject(plotWarpAttributes.getValue(PlotWarpAttributes.Field.LOCATION))
                        .setObject(plotWarpAttributes.getValue(PlotWarpAttributes.Field.PRIVATE_STATUS))
                        .setObject(plotWarpAttributes.getValue(PlotWarpAttributes.Field.ICON))
                        .addBatch());
    }

    private <T> void runOnEnvironments(T[] arr, BiConsumer<T, World.Environment> consumer) {
        for (World.Environment environment : World.Environment.values()) {
            if (arr[environment.ordinal()] != null) {
                consumer.accept(arr[environment.ordinal()], environment);
            }
        }
    }

    private PlayerAttributes loadPlayer(ResultSetMapBridge resultSet) {
        PlayerRole playerRole;

        try {
            playerRole = SPlayerRole.fromId(Integer.parseInt(resultSet.get("plotRole", "-1")));
        } catch (Exception ex) {
            playerRole = SPlayerRole.of((String) resultSet.get("plotRole"));
        }

        long currentTime = System.currentTimeMillis();

        return new PlayerAttributes()
                .setValue(PlayerAttributes.Field.UUID, resultSet.get("player"))
                .setValue(PlayerAttributes.Field.PLOT_LEADER, resultSet.get("teamLeader", resultSet.get("player")))
                .setValue(PlayerAttributes.Field.LAST_USED_NAME, resultSet.get("name", "null"))
                .setValue(PlayerAttributes.Field.LAST_USED_SKIN, resultSet.get("textureValue", ""))
                .setValue(PlayerAttributes.Field.PLOT_ROLE, playerRole)
                .setValue(PlayerAttributes.Field.DISBANDS, resultSet.get("disbands", plugin.getSettings().getDisbandCount()))
                .setValue(PlayerAttributes.Field.LAST_TIME_UPDATED, resultSet.get("lastTimeStatus", currentTime / 1000))
                .setValue(PlayerAttributes.Field.COMPLETED_MISSIONS, deserializer.deserializeMissions(resultSet.get("missions", "")))
                .setValue(PlayerAttributes.Field.TOGGLED_PANEL, resultSet.get("toggledPanel", plugin.getSettings().isDefaultToggledPanel()))
                .setValue(PlayerAttributes.Field.PLOT_FLY, resultSet.get("plotFly", plugin.getSettings().isDefaultPlotFly()))
                .setValue(PlayerAttributes.Field.BORDER_COLOR, BorderColor.valueOf(resultSet.get("borderColor", plugin.getSettings().getDefaultBorderColor())))
                .setValue(PlayerAttributes.Field.LANGUAGE, resultSet.get("language", plugin.getSettings().getDefaultLanguage()))
                .setValue(PlayerAttributes.Field.TOGGLED_BORDER, resultSet.get("toggledBorder", plugin.getSettings().isDefaultWorldBorder())
                );
    }

    private PlotAttributes loadPlot(ResultSetMapBridge resultSet) {
        UUID ownerUUID = UUID.fromString((String) resultSet.get("owner"));
        UUID plotUUID;

        String uuidRaw = resultSet.get("uuid", null);
        if (Text.isBlank(uuidRaw)) {
            plotUUID = ownerUUID;
        } else {
            plotUUID = UUID.fromString(uuidRaw);
        }

        int generatedSchematics = 0;
        String generatedSchematicsRaw = resultSet.get("generatedSchematics", "0");
        try {
            generatedSchematics = Integer.parseInt(generatedSchematicsRaw);
        } catch (Exception ex) {
            if (generatedSchematicsRaw.contains("normal"))
                generatedSchematics |= 8;
            if (generatedSchematicsRaw.contains("nether"))
                generatedSchematics |= 4;
            if (generatedSchematicsRaw.contains("the_end"))
                generatedSchematics |= 3;
        }

        int unlockedWorlds = 0;
        String unlockedWorldsRaw = resultSet.get("unlockedWorlds", "0");
        try {
            unlockedWorlds = Integer.parseInt(unlockedWorldsRaw);
        } catch (Exception ex) {
            if (unlockedWorldsRaw.contains("nether"))
                unlockedWorlds |= 1;
            if (unlockedWorldsRaw.contains("the_end"))
                unlockedWorlds |= 2;
        }

        long currentTime = System.currentTimeMillis();

        return new PlotAttributes()
                .setValue(PlotAttributes.Field.UUID, plotUUID.toString())
                .setValue(PlotAttributes.Field.OWNER, ownerUUID.toString())
                .setValue(PlotAttributes.Field.CENTER, (String) resultSet.get("center"))
                .setValue(PlotAttributes.Field.CREATION_TIME, resultSet.get("creationTime", currentTime / 1000))
                .setValue(PlotAttributes.Field.PLOT_TYPE, resultSet.get("schemName", ""))
                .setValue(PlotAttributes.Field.DISCORD, resultSet.get("discord", "None"))
                .setValue(PlotAttributes.Field.PAYPAL, resultSet.get("paypal", "None"))
                .setValue(PlotAttributes.Field.WORTH_BONUS, resultSet.get("bonusWorth", ""))
                .setValue(PlotAttributes.Field.LEVELS_BONUS, resultSet.get("bonusLevel", ""))
                .setValue(PlotAttributes.Field.LOCKED, resultSet.get("locked", false))
                .setValue(PlotAttributes.Field.IGNORED, resultSet.get("ignored", false))
                .setValue(PlotAttributes.Field.NAME, resultSet.get("name", ""))
                .setValue(PlotAttributes.Field.DESCRIPTION, resultSet.get("description", ""))
                .setValue(PlotAttributes.Field.GENERATED_SCHEMATICS, generatedSchematics)
                .setValue(PlotAttributes.Field.UNLOCKED_WORLDS, unlockedWorlds)
                .setValue(PlotAttributes.Field.LAST_TIME_UPDATED, resultSet.get("lastTimeUpdate", currentTime / 1000))
                .setValue(PlotAttributes.Field.DIRTY_CHUNKS, deserializer.deserializeDirtyChunks(resultSet.get("dirtyChunks", "")))
                .setValue(PlotAttributes.Field.BLOCK_COUNTS, deserializer.deserializeBlockCounts(resultSet.get("blockCounts", "")))
                .setValue(PlotAttributes.Field.HOMES, deserializer.deserializeHomes(resultSet.get("teleportLocation", "")))
                .setValue(PlotAttributes.Field.MEMBERS, deserializer.deserializePlayers(resultSet.get("members", "")))
                .setValue(PlotAttributes.Field.BANS, deserializer.deserializePlayers(resultSet.get("banned", "")))
                .setValue(PlotAttributes.Field.PLAYER_PERMISSIONS, deserializer.deserializePlayerPerms(resultSet.get("permissionNodes", "")))
                .setValue(PlotAttributes.Field.ROLE_PERMISSIONS, deserializer.deserializeRolePerms(resultSet.get("permissionNodes", "")))
                .setValue(PlotAttributes.Field.UPGRADES, deserializer.deserializeUpgrades(resultSet.get("upgrades", "")))
                .setValue(PlotAttributes.Field.WARPS, deserializer.deserializeWarps(resultSet.get("warps", "")))
                .setValue(PlotAttributes.Field.BLOCK_LIMITS, deserializer.deserializeBlockLimits(resultSet.get("blockLimits", "")))
                .setValue(PlotAttributes.Field.RATINGS, deserializer.deserializeRatings(resultSet.get("ratings", "")))
                .setValue(PlotAttributes.Field.MISSIONS, deserializer.deserializeMissions(resultSet.get("missions", "")))
                .setValue(PlotAttributes.Field.PLOT_FLAGS, deserializer.deserializePlotFlags(resultSet.get("settings", "")))
                .setValue(PlotAttributes.Field.GENERATORS, deserializer.deserializeGenerators(resultSet.get("generator", "")))
                .setValue(PlotAttributes.Field.VISITORS, deserializer.deserializeVisitors(resultSet.get("uniqueVisitors", "")))
                .setValue(PlotAttributes.Field.ENTITY_LIMITS, deserializer.deserializeEntityLimits(resultSet.get("entityLimits", "")))
                .setValue(PlotAttributes.Field.EFFECTS, deserializer.deserializeEffects(resultSet.get("plotEffects", "")))
                .setValue(PlotAttributes.Field.PLOT_CHESTS, deserializer.deserializePlotChests(resultSet.get("plotChest", "")))
                .setValue(PlotAttributes.Field.ROLE_LIMITS, deserializer.deserializeRoleLimits(resultSet.get("roleLimits", "")))
                .setValue(PlotAttributes.Field.WARP_CATEGORIES, deserializer.deserializeWarpCategories(resultSet.get("warpCategories", "")))
                .setValue(PlotAttributes.Field.BANK_BALANCE, resultSet.get("plotBank", ""))
                .setValue(PlotAttributes.Field.BANK_LAST_INTEREST, resultSet.get("lastInterest", currentTime / 1000))
                .setValue(PlotAttributes.Field.VISITOR_HOMES, resultSet.get("visitorsLocation", ""))
                .setValue(PlotAttributes.Field.PLOT_SIZE, resultSet.get("plotSize", -1))
                .setValue(PlotAttributes.Field.TEAM_LIMIT, resultSet.get("teamLimit", -1))
                .setValue(PlotAttributes.Field.WARPS_LIMIT, resultSet.get("warpsLimit", -1))
                .setValue(PlotAttributes.Field.CROP_GROWTH_MULTIPLIER, resultSet.get("cropGrowth", -1D))
                .setValue(PlotAttributes.Field.SPAWNER_RATES_MULTIPLIER, resultSet.get("spawnerRates", -1D))
                .setValue(PlotAttributes.Field.MOB_DROPS_MULTIPLIER, resultSet.get("mobDrops", -1D))
                .setValue(PlotAttributes.Field.COOP_LIMIT, resultSet.get("coopLimit", -1))
                .setValue(PlotAttributes.Field.BANK_LIMIT, resultSet.get("bankLimit", "-2"));
    }

    private StackedBlockAttributes loadStackedBlock(ResultSetMapBridge resultSet) {
        String world = (String) resultSet.get("world");
        int x = (int) resultSet.get("x");
        int y = (int) resultSet.get("y");
        int z = (int) resultSet.get("z");
        String amount = (String) resultSet.get("amount");
        String blockType = (String) resultSet.get("item");

        return new StackedBlockAttributes()
                .setValue(StackedBlockAttributes.Field.LOCATION, world + ", " + x + ", " + y + ", " + z)
                .setValue(StackedBlockAttributes.Field.BLOCK_TYPE, blockType)
                .setValue(StackedBlockAttributes.Field.AMOUNT, amount);
    }

    private BankTransactionsAttributes loadBankTransaction(ResultSetMapBridge resultSet) {
        return new BankTransactionsAttributes()
                .setValue(BankTransactionsAttributes.Field.PLOT, resultSet.get("plot"))
                .setValue(BankTransactionsAttributes.Field.PLAYER, resultSet.get("player"))
                .setValue(BankTransactionsAttributes.Field.BANK_ACTION, resultSet.get("bankAction"))
                .setValue(BankTransactionsAttributes.Field.POSITION, resultSet.get("position"))
                .setValue(BankTransactionsAttributes.Field.TIME, resultSet.get("time"))
                .setValue(BankTransactionsAttributes.Field.FAILURE_REASON, resultSet.get("failureReason"))
                .setValue(BankTransactionsAttributes.Field.AMOUNT, resultSet.get("amount"));
    }

    public PlayerAttributes getPlayerAttributes(String uuid) {
        return loadedPlayers.stream().filter(playerAttributes ->
                        playerAttributes.getValue(PlayerAttributes.Field.UUID).equals(uuid))
                .findFirst()
                .orElse(null);
    }

}

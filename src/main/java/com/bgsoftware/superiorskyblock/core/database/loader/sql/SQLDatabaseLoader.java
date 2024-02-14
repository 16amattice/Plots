package com.bgsoftware.superiorskyblock.core.database.loader.sql;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.core.database.bridge.GridDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.database.loader.MachineStateDatabaseLoader;
import com.bgsoftware.superiorskyblock.core.database.sql.SQLHelper;
import com.bgsoftware.superiorskyblock.core.database.sql.session.QueryResult;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;

import java.sql.ResultSet;

public class SQLDatabaseLoader extends MachineStateDatabaseLoader {

    private final SuperiorSkyblockPlugin plugin;

    public SQLDatabaseLoader(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setState(State state) throws ManagerLoadException {
        if (!plugin.getFactory().hasCustomDatabaseBridge())
            super.setState(state);
    }

    protected void handleInitialize() throws ManagerLoadException {
        if (!SQLHelper.createConnection(plugin)) {
            throw new ManagerLoadException("Couldn't connect to the database.\nMake sure all information is correct.",
                    ManagerLoadException.ErrorLevel.SERVER_SHUTDOWN);
        }

        createPlotsTable();
        createPlayersTable();
        createGridTable();
        createBankTransactionsTable();
        createStackedBlocksTable();

        SQLHelper.select("grid", "", new QueryResult<ResultSet>()
                .onFail(error -> GridDatabaseBridge.insertGrid(plugin.getGrid())));
    }

    @Override
    protected void handlePostInitialize() {
        SQLHelper.createIndex("plots_bans_index", "plots_bans",
                "plot", "player");

        SQLHelper.createIndex("block_limits_index", "plots_block_limits",
                "plot", "block");

        SQLHelper.createIndex("plots_chests_index", "plots_chests",
                "plot", "`index`");

        SQLHelper.createIndex("plots_effects_index", "plots_effects",
                "plot", "effect_type");

        SQLHelper.createIndex("entity_limits_index", "plots_entity_limits",
                "plot", "entity");

        SQLHelper.createIndex("plots_flags_index", "plots_flags",
                "plot", "name");

        SQLHelper.createIndex("plots_generators_index", "plots_generators",
                "plot", "environment", "block");

        SQLHelper.createIndex("plots_homes_index", "plots_homes",
                "plot", "environment");

        SQLHelper.createIndex("plots_members_index", "plots_members",
                "plot", "player");

        SQLHelper.createIndex("plots_missions_index", "plots_missions",
                "plot", "name");

        SQLHelper.createIndex("player_permissions_index", "plots_player_permissions",
                "plot", "player", "permission");

        SQLHelper.createIndex("plots_ratings_index", "plots_ratings",
                "plot", "player");

        SQLHelper.createIndex("role_limits_index", "plots_role_limits",
                "plot", "role");

        SQLHelper.createIndex("role_permissions_index", "plots_role_permissions",
                "plot", "permission");

        SQLHelper.createIndex("plots_upgrades_index", "plots_upgrades",
                "plot", "upgrade");

        SQLHelper.createIndex("visitor_homes_index", "plots_visitor_homes",
                "plot", "environment");

        SQLHelper.createIndex("plots_visitors_index", "plots_visitors",
                "plot", "player");

        SQLHelper.createIndex("warp_categories_index", "plots_warp_categories",
                "plot", "name");

        SQLHelper.createIndex("plots_warps_index", "plots_warps",
                "plot", "name");

        SQLHelper.createIndex("players_missions_index", "players_missions",
                "player", "name");
    }

    @Override
    protected void handlePreLoadData() {
        SQLHelper.setJournalMode("MEMORY", QueryResult.EMPTY_QUERY_RESULT);
    }

    @Override
    protected void handlePostLoadData() {
        SQLHelper.setJournalMode("DELETE", QueryResult.EMPTY_QUERY_RESULT);
    }

    @Override
    protected void handleShutdown() {
        SQLHelper.close();
    }

    @SuppressWarnings("unchecked")
    private void createPlotsTable() {
        SQLHelper.createTable("plots",
                new Pair<>("uuid", "UUID PRIMARY KEY"),
                new Pair<>("owner", "UUID"),
                new Pair<>("center", "TEXT"),
                new Pair<>("creation_time", "BIGINT"),
                new Pair<>("plot_type", "TEXT"),
                new Pair<>("discord", "TEXT"),
                new Pair<>("paypal", "TEXT"),
                new Pair<>("worth_bonus", "BIG_DECIMAL"),
                new Pair<>("levels_bonus", "BIG_DECIMAL"),
                new Pair<>("locked", "BOOLEAN"),
                new Pair<>("ignored", "BOOLEAN"),
                new Pair<>("name", "TEXT"),
                new Pair<>("description", "TEXT"),
                new Pair<>("generated_schematics", "INTEGER"),
                new Pair<>("unlocked_worlds", "INTEGER"),
                new Pair<>("last_time_updated", "BIGINT"),
                new Pair<>("dirty_chunks", "LONGTEXT"),
                new Pair<>("block_counts", "LONGTEXT")
        );

        SQLHelper.modifyColumnType("plots", "dirty_chunks", "LONGTEXT");
        SQLHelper.modifyColumnType("plots", "block_counts", "LONGTEXT");

        SQLHelper.createTable("plots_banks",
                new Pair<>("plot", "UUID PRIMARY KEY"),
                new Pair<>("balance", "BIG_DECIMAL"),
                new Pair<>("last_interest_time", "BIGINT")
        );

        SQLHelper.createTable("plots_bans",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("banned_by", "UUID"),
                new Pair<>("banned_time", "BIGINT")
        );

        SQLHelper.createTable("plots_block_limits",
                new Pair<>("plot", "UUID"),
                new Pair<>("block", "UNIQUE_TEXT"),
                new Pair<>("`limit`", "INTEGER")
        );

        SQLHelper.createTable("plots_chests",
                new Pair<>("plot", "UUID"),
                new Pair<>("`index`", "INTEGER"),
                new Pair<>("contents", "LONGBLOB")
        );

        SQLHelper.modifyColumnType("plots_chests", "contents", "LONGBLOB");

        SQLHelper.createTable("plots_custom_data",
                new Pair<>("plot", "UUID PRIMARY KEY"),
                new Pair<>("data", "BLOB")
        );

        SQLHelper.createTable("plots_effects",
                new Pair<>("plot", "UUID"),
                new Pair<>("effect_type", "UNIQUE_TEXT"),
                new Pair<>("level", "INTEGER")
        );

        SQLHelper.createTable("plots_entity_limits",
                new Pair<>("plot", "UUID"),
                new Pair<>("entity", "UNIQUE_TEXT"),
                new Pair<>("`limit`", "INTEGER")
        );

        SQLHelper.createTable("plots_flags",
                new Pair<>("plot", "UUID"),
                new Pair<>("name", "UNIQUE_TEXT"),
                new Pair<>("status", "INTEGER")
        );

        SQLHelper.createTable("plots_generators",
                new Pair<>("plot", "UUID"),
                new Pair<>("environment", "VARCHAR(7)"),
                new Pair<>("block", "UNIQUE_TEXT"),
                new Pair<>("rate", "INTEGER")
        );

        SQLHelper.createTable("plots_homes",
                new Pair<>("plot", "UUID"),
                new Pair<>("environment", "VARCHAR(7)"),
                new Pair<>("location", "TEXT")
        );

        SQLHelper.createTable("plots_members",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("role", "INTEGER"),
                new Pair<>("join_time", "BIGINT")
        );

        SQLHelper.createTable("plots_missions",
                new Pair<>("plot", "UUID"),
                new Pair<>("name", "LONG_UNIQUE_TEXT"),
                new Pair<>("finish_count", "INTEGER")
        );

        SQLHelper.modifyColumnType("plots_missions", "name", "LONG_UNIQUE_TEXT");

        SQLHelper.createTable("plots_player_permissions",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("permission", "UNIQUE_TEXT"),
                new Pair<>("status", "BOOLEAN")
        );

        SQLHelper.createTable("plots_ratings",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("rating", "INTEGER"),
                new Pair<>("rating_time", "BIGINT")
        );

        SQLHelper.createTable("plots_role_limits",
                new Pair<>("plot", "UUID"),
                new Pair<>("role", "INTEGER"),
                new Pair<>("`limit`", "INTEGER")
        );

        SQLHelper.createTable("plots_role_permissions",
                new Pair<>("plot", "UUID"),
                new Pair<>("role", "INTEGER"),
                new Pair<>("permission", "UNIQUE_TEXT")
        );

        SQLHelper.createTable("plots_settings",
                new Pair<>("plot", "UUID PRIMARY KEY"),
                new Pair<>("size", "INTEGER"),
                new Pair<>("bank_limit", "BIG_DECIMAL"),
                new Pair<>("coops_limit", "INTEGER"),
                new Pair<>("members_limit", "INTEGER"),
                new Pair<>("warps_limit", "INTEGER"),
                new Pair<>("crop_growth_multiplier", "DECIMAL"),
                new Pair<>("spawner_rates_multiplier", "DECIMAL"),
                new Pair<>("mob_drops_multiplier", "DECIMAL")
        );

        // Up to 1.9.0.574, decimals would not be saved correctly in MySQL
        // This occurred because the field type was DECIMAL(10,0) instead of DECIMAL(10,2)
        // Updating the column types to "DECIMAL" again should fix the issue.
        // https://github.com/BG-Software-LLC/SuperiorSkyblock2/issues/1021
        SQLHelper.modifyColumnType("plots_settings", "crop_growth_multiplier", "DECIMAL");
        SQLHelper.modifyColumnType("plots_settings", "spawner_rates_multiplier", "DECIMAL");
        SQLHelper.modifyColumnType("plots_settings", "mob_drops_multiplier", "DECIMAL");

        SQLHelper.createTable("plots_upgrades",
                new Pair<>("plot", "UUID"),
                new Pair<>("upgrade", "LONG_UNIQUE_TEXT"),
                new Pair<>("level", "INTEGER")
        );

        SQLHelper.modifyColumnType("plots_upgrades", "upgrade", "LONG_UNIQUE_TEXT");
        SQLHelper.removePrimaryKey("plots_upgrades", "plot");

        SQLHelper.createTable("plots_visitor_homes",
                new Pair<>("plot", "UUID"),
                new Pair<>("environment", "VARCHAR(7)"),
                new Pair<>("location", "TEXT")
        );

        SQLHelper.createTable("plots_visitors",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("visit_time", "BIGINT")
        );

        SQLHelper.createTable("plots_warp_categories",
                new Pair<>("plot", "UUID"),
                new Pair<>("name", "LONG_UNIQUE_TEXT"),
                new Pair<>("slot", "INTEGER"),
                new Pair<>("icon", "TEXT")
        );

        SQLHelper.modifyColumnType("plots_warp_categories", "name", "LONG_UNIQUE_TEXT");

        SQLHelper.createTable("plots_warps",
                new Pair<>("plot", "UUID"),
                new Pair<>("name", "LONG_UNIQUE_TEXT"),
                new Pair<>("category", "TEXT"),
                new Pair<>("location", "TEXT"),
                new Pair<>("private", "BOOLEAN"),
                new Pair<>("icon", "TEXT")
        );

        SQLHelper.modifyColumnType("plots_warps", "name", "LONG_UNIQUE_TEXT");
    }

    @SuppressWarnings("unchecked")
    private void createPlayersTable() {
        SQLHelper.createTable("players",
                new Pair<>("uuid", "UUID PRIMARY KEY"),
                new Pair<>("last_used_name", "TEXT"),
                new Pair<>("last_used_skin", "TEXT"),
                new Pair<>("disbands", "INTEGER"),
                new Pair<>("last_time_updated", "BIGINT")
        );

        SQLHelper.createTable("players_custom_data",
                new Pair<>("player", "UUID PRIMARY KEY"),
                new Pair<>("data", "BLOB")
        );

        SQLHelper.createTable("players_missions",
                new Pair<>("player", "UUID"),
                new Pair<>("name", "LONG_UNIQUE_TEXT"),
                new Pair<>("finish_count", "INTEGER")
        );

        SQLHelper.modifyColumnType("players_missions", "name", "LONG_UNIQUE_TEXT");

        SQLHelper.createTable("players_settings",
                new Pair<>("player", "UUID PRIMARY KEY"),
                new Pair<>("language", "TEXT"),
                new Pair<>("toggled_panel", "BOOLEAN"),
                new Pair<>("border_color", "TEXT"),
                new Pair<>("toggled_border", "BOOLEAN"),
                new Pair<>("plot_fly", "BOOLEAN")
        );
    }

    @SuppressWarnings("unchecked")
    private void createGridTable() {
        SQLHelper.createTable("grid",
                new Pair<>("last_plot", "TEXT"),
                new Pair<>("max_plot_size", "INTEGER"),
                new Pair<>("world", "TEXT")
        );
    }

    @SuppressWarnings("unchecked")
    private void createBankTransactionsTable() {
        SQLHelper.createTable("bank_transactions",
                new Pair<>("plot", "UUID"),
                new Pair<>("player", "UUID"),
                new Pair<>("bank_action", "TEXT"),
                new Pair<>("position", "INTEGER"),
                new Pair<>("time", "BIGINT"),
                new Pair<>("failure_reason", "TEXT"),
                new Pair<>("amount", "TEXT")
        );
    }

    private void createStackedBlocksTable() {
        //noinspection unchecked
        SQLHelper.createTable("stacked_blocks",
                new Pair<>("location", "LONG_UNIQUE_TEXT PRIMARY KEY"),
                new Pair<>("block_type", "TEXT"),
                new Pair<>("amount", "INTEGER")
        );
        // Before v1.8.1.363, location column of stacked_blocks was limited to 30 chars.
        // In order to make sure all tables keep the large number, we modify the column to 255-chars long
        // each time the plugin attempts to create the table.
        // https://github.com/BG-Software-LLC/SuperiorSkyblock2/issues/730
        SQLHelper.modifyColumnType("stacked_blocks", "location", "LONG_UNIQUE_TEXT");
    }

}

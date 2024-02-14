package com.bgsoftware.superiorskyblock.api;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.config.SettingsManager;
import com.bgsoftware.superiorskyblock.api.handlers.BlockValuesManager;
import com.bgsoftware.superiorskyblock.api.handlers.CommandsManager;
import com.bgsoftware.superiorskyblock.api.handlers.FactoriesManager;
import com.bgsoftware.superiorskyblock.api.handlers.GridManager;
import com.bgsoftware.superiorskyblock.api.handlers.KeysManager;
import com.bgsoftware.superiorskyblock.api.handlers.MenusManager;
import com.bgsoftware.superiorskyblock.api.handlers.MissionsManager;
import com.bgsoftware.superiorskyblock.api.handlers.ModulesManager;
import com.bgsoftware.superiorskyblock.api.handlers.PlayersManager;
import com.bgsoftware.superiorskyblock.api.handlers.ProvidersManager;
import com.bgsoftware.superiorskyblock.api.handlers.RolesManager;
import com.bgsoftware.superiorskyblock.api.handlers.SchematicManager;
import com.bgsoftware.superiorskyblock.api.handlers.StackedBlocksManager;
import com.bgsoftware.superiorskyblock.api.handlers.UpgradesManager;
import com.bgsoftware.superiorskyblock.api.hooks.SpawnersProvider;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class SuperiorSkyblockAPI {

    private static SuperiorSkyblock plugin;

    /**
     * Private constructor to prevent people from creating an instance of this class.
     */
    private SuperiorSkyblockAPI() {

    }

    /*
     * General Methods
     */

    /**
     * Get the plugin instance.
     */
    public static SuperiorSkyblock getSuperiorSkyblock() {
        return plugin;
    }

    /**
     * Set the plugin's instance for the API.
     * Do not use this method on your own, as it may cause an undefined behavior when using the API.
     *
     * @param plugin The instance of the plugin to set to the API.
     */
    public static void setPluginInstance(SuperiorSkyblock plugin) {
        if (SuperiorSkyblockAPI.plugin != null) {
            throw new UnsupportedOperationException("You cannot initialize the plugin instance after it was initialized.");
        }

        SuperiorSkyblockAPI.plugin = plugin;
    }

    /**
     * Get the version of the API.
     * Everytime a change is made to the API, the version of it changes.
     */
    public static int getAPIVersion() {
        return 9;
    }

    /*
     *  Player Methods
     */

    /**
     * Get the superior player object from a player instance.
     */
    public static SuperiorPlayer getPlayer(Player player) {
        return plugin.getPlayers().getSuperiorPlayer(player.getUniqueId());
    }

    /**
     * Get the superior player object by a player's name.
     */
    @Nullable
    public static SuperiorPlayer getPlayer(String name) {
        return plugin.getPlayers().getSuperiorPlayer(name);
    }

    /**
     * Get the superior player object from a player's uuid.
     *
     * @param uuid player uuid
     * @return The superior player object. null if doesn't exist.
     */
    public static SuperiorPlayer getPlayer(UUID uuid) {
        return plugin.getPlayers().getSuperiorPlayer(uuid);
    }

    /*
     *  Plot Methods
     */

    /**
     * Create a new plot.
     *
     * @param superiorPlayer owner of the plot
     * @param schemName      the schematic of the plot to be pasted
     * @param bonus          The default plot bonus level
     * @param biome          The default plot biome
     * @param plotName     The plot name
     */
    public static void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName) {
        plugin.getGrid().createPlot(superiorPlayer, schemName, bonus, biome, plotName);
    }

    /**
     * Create a new plot.
     *
     * @param superiorPlayer The new owner for the plot.
     * @param schemName      The schematic that should be used.
     * @param bonus          A starting worth for the plot.
     * @param biome          A starting biome for the plot.
     * @param plotName     The name of the new plot.
     * @param offset         Should the plot have an offset for it's values? If disabled, the bonus will be given.
     */
    public static void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonus, Biome biome, String plotName, boolean offset) {
        plugin.getGrid().createPlot(superiorPlayer, schemName, bonus, biome, plotName, offset);
    }

    /**
     * Create a new plot.
     *
     * @param superiorPlayer The new owner for the plot.
     * @param schemName      The schematic that should be used.
     * @param bonusWorth     A starting worth for the plot.
     * @param bonusLevel     A starting level for the plot.
     * @param biome          A starting biome for the plot.
     * @param plotName     The name of the new plot.
     * @param offset         Should the plot have an offset for it's values? If disabled, the bonus will be given.
     */
    public static void createPlot(SuperiorPlayer superiorPlayer, String schemName, BigDecimal bonusWorth, BigDecimal bonusLevel, Biome biome, String plotName, boolean offset) {
        plugin.getGrid().createPlot(superiorPlayer, schemName, bonusWorth, bonusLevel, biome, plotName, offset);
    }

    /**
     * Delete an plot
     */
    public static void deletePlot(Plot plot) {
        plugin.getGrid().deletePlot(plot);
    }

    /**
     * Get an plot by it's name.
     */
    @Nullable
    public static Plot getPlot(String plotName) {
        return plugin.getGrid().getPlot(plotName);
    }

    /**
     * Get an plot by it's uuid.
     */
    @Nullable
    public static Plot getPlotByUUID(UUID uuid) {
        return plugin.getGrid().getPlotByUUID(uuid);
    }

    /**
     * Get the spawn plot.
     */
    public static Plot getSpawnPlot() {
        return plugin.getGrid().getSpawnPlot();
    }

    /**
     * Get the world of an plot by the world's environment.
     */
    @Nullable
    public static World getPlotsWorld(Plot plot, World.Environment environment) {
        return plugin.getGrid().getPlotsWorld(plot, environment);
    }

    /**
     * Get an plot at a location.
     */
    @Nullable
    public static Plot getPlotAt(Location location) {
        return plugin.getGrid().getPlotAt(location);
    }

    /**
     * Calculate all plot worths on the server
     */
    public static void calcAllPlots() {
        plugin.getGrid().calcAllPlots();
    }

    /*
     *  Schematic Methods
     */

    /**
     * Get a schematic object by its name
     */
    @Nullable
    public static Schematic getSchematic(String name) {
        return plugin.getSchematics().getSchematic(name);
    }

    /*
     *  Providers Methods
     */

    /**
     * Set custom spawners provider for the plugin.
     *
     * @param spawnersProvider The spawner provider to set.
     */
    public static void setSpawnersProvider(SpawnersProvider spawnersProvider) {
        plugin.getProviders().setSpawnersProvider(spawnersProvider);
    }

    /*
     *  Commands Methods
     */

    /**
     * Register a sub-command.
     *
     * @param superiorCommand The sub command to register.
     */
    public static void registerCommand(SuperiorCommand superiorCommand) {
        plugin.getCommands().registerCommand(superiorCommand);
    }

    /*
     *  Main Method
     */

    /**
     * Get the grid of the core.
     */
    public static GridManager getGrid() {
        return plugin.getGrid();
    }

    /**
     * Get the stacked-blocks manager of the core.
     */
    public static StackedBlocksManager getStackedBlocks() {
        return plugin.getStackedBlocks();
    }

    /**
     * Get the blocks manager of the core.
     */
    public static BlockValuesManager getBlockValues() {
        return plugin.getBlockValues();
    }

    /**
     * Get the schematics manager of the core.
     */
    public static SchematicManager getSchematics() {
        return plugin.getSchematics();
    }

    /**
     * Get the players manager of the core.
     */
    public static PlayersManager getPlayers() {
        return plugin.getPlayers();
    }

    /**
     * Get the roles manager of the core.
     */
    public static RolesManager getRoles() {
        return plugin.getRoles();
    }

    /**
     * Get the missions manager of the core.
     */
    public static MissionsManager getMissions() {
        return plugin.getMissions();
    }

    /**
     * Get the menus manager of the core.
     */
    public static MenusManager getMenus() {
        return plugin.getMenus();
    }

    /**
     * Get the keys manager of the core.
     */
    public static KeysManager getKeys() {
        return plugin.getKeys();
    }

    /**
     * Get the providers manager of the core.
     */
    public static ProvidersManager getProviders() {
        return plugin.getProviders();
    }

    /**
     * Get the upgrades manager of the core.
     */
    public static UpgradesManager getUpgrades() {
        return plugin.getUpgrades();
    }

    /**
     * Get the commands manager of the core.
     */
    public static CommandsManager getCommands() {
        return plugin.getCommands();
    }

    /**
     * Get the settings of the plugin.
     */
    public static SettingsManager getSettings() {
        return plugin.getSettings();
    }

    /**
     * Get the objects factory of the plugin.
     */
    public static FactoriesManager getFactory() {
        return plugin.getFactory();
    }

    /**
     * Get the modules manager of the plugin.
     */
    public static ModulesManager getModules() {
        return plugin.getModules();
    }

}

package com.bgsoftware.superiorskyblock.core.database.bridge;

import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridgeMode;
import com.bgsoftware.superiorskyblock.api.data.DatabaseFilter;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.database.serialization.PlotsSerializer;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.plot.chunk.DirtyChunksContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class PlotsDatabaseBridge {

    private static final Map<UUID, Map<FutureSave, Set<Object>>> SAVE_METHODS_TO_BE_EXECUTED = new ConcurrentHashMap<>();

    private PlotsDatabaseBridge() {
    }

    public static void addMember(Plot plot, SuperiorPlayer superiorPlayer, long addTime) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_members",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", superiorPlayer.getUniqueId().toString()),
                new Pair<>("role", superiorPlayer.getPlayerRole().getId()),
                new Pair<>("join_time", addTime)
        ));
    }

    public static void removeMember(Plot plot, SuperiorPlayer superiorPlayer) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_members",
                createFilter("plot", plot, new Pair<>("player", superiorPlayer.getUniqueId().toString()))
        ));
    }

    public static void saveMemberRole(Plot plot, SuperiorPlayer superiorPlayer) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_members",
                createFilter("plot", plot, new Pair<>("player", superiorPlayer.getUniqueId().toString())),
                new Pair<>("role", superiorPlayer.getPlayerRole().getId())
        ));
    }

    public static void addBannedPlayer(Plot plot, SuperiorPlayer superiorPlayer, UUID banner, long banTime) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_bans",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", superiorPlayer.getUniqueId().toString()),
                new Pair<>("banned_by", banner.toString()),
                new Pair<>("banned_time", banTime)
        ));
    }

    public static void removeBannedPlayer(Plot plot, SuperiorPlayer superiorPlayer) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_bans",
                createFilter("plot", plot, new Pair<>("player", superiorPlayer.getUniqueId().toString()))
        ));
    }

    public static void saveCoopLimit(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("coops_limit", plot.getCoopLimit())
        ));
    }

    public static void savePlotHome(Plot plot, World.Environment environment, Location location) {
        if (location == null) {
            runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_homes",
                    createFilter("plot", plot, new Pair<>("environment", environment.name()))
            ));
        } else {
            runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_homes",
                    new Pair<>("plot", plot.getUniqueId().toString()),
                    new Pair<>("environment", environment.name()),
                    new Pair<>("location", Serializers.LOCATION_SERIALIZER.serialize(location))
            ));
        }
    }

    public static void saveVisitorLocation(Plot plot, World.Environment environment, Location location) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_visitor_homes",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("environment", environment.name()),
                new Pair<>("location", Serializers.LOCATION_SERIALIZER.serialize(location))
        ));
    }

    public static void removeVisitorLocation(Plot plot, World.Environment environment) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_visitor_homes",
                createFilter("plot", plot, new Pair<>("environment", environment.name()))
        ));
    }

    public static void saveUnlockedWorlds(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("unlocked_worlds", plot.getUnlockedWorldsFlag())
        ));
    }

    public static void savePlayerPermission(Plot plot, SuperiorPlayer superiorPlayer, PlotPrivilege privilege,
                                            boolean status) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_player_permissions",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", superiorPlayer.getUniqueId().toString()),
                new Pair<>("permission", privilege.getName()),
                new Pair<>("status", status)
        ));
    }

    public static void clearPlayerPermission(Plot plot, SuperiorPlayer superiorPlayer) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_player_permissions",
                createFilter("plot", plot, new Pair<>("player", superiorPlayer.getUniqueId().toString()))
        ));
    }

    public static void saveRolePermission(Plot plot, PlayerRole playerRole, PlotPrivilege privilege) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_role_permissions",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("role", playerRole.getId()),
                new Pair<>("permission", privilege.getName())
        ));
    }

    public static void clearRolePermissions(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_role_permissions",
                createFilter("plot", plot)));
    }

    public static void saveName(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("name", plot.getName())
        ));
    }

    public static void saveDescription(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("description", plot.getDescription())
        ));
    }

    public static void saveSize(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("size", plot.getPlotSize())
        ));
    }

    public static void saveDiscord(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("discord", plot.getDiscord())
        ));
    }

    public static void savePaypal(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("paypal", plot.getPaypal())
        ));
    }

    public static void saveLockedStatus(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("locked", plot.isLocked())
        ));
    }

    public static void saveIgnoredStatus(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("ignored", plot.isIgnored())
        ));
    }

    public static void saveLastTimeUpdate(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("last_time_updated", plot.getLastTimeUpdate())
        ));
    }

    public static void saveBankLimit(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("bank_limit", plot.getBankLimit() + "")
        ));
    }

    public static void saveBonusWorth(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("worth_bonus", plot.getBonusWorth() + "")
        ));
    }

    public static void saveBonusLevel(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("levels_bonus", plot.getBonusLevel() + "")
        ));
    }

    public static void saveUpgrade(Plot plot, Upgrade upgrade, int level) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_upgrades",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("upgrade", upgrade.getName()),
                new Pair<>("level", level)
        ));
    }

    public static void saveCropGrowth(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("crop_growth_multiplier", plot.getCropGrowthMultiplier())
        ));
    }

    public static void saveSpawnerRates(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("spawner_rates_multiplier", plot.getSpawnerRatesMultiplier())
        ));
    }

    public static void saveMobDrops(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("mob_drops_multiplier", plot.getMobDropsMultiplier())
        ));
    }

    public static void saveBlockLimit(Plot plot, Key block, int limit) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_block_limits",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("block", block.toString()),
                new Pair<>("limit", limit)
        ));
    }

    public static void clearBlockLimits(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_block_limits",
                createFilter("plot", plot)));
    }

    public static void removeBlockLimit(Plot plot, Key block) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_block_limits",
                createFilter("plot", plot, new Pair<>("block", block.toString()))
        ));
    }

    public static void saveEntityLimit(Plot plot, Key entityType, int limit) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_entity_limits",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("entity", entityType.toString()),
                new Pair<>("limit", limit)
        ));
    }

    public static void clearEntityLimits(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_entity_limits",
                createFilter("plot", plot)));
    }

    public static void saveTeamLimit(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("members_limit", plot.getTeamLimit())
        ));
    }

    public static void saveWarpsLimit(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_settings",
                createFilter("plot", plot),
                new Pair<>("warps_limit", plot.getWarpsLimit())
        ));
    }

    public static void savePlotEffect(Plot plot, PotionEffectType potionEffectType, int level) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_effects",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("effect_type", potionEffectType.getName()),
                new Pair<>("level", level)
        ));
    }

    public static void removePlotEffect(Plot plot, PotionEffectType potionEffectType) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_effects",
                createFilter("plot", plot, new Pair<>("effect_type", potionEffectType.getName()))
        ));
    }

    public static void clearPlotEffects(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_effects",
                createFilter("plot", plot)));
    }

    public static void saveRoleLimit(Plot plot, PlayerRole playerRole, int limit) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_role_limits",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("role", playerRole.getId()),
                new Pair<>("limit", limit)
        ));
    }

    public static void removeRoleLimit(Plot plot, PlayerRole playerRole) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_role_limits",
                createFilter("plot", plot, new Pair<>("role", playerRole.getId()))
        ));
    }

    public static void saveWarp(Plot plot, PlotWarp plotWarp) {
        WarpCategory category = plotWarp.getCategory();
        ItemStack icon = plotWarp.getRawIcon();
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_warps",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("name", plotWarp.getName()),
                new Pair<>("category", category == null ? "" : category.getName()),
                new Pair<>("location", Serializers.LOCATION_SERIALIZER.serialize(plotWarp.getLocation())),
                new Pair<>("private", plotWarp.hasPrivateFlag()),
                new Pair<>("icon", Serializers.ITEM_STACK_SERIALIZER.serialize(icon))
        ));
    }

    public static void updateWarpName(Plot plot, PlotWarp plotWarp, String oldName) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warps",
                createFilter("plot", plot, new Pair<>("name", oldName)),
                new Pair<>("name", plotWarp.getName())
        ));
    }

    public static void updateWarpLocation(Plot plot, PlotWarp plotWarp) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warps",
                createFilter("plot", plot, new Pair<>("name", plotWarp.getName())),
                new Pair<>("location", Serializers.LOCATION_SERIALIZER.serialize(plotWarp.getLocation()))
        ));
    }

    public static void updateWarpPrivateStatus(Plot plot, PlotWarp plotWarp) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warps",
                createFilter("plot", plot, new Pair<>("name", plotWarp.getName())),
                new Pair<>("private", plotWarp.hasPrivateFlag())
        ));
    }

    public static void updateWarpIcon(Plot plot, PlotWarp plotWarp) {
        ItemStack icon = plotWarp.getRawIcon();
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warps",
                createFilter("plot", plot, new Pair<>("name", plotWarp.getName())),
                new Pair<>("icon", Serializers.ITEM_STACK_SERIALIZER.serialize(icon))
        ));
    }

    public static void removeWarp(Plot plot, PlotWarp plotWarp) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_warps",
                createFilter("plot", plot, new Pair<>("name", plotWarp.getName()))
        ));
    }

    public static void saveRating(Plot plot, SuperiorPlayer superiorPlayer, Rating rating, long rateTime) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_ratings",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", superiorPlayer.getUniqueId().toString()),
                new Pair<>("rating", rating.getValue()),
                new Pair<>("rating_time", rateTime)
        ));
    }

    public static void removeRating(Plot plot, SuperiorPlayer superiorPlayer) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_ratings",
                createFilter("plot", plot, new Pair<>("player", superiorPlayer.getUniqueId().toString()))
        ));
    }

    public static void clearRatings(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_ratings",
                createFilter("plot", plot)
        ));
    }

    public static void saveMission(Plot plot, Mission<?> mission, int finishCount) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_missions",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("name", mission.getName().toLowerCase(Locale.ENGLISH)),
                new Pair<>("finish_count", finishCount)
        ));
    }

    public static void removeMission(Plot plot, Mission<?> mission) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_missions",
                createFilter("plot", plot, new Pair<>("name", mission.getName()))
        ));
    }

    public static void savePlotFlag(Plot plot, PlotFlag plotFlag, int status) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_flags",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("name", plotFlag.getName()),
                new Pair<>("status", status)
        ));
    }

    public static void removePlotFlag(Plot plot, PlotFlag plotFlag) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_flags",
                createFilter("plot", plot, new Pair<>("name", plotFlag.getName()))
        ));
    }

    public static void saveGeneratorRate(Plot plot, World.Environment environment, Key blockKey, int rate) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_generators",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("environment", environment.name()),
                new Pair<>("block", blockKey.toString()),
                new Pair<>("rate", rate)
        ));
    }

    public static void removeGeneratorRate(Plot plot, World.Environment environment, Key blockKey) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_generators",
                createFilter("plot", plot,
                        new Pair<>("environment", environment.name()),
                        new Pair<>("block", blockKey.toString()))
        ));
    }

    public static void clearGeneratorRates(Plot plot, World.Environment environment) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_generators",
                createFilter("plot", plot, new Pair<>("environment", environment.name()))
        ));
    }

    public static void saveGeneratedSchematics(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("generated_schematics", plot.getGeneratedSchematicsFlag())
        ));
    }

    public static void saveDirtyChunks(DirtyChunksContainer dirtyChunksContainer) {
        runOperationIfRunning(dirtyChunksContainer.getPlot().getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", dirtyChunksContainer.getPlot()),
                new Pair<>("dirty_chunks", PlotsSerializer.serializeDirtyChunkPositions(dirtyChunksContainer.getDirtyChunks()))
        ));
    }

    public static void saveBlockCounts(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("block_counts", PlotsSerializer.serializeBlockCounts(plot.getBlockCountsAsBigInteger()))
        ));
    }

    public static void savePlotChest(Plot plot, PlotChest plotChest) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_chests",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("index", plotChest.getIndex()),
                new Pair<>("contents", Serializers.INVENTORY_SERIALIZER.serialize(plotChest.getContents()))
        ));
    }

    public static void saveLastInterestTime(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_banks",
                createFilter("plot", plot),
                new Pair<>("last_interest_time", plot.getLastInterestTime() * 1000)
        ));
    }

    public static void saveVisitor(Plot plot, SuperiorPlayer visitor, long visitTime) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_visitors",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", visitor.getUniqueId().toString()),
                new Pair<>("visit_time", visitTime)
        ));
    }

    public static void saveWarpCategory(Plot plot, WarpCategory warpCategory) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_warp_categories",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("name", warpCategory.getName()),
                new Pair<>("slot", warpCategory.getSlot()),
                new Pair<>("icon", Serializers.ITEM_STACK_SERIALIZER.serialize(warpCategory.getRawIcon()))
        ));
    }

    public static void updateWarpCategory(Plot plot, PlotWarp plotWarp, String oldCategoryName) {
        WarpCategory category = plotWarp.getCategory();
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warps",
                createFilter("plot", plot, new Pair<>("category", oldCategoryName)),
                new Pair<>("category", category == null ? "" : category.getName())
        ));
    }

    public static void updateWarpCategoryName(Plot plot, WarpCategory warpCategory, String oldName) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warp_categories",
                createFilter("plot", plot, new Pair<>("name", oldName)),
                new Pair<>("name", warpCategory.getName())
        ));
    }

    public static void updateWarpCategorySlot(Plot plot, WarpCategory warpCategory) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warp_categories",
                createFilter("plot", plot, new Pair<>("name", warpCategory.getName())),
                new Pair<>("slot", warpCategory.getSlot())
        ));
    }

    public static void updateWarpCategoryIcon(Plot plot, WarpCategory warpCategory) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_warp_categories",
                createFilter("plot", plot, new Pair<>("name", warpCategory.getName())),
                new Pair<>("icon", Serializers.ITEM_STACK_SERIALIZER.serialize(warpCategory.getRawIcon()))
        ));
    }

    public static void removeWarpCategory(Plot plot, WarpCategory warpCategory) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.deleteObject("plots_warp_categories",
                createFilter("plot", plot, new Pair<>("name", warpCategory.getName()))
        ));
    }

    public static void savePlotLeader(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots",
                createFilter("uuid", plot),
                new Pair<>("owner", plot.getOwner().getUniqueId().toString())
        ));
    }

    public static void saveBankBalance(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.updateObject("plots_banks",
                createFilter("plot", plot),
                new Pair<>("balance", plot.getPlotBank().getBalance() + "")
        ));
    }

    public static void saveBankTransaction(Plot plot, BankTransaction bankTransaction) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("bank_transactions",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("player", bankTransaction.getPlayer() == null ? "" : bankTransaction.getPlayer().toString()),
                new Pair<>("bank_action", bankTransaction.getAction().name()),
                new Pair<>("position", bankTransaction.getPosition()),
                new Pair<>("time", bankTransaction.getTime()),
                new Pair<>("failure_reason", bankTransaction.getFailureReason()),
                new Pair<>("amount", bankTransaction.getAmount() + "")
        ));
    }

    public static void savePersistentDataContainer(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> databaseBridge.insertObject("plots_custom_data",
                new Pair<>("plot", plot.getUniqueId().toString()),
                new Pair<>("data", plot.getPersistentDataContainer().serialize())
        ));
    }

    public static void removePersistentDataContainer(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge ->
                databaseBridge.deleteObject("plots_custom_data", createFilter("plot", plot)));
    }

    public static void insertPlot(Plot plot, List<ChunkPosition> dirtyChunks) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> {
            databaseBridge.insertObject("plots",
                    new Pair<>("uuid", plot.getUniqueId().toString()),
                    new Pair<>("owner", plot.getOwner().getUniqueId().toString()),
                    new Pair<>("center", Serializers.LOCATION_SERIALIZER.serialize(plot.getCenter(World.Environment.NORMAL))),
                    new Pair<>("creation_time", plot.getCreationTime()),
                    new Pair<>("plot_type", plot.getSchematicName()),
                    new Pair<>("discord", plot.getDiscord()),
                    new Pair<>("paypal", plot.getPaypal()),
                    new Pair<>("worth_bonus", plot.getBonusWorth() + ""),
                    new Pair<>("levels_bonus", plot.getBonusLevel() + ""),
                    new Pair<>("locked", plot.isLocked()),
                    new Pair<>("ignored", plot.isIgnored()),
                    new Pair<>("name", plot.getName()),
                    new Pair<>("description", plot.getDescription()),
                    new Pair<>("generated_schematics", plot.getGeneratedSchematicsFlag()),
                    new Pair<>("unlocked_worlds", plot.getUnlockedWorldsFlag()),
                    new Pair<>("last_time_updated", System.currentTimeMillis() / 1000L),
                    new Pair<>("dirty_chunks", PlotsSerializer.serializeDirtyChunkPositions(dirtyChunks)),
                    new Pair<>("block_counts", PlotsSerializer.serializeBlockCounts(plot.getBlockCountsAsBigInteger()))
            );

            databaseBridge.insertObject("plots_banks",
                    new Pair<>("plot", plot.getUniqueId().toString()),
                    new Pair<>("balance", plot.getPlotBank().getBalance() + ""),
                    new Pair<>("last_interest_time", plot.getLastInterestTime())
            );

            databaseBridge.insertObject("plots_settings",
                    new Pair<>("plot", plot.getUniqueId().toString()),
                    new Pair<>("size", plot.getPlotSizeRaw()),
                    new Pair<>("bank_limit", plot.getBankLimitRaw() + ""),
                    new Pair<>("coops_limit", plot.getCoopLimitRaw()),
                    new Pair<>("members_limit", plot.getTeamLimitRaw()),
                    new Pair<>("warps_limit", plot.getWarpsLimitRaw()),
                    new Pair<>("crop_growth_multiplier", plot.getCropGrowthRaw()),
                    new Pair<>("spawner_rates_multiplier", plot.getSpawnerRatesRaw()),
                    new Pair<>("mob_drops_multiplier", plot.getMobDropsRaw())
            );
        });
    }

    public static void deletePlot(Plot plot) {
        runOperationIfRunning(plot.getDatabaseBridge(), databaseBridge -> {
            DatabaseFilter plotFilter = createFilter("plot", plot);
            databaseBridge.deleteObject("plots", createFilter("uuid", plot));
            databaseBridge.deleteObject("plots_banks", plotFilter);
            databaseBridge.deleteObject("plots_bans", plotFilter);
            databaseBridge.deleteObject("plots_block_limits", plotFilter);
            databaseBridge.deleteObject("plots_custom_data", plotFilter);
            databaseBridge.deleteObject("plots_chests", plotFilter);
            databaseBridge.deleteObject("plots_effects", plotFilter);
            databaseBridge.deleteObject("plots_entity_limits", plotFilter);
            databaseBridge.deleteObject("plots_flags", plotFilter);
            databaseBridge.deleteObject("plots_generators", plotFilter);
            databaseBridge.deleteObject("plots_homes", plotFilter);
            databaseBridge.deleteObject("plots_members", plotFilter);
            databaseBridge.deleteObject("plots_missions", plotFilter);
            databaseBridge.deleteObject("plots_player_permissions", plotFilter);
            databaseBridge.deleteObject("plots_ratings", plotFilter);
            databaseBridge.deleteObject("plots_role_limits", plotFilter);
            databaseBridge.deleteObject("plots_role_permissions", plotFilter);
            databaseBridge.deleteObject("plots_settings", plotFilter);
            databaseBridge.deleteObject("plots_upgrades", plotFilter);
            databaseBridge.deleteObject("plots_visitor_homes", plotFilter);
            databaseBridge.deleteObject("plots_visitors", plotFilter);
            databaseBridge.deleteObject("plots_warp_categories", plotFilter);
            databaseBridge.deleteObject("plots_warps", plotFilter);
        });
    }

    public static void markPlotChestsToBeSaved(Plot plot, PlotChest plotChest) {
        SAVE_METHODS_TO_BE_EXECUTED.computeIfAbsent(plot.getUniqueId(), u -> new EnumMap<>(FutureSave.class))
                .computeIfAbsent(FutureSave.PLOT_CHESTS, e -> new HashSet<>())
                .add(plotChest);
    }

    public static void markBlockCountsToBeSaved(Plot plot) {
        Set<Object> varsForBlockCounts = SAVE_METHODS_TO_BE_EXECUTED.computeIfAbsent(plot.getUniqueId(), u -> new EnumMap<>(FutureSave.class))
                .computeIfAbsent(FutureSave.BLOCK_COUNTS, e -> new HashSet<>());
        if (varsForBlockCounts.isEmpty())
            varsForBlockCounts.add(new Object());
    }

    public static void markPersistentDataContainerToBeSaved(Plot plot) {
        Set<Object> varsForPersistentData = SAVE_METHODS_TO_BE_EXECUTED.computeIfAbsent(plot.getUniqueId(), u -> new EnumMap<>(FutureSave.class))
                .computeIfAbsent(FutureSave.PERSISTENT_DATA, e -> new HashSet<>());
        if (varsForPersistentData.isEmpty())
            varsForPersistentData.add(new Object());
    }

    public static boolean isModified(Plot plot) {
        return SAVE_METHODS_TO_BE_EXECUTED.containsKey(plot.getUniqueId());
    }

    public static void executeFutureSaves(Plot plot) {
        Map<FutureSave, Set<Object>> futureSaves = SAVE_METHODS_TO_BE_EXECUTED.remove(plot.getUniqueId());
        if (futureSaves != null) {
            for (Map.Entry<FutureSave, Set<Object>> futureSaveEntry : futureSaves.entrySet()) {
                switch (futureSaveEntry.getKey()) {
                    case BLOCK_COUNTS:
                        saveBlockCounts(plot);
                        break;
                    case PLOT_CHESTS:
                        for (Object plotChest : futureSaveEntry.getValue())
                            savePlotChest(plot, (PlotChest) plotChest);
                        break;
                    case PERSISTENT_DATA:
                        savePersistentDataContainer(plot);
                        break;
                }
            }
        }
    }

    public static void executeFutureSaves(Plot plot, FutureSave futureSave) {
        Map<FutureSave, Set<Object>> futureSaves = SAVE_METHODS_TO_BE_EXECUTED.get(plot.getUniqueId());

        if (futureSaves == null)
            return;

        Set<Object> values = futureSaves.remove(futureSave);

        if (values == null)
            return;

        if (futureSaves.isEmpty())
            SAVE_METHODS_TO_BE_EXECUTED.remove(plot.getUniqueId());

        switch (futureSave) {
            case BLOCK_COUNTS:
                saveBlockCounts(plot);
                break;
            case PLOT_CHESTS:
                for (Object plotChest : values)
                    savePlotChest(plot, (PlotChest) plotChest);
                break;
            case PERSISTENT_DATA: {
                if (plot.isPersistentDataContainerEmpty())
                    removePersistentDataContainer(plot);
                else
                    savePersistentDataContainer(plot);
                break;
            }
        }
    }

    private static DatabaseFilter createFilter(String id, Plot plot, Pair<String, Object>... others) {
        List<Pair<String, Object>> filters = new LinkedList<>();
        filters.add(new Pair<>(id, plot.getUniqueId().toString()));
        if (others != null)
            filters.addAll(Arrays.asList(others));
        return DatabaseFilter.fromFilters(filters);
    }

    private static void runOperationIfRunning(DatabaseBridge databaseBridge, Consumer<DatabaseBridge> databaseBridgeConsumer) {
        if (databaseBridge.getDatabaseBridgeMode() == DatabaseBridgeMode.SAVE_DATA)
            databaseBridgeConsumer.accept(databaseBridge);
    }

    public enum FutureSave {

        BLOCK_COUNTS,
        PLOT_CHESTS,
        PERSISTENT_DATA

    }

}

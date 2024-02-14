package com.bgsoftware.superiorskyblock.plot;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.common.annotations.Size;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridgeMode;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.BlockChangeResult;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotBlockFlags;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.plot.PlotChunkFlags;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PermissionNode;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.bank.PlotBank;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.persistence.PersistentDataContainer;
import com.bgsoftware.superiorskyblock.api.service.message.IMessageComponent;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.upgrades.UpgradeLevel;
import com.bgsoftware.superiorskyblock.api.world.WorldInfo;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.PlotArea;
import com.bgsoftware.superiorskyblock.core.LazyWorldLocation;
import com.bgsoftware.superiorskyblock.core.LocationKey;
import com.bgsoftware.superiorskyblock.core.Mutable;
import com.bgsoftware.superiorskyblock.core.SBlockPosition;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.database.bridge.PlotsDatabaseBridge;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.events.EventsBus;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.BaseKey;
import com.bgsoftware.superiorskyblock.core.key.ConstantKeys;
import com.bgsoftware.superiorskyblock.core.key.KeyIndicator;
import com.bgsoftware.superiorskyblock.core.key.KeyMaps;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.profiler.ProfileType;
import com.bgsoftware.superiorskyblock.core.profiler.Profiler;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.core.threads.Synchronized;
import com.bgsoftware.superiorskyblock.plot.builder.PlotBuilderImpl;
import com.bgsoftware.superiorskyblock.plot.chunk.DirtyChunksContainer;
import com.bgsoftware.superiorskyblock.plot.container.value.SyncedValue;
import com.bgsoftware.superiorskyblock.plot.container.value.Value;
import com.bgsoftware.superiorskyblock.plot.flag.PlotFlags;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.plot.privilege.PlayerPrivilegeNode;
import com.bgsoftware.superiorskyblock.plot.privilege.PrivilegeNodeAbstract;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import com.bgsoftware.superiorskyblock.plot.top.SortingComparators;
import com.bgsoftware.superiorskyblock.plot.top.SortingTypes;
import com.bgsoftware.superiorskyblock.plot.upgrade.DefaultUpgradeLevel;
import com.bgsoftware.superiorskyblock.plot.upgrade.SUpgradeLevel;
import com.bgsoftware.superiorskyblock.plot.warp.SPlotWarp;
import com.bgsoftware.superiorskyblock.plot.warp.SWarpCategory;
import com.bgsoftware.superiorskyblock.mission.MissionData;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeCropGrowth;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypePlotEffects;
import com.bgsoftware.superiorskyblock.world.WorldBlocks;
import com.bgsoftware.superiorskyblock.world.chunk.ChunkLoadReason;
import com.bgsoftware.superiorskyblock.world.chunk.ChunksProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SPlot implements Plot {

    private static final UUID CONSOLE_UUID = new UUID(0, 0);
    private static final BigDecimal SYNCED_BANK_LIMIT_VALUE = BigDecimal.valueOf(-2);
    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private final DatabaseBridge databaseBridge;
    private final PlotBank plotBank;
    private final PlotCalculationAlgorithm calculationAlgorithm;
    private final PlotBlocksTrackerAlgorithm blocksTracker;
    private final PlotEntitiesTrackerAlgorithm entitiesTracker;
    private final Synchronized<BukkitTask> bankInterestTask = Synchronized.of(null);
    private final DirtyChunksContainer dirtyChunksContainer;

    /*
     * Plot Identifiers
     */
    private final UUID uuid;
    private final BlockPosition center;
    private final long creationTime;
    @Nullable
    private final String schemName;
    /*
     * Plot Upgrade Values
     */
    private final Synchronized<Value<Integer>> plotSize = Synchronized.of(Value.syncedFixed(-1));
    private final Synchronized<Value<Integer>> warpsLimit = Synchronized.of(Value.syncedFixed(-1));
    private final Synchronized<Value<Integer>> teamLimit = Synchronized.of(Value.syncedFixed(-1));
    private final Synchronized<Value<Integer>> coopLimit = Synchronized.of(Value.syncedFixed(-1));
    private final Synchronized<Value<Double>> cropGrowth = Synchronized.of(Value.syncedFixed(-1D));
    private final Synchronized<Value<Double>> spawnerRates = Synchronized.of(Value.syncedFixed(-1D));
    private final Synchronized<Value<Double>> mobDrops = Synchronized.of(Value.syncedFixed(-1D));
    private final Synchronized<Value<BigDecimal>> bankLimit = Synchronized.of(Value.syncedFixed(SYNCED_BANK_LIMIT_VALUE));
    private final Map<PlayerRole, Value<Integer>> roleLimits = new ConcurrentHashMap<>();
    private final Synchronized<EnumMap<World.Environment, KeyMap<Value<Integer>>>> cobbleGeneratorValues = Synchronized.of(new EnumMap<>(World.Environment.class));
    private final Map<PotionEffectType, Value<Integer>> plotEffects = new ConcurrentHashMap<>();
    private final KeyMap<Value<Integer>> blockLimits = KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL);
    private final KeyMap<Value<Integer>> entityLimits = KeyMaps.createConcurrentHashMap(KeyIndicator.ENTITY_TYPE);
    /*
     * Plot Player-Trackers
     */
    private final Synchronized<SortedSet<SuperiorPlayer>> members = Synchronized.of(new TreeSet<>(SortingComparators.PLAYER_NAMES_COMPARATOR));
    private final Synchronized<SortedSet<SuperiorPlayer>> playersInside = Synchronized.of(new TreeSet<>(SortingComparators.PLAYER_NAMES_COMPARATOR));
    private final Synchronized<SortedSet<UniqueVisitor>> uniqueVisitors = Synchronized.of(new TreeSet<>(SortingComparators.PAIRED_PLAYERS_NAMES_COMPARATOR));
    private final Set<SuperiorPlayer> bannedPlayers = Sets.newConcurrentHashSet();
    private final Set<SuperiorPlayer> coopPlayers = Sets.newConcurrentHashSet();
    private final Set<SuperiorPlayer> invitedPlayers = Sets.newConcurrentHashSet();
    private final Map<SuperiorPlayer, PlayerPrivilegeNode> playerPermissions = new ConcurrentHashMap<>();
    private final Map<UUID, Rating> ratings = new ConcurrentHashMap<>();
    /*
     * Plot Warps
     */
    private final Map<String, PlotWarp> warpsByName = new ConcurrentHashMap<>();
    private final Map<LocationKey, PlotWarp> warpsByLocation = new ConcurrentHashMap<>();
    private final Map<String, WarpCategory> warpCategories = new ConcurrentHashMap<>();
    /*
     * General Settings
     */
    private final Synchronized<EnumMap<World.Environment, Location>> plotHomes = Synchronized.of(new EnumMap<>(World.Environment.class));
    private final Synchronized<EnumMap<World.Environment, Location>> visitorHomes = Synchronized.of(new EnumMap<>(World.Environment.class));
    private final Map<PlotPrivilege, PlayerRole> rolePermissions = new ConcurrentHashMap<>();
    private final Map<PlotFlag, Byte> plotFlags = new ConcurrentHashMap<>();
    private final Map<String, Integer> upgrades = new ConcurrentHashMap<>();
    private final AtomicReference<BigDecimal> plotWorth = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> plotLevel = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> bonusWorth = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> bonusLevel = new AtomicReference<>(BigDecimal.ZERO);
    private final Map<Mission<?>, Integer> completedMissions = new ConcurrentHashMap<>();
    private final Synchronized<PlotChest[]> plotChests = Synchronized.of(createDefaultPlotChests());
    private final Synchronized<CompletableFuture<Biome>> biomeGetterTask = Synchronized.of(null);
    private final AtomicInteger generatedSchematics = new AtomicInteger(0);
    private final AtomicInteger unlockedWorlds = new AtomicInteger(0);
    @Nullable
    private PersistentDataContainer persistentDataContainer;
    /*
     * Plot Flags
     */
    private volatile boolean beingRecalculated = false;
    private final AtomicReference<BigInteger> currentTotalBlockCounts = new AtomicReference<>(BigInteger.ZERO);
    private volatile BigInteger lastSavedBlockCounts = BigInteger.ZERO;
    private SuperiorPlayer owner;
    private String creationTimeDate;
    /*
     * Plot Time-Trackers
     */
    private volatile long lastTimeUpdate;
    private volatile boolean currentlyActive = false;
    private volatile long lastInterest;
    private volatile long lastUpgradeTime = -1L;
    private volatile boolean giveInterestFailed = false;
    private volatile String discord;
    private volatile String paypal;
    private volatile boolean isLocked;
    private volatile boolean isTopPlotsIgnored;
    private volatile String plotName;
    private volatile String plotRawName;
    private volatile String description;
    private volatile Biome biome = null;

    public SPlot(PlotBuilderImpl builder) {
        this.uuid = builder.uuid;
        this.owner = builder.owner;

        if (this.owner != null) {
            this.owner.setPlayerRole(SPlayerRole.lastRole());
            this.owner.setPlot(this);
        }

        this.center = new SBlockPosition(builder.center);
        this.creationTime = builder.creationTime;
        this.plotName = builder.plotName;
        this.plotRawName = Formatters.STRIP_COLOR_FORMATTER.format(this.plotName);
        this.schemName = builder.plotType;
        this.discord = builder.discord;
        this.paypal = builder.paypal;
        this.bonusWorth.set(builder.bonusWorth);
        this.bonusLevel.set(builder.bonusLevel);
        this.isLocked = builder.isLocked;
        this.isTopPlotsIgnored = builder.isIgnored;
        this.description = builder.description;
        this.generatedSchematics.set(builder.generatedSchematicsMask);
        this.unlockedWorlds.set(builder.unlockedWorldsMask);
        this.lastTimeUpdate = builder.lastTimeUpdated;
        this.plotHomes.write(plotHomes -> plotHomes.putAll(builder.plotHomes));
        this.members.write(members -> {
            members.addAll(builder.members);
            members.forEach(member -> member.setPlot(this));
        });
        this.bannedPlayers.addAll(builder.bannedPlayers);
        this.playerPermissions.putAll(builder.playerPermissions);
        this.playerPermissions.values().forEach(permissionNode -> permissionNode.setPlot(this));
        this.rolePermissions.putAll(builder.rolePermissions);
        this.upgrades.putAll(builder.upgrades);
        this.blockLimits.putAll(builder.blockLimits);
        this.ratings.putAll(builder.ratings);
        this.completedMissions.putAll(builder.completedMissions);
        this.plotFlags.putAll(builder.plotFlags);
        this.cobbleGeneratorValues.write(cobbleGeneratorValues -> cobbleGeneratorValues.putAll(builder.cobbleGeneratorValues));
        this.uniqueVisitors.write(uniqueVisitors -> uniqueVisitors.addAll(builder.uniqueVisitors));
        this.entityLimits.putAll(builder.entityLimits);
        this.plotEffects.putAll(builder.plotEffects);
        PlotChest[] plotChests = new PlotChest[builder.plotChests.size()];
        for (int index = 0; index < plotChests.length; ++index) {
            plotChests[index] = SPlotChest.createChest(this, index, builder.plotChests.get(index));
        }
        this.plotChests.set(plotChests);
        this.roleLimits.putAll(builder.roleLimits);
        this.visitorHomes.set(builder.visitorHomes);
        this.plotSize.set(builder.plotSize);
        this.teamLimit.set(builder.teamLimit);
        this.warpsLimit.set(builder.warpsLimit);
        this.cropGrowth.set(builder.cropGrowth);
        this.spawnerRates.set(builder.spawnerRates);
        this.mobDrops.set(builder.mobDrops);
        this.coopLimit.set(builder.coopLimit);
        this.bankLimit.set(builder.bankLimit);
        this.lastInterest = builder.lastInterestTime;

        this.databaseBridge = plugin.getFactory().createDatabaseBridge(this);
        this.plotBank = plugin.getFactory().createPlotBank(this, this::hasGiveInterestFailed);
        this.calculationAlgorithm = plugin.getFactory().createPlotCalculationAlgorithm(this);
        this.blocksTracker = plugin.getFactory().createPlotBlocksTrackerAlgorithm(this);
        this.entitiesTracker = plugin.getFactory().createPlotEntitiesTrackerAlgorithm(this);
        this.dirtyChunksContainer = new DirtyChunksContainer(this);

        // We make sure the default world is always marked as generated.
        if (!wasSchematicGenerated(plugin.getSettings().getWorlds().getDefaultWorld())) {
            setSchematicGenerate(plugin.getSettings().getWorlds().getDefaultWorld());
        }

        builder.dirtyChunks.forEach(dirtyChunk -> {
            try {
                WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(this, dirtyChunk.getWorldName());
                if (worldInfo != null)
                    this.dirtyChunksContainer.markDirty(ChunkPosition.of(worldInfo, dirtyChunk.getX(), dirtyChunk.getZ()), false);
            } catch (IllegalStateException ignored) {
            }
        });
        if (!builder.blockCounts.isEmpty()) {
            plugin.getProviders().addPricesLoadCallback(() -> {
                builder.blockCounts.forEach((block, count) -> handleBlockPlaceInternal(block, count, 0));
                this.lastSavedBlockCounts = this.currentTotalBlockCounts.get();
            });
        }

        builder.warpCategories.forEach(warpCategoryRecord -> {
            loadWarpCategory(warpCategoryRecord.name, warpCategoryRecord.slot, warpCategoryRecord.icon);
        });

        builder.warps.forEach(warpRecord -> {
            WarpCategory warpCategory = null;

            if (!warpRecord.category.isEmpty())
                warpCategory = getWarpCategory(warpRecord.category);

            loadPlotWarp(warpRecord.name, warpRecord.location, warpCategory, warpRecord.isPrivate, warpRecord.icon);
        });

        // We want to save all the limits to the custom block keys
        plugin.getBlockValues().addCustomBlockKeys(builder.blockLimits.keySet());

        updateDatesFormatter();
        startBankInterest();
        checkMembersDuplication();
        updateOldUpgradeValues();
        updateUpgrades();
        updatePlotChests();

        this.plotBank.setBalance(builder.balance);
        builder.bankTransactions.forEach(this.plotBank::loadTransaction);
        if (builder.persistentData.length > 0)
            getPersistentDataContainer().load(builder.persistentData);

        this.databaseBridge.setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
    }

    /*
     *  General methods
     */

    private static int getGeneratedSchematicBitMask(World.Environment environment) {
        switch (environment) {
            case NORMAL:
                return 8;
            case NETHER:
                return 4;
            case THE_END:
                return 3;
            default:
                return 0;
        }
    }

    private static boolean adjustLocationToCenterOfBlock(Location location) {
        boolean changed = false;

        if (location.getX() - 0.5 != location.getBlockX()) {
            location.setX(location.getBlockX() + 0.5);
            changed = true;
        }

        if (location.getZ() - 0.5 != location.getBlockZ()) {
            location.setZ(location.getBlockZ() + 0.5);
            changed = true;
        }

        return changed;
    }

    @Override
    public SuperiorPlayer getOwner() {
        return owner;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    /*
     *  Player related methods
     */

    @Override
    public String getCreationTimeDate() {
        return creationTimeDate;
    }

    @Override
    public void updateDatesFormatter() {
        this.creationTimeDate = Formatters.DATE_FORMATTER.format(new Date(creationTime * 1000));
    }

    @Override
    public List<SuperiorPlayer> getPlotMembers(boolean includeOwner) {
        List<SuperiorPlayer> members = this.members.readAndGet(_members -> new SequentialListBuilder<SuperiorPlayer>()
                .mutable()
                .build(_members));

        if (includeOwner)
            members.add(owner);

        return Collections.unmodifiableList(members);
    }

    @Override
    public List<SuperiorPlayer> getPlotMembers(PlayerRole... playerRoles) {
        Preconditions.checkNotNull(playerRoles, "playerRoles parameter cannot be null.");

        List<PlayerRole> rolesToFilter = Arrays.asList(playerRoles);
        List<SuperiorPlayer> members = this.members.readAndGet(_members -> new SequentialListBuilder<SuperiorPlayer>()
                .mutable()
                .filter(superiorPlayer -> rolesToFilter.contains(superiorPlayer.getPlayerRole()))
                .build(_members));


        if (rolesToFilter.contains(SPlayerRole.lastRole()))
            members.add(owner);

        return Collections.unmodifiableList(members);
    }

    @Override
    public List<SuperiorPlayer> getBannedPlayers() {
        return new SequentialListBuilder<SuperiorPlayer>().build(this.bannedPlayers);
    }

    @Override
    public List<SuperiorPlayer> getPlotVisitors() {
        return getPlotVisitors(true);
    }

    @Override
    public List<SuperiorPlayer> getPlotVisitors(boolean vanishPlayers) {
        return playersInside.readAndGet(playersInside -> new SequentialListBuilder<SuperiorPlayer>()
                .filter(superiorPlayer -> !isMember(superiorPlayer) && (vanishPlayers || superiorPlayer.isShownAsOnline()))
                .build(playersInside));
    }

    @Override
    public List<SuperiorPlayer> getAllPlayersInside() {
        return playersInside.readAndGet(playersInside -> new SequentialListBuilder<SuperiorPlayer>()
                .filter(SuperiorPlayer::isOnline)
                .build(playersInside));
    }

    @Override
    public List<SuperiorPlayer> getUniqueVisitors() {
        return uniqueVisitors.readAndGet(uniqueVisitors -> new SequentialListBuilder<SuperiorPlayer>()
                .build(uniqueVisitors, UniqueVisitor::getSuperiorPlayer));
    }

    @Override
    public List<Pair<SuperiorPlayer, Long>> getUniqueVisitorsWithTimes() {
        return uniqueVisitors.readAndGet(uniqueVisitors -> new SequentialListBuilder<Pair<SuperiorPlayer, Long>>()
                .build(uniqueVisitors, UniqueVisitor::toPair));
    }

    @Override
    public void inviteMember(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.INVITE_MEMBER, owner.getName(), superiorPlayer.getName());

        invitedPlayers.add(superiorPlayer);
        superiorPlayer.addInvite(this);

        //Revoke the invite after 5 minutes
        BukkitExecutor.sync(() -> revokeInvite(superiorPlayer), 6000L);
    }

    @Override
    public void revokeInvite(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.REVOKE_INVITE, owner.getName(), superiorPlayer.getName());

        invitedPlayers.remove(superiorPlayer);
        superiorPlayer.removeInvite(this);
    }

    @Override
    public boolean isInvited(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return invitedPlayers.contains(superiorPlayer);
    }

    @Override
    public List<SuperiorPlayer> getInvitedPlayers() {
        return new SequentialListBuilder<SuperiorPlayer>().build(this.invitedPlayers);
    }

    @Override
    public void addMember(SuperiorPlayer superiorPlayer, PlayerRole playerRole) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");

        Log.debug(Debug.ADD_MEMBER, owner.getName(), superiorPlayer.getName(), playerRole);

        boolean addedNewMember = members.writeAndGet(members -> members.add(superiorPlayer));

        // This player is already an member of the plot
        if (!addedNewMember)
            return;

        // Removing player from being a coop.
        if (isCoop(superiorPlayer)) {
            removeCoop(superiorPlayer);
        }

        superiorPlayer.setPlot(this);

        if (plugin.getEventsBus().callPlayerChangeRoleEvent(superiorPlayer, playerRole)) {
            superiorPlayer.setPlayerRole(playerRole);
        } else {
            superiorPlayer.setPlayerRole(SPlayerRole.defaultRole());
        }

        plugin.getMenus().refreshMembers(this);

        updateLastTime();

        if (superiorPlayer.isOnline()) {
            updatePlotFly(superiorPlayer);
            setCurrentlyActive();
        }

        PlotsDatabaseBridge.addMember(this, superiorPlayer, System.currentTimeMillis());
    }

    @Override
    public void kickMember(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.KICK_MEMBER, owner.getName(), superiorPlayer.getName());

        boolean removedMember = members.writeAndGet(members -> members.remove(superiorPlayer));

        if (!removedMember) {
            // If the remove method failed, we iterate through all the members and remove the member manually.
            // Should fix issues if members are not in the correct order.
            // Reference: https://github.com/BG-Software-LLC/SuperiorSkyblock2/issues/734
            removedMember = members.writeAndGet(members -> members.removeIf(superiorPlayer::equals));
        }

        // This player is not a member of the plot.
        if (!removedMember)
            return;

        superiorPlayer.setPlot(null);

        superiorPlayer.runIfOnline(player -> {
            MenuView<?, ?> openedView = superiorPlayer.getOpenedView();

            if (openedView != null)
                openedView.closeView();

            if (plugin.getSettings().isTeleportOnKick() && getAllPlayersInside().contains(superiorPlayer)) {
                superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
            } else {
                updatePlotFly(superiorPlayer);
            }
        });

        plugin.getMissions().getAllMissions().stream().filter(mission -> {
            MissionData missionData = plugin.getMissions().getMissionData(mission).orElse(null);
            return missionData != null && missionData.isLeaveReset();
        }).forEach(superiorPlayer::resetMission);

        plugin.getMenus().destroyMemberManage(superiorPlayer);
        plugin.getMenus().destroyMemberRole(superiorPlayer);
        plugin.getMenus().refreshMembers(this);

        PlotsDatabaseBridge.removeMember(this, superiorPlayer);
    }

    @Override
    public boolean isMember(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return owner.equals(superiorPlayer.getPlotLeader());
    }

    @Override
    public void banMember(SuperiorPlayer superiorPlayer) {
        banMember(superiorPlayer, null);
    }

    @Override
    public void banMember(SuperiorPlayer superiorPlayer, @Nullable SuperiorPlayer whom) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.BAN_PLAYER, owner.getName(), superiorPlayer.getName(), whom);

        boolean bannedPlayer = bannedPlayers.add(superiorPlayer);

        // This player is already banned.
        if (!bannedPlayer)
            return;

        if (isMember(superiorPlayer))
            kickMember(superiorPlayer);

        Location location = superiorPlayer.getLocation();

        if (location != null && isInside(location))
            superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());

        PlotsDatabaseBridge.addBannedPlayer(this,
                superiorPlayer, whom == null ? CONSOLE_UUID : whom.getUniqueId(),
                System.currentTimeMillis());
    }

    @Override
    public void unbanMember(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.UNBAN_PLAYER, owner.getName(), superiorPlayer.getName());

        boolean unbannedPlayer = bannedPlayers.remove(superiorPlayer);

        if (unbannedPlayer)
            PlotsDatabaseBridge.removeBannedPlayer(this, superiorPlayer);
    }

    @Override
    public boolean isBanned(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return bannedPlayers.contains(superiorPlayer);
    }

    @Override
    public void addCoop(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.ADD_COOP, owner.getName(), superiorPlayer.getName());

        boolean coopPlayer = coopPlayers.add(superiorPlayer);

        if (coopPlayer)
            plugin.getMenus().refreshCoops(this);
    }

    @Override
    public void removeCoop(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.REMOVE_COOP, owner.getName(), superiorPlayer.getName());

        boolean uncoopPlayer = coopPlayers.remove(superiorPlayer);

        // This player was not coop.
        if (!uncoopPlayer)
            return;

        Location location = superiorPlayer.getLocation();

        if (isLocked() && location != null && isInside(location)) {
            MenuView<?, ?> openedView = superiorPlayer.getOpenedView();
            if (openedView != null)
                openedView.closeView();

            superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
        }

        plugin.getMenus().refreshCoops(this);
    }

    @Override
    public boolean isCoop(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return plugin.getSettings().isCoopMembers() && coopPlayers.contains(superiorPlayer);
    }

    @Override
    public List<SuperiorPlayer> getCoopPlayers() {
        return new SequentialListBuilder<SuperiorPlayer>().build(this.coopPlayers);
    }

    @Override
    public int getCoopLimit() {
        return this.coopLimit.readAndGet(Value::get);
    }

    @Override
    public int getCoopLimitRaw() {
        return this.coopLimit.readAndGet(coopLimit -> coopLimit.getRaw(-1));
    }

    @Override
    public void setCoopLimit(int coopLimit) {
        coopLimit = Math.max(0, coopLimit);

        Log.debug(Debug.SET_COOP_LIMIT, owner.getName(), coopLimit);

        // Original and new coop limit are the same
        if (coopLimit == getCoopLimitRaw())
            return;

        this.coopLimit.set(Value.fixed(coopLimit));
        PlotsDatabaseBridge.saveCoopLimit(this);
    }

    /*
     *  Location related methods
     */

    @Override
    public void setPlayerInside(SuperiorPlayer superiorPlayer, boolean inside) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        if (inside) {
            Log.debug(Debug.ENTER_PLOT, owner.getName(), superiorPlayer.getName());
        } else {
            Log.debug(Debug.LEAVE_PLOT, owner.getName(), superiorPlayer.getName());
        }

        boolean changePlayers = playersInside.writeAndGet(playersInside -> {
            if (inside)
                return playersInside.add(superiorPlayer);
            else
                return playersInside.remove(superiorPlayer);
        });

        // The players inside the player weren't changed.
        if (!changePlayers)
            return;

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_PLAYERS, this);

        if (!isMember(superiorPlayer) && superiorPlayer.isShownAsOnline()) {
            Optional<UniqueVisitor> uniqueVisitorOptional = uniqueVisitors.readAndGet(uniqueVisitors ->
                    uniqueVisitors.stream().filter(pair -> pair.getSuperiorPlayer().equals(superiorPlayer)).findFirst());

            long visitTime = System.currentTimeMillis();

            boolean updateVisitor;

            if (uniqueVisitorOptional.isPresent()) {
                uniqueVisitorOptional.get().setLastVisitTime(visitTime);
                updateVisitor = true;
            } else {
                updateVisitor = uniqueVisitors.writeAndGet(uniqueVisitors -> uniqueVisitors.add(new UniqueVisitor(superiorPlayer, visitTime)));
            }

            if (updateVisitor) {
                plugin.getMenus().refreshUniqueVisitors(this);

                PlotsDatabaseBridge.saveVisitor(this, superiorPlayer, visitTime);
            }
        }

        updateLastTime();

        plugin.getMenus().refreshVisitors(this);
    }

    @Override
    public boolean isVisitor(SuperiorPlayer superiorPlayer, boolean includeCoopStatus) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        return !isMember(superiorPlayer) && (!includeCoopStatus || !isCoop(superiorPlayer));
    }

    @Override
    public Location getCenter(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        World world = plugin.getGrid().getPlotsWorld(this, environment);

        Preconditions.checkNotNull(world, "Couldn't find world for environment " + environment + ".");

        // noinspection deprecation
        return center.parse(world).add(0.5, 0, 0.5);
    }

    @Override
    public BlockPosition getCenterPosition() {
        return center;
    }

    @Override
    public Location getTeleportLocation(World.Environment environment) {
        return this.getPlotHome(environment);
    }

    @Override
    public Map<World.Environment, Location> getTeleportLocations() {
        return this.getPlotHomes();
    }

    @Override
    public void setTeleportLocation(Location teleportLocation) {
        this.setPlotHome(teleportLocation);
    }

    @Override
    public void setTeleportLocation(World.Environment environment, @Nullable Location teleportLocation) {
        this.setPlotHome(environment, teleportLocation);
    }

    @Override
    public Location getPlotHome(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Location teleportLocation = plotHomes.readAndGet(teleportLocations -> teleportLocations.get(environment));

        if (teleportLocation == null)
            teleportLocation = getCenter(environment);

        if (teleportLocation == null)
            return null;

        World world = plugin.getGrid().getPlotsWorld(this, environment);

        teleportLocation = teleportLocation.clone();
        teleportLocation.setWorld(world);

        return teleportLocation;
    }

    @Override
    public Map<World.Environment, Location> getPlotHomes() {
        return plotHomes.readAndGet(Collections::unmodifiableMap);
    }

    @Override
    public void setPlotHome(Location homeLocation) {
        Preconditions.checkNotNull(homeLocation, "homeLocation parameter cannot be null.");
        Preconditions.checkNotNull(homeLocation.getWorld(), "homeLocation's world cannot be null.");
        setPlotHome(homeLocation.getWorld().getEnvironment(), homeLocation);
    }

    @Override
    public void setPlotHome(World.Environment environment, @Nullable Location homeLocation) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.SET_PLOT_HOME, owner.getName(), environment, homeLocation);

        Location oldHome = plotHomes.writeAndGet(plotHomes ->
                plotHomes.put(environment, homeLocation == null ? null : homeLocation.clone()));

        if (!Objects.equals(oldHome, homeLocation))
            PlotsDatabaseBridge.savePlotHome(this, environment, homeLocation);
    }

    @Override
    public Location getVisitorsLocation() {
        return getVisitorsLocation(null /* unused */);
    }

    @Nullable
    @Override
    public Location getVisitorsLocation(World.Environment unused) {
        Location visitorsLocation = this.visitorHomes.readAndGet(visitorsLocations ->
                visitorsLocations.get(plugin.getSettings().getWorlds().getDefaultWorld()));

        if (visitorsLocation == null)
            return null;

        if (adjustLocationToCenterOfBlock(visitorsLocation))
            PlotsDatabaseBridge.saveVisitorLocation(this, plugin.getSettings().getWorlds().getDefaultWorld(), visitorsLocation);

        World world = plugin.getGrid().getPlotsWorld(this, plugin.getSettings().getWorlds().getDefaultWorld());
        visitorsLocation.setWorld(world);

        return visitorsLocation.clone();
    }

    @Override
    public void setVisitorsLocation(Location visitorsLocation) {
        Log.debug(Debug.SET_VISITOR_HOME, owner.getName(), visitorsLocation);

        if (visitorsLocation == null) {
            Location oldVisitorsLocation = this.visitorHomes.writeAndGet(visitorsLocations ->
                    visitorsLocations.remove(plugin.getSettings().getWorlds().getDefaultWorld()));
            if (oldVisitorsLocation != null)
                PlotsDatabaseBridge.removeVisitorLocation(this, plugin.getSettings().getWorlds().getDefaultWorld());
        } else {
            adjustLocationToCenterOfBlock(visitorsLocation);

            Location oldVisitorsLocation = this.visitorHomes.writeAndGet(visitorsLocations ->
                    visitorsLocations.put(plugin.getSettings().getWorlds().getDefaultWorld(), visitorsLocation.clone()));

            if (!Objects.equals(oldVisitorsLocation, visitorsLocation))
                PlotsDatabaseBridge.saveVisitorLocation(this, plugin.getSettings().getWorlds().getDefaultWorld(), visitorsLocation);
        }
    }

    @Override
    public Location getMinimum() {
        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        return getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).subtract(plotDistance, 0, plotDistance);
    }

    @Override
    public BlockPosition getMinimumPosition() {
        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        return getCenterPosition().offset(-plotDistance, 0, -plotDistance);
    }

    @Override
    public Location getMinimumProtected() {
        int plotSize = getPlotSize();
        return getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).subtract(plotSize, 0, plotSize);
    }

    @Override
    public BlockPosition getMinimumProtectedPosition() {
        int plotSize = getPlotSize();
        return getCenterPosition().offset(-plotSize, 0, -plotSize);
    }

    @Override
    public Location getMaximum() {
        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        return getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).add(plotDistance, 0, plotDistance);
    }

    @Override
    public BlockPosition getMaximumPosition() {
        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        return getCenterPosition().offset(plotDistance, 0, plotDistance);
    }

    @Override
    public Location getMaximumProtected() {
        int plotSize = getPlotSize();
        return getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).add(plotSize, 0, plotSize);
    }

    @Override
    public BlockPosition getMaximumProtectedPosition() {
        int plotSize = getPlotSize();
        return getCenterPosition().offset(plotSize, 0, plotSize);
    }

    @Override
    public List<Chunk> getAllChunks() {
        return getAllChunks(0);
    }

    @Override
    public List<Chunk> getAllChunks(int flags) {
        List<Chunk> chunks = new LinkedList<>();

        for (World.Environment environment : World.Environment.values()) {
            try {
                chunks.addAll(getAllChunks(environment, flags));
            } catch (NullPointerException ignored) {
            }
        }

        return Collections.unmodifiableList(chunks);
    }

    @Override
    public List<Chunk> getAllChunks(World.Environment environment) {
        return getAllChunks(environment, 0);
    }

    @Override
    public List<Chunk> getAllChunks(World.Environment environment, @PlotChunkFlags int flags) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null");

        World world = getCenter(environment).getWorld();
        return new SequentialListBuilder<Chunk>().build(PlotUtils.getChunkCoords(this, WorldInfo.of(world), flags),
                chunkPosition -> world.getChunkAt(chunkPosition.getX(), chunkPosition.getZ()));
    }

    @Override
    @Deprecated
    public List<Chunk> getAllChunks(boolean onlyProtected) {
        return getAllChunks(onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0);
    }

    @Override
    @Deprecated
    public List<Chunk> getAllChunks(World.Environment environment, boolean onlyProtected) {
        return getAllChunks(environment, onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0);
    }

    @Override
    @Deprecated
    public List<Chunk> getAllChunks(World.Environment environment, boolean onlyProtected, boolean noEmptyChunks) {
        int flags = 0;
        if (onlyProtected) flags |= PlotChunkFlags.ONLY_PROTECTED;
        if (noEmptyChunks) flags |= PlotChunkFlags.NO_EMPTY_CHUNKS;
        return getAllChunks(environment, flags);
    }

    @Override
    public List<Chunk> getLoadedChunks() {
        return getLoadedChunks(0);
    }

    @Override
    public List<Chunk> getLoadedChunks(@PlotChunkFlags int flags) {
        List<Chunk> chunks = new LinkedList<>();

        for (World.Environment environment : World.Environment.values()) {
            try {
                chunks.addAll(getLoadedChunks(environment, flags));
            } catch (NullPointerException ignored) {
            }
        }

        return Collections.unmodifiableList(chunks);
    }

    @Override
    public List<Chunk> getLoadedChunks(World.Environment environment) {
        return getLoadedChunks(environment, 0);
    }

    @Override
    public List<Chunk> getLoadedChunks(World.Environment environment, @PlotChunkFlags int flags) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null");

        WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(this, environment);

        return new SequentialListBuilder<Chunk>().filter(Objects::nonNull).build(
                PlotUtils.getChunkCoords(this, worldInfo, flags), plugin.getNMSChunks()::getChunkIfLoaded);
    }

    @Override
    @Deprecated
    public List<Chunk> getLoadedChunks(boolean onlyProtected, boolean noEmptyChunks) {
        int flags = 0;
        if (onlyProtected) flags |= PlotChunkFlags.ONLY_PROTECTED;
        if (noEmptyChunks) flags |= PlotChunkFlags.NO_EMPTY_CHUNKS;
        return getLoadedChunks(flags);
    }

    @Override
    @Deprecated
    public List<Chunk> getLoadedChunks(World.Environment environment, boolean onlyProtected, boolean noEmptyChunks) {
        int flags = 0;
        if (onlyProtected) flags |= PlotChunkFlags.ONLY_PROTECTED;
        if (noEmptyChunks) flags |= PlotChunkFlags.NO_EMPTY_CHUNKS;
        return getLoadedChunks(environment, flags);
    }

    @Override
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment) {
        return getAllChunksAsync(environment, 0);
    }

    @Override
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, @PlotChunkFlags int flags) {
        return getAllChunksAsync(environment, flags, null);
    }

    @Override
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment,
                                                            @Nullable Consumer<Chunk> onChunkLoad) {
        return getAllChunksAsync(environment, 0, onChunkLoad);
    }

    @Override
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, int flags,
                                                            @Nullable Consumer<Chunk> onChunkLoad) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null");

        World world = getCenter(environment).getWorld();
        return PlotUtils.getAllChunksAsync(this, world, flags, ChunkLoadReason.API_REQUEST, onChunkLoad);
    }

    @Override
    @Deprecated
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, boolean onlyProtected,
                                                            @Nullable Consumer<Chunk> onChunkLoad) {
        return getAllChunksAsync(environment, onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0, onChunkLoad);
    }

    @Override
    @Deprecated
    public List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment,
                                                            boolean onlyProtected, boolean noEmptyChunks,
                                                            @Nullable Consumer<Chunk> onChunkLoad) {
        int flags = 0;
        if (onlyProtected) flags |= PlotChunkFlags.ONLY_PROTECTED;
        if (noEmptyChunks) flags |= PlotChunkFlags.NO_EMPTY_CHUNKS;
        return getAllChunksAsync(environment, flags, onChunkLoad);
    }

    @Override
    public void resetChunks() {
        resetChunks((Runnable) null);
    }

    @Override
    public void resetChunks(@Nullable Runnable onFinish) {
        resetChunks(0, onFinish);
    }

    @Override
    public void resetChunks(World.Environment environment) {
        resetChunks(environment, 0);
    }

    @Override
    public void resetChunks(World.Environment environment, @Nullable Runnable onFinish) {
        resetChunks(environment, 0, onFinish);
    }

    @Override
    public void resetChunks(@PlotChunkFlags int flags) {
        resetChunks(flags, null);
    }

    @Override
    public void resetChunks(@PlotChunkFlags int flags, @Nullable Runnable onFinish) {
        LinkedList<List<ChunkPosition>> worldsChunks = new LinkedList<>(
                PlotUtils.getChunkCoords(this, flags | PlotChunkFlags.NO_EMPTY_CHUNKS).values());


        if (worldsChunks.isEmpty()) {
            if (onFinish != null)
                onFinish.run();
            return;
        }

        for (List<ChunkPosition> chunkPositions : worldsChunks)
            PlotUtils.deleteChunks(this, chunkPositions, chunkPositions == worldsChunks.getLast() ? onFinish : null);
    }

    @Override
    public void resetChunks(World.Environment environment, @PlotChunkFlags int flags) {
        resetChunks(environment, flags, null);
    }

    @Override
    public void resetChunks(World.Environment environment, @PlotChunkFlags int flags, @Nullable Runnable onFinish) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null");

        WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(this, environment);

        List<ChunkPosition> chunkPositions = PlotUtils.getChunkCoords(this,
                worldInfo, flags | PlotChunkFlags.NO_EMPTY_CHUNKS);

        if (chunkPositions.isEmpty()) {
            if (onFinish != null)
                onFinish.run();
            return;
        }

        PlotUtils.deleteChunks(this, chunkPositions, onFinish);
    }

    @Override
    @Deprecated
    public void resetChunks(World.Environment environment, boolean onlyProtected) {
        resetChunks(environment, onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0);
    }

    @Override
    @Deprecated
    public void resetChunks(World.Environment environment, boolean onlyProtected, @Nullable Runnable onFinish) {
        resetChunks(environment, onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0, onFinish);
    }

    @Override
    @Deprecated
    public void resetChunks(boolean onlyProtected) {
        resetChunks(onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0);
    }

    @Override
    @Deprecated
    public void resetChunks(boolean onlyProtected, @Nullable Runnable onFinish) {
        resetChunks(onlyProtected ? PlotChunkFlags.ONLY_PROTECTED : 0, onFinish);
    }

    @Override
    public boolean isInside(Location location) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");

        if (location.getWorld() == null || !plugin.getGrid().isPlotsWorld(location.getWorld()))
            return false;

        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        PlotArea plotArea = new PlotArea(this.center, plotDistance);

        return plotArea.intercepts(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public boolean isInside(World world, int chunkX, int chunkZ) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");

        if (!plugin.getGrid().isPlotsWorld(world))
            return false;

        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        PlotArea plotArea = new PlotArea(this.center, plotDistance);
        plotArea.rshift(4);

        return plotArea.intercepts(chunkX, chunkZ);
    }

    private boolean isChunkInside(int chunkX, int chunkZ) {
        int plotDistance = (int) Math.round(plugin.getSettings().getMaxPlotSize() *
                (plugin.getSettings().isBuildOutsidePlot() ? 1.5 : 1D));
        PlotArea plotArea = new PlotArea(this.center, plotDistance);
        plotArea.rshift(4);

        return plotArea.intercepts(chunkX, chunkZ);
    }

    @Override
    public boolean isInsideRange(Location location) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        return isInsideRange(location, 0);
    }

    public boolean isInsideRange(Location location, int extra) {
        if (location.getWorld() == null || !plugin.getGrid().isPlotsWorld(location.getWorld()))
            return false;

        PlotArea plotArea = new PlotArea(center, getPlotSize());
        plotArea.expand(extra);

        return plotArea.intercepts(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public boolean isInsideRange(Chunk chunk) {
        Preconditions.checkNotNull(chunk, "chunk parameter cannot be null.");

        if (chunk.getWorld() == null || !plugin.getGrid().isPlotsWorld(chunk.getWorld()))
            return false;

        PlotArea plotArea = new PlotArea(center, getPlotSize());
        plotArea.rshift(4);

        return plotArea.intercepts(chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean isNormalEnabled() {
        return plugin.getProviders().getWorldsProvider().isNormalUnlocked() || (unlockedWorlds.get() & 4) == 4;
    }

    @Override
    public void setNormalEnabled(boolean enabled) {
        Log.debug(Debug.SET_NORMAL_ENABLED, owner.getName(), enabled);

        Mutable<Boolean> updatedUnlockedWorlds = new Mutable<>(false);

        this.unlockedWorlds.updateAndGet(unlockedWorlds -> {
            int newUnlockedWorlds = enabled ? unlockedWorlds | 4 : unlockedWorlds & 3;
            updatedUnlockedWorlds.setValue(newUnlockedWorlds != unlockedWorlds);
            return newUnlockedWorlds;
        });

        if (updatedUnlockedWorlds.getValue())
            PlotsDatabaseBridge.saveUnlockedWorlds(this);
    }

    @Override
    public boolean isNetherEnabled() {
        return plugin.getProviders().getWorldsProvider().isNetherUnlocked() || (unlockedWorlds.get() & 1) == 1;
    }

    @Override
    public void setNetherEnabled(boolean enabled) {
        Log.debug(Debug.SET_NETHER_ENABLED, owner.getName(), enabled);

        Mutable<Boolean> updatedUnlockedWorlds = new Mutable<>(false);

        this.unlockedWorlds.updateAndGet(unlockedWorlds -> {
            int newUnlockedWorlds = enabled ? unlockedWorlds | 1 : unlockedWorlds & 6;
            updatedUnlockedWorlds.setValue(newUnlockedWorlds != unlockedWorlds);
            return newUnlockedWorlds;
        });

        if (updatedUnlockedWorlds.getValue())
            PlotsDatabaseBridge.saveUnlockedWorlds(this);
    }

    @Override
    public boolean isEndEnabled() {
        return plugin.getProviders().getWorldsProvider().isEndUnlocked() || (unlockedWorlds.get() & 2) == 2;
    }

    /*
     *  Permissions related methods
     */

    @Override
    public void setEndEnabled(boolean enabled) {
        Log.debug(Debug.SET_END_ENABLED, owner.getName(), enabled);

        Mutable<Boolean> updatedUnlockedWorlds = new Mutable<>(false);

        this.unlockedWorlds.updateAndGet(unlockedWorlds -> {
            int newUnlockedWorlds = enabled ? unlockedWorlds | 2 : unlockedWorlds & 5;
            updatedUnlockedWorlds.setValue(newUnlockedWorlds != unlockedWorlds);
            return newUnlockedWorlds;
        });

        if (updatedUnlockedWorlds.getValue())
            PlotsDatabaseBridge.saveUnlockedWorlds(this);
    }

    @Override
    public int getUnlockedWorldsFlag() {
        return this.unlockedWorlds.get();
    }

    @Override
    public boolean hasPermission(CommandSender sender, PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(sender, "sender parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        return sender instanceof ConsoleCommandSender || hasPermission(plugin.getPlayers().getSuperiorPlayer(sender), plotPrivilege);
    }

    @Override
    public boolean hasPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        PermissionNode playerNode = getPermissionNode(superiorPlayer);
        return superiorPlayer.hasBypassModeEnabled() || superiorPlayer.hasPermissionWithoutOP("superior.admin.bypass.*") ||
                superiorPlayer.hasPermissionWithoutOP("superior.admin.bypass." + plotPrivilege.getName()) ||
                (playerNode != null && playerNode.hasPermission(plotPrivilege));
    }

    @Override
    public boolean hasPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        return getRequiredPlayerRole(plotPrivilege).getWeight() <= playerRole.getWeight();
    }

    @Override
    public void setPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege, boolean value) {
        if (value)
            this.setPermission(playerRole, plotPrivilege);
    }

    @Override
    public void setPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        Log.debug(Debug.SET_PERMISSION, owner.getName(), playerRole, plotPrivilege);

        PlayerRole oldRole = rolePermissions.put(plotPrivilege, playerRole);

        if (oldRole == playerRole)
            return;

        if (plotPrivilege == PlotPrivileges.FLY) {
            getAllPlayersInside().forEach(this::updatePlotFly);
        } else if (plotPrivilege == PlotPrivileges.VILLAGER_TRADING) {
            getAllPlayersInside().forEach(superiorPlayer -> PlotUtils.updateTradingMenus(this, superiorPlayer));
        }

        PlotsDatabaseBridge.saveRolePermission(this, playerRole, plotPrivilege);
    }

    @Override
    public void resetPermissions() {
        Log.debug(Debug.RESET_PERMISSIONS, owner.getName());

        if (rolePermissions.isEmpty())
            return;

        rolePermissions.clear();

        getAllPlayersInside().forEach(superiorPlayer -> {
            updatePlotFly(superiorPlayer);
            PlotUtils.updateTradingMenus(this, superiorPlayer);
        });

        PlotsDatabaseBridge.clearRolePermissions(this);

        plugin.getMenus().refreshPermissions(this);
    }

    @Override
    public void setPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege, boolean value) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        Log.debug(Debug.SET_PERMISSION, owner.getName(),
                superiorPlayer.getName(), plotPrivilege, value);

        PlayerPrivilegeNode privilegeNode = playerPermissions.computeIfAbsent(superiorPlayer,
                s -> new PlayerPrivilegeNode(superiorPlayer, this));

        privilegeNode.setPermission(plotPrivilege, value);

        if (superiorPlayer.isOnline() && isInside(superiorPlayer.getLocation())) {
            if (plotPrivilege == PlotPrivileges.FLY) {
                updatePlotFly(superiorPlayer);
            } else if (plotPrivilege == PlotPrivileges.VILLAGER_TRADING) {
                PlotUtils.updateTradingMenus(this, superiorPlayer);
            }
        }

        PlotsDatabaseBridge.savePlayerPermission(this, superiorPlayer, plotPrivilege, value);

        plugin.getMenus().refreshPermissions(this, superiorPlayer);
    }

    @Override
    public void resetPermissions(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.RESET_PERMISSIONS, owner.getName(), superiorPlayer.getName());

        PlayerPrivilegeNode oldPrivilegeNode = playerPermissions.remove(superiorPlayer);

        if (oldPrivilegeNode == null)
            return;

        if (superiorPlayer.isOnline()) {
            updatePlotFly(superiorPlayer);
            PlotUtils.updateTradingMenus(this, superiorPlayer);
        }

        PlotsDatabaseBridge.clearPlayerPermission(this, superiorPlayer);

        plugin.getMenus().refreshPermissions(this, superiorPlayer);
    }

    @Override
    public PrivilegeNodeAbstract getPermissionNode(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        return playerPermissions.getOrDefault(superiorPlayer, new PlayerPrivilegeNode(superiorPlayer, this));
    }

    @Override
    public PlayerRole getRequiredPlayerRole(PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        PlayerRole playerRole = rolePermissions.get(plotPrivilege);

        if (playerRole != null)
            return playerRole;

        return plugin.getRoles().getRoles().stream()
                .filter(_playerRole -> {
                    if (!plugin.getSettings().isCoopMembers() && _playerRole == SPlayerRole.coopRole())
                        return false;

                    return ((SPlayerRole) _playerRole).getDefaultPermissions().hasPermission(plotPrivilege);
                })
                .min(Comparator.comparingInt(PlayerRole::getWeight)).orElse(SPlayerRole.lastRole());
    }

    /*
     *  General methods
     */

    @Override
    public Map<SuperiorPlayer, PermissionNode> getPlayerPermissions() {
        return Collections.unmodifiableMap(playerPermissions);
    }

    @Override
    public Map<PlotPrivilege, PlayerRole> getRolePermissions() {
        return Collections.unmodifiableMap(rolePermissions);
    }

    @Override
    public boolean isSpawn() {
        return false;
    }

    @Override
    public String getName() {
        return plugin.getSettings().getPlotNames().isColorSupport() ? plotName : plotRawName;
    }

    @Override
    public void setName(String plotName) {
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");

        Log.debug(Debug.SET_NAME, owner.getName(), plotName);

        if (Objects.equals(plotName, this.plotName))
            return;

        this.plotName = plotName;
        this.plotRawName = Formatters.STRIP_COLOR_FORMATTER.format(this.plotName);

        PlotsDatabaseBridge.saveName(this);
    }

    @Override
    public String getRawName() {
        return plotRawName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        Preconditions.checkNotNull(description, "description parameter cannot be null.");

        Log.debug(Debug.SET_DESCRIPTION, owner.getName(), description);

        if (Objects.equals(this.description, description))
            return;

        this.description = description;

        PlotsDatabaseBridge.saveDescription(this);
    }

    @Override
    public void disbandPlot() {
        long profilerId = Profiler.start(ProfileType.DISBAND_PLOT, 2);

        forEachPlotMember(Collections.emptyList(), false, plotMember -> {
            if (plotMember.equals(owner)) {
                owner.setPlot(null);
            } else {
                kickMember(plotMember);
            }

            if (plugin.getSettings().isDisbandInventoryClear())
                plugin.getNMSPlayers().clearInventory(plotMember.asOfflinePlayer());

            for (Mission<?> mission : plugin.getMissions().getAllMissions()) {
                MissionData missionData = plugin.getMissions().getMissionData(mission).orElse(null);
                if (missionData != null && missionData.isDisbandReset()) {
                    plotMember.resetMission(mission);
                }
            }
        });

        invitedPlayers.forEach(invitedPlayer -> invitedPlayer.removeInvite(this));

        if (BuiltinModules.BANK.disbandRefund > 0)
            plugin.getProviders().depositMoney(getOwner(), plotBank.getBalance()
                    .multiply(BigDecimal.valueOf(BuiltinModules.BANK.disbandRefund)));

        plugin.getMissions().getAllMissions().forEach(this::resetMission);

        resetChunks(PlotChunkFlags.ONLY_PROTECTED, () -> Profiler.end(profilerId));

        plugin.getGrid().deletePlot(this);

        Profiler.end(profilerId);
    }

    @Override
    public boolean transferPlot(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        if (superiorPlayer.equals(owner))
            return false;

        SuperiorPlayer previousOwner = getOwner();

        if (!plugin.getEventsBus().callPlotTransferEvent(this, previousOwner, superiorPlayer))
            return false;

        Log.debug(Debug.TRANSFER_PLOT, owner.getName(), superiorPlayer.getName());

        //Kick member without saving to database
        members.write(members -> members.remove(superiorPlayer));

        superiorPlayer.setPlayerRole(SPlayerRole.lastRole());

        //Add member without saving to database
        members.write(members -> members.add(previousOwner));

        PlayerRole previousRole = SPlayerRole.lastRole().getPreviousRole();
        previousOwner.setPlayerRole(previousRole == null ? SPlayerRole.lastRole() : previousRole);

        //Changing owner of the plot.
        owner = superiorPlayer;

        PlotsDatabaseBridge.savePlotLeader(this);
        PlotsDatabaseBridge.addMember(this, previousOwner, getCreationTime());

        plugin.getMissions().getAllMissions().forEach(mission -> mission.transferData(previousOwner, owner));

        return true;
    }

    @Override
    public void replacePlayers(SuperiorPlayer originalPlayer, @Nullable SuperiorPlayer newPlayer) {
        Preconditions.checkNotNull(originalPlayer, "originalPlayer parameter cannot be null.");
        Preconditions.checkState(originalPlayer != newPlayer, "originalPlayer and newPlayer cannot equal.");

        Log.debug(Debug.REPLACE_PLAYER, owner, originalPlayer, newPlayer);

        if (owner.equals(originalPlayer)) {
            if (newPlayer == null) {
                Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Disband Plot");
                this.disbandPlot();
            } else {
                Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Replace Owner");
                owner = newPlayer;
            }
        } else if (isMember(originalPlayer)) {
            Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Replace Member");
            members.write(members -> {
                members.remove(originalPlayer);
                if (newPlayer != null)
                    members.add(newPlayer);
            });
        }

        replaceVisitor(originalPlayer, newPlayer);
        replaceBannedPlayer(originalPlayer, newPlayer);
        replacePermissions(originalPlayer, newPlayer);
    }

    @Override
    public void calcPlotWorth(@Nullable SuperiorPlayer asker) {
        calcPlotWorth(asker, null);
    }

    @Override
    public void calcPlotWorth(@Nullable SuperiorPlayer asker, @Nullable Runnable callback) {
        Log.debug(Debug.CALCULATE_PLOT, owner.getName(), asker);

        long lastUpdateTime = getLastTimeUpdate();

        if (lastUpdateTime != -1 && (System.currentTimeMillis() / 1000) - lastUpdateTime >= 600) {
            Log.debugResult(Debug.CALCULATE_PLOT, "Result Cooldown", owner.getName());
            finishCalcPlot(asker, callback, getPlotLevel(), getWorth());
            return;
        }

        if (Bukkit.isPrimaryThread()) {
            calcPlotWorthInternal(asker, callback);
        } else {
            BukkitExecutor.sync(() -> calcPlotWorthInternal(asker, callback));
        }
    }

    @Override
    public PlotCalculationAlgorithm getCalculationAlgorithm() {
        return this.calculationAlgorithm;
    }

    @Override
    public void updateBorder() {
        Log.debug(Debug.UPDATE_BORDER, owner.getName());
        getAllPlayersInside().forEach(superiorPlayer -> superiorPlayer.updateWorldBorder(this));
    }

    @Override
    public void updatePlotFly(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        PlotUtils.updatePlotFly(this, superiorPlayer);
    }

    @Override
    public int getPlotSize() {
        if (plugin.getSettings().isBuildOutsidePlot())
            return (int) Math.round(plugin.getSettings().getMaxPlotSize() * 1.5);

        return this.plotSize.readAndGet(Value::get);
    }

    @Override
    public void setPlotSize(int plotSize) {
        plotSize = Math.max(1, plotSize);

        Preconditions.checkArgument(plotSize <= plugin.getSettings().getMaxPlotSize(), "Border size " + plotSize + " cannot be larger than max plot size: " + plugin.getSettings().getMaxPlotSize());

        Log.debug(Debug.SET_SIZE, owner.getName(), plotSize);

        if (plotSize == getPlotSizeRaw())
            return;

        setPlotSizeInternal(Value.fixed(plotSize));

        PlotsDatabaseBridge.saveSize(this);
    }

    private void setPlotSizeInternal(Value<Integer> plotSize) {
        boolean cropGrowthEnabled = BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeCropGrowth.class);

        if (cropGrowthEnabled) {
            // First, we want to remove all the current crop tile entities
            getLoadedChunks(PlotChunkFlags.ONLY_PROTECTED).forEach(chunk ->
                    plugin.getNMSChunks().startTickingChunk(this, chunk, true));
        }

        this.plotSize.set(plotSize);

        if (cropGrowthEnabled) {
            // Now, we want to update the tile entities again
            getLoadedChunks(PlotChunkFlags.ONLY_PROTECTED).forEach(chunk ->
                    plugin.getNMSChunks().startTickingChunk(this, chunk, false));
        }

        updateBorder();
    }

    @Override
    public int getPlotSizeRaw() {
        return this.plotSize.readAndGet(plotSize -> plotSize.getRaw(-1));
    }

    @Override
    public String getDiscord() {
        return discord;
    }

    @Override
    public void setDiscord(String discord) {
        Preconditions.checkNotNull(discord, "discord parameter cannot be null.");

        Log.debug(Debug.SET_DISCORD, owner.getName(), discord);

        if (Objects.equals(discord, this.discord))
            return;

        this.discord = discord;
        PlotsDatabaseBridge.saveDiscord(this);
    }

    @Override
    public String getPaypal() {
        return paypal;
    }

    @Override
    public void setPaypal(String paypal) {
        Preconditions.checkNotNull(paypal, "paypal parameter cannot be null.");

        Log.debug(Debug.SET_PAYPAL, owner.getName(), paypal);

        if (Objects.equals(paypal, this.paypal))
            return;

        this.paypal = paypal;
        PlotsDatabaseBridge.savePaypal(this);
    }

    @Override
    public Biome getBiome() {
        if (biome == null) {
            biomeGetterTask.set(task -> {
                if (task != null)
                    return task;

                World.Environment defaultWorldEnvironment = plugin.getSettings().getWorlds().getDefaultWorld();
                WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(this, defaultWorldEnvironment);
                Location centerBlock = getCenter(defaultWorldEnvironment);

                ChunkPosition centerChunkPosition = ChunkPosition.of(worldInfo,
                        centerBlock.getBlockX() >> 4, centerBlock.getBlockZ() >> 4);

                return ChunksProvider.loadChunk(centerChunkPosition, ChunkLoadReason.BIOME_REQUEST, null)
                        .thenApply(chunk -> centerBlock.getBlock().getBiome())
                        .whenComplete((biome, error) -> {
                            if (error != null)
                                error.printStackTrace();
                            else {
                                this.biome = biome;
                                biomeGetterTask.set((CompletableFuture<Biome>) null);
                            }
                        });
            });

            return PlotUtils.getDefaultWorldBiome(plugin.getSettings().getWorlds().getDefaultWorld());
        }

        return biome;
    }

    @Override
    public void setBiome(Biome biome) {
        setBiome(biome, true);
    }

    @Override
    public void setBiome(Biome biome, boolean updateBlocks) {
        Preconditions.checkNotNull(biome, "biome parameter cannot be null.");

        Log.debug(Debug.SET_BIOME, owner.getName(), biome, updateBlocks);

        this.biome = biome;

        if (!updateBlocks)
            return;

        List<Player> playersToUpdate = new SequentialListBuilder<Player>()
                .build(getAllPlayersInside(), SuperiorPlayer::asPlayer);

        {
            WorldInfo normalWorld = plugin.getGrid().getPlotsWorldInfo(this, plugin.getSettings().getWorlds().getDefaultWorld());
            List<ChunkPosition> chunkPositions = PlotUtils.getChunkCoords(this, normalWorld, 0);
            plugin.getNMSChunks().setBiome(chunkPositions, biome, playersToUpdate);
        }

        if (plugin.getProviders().getWorldsProvider().isNetherEnabled() && wasSchematicGenerated(World.Environment.NETHER)) {
            WorldInfo netherWorld = plugin.getGrid().getPlotsWorldInfo(this, World.Environment.NETHER);
            Biome netherBiome = PlotUtils.getDefaultWorldBiome(World.Environment.NETHER);
            List<ChunkPosition> chunkPositions = PlotUtils.getChunkCoords(this, netherWorld, 0);
            plugin.getNMSChunks().setBiome(chunkPositions, netherBiome, playersToUpdate);
        }

        if (plugin.getProviders().getWorldsProvider().isEndEnabled() && wasSchematicGenerated(World.Environment.THE_END)) {
            WorldInfo endWorld = plugin.getGrid().getPlotsWorldInfo(this, World.Environment.THE_END);
            Biome endBiome = PlotUtils.getDefaultWorldBiome(World.Environment.THE_END);
            List<ChunkPosition> chunkPositions = PlotUtils.getChunkCoords(this, endWorld, 0);
            plugin.getNMSChunks().setBiome(chunkPositions, endBiome, playersToUpdate);
        }

        for (World registeredWorld : plugin.getGrid().getRegisteredWorlds()) {
            List<ChunkPosition> chunkPositions = PlotUtils.getChunkCoords(this, WorldInfo.of(registeredWorld), 0);
            plugin.getNMSChunks().setBiome(chunkPositions, biome, playersToUpdate);
        }
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void setLocked(boolean locked) {
        Log.debug(Debug.SET_LOCKED, owner.getName(), locked);

        if (this.isLocked == locked)
            return;

        this.isLocked = locked;

        if (this.isLocked) {
            for (SuperiorPlayer victimPlayer : getAllPlayersInside()) {
                if (!hasPermission(victimPlayer, PlotPrivileges.CLOSE_BYPASS)) {
                    victimPlayer.teleport(plugin.getGrid().getSpawnPlot());
                    Message.PLOT_WAS_CLOSED.send(victimPlayer);
                }
            }
        }

        PlotsDatabaseBridge.saveLockedStatus(this);
    }

    @Override
    public boolean isIgnored() {
        return isTopPlotsIgnored;
    }

    @Override
    public void setIgnored(boolean ignored) {
        Log.debug(Debug.SET_IGNORED, owner.getName(), ignored);

        if (this.isTopPlotsIgnored == ignored)
            return;

        this.isTopPlotsIgnored = ignored;

        // We want top plots to get sorted again even if only 1 plot exists
        plugin.getGrid().setForceSort(true);

        PlotsDatabaseBridge.saveIgnoredStatus(this);
    }

    @Override
    public void sendMessage(String message, UUID... ignoredMembers) {
        Preconditions.checkNotNull(message, "message parameter cannot be null.");
        Preconditions.checkNotNull(ignoredMembers, "ignoredMembers parameter cannot be null.");

        List<UUID> ignoredList = ignoredMembers.length == 0 ? Collections.emptyList() : Arrays.asList(ignoredMembers);

        Log.debug(Debug.SEND_MESSAGE, owner.getName(), message, ignoredList);

        forEachPlotMember(ignoredList, false, plotMember -> Message.CUSTOM.send(plotMember, message, false));
    }

    @Override
    public void sendMessage(IMessageComponent messageComponent, Object... args) {
        this.sendMessage(messageComponent, Collections.emptyList(), args);
    }

    @Override
    public void sendMessage(IMessageComponent messageComponent, List<UUID> ignoredMembers, Object... args) {
        Preconditions.checkNotNull(messageComponent, "messageComponent parameter cannot be null.");
        Preconditions.checkNotNull(ignoredMembers, "ignoredMembers parameter cannot be null.");

        Log.debug(Debug.SEND_MESSAGE, owner.getName(), messageComponent.getMessage(), ignoredMembers, Arrays.asList(args));

        forEachPlotMember(ignoredMembers, false, plotMember -> messageComponent.sendMessage(plotMember.asPlayer(), args));
    }

    @Override
    public void sendTitle(@Nullable String title, @Nullable String subtitle, int fadeIn, int duration,
                          int fadeOut, UUID... ignoredMembers) {
        Preconditions.checkNotNull(ignoredMembers, "ignoredMembers parameter cannot be null.");

        List<UUID> ignoredList = ignoredMembers.length == 0 ? Collections.emptyList() : Arrays.asList(ignoredMembers);

        Log.debug(Debug.SEND_TITLE, owner.getName(), title, subtitle, fadeIn, duration, fadeOut, ignoredList);

        forEachPlotMember(ignoredList, true, plotMember ->
                plugin.getNMSPlayers().sendTitle(plotMember.asPlayer(), title, subtitle, fadeIn, duration, fadeOut)
        );
    }

    @Override
    public void executeCommand(String command, boolean onlyOnlineMembers, UUID... ignoredMembers) {
        Preconditions.checkNotNull(command, "command parameter cannot be null.");
        Preconditions.checkNotNull(ignoredMembers, "ignoredMembers parameter cannot be null.");

        List<UUID> ignoredList = ignoredMembers.length == 0 ? Collections.emptyList() : Arrays.asList(ignoredMembers);

        Log.debug(Debug.EXECUTE_PLOT_COMMANDS, owner.getName(), command, onlyOnlineMembers, ignoredList);

        forEachPlotMember(ignoredList, true, plotMember ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player-name}", plotMember.getName()))
        );
    }

    @Override
    public boolean isBeingRecalculated() {
        return beingRecalculated;
    }

    @Override
    public void updateLastTime() {
        setLastTimeUpdate(System.currentTimeMillis() / 1000);
    }

    @Override
    public void setCurrentlyActive() {
        setCurrentlyActive(true);
    }

    @Override
    public void setCurrentlyActive(boolean active) {
        this.currentlyActive = active;
    }

    @Override
    public boolean isCurrentlyActive() {
        return this.currentlyActive;
    }

    @Override
    public long getLastTimeUpdate() {
        return this.currentlyActive ? -1 : lastTimeUpdate;
    }

    @Override
    public void setLastTimeUpdate(long lastTimeUpdate) {
        Log.debug(Debug.SET_PLOT_LAST_TIME_UPDATED, owner.getName(), lastTimeUpdate);

        if (this.lastTimeUpdate == lastTimeUpdate)
            return;

        this.lastTimeUpdate = lastTimeUpdate;

        if (!isCurrentlyActive())
            PlotsDatabaseBridge.saveLastTimeUpdate(this);
    }

    @Override
    public PlotBank getPlotBank() {
        return plotBank;
    }

    @Override
    public BigDecimal getBankLimit() {
        return this.bankLimit.readAndGet(Value::get);
    }

    /*
     *  Bank related methods
     */

    @Override
    public void setBankLimit(BigDecimal bankLimit) {
        Preconditions.checkNotNull(bankLimit, "bankLimit parameter cannot be null.");

        Log.debug(Debug.SET_BANK_LIMIT, owner.getName(), bankLimit);

        if (Objects.equals(bankLimit, getBankLimitRaw()))
            return;

        if (bankLimit.compareTo(SYNCED_BANK_LIMIT_VALUE) <= 0) {
            this.bankLimit.set(() -> SYNCED_BANK_LIMIT_VALUE);

            getUpgrades().forEach((upgradeName, level) -> {
                Upgrade upgrade = plugin.getUpgrades().getUpgrade(upgradeName);
                if (upgrade != null) {
                    UpgradeLevel upgradeLevel = upgrade.getUpgradeLevel(level);
                    if (upgradeLevel.getBankLimit().compareTo(getBankLimit()) > 0) {
                        this.bankLimit.set(Value.syncedFixed(upgradeLevel.getBankLimit()));
                    }
                }
            });
        } else {
            this.bankLimit.set(Value.fixed(bankLimit));
        }

        // Trying to give interest again if the last one failed.
        if (hasGiveInterestFailed())
            giveInterest(false);

        PlotsDatabaseBridge.saveBankLimit(this);
    }

    @Override
    public BigDecimal getBankLimitRaw() {
        return this.bankLimit.readAndGet(bankLimit -> bankLimit.getRaw(SYNCED_BANK_LIMIT_VALUE));
    }

    @Override
    public boolean giveInterest(boolean checkOnlineOwner) {
        Log.debug(Debug.GIVE_BANK_INTEREST, owner.getName());

        long currentTime = System.currentTimeMillis() / 1000;

        if (checkOnlineOwner && BuiltinModules.BANK.bankInterestRecentActive > 0 &&
                currentTime - owner.getLastTimeStatus() > BuiltinModules.BANK.bankInterestRecentActive) {
            Log.debugResult(Debug.GIVE_BANK_INTEREST, "Return Cooldown", owner.getName());
            return false;
        }

        BigDecimal balance = plotBank.getBalance().max(BigDecimal.ONE);
        BigDecimal balanceToGive = balance.multiply(new BigDecimal(BuiltinModules.BANK.bankInterestPercentage / 100D));

        // If the money that will be given exceeds limit, we want to give money later.
        if (!plotBank.canDepositMoney(balanceToGive)) {
            Log.debugResult(Debug.GIVE_BANK_INTEREST, "Return Cannot Deposit Money", owner.getName());
            giveInterestFailed = true;
            return false;
        }

        Log.debugResult(Debug.GIVE_BANK_INTEREST, "Return Success", owner.getName());

        giveInterestFailed = false;

        plotBank.depositAdminMoney(Bukkit.getConsoleSender(), balanceToGive);
        plugin.getMenus().refreshPlotBank(this);

        setLastInterestTime(currentTime);

        return true;
    }

    @Override
    public long getLastInterestTime() {
        return lastInterest;
    }

    @Override
    public void setLastInterestTime(long lastInterest) {
        if (this.lastInterest == lastInterest)
            return;

        if (BuiltinModules.BANK.bankInterestEnabled) {
            long ticksToNextInterest = BuiltinModules.BANK.bankInterestInterval * 20L;
            this.bankInterestTask.set(bankInterestTask -> {
                if (bankInterestTask != null)
                    bankInterestTask.cancel();
                return BukkitExecutor.sync(() -> giveInterest(true), ticksToNextInterest);
            });
        }

        this.lastInterest = lastInterest;
        PlotsDatabaseBridge.saveLastInterestTime(this);
    }

    @Override
    public long getNextInterest() {
        long currentTime = System.currentTimeMillis() / 1000;
        return BuiltinModules.BANK.bankInterestInterval - (currentTime - lastInterest);
    }

    /*
     *  Worth related methods
     */

    @Override
    public void handleBlockPlace(Block block) {
        handleBlockPlace(block, 1);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Block block) {
        return handleBlockPlaceWithResult(block, 1);
    }

    @Override
    public void handleBlockPlace(Key key) {
        handleBlockPlace(key, 1);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Key key) {
        return handleBlockPlaceWithResult(key, 1);
    }

    @Override
    public void handleBlockPlace(Block block, @Size int amount) {
        handleBlockPlace(block, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Block block, @Size int amount) {
        return handleBlockPlaceWithResult(block, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlockPlace(Key key, @Size int amount) {
        handleBlockPlace(key, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Key key, @Size int amount) {
        return handleBlockPlaceWithResult(key, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlockPlace(Block block, @Size int amount, @PlotBlockFlags int flags) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        handleBlockPlace(Keys.of(block), amount, flags);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Block block, @Size int amount, @PlotBlockFlags int flags) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        return handleBlockPlaceWithResult(Keys.of(block), amount, flags);
    }

    @Override
    public void handleBlockPlace(Key key, @Size int amount, @PlotBlockFlags int flags) {
        handleBlockPlaceWithResult(key, amount, flags);
    }

    @Override
    public BlockChangeResult handleBlockPlaceWithResult(Key key, @Size int amount, @PlotBlockFlags int flags) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkArgument(amount > 0, "amount parameter must be positive.");

        BigInteger amountBig = BigInteger.valueOf(amount);

        return handleBlockPlaceInternal(key, amountBig, flags);
    }

    private BlockChangeResult handleBlockPlaceInternal(Key key, @Size BigInteger amount, @PlotBlockFlags int flags) {
        boolean trackedBlock = this.blocksTracker.trackBlock(key, amount);

        if (!trackedBlock)
            return BlockChangeResult.MISSING_BLOCK_VALUE;

        BigInteger newTotalBlocksCount = this.currentTotalBlockCounts.updateAndGet(count -> count.add(amount));

        BigDecimal oldWorth = getWorth();
        BigDecimal oldLevel = getPlotLevel();

        BigDecimal blockValue = plugin.getBlockValues().getBlockWorth(key);
        BigDecimal blockLevel = plugin.getBlockValues().getBlockLevel(key);

        boolean saveBlockCounts = (flags & PlotBlockFlags.SAVE_BLOCK_COUNTS) != 0;
        boolean updateLastTimeStatus = (flags & PlotBlockFlags.UPDATE_LAST_TIME_STATUS) != 0;

        if (blockValue.compareTo(BigDecimal.ZERO) != 0) {
            plotWorth.updateAndGet(plotWorth -> plotWorth.add(blockValue.multiply(new BigDecimal(amount))));
            if (saveBlockCounts)
                plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_WORTH, this);
        }

        if (blockLevel.compareTo(BigDecimal.ZERO) != 0) {
            plotLevel.updateAndGet(plotLevel -> plotLevel.add(blockLevel.multiply(new BigDecimal(amount))));
            if (saveBlockCounts)
                plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_LEVEL, this);
        }

        if (updateLastTimeStatus)
            updateLastTime();

        if (saveBlockCounts)
            saveBlockCounts(newTotalBlocksCount, oldWorth, oldLevel);

        return BlockChangeResult.SUCCESS;
    }

    @Override
    @Deprecated
    public void handleBlockPlace(Block block, @Size int amount, boolean save) {
        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;
        handleBlockPlace(block, amount, flags);
    }

    @Override
    @Deprecated
    public void handleBlockPlace(Key key, @Size int amount, boolean save) {
        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;
        handleBlockPlace(key, amount, flags);
    }

    @Override
    @Deprecated
    public void handleBlockPlace(Key key, @Size BigInteger amount, boolean save) {
        Preconditions.checkNotNull(key, "key argument cannot be null");
        Preconditions.checkNotNull(amount, "amount argument cannot be null");

        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;

        handleBlockPlace(key, amount, flags);
    }

    @Override
    @Deprecated
    public void handleBlockPlace(Key key, @Size BigInteger amount, boolean save, boolean updateLastTimeStatus) {
        Preconditions.checkNotNull(key, "key argument cannot be null");
        Preconditions.checkNotNull(amount, "amount argument cannot be null");

        int flags = 0;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;
        if (updateLastTimeStatus) flags |= PlotBlockFlags.UPDATE_LAST_TIME_STATUS;

        handleBlockPlace(key, amount, flags);
    }

    @Deprecated
    private void handleBlockPlace(Key key, @Size BigInteger amount, @PlotBlockFlags int flags) {
        BigInteger MAX_INT_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
        while (amount.compareTo(MAX_INT_VALUE) > 0) {
            handleBlockPlace(key, Integer.MAX_VALUE, flags);
            amount = amount.subtract(MAX_INT_VALUE);
        }

        handleBlockPlace(key, amount.intValueExact(), flags);
    }

    @Override
    public void handleBlocksPlace(Map<Key, Integer> blocks) {
        handleBlocksPlace(blocks, PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public Map<Key, BlockChangeResult> handleBlocksPlaceWithResult(Map<Key, Integer> blocks) {
        return handleBlocksPlaceWithResult(blocks,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlocksPlace(Map<Key, Integer> blocks, @PlotBlockFlags int flags) {
        handleBlocksPlaceWithResult(blocks, flags);
    }

    @Override
    public Map<Key, BlockChangeResult> handleBlocksPlaceWithResult(Map<Key, Integer> blocks, @PlotBlockFlags int flags) {
        Preconditions.checkNotNull(blocks, "blocks parameter cannot be null.");

        if (blocks.isEmpty())
            return KeyMaps.createEmptyMap();

        BigDecimal oldWorth = getWorth();
        BigDecimal oldLevel = getPlotLevel();

        KeyMap<BlockChangeResult> result = KeyMaps.createHashMap(KeyIndicator.MATERIAL);

        blocks.forEach((blockKey, amount) -> {
            BlockChangeResult blockResult = handleBlockPlaceWithResult(blockKey, amount, 0);
            if (blockResult != BlockChangeResult.SUCCESS)
                result.put(blockKey, blockResult);
        });

        boolean saveBlockCounts = (flags & PlotBlockFlags.SAVE_BLOCK_COUNTS) != 0;
        boolean updateLastTimeStatus = (flags & PlotBlockFlags.UPDATE_LAST_TIME_STATUS) != 0;

        if (saveBlockCounts)
            saveBlockCounts(this.currentTotalBlockCounts.get(), oldWorth, oldLevel);

        if (updateLastTimeStatus)
            updateLastTime();

        return result.isEmpty() ? KeyMaps.createEmptyMap() : KeyMaps.unmodifiableKeyMap(result);
    }

    @Override
    public void handleBlockBreak(Block block) {
        handleBlockBreak(block, 1);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Block block) {
        return handleBlockBreakWithResult(block, 1);
    }

    @Override
    public void handleBlockBreak(Key key) {
        handleBlockBreak(key, 1);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Key key) {
        return handleBlockBreakWithResult(key, 1);
    }

    @Override
    public void handleBlockBreak(Block block, @Size int amount) {
        handleBlockBreak(block, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Block block, @Size int amount) {
        return handleBlockBreakWithResult(block, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlockBreak(Key key, @Size int amount) {
        handleBlockBreak(key, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Key key, @Size int amount) {
        return handleBlockBreakWithResult(key, amount,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlockBreak(Block block, @Size int amount, @PlotBlockFlags int flags) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        handleBlockBreak(Keys.of(block), amount, flags);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Block block, @Size int amount, int flags) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        return handleBlockBreakWithResult(Keys.of(block), amount, flags);
    }

    @Override
    public void handleBlockBreak(Key key, @Size int amount, @PlotBlockFlags int flags) {
        handleBlockBreakWithResult(key, amount, flags);
    }

    @Override
    public BlockChangeResult handleBlockBreakWithResult(Key key, @Size int amount, int flags) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkArgument(amount > 0, "amount parameter must be positive.");

        BigInteger amountBig = BigInteger.valueOf(amount);

        boolean untrackedBlocks = this.blocksTracker.untrackBlock(key, amountBig);

        if (!untrackedBlocks)
            return BlockChangeResult.MISSING_BLOCK_VALUE;

        BigInteger newTotalBlocksCount = this.currentTotalBlockCounts.updateAndGet(count -> count.subtract(amountBig));

        BigDecimal oldWorth = getWorth(), oldLevel = getPlotLevel();

        BigDecimal blockValue = plugin.getBlockValues().getBlockWorth(key);
        BigDecimal blockLevel = plugin.getBlockValues().getBlockLevel(key);

        boolean saveBlockCounts = (flags & PlotBlockFlags.SAVE_BLOCK_COUNTS) != 0;
        boolean updateLastTimeStatus = (flags & PlotBlockFlags.UPDATE_LAST_TIME_STATUS) != 0;

        if (blockValue.compareTo(BigDecimal.ZERO) != 0) {
            this.plotWorth.updateAndGet(plotWorth -> plotWorth.subtract(blockValue.multiply(new BigDecimal(amount))));
            if (saveBlockCounts)
                plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_WORTH, this);
        }

        if (blockLevel.compareTo(BigDecimal.ZERO) != 0) {
            this.plotLevel.updateAndGet(plotLevel -> plotLevel.subtract(blockLevel.multiply(new BigDecimal(amount))));
            if (saveBlockCounts)
                plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_LEVEL, this);
        }

        if (updateLastTimeStatus)
            updateLastTime();

        if (saveBlockCounts)
            saveBlockCounts(newTotalBlocksCount, oldWorth, oldLevel);

        return BlockChangeResult.SUCCESS;
    }

    @Override
    @Deprecated
    public void handleBlockBreak(Block block, @Size int amount, boolean save) {
        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;
        handleBlockBreak(block, amount, flags);
    }

    @Override
    @Deprecated
    public void handleBlockBreak(Key key, @Size int amount, boolean save) {
        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;
        handleBlockBreak(key, amount, flags);
    }

    @Override
    @Deprecated
    public void handleBlockBreak(Key key, @Size BigInteger amount, boolean save) {
        Preconditions.checkNotNull(key, "key argument cannot be null");
        Preconditions.checkNotNull(amount, "amount argument cannot be null");

        int flags = PlotBlockFlags.UPDATE_LAST_TIME_STATUS;
        if (save) flags |= PlotBlockFlags.SAVE_BLOCK_COUNTS;

        BigInteger MAX_INT_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
        while (amount.compareTo(MAX_INT_VALUE) > 0) {
            handleBlockBreak(key, Integer.MAX_VALUE, flags);
            amount = amount.subtract(MAX_INT_VALUE);
        }

        handleBlockBreak(key, amount.intValueExact(), flags);
    }

    @Override
    public void handleBlocksBreak(Map<Key, Integer> blocks) {
        handleBlocksBreak(blocks,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public Map<Key, BlockChangeResult> handleBlocksBreakWithResult(Map<Key, Integer> blocks) {
        return handleBlocksBreakWithResult(blocks,
                PlotBlockFlags.SAVE_BLOCK_COUNTS | PlotBlockFlags.UPDATE_LAST_TIME_STATUS);
    }

    @Override
    public void handleBlocksBreak(Map<Key, Integer> blocks, @PlotBlockFlags int flags) {
        handleBlocksBreakWithResult(blocks, flags);
    }

    @Override
    public Map<Key, BlockChangeResult> handleBlocksBreakWithResult(Map<Key, Integer> blocks, int flags) {
        Preconditions.checkNotNull(blocks, "blocks parameter cannot be null.");

        if (blocks.isEmpty())
            return KeyMaps.createEmptyMap();

        BigDecimal oldWorth = getWorth();
        BigDecimal oldLevel = getPlotLevel();

        KeyMap<BlockChangeResult> result = KeyMaps.createHashMap(KeyIndicator.MATERIAL);

        blocks.forEach((blockKey, amount) -> {
            BlockChangeResult blockResult = handleBlockBreakWithResult(blockKey, amount, 0);
            if (blockResult != BlockChangeResult.SUCCESS)
                result.put(blockKey, blockResult);
        });

        boolean saveBlockCounts = (flags & PlotBlockFlags.SAVE_BLOCK_COUNTS) != 0;
        boolean updateLastTimeStatus = (flags & PlotBlockFlags.UPDATE_LAST_TIME_STATUS) != 0;

        if (saveBlockCounts)
            saveBlockCounts(this.currentTotalBlockCounts.get(), oldWorth, oldLevel);

        if (updateLastTimeStatus)
            updateLastTime();

        return result.isEmpty() ? KeyMaps.createEmptyMap() : KeyMaps.unmodifiableKeyMap(result);
    }

    @Override
    public boolean isChunkDirty(World world, int chunkX, int chunkZ) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        Preconditions.checkArgument(isInside(world, chunkX, chunkZ), "Chunk must be within the plot boundaries.");
        return this.isChunkDirty(world.getName(), chunkX, chunkZ);
    }

    @Override
    public boolean isChunkDirty(String worldName, int chunkX, int chunkZ) {
        Preconditions.checkNotNull(worldName, "worldName parameter cannot be null.");
        WorldInfo worldInfo = plugin.getGrid().getPlotsWorldInfo(this, worldName);
        Preconditions.checkArgument(worldInfo == null || isChunkInside(chunkX, chunkZ),
                "Chunk must be within the plot boundaries.");
        return this.dirtyChunksContainer.isMarkedDirty(ChunkPosition.of(worldInfo, chunkX, chunkZ));
    }

    @Override
    public void markChunkDirty(World world, int chunkX, int chunkZ, boolean save) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        Preconditions.checkArgument(isInside(world, chunkX, chunkZ), "Chunk must be within the plot boundaries.");
        this.dirtyChunksContainer.markDirty(ChunkPosition.of(WorldInfo.of(world), chunkX, chunkZ), save);
    }

    @Override
    public void markChunkEmpty(World world, int chunkX, int chunkZ, boolean save) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        Preconditions.checkArgument(isInside(world, chunkX, chunkZ), "Chunk must be within the plot boundaries.");
        this.dirtyChunksContainer.markEmpty(ChunkPosition.of(WorldInfo.of(world), chunkX, chunkZ), save);
    }

    @Override
    public BigInteger getBlockCountAsBigInteger(Key key) {
        return this.blocksTracker.getBlockCount(key);
    }

    @Override
    public Map<Key, BigInteger> getBlockCountsAsBigInteger() {
        return this.blocksTracker.getBlockCounts();
    }

    @Override
    public BigInteger getExactBlockCountAsBigInteger(Key key) {
        return this.blocksTracker.getExactBlockCount(key);
    }

    @Override
    public void clearBlockCounts() {
        blocksTracker.clearBlockCounts();
        this.currentTotalBlockCounts.set(BigInteger.ZERO);

        plotWorth.set(BigDecimal.ZERO);
        plotLevel.set(BigDecimal.ZERO);

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_WORTH, this);
        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_LEVEL, this);
    }

    @Override
    public PlotBlocksTrackerAlgorithm getBlocksTracker() {
        return this.blocksTracker;
    }

    @Override
    public BigDecimal getWorth() {
        double bankWorthRate = BuiltinModules.BANK.bankWorthRate;

        BigDecimal plotWorth = this.plotWorth.get();
        BigDecimal plotBank = this.plotBank.getBalance();
        BigDecimal bonusWorth = this.bonusWorth.get();
        BigDecimal finalPlotWorth = (bankWorthRate <= 0 ? getRawWorth() : plotWorth.add(
                plotBank.multiply(BigDecimal.valueOf(bankWorthRate)))).add(bonusWorth);

        if (!plugin.getSettings().isNegativeWorth() && finalPlotWorth.compareTo(BigDecimal.ZERO) < 0)
            return BigDecimal.ZERO;

        return finalPlotWorth;
    }

    @Override
    public BigDecimal getRawWorth() {
        return plotWorth.get();
    }

    @Override
    public BigDecimal getBonusWorth() {
        return bonusWorth.get();
    }

    @Override
    public void setBonusWorth(BigDecimal bonusWorth) {
        Preconditions.checkNotNull(bonusWorth, "bonusWorth parameter cannot be null.");

        Log.debug(Debug.SET_BONUS_WORTH, owner.getName(), bonusWorth);

        BigDecimal oldBonusWorth = this.bonusWorth.getAndSet(bonusWorth);

        if (Objects.equals(oldBonusWorth, bonusWorth))
            return;

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_WORTH, this);
        plugin.getGrid().sortPlots(SortingTypes.BY_WORTH);

        PlotsDatabaseBridge.saveBonusWorth(this);
    }

    @Override
    public BigDecimal getBonusLevel() {
        return bonusLevel.get();
    }

    @Override
    public void setBonusLevel(BigDecimal bonusLevel) {
        Preconditions.checkNotNull(bonusLevel, "bonusLevel parameter cannot be null.");

        Log.debug(Debug.SET_BONUS_LEVEL, owner.getName(), bonusLevel);

        BigDecimal oldBonusLevel = this.bonusLevel.getAndSet(bonusLevel);

        if (Objects.equals(oldBonusLevel, bonusLevel))
            return;

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_LEVEL, this);
        plugin.getGrid().sortPlots(SortingTypes.BY_LEVEL);

        PlotsDatabaseBridge.saveBonusLevel(this);
    }

    @Override
    public BigDecimal getPlotLevel() {
        BigDecimal bonusLevel = this.bonusLevel.get();
        BigDecimal plotLevel = this.plotLevel.get().add(bonusLevel);

        if (plugin.getSettings().isRoundedPlotLevels()) {
            plotLevel = plotLevel.setScale(0, RoundingMode.HALF_UP);
        }

        if (!plugin.getSettings().isNegativeLevel() && plotLevel.compareTo(BigDecimal.ZERO) < 0)
            plotLevel = BigDecimal.ZERO;

        return plotLevel;
    }

    @Override
    public BigDecimal getRawLevel() {
        BigDecimal plotLevel = this.plotLevel.get();

        if (plugin.getSettings().isRoundedPlotLevels()) {
            plotLevel = plotLevel.setScale(0, RoundingMode.HALF_UP);
        }

        if (!plugin.getSettings().isNegativeLevel() && plotLevel.compareTo(BigDecimal.ZERO) < 0)
            plotLevel = BigDecimal.ZERO;

        return plotLevel;
    }

    @Override
    public UpgradeLevel getUpgradeLevel(Upgrade upgrade) {
        Preconditions.checkNotNull(upgrade, "upgrade parameter cannot be null.");
        return upgrade.getUpgradeLevel(getUpgrades().getOrDefault(upgrade.getName(), 1));
    }

    @Override
    public void setUpgradeLevel(Upgrade upgrade, int level) {
        Preconditions.checkNotNull(upgrade, "upgrade parameter cannot be null.");

        Log.debug(Debug.SET_UPGRADE, owner.getName(), upgrade.getName(), level);

        int currentLevel = getUpgradeLevel(upgrade).getLevel();

        if (currentLevel == level)
            return;

        upgrades.put(upgrade.getName(), Math.min(upgrade.getMaxUpgradeLevel(), level));

        lastUpgradeTime = System.currentTimeMillis();

        PlotsDatabaseBridge.saveUpgrade(this, upgrade, level);

        UpgradeLevel upgradeLevel = getUpgradeLevel(upgrade);

        // Level was downgraded, we need to clear the values of that level and sync all upgrades again
        if (currentLevel > level) {
            syncUpgrades(false);
        } else {
            syncUpgrade((SUpgradeLevel) upgradeLevel, false);
        }

        plugin.getMenus().refreshUpgrades(this);
    }

    @Override
    public Map<String, Integer> getUpgrades() {
        return Collections.unmodifiableMap(upgrades);
    }

    @Override
    public void syncUpgrades() {
        syncUpgrades(true);
    }

    /*
     *  Upgrade related methods
     */

    @Override
    public void updateUpgrades() {
        clearUpgrades(false);
        // We want to sync the default upgrade first, then the actual upgrades
        syncUpgrade(DefaultUpgradeLevel.getInstance(), false);
        // Syncing all real upgrades
        plugin.getUpgrades().getUpgrades().forEach(upgrade -> syncUpgrade((SUpgradeLevel) getUpgradeLevel(upgrade), false));
    }

    @Override
    public long getLastTimeUpgrade() {
        return lastUpgradeTime;
    }

    @Override
    public boolean hasActiveUpgradeCooldown() {
        long lastTimeUpgrade = getLastTimeUpgrade();
        long currentTime = System.currentTimeMillis();
        long upgradeCooldown = plugin.getSettings().getUpgradeCooldown();
        return upgradeCooldown > 0 && lastTimeUpgrade > 0 && currentTime - lastTimeUpgrade <= upgradeCooldown;
    }

    @Override
    public double getCropGrowthMultiplier() {
        return this.cropGrowth.readAndGet(Value::get);
    }

    @Override
    public void setCropGrowthMultiplier(double cropGrowth) {
        cropGrowth = Math.max(1, cropGrowth);

        Log.debug(Debug.SET_CROP_GROWTH, owner.getName(), cropGrowth);

        if (cropGrowth == getCropGrowthRaw())
            return;

        Value<Double> oldCropGrowth = this.cropGrowth.set(Value.fixed(cropGrowth));

        if (cropGrowth == Value.getRaw(oldCropGrowth, -1D))
            return;

        PlotsDatabaseBridge.saveCropGrowth(this);

        notifyCropGrowthChange(cropGrowth);
    }

    @Override
    public double getCropGrowthRaw() {
        return this.cropGrowth.readAndGet(cropGrowth -> cropGrowth.getRaw(-1D));
    }

    @Override
    public double getSpawnerRatesMultiplier() {
        return this.spawnerRates.readAndGet(Value::get);
    }

    @Override
    public void setSpawnerRatesMultiplier(double spawnerRates) {
        spawnerRates = Math.max(1, spawnerRates);

        Log.debug(Debug.SET_SPAWNER_RATES, owner.getName(), spawnerRates);

        Value<Double> oldSpawnerRates = this.spawnerRates.set(Value.fixed(spawnerRates));

        if (spawnerRates == Value.getRaw(oldSpawnerRates, -1D))
            return;

        PlotsDatabaseBridge.saveSpawnerRates(this);
    }

    @Override
    public double getSpawnerRatesRaw() {
        return this.spawnerRates.readAndGet(spawnerRates -> spawnerRates.getRaw(-1D));
    }

    @Override
    public double getMobDropsMultiplier() {
        return this.mobDrops.readAndGet(Value::get);
    }

    @Override
    public void setMobDropsMultiplier(double mobDrops) {
        mobDrops = Math.max(1, mobDrops);

        Log.debug(Debug.SET_MOB_DROPS, owner.getName(), mobDrops);

        Value<Double> oldMobDrops = this.mobDrops.set(Value.fixed(mobDrops));

        if (mobDrops == Value.getRaw(oldMobDrops, -1D))
            return;

        PlotsDatabaseBridge.saveMobDrops(this);
    }

    @Override
    public double getMobDropsRaw() {
        return this.mobDrops.readAndGet(mobDrops -> mobDrops.getRaw(-1D));
    }

    @Override
    public int getBlockLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Value<Integer> blockLimit = blockLimits.get(key);
        return blockLimit == null ? -1 : blockLimit.get();
    }

    @Override
    public int getExactBlockLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Value<Integer> blockLimit = blockLimits.getRaw(key, null);
        return blockLimit == null ? -1 : blockLimit.get();
    }

    @Override
    public Key getBlockLimitKey(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        return blockLimits.getKey(key, key);
    }

    @Override
    public Map<Key, Integer> getBlocksLimits() {
        KeyMap<Integer> blockLimits = KeyMap.createKeyMap();
        this.blockLimits.forEach((block, limit) -> blockLimits.put(block, limit.get()));
        return Collections.unmodifiableMap(blockLimits);
    }

    @Override
    public Map<Key, Integer> getCustomBlocksLimits() {
        return Collections.unmodifiableMap(this.blockLimits.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof SyncedValue))
                .collect(KeyMap.getCollector(Map.Entry::getKey, entry -> entry.getValue().get())));
    }

    @Override
    public void clearBlockLimits() {
        Log.debug(Debug.CLEAR_BLOCK_LIMITS, owner.getName());

        if (blockLimits.isEmpty())
            return;

        blockLimits.clear();
        PlotsDatabaseBridge.clearBlockLimits(this);
    }

    @Override
    public void setBlockLimit(Key key, int limit) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");

        int finalLimit = Math.max(0, limit);

        Log.debug(Debug.SET_BLOCK_LIMIT, owner.getName(), key, finalLimit);

        Value<Integer> oldLimit = blockLimits.put(key, Value.fixed(finalLimit));

        if (limit == Value.getRaw(oldLimit, -1))
            return;

        plugin.getBlockValues().addCustomBlockKey(key);
        PlotsDatabaseBridge.saveBlockLimit(this, key, limit);
    }

    @Override
    public void removeBlockLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");

        Log.debug(Debug.REMOVE_BLOCK_LIMIT, owner.getName(), key);

        Value<Integer> oldBlockLimit = blockLimits.remove(key);

        if (oldBlockLimit == null)
            return;

        PlotsDatabaseBridge.removeBlockLimit(this, key);
    }

    @Override
    public boolean hasReachedBlockLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        return hasReachedBlockLimit(key, 1);
    }

    @Override
    public boolean hasReachedBlockLimit(Key key, @Size int amount) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkArgument(amount >= 0, "amount parameter must be non-negative.");

        int blockLimit = getExactBlockLimit(key);

        //Checking for the specific provided key.
        if (blockLimit >= 0) {
            return getBlockCountAsBigInteger(key).add(BigInteger.valueOf(amount))
                    .compareTo(BigInteger.valueOf(blockLimit)) > 0;
        }

        //Getting the global key values.
        key = ((BaseKey<?>) key).toGlobalKey();
        blockLimit = getBlockLimit(key);

        return blockLimit >= 0 && getBlockCountAsBigInteger(key)
                .add(BigInteger.valueOf(amount)).compareTo(BigInteger.valueOf(blockLimit)) > 0;
    }

    @Override
    public int getEntityLimit(EntityType entityType) {
        Preconditions.checkNotNull(entityType, "entityType parameter cannot be null.");
        return getEntityLimit(Keys.of(entityType));
    }

    @Override
    public int getEntityLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Value<Integer> entityLimit = entityLimits.get(key);
        return entityLimit == null ? -1 : entityLimit.get();
    }

    @Override
    public Map<Key, Integer> getEntitiesLimitsAsKeys() {
        return Collections.unmodifiableMap(this.entityLimits.entrySet().stream().collect(
                KeyMap.getCollector(Map.Entry::getKey, entry -> entry.getValue().get())
        ));
    }

    @Override
    public Map<Key, Integer> getCustomEntitiesLimits() {
        return Collections.unmodifiableMap(this.entityLimits.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof SyncedValue))
                .collect(KeyMap.getCollector(Map.Entry::getKey, entry -> entry.getValue().get())));
    }

    @Override
    public void clearEntitiesLimits() {
        Log.debug(Debug.CLEAR_ENTITY_LIMITS, owner.getName());

        if (entityLimits.isEmpty())
            return;

        entityLimits.clear();
        PlotsDatabaseBridge.clearEntityLimits(this);
    }

    @Override
    public void setEntityLimit(EntityType entityType, int limit) {
        Preconditions.checkNotNull(entityType, "entityType parameter cannot be null.");
        setEntityLimit(Keys.of(entityType), limit);
    }

    @Override
    public void setEntityLimit(Key key, int limit) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");

        int finalLimit = Math.max(0, limit);

        Log.debug(Debug.SET_ENTITY_LIMIT, owner.getName(), key, finalLimit);

        Value<Integer> oldEntityLimit = entityLimits.put(key, Value.fixed(limit));

        if (limit == Value.getRaw(oldEntityLimit, -1))
            return;

        PlotsDatabaseBridge.saveEntityLimit(this, key, limit);
    }

    @Override
    public CompletableFuture<Boolean> hasReachedEntityLimit(EntityType entityType) {
        Preconditions.checkNotNull(entityType, "entityType parameter cannot be null.");
        return hasReachedEntityLimit(Keys.of(entityType));
    }

    @Override
    public CompletableFuture<Boolean> hasReachedEntityLimit(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        return hasReachedEntityLimit(key, 1);
    }

    @Override
    public CompletableFuture<Boolean> hasReachedEntityLimit(EntityType entityType, @Size int amount) {
        Preconditions.checkNotNull(entityType, "entityType parameter cannot be null.");
        return hasReachedEntityLimit(Keys.of(entityType), amount);
    }

    @Override
    public CompletableFuture<Boolean> hasReachedEntityLimit(Key key, @Size int amount) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkArgument(amount >= 0, "amount parameter must be non-negative.");

        int entityLimit = getEntityLimit(key);

        if (entityLimit < 0)
            return CompletableFuture.completedFuture(false);

        return CompletableFuture.completedFuture(this.entitiesTracker.getEntityCount(key) + amount > entityLimit);
    }

    @Override
    public PlotEntitiesTrackerAlgorithm getEntitiesTracker() {
        return this.entitiesTracker;
    }

    @Override
    public int getTeamLimit() {
        return this.teamLimit.readAndGet(Value::get);
    }

    @Override
    public void setTeamLimit(int teamLimit) {
        teamLimit = Math.max(0, teamLimit);

        Log.debug(Debug.SET_TEAM_LIMIT, owner.getName(), teamLimit);

        Value<Integer> oldTeamLimit = this.teamLimit.set(Value.fixed(teamLimit));

        if (teamLimit == Value.getRaw(oldTeamLimit, -1))
            return;

        PlotsDatabaseBridge.saveTeamLimit(this);
    }

    @Override
    public int getTeamLimitRaw() {
        return this.teamLimit.readAndGet(teamLimit -> teamLimit.getRaw(-1));
    }

    @Override
    public int getWarpsLimit() {
        return this.warpsLimit.readAndGet(Value::get);
    }

    @Override
    public void setWarpsLimit(int warpsLimit) {
        warpsLimit = Math.max(0, warpsLimit);

        Log.debug(Debug.SET_WARPS_LIMIT, owner.getName(), warpsLimit);

        Value<Integer> oldWarpsLimit = this.warpsLimit.set(Value.fixed(warpsLimit));

        if (warpsLimit == Value.getRaw(oldWarpsLimit, -1))
            return;

        PlotsDatabaseBridge.saveWarpsLimit(this);
    }

    @Override
    public int getWarpsLimitRaw() {
        return this.warpsLimit.readAndGet(warpsLimit -> warpsLimit.getRaw(-1));
    }

    @Override
    public void setPotionEffect(PotionEffectType type, int level) {
        // Legacy support for levels can be set to <= 0 for removing the effect.
        // Nowadays, removePotionEffect exists.
        if (level <= 0) {
            removePotionEffect(type);
            return;
        }

        Preconditions.checkNotNull(type, "potionEffectType parameter cannot be null.");

        Log.debug(Debug.SET_PLOT_EFFECT, owner.getName(), type.getName(), level);

        Value<Integer> oldPotionLevel = plotEffects.put(type, Value.fixed(level));

        if (level == Value.getRaw(oldPotionLevel, -1))
            return;

        BukkitExecutor.ensureMain(() -> getAllPlayersInside().forEach(superiorPlayer -> {
            Player player = superiorPlayer.asPlayer();
            assert player != null;
            if (oldPotionLevel != null && oldPotionLevel.get() > level)
                player.removePotionEffect(type);

            PotionEffect potionEffect = new PotionEffect(type, Integer.MAX_VALUE, level - 1);
            player.addPotionEffect(potionEffect, true);
        }));

        PlotsDatabaseBridge.savePlotEffect(this, type, level);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        Preconditions.checkNotNull(type, "potionEffectType parameter cannot be null.");

        Log.debug(Debug.REMOVE_PLOT_EFFECT, owner.getName(), type.getName());

        Value<Integer> oldEffectLevel = plotEffects.remove(type);

        if (oldEffectLevel == null)
            return;

        BukkitExecutor.ensureMain(() -> getAllPlayersInside().forEach(superiorPlayer -> {
            Player player = superiorPlayer.asPlayer();
            if (player != null)
                player.removePotionEffect(type);
        }));

        PlotsDatabaseBridge.removePlotEffect(this, type);
    }

    @Override
    public int getPotionEffectLevel(PotionEffectType type) {
        Preconditions.checkNotNull(type, "potionEffectType parameter cannot be null.");
        Value<Integer> plotEffect = plotEffects.get(type);
        return plotEffect == null ? -1 : plotEffect.get();
    }

    @Override
    public Map<PotionEffectType, Integer> getPotionEffects() {
        Map<PotionEffectType, Integer> plotEffects = new HashMap<>();

        for (PotionEffectType potionEffectType : PotionEffectType.values()) {
            if (potionEffectType != null) {
                int level = getPotionEffectLevel(potionEffectType);
                if (level > 0)
                    plotEffects.put(potionEffectType, level);
            }
        }

        return plotEffects;
    }

    @Override
    public void applyEffects(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        if (!BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypePlotEffects.class))
            return;

        applyEffectsNoUpgradeCheck(superiorPlayer);
    }

    @Override
    public void applyEffects() {
        if (BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypePlotEffects.class))
            getAllPlayersInside().forEach(this::applyEffectsNoUpgradeCheck);
    }

    @Override
    public void removeEffects(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        if (BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypePlotEffects.class))
            removeEffectsNoUpgradeCheck(superiorPlayer);
    }

    @Override
    public void removeEffects() {
        if (BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypePlotEffects.class))
            getAllPlayersInside().forEach(this::removeEffectsNoUpgradeCheck);
    }

    @Override
    public void clearEffects() {
        Log.debug(Debug.CLEAR_PLOT_EFFECTS, owner.getName());

        if (plotEffects.isEmpty())
            return;

        plotEffects.clear();
        removeEffects();

        PlotsDatabaseBridge.clearPlotEffects(this);
    }

    @Override
    public void setRoleLimit(PlayerRole playerRole, int limit) {
        // Legacy support for limits can be set to < 0 for removing the limit.
        // Nowadays, removeRoleLimit exists.
        if (limit < 0) {
            removeRoleLimit(playerRole);
            return;
        }

        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");

        Log.debug(Debug.SET_ROLE_LIMIT, owner.getName(), playerRole.getName(), limit);

        Value<Integer> oldRoleLimit = roleLimits.put(playerRole, Value.fixed(limit));

        if (limit == Value.getRaw(oldRoleLimit, -1))
            return;

        PlotsDatabaseBridge.saveRoleLimit(this, playerRole, limit);
    }

    @Override
    public void removeRoleLimit(PlayerRole playerRole) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");

        Log.debug(Debug.REMOVE_ROLE_LIMIT, owner.getName(), playerRole.getName());

        Value<Integer> oldRoleLimit = roleLimits.remove(playerRole);

        if (oldRoleLimit == null)
            return;

        PlotsDatabaseBridge.removeRoleLimit(this, playerRole);
    }

    @Override
    public int getRoleLimit(PlayerRole playerRole) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        Value<Integer> roleLimit = roleLimits.get(playerRole);
        return roleLimit == null ? -1 : roleLimit.get();
    }

    @Override
    public int getRoleLimitRaw(PlayerRole playerRole) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        return Value.getRaw(roleLimits.get(playerRole), -1);
    }

    @Override
    public Map<PlayerRole, Integer> getRoleLimits() {
        return roleLimits.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    @Override
    public Map<PlayerRole, Integer> getCustomRoleLimits() {
        return this.roleLimits.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof SyncedValue))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    @Override
    public WarpCategory createWarpCategory(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        Log.debug(Debug.CREATE_WARP_CATEGORY, owner.getName(), name);

        WarpCategory warpCategory = warpCategories.get(name.toLowerCase(Locale.ENGLISH));

        if (warpCategory == null) {
            Log.debugResult(Debug.CREATE_WARP_CATEGORY, "Result New Category", name);
            List<Integer> occupiedSlots = warpCategories.values().stream().map(WarpCategory::getSlot).collect(Collectors.toList());

            int slot = 0;
            while (occupiedSlots.contains(slot))
                ++slot;

            warpCategory = loadWarpCategory(name, slot, null);

            PlotsDatabaseBridge.saveWarpCategory(this, warpCategory);

            plugin.getMenus().refreshWarpCategories(this);
        } else {
            Log.debugResult(Debug.CREATE_WARP_CATEGORY, "Result Already Exists", name);
        }

        return warpCategory;
    }

    @Override
    public WarpCategory getWarpCategory(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        return warpCategories.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public WarpCategory getWarpCategory(int slot) {
        return warpCategories.values().stream().filter(warpCategory -> warpCategory.getSlot() == slot)
                .findAny().orElse(null);
    }

    @Override
    public void renameCategory(WarpCategory warpCategory, String newName) {
        Preconditions.checkNotNull(warpCategory, "warpCategory parameter cannot be null.");
        Preconditions.checkNotNull(newName, "newName parameter cannot be null.");

        warpCategories.remove(warpCategory.getName().toLowerCase(Locale.ENGLISH));
        warpCategories.put(newName.toLowerCase(Locale.ENGLISH), warpCategory);
        warpCategory.setName(newName);
    }

    @Override
    public void deleteCategory(WarpCategory warpCategory) {
        Preconditions.checkNotNull(warpCategory, "warpCategory parameter cannot be null.");

        Log.debug(Debug.DELETE_WARP_CATEGORY, owner.getName(), warpCategory.getName());

        boolean validCategoryRemoval = warpCategories.remove(warpCategory.getName().toLowerCase(Locale.ENGLISH)) != null;

        if (!validCategoryRemoval)
            return;

        PlotsDatabaseBridge.removeWarpCategory(this, warpCategory);

        boolean shouldSaveWarps = !warpCategory.getWarps().isEmpty();

        if (shouldSaveWarps) {
            new LinkedList<>(warpCategory.getWarps()).forEach(plotWarp -> deleteWarp(plotWarp.getName()));
            plugin.getMenus().destroyWarps(warpCategory);
        }

        plugin.getMenus().destroyWarpCategories(this);
    }

    @Override
    public Map<String, WarpCategory> getWarpCategories() {
        return Collections.unmodifiableMap(warpCategories);
    }

    @Override
    public PlotWarp createWarp(String name, Location location, @Nullable WarpCategory warpCategory) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        if (!(location instanceof LazyWorldLocation))
            Preconditions.checkNotNull(location.getWorld(), "location's world cannot be null.");
        Preconditions.checkState(getWarp(name) == null, "Warp already exists: " + name);

        Log.debug(Debug.CREATE_WARP, owner.getName(), name, location, warpCategory);

        PlotWarp plotWarp = loadPlotWarp(name, location, warpCategory, !plugin.getSettings().isPublicWarps(), null);

        PlotsDatabaseBridge.saveWarp(this, plotWarp);

        plugin.getMenus().refreshGlobalWarps();
        plugin.getMenus().refreshWarps(plotWarp.getCategory());

        return plotWarp;
    }

    /*
     *  Warps related methods
     */

    @Override
    public void renameWarp(PlotWarp plotWarp, String newName) {
        Preconditions.checkNotNull(plotWarp, "plotWarp parameter cannot be null.");
        Preconditions.checkNotNull(newName, "newName parameter cannot be null.");
        Preconditions.checkArgument(PlotUtils.isWarpNameLengthValid(newName), "Warp names must cannot be longer than 255 chars.");
        Preconditions.checkState(getWarp(newName) == null, "Cannot rename warps to an already existing warps.");

        warpsByName.remove(plotWarp.getName().toLowerCase(Locale.ENGLISH));
        warpsByName.put(newName.toLowerCase(Locale.ENGLISH), plotWarp);
        plotWarp.setName(newName);
    }

    @Override
    public PlotWarp getWarp(Location location) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        return warpsByLocation.get(new LocationKey(location));
    }

    @Override
    public PlotWarp getWarp(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        return warpsByName.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void warpPlayer(SuperiorPlayer superiorPlayer, String warp) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(warp, "warp parameter cannot be null.");

        PlotWarp plotWarp = getWarp(warp);

        if (plotWarp == null) {
            Message.INVALID_WARP.send(superiorPlayer, warp);
            return;
        }

        if (plugin.getSettings().getWarpsWarmup() > 0 && !superiorPlayer.hasBypassModeEnabled() &&
                !superiorPlayer.hasPermission("superior.admin.bypass.warmup")) {
            Message.TELEPORT_WARMUP.send(superiorPlayer, Formatters.TIME_FORMATTER.format(
                    Duration.ofMillis(plugin.getSettings().getWarpsWarmup()), superiorPlayer.getUserLocale()));
            superiorPlayer.setTeleportTask(BukkitExecutor.sync(() ->
                    warpPlayerWithoutWarmup(superiorPlayer, plotWarp), plugin.getSettings().getWarpsWarmup() / 50));
        } else {
            warpPlayerWithoutWarmup(superiorPlayer, plotWarp);
        }
    }

    @Override
    public void deleteWarp(@Nullable SuperiorPlayer superiorPlayer, Location location) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");

        PlotWarp plotWarp = warpsByLocation.remove(new LocationKey(location));
        if (plotWarp != null) {
            deleteWarp(plotWarp.getName());
            if (superiorPlayer != null)
                Message.DELETE_WARP.send(superiorPlayer, plotWarp.getName());
        }
    }

    @Override
    public void deleteWarp(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        Log.debug(Debug.DELETE_WARP, owner.getName(), name);

        PlotWarp plotWarp = warpsByName.remove(name.toLowerCase(Locale.ENGLISH));
        WarpCategory warpCategory = plotWarp == null ? null : plotWarp.getCategory();

        if (plotWarp != null) {
            warpsByLocation.remove(new LocationKey(plotWarp.getLocation()));
            warpCategory.getWarps().remove(plotWarp);

            PlotsDatabaseBridge.removeWarp(this, plotWarp);

            if (warpCategory.getWarps().isEmpty())
                deleteCategory(warpCategory);
        }

        plugin.getMenus().refreshGlobalWarps();

        if (warpCategory != null)
            plugin.getMenus().refreshWarps(warpCategory);
    }

    @Override
    public Map<String, PlotWarp> getPlotWarps() {
        return Collections.unmodifiableMap(warpsByName);
    }

    @Override
    public Rating getRating(SuperiorPlayer superiorPlayer) {
        return ratings.getOrDefault(superiorPlayer.getUniqueId(), Rating.UNKNOWN);
    }

    @Override
    public void setRating(SuperiorPlayer superiorPlayer, Rating rating) {
        // Legacy support for rating can be set to UNKNOWN in order to remove rating.
        // Nowadays, removeRating exists.
        if (rating == Rating.UNKNOWN) {
            removeRating(superiorPlayer);
            return;
        }

        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(rating, "rating parameter cannot be null.");

        Log.debug(Debug.SET_RATING, owner.getName(), superiorPlayer.getName(), rating);

        Rating oldRating = ratings.put(superiorPlayer.getUniqueId(), rating);

        if (rating == oldRating)
            return;

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_RATING, this);

        PlotsDatabaseBridge.saveRating(this, superiorPlayer, rating, System.currentTimeMillis());

        plugin.getMenus().refreshPlotRatings(this);
    }

    @Override
    public void removeRating(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");

        Log.debug(Debug.REMOVE_RATING, owner.getName(), superiorPlayer.getName());

        Rating oldRating = ratings.remove(superiorPlayer.getUniqueId());

        if (oldRating == null)
            return;

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_RATING, this);

        PlotsDatabaseBridge.removeRating(this, superiorPlayer);

        plugin.getMenus().refreshPlotRatings(this);
    }

    @Override
    public double getTotalRating() {
        double avg = 0;

        for (Rating rating : ratings.values())
            avg += rating.getValue();

        return avg == 0 ? 0 : avg / getRatingAmount();
    }

    @Override
    public int getRatingAmount() {
        return ratings.size();
    }

    @Override
    public Map<UUID, Rating> getRatings() {
        return Collections.unmodifiableMap(ratings);
    }

    @Override
    public void removeRatings() {
        Log.debug(Debug.REMOVE_RATINGS, owner.getName());

        if (ratings.isEmpty())
            return;

        ratings.clear();

        plugin.getGrid().getPlotsContainer().notifyChange(SortingTypes.BY_RATING, this);

        PlotsDatabaseBridge.clearRatings(this);

        plugin.getMenus().refreshPlotRatings(this);
    }

    @Override
    public boolean hasSettingsEnabled(PlotFlag settings) {
        Preconditions.checkNotNull(settings, "settings parameter cannot be null.");
        return plotFlags.getOrDefault(settings, (byte) (plugin.getSettings().getDefaultSettings().contains(settings.getName()) ? 1 : 0)) == 1;
    }

    @Override
    public Map<PlotFlag, Byte> getAllSettings() {
        return Collections.unmodifiableMap(plotFlags);
    }

    @Override
    public void enableSettings(PlotFlag settings) {
        Preconditions.checkNotNull(settings, "settings parameter cannot be null.");

        Log.debug(Debug.ENABLE_PLOT_FLAG, owner.getName(), settings.getName());

        Byte oldStatus = plotFlags.put(settings, (byte) 1);

        if (Objects.equals(oldStatus, 1))
            return;

        boolean disableTime = false;
        boolean disableWeather = false;

        //Updating times / weather if necessary
        switch (settings.getName()) {
            case "ALWAYS_DAY":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerTime(0, false);
                });
                disableTime = true;
                break;
            case "ALWAYS_MIDDLE_DAY":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerTime(6000, false);
                });
                disableTime = true;
                break;
            case "ALWAYS_NIGHT":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerTime(14000, false);
                });
                disableTime = true;
                break;
            case "ALWAYS_MIDDLE_NIGHT":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerTime(18000, false);
                });
                disableTime = true;
                break;
            case "ALWAYS_SHINY":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerWeather(WeatherType.CLEAR);
                });
                disableWeather = true;
                break;
            case "ALWAYS_RAIN":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.setPlayerWeather(WeatherType.DOWNFALL);
                });
                disableWeather = true;
                break;
            case "PVP":
                if (plugin.getSettings().isTeleportOnPvPEnable())
                    getPlotVisitors().forEach(superiorPlayer -> {
                        superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
                        Message.PLOT_GOT_PVP_ENABLED_WHILE_INSIDE.send(superiorPlayer);
                    });
                break;
        }

        if (disableTime) {
            if (settings != PlotFlags.ALWAYS_DAY && plotFlags.remove(PlotFlags.ALWAYS_DAY) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_DAY);
            if (settings != PlotFlags.ALWAYS_MIDDLE_DAY && plotFlags.remove(PlotFlags.ALWAYS_MIDDLE_DAY) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_MIDDLE_DAY);
            if (settings != PlotFlags.ALWAYS_NIGHT && plotFlags.remove(PlotFlags.ALWAYS_NIGHT) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_NIGHT);
            if (settings != PlotFlags.ALWAYS_MIDDLE_NIGHT && plotFlags.remove(PlotFlags.ALWAYS_MIDDLE_NIGHT) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_MIDDLE_NIGHT);
        }

        if (disableWeather) {
            if (settings != PlotFlags.ALWAYS_RAIN && plotFlags.remove(PlotFlags.ALWAYS_RAIN) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_RAIN);
            if (settings != PlotFlags.ALWAYS_SHINY && plotFlags.remove(PlotFlags.ALWAYS_SHINY) != null)
                PlotsDatabaseBridge.removePlotFlag(this, PlotFlags.ALWAYS_SHINY);
        }

        PlotsDatabaseBridge.savePlotFlag(this, settings, 1);

        plugin.getMenus().refreshSettings(this);
    }

    /*
     *  Ratings related methods
     */

    @Override
    public void disableSettings(PlotFlag settings) {
        Preconditions.checkNotNull(settings, "settings parameter cannot be null.");

        Log.debug(Debug.DISABLE_PLOT_FLAG, owner.getName(), settings.getName());

        Byte oldStatus = plotFlags.put(settings, (byte) 0);

        if (Objects.equals(oldStatus, 0))
            return;

        switch (settings.getName()) {
            case "ALWAYS_DAY":
            case "ALWAYS_MIDDLE_DAY":
            case "ALWAYS_NIGHT":
            case "ALWAYS_MIDDLE_NIGHT":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.resetPlayerTime();
                });
                break;
            case "ALWAYS_RAIN":
            case "ALWAYS_SHINY":
                getAllPlayersInside().forEach(superiorPlayer -> {
                    Player player = superiorPlayer.asPlayer();
                    if (player != null)
                        player.resetPlayerWeather();
                });
                break;
        }

        PlotsDatabaseBridge.savePlotFlag(this, settings, 0);

        plugin.getMenus().refreshSettings(this);
    }

    @Override
    public void setGeneratorPercentage(Key key, int percentage, World.Environment environment) {
        setGeneratorPercentage(key, percentage, environment, null, false);
    }

    @Override
    public boolean setGeneratorPercentage(Key key, int percentage, World.Environment environment,
                                          @Nullable SuperiorPlayer caller, boolean callEvent) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.SET_GENERATOR_PERCENTAGE, owner.getName(), key, percentage, environment, caller, callEvent);

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.writeAndGet(cobbleGeneratorValues ->
                cobbleGeneratorValues.computeIfAbsent(environment, e -> KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL)));

        Preconditions.checkArgument(percentage >= 0 && percentage <= 100, "Percentage must be between 0 and 100 - got " + percentage + ".");

        if (percentage == 0) {
            if (callEvent && !plugin.getEventsBus().callPlotRemoveGeneratorRateEvent(caller, this, key, environment))
                return false;

            removeGeneratorAmount(key, environment);
        } else if (percentage == 100) {
            KeyMap<Value<Integer>> cobbleGeneratorValuesOriginal = KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL, worldGeneratorRates);
            worldGeneratorRates.clear();

            int generatorRate = 1;

            if (callEvent) {
                EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeGeneratorRateEvent(caller, this, key, environment, generatorRate);
                if (eventResult.isCancelled()) {
                    // Restore the original values
                    worldGeneratorRates.putAll(cobbleGeneratorValuesOriginal);
                    return false;
                }
                generatorRate = eventResult.getResult();
            }

            setGeneratorAmount(key, generatorRate, environment);
        } else {
            //Removing the key from the generator
            removeGeneratorAmount(key, environment);

            int totalAmount = getGeneratorTotalAmount(environment);
            double realPercentage = percentage / 100D;
            double amount = (realPercentage * totalAmount) / (1 - realPercentage);
            if (amount < 1) {
                worldGeneratorRates.entrySet().forEach(entry -> {
                    int newAmount = entry.getValue().get() * 10;
                    if (entry.getValue() instanceof SyncedValue) {
                        entry.setValue(Value.syncedFixed(newAmount));
                    } else {
                        entry.setValue(Value.fixed(newAmount));
                    }
                });
                amount *= 10;
            }

            EventResult<Integer> eventResult = plugin.getEventsBus().callPlotChangeGeneratorRateEvent(caller,
                    this, key, environment, (int) Math.round(amount));

            if (eventResult.isCancelled())
                return false;

            setGeneratorAmount(key, eventResult.getResult(), environment);
        }

        return true;
    }

    @Override
    public int getGeneratorPercentage(Key key, World.Environment environment) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        int totalAmount = getGeneratorTotalAmount(environment);
        return totalAmount == 0 ? 0 : (getGeneratorAmount(key, environment) * 100) / totalAmount;
    }

    @Override
    public Map<String, Integer> getGeneratorPercentages(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        return getGeneratorAmounts(environment).keySet().stream().collect(Collectors.toMap(key -> key,
                key -> getGeneratorAmount(Keys.ofMaterialAndData(key), environment)));
    }

    @Override
    public void setGeneratorAmount(Key key, @Size int amount, World.Environment environment) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        Preconditions.checkArgument(amount >= 0, "amount parameter must be non-negative.");

        Log.debug(Debug.SET_GENERATOR_RATE, owner.getName(), key, amount, environment);

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.writeAndGet(cobbleGeneratorValues ->
                cobbleGeneratorValues.computeIfAbsent(environment, e -> KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL)));

        Value<Integer> oldGeneratorRate = worldGeneratorRates.put(key, Value.fixed(amount));

        if (amount == Value.getRaw(oldGeneratorRate, -1))
            return;

        PlotsDatabaseBridge.saveGeneratorRate(this, environment, key, amount);
    }

    @Override
    public void removeGeneratorAmount(Key key, World.Environment environment) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.REMOVE_GENERATOR_RATE, owner.getName(), key, environment);

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.readAndGet(
                cobbleGeneratorValues -> cobbleGeneratorValues.get(environment));

        if (worldGeneratorRates == null)
            return;

        Value<Integer> oldGeneratorRate = worldGeneratorRates.get(key);

        if (oldGeneratorRate == null || oldGeneratorRate.get() <= 0)
            return;

        if (oldGeneratorRate instanceof SyncedValue) {
            // In case the old rate was upgrade-synced, we want to keep it in DB and cache as a 0 rate.
            PlotsDatabaseBridge.saveGeneratorRate(this, environment, key, 0);
            worldGeneratorRates.put(key, Value.fixed(0));
        } else {
            PlotsDatabaseBridge.removeGeneratorRate(this, environment, key);
            worldGeneratorRates.remove(key);
        }
    }

    /*
     *  Missions related methods
     */

    @Override
    public int getGeneratorAmount(Key key, World.Environment environment) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.readAndGet(
                cobbleGeneratorValues -> cobbleGeneratorValues.get(environment));

        if (worldGeneratorRates == null)
            return 0;

        Value<Integer> generatorRate = worldGeneratorRates.get(key);
        return generatorRate == null ? 0 : generatorRate.get();
    }

    @Override
    public int getGeneratorTotalAmount(World.Environment environment) {
        int totalAmount = 0;
        for (int amt : getGeneratorAmounts(environment).values())
            totalAmount += amt;
        return totalAmount;
    }

    @Override
    public Map<String, Integer> getGeneratorAmounts(World.Environment environment) {
        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.readAndGet(
                cobbleGeneratorValues -> cobbleGeneratorValues.get(environment));

        if (worldGeneratorRates == null)
            return Collections.emptyMap();

        Map<String, Integer> generatorAmountsResult = new HashMap<>();

        worldGeneratorRates.forEach((blockKey, valueAmount) -> {
            int amount = valueAmount.get();
            if (amount > 0) {
                generatorAmountsResult.put(blockKey.toString(), amount);
            }
        });

        return generatorAmountsResult.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(generatorAmountsResult);
    }

    @Override
    public Map<Key, Integer> getCustomGeneratorAmounts(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.readAndGet(
                cobbleGeneratorValues -> cobbleGeneratorValues.get(environment));

        if (worldGeneratorRates == null)
            return Collections.emptyMap();

        return Collections.unmodifiableMap(worldGeneratorRates.entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof SyncedValue))
                .collect(KeyMap.getCollector(Map.Entry::getKey, entry -> entry.getValue().get())));
    }

    @Override
    public void clearGeneratorAmounts(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.CLEAR_GENERATOR_RATES, owner.getName(), environment);

        KeyMap<Value<Integer>> worldGeneratorRates = this.cobbleGeneratorValues.readAndGet(
                cobbleGeneratorValues -> cobbleGeneratorValues.get(environment));
        if (worldGeneratorRates != null && !worldGeneratorRates.isEmpty()) {
            worldGeneratorRates.clear();
            PlotsDatabaseBridge.clearGeneratorRates(this, environment);
        }
    }

    @Nullable
    @Override
    public Key generateBlock(Location location, boolean optimizeCobblestone) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        Preconditions.checkNotNull(location.getWorld(), "location's world cannot be null.");
        return generateBlock(location, location.getWorld().getEnvironment(), optimizeCobblestone);
    }

    @Override
    public Key generateBlock(Location location, World.Environment environment, boolean optimizeCobblestone) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        Preconditions.checkNotNull(location.getWorld(), "location's world cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.GENERATE_BLOCK, owner.getName(), location, environment, optimizeCobblestone);

        int totalGeneratorAmounts = getGeneratorTotalAmount(environment);

        if (totalGeneratorAmounts == 0) {
            Log.debugResult(Debug.GENERATE_BLOCK, "Return No Generator Rates", "null");
            return null;
        }

        Map<String, Integer> generatorAmounts = getGeneratorAmounts(environment);

        Key newStateKey = ConstantKeys.COBBLESTONE;

        if (totalGeneratorAmounts == 1) {
            newStateKey = Keys.ofMaterialAndData(generatorAmounts.keySet().iterator().next());
        } else {
            int generatedIndex = ThreadLocalRandom.current().nextInt(totalGeneratorAmounts);
            int currentIndex = 0;
            for (Map.Entry<String, Integer> entry : generatorAmounts.entrySet()) {
                currentIndex += entry.getValue();
                if (generatedIndex < currentIndex) {
                    newStateKey = Keys.ofMaterialAndData(entry.getKey());
                    break;
                }
            }
        }

        EventResult<EventsBus.GenerateBlockResult> eventResult = plugin.getEventsBus().callPlotGenerateBlockEvent(
                this, location, newStateKey);

        if (eventResult.isCancelled()) {
            Log.debugResult(Debug.GENERATE_BLOCK, "Return Event Cancelled", "null");
            return null;
        }

        Key generatedBlock = eventResult.getResult().getBlock();

        if (optimizeCobblestone && generatedBlock.getGlobalKey().equals("COBBLESTONE")) {
            Log.debugResult(Debug.GENERATE_BLOCK, "Return Cobblestone", generatedBlock);
            return generatedBlock;
        }

        // If the block is a custom block, and the event was cancelled - we need to call the handleBlockPlace manually.
        handleBlockPlace(generatedBlock, 1);

        // Checking whether the plugin should set the block in the world.
        if (eventResult.getResult().isPlaceBlock()) {
            int combinedId;

            try {
                Material generateBlockType = Material.valueOf(generatedBlock.getGlobalKey());
                byte blockData = generatedBlock.getSubKey().isEmpty() ? 0 : Byte.parseByte(generatedBlock.getSubKey());
                combinedId = plugin.getNMSAlgorithms().getCombinedId(generateBlockType, blockData);
            } catch (IllegalArgumentException error) {
                Log.error("Invalid block for generating block: ", generatedBlock);
                combinedId = plugin.getNMSAlgorithms().getCombinedId(Material.COBBLESTONE, (byte) 0);
            }

            plugin.getNMSWorld().setBlock(location, combinedId);
        }

        plugin.getNMSWorld().playGeneratorSound(location);

        Log.debugResult(Debug.GENERATE_BLOCK, "Return", generatedBlock);

        return generatedBlock;
    }

    @Override
    public boolean wasSchematicGenerated(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        int generateBitChange = getGeneratedSchematicBitMask(environment);

        if (generateBitChange == 0)
            return false;

        return (generatedSchematics.get() & generateBitChange) != 0;
    }

    @Override
    public void setSchematicGenerate(World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        setSchematicGenerate(environment, true);
    }

    @Override
    public void setSchematicGenerate(World.Environment environment, boolean generated) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");

        Log.debug(Debug.SET_SCHEMATIC, environment, generated);

        int generateBitChange = getGeneratedSchematicBitMask(environment);

        if (generateBitChange == 0)
            return;

        Mutable<Boolean> updatedGeneratedSchematics = new Mutable<>(false);

        this.generatedSchematics.updateAndGet(generatedSchematics -> {
            int newGeneratedSchematics = generated ? generatedSchematics | generateBitChange : generatedSchematics & ~generateBitChange & 0xF;
            updatedGeneratedSchematics.setValue(newGeneratedSchematics != generatedSchematics);
            return newGeneratedSchematics;
        });

        if (!updatedGeneratedSchematics.getValue())
            return;

        PlotsDatabaseBridge.saveGeneratedSchematics(this);
    }

    /*
     *  Settings related methods
     */

    @Override
    public int getGeneratedSchematicsFlag() {
        return this.generatedSchematics.get();
    }

    @Override
    public String getSchematicName() {
        return this.schemName == null ? "" : this.schemName;
    }

    @Override
    public int getPosition(SortingType sortingType) {
        return plugin.getGrid().getPlotPosition(this, sortingType);
    }

    @Override
    public PlotChest[] getChest() {
        return plotChests.readAndGet(plotChests -> Arrays.copyOf(plotChests, plotChests.length));
    }

    /*
     *  Generator related methods
     */

    @Override
    public int getChestSize() {
        return plotChests.readAndGet(plotChests -> plotChests.length);
    }

    @Override
    public void setChestRows(int index, int rows) {
        PlotChest[] plotChests = this.plotChests.get();
        int oldSize = plotChests.length;

        boolean updatedPlotChests = false;

        if (index >= oldSize) {
            updatedPlotChests = true;
            plotChests = Arrays.copyOf(plotChests, index + 1);
            this.plotChests.set(plotChests);
            for (int i = oldSize; i <= index; i++) {
                (plotChests[i] = new SPlotChest(this, i)).setRows(plugin.getSettings().getPlotChests().getDefaultSize());
            }
        }

        PlotChest plotChest = plotChests[index];

        if (!updatedPlotChests && plotChest.getRows() == rows)
            return;

        plotChests[index].setRows(rows);

        PlotsDatabaseBridge.markPlotChestsToBeSaved(this, plotChests[index]);
    }

    private void calcPlotWorthInternal(@Nullable SuperiorPlayer asker, @Nullable Runnable callback) {
        Log.debug(Debug.CALCULATE_PLOT, owner.getName(), asker);

        beingRecalculated = true;

        BigDecimal oldWorth = getWorth();
        BigDecimal oldLevel = getPlotLevel();

        CompletableFuture<PlotCalculationAlgorithm.PlotCalculationResult> calculationResult;

        try {
            // Legacy support
            // noinspection deprecation
            calculationResult = calculationAlgorithm.calculatePlot();
        } catch (UnsupportedOperationException ex) {
            calculationResult = calculationAlgorithm.calculatePlot(this);
        }

        calculationResult.whenComplete((result, error) -> {
            if (error != null) {
                if (error instanceof TimeoutException) {
                    if (asker != null)
                        Message.PLOT_WORTH_TIME_OUT.send(asker);
                } else {
                    Log.entering(owner.getName(), asker);
                    Log.error(error, "An unexepcted error occurred while calculating plot worth:");

                    if (asker != null)
                        Message.PLOT_WORTH_ERROR.send(asker);
                }

                beingRecalculated = false;

                return;
            }

            clearBlockCounts();
            result.getBlockCounts().forEach((blockKey, amount) -> handleBlockPlaceInternal(blockKey, amount, 0));

            BigDecimal newPlotLevel = getPlotLevel();
            BigDecimal newPlotWorth = getWorth();

            finishCalcPlot(asker, callback, newPlotLevel, newPlotWorth);

            plugin.getMenus().refreshValues(this);
            plugin.getMenus().refreshCounts(this);

            saveBlockCounts(this.currentTotalBlockCounts.get(), oldWorth, oldLevel);
            updateLastTime();

            beingRecalculated = false;
        });
    }

    private boolean hasGiveInterestFailed() {
        return this.giveInterestFailed;
    }

    private void applyEffectsNoUpgradeCheck(SuperiorPlayer superiorPlayer) {
        Player player = superiorPlayer.asPlayer();
        if (player != null) {
            getPotionEffects().forEach((potionEffectType, level) -> player.addPotionEffect(
                    new PotionEffect(potionEffectType, Integer.MAX_VALUE, level - 1), true));
        }
    }

    private void removeEffectsNoUpgradeCheck(SuperiorPlayer superiorPlayer) {
        Player player = superiorPlayer.asPlayer();
        if (player != null)
            getPotionEffects().keySet().forEach(player::removePotionEffect);
    }

    private WarpCategory loadWarpCategory(String name, int slot, @Nullable ItemStack icon) {
        WarpCategory warpCategory = new SWarpCategory(getUniqueId(), name, slot, icon);
        warpCategories.put(name.toLowerCase(Locale.ENGLISH), warpCategory);
        return warpCategory;
    }

    public PlotWarp loadPlotWarp(String name, Location location, @Nullable WarpCategory warpCategory,
                                     boolean isPrivate, @Nullable ItemStack icon) {
        if (warpCategory == null)
            warpCategory = warpCategories.values().stream().findFirst().orElseGet(() -> createWarpCategory("Default Category"));

        PlotWarp plotWarp = new SPlotWarp(name, location.clone(), warpCategory, isPrivate, icon);

        plotWarp.getCategory().getWarps().add(plotWarp);

        String warpName = plotWarp.getName().toLowerCase(Locale.ENGLISH);

        if (warpsByName.containsKey(warpName))
            deleteWarp(warpName);

        warpsByName.put(warpName, plotWarp);

        warpsByLocation.put(new LocationKey(location), plotWarp);

        return plotWarp;
    }

    @Override
    public DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        if (persistentDataContainer == null)
            persistentDataContainer = plugin.getFactory().createPersistentDataContainer(this);
        return persistentDataContainer;
    }

    @Override
    public boolean isPersistentDataContainerEmpty() {
        return persistentDataContainer == null || persistentDataContainer.isEmpty();
    }

    @Override
    public void savePersistentDataContainer() {
        PlotsDatabaseBridge.executeFutureSaves(this, PlotsDatabaseBridge.FutureSave.PERSISTENT_DATA);
    }

    /*
     *  Schematic methods
     */

    private void replaceVisitor(SuperiorPlayer originalPlayer, @Nullable SuperiorPlayer newPlayer) {
        uniqueVisitors.write(uniqueVisitors -> {
            Iterator<UniqueVisitor> iterator = uniqueVisitors.iterator();
            while (iterator.hasNext()) {
                UniqueVisitor uniqueVisitor = iterator.next();
                if (uniqueVisitor.getSuperiorPlayer().equals(originalPlayer)) {
                    Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Replace Visitor");
                    if (newPlayer == null) {
                        iterator.remove();
                    } else {
                        uniqueVisitor.setSuperiorPlayer(newPlayer);
                    }
                }
            }
        });
    }

    private void replaceBannedPlayer(SuperiorPlayer originalPlayer, @Nullable SuperiorPlayer newPlayer) {
        if (bannedPlayers.remove(originalPlayer)) {
            Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Replace Banned Player");
            if (newPlayer != null)
                bannedPlayers.add(newPlayer);
        }
    }

    private void replacePermissions(SuperiorPlayer originalPlayer, @Nullable SuperiorPlayer newPlayer) {
        PlayerPrivilegeNode playerPermissionNode = playerPermissions.remove(originalPlayer);
        if (playerPermissionNode != null) {
            Log.debugResult(Debug.REPLACE_PLAYER, "Action", "Replace Permissions");
            if (newPlayer != null)
                playerPermissions.put(newPlayer, playerPermissionNode);
        }
    }

    private void saveBlockCounts(BigInteger currentTotalBlocksCount, BigDecimal oldWorth, BigDecimal oldLevel) {
        BigDecimal newWorth = getWorth();
        BigDecimal newLevel = getPlotLevel();

        if (oldLevel.compareTo(newLevel) != 0 || oldWorth.compareTo(newWorth) != 0) {
            BukkitExecutor.async(() -> plugin.getEventsBus().callPlotWorthUpdateEvent(this, oldWorth, oldLevel, newWorth, newLevel), 0L);
        }

        BigInteger deltaBlockCounts = this.lastSavedBlockCounts.subtract(currentTotalBlocksCount);
        if (deltaBlockCounts.compareTo(BigInteger.ZERO) < 0)
            deltaBlockCounts = deltaBlockCounts.negate();

        if (deltaBlockCounts.compareTo(plugin.getSettings().getBlockCountsSaveThreshold()) >= 0) {
            this.lastSavedBlockCounts = currentTotalBlocksCount;
            PlotsDatabaseBridge.saveBlockCounts(this);
            plugin.getGrid().sortPlots(SortingTypes.BY_WORTH);
            plugin.getGrid().sortPlots(SortingTypes.BY_LEVEL);
            plugin.getMenus().refreshValues(this);
            plugin.getMenus().refreshCounts(this);
        } else {
            PlotsDatabaseBridge.markBlockCountsToBeSaved(this);
        }
    }

    public void syncUpgrades(boolean overrideCustom) {
        clearUpgrades(overrideCustom);

        // We want to sync the default upgrade first, then the actual upgrades
        syncUpgrade(DefaultUpgradeLevel.getInstance(), overrideCustom);

        // Syncing all real upgrades
        plugin.getUpgrades().getUpgrades().forEach(upgrade -> syncUpgrade((SUpgradeLevel) getUpgradeLevel(upgrade), overrideCustom));
    }

    /*
     *  Plot top methods
     */

    private void warpPlayerWithoutWarmup(SuperiorPlayer superiorPlayer, PlotWarp plotWarp) {
        Location location = plotWarp.getLocation();
        superiorPlayer.setTeleportTask(null);

        // Warp doesn't exist anymore.
        if (getWarp(plotWarp.getName()) == null) {
            Message.INVALID_WARP.send(superiorPlayer, plotWarp.getName());
            deleteWarp(plotWarp.getName());
            return;
        }

        if (!isInsideRange(location)) {
            Message.UNSAFE_WARP.send(superiorPlayer);
            if (plugin.getSettings().getDeleteUnsafeWarps())
                deleteWarp(plotWarp.getName());
            return;
        }

        if (!WorldBlocks.isSafeBlock(location.getBlock())) {
            Message.UNSAFE_WARP.send(superiorPlayer);
            return;
        }

        superiorPlayer.teleport(location, success -> {
            if (success) {
                Message.TELEPORTED_TO_WARP.send(superiorPlayer);
                if (superiorPlayer.isShownAsOnline()) {
                    PlotUtils.sendMessage(this, Message.TELEPORTED_TO_WARP_ANNOUNCEMENT,
                            Collections.singletonList(superiorPlayer.getUniqueId()), superiorPlayer.getName(), plotWarp.getName());
                }
            }
        });
    }

    /*
     *  Vault related methods
     */

    @Override
    public void completeMission(Mission<?> mission) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        setAmountMissionCompleted(mission, completedMissions.getOrDefault(mission, 0) + 1);
    }

    @Override
    public void resetMission(Mission<?> mission) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        setAmountMissionCompleted(mission, completedMissions.getOrDefault(mission, 0) - 1);
    }

    @Override
    public boolean hasCompletedMission(Mission<?> mission) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        return completedMissions.getOrDefault(mission, 0) > 0;
    }

    @Override
    public boolean canCompleteMissionAgain(Mission<?> mission) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        Optional<MissionData> missionDataOptional = plugin.getMissions().getMissionData(mission);
        return missionDataOptional.isPresent() && getAmountMissionCompleted(mission) <
                missionDataOptional.get().getResetAmount();
    }

    /*
     *  Data related methods
     */

    @Override
    public int getAmountMissionCompleted(Mission<?> mission) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        return completedMissions.getOrDefault(mission, 0);
    }

    @Override
    public void setAmountMissionCompleted(Mission<?> mission, int finishCount) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");

        Log.debug(Debug.SET_PLOT_MISSION_COMPLETED, mission.getName(), finishCount);

        // We always want to reset data
        mission.clearData(getOwner());

        if (finishCount > 0) {
            Integer oldFinishCount = completedMissions.put(mission, finishCount);

            if (Objects.equals(oldFinishCount, finishCount))
                return;

            PlotsDatabaseBridge.saveMission(this, mission, finishCount);
        } else {
            Integer oldFinishCount = completedMissions.remove(mission);

            if (oldFinishCount == null)
                return;

            PlotsDatabaseBridge.removeMission(this, mission);
        }

        plugin.getMenus().refreshMissionsCategory(mission.getMissionCategory());
    }

    /*
     *  Object related methods
     */

    @Override
    public List<Mission<?>> getCompletedMissions() {
        return new SequentialListBuilder<Mission<?>>().build(completedMissions.keySet());
    }

    @Override
    public Map<Mission<?>, Integer> getCompletedMissionsWithAmounts() {
        return Collections.unmodifiableMap(completedMissions);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    /*
     *  Private methods
     */

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Plot && (this == obj || this.uuid.equals(((Plot) obj).getUniqueId()));
    }

    @Override
    @SuppressWarnings("all")
    public int compareTo(Plot other) {
        if (other == null)
            return -1;

        if (plugin.getSettings().getPlotTopOrder().equals("WORTH")) {
            int compare = getWorth().compareTo(other.getWorth());
            if (compare != 0) return compare;
        } else {
            int compare = getPlotLevel().compareTo(other.getPlotLevel());
            if (compare != 0) return compare;
        }

        return getOwner().getName().compareTo(other.getOwner().getName());
    }

    private PlotChest[] createDefaultPlotChests() {
        PlotChest[] plotChests = new PlotChest[plugin.getSettings().getPlotChests().getDefaultPages()];
        for (int i = 0; i < plotChests.length; i++) {
            if (plotChests[i] == null) {
                plotChests[i] = new SPlotChest(this, i);
                plotChests[i].setRows(plugin.getSettings().getPlotChests().getDefaultSize());
            }
        }
        return plotChests;
    }

    private void startBankInterest() {
        if (BuiltinModules.BANK.bankInterestEnabled) {
            long currentTime = System.currentTimeMillis() / 1000;
            long ticksToNextInterest = (BuiltinModules.BANK.bankInterestInterval - (currentTime - this.lastInterest)) * 20;
            if (ticksToNextInterest <= 0) {
                giveInterest(true);
            } else {
                this.bankInterestTask.set(bankInterestTask -> {
                    if (bankInterestTask != null)
                        bankInterestTask.cancel();
                    return BukkitExecutor.sync(() -> giveInterest(true), ticksToNextInterest);
                });
            }
        }
    }

    private void checkMembersDuplication() {
        members.write(members -> {
            Iterator<SuperiorPlayer> iterator = members.iterator();
            while (iterator.hasNext()) {
                SuperiorPlayer superiorPlayer = iterator.next();
                if (superiorPlayer.equals(owner) || !this.equals(superiorPlayer.getPlot())) {
                    iterator.remove();
                    PlotsDatabaseBridge.removeMember(this, superiorPlayer);
                }
            }
        });
    }

    private void updateOldUpgradeValues() {
        this.blockLimits.forEach((block, limit) -> {
            Integer defaultValue = plugin.getSettings().getDefaultValues().getBlockLimits().get(block);
            if (defaultValue != null && (int) limit.get() == defaultValue)
                this.blockLimits.put(block, Value.syncedFixed(defaultValue));
        });

        this.entityLimits.forEach((entity, limit) -> {
            Integer defaultValue = plugin.getSettings().getDefaultValues().getEntityLimits().get(entity);
            if (defaultValue != null && (int) limit.get() == defaultValue)
                this.entityLimits.put(entity, Value.syncedFixed(defaultValue));
        });

        this.cobbleGeneratorValues.write(cobbleGeneratorValues -> {
            for (World.Environment environment : World.Environment.values()) {
                Map<Key, Integer> defaultGenerator = plugin.getSettings().getDefaultValues().getGenerators()[environment.ordinal()];
                if (defaultGenerator != null) {
                    KeyMap<Value<Integer>> worldGeneratorRates = cobbleGeneratorValues.get(environment);

                    if (worldGeneratorRates == null)
                        continue;

                    worldGeneratorRates.forEach((key, rate) -> {
                        Integer defaultValue = defaultGenerator.get(key);
                        if (defaultValue != null && (int) rate.get() == defaultValue)
                            worldGeneratorRates.put(key, Value.syncedFixed(defaultValue));
                    });
                }
            }
        });

        if (getPlotSize() == plugin.getSettings().getDefaultValues().getPlotSize())
            this.plotSize.set(DefaultUpgradeLevel.getInstance().getBorderSizeUpgradeValue());

        if (getWarpsLimit() == plugin.getSettings().getDefaultValues().getWarpsLimit())
            this.warpsLimit.set(DefaultUpgradeLevel.getInstance().getWarpsLimitUpgradeValue());

        if (getTeamLimit() == plugin.getSettings().getDefaultValues().getTeamLimit())
            this.teamLimit.set(DefaultUpgradeLevel.getInstance().getTeamLimitUpgradeValue());

        if (getCoopLimit() == plugin.getSettings().getDefaultValues().getCoopLimit())
            this.coopLimit.set(DefaultUpgradeLevel.getInstance().getCoopLimitUpgradeValue());

        if (getCropGrowthMultiplier() == plugin.getSettings().getDefaultValues().getCropGrowth())
            this.cropGrowth.set(DefaultUpgradeLevel.getInstance().getCropGrowthUpgradeValue());

        if (getSpawnerRatesMultiplier() == plugin.getSettings().getDefaultValues().getSpawnerRates())
            this.spawnerRates.set(DefaultUpgradeLevel.getInstance().getSpawnerRatesUpgradeValue());

        if (getMobDropsMultiplier() == plugin.getSettings().getDefaultValues().getMobDrops())
            this.mobDrops.set(DefaultUpgradeLevel.getInstance().getMobDropsUpgradeValue());
    }

    private void clearUpgrades(boolean overrideCustom) {
        if (overrideCustom || this.plotSize.get() instanceof SyncedValue) {
            if (overrideCustom)
                PlotsDatabaseBridge.saveSize(this);

            setPlotSizeInternal(Value.syncedFixed(-1));
        }

        warpsLimit.set(warpsLimit -> {
            if (overrideCustom || warpsLimit instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveWarpsLimit(this);
                return Value.syncedFixed(-1);
            }
            return warpsLimit;
        });

        teamLimit.set(teamLimit -> {
            if (overrideCustom || teamLimit instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveTeamLimit(this);
                return Value.syncedFixed(-1);
            }
            return teamLimit;
        });

        coopLimit.set(coopLimit -> {
            if (overrideCustom || coopLimit instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveCoopLimit(this);
                return Value.syncedFixed(-1);
            }
            return coopLimit;
        });

        cropGrowth.set(cropGrowth -> {
            if (overrideCustom || cropGrowth instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveCropGrowth(this);

                notifyCropGrowthChange(-1D);

                return Value.syncedFixed(-1D);
            }

            return cropGrowth;
        });

        spawnerRates.set(spawnerRates -> {
            if (overrideCustom || spawnerRates instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveSpawnerRates(this);
                return Value.syncedFixed(-1D);
            }
            return spawnerRates;
        });

        mobDrops.set(mobDrops -> {
            if (overrideCustom || mobDrops instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveMobDrops(this);
                return Value.syncedFixed(-1D);
            }
            return mobDrops;
        });

        bankLimit.set(bankLimit -> {
            if (overrideCustom || bankLimit instanceof SyncedValue) {
                if (overrideCustom)
                    PlotsDatabaseBridge.saveBankLimit(this);
                return Value.syncedFixed(SYNCED_BANK_LIMIT_VALUE);
            }
            return bankLimit;
        });

        blockLimits.entrySet().stream()
                .filter(entry -> overrideCustom || entry.getValue() instanceof SyncedValue)
                .forEach(entry -> entry.setValue(Value.syncedFixed(-1)));

        entityLimits.entrySet().stream()
                .filter(entry -> overrideCustom || entry.getValue() instanceof SyncedValue)
                .forEach(entry -> entry.setValue(Value.syncedFixed(-1)));

        cobbleGeneratorValues.write(cobbleGeneratorValues -> {
            cobbleGeneratorValues.values().forEach(cobbleGeneratorValue -> {
                cobbleGeneratorValue.entrySet().stream()
                        .filter(entry -> overrideCustom || entry.getValue() instanceof SyncedValue)
                        .forEach(entry -> entry.setValue(Value.syncedFixed(-1)));
            });
        });

        plotEffects.entrySet().stream()
                .filter(entry -> overrideCustom || entry.getValue() instanceof SyncedValue)
                .forEach(entry -> entry.setValue(Value.syncedFixed(-1)));

        roleLimits.entrySet().stream()
                .filter(entry -> overrideCustom || entry.getValue() instanceof SyncedValue)
                .forEach(entry -> entry.setValue(Value.syncedFixed(-1)));
    }

    private void syncUpgrade(SUpgradeLevel upgradeLevel, boolean overrideCustom) {
        cropGrowth.set(cropGrowth -> {
            if ((overrideCustom || cropGrowth instanceof SyncedValue) && cropGrowth.get() < upgradeLevel.getCropGrowth()) {
                notifyCropGrowthChange(upgradeLevel.getCropGrowth());
                return upgradeLevel.getCropGrowthUpgradeValue();
            }

            return cropGrowth;
        });

        spawnerRates.set(spawnerRates -> {
            if ((overrideCustom || spawnerRates instanceof SyncedValue) && spawnerRates.get() < upgradeLevel.getSpawnerRates())
                return upgradeLevel.getSpawnerRatesUpgradeValue();
            return spawnerRates;
        });

        mobDrops.set(mobDrops -> {
            if ((overrideCustom || mobDrops instanceof SyncedValue) && mobDrops.get() < upgradeLevel.getMobDrops())
                return upgradeLevel.getMobDropsUpgradeValue();
            return mobDrops;
        });

        teamLimit.set(teamLimit -> {
            if ((overrideCustom || teamLimit instanceof SyncedValue) && teamLimit.get() < upgradeLevel.getTeamLimit())
                return upgradeLevel.getTeamLimitUpgradeValue();
            return teamLimit;
        });

        warpsLimit.set(warpsLimit -> {
            if ((overrideCustom || warpsLimit instanceof SyncedValue) && warpsLimit.get() < upgradeLevel.getWarpsLimit())
                return upgradeLevel.getWarpsLimitUpgradeValue();
            return warpsLimit;
        });

        coopLimit.set(coopLimit -> {
            if ((overrideCustom || coopLimit instanceof SyncedValue) && coopLimit.get() < upgradeLevel.getCoopLimit())
                return upgradeLevel.getCoopLimitUpgradeValue();
            return coopLimit;
        });

        Value<Integer> plotSize = this.plotSize.get();
        if ((overrideCustom || plotSize instanceof SyncedValue) && plotSize.get() < upgradeLevel.getBorderSize())
            setPlotSizeInternal(upgradeLevel.getBorderSizeUpgradeValue());

        bankLimit.set(bankLimit -> {
            if ((overrideCustom || bankLimit instanceof SyncedValue) && bankLimit.get().compareTo(upgradeLevel.getBankLimit()) < 0)
                return upgradeLevel.getBankLimitUpgradeValue();
            return bankLimit;
        });

        for (Map.Entry<Key, Value<Integer>> entry : upgradeLevel.getBlockLimitsUpgradeValue().entrySet()) {
            Value<Integer> currentValue = blockLimits.getRaw(entry.getKey(), null);
            if (currentValue == null || ((overrideCustom || currentValue instanceof SyncedValue) && currentValue.get() < entry.getValue().get()))
                blockLimits.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Key, Value<Integer>> entry : upgradeLevel.getEntityLimitsUpgradeValue().entrySet()) {
            Value<Integer> currentValue = entityLimits.getRaw(entry.getKey(), null);
            if (currentValue == null || ((overrideCustom || currentValue instanceof SyncedValue) && currentValue.get() < entry.getValue().get()))
                entityLimits.put(entry.getKey(), entry.getValue());
        }

        Map<World.Environment, Map<Key, Value<Integer>>> upgradeGeneratorRates = upgradeLevel.getGeneratorUpgradeValue();
        if (!upgradeGeneratorRates.isEmpty()) {
            this.cobbleGeneratorValues.write(cobbleGeneratorValues -> {
                for (World.Environment environment : World.Environment.values()) {
                    Map<Key, Value<Integer>> upgradeLevelGeneratorRates = upgradeGeneratorRates.get(environment);

                    if (upgradeLevelGeneratorRates == null)
                        continue;

                    KeyMap<Value<Integer>> worldGeneratorRates = cobbleGeneratorValues.get(environment);

                    if (worldGeneratorRates != null && !upgradeLevelGeneratorRates.isEmpty()) {
                        KeyMap<Value<Integer>> worldGeneratorRatesCopy = worldGeneratorRates;
                        worldGeneratorRatesCopy.removeIf(key -> worldGeneratorRatesCopy.get(key) instanceof SyncedValue);
                    }

                    for (Map.Entry<Key, Value<Integer>> entry : upgradeLevelGeneratorRates.entrySet()) {
                        Key block = entry.getKey();
                        Value<Integer> rate = entry.getValue();

                        Value<Integer> currentValue = worldGeneratorRates == null ? null : worldGeneratorRates.get(block);
                        if (currentValue == null || ((overrideCustom || currentValue instanceof SyncedValue) &&
                                currentValue.get() < rate.get())) {
                            if (worldGeneratorRates == null) {
                                worldGeneratorRates = KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL);
                                cobbleGeneratorValues.put(environment, worldGeneratorRates);
                            }

                            worldGeneratorRates.put(block, rate);
                        }
                    }
                }
            });
        }

        boolean editedPlotEffects = false;

        for (Map.Entry<PotionEffectType, Value<Integer>> entry : upgradeLevel.getPotionEffectsUpgradeValue().entrySet()) {
            Value<Integer> currentValue = plotEffects.get(entry.getKey());
            if (currentValue == null || ((overrideCustom || currentValue instanceof SyncedValue) && currentValue.get() < entry.getValue().get())) {
                plotEffects.put(entry.getKey(), entry.getValue());
                editedPlotEffects = true;
            }
        }

        if (editedPlotEffects) {
            applyEffects();
        }

        for (Map.Entry<PlayerRole, Value<Integer>> entry : upgradeLevel.getRoleLimitsUpgradeValue().entrySet()) {
            Value<Integer> currentValue = roleLimits.get(entry.getKey());
            if (currentValue == null || ((overrideCustom || currentValue instanceof SyncedValue) && currentValue.get() < entry.getValue().get()))
                roleLimits.put(entry.getKey(), entry.getValue());
        }
    }

    private void updatePlotChests() {
        List<PlotChest> plotChestList = new ArrayList<>(Arrays.asList(this.plotChests.get()));
        boolean updatedChests = false;

        while (plotChestList.size() < plugin.getSettings().getPlotChests().getDefaultPages()) {
            PlotChest newPlotChest = new SPlotChest(this, plotChestList.size());
            newPlotChest.setRows(plugin.getSettings().getPlotChests().getDefaultSize());
            plotChestList.add(newPlotChest);
            updatedChests = true;
        }

        if (updatedChests) {
            this.plotChests.set(plotChestList.toArray(new PlotChest[0]));
        }
    }

    private void finishCalcPlot(SuperiorPlayer asker, Runnable callback, BigDecimal plotLevel, BigDecimal plotWorth) {
        plugin.getEventsBus().callPlotWorthCalculatedEvent(this, asker, plotLevel, plotWorth);

        if (asker != null)
            Message.PLOT_WORTH_RESULT.send(asker, plotWorth, plotLevel);

        if (callback != null)
            callback.run();
    }

    private void forEachPlotMember(List<UUID> ignoredMembers, boolean onlyOnline, Consumer<SuperiorPlayer> plotMemberConsumer) {
        for (SuperiorPlayer plotMember : getPlotMembers(true)) {
            if (!ignoredMembers.contains(plotMember.getUniqueId()) && (!onlyOnline || plotMember.isOnline())) {
                plotMemberConsumer.accept(plotMember);
            }
        }
    }

    private void notifyCropGrowthChange(double newCropGrowth) {
        if (!BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeCropGrowth.class))
            return;

        double newCropGrowthMultiplier = newCropGrowth - 1;
        PlotUtils.getChunkCoords(this, PlotChunkFlags.ONLY_PROTECTED | PlotChunkFlags.NO_EMPTY_CHUNKS)
                .values().forEach(chunkPositions -> plugin.getNMSChunks().updateCropsTicker(chunkPositions, newCropGrowthMultiplier));
    }

    public static class UniqueVisitor {

        private final Pair<SuperiorPlayer, Long> pair;

        private SuperiorPlayer superiorPlayer;
        private long lastVisitTime;

        public UniqueVisitor(SuperiorPlayer superiorPlayer, long lastVisitTime) {
            this.superiorPlayer = superiorPlayer;
            this.lastVisitTime = lastVisitTime;
            this.pair = new Pair<>(superiorPlayer, lastVisitTime);
        }

        public SuperiorPlayer getSuperiorPlayer() {
            return superiorPlayer;
        }

        public void setSuperiorPlayer(SuperiorPlayer superiorPlayer) {
            this.superiorPlayer = superiorPlayer;
            this.pair.setKey(superiorPlayer);
        }

        public long getLastVisitTime() {
            return lastVisitTime;
        }

        public void setLastVisitTime(long lastVisitTime) {
            this.lastVisitTime = lastVisitTime;
            this.pair.setValue(lastVisitTime);
        }

        public Pair<SuperiorPlayer, Long> toPair() {
            return this.pair;
        }

        @Override
        public int hashCode() {
            return Objects.hash(superiorPlayer, lastVisitTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UniqueVisitor that = (UniqueVisitor) o;
            return lastVisitTime == that.lastVisitTime && superiorPlayer.equals(that.superiorPlayer);
        }

    }

}

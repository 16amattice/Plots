package com.bgsoftware.superiorskyblock.plot.builder;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.enums.SyncStatus;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PermissionNode;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.DirtyChunk;
import com.bgsoftware.superiorskyblock.core.key.KeyIndicator;
import com.bgsoftware.superiorskyblock.core.key.KeyMaps;
import com.bgsoftware.superiorskyblock.plot.SPlot;
import com.bgsoftware.superiorskyblock.plot.container.value.Value;
import com.bgsoftware.superiorskyblock.plot.privilege.PlayerPrivilegeNode;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlotBuilderImpl implements Plot.Builder {

    private static final BigDecimal SYNCED_BANK_LIMIT_VALUE = BigDecimal.valueOf(-2);
    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    @Nullable
    public SuperiorPlayer owner;
    public UUID uuid = null;
    public Location center = null;
    public String plotName = "";
    @Nullable
    public String plotType;
    public long creationTime = System.currentTimeMillis() / 1000;
    public String discord = "None";
    public String paypal = "None";
    public BigDecimal bonusWorth = BigDecimal.ZERO;
    public BigDecimal bonusLevel = BigDecimal.ZERO;
    public boolean isLocked = false;
    public boolean isIgnored = false;
    public String description = "";
    public int generatedSchematicsMask = 0;
    public int unlockedWorldsMask = 0;
    public long lastTimeUpdated = System.currentTimeMillis() / 1000;
    public final Set<DirtyChunk> dirtyChunks = new LinkedHashSet<>();
    public final KeyMap<BigInteger> blockCounts = KeyMaps.createHashMap(KeyIndicator.MATERIAL);
    public final EnumMap<World.Environment, Location> plotHomes = new EnumMap<>(World.Environment.class);
    public final List<SuperiorPlayer> members = new LinkedList<>();
    public final List<SuperiorPlayer> bannedPlayers = new LinkedList<>();
    public final Map<SuperiorPlayer, PlayerPrivilegeNode> playerPermissions = new LinkedHashMap<>();
    public final Map<PlotPrivilege, PlayerRole> rolePermissions = new LinkedHashMap<>();
    public final Map<String, Integer> upgrades = new LinkedHashMap<>();
    public final KeyMap<Value<Integer>> blockLimits = KeyMaps.createHashMap(KeyIndicator.MATERIAL);
    public final Map<UUID, Rating> ratings = new LinkedHashMap<>();
    public final Map<Mission<?>, Integer> completedMissions = new LinkedHashMap<>();
    public final Map<PlotFlag, Byte> plotFlags = new LinkedHashMap<>();
    public final EnumMap<World.Environment, KeyMap<Value<Integer>>> cobbleGeneratorValues = new EnumMap<>(World.Environment.class);
    public final List<SPlot.UniqueVisitor> uniqueVisitors = new LinkedList<>();
    public final KeyMap<Value<Integer>> entityLimits = KeyMaps.createIdentityHashMap(KeyIndicator.ENTITY_TYPE);
    public final Map<PotionEffectType, Value<Integer>> plotEffects = new LinkedHashMap<>();
    public final List<ItemStack[]> plotChests = new ArrayList<>(plugin.getSettings().getPlotChests().getDefaultPages());
    public final Map<PlayerRole, Value<Integer>> roleLimits = new LinkedHashMap<>();
    public final EnumMap<World.Environment, Location> visitorHomes = new EnumMap<>(World.Environment.class);
    public Value<Integer> plotSize = Value.syncedFixed(-1);
    public Value<Integer> warpsLimit = Value.syncedFixed(-1);
    public Value<Integer> teamLimit = Value.syncedFixed(-1);
    public Value<Integer> coopLimit = Value.syncedFixed(-1);
    public Value<Double> cropGrowth = Value.syncedFixed(-1D);
    public Value<Double> spawnerRates = Value.syncedFixed(-1D);
    public Value<Double> mobDrops = Value.syncedFixed(-1D);
    public Value<BigDecimal> bankLimit = Value.syncedFixed(SYNCED_BANK_LIMIT_VALUE);
    public BigDecimal balance = BigDecimal.ZERO;
    public long lastInterestTime = System.currentTimeMillis() / 1000;
    public List<WarpRecord> warps = new LinkedList<>();
    public List<WarpCategoryRecord> warpCategories = new LinkedList<>();
    public List<BankTransaction> bankTransactions = new LinkedList<>();
    public byte[] persistentData = new byte[0];

    public PlotBuilderImpl() {

    }

    @Override
    public Plot.Builder setOwner(@Nullable SuperiorPlayer owner) {
        this.owner = owner;
        return this;
    }

    @Override
    @Nullable
    public SuperiorPlayer getOwner() {
        return this.owner;
    }

    @Override
    public Plot.Builder setUniqueId(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid parameter cannot be null.");
        Preconditions.checkState(plugin.getGrid().getPlotByUUID(uuid) == null, "The provided uuid is not unique.");
        this.uuid = uuid;
        return this;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Plot.Builder setCenter(Location center) {
        Preconditions.checkNotNull(center, "center parameter cannot be null.");
        Preconditions.checkState(isValidCenter(center), "The provided center is not centered. center: " + center +
                ", maxPlotSize: " + plugin.getSettings().getMaxPlotSize());
        this.center = center;
        return this;
    }

    @Override
    public Location getCenter() {
        return this.center;
    }

    @Override
    public Plot.Builder setName(String plotName) {
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        this.plotName = plotName;
        return this;
    }

    @Override
    public String getName() {
        return this.plotName;
    }

    @Override
    public Plot.Builder setSchematicName(String plotType) {
        Preconditions.checkNotNull(plotType, "plotType parameter cannot be null.");
        this.plotType = plotType;
        return this;
    }

    @Override
    public String getScehmaticName() {
        return this.plotType;
    }

    @Override
    public Plot.Builder setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Plot.Builder setDiscord(String discord) {
        Preconditions.checkNotNull(discord, "discord parameter cannot be null.");
        this.discord = discord;
        return this;
    }

    @Override
    public String getDiscord() {
        return this.discord;
    }

    @Override
    public Plot.Builder setPaypal(String paypal) {
        Preconditions.checkNotNull(paypal, "paypal parameter cannot be null.");
        this.paypal = paypal;
        return this;
    }

    @Override
    public String getPaypal() {
        return this.paypal;
    }

    @Override
    public Plot.Builder setBonusWorth(BigDecimal bonusWorth) {
        Preconditions.checkNotNull(bonusWorth, "bonusWorth parameter cannot be null.");
        this.bonusWorth = bonusWorth;
        return this;
    }

    @Override
    public BigDecimal getBonusWorth() {
        return this.bonusWorth;
    }

    @Override
    public Plot.Builder setBonusLevel(BigDecimal bonusLevel) {
        Preconditions.checkNotNull(bonusLevel, "bonusLevel parameter cannot be null.");
        this.bonusLevel = bonusLevel;
        return this;
    }

    @Override
    public BigDecimal getBonusLevel() {
        return this.bonusLevel;
    }

    @Override
    public Plot.Builder setLocked(boolean isLocked) {
        this.isLocked = isLocked;
        return this;
    }

    @Override
    public boolean isLocked() {
        return this.isLocked;
    }

    @Override
    public Plot.Builder setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
        return this;
    }

    @Override
    public boolean isIgnored() {
        return this.isIgnored;
    }

    @Override
    public Plot.Builder setDescription(String description) {
        Preconditions.checkNotNull(description, "description parameter cannot be null.");
        this.description = description;
        return this;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Plot.Builder setGeneratedSchematics(int generatedSchematicsMask) {
        this.generatedSchematicsMask = generatedSchematicsMask;
        return this;
    }

    @Override
    public int getGeneratedSchematicsMask() {
        return this.generatedSchematicsMask;
    }

    @Override
    public Plot.Builder setUnlockedWorlds(int unlockedWorldsMask) {
        this.unlockedWorldsMask = unlockedWorldsMask;
        return this;
    }

    @Override
    public int getUnlockedWorldsMask() {
        return this.unlockedWorldsMask;
    }

    @Override
    public Plot.Builder setLastTimeUpdated(long lastTimeUpdated) {
        this.lastTimeUpdated = lastTimeUpdated;
        return this;
    }

    @Override
    public long getLastTimeUpdated() {
        return this.lastTimeUpdated;
    }

    @Override
    public Plot.Builder setDirtyChunk(String worldName, int chunkX, int chunkZ) {
        Preconditions.checkNotNull(worldName, "worldName parameter cannot be null.");
        this.dirtyChunks.add(new DirtyChunk(worldName, chunkX, chunkZ));
        return this;
    }

    @Override
    public boolean isDirtyChunk(String worldName, int chunkX, int chunkZ) {
        return this.dirtyChunks.contains(new DirtyChunk(worldName, chunkX, chunkZ));
    }

    @Override
    public Plot.Builder setBlockCount(Key block, BigInteger count) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        Preconditions.checkNotNull(count, "count parameter cannot be null.");
        this.blockCounts.put(block, count);
        return this;
    }

    @Override
    public KeyMap<BigInteger> getBlockCounts() {
        return KeyMaps.unmodifiableKeyMap(this.blockCounts);
    }

    @Override
    public Plot.Builder setPlotHome(Location location, World.Environment environment) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        this.plotHomes.put(environment, location);
        return this;
    }

    @Override
    public Map<World.Environment, Location> getPlotHomes() {
        return Collections.unmodifiableMap(this.plotHomes);
    }

    @Override
    public Plot.Builder addPlotMember(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        this.members.add(superiorPlayer);
        return this;
    }

    @Override
    public List<SuperiorPlayer> getPlotMembers() {
        return Collections.unmodifiableList(this.members);
    }

    @Override
    public Plot.Builder addBannedPlayer(SuperiorPlayer superiorPlayer) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        this.bannedPlayers.add(superiorPlayer);
        return this;
    }

    @Override
    public List<SuperiorPlayer> getBannedPlayers() {
        return Collections.unmodifiableList(this.bannedPlayers);
    }

    @Override
    public Plot.Builder setPlayerPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege, boolean value) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
        this.playerPermissions.computeIfAbsent(superiorPlayer, p -> new PlayerPrivilegeNode(superiorPlayer, null))
                .loadPrivilege(plotPrivilege, (byte) (value ? 1 : 0));
        return this;
    }

    @Override
    public Map<SuperiorPlayer, PermissionNode> getPlayerPermissions() {
        return Collections.unmodifiableMap(this.playerPermissions);
    }

    @Override
    public Plot.Builder setRolePermission(PlotPrivilege plotPrivilege, PlayerRole requiredRole) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
        Preconditions.checkNotNull(requiredRole, "requiredRole parameter cannot be null.");
        this.rolePermissions.put(plotPrivilege, requiredRole);
        return this;
    }

    @Override
    public Map<PlotPrivilege, PlayerRole> getRolePermissions() {
        return Collections.unmodifiableMap(this.rolePermissions);
    }

    @Override
    public Plot.Builder setUpgrade(Upgrade upgrade, int level) {
        Preconditions.checkNotNull(upgrade, "upgrade parameter cannot be null.");
        this.upgrades.put(upgrade.getName(), level);
        return this;
    }

    @Override
    public Map<Upgrade, Integer> getUpgrades() {
        return Collections.unmodifiableMap(this.upgrades.entrySet().stream().collect(Collectors.toMap(
                entry -> plugin.getUpgrades().getUpgrade(entry.getKey()),
                Map.Entry::getValue
        )));
    }

    @Override
    public Plot.Builder setBlockLimit(Key block, int limit) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        this.blockLimits.put(block, limit < 0 ? Value.syncedFixed(limit) : Value.fixed(limit));
        return this;
    }

    @Override
    public KeyMap<Integer> getBlockLimits() {
        return KeyMap.createKeyMap(convertFromValuesToRaw(this.blockLimits));
    }

    @Override
    public Plot.Builder setRating(SuperiorPlayer superiorPlayer, Rating rating) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        Preconditions.checkNotNull(rating, "rating parameter cannot be null.");
        this.ratings.put(superiorPlayer.getUniqueId(), rating);
        return this;
    }

    @Override
    public Map<SuperiorPlayer, Rating> getRatings() {
        return Collections.unmodifiableMap(this.ratings.entrySet().stream().collect(Collectors.toMap(
                entry -> plugin.getPlayers().getSuperiorPlayer(entry.getKey()),
                Map.Entry::getValue
        )));
    }

    @Override
    public Plot.Builder setCompletedMission(Mission<?> mission, int finishCount) {
        Preconditions.checkNotNull(mission, "mission parameter cannot be null.");
        this.completedMissions.put(mission, finishCount);
        return this;
    }

    @Override
    public Map<Mission<?>, Integer> getCompletedMissions() {
        return Collections.unmodifiableMap(this.completedMissions);
    }

    @Override
    public Plot.Builder setPlotFlag(PlotFlag plotFlag, boolean value) {
        Preconditions.checkNotNull(plotFlag, "plotFlag parameter cannot be null.");
        this.plotFlags.put(plotFlag, (byte) (value ? 1 : 0));
        return this;
    }

    @Override
    public Map<PlotFlag, SyncStatus> getPlotFlags() {
        return Collections.unmodifiableMap(this.plotFlags.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue() == 1 ? SyncStatus.ENABLED : SyncStatus.DISABLED
        )));
    }

    @Override
    public Plot.Builder setGeneratorRate(Key block, int rate, World.Environment environment) {
        Preconditions.checkNotNull(block, "block parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        this.cobbleGeneratorValues.computeIfAbsent(environment, e -> KeyMaps.createHashMap(KeyIndicator.MATERIAL))
                .put(block, rate < 0 ? Value.syncedFixed(rate) : Value.fixed(rate));
        return this;
    }

    @Override
    public Map<World.Environment, KeyMap<Integer>> getGeneratorRates() {
        Map<World.Environment, KeyMap<Integer>> result = new EnumMap<>(World.Environment.class);

        this.cobbleGeneratorValues.forEach(((environment, generatorRates) ->
                result.put(environment, KeyMap.createKeyMap(convertFromValuesToRaw(generatorRates)))));

        return Collections.unmodifiableMap(result);
    }

    @Override
    public Plot.Builder addUniqueVisitor(SuperiorPlayer superiorPlayer, long visitTime) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer parameter cannot be null.");
        this.uniqueVisitors.add(new SPlot.UniqueVisitor(superiorPlayer, visitTime));
        return this;
    }

    @Override
    public Map<SuperiorPlayer, Long> getUniqueVisitors() {
        LinkedHashMap<SuperiorPlayer, Long> result = new LinkedHashMap<>();
        this.uniqueVisitors.forEach(uniqueVisitor ->
                result.put(uniqueVisitor.getSuperiorPlayer(), uniqueVisitor.getLastVisitTime()));
        return Collections.unmodifiableMap(result);
    }

    @Override
    public Plot.Builder setEntityLimit(Key entity, int limit) {
        Preconditions.checkNotNull(entity, "entity parameter cannot be null.");
        this.entityLimits.put(entity, limit < 0 ? Value.syncedFixed(limit) : Value.fixed(limit));
        return this;
    }

    @Override
    public KeyMap<Integer> getEntityLimits() {
        return KeyMap.createKeyMap(convertFromValuesToRaw(this.entityLimits));
    }

    @Override
    public Plot.Builder setPlotEffect(PotionEffectType potionEffectType, int level) {
        Preconditions.checkNotNull(potionEffectType, "potionEffectType parameter cannot be null.");
        this.plotEffects.put(potionEffectType, level < 0 ? Value.syncedFixed(level) : Value.fixed(level));
        return this;
    }

    @Override
    public Map<PotionEffectType, Integer> getPlotEffects() {
        return Collections.unmodifiableMap(convertFromValuesToRaw(this.plotEffects));
    }

    @Override
    public Plot.Builder setPlotChest(int index, ItemStack[] contents) {
        Preconditions.checkNotNull(contents, "contents parameter cannot be null.");

        if (index >= this.plotChests.size()) {
            while (index > this.plotChests.size()) {
                this.plotChests.add(new ItemStack[plugin.getSettings().getPlotChests().getDefaultSize() * 9]);
            }

            this.plotChests.add(contents);
        } else {
            this.plotChests.set(index, contents);
        }

        return this;
    }

    @Override
    public List<ItemStack[]> getPlotChests() {
        return Collections.unmodifiableList(this.plotChests);
    }

    @Override
    public Plot.Builder setRoleLimit(PlayerRole playerRole, int limit) {
        Preconditions.checkNotNull(playerRole, "playerRole parameter cannot be null.");
        this.roleLimits.put(playerRole, limit < 0 ? Value.syncedFixed(limit) : Value.fixed(limit));
        return this;
    }

    @Override
    public Map<PlayerRole, Integer> getRoleLimits() {
        return Collections.unmodifiableMap(convertFromValuesToRaw(this.roleLimits));
    }

    @Override
    public Plot.Builder setVisitorHome(Location location, World.Environment environment) {
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        this.visitorHomes.put(environment, location);
        return this;
    }

    @Override
    public Map<World.Environment, Location> getVisitorHomes() {
        return Collections.unmodifiableMap(visitorHomes);
    }

    @Override
    public Plot.Builder setPlotSize(int plotSize) {
        this.plotSize = plotSize < 0 ? Value.syncedFixed(plotSize) : Value.fixed(plotSize);
        return this;
    }

    @Override
    public int getPlotSize() {
        return this.plotSize.get();
    }

    @Override
    public Plot.Builder setTeamLimit(int teamLimit) {
        this.teamLimit = teamLimit < 0 ? Value.syncedFixed(teamLimit) : Value.fixed(teamLimit);
        return this;
    }

    @Override
    public int getTeamLimit() {
        return this.teamLimit.get();
    }

    @Override
    public Plot.Builder setWarpsLimit(int warpsLimit) {
        this.warpsLimit = warpsLimit < 0 ? Value.syncedFixed(warpsLimit) : Value.fixed(warpsLimit);
        return this;
    }

    @Override
    public int getWarpsLimit() {
        return this.warpsLimit.get();
    }

    @Override
    public Plot.Builder setCropGrowth(double cropGrowth) {
        this.cropGrowth = cropGrowth < 0 ? Value.syncedFixed(cropGrowth) : Value.fixed(cropGrowth);
        return this;
    }

    @Override
    public double getCropGrowth() {
        return this.cropGrowth.get();
    }

    @Override
    public Plot.Builder setSpawnerRates(double spawnerRates) {
        this.spawnerRates = spawnerRates < 0 ? Value.syncedFixed(spawnerRates) : Value.fixed(spawnerRates);
        return this;
    }

    @Override
    public double getSpawnerRates() {
        return this.spawnerRates.get();
    }

    @Override
    public Plot.Builder setMobDrops(double mobDrops) {
        this.mobDrops = mobDrops < 0 ? Value.syncedFixed(mobDrops) : Value.fixed(mobDrops);
        return this;
    }

    @Override
    public double getMobDrops() {
        return this.mobDrops.get();
    }

    @Override
    public Plot.Builder setCoopLimit(int coopLimit) {
        this.coopLimit = coopLimit < 0 ? Value.syncedFixed(coopLimit) : Value.fixed(coopLimit);
        return this;
    }

    @Override
    public int getCoopLimit() {
        return this.coopLimit.get();
    }

    @Override
    public Plot.Builder setBankLimit(BigDecimal bankLimit) {
        Preconditions.checkNotNull(bankLimit, "bankLimit parameter cannot be null.");
        this.bankLimit = bankLimit.compareTo(SYNCED_BANK_LIMIT_VALUE) <= 0 ? Value.syncedFixed(bankLimit) : Value.fixed(bankLimit);
        return this;
    }

    @Override
    public BigDecimal getBankLimit() {
        return this.bankLimit.get();
    }

    @Override
    public Plot.Builder setBalance(BigDecimal balance) {
        Preconditions.checkNotNull(balance, "balance parameter cannot be null.");
        this.balance = balance;
        return this;
    }

    @Override
    public BigDecimal getBalance() {
        return this.balance;
    }

    @Override
    public Plot.Builder setLastInterestTime(long lastInterestTime) {
        this.lastInterestTime = lastInterestTime;
        return this;
    }

    @Override
    public long getLastInterestTime() {
        return this.lastInterestTime;
    }

    @Override
    public Plot.Builder addWarp(String name, String category, Location location, boolean isPrivate, @Nullable ItemStack icon) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        Preconditions.checkNotNull(category, "category parameter cannot be null.");
        Preconditions.checkNotNull(location, "location parameter cannot be null.");
        this.warps.add(new WarpRecord(name, category, location, isPrivate, icon));
        return this;
    }

    @Override
    public boolean hasWarp(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null");

        for (WarpRecord warpRecord : this.warps) {
            if (warpRecord.name.equals(name))
                return true;
        }

        return false;
    }

    @Override
    public boolean hasWarp(Location location) {
        Preconditions.checkNotNull(location, "location parameter cannot be null");

        for (WarpRecord warpRecord : this.warps) {
            if (warpRecord.location.equals(location))
                return true;
        }

        return false;
    }

    @Override
    public Plot.Builder addWarpCategory(String name, int slot, @Nullable ItemStack icon) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");
        Preconditions.checkArgument(slot >= 0, "slot must be positive.");
        this.warpCategories.add(new WarpCategoryRecord(name, slot, icon));
        return this;
    }

    @Override
    public boolean hasWarpCategory(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null");

        for (WarpCategoryRecord warpCategoryRecord : this.warpCategories) {
            if (warpCategoryRecord.name.equals(name))
                return true;
        }

        return false;
    }

    @Override
    public Plot.Builder addBankTransaction(BankTransaction bankTransaction) {
        Preconditions.checkNotNull(bankTransaction, "bankTransaction parameter cannot be null.");
        this.bankTransactions.add(bankTransaction);
        return this;
    }

    @Override
    public List<BankTransaction> getBankTransactions() {
        return Collections.unmodifiableList(this.bankTransactions);
    }

    @Override
    public Plot.Builder setPersistentData(byte[] persistentData) {
        Preconditions.checkNotNull(persistentData, "persistentData parameter cannot be null.");
        this.persistentData = persistentData;
        return this;
    }

    @Override
    public byte[] getPersistentData() {
        return this.persistentData;
    }

    @Override
    public Plot build() {
        if (this.uuid == null)
            throw new IllegalStateException("Cannot create an plot with no valid uuid.");
        if (this.center == null)
            throw new IllegalStateException("Cannot create an plot with no valid location.");

        return plugin.getFactory().createPlot(this);
    }

    private static boolean isValidCenter(Location center) {
        int maxPlotSize = plugin.getSettings().getMaxPlotSize() * 3;
        return center.getBlockX() % maxPlotSize == 0 && center.getBlockZ() % maxPlotSize == 0 &&
                plugin.getGrid().getPlotAt(center) == null;
    }

    private static <K, V extends Number> Map<K, V> convertFromValuesToRaw(Map<K, Value<V>> input) {
        return input.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get()
        ));
    }

}

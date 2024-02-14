package com.bgsoftware.superiorskyblock.core.database.serialization;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridge;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.database.DatabaseResult;
import com.bgsoftware.superiorskyblock.core.database.cache.DatabaseCache;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.IDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.JsonDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.MultipleDeserializer;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer.RawDeserializer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.serialization.Serializers;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.plot.bank.SBankTransaction;
import com.bgsoftware.superiorskyblock.plot.builder.PlotBuilderImpl;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public class PlotsDeserializer {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();
    private static final Gson gson = new GsonBuilder().create();
    private static final IDeserializer oldDataDeserializer = new MultipleDeserializer(
            new JsonDeserializer(null), new RawDeserializer(null, plugin)
    );

    private static final BigDecimal SYNCED_BANK_LIMIT_VALUE = BigDecimal.valueOf(-2);

    private PlotsDeserializer() {

    }

    public static void deserializeMembers(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_members", membersRow -> {
            DatabaseResult members = new DatabaseResult(membersRow);

            Optional<UUID> uuid = members.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot members for null plots, skipping...");
                return;
            }

            Optional<UUID> playerUUID = members.getUUID("player");
            if (!playerUUID.isPresent()) {
                Log.warn("Cannot load plot members with invalid uuids for ", uuid.get(), ", skipping...");
                return;
            }

            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID.get(), false);
            if (superiorPlayer == null) {
                Log.warn("Cannot load plot member with unrecognized uuid: " + playerUUID.get() + ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);

            PlayerRole playerRole = members.getInt("role").map(SPlayerRole::fromId)
                    .orElse(SPlayerRole.defaultRole());


            superiorPlayer.setPlayerRole(playerRole);
            builder.addPlotMember(superiorPlayer);
        });
    }

    public static void deserializeBanned(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_bans", bansRow -> {
            DatabaseResult bans = new DatabaseResult(bansRow);

            Optional<UUID> uuid = bans.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load banned players for null plots, skipping...");
                return;
            }

            Optional<UUID> playerUUID = bans.getUUID("player");
            if (!playerUUID.isPresent()) {
                Log.warn("Cannot load banned players with invalid uuids for ", uuid.get(), ", skipping...");
                return;
            }

            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID.get(), false);
            if (superiorPlayer == null) {
                Log.warn("Cannot load plot ban with unrecognized uuid: " + playerUUID.get() + ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.addBannedPlayer(superiorPlayer);
        });
    }

    public static void deserializeVisitors(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_visitors", visitorsRow -> {
            DatabaseResult visitors = new DatabaseResult(visitorsRow);

            Optional<UUID> plotUUID = visitors.getUUID("plot");
            if (!plotUUID.isPresent()) {
                Log.warn("Cannot load plot visitors for null plots, skipping...");
                return;
            }

            Optional<UUID> uuid = visitors.getUUID("player");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot visitors with invalid uuids for ", plotUUID.get(), ", skipping...");
                return;
            }

            SuperiorPlayer visitorPlayer = plugin.getPlayers().getSuperiorPlayer(uuid.get(), false);
            if (visitorPlayer == null) {
                Log.warn("Cannot load plot visitor with unrecognized uuid: " + uuid.get() + ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(plotUUID.get(), PlotBuilderImpl::new);
            long visitTime = visitors.getLong("visit_time").orElse(System.currentTimeMillis());
            builder.addUniqueVisitor(visitorPlayer, visitTime);
        });
    }

    public static void deserializePlayerPermissions(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_player_permissions", playerPermissionRow -> {
            DatabaseResult playerPermissions = new DatabaseResult(playerPermissionRow);

            Optional<UUID> uuid = playerPermissions.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load player permissions for null plots, skipping...");
                return;
            }

            Optional<UUID> playerUUID = playerPermissions.getUUID("player");
            if (!playerUUID.isPresent()) {
                Log.warn("Cannot load player permissions for invalid players on ", uuid.get(), ", skipping...");
                return;
            }

            SuperiorPlayer superiorPlayer = plugin.getPlayers().getSuperiorPlayer(playerUUID.get(), false);
            if (superiorPlayer == null) {
                Log.warn("Cannot load plot player permissions with unrecognized uuid: " + playerUUID.get() + ", skipping...");
                return;
            }

            Optional<PlotPrivilege> plotPrivilege = playerPermissions.getString("permission").map(name -> {
                try {
                    return PlotPrivilege.getByName(name);
                } catch (NullPointerException error) {
                    return null;
                }
            });
            if (!plotPrivilege.isPresent()) {
                Log.warn("Cannot load player permissions with invalid permission for player ", playerUUID.get(), ", skipping...");
                return;
            }

            Optional<Byte> status = playerPermissions.getByte("status");
            if (!status.isPresent()) {
                Log.warn("Cannot load player permissions with invalid status for player ", playerUUID.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPlayerPermission(superiorPlayer, plotPrivilege.get(), status.get() == 1);
        });
    }

    public static void deserializeRolePermissions(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_role_permissions", rolePermissionsRow -> {
            DatabaseResult rolePermissions = new DatabaseResult(rolePermissionsRow);

            Optional<UUID> uuid = rolePermissions.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load role permissions for null plots, skipping...");
                return;
            }

            Optional<PlayerRole> playerRole = rolePermissions.getInt("role").map(SPlayerRole::fromId);
            if (!playerRole.isPresent()) {
                Log.warn("Cannot load role permissions with invalid role for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<PlotPrivilege> plotPrivilege = rolePermissions.getString("permission").map(name -> {
                try {
                    return PlotPrivilege.getByName(name);
                } catch (NullPointerException error) {
                    return null;
                }
            });
            if (!plotPrivilege.isPresent()) {
                Log.warn("Cannot load role permissions with invalid permission for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setRolePermission(plotPrivilege.get(), playerRole.get());
        });
    }

    public static void deserializeUpgrades(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_upgrades", upgradesRow -> {
            DatabaseResult upgrades = new DatabaseResult(upgradesRow);

            Optional<UUID> uuid = upgrades.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load upgrades for null plots, skipping...");
                return;
            }

            Optional<Upgrade> upgrade = upgrades.getString("upgrade").map(plugin.getUpgrades()::getUpgrade);
            if (!upgrade.isPresent()) {
                Log.warn("Cannot load upgrades with invalid upgrade names for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> level = upgrades.getInt("level");
            if (!level.isPresent()) {
                Log.warn("Cannot load upgrades with invalid levels for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setUpgrade(upgrade.get(), level.get());
        });
    }

    public static void deserializeWarps(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_warps", plotWarpsRow -> {
            DatabaseResult plotWarp = new DatabaseResult(plotWarpsRow);

            Optional<UUID> uuid = plotWarp.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load warps for null plots, skipping...");
                return;
            }

            Optional<String> name = plotWarp.getString("name").map(_name -> {
                return PlotUtils.isWarpNameLengthValid(_name) ? _name : _name.substring(0, PlotUtils.getMaxWarpNameLength());
            });
            if (!name.isPresent() || name.get().isEmpty()) {
                Log.warn("Cannot load warps with invalid names for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Location> location = plotWarp.getString("location").map(Serializers.LOCATION_SERIALIZER::deserialize);
            if (!location.isPresent()) {
                Log.warn("Cannot load warps with invalid locations for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.addWarp(name.get(), plotWarp.getString("category").orElse(""),
                    location.get(), plotWarp.getBoolean("private").orElse(!plugin.getSettings().isPublicWarps()),
                    plotWarp.getString("icon").map(Serializers.ITEM_STACK_SERIALIZER::deserialize).orElse(null));
        });
    }

    public static void deserializeDirtyChunks(Plot.Builder builder, String dirtyChunks) {
        if (Text.isBlank(dirtyChunks))
            return;

        try {
            JsonObject dirtyChunksObject = gson.fromJson(dirtyChunks, JsonObject.class);
            dirtyChunksObject.entrySet().forEach(dirtyChunkEntry -> {
                String worldName = dirtyChunkEntry.getKey();
                JsonArray dirtyChunksArray = dirtyChunkEntry.getValue().getAsJsonArray();

                dirtyChunksArray.forEach(dirtyChunkElement -> {
                    String[] chunkPositionSections = dirtyChunkElement.getAsString().split(",");
                    builder.setDirtyChunk(worldName, Integer.parseInt(chunkPositionSections[0]),
                            Integer.parseInt(chunkPositionSections[1]));
                });
            });
        } catch (JsonSyntaxException ex) {
            if (dirtyChunks.contains("|")) {
                String[] serializedSections = dirtyChunks.split("\\|");

                for (String section : serializedSections) {
                    String[] worldSections = section.split("=");
                    if (worldSections.length == 2) {
                        String[] dirtyChunkSections = worldSections[1].split(";");
                        for (String dirtyChunk : dirtyChunkSections) {
                            String[] dirtyChunkSection = dirtyChunk.split(",");
                            if (dirtyChunkSection.length == 2) {
                                builder.setDirtyChunk(worldSections[0],
                                        Integer.parseInt(dirtyChunkSection[0]), Integer.parseInt(dirtyChunkSection[1]));
                            }
                        }
                    }
                }
            } else {
                String[] dirtyChunkSections = dirtyChunks.split(";");
                for (String dirtyChunk : dirtyChunkSections) {
                    String[] dirtyChunkSection = dirtyChunk.split(",");
                    if (dirtyChunkSection.length == 3) {
                        builder.setDirtyChunk(dirtyChunkSection[0],
                                Integer.parseInt(dirtyChunkSection[1]), Integer.parseInt(dirtyChunkSection[2]));
                    }
                }
            }
        }
    }

    public static void deserializeBlockCounts(Plot.Builder builder, String blocks) {
        if (Text.isBlank(blocks))
            return;

        JsonArray blockCounts;

        try {
            blockCounts = gson.fromJson(blocks, JsonArray.class);
        } catch (JsonSyntaxException error) {
            blockCounts = gson.fromJson(oldDataDeserializer.deserializeBlockCounts(blocks), JsonArray.class);
        }

        blockCounts.forEach(blockCountElement -> {
            JsonObject blockCountObject = blockCountElement.getAsJsonObject();
            Key blockKey = Keys.ofMaterialAndData(blockCountObject.get("id").getAsString());
            BigInteger amount = new BigInteger(blockCountObject.get("amount").getAsString());
            builder.setBlockCount(blockKey, amount);
        });
    }

    public static void deserializeBlockLimits(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_block_limits", blockLimitRow -> {
            DatabaseResult blockLimits = new DatabaseResult(blockLimitRow);

            Optional<UUID> uuid = blockLimits.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load block limits for null plots, skipping...");
                return;
            }

            Optional<Key> block = blockLimits.getString("block").map(Keys::ofMaterialAndData);
            if (!block.isPresent()) {
                Log.warn("Cannot load block limits for invalid blocks for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> limit = blockLimits.getInt("limit");
            if (!limit.isPresent()) {
                Log.warn("Cannot load block limits with invalid limits for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setBlockLimit(block.get(), limit.get());
        });
    }

    public static void deserializeEntityLimits(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_entity_limits", entityLimitsRow -> {
            DatabaseResult entityLimits = new DatabaseResult(entityLimitsRow);

            Optional<UUID> uuid = entityLimits.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load entity limits for null plots, skipping...");
                return;
            }

            Optional<Key> entity = entityLimits.getString("entity").map(Keys::ofEntityType);
            if (!entity.isPresent()) {
                Log.warn("Cannot load entity limits for invalid entities on ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> limit = entityLimits.getInt("limit");
            if (!limit.isPresent()) {
                Log.warn("Cannot load entity limits with invalid limits for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setEntityLimit(entity.get(), limit.get());
        });
    }

    public static void deserializeRatings(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_ratings", ratingsRow -> {
            DatabaseResult ratings = new DatabaseResult(ratingsRow);

            Optional<UUID> plotUUID = ratings.getUUID("plot");
            if (!plotUUID.isPresent()) {
                Log.warn("Cannot load ratings for null plots, skipping...");
                return;
            }

            Optional<UUID> uuid = ratings.getUUID("player");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load ratings with invalid players for ", plotUUID.get(), ", skipping...");
                return;
            }

            SuperiorPlayer ratingPlayer = plugin.getPlayers().getSuperiorPlayer(uuid.get(), false);
            if (ratingPlayer == null) {
                Log.warn("Cannot load plot rating with unrecognized uuid: " + uuid.get() + ", skipping...");
                return;
            }

            Optional<Rating> rating = ratings.getInt("rating").map(value -> {
                try {
                    return Rating.valueOf(value);
                } catch (ArrayIndexOutOfBoundsException error) {
                    return null;
                }
            });
            if (!rating.isPresent()) {
                Log.warn("Cannot load ratings with invalid rating value for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(plotUUID.get(), PlotBuilderImpl::new);
            builder.setRating(ratingPlayer, rating.get());
        });
    }

    public static void deserializeMissions(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_missions", missionsRow -> {
            DatabaseResult missions = new DatabaseResult(missionsRow);

            Optional<UUID> uuid = missions.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot missions for null plots, skipping...");
                return;
            }

            Optional<String> missionName = missions.getString("name");
            Optional<Mission<?>> mission = missionName.map(plugin.getMissions()::getMission);
            if (!mission.isPresent()) {
                if (!missionName.isPresent()) {
                    Log.warn("Cannot load plot missions with invalid missions for ", uuid.get(), ", skipping...");
                } else {
                    Log.warn("Cannot load plot missions with invalid mission ",
                            missionName.get(), " for ", uuid.get(), ", skipping...");
                }
                return;
            }

            Optional<Integer> finishCount = missions.getInt("finish_count");
            if (!finishCount.isPresent()) {
                Log.warn("Cannot load plot missions with invalid finish count for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setCompletedMission(mission.get(), finishCount.get());
        });
    }

    public static void deserializePlotFlags(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_flags", plotFlagRow -> {
            DatabaseResult plotFlagResult = new DatabaseResult(plotFlagRow);

            Optional<UUID> uuid = plotFlagResult.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot flags for null plots, skipping...");
                return;
            }

            Optional<PlotFlag> plotFlag = plotFlagResult.getString("name").map(name -> {
                try {
                    return PlotFlag.getByName(name);
                } catch (NullPointerException error) {
                    return null;
                }
            });
            if (!plotFlag.isPresent()) {
                Log.warn("Cannot load plot flags with invalid flags for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Byte> status = plotFlagResult.getByte("status");
            if (!status.isPresent()) {
                Log.warn("Cannot load plot flags with invalid status for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPlotFlag(plotFlag.get(), status.get() == 1);
        });
    }

    public static void deserializeGenerators(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_generators", generatorsRow -> {
            DatabaseResult generators = new DatabaseResult(generatorsRow);

            Optional<UUID> uuid = generators.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load generator rates for null plots, skipping...");
                return;
            }

            Optional<Integer> environment = generators.getEnum("environment", World.Environment.class)
                    .map(Enum::ordinal);
            if (!environment.isPresent()) {
                Log.warn("Cannot load generator rates with invalid environment for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Key> block = generators.getString("block").map(Keys::ofMaterialAndData);
            if (!block.isPresent()) {
                Log.warn("Cannot load generator rates with invalid block for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> rate = generators.getInt("rate");
            if (!rate.isPresent()) {
                Log.warn("Cannot load generator rates with invalid rate for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setGeneratorRate(block.get(), rate.get(), World.Environment.values()[environment.get()]);
        });
    }

    public static void deserializePlotHomes(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_homes", plotHomesRow -> {
            DatabaseResult plotHomes = new DatabaseResult(plotHomesRow);

            Optional<UUID> uuid = plotHomes.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot homes for null plots, skipping...");
                return;
            }

            Optional<Integer> environment = plotHomes.getEnum("environment", World.Environment.class)
                    .map(Enum::ordinal);
            if (!environment.isPresent()) {
                Log.warn("Cannot load plot homes with invalid environment for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Location> location = plotHomes.getString("location").map(Serializers.LOCATION_SERIALIZER::deserialize);
            if (!location.isPresent()) {
                Log.warn("Cannot load plot homes with invalid location for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPlotHome(location.get(), World.Environment.values()[environment.get()]);
        });
    }

    public static void deserializeVisitorHomes(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_visitor_homes", plotVisitorHomesRow -> {
            DatabaseResult plotVisitorHomes = new DatabaseResult(plotVisitorHomesRow);

            Optional<UUID> uuid = plotVisitorHomes.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot homes for null plots, skipping...");
                return;
            }

            Optional<Integer> environment = plotVisitorHomes.getEnum("environment", World.Environment.class)
                    .map(Enum::ordinal);
            if (!environment.isPresent()) {
                Log.warn("Cannot load plot homes with invalid environment for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Location> location = plotVisitorHomes.getString("location").map(Serializers.LOCATION_SERIALIZER::deserialize);
            if (!location.isPresent()) {
                Log.warn("Cannot load plot homes with invalid location for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setVisitorHome(location.get(), World.Environment.values()[environment.get()]);
        });
    }

    public static void deserializeEffects(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_effects", plotEffectRow -> {
            DatabaseResult plotEffects = new DatabaseResult(plotEffectRow);

            Optional<UUID> uuid = plotEffects.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot effects for null plots, skipping...");
                return;
            }

            Optional<PotionEffectType> effectType = plotEffects.getString("effect_type")
                    .map(PotionEffectType::getByName);
            if (!effectType.isPresent()) {
                Log.warn("Cannot load plot effects with invalid effect for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> level = plotEffects.getInt("level");
            if (!level.isPresent()) {
                Log.warn("Cannot load plot effects with invalid level for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPlotEffect(effectType.get(), level.get());
        });
    }

    public static void deserializePlotChest(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_chests", plotChestsRow -> {
            DatabaseResult plotChests = new DatabaseResult(plotChestsRow);

            Optional<UUID> uuid = plotChests.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot chests for null plots, skipping...");
                return;
            }

            Optional<Integer> index = plotChests.getInt("index");
            if (!index.isPresent() || index.get() < 0) {
                Log.warn("Cannot load plot chest with invalid index for ", uuid.get(), ", skipping...");
                return;
            }

            Optional<ItemStack[]> contents = plotChests.getBlob("contents").map(Serializers.INVENTORY_SERIALIZER::deserialize);
            if (!contents.isPresent()) {
                Log.warn("Cannot load plot chest with invalid contents for ", uuid.get(), ", skipping...");
                return;
            }

            int contentsLength = contents.get().length;
            ItemStack[] chestContents;

            if (contentsLength % 9 != 0) {
                int amountOfRows = Math.min(1, Math.max(6, (contentsLength / 9) + 1));
                chestContents = new ItemStack[amountOfRows * 9];
                int amountOfContentsToCopy = Math.min(contentsLength, chestContents.length);
                System.arraycopy(contents.get(), 0, chestContents, 0, amountOfContentsToCopy);
            } else if (contentsLength > 54) {
                chestContents = new ItemStack[54];
                System.arraycopy(contents.get(), 0, chestContents, 0, 54);
            } else if (contentsLength < 9) {
                chestContents = new ItemStack[9];
                System.arraycopy(contents.get(), 0, chestContents, 0, contentsLength);
            } else {
                chestContents = contents.get();
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPlotChest(index.get(), chestContents);
        });
    }

    public static void deserializeRoleLimits(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_role_limits", roleLimitRaw -> {
            DatabaseResult roleLimits = new DatabaseResult(roleLimitRaw);

            Optional<UUID> uuid = roleLimits.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load role limits for null plots, skipping...");
                return;
            }

            Optional<PlayerRole> playerRole = roleLimits.getInt("role").map(SPlayerRole::fromId);
            if (!playerRole.isPresent()) {
                Log.warn("Cannot load role limit for invalid role on ", uuid.get(), ", skipping...");
                return;
            }

            Optional<Integer> limit = roleLimits.getInt("limit");
            if (!limit.isPresent()) {
                Log.warn("Cannot load role limit for invalid limit on ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setRoleLimit(playerRole.get(), limit.get());
        });
    }

    public static void deserializeWarpCategories(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_warp_categories", warpCategoryRow -> {
            DatabaseResult warpCategory = new DatabaseResult(warpCategoryRow);

            Optional<UUID> uuid = warpCategory.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load warp categories for null plots, skipping...");
                return;
            }

            Optional<String> name = warpCategory.getString("name").map(Formatters.STRIP_COLOR_FORMATTER::format);
            if (!name.isPresent() || name.get().isEmpty()) {
                Log.warn("Cannot load warp categories with invalid name for ", uuid.get(), ", skipping...");
                return;
            }

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.addWarpCategory(name.get(), warpCategory.getInt("slot").orElse(-1),
                    warpCategory.getString("icon").map(Serializers.ITEM_STACK_SERIALIZER::deserialize).orElse(null));
        });
    }

    public static void deserializePlotBank(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_banks", plotBankRow -> {
            DatabaseResult plotBank = new DatabaseResult(plotBankRow);

            Optional<UUID> uuid = plotBank.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load plot banks for null plots, skipping...");
                return;
            }

            Optional<BigDecimal> balance = plotBank.getBigDecimal("balance");
            if (!balance.isPresent()) {
                Log.warn("Cannot load plot banks with invalid balance for ", uuid.get(), ", skipping...");
                return;
            }

            long currentTime = System.currentTimeMillis() / 1000;

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setBalance(balance.get());
            long lastInterestTime = plotBank.getLong("last_interest_time").orElse(currentTime);
            builder.setLastInterestTime(lastInterestTime > currentTime ? lastInterestTime / 1000 : lastInterestTime);
        });
    }

    public static void deserializePlotSettings(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_settings", plotSettingsRow -> {
            DatabaseResult plotSettings = new DatabaseResult(plotSettingsRow);

            Optional<String> plot = plotSettings.getString("plot");
            if (!plot.isPresent()) {
                Log.warn("Cannot load plot settings of null plot, skipping ");
                return;
            }

            UUID uuid = UUID.fromString(plot.get());
            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid, PlotBuilderImpl::new);

            builder.setPlotSize(plotSettings.getInt("size").orElse(-1));
            builder.setTeamLimit(plotSettings.getInt("members_limit").orElse(-1));
            builder.setWarpsLimit(plotSettings.getInt("warps_limit").orElse(-1));
            builder.setCropGrowth(plotSettings.getDouble("crop_growth_multiplier").orElse(-1D));
            builder.setSpawnerRates(plotSettings.getDouble("spawner_rates_multiplier").orElse(-1D));
            builder.setMobDrops(plotSettings.getDouble("mob_drops_multiplier").orElse(-1D));
            builder.setCoopLimit(plotSettings.getInt("coops_limit").orElse(-1));
            builder.setBankLimit(plotSettings.getBigDecimal("bank_limit").orElse(SYNCED_BANK_LIMIT_VALUE));
        });
    }

    public static void deserializeBankTransactions(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        if (BuiltinModules.BANK.bankLogs && BuiltinModules.BANK.cacheAllLogs) {
            databaseBridge.loadAllObjects("bank_transactions", bankTransactionRow -> {
                DatabaseResult bankTransaction = new DatabaseResult(bankTransactionRow);

                Optional<UUID> uuid = bankTransaction.getUUID("plot");
                if (!uuid.isPresent()) {
                    Log.warn("Cannot load bank transaction for null plots, skipping...");
                    return;
                }

                Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
                SBankTransaction.fromDatabase(bankTransaction).ifPresent(builder::addBankTransaction);
            });
        }
    }

    public static void deserializePersistentDataContainer(DatabaseBridge databaseBridge, DatabaseCache<Plot.Builder> databaseCache) {
        databaseBridge.loadAllObjects("plots_custom_data", customDataRow -> {
            DatabaseResult customData = new DatabaseResult(customDataRow);

            Optional<UUID> uuid = customData.getUUID("plot");
            if (!uuid.isPresent()) {
                Log.warn("Cannot load custom data for null plots, skipping...");
                return;
            }

            byte[] persistentData = customData.getBlob("data").orElse(new byte[0]);

            if (persistentData.length == 0)
                return;

            Plot.Builder builder = databaseCache.computeIfAbsentInfo(uuid.get(), PlotBuilderImpl::new);
            builder.setPersistentData(persistentData);
        });
    }
}

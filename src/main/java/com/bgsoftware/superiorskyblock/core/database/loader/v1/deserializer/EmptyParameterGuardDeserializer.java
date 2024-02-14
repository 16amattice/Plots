package com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer;

import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotChestAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotWarpAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlayerAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.WarpCategoryAttributes;
import com.bgsoftware.superiorskyblock.core.key.KeyMaps;
import com.bgsoftware.superiorskyblock.plot.privilege.PlayerPrivilegeNode;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class EmptyParameterGuardDeserializer implements IDeserializer {

    private static final EmptyParameterGuardDeserializer INSTANCE = new EmptyParameterGuardDeserializer();

    public static EmptyParameterGuardDeserializer getInstance() {
        return INSTANCE;
    }

    private EmptyParameterGuardDeserializer() {

    }

    private static <T> T checkParam(String param, Supplier<T> emptyValue) {
        if (Text.isBlank(param))
            return emptyValue.get();

        throw new RuntimeException(); // Will continue to other deserializers
    }

    @Override
    public Map<String, Integer> deserializeMissions(String missions) {
        return checkParam(missions, Collections::emptyMap);
    }

    @Override
    public String[] deserializeHomes(String locationParam) {
        return checkParam(locationParam, () -> new String[World.Environment.values().length]);
    }

    @Override
    public List<PlayerAttributes> deserializePlayers(String players) {
        return checkParam(players, Collections::emptyList);
    }

    @Override
    public Map<UUID, PlayerPrivilegeNode> deserializePlayerPerms(String permissionNodes) {
        return checkParam(permissionNodes, Collections::emptyMap);
    }

    @Override
    public Map<PlotPrivilege, PlayerRole> deserializeRolePerms(String permissionNodes) {
        return checkParam(permissionNodes, Collections::emptyMap);
    }

    @Override
    public Map<String, Integer> deserializeUpgrades(String upgrades) {
        return checkParam(upgrades, Collections::emptyMap);
    }

    @Override
    public List<PlotWarpAttributes> deserializeWarps(String plotWarps) {
        return checkParam(plotWarps, Collections::emptyList);
    }

    @Override
    public KeyMap<Integer> deserializeBlockLimits(String blocks) {
        return checkParam(blocks, KeyMaps::createEmptyMap);
    }

    @Override
    public Map<UUID, Rating> deserializeRatings(String ratings) {
        return checkParam(ratings, Collections::emptyMap);
    }

    @Override
    public Map<PlotFlag, Byte> deserializePlotFlags(String settings) {
        return checkParam(settings, Collections::emptyMap);
    }

    @Override
    public KeyMap<Integer>[] deserializeGenerators(String generator) {
        return checkParam(generator, () -> new KeyMap[World.Environment.values().length]);
    }

    @Override
    public List<Pair<UUID, Long>> deserializeVisitors(String visitors) {
        return checkParam(visitors, Collections::emptyList);
    }

    @Override
    public KeyMap<Integer> deserializeEntityLimits(String entities) {
        return checkParam(entities, KeyMaps::createEmptyMap);
    }

    @Override
    public Map<PotionEffectType, Integer> deserializeEffects(String effects) {
        return checkParam(effects, Collections::emptyMap);
    }

    @Override
    public List<PlotChestAttributes> deserializePlotChests(String plotChest) {
        return checkParam(plotChest, Collections::emptyList);
    }

    @Override
    public Map<PlayerRole, Integer> deserializeRoleLimits(String roles) {
        return checkParam(roles, Collections::emptyMap);
    }

    @Override
    public List<WarpCategoryAttributes> deserializeWarpCategories(String categories) {
        return checkParam(categories, Collections::emptyList);
    }

    @Override
    public String deserializeBlockCounts(String blockCountsParam) {
        return checkParam(blockCountsParam, () -> "[]");
    }

    @Override
    public String deserializeDirtyChunks(String dirtyChunksParam) {
        return checkParam(dirtyChunksParam, () -> "[]");
    }

}

package com.bgsoftware.superiorskyblock.core.database.loader.v1.deserializer;

import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotChestAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlotWarpAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.PlayerAttributes;
import com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes.WarpCategoryAttributes;
import com.bgsoftware.superiorskyblock.plot.privilege.PlayerPrivilegeNode;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IDeserializer {

    Map<String, Integer> deserializeMissions(String missions);

    String[] deserializeHomes(String locationParam);

    List<PlayerAttributes> deserializePlayers(String players);

    Map<UUID, PlayerPrivilegeNode> deserializePlayerPerms(String permissionNodes);

    Map<PlotPrivilege, PlayerRole> deserializeRolePerms(String permissionNodes);

    Map<String, Integer> deserializeUpgrades(String upgrades);

    List<PlotWarpAttributes> deserializeWarps(String plotWarps);

    KeyMap<Integer> deserializeBlockLimits(String blocks);

    Map<UUID, Rating> deserializeRatings(String ratings);

    Map<PlotFlag, Byte> deserializePlotFlags(String settings);

    KeyMap<Integer>[] deserializeGenerators(String generator);

    List<Pair<UUID, Long>> deserializeVisitors(String visitors);

    KeyMap<Integer> deserializeEntityLimits(String entities);

    Map<PotionEffectType, Integer> deserializeEffects(String effects);

    List<PlotChestAttributes> deserializePlotChests(String plotChest);

    Map<PlayerRole, Integer> deserializeRoleLimits(String roles);

    List<WarpCategoryAttributes> deserializeWarpCategories(String categories);

    String deserializeBlockCounts(String blockCountsParam);

    String deserializeDirtyChunks(String dirtyChunksParam);

}

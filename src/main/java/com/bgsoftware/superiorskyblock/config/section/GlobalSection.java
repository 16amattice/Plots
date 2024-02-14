package com.bgsoftware.superiorskyblock.config.section;

import com.bgsoftware.superiorskyblock.api.enums.TopPlotMembersSorting;
import com.bgsoftware.superiorskyblock.api.handlers.BlockValuesManager;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.player.respawn.RespawnAction;
import com.bgsoftware.superiorskyblock.config.SettingsContainerHolder;
import org.bukkit.Location;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalSection extends SettingsContainerHolder {

    public long getCalcInterval() {
        return getContainer().calcInterval;
    }

    public String getPlotCommand() {
        return getContainer().plotCommand;
    }

    public int getMaxPlotSize() {
        return getContainer().maxPlotSize;
    }

    public int getPlotHeight() {
        return getContainer().plotsHeight;
    }

    public boolean isWorldBorders() {
        return getContainer().worldBordersEnabled;
    }

    public String getPlotLevelFormula() {
        return getContainer().plotLevelFormula;
    }

    public boolean isRoundedPlotLevels() {
        return getContainer().roundedPlotLevel;
    }

    public String getPlotTopOrder() {
        return getContainer().plotTopOrder;
    }

    public boolean isCoopMembers() {
        return getContainer().coopMembers;
    }

    public String getSignWarpLine() {
        return getContainer().signWarpLine;
    }

    public List<String> getSignWarp() {
        return getContainer().signWarp;
    }

    public List<String> getInteractables() {
        return getContainer().interactables;
    }

    public Collection<Key> getSafeBlocks() {
        return getContainer().safeBlocks;
    }

    public boolean isVisitorsDamage() {
        return getContainer().visitorsDamage;
    }

    public boolean isCoopDamage() {
        return getContainer().coopDamage;
    }

    public int getDisbandCount() {
        return getContainer().disbandCount;
    }

    public boolean isPlotTopIncludeLeader() {
        return getContainer().plotTopIncludeLeader;
    }

    public Map<String, String> getDefaultPlaceholders() {
        return getContainer().defaultPlaceholders;
    }

    public boolean isBanConfirm() {
        return getContainer().banConfirm;
    }

    public boolean isDisbandConfirm() {
        return getContainer().disbandConfirm;
    }

    public boolean isKickConfirm() {
        return getContainer().kickConfirm;
    }

    public boolean isLeaveConfirm() {
        return getContainer().leaveConfirm;
    }

    public String getSpawnersProvider() {
        return getContainer().spawnersProvider;
    }

    public String getStackedBlocksProvider() {
        return getContainer().stackedBlocksProvider;
    }

    public boolean isDisbandInventoryClear() {
        return getContainer().disbandInventoryClear;
    }

    public boolean isTeleportOnJoin() {
        return getContainer().teleportOnJoin;
    }

    public boolean isTeleportOnKick() {
        return getContainer().teleportOnKick;
    }

    public boolean isClearOnJoin() {
        return getContainer().clearOnJoin;
    }

    public boolean isRateOwnPlot() {
        return getContainer().rateOwnPlot;
    }

    public List<String> getDefaultSettings() {
        return getContainer().defaultSettings;
    }

    public boolean isDisableRedstoneOffline() {
        return getContainer().disableRedstoneOffline;
    }

    public Map<String, Pair<Integer, String>> getCommandsCooldown() {
        return getContainer().commandsCooldown;
    }

    public long getUpgradeCooldown() {
        return getContainer().upgradeCooldown;
    }

    public String getNumbersFormat() {
        return getContainer().numberFormat;
    }

    public String getDateFormat() {
        return getContainer().dateFormat;
    }

    public boolean isSkipOneItemMenus() {
        return getContainer().skipOneItemMenus;
    }

    public boolean isTeleportOnPvPEnable() {
        return getContainer().teleportOnPVPEnable;
    }

    public boolean isImmuneToPvPWhenTeleport() {
        return getContainer().immuneToPVPWhenTeleport;
    }

    public List<String> getBlockedVisitorsCommands() {
        return getContainer().blockedVisitorsCommands;
    }

    public List<String> getDefaultSign() {
        return getContainer().defaultSignLines;
    }

    public Map<String, List<String>> getEventCommands() {
        return getContainer().eventCommands;
    }

    public long getWarpsWarmup() {
        return getContainer().warpsWarmup;
    }

    public long getHomeWarmup() {
        return getContainer().homeWarmup;
    }

    public boolean isLiquidUpdate() {
        return getContainer().liquidUpdate;
    }

    public boolean isLightsUpdate() {
        return getContainer().lightsUpdate;
    }

    public List<String> getPvPWorlds() {
        return getContainer().pvpWorlds;
    }

    public boolean isStopLeaving() {
        return getContainer().stopLeaving;
    }

    public boolean isValuesMenu() {
        return getContainer().valuesMenu;
    }

    public List<String> getCropsToGrow() {
        return getContainer().cropsToGrow;
    }

    public int getCropsInterval() {
        return getContainer().cropsInterval;
    }

    public boolean isOnlyBackButton() {
        return getContainer().onlyBackButton;
    }

    public boolean isBuildOutsidePlot() {
        return getContainer().buildOutsidePlot;
    }

    public String getDefaultLanguage() {
        return getContainer().defaultLanguage;
    }

    public boolean isDefaultWorldBorder() {
        return getContainer().defaultWorldBorder;
    }

    public boolean isDefaultStackedBlocks() {
        return getContainer().defaultBlocksStacker;
    }

    public boolean isDefaultToggledPanel() {
        return getContainer().defaultToggledPanel;
    }

    public boolean isDefaultPlotFly() {
        return getContainer().defaultPlotFly;
    }

    public String getDefaultBorderColor() {
        return getContainer().defaultBorderColor;
    }

    public boolean isObsidianToLava() {
        return getContainer().obsidianToLava;
    }

    public BlockValuesManager.SyncWorthStatus getSyncWorth() {
        return getContainer().syncWorth;
    }

    public boolean isNegativeWorth() {
        return getContainer().negativeWorth;
    }

    public boolean isNegativeLevel() {
        return getContainer().negativeLevel;
    }

    public List<String> getDisabledEvents() {
        return getContainer().disabledEvents;
    }

    public List<String> getDisabledCommands() {
        return getContainer().disabledCommands;
    }

    public List<String> getDisabledHooks() {
        return getContainer().disabledHooks;
    }

    public boolean isSchematicNameArgument() {
        return getContainer().schematicNameArgument;
    }

    public Map<String, List<String>> getCommandAliases() {
        return getContainer().commandAliases;
    }

    public Set<Key> getValuableBlocks() {
        return getContainer().valuableBlocks;
    }

    public Map<String, Location> getPreviewPlots() {
        return getContainer().plotPreviewLocations;
    }

    public boolean isTabCompleteHideVanished() {
        return getContainer().tabCompleteHideVanished;
    }

    public boolean isDropsUpgradePlayersMultiply() {
        return getContainer().dropsUpgradePlayersMultiply;
    }

    public long getProtectedMessageDelay() {
        return getContainer().protectedMessageDelay;
    }

    public boolean isWarpCategories() {
        return getContainer().warpCategories;
    }

    public boolean isPhysicsListener() {
        return getContainer().physicsListener;
    }

    public double getChargeOnWarp() {
        return getContainer().chargeOnWarp;
    }

    public boolean isPublicWarps() {
        return getContainer().publicWarps;
    }

    public long getRecalcTaskTimeout() {
        return getContainer().recalcTaskTimeout;
    }

    public boolean isAutoLanguageDetection() {
        return getContainer().autoLanguageDetection;
    }

    public boolean isAutoUncoopWhenAlone() {
        return getContainer().autoUncoopWhenAlone;
    }

    public TopPlotMembersSorting getTopPlotMembersSorting() {
        return getContainer().plotTopMembersSorting;
    }

    public int getBossbarLimit() {
        return getContainer().bossBarLimit;
    }

    public boolean getDeleteUnsafeWarps() {
        return getContainer().deleteUnsafeWarps;
    }

    public List<RespawnAction> getPlayerRespawn() {
        return getContainer().playerRespawnActions;
    }

    public BigInteger getBlockCountsSaveThreshold() {
        return getContainer().blockCountsSaveThreshold;
    }

}

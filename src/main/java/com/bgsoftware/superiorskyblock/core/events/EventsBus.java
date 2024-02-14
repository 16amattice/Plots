package com.bgsoftware.superiorskyblock.core.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.enums.BorderColor;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.events.*;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.container.PlotsContainer;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.missions.IMissionsHolder;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.player.container.PlayersContainer;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.service.message.IMessageComponent;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.upgrades.UpgradeLevel;
import com.bgsoftware.superiorskyblock.api.upgrades.cost.UpgradeCost;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.ChunkPosition;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public class EventsBus {

    private final SuperiorSkyblockPlugin plugin;

    public EventsBus(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }


    public boolean callAttemptPlayerSendMessageEvent(SuperiorPlayer receiver, String messageType, Object... args) {
        return callEvent(() -> new AttemptPlayerSendMessageEvent(receiver, messageType, args), "attemptplayersendmessageevent");
    }

    public boolean callBlockStackEvent(Block block, Player player, int originalAmount, int newAmount) {
        return callEvent(() -> new BlockStackEvent(block, player, originalAmount, newAmount),
                "blockstackevent");
    }

    public boolean callBlockUnstackEvent(Block block, Player player, int originalAmount, int newAmount) {
        return callEvent(() -> new BlockUnstackEvent(block, player, originalAmount, newAmount),
                "blockunstackevent");
    }

    public boolean callPlotBanEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        return callEvent(() -> new PlotBanEvent(superiorPlayer, targetPlayer, plot), "plotbanevent");
    }

    public EventResult<String> callPlotBankDepositEvent(SuperiorPlayer superiorPlayer, Plot plot, BigDecimal amount) {
        return callEvent(() -> new PlotBankDepositEvent(superiorPlayer, plot, amount),
                "plotbankdepositevent", null, PlotBankDepositEvent::getFailureReason);
    }

    public EventResult<String> callPlotBankWithdrawEvent(SuperiorPlayer superiorPlayer, Plot plot, BigDecimal amount) {
        return callEvent(() -> new PlotBankWithdrawEvent(superiorPlayer, plot, amount),
                "plotbankwithdrawevent", null, PlotBankWithdrawEvent::getFailureReason);
    }

    public EventResult<Biome> callPlotBiomeChangeEvent(SuperiorPlayer superiorPlayer, Plot plot, Biome biome) {
        return callEvent(() -> new PlotBiomeChangeEvent(superiorPlayer, plot, biome),
                "plotbiomechangeevent", biome, PlotBiomeChangeEvent::getBiome);
    }

    public EventResult<BigDecimal> callPlotChangeBankLimitEvent(CommandSender commandSender, Plot plot, BigDecimal bankLimit) {
        return callEvent(() -> new PlotChangeBankLimitEvent(getSuperiorPlayer(commandSender), plot, bankLimit),
                "plotchangebanklimitevent", bankLimit, PlotChangeBankLimitEvent::getBankLimit);
    }

    public EventResult<Integer> callPlotChangeBlockLimitEvent(CommandSender commandSender, Plot plot, Key block, int blockLimit) {
        return callEvent(() -> new PlotChangeBlockLimitEvent(getSuperiorPlayer(commandSender), plot, block, blockLimit),
                "plotchangeblocklimitevent", blockLimit, PlotChangeBlockLimitEvent::getBlockLimit);
    }

    public EventResult<Integer> callPlotChangeBorderSizeEvent(CommandSender commandSender, Plot plot, int borderSize) {
        return callEvent(() -> new PlotChangeBorderSizeEvent(getSuperiorPlayer(commandSender), plot, borderSize),
                "plotchangebordersizeevent", borderSize, PlotChangeBorderSizeEvent::getBorderSize);
    }

    public EventResult<Integer> callPlotChangeCoopLimitEvent(CommandSender commandSender, Plot plot, int coopLimit) {
        return callEvent(() -> new PlotChangeCoopLimitEvent(getSuperiorPlayer(commandSender), plot, coopLimit),
                "plotchangecooplimitevent", coopLimit, PlotChangeCoopLimitEvent::getCoopLimit);
    }

    public EventResult<Double> callPlotChangeCropGrowthEvent(CommandSender commandSender, Plot plot, double cropGrowth) {
        return callEvent(() -> new PlotChangeCropGrowthEvent(getSuperiorPlayer(commandSender), plot, cropGrowth),
                "plotchangecropgrowthevent", cropGrowth, PlotChangeCropGrowthEvent::getCropGrowth);
    }

    public EventResult<String> callPlotChangeDescriptionEvent(SuperiorPlayer superiorPlayer, Plot plot, String description) {
        return callEvent(() -> new PlotChangeDescriptionEvent(plot, superiorPlayer, description),
                "plotchangedescriptionevent", description, PlotChangeDescriptionEvent::getDescription);
    }

    public EventResult<String> callPlotChangeDiscordEvent(SuperiorPlayer superiorPlayer, Plot plot, String discord) {
        return callEvent(() -> new PlotChangeDiscordEvent(superiorPlayer, plot, discord),
                "plotchangediscordevent", discord, PlotChangeDiscordEvent::getDiscord);
    }

    public EventResult<Integer> callPlotChangeEffectLevelEvent(CommandSender commandSender, Plot plot,
                                                                 PotionEffectType effectType, int effectLevel) {
        return callEvent(() -> new PlotChangeEffectLevelEvent(getSuperiorPlayer(commandSender), plot, effectType, effectLevel),
                "plotchangeeffectlevelevent", effectLevel, PlotChangeEffectLevelEvent::getEffectLevel);
    }

    public EventResult<Integer> callPlotChangeEntityLimitEvent(CommandSender commandSender, Plot plot, Key entity, int entityLimit) {
        return callEvent(() -> new PlotChangeEntityLimitEvent(getSuperiorPlayer(commandSender), plot, entity, entityLimit),
                "plotchangeentitylimitevent", entityLimit, PlotChangeEntityLimitEvent::getEntityLimit);
    }

    public EventResult<Integer> callPlotChangeGeneratorRateEvent(CommandSender commandSender, Plot plot, Key block,
                                                                   World.Environment environment, int generatorRate) {
        return callPlotChangeGeneratorRateEvent(getSuperiorPlayer(commandSender), plot, block, environment, generatorRate);
    }

    public EventResult<Integer> callPlotChangeGeneratorRateEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, Key block,
                                                                   World.Environment environment, int generatorRate) {
        return callEvent(() -> new PlotChangeGeneratorRateEvent(superiorPlayer, plot, block, environment, generatorRate),
                "plotchangegeneratorrateevent", generatorRate, PlotChangeGeneratorRateEvent::getGeneratorRate);
    }

    public EventResult<BigDecimal> callPlotChangeLevelBonusEvent(CommandSender commandSender, Plot plot,
                                                                   PlotChangeLevelBonusEvent.Reason reason,
                                                                   BigDecimal levelBonus) {
        return callEvent(() -> new PlotChangeLevelBonusEvent(getSuperiorPlayer(commandSender), plot, reason, levelBonus),
                "plotchangelevelbonusevent", levelBonus, PlotChangeLevelBonusEvent::getLevelBonus);
    }

    public EventResult<Integer> callPlotChangeMembersLimitEvent(CommandSender commandSender, Plot plot, int membersLimit) {
        return callEvent(() -> new PlotChangeMembersLimitEvent(getSuperiorPlayer(commandSender), plot, membersLimit),
                "plotchangememberslimitevent", membersLimit, PlotChangeMembersLimitEvent::getMembersLimit);
    }

    public EventResult<Double> callPlotChangeMobDropsEvent(CommandSender commandSender, Plot plot, double mobDrops) {
        return callEvent(() -> new PlotChangeMobDropsEvent(getSuperiorPlayer(commandSender), plot, mobDrops),
                "plotchangemobdropsevent", mobDrops, PlotChangeMobDropsEvent::getMobDrops);
    }

    public EventResult<String> callPlotChangePaypalEvent(SuperiorPlayer superiorPlayer, Plot plot, String paypal) {
        return callEvent(() -> new PlotChangePaypalEvent(superiorPlayer, plot, paypal),
                "plotchangepaypalevent", paypal, PlotChangePaypalEvent::getPaypal);
    }

    public boolean callPlotChangePlayerPrivilegeEvent(Plot plot, SuperiorPlayer superiorPlayer,
                                                        SuperiorPlayer privilegedPlayer, boolean privilegeEnabled) {
        return callEvent(() -> new PlotChangePlayerPrivilegeEvent(plot, superiorPlayer, privilegedPlayer, privilegeEnabled),
                "plotchangeplayerprivilegeevent");
    }

    public EventResult<Integer> callPlotChangeRoleLimitEvent(CommandSender commandSender, Plot plot, PlayerRole playerRole, int roleLimit) {
        return callEvent(() -> new PlotChangeRoleLimitEvent(getSuperiorPlayer(commandSender), plot, playerRole, roleLimit),
                "plotchangerolelimitevent", roleLimit, PlotChangeRoleLimitEvent::getRoleLimit);
    }

    public boolean callPlotChangeRolePrivilegeEvent(Plot plot, PlayerRole playerRole) {
        return callPlotChangeRolePrivilegeEvent(plot, null, playerRole);
    }

    public EventResult<Double> callPlotChangeSpawnerRatesEvent(CommandSender commandSender, Plot plot, double spawnerRates) {
        return callEvent(() -> new PlotChangeSpawnerRatesEvent(getSuperiorPlayer(commandSender), plot, spawnerRates),
                "plotchangespawnerratesevent", spawnerRates, PlotChangeSpawnerRatesEvent::getSpawnerRates);
    }

    public EventResult<ItemStack> callPlotChangeWarpCategoryIconEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                                        WarpCategory warpCategory, @Nullable ItemStack icon) {
        return callEvent(() -> new PlotChangeWarpCategoryIconEvent(superiorPlayer, plot, warpCategory, icon),
                "plotchangewarpcategoryiconevent", icon, PlotChangeWarpCategoryIconEvent::getIcon);
    }

    public EventResult<Integer> callPlotChangeWarpCategorySlotEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                                      WarpCategory warpCategory, int slot, int maxSlot) {
        return callEvent(() -> new PlotChangeWarpCategorySlotEvent(superiorPlayer, plot, warpCategory, slot, maxSlot),
                "plotchangewarpcategoryslotevent", slot, PlotChangeWarpCategorySlotEvent::getSlot);
    }

    public EventResult<ItemStack> callPlotChangeWarpIconEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                                PlotWarp plotWarp, @Nullable ItemStack icon) {
        return callEvent(() -> new PlotChangeWarpIconEvent(superiorPlayer, plot, plotWarp, icon),
                "plotchangewarpiconevent", icon, PlotChangeWarpIconEvent::getIcon);
    }

    public EventResult<Location> callPlotChangeWarpLocationEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                                   PlotWarp plotWarp, Location location) {
        return callEvent(() -> new PlotChangeWarpLocationEvent(superiorPlayer, plot, plotWarp, location),
                "plotchangewarplocationevent", location, PlotChangeWarpLocationEvent::getLocation);
    }

    public EventResult<Integer> callPlotChangeWarpsLimitEvent(CommandSender commandSender, Plot plot, int warpsLimit) {
        return callEvent(() -> new PlotChangeWarpsLimitEvent(getSuperiorPlayer(commandSender), plot, warpsLimit),
                "plotchangewarpslimitevent", warpsLimit, PlotChangeWarpsLimitEvent::getWarpsLimit);
    }

    public EventResult<BigDecimal> callPlotChangeWorthBonusEvent(CommandSender commandSender, Plot plot,
                                                                   PlotChangeWorthBonusEvent.Reason reason,
                                                                   BigDecimal worthBonus) {
        return callEvent(() -> new PlotChangeWorthBonusEvent(getSuperiorPlayer(commandSender), plot, reason, worthBonus),
                "plotchangeworthbonusevent", worthBonus, PlotChangeWorthBonusEvent::getWorthBonus);
    }

    public boolean callPlotChangeRolePrivilegeEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer, PlayerRole playerRole) {
        return callEvent(() -> new PlotChangeRolePrivilegeEvent(plot, superiorPlayer, playerRole),
                "plotchangeroleprivilegeevent");
    }

    public EventResult<String> callPlotChatEvent(Plot plot, SuperiorPlayer superiorPlayer, String message) {
        return callEvent(() -> new PlotChatEvent(plot, superiorPlayer, message),
                "plotchatevent", message, PlotChatEvent::getMessage);
    }

    public void callPlotChunkResetEvent(Plot plot, ChunkPosition chunkPosition) {
        if (!plugin.getSettings().getDisabledEvents().contains("plotchunkresetevent")) {
            callEvent(new PlotChunkResetEvent(plot, chunkPosition.getWorld(), chunkPosition.getX(), chunkPosition.getZ()));
        }
    }

    public boolean callPlotClearGeneratorRatesEvent(CommandSender commandSender, Plot plot, World.Environment environment) {
        return callEvent(() -> new PlotClearGeneratorRatesEvent(getSuperiorPlayer(commandSender), plot, environment),
                "plotcleargeneratorratesevent");
    }

    public boolean callPlotClearPlayerPrivilegesEvent(Plot plot, SuperiorPlayer superiorPlayer,
                                                        SuperiorPlayer privilegedPlayer) {
        return callEvent(() -> new PlotClearPlayerPrivilegesEvent(plot, superiorPlayer, privilegedPlayer),
                "plotclearplayerprivilegesevent");
    }

    public boolean callPlotClearRatingsEvent(CommandSender sender, Plot plot) {
        return callEvent(() -> new PlotClearRatingsEvent(getSuperiorPlayer(sender), plot), "plotclearratingssevent");
    }

    public boolean callPlotClearRolesPrivilegesEvent(Plot plot, SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlotClearRolesPrivilegesEvent(plot, superiorPlayer),
                "plotclearrolesprivilegesevent");
    }

    public boolean callPlotCloseEvent(Plot plot, CommandSender commandSender) {
        return callPlotCloseEvent(plot, getSuperiorPlayer(commandSender));
    }

    public boolean callPlotCloseEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlotCloseEvent(superiorPlayer, plot), "plotcloseevent");
    }

    public boolean callPlotCloseWarpEvent(Plot plot, SuperiorPlayer superiorPlayer, PlotWarp plotWarp) {
        return callEvent(() -> new PlotCloseWarpEvent(superiorPlayer, plot, plotWarp), "plotclosewarpevent");
    }

    public boolean callPlotCoopPlayerEvent(Plot plot, SuperiorPlayer player, SuperiorPlayer target) {
        return callEvent(() -> new PlotCoopPlayerEvent(plot, player, target), "plotcoopplayerevent");
    }

    public EventResult<Boolean> callPlotCreateEvent(SuperiorPlayer superiorPlayer, Plot plot, String schemName) {
        return callEvent(() -> new PlotCreateEvent(superiorPlayer, plot, schemName),
                "plotcreateevent", true, PlotCreateEvent::canTeleport);
    }

    public boolean callPlotCreateWarpCategoryEvent(SuperiorPlayer superiorPlayer, Plot plot, String categoryName) {
        return callEvent(() -> new PlotCreateWarpCategoryEvent(superiorPlayer, plot, categoryName),
                "plotcreatewarpcategoryevent");
    }

    public boolean callPlotCreateWarpEvent(SuperiorPlayer superiorPlayer, Plot plot, String warpName,
                                             Location location, @Nullable WarpCategory warpCategory) {
        return callPlotCreateWarpEvent(superiorPlayer, plot, warpName, location, plugin.getSettings().isPublicWarps(), warpCategory);
    }

    public boolean callPlotCreateWarpEvent(SuperiorPlayer superiorPlayer, Plot plot, String warpName,
                                             Location location, boolean openToPublic, @Nullable WarpCategory warpCategory) {
        return callEvent(() -> new PlotCreateWarpEvent(superiorPlayer, plot, warpName, location, openToPublic, warpCategory),
                "plotcreatewarpevent");
    }

    public boolean callPlotDeleteWarpEvent(CommandSender commandSender, Plot plot, PlotWarp plotWarp) {
        return callPlotDeleteWarpEvent(getSuperiorPlayer(commandSender), plot, plotWarp);
    }

    public boolean callPlotDeleteWarpEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp) {
        return callEvent(() -> new PlotDeleteWarpEvent(superiorPlayer, plot, plotWarp), "plotdeletewarpevent");
    }

    public boolean callPlotDisableFlagEvent(CommandSender commandSender, Plot plot, PlotFlag plotFlag) {
        return callPlotDisableFlagEvent(getSuperiorPlayer(commandSender), plot, plotFlag);
    }

    public boolean callPlotDisableFlagEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlotFlag plotFlag) {
        return callEvent(() -> new PlotDisableFlagEvent(superiorPlayer, plot, plotFlag), "plotdisableflagevent");
    }

    public boolean callPlotDisbandEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        return callEvent(() -> new PlotDisbandEvent(superiorPlayer, plot), "plotdisbandevent");
    }

    public boolean callPlotEnableFlagEvent(CommandSender commandSender, Plot plot, PlotFlag plotFlag) {
        return callPlotEnableFlagEvent(getSuperiorPlayer(commandSender), plot, plotFlag);
    }

    public boolean callPlotEnableFlagEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlotFlag plotFlag) {
        return callEvent(() -> new PlotEnableFlagEvent(superiorPlayer, plot, plotFlag), "plotenableflagevent");
    }

    public boolean callPlotEnterEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotEnterEvent.EnterCause enterCause) {
        if (plugin.getSettings().getDisabledEvents().contains("plotenterevent"))
            return true;

        PlotEnterEvent plotEnterEvent = callEvent(new PlotEnterEvent(superiorPlayer, plot, enterCause));

        if (plotEnterEvent.isCancelled() && plotEnterEvent.getCancelTeleport() != null)
            superiorPlayer.teleport(plotEnterEvent.getCancelTeleport());

        return !plotEnterEvent.isCancelled();
    }

    public EventResult<PortalEventResult> callPlotEnterPortalEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                                     PortalType portalType, World.Environment destination,
                                                                     @Nullable Schematic schematic, boolean ignoreInvalidSchematic) {
        return callEvent(() -> new PlotEnterPortalEvent(plot, superiorPlayer, portalType, destination, schematic, ignoreInvalidSchematic),
                "plotenterportalevent", new PortalEventResult(destination, schematic, ignoreInvalidSchematic), PortalEventResult::new);
    }

    public boolean callPlotEnterProtectedEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotEnterEvent.EnterCause enterCause) {
        if (plugin.getSettings().getDisabledEvents().contains("plotenterprotectedevent"))
            return true;

        PlotEnterProtectedEvent plotEnterProtectedEvent = callEvent(new PlotEnterProtectedEvent(superiorPlayer, plot, enterCause));

        if (plotEnterProtectedEvent.isCancelled() && plotEnterProtectedEvent.getCancelTeleport() != null)
            superiorPlayer.teleport(plotEnterProtectedEvent.getCancelTeleport());

        return !plotEnterProtectedEvent.isCancelled();
    }

    public EventResult<GenerateBlockResult> callPlotGenerateBlockEvent(Plot plot, Location location, Key block) {
        return callEvent(() -> new PlotGenerateBlockEvent(plot, location, block), "plotgenerateblockevent",
                new GenerateBlockResult(block, true), GenerateBlockResult::new);
    }

    public boolean callPlotInviteEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        return callEvent(() -> new PlotInviteEvent(superiorPlayer, targetPlayer, plot), "plotinviteevent");
    }

    @SuppressWarnings("all")
    public boolean callPlotJoinEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotJoinEvent.Cause cause) {
        return callEvent(() -> new PlotJoinEvent(superiorPlayer, plot, cause), "plotjoinevent");
    }

    public boolean callPlotKickEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        return callEvent(() -> new PlotKickEvent(superiorPlayer, targetPlayer, plot), "plotkickevent");
    }

    public boolean callPlotLeaveEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                        PlotLeaveEvent.LeaveCause leaveCause, Location location) {
        return callEvent(() -> new PlotLeaveEvent(superiorPlayer, plot, leaveCause, location), "plotleaveevent");
    }

    public boolean callPlotLeaveProtectedEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                 PlotLeaveEvent.LeaveCause leaveCause, Location location) {
        return callEvent(() -> new PlotLeaveProtectedEvent(superiorPlayer, plot, leaveCause, location),
                "plotleaveprotectedevent");
    }

    public boolean callPlotLockWorldEvent(Plot plot, World.Environment environment) {
        return callEvent(() -> new PlotLockWorldEvent(plot, environment), "plotlockworldevent");
    }

    public boolean callPlotOpenEvent(Plot plot, CommandSender commandSender) {
        return callPlotOpenEvent(plot, getSuperiorPlayer(commandSender));
    }

    public boolean callPlotOpenEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlotOpenEvent(superiorPlayer, plot), "plotopenevent");
    }

    public boolean callPlotOpenWarpEvent(Plot plot, SuperiorPlayer superiorPlayer, PlotWarp plotWarp) {
        return callEvent(() -> new PlotOpenWarpEvent(superiorPlayer, plot, plotWarp), "plotopenwarpevent");
    }

    public boolean callPlotQuitEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        return callEvent(() -> new PlotQuitEvent(superiorPlayer, plot), "plotquitevent");
    }

    public boolean callPlotRateEvent(CommandSender commandSender, SuperiorPlayer ratingPlayer, Plot plot, Rating rating) {
        return callPlotRateEvent(getSuperiorPlayer(commandSender), ratingPlayer, plot, rating);
    }

    public boolean callPlotRateEvent(@Nullable SuperiorPlayer superiorPlayer, SuperiorPlayer ratingPlayer, Plot plot, Rating rating) {
        return callEvent(() -> new PlotRateEvent(superiorPlayer, ratingPlayer, plot, rating), "plotrateevent");
    }

    public boolean callPlotRemoveBlockLimitEvent(CommandSender commandSender, Plot plot, Key block) {
        return callEvent(() -> new PlotRemoveBlockLimitEvent(getSuperiorPlayer(commandSender), plot, block),
                "plotremoveblocklimitevent");
    }

    public boolean callPlotRemoveEffectEvent(CommandSender commandSender, Plot plot, PotionEffectType effectType) {
        return callEvent(() -> new PlotRemoveEffectEvent(getSuperiorPlayer(commandSender), plot, effectType),
                "plotremoveeffectevent");
    }

    public boolean callPlotRemoveGeneratorRateEvent(CommandSender commandSender, Plot plot, Key block,
                                                      World.Environment environment) {
        return callPlotRemoveGeneratorRateEvent(getSuperiorPlayer(commandSender), plot, block, environment);
    }

    public boolean callPlotRemoveGeneratorRateEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, Key block,
                                                      World.Environment environment) {
        return callEvent(() -> new PlotRemoveGeneratorRateEvent(superiorPlayer, plot, block, environment),
                "plotremovegeneratorrateevent");
    }

    public boolean callPlotRemoveRatingEvent(CommandSender commandSender, SuperiorPlayer ratingPlayer, Plot plot) {
        return callPlotRemoveRatingEvent(getSuperiorPlayer(commandSender), ratingPlayer, plot);
    }

    public boolean callPlotRemoveRatingEvent(@Nullable SuperiorPlayer superiorPlayer, SuperiorPlayer ratingPlayer, Plot plot) {
        return callEvent(() -> new PlotRemoveRatingEvent(superiorPlayer, ratingPlayer, plot),
                "plotremoveratingevent");
    }

    public boolean callPlotRemoveRoleLimitEvent(CommandSender commandSender, Plot plot, PlayerRole playerRole) {
        return callEvent(() -> new PlotRemoveRoleLimitEvent(getSuperiorPlayer(commandSender), plot, playerRole),
                "plotremoverolelimitevent");
    }

    public boolean callPlotRemoveVisitorHomeEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        return callEvent(() -> new PlotRemoveVisitorHomeEvent(superiorPlayer, plot), "plotremovevisitorhomeevent");
    }

    public EventResult<String> callPlotRenameEvent(Plot plot, String plotName) {
        return callPlotRenameEvent(plot, null, plotName);
    }

    public EventResult<String> callPlotRenameEvent(Plot plot, @Nullable SuperiorPlayer superiorPlayer, String plotName) {
        return callEvent(() -> new PlotRenameEvent(plot, superiorPlayer, plotName),
                "plotrenameevent", plotName, PlotRenameEvent::getPlotName);
    }

    public EventResult<String> callPlotRenameWarpCategoryEvent(Plot plot, SuperiorPlayer superiorPlayer,
                                                                 WarpCategory warpCategory, String categoryName) {
        return callEvent(() -> new PlotRenameWarpCategoryEvent(superiorPlayer, plot, warpCategory, categoryName),
                "plotrenamewarpcategoryevent", categoryName, PlotRenameWarpCategoryEvent::getCategoryName);
    }

    public EventResult<String> callPlotRenameWarpEvent(Plot plot, SuperiorPlayer superiorPlayer,
                                                         PlotWarp plotWarp, String warpName) {
        return callEvent(() -> new PlotRenameWarpEvent(superiorPlayer, plot, plotWarp, warpName),
                "plotrenamewarpevent", warpName, PlotRenameWarpEvent::getWarpName);
    }

    public void callPlotRestrictMoveEvent(SuperiorPlayer superiorPlayer, PlotRestrictMoveEvent.RestrictReason restrictReason) {
        if (!plugin.getSettings().getDisabledEvents().contains("plotrestrictmoveevent")) {
            callEvent(new PlotRestrictMoveEvent(superiorPlayer, restrictReason));
        }
    }

    public void callPlotSchematicPasteEvent(Plot plot, String name, Location location) {
        if (!plugin.getSettings().getDisabledEvents().contains("plotschematicpasteevent")) {
            callEvent(new PlotSchematicPasteEvent(plot, name, location));
        }
    }

    public EventResult<Location> callPlotSetHomeEvent(Plot plot, Location plotHome, PlotSetHomeEvent.Reason reason,
                                                        @Nullable SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlotSetHomeEvent(plot, plotHome, reason, superiorPlayer),
                "plotsethomeevent", plotHome, PlotSetHomeEvent::getPlotHome);
    }

    public EventResult<Location> callPlotSetVisitorHomeEvent(SuperiorPlayer superiorPlayer, Plot plot,
                                                               Location plotVisitorHome) {
        return callEvent(() -> new PlotSetVisitorHomeEvent(superiorPlayer, plot, plotVisitorHome),
                "plotsetvisitorhomeevent", plotVisitorHome, PlotSetVisitorHomeEvent::getPlotVisitorHome);
    }

    public boolean callPlotTransferEvent(Plot plot, SuperiorPlayer previousOwner, SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlotTransferEvent(plot, previousOwner, superiorPlayer), "plottransferevent");
    }

    public boolean callPlotUnbanEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer unbannedPlayer, Plot plot) {
        return callEvent(() -> new PlotUnbanEvent(superiorPlayer, unbannedPlayer, plot), "plotunbanevent");
    }

    public boolean callPlotUncoopPlayerEvent(Plot plot, SuperiorPlayer player, SuperiorPlayer target,
                                               PlotUncoopPlayerEvent.UncoopReason uncoopReason) {
        return callEvent(() -> new PlotUncoopPlayerEvent(plot, player, target, uncoopReason), "plotuncoopplayerevent");
    }

    public boolean callPlotUnlockWorldEvent(Plot plot, World.Environment environment) {
        return callEvent(() -> new PlotUnlockWorldEvent(plot, environment), "plotunlockworldevent");
    }

    public EventResult<UpgradeResult> callPlotUpgradeEvent(CommandSender commandSender, Plot plot,
                                                             Upgrade upgrade, UpgradeLevel nextUpdate,
                                                             PlotUpgradeEvent.Cause cause) {
        return callPlotUpgradeEvent(getSuperiorPlayer(commandSender), plot, upgrade, nextUpdate,
                Collections.emptyList(), cause, null);
    }

    public EventResult<UpgradeResult> callPlotUpgradeEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot,
                                                             Upgrade upgrade, UpgradeLevel currentLevel,
                                                             UpgradeLevel nextLevel, PlotUpgradeEvent.Cause cause) {
        return callPlotUpgradeEvent(superiorPlayer, plot, upgrade, nextLevel, currentLevel.getCommands(), cause, currentLevel.getCost());
    }

    public EventResult<UpgradeResult> callPlotUpgradeEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot,
                                                             Upgrade upgrade, UpgradeLevel nextLevel,
                                                             List<String> commands, PlotUpgradeEvent.Cause cause,
                                                             @Nullable UpgradeCost upgradeCost) {
        return callEvent(() -> new PlotUpgradeEvent(superiorPlayer, plot, upgrade, nextLevel, commands, cause, upgradeCost),
                "plotupgradeevent", new UpgradeResult(commands, upgradeCost), UpgradeResult::new);
    }

    public boolean callPlotWorldResetEvent(CommandSender sender, Plot plot, World.Environment environment) {
        return callEvent(() -> new PlotWorldResetEvent(getSuperiorPlayer(sender), plot, environment),
                "plotworldresetevent");
    }

    public void callPlotWorthCalculatedEvent(Plot plot, SuperiorPlayer asker, BigDecimal plotLevel, BigDecimal plotWorth) {
        if (!plugin.getSettings().getDisabledEvents().contains("plotworthcalculatedevent")) {
            callEvent(new PlotWorthCalculatedEvent(plot, asker, plotLevel, plotWorth));
        }
    }

    public void callPlotWorthUpdateEvent(Plot plot, BigDecimal oldWorth, BigDecimal oldLevel, BigDecimal newWorth, BigDecimal newLevel) {
        if (!plugin.getSettings().getDisabledEvents().contains("plotworthupdateevent")) {
            callEvent(new PlotWorthUpdateEvent(plot, oldWorth, oldLevel, newWorth, newLevel));
        }
    }

    public EventResult<MissionRewards> callMissionCompleteEvent(SuperiorPlayer superiorPlayer, IMissionsHolder missionsHolder,
                                                                Mission<?> mission, List<ItemStack> itemRewards,
                                                                List<String> commandRewards) {
        return callEvent(() -> new MissionCompleteEvent(superiorPlayer, missionsHolder, mission, itemRewards, commandRewards),
                "missioncompleteevent", new MissionRewards(itemRewards, commandRewards), MissionRewards::of);
    }

    public boolean callMissionResetEvent(CommandSender commandSender, IMissionsHolder missionsHolder, Mission<?> mission) {
        return callMissionResetEvent(getSuperiorPlayer(commandSender), missionsHolder, mission);
    }

    public boolean callMissionResetEvent(@Nullable SuperiorPlayer superiorPlayer, IMissionsHolder missionsHolder, Mission<?> mission) {
        return callEvent(() -> new MissionResetEvent(superiorPlayer, missionsHolder, mission), "missionresetevent");
    }

    public boolean callPlayerChangeBorderColorEvent(SuperiorPlayer superiorPlayer, BorderColor borderColor) {
        return callEvent(() -> new PlayerChangeBorderColorEvent(superiorPlayer, borderColor),
                "playerchangebordercolorevent");
    }

    public boolean callPlayerChangeLanguageEvent(SuperiorPlayer superiorPlayer, Locale language) {
        return callEvent(() -> new PlayerChangeLanguageEvent(superiorPlayer, language), "playerchangelanguageevent");
    }

    public void callPlayerChangeNameEvent(SuperiorPlayer superiorPlayer, String newName) {
        if (!plugin.getSettings().getDisabledEvents().contains("playerchangenameevent")) {
            callEvent(new PlayerChangeNameEvent(superiorPlayer, newName));
        }
    }

    public boolean callPlayerChangeRoleEvent(SuperiorPlayer superiorPlayer, PlayerRole newPlayer) {
        return callEvent(() -> new PlayerChangeRoleEvent(superiorPlayer, newPlayer), "playerchangeroleevent");
    }

    public EventResult<MenuView<?, ?>> callPlayerCloseMenuEvent(SuperiorPlayer superiorPlayer, MenuView<?, ?> menuView,
                                                                @Nullable MenuView<?, ?> newMenuView) {
        return callEvent(() -> new PlayerCloseMenuEvent(superiorPlayer, menuView, newMenuView),
                "playerclosemenuevent", newMenuView, PlayerCloseMenuEvent::getNewMenuView);
    }

    public boolean callPlayerOpenMenuEvent(SuperiorPlayer superiorPlayer, MenuView<?, ?> menuView) {
        return callEvent(() -> new PlayerOpenMenuEvent(superiorPlayer, menuView), "playeropenmenuevent");
    }

    public void callPlayerReplaceEvent(SuperiorPlayer oldPlayer, SuperiorPlayer newPlayer) {
        if (!plugin.getSettings().getDisabledEvents().contains("playerreplaceevent")) {
            callEvent(new PlayerReplaceEvent(oldPlayer, newPlayer));
        }
    }

    public boolean callPlayerToggleBlocksStackerEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleBlocksStackerEvent(superiorPlayer), "playertoggleblocksstackerevent");
    }

    public boolean callPlayerToggleBorderEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleBorderEvent(superiorPlayer), "playertoggleborderevent");
    }

    public boolean callPlayerToggleBypassEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleBypassEvent(superiorPlayer), "playertogglebypassevent");
    }

    public boolean callPlayerToggleFlyEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleFlyEvent(superiorPlayer), "playertoggleflyevent");
    }

    public boolean callPlayerTogglePanelEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerTogglePanelEvent(superiorPlayer), "playertogglepanelevent");
    }

    public boolean callPlayerToggleSpyEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleSpyEvent(superiorPlayer), "playertogglespyevent");
    }

    public boolean callPlayerToggleTeamChatEvent(SuperiorPlayer superiorPlayer) {
        return callEvent(() -> new PlayerToggleTeamChatEvent(superiorPlayer), "playertoggleteamchatevent");
    }

    public void callPluginInitializedEvent(SuperiorSkyblock plugin) {
        callEvent(new PluginInitializedEvent(plugin));
    }

    public PluginInitializeResult callPluginInitializeEvent(SuperiorSkyblock plugin) {
        return new PluginInitializeResult(callEvent(new PluginInitializeEvent(plugin)));
    }

    public boolean callPluginLoadDataEvent(SuperiorSkyblock plugin) {
        return !callEvent(new PluginLoadDataEvent(plugin)).isCancelled();
    }

    public boolean callPrePlotCreateEvent(SuperiorPlayer superiorPlayer, String plotName) {
        return callEvent(() -> new PrePlotCreateEvent(superiorPlayer, plotName), "preplotcreateevent");
    }

    public EventResult<IMessageComponent> callSendMessageEvent(CommandSender receiver, String messageType, IMessageComponent messageComponent, Object... args) {
        return callEvent(() -> new SendMessageEvent(receiver, messageType, messageComponent, args),
                "sendmessageevent", messageComponent, SendMessageEvent::getMessageComponent);
    }

    private <T, E extends Event & Cancellable> EventResult<T> callEvent(Supplier<E> eventSupplier, String eventName,
                                                                        @Nullable T def, Function<E, T> getResultFunction) {
        if (plugin.getSettings().getDisabledEvents().contains(eventName))
            return EventResult.of(false, def);

        Log.debug(Debug.FIRE_EVENT, eventName);

        E event = eventSupplier.get();

        Bukkit.getPluginManager().callEvent(event);

        boolean cancelled = event.isCancelled();
        T result = getResultFunction.apply(event);

        Log.debugResult(Debug.FIRE_EVENT, "Cancelled:", cancelled);
        Log.debugResult(Debug.FIRE_EVENT, "Result:", result);

        return EventResult.of(cancelled, result);
    }

    private <E extends Event & Cancellable> boolean callEvent(Supplier<E> eventSupplier, String eventName) {
        if (plugin.getSettings().getDisabledEvents().contains(eventName))
            return true;

        Log.debug(Debug.FIRE_EVENT, eventName);

        E event = eventSupplier.get();

        Bukkit.getPluginManager().callEvent(event);

        Log.debugResult(Debug.FIRE_EVENT, "Cancelled:", event.isCancelled());

        return !event.isCancelled();
    }

    private static <T extends Event> T callEvent(T event) {
        Log.debug(Debug.FIRE_EVENT, event.getEventName());
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    @Nullable
    private SuperiorPlayer getSuperiorPlayer(CommandSender commandSender) {
        return commandSender instanceof Player ? plugin.getPlayers().getSuperiorPlayer(commandSender) : null;
    }

    public static class MissionRewards {

        private static final MissionRewards EMPTY = new MissionRewards(Collections.emptyList(), Collections.emptyList());

        private final List<ItemStack> itemRewards;
        private final List<String> commandRewards;

        private static MissionRewards of(MissionCompleteEvent missionCompleteEvent) {
            return missionCompleteEvent.getItemRewards().isEmpty() && missionCompleteEvent.getCommandRewards().isEmpty()
                    ? EMPTY : new MissionRewards(missionCompleteEvent.getItemRewards(), missionCompleteEvent.getCommandRewards());
        }

        private MissionRewards(List<ItemStack> itemRewards, List<String> commandRewards) {
            this.itemRewards = itemRewards;
            this.commandRewards = commandRewards;
        }

        public List<ItemStack> getItemRewards() {
            return itemRewards;
        }

        public List<String> getCommandRewards() {
            return commandRewards;
        }

    }

    public static class UpgradeResult {

        private final List<String> commands;
        private final UpgradeCost upgradeCost;

        public UpgradeResult(PlotUpgradeEvent plotUpgradeEvent) {
            this(plotUpgradeEvent.getCommands(), plotUpgradeEvent.getUpgradeCost());
        }

        public UpgradeResult(List<String> commands, UpgradeCost upgradeCost) {
            this.commands = commands;
            this.upgradeCost = upgradeCost;
        }

        public List<String> getCommands() {
            return commands;
        }

        public UpgradeCost getUpgradeCost() {
            return upgradeCost;
        }

    }

    public static class GenerateBlockResult {

        private final Key block;
        private final boolean placeBlock;

        public GenerateBlockResult(PlotGenerateBlockEvent event) {
            this(event.getBlock(), event.isPlaceBlock());
        }

        public GenerateBlockResult(Key block, boolean placeBlock) {
            this.block = block;
            this.placeBlock = placeBlock;
        }

        public Key getBlock() {
            return block;
        }

        public boolean isPlaceBlock() {
            return placeBlock;
        }

    }

    public static class PluginInitializeResult {

        @Nullable
        private final PlotsContainer plotsContainer;
        @Nullable
        private final PlayersContainer playersContainer;

        public PluginInitializeResult(PluginInitializeEvent event) {
            this(event.getPlotsContainer(), event.getPlayersContainer());
        }

        public PluginInitializeResult(@Nullable PlotsContainer plotsContainer, @Nullable PlayersContainer playersContainer) {
            this.plotsContainer = plotsContainer;
            this.playersContainer = playersContainer;
        }

        @Nullable
        public PlotsContainer getPlotsContainer() {
            return plotsContainer;
        }

        @Nullable
        public PlayersContainer getPlayersContainer() {
            return playersContainer;
        }

    }

    public static class PortalEventResult {

        private final World.Environment destination;
        @Nullable
        private final Schematic schematic;
        private final boolean isIgnoreInvalidSchematic;

        public PortalEventResult(PlotEnterPortalEvent event) {
            this(event.getDestination(), event.getSchematic(), event.isIgnoreInvalidSchematic());
        }

        public PortalEventResult(World.Environment destination, @Nullable Schematic schematic, boolean isIgnoreInvalidSchematic) {
            this.destination = destination;
            this.schematic = schematic;
            this.isIgnoreInvalidSchematic = isIgnoreInvalidSchematic;
        }

        public World.Environment getDestination() {
            return destination;
        }

        @Nullable
        public Schematic getSchematic() {
            return schematic;
        }

        public boolean isIgnoreInvalidSchematic() {
            return isIgnoreInvalidSchematic;
        }

    }

}

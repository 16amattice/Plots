package com.bgsoftware.superiorskyblock.service.region;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.events.PlotEnterEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotLeaveEvent;
import com.bgsoftware.superiorskyblock.api.events.PlotRestrictMoveEvent;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPreview;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.player.PlayerStatus;
import com.bgsoftware.superiorskyblock.api.service.region.InteractionResult;
import com.bgsoftware.superiorskyblock.api.service.region.MoveResult;
import com.bgsoftware.superiorskyblock.api.service.region.RegionManagerService;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.EnumHelper;
import com.bgsoftware.superiorskyblock.core.Materials;
import com.bgsoftware.superiorskyblock.core.ServerVersion;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.flag.PlotFlags;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.service.IService;
import com.bgsoftware.superiorskyblock.world.BukkitEntities;
import com.bgsoftware.superiorskyblock.world.BukkitItems;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;

public class RegionManagerServiceImpl implements RegionManagerService, IService {

    private static final Material FARMLAND = EnumHelper.getEnum(Material.class, "FARMLAND", "SOIL");
    @Nullable
    private static final Material TURTLE_EGG = EnumHelper.getEnum(Material.class, "TURTLE_EGG");
    @Nullable
    private static final Material SWEET_BERRY_BUSH = EnumHelper.getEnum(Material.class, "SWEET_BERRY_BUSH");
    @Nullable
    private static final Material LECTERN = EnumHelper.getEnum(Material.class, "LECTERN");
    @Nullable
    private static final EntityType AXOLOTL_TYPE = getSafeEntityType("AXOLOTL");
    private static final int MAX_PICKUP_DISTANCE = 1;

    private final SuperiorSkyblockPlugin plugin;

    public RegionManagerServiceImpl(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<?> getAPIClass() {
        return RegionManagerService.class;
    }

    @Override
    public InteractionResult handleBlockPlace(SuperiorPlayer superiorPlayer, Block block) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(block, "block cannot be null");

        Location blockLocation = block.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(blockLocation);

        return handleInteractionInternal(superiorPlayer, blockLocation, plot, PlotPrivileges.BUILD,
                0, true, true);
    }

    @Override
    public InteractionResult handleBlockBreak(SuperiorPlayer superiorPlayer, Block block) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(block, "block cannot be null");

        Location blockLocation = block.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(blockLocation);

        Material blockType = block.getType();
        PlotPrivilege plotPrivilege = blockType == Materials.SPAWNER.toBukkitType() ?
                PlotPrivileges.SPAWNER_BREAK : PlotPrivileges.BREAK;

        InteractionResult interactionResult = handleInteractionInternal(superiorPlayer, blockLocation, plot, plotPrivilege,
                0, true, true);

        if (interactionResult != InteractionResult.SUCCESS)
            return interactionResult;

        if (plot == null)
            return InteractionResult.SUCCESS;

        if (plugin.getSettings().getValuableBlocks().contains(Keys.of(block)))
            return handleInteractionInternal(superiorPlayer, blockLocation, plot, PlotPrivileges.VALUABLE_BREAK,
                    0, false, false);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult handleBlockInteract(SuperiorPlayer superiorPlayer, Block block, Action action,
                                                 @Nullable ItemStack usedItem) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(block, "block cannot be null");

        Location blockLocation = block.getLocation();
        Material blockType = block.getType();

        int stackedBlockAmount = plugin.getStackedBlocks().getStackedBlockAmount(blockLocation);
        if (stackedBlockAmount <= 1 && !plugin.getSettings().getInteractables().contains(blockType.name()))
            return InteractionResult.SUCCESS;

        Plot plot = plugin.getGrid().getPlotAt(blockLocation);

        InteractionResult interactionResult = handleInteractionInternal(superiorPlayer, blockLocation, plot,
                null, 0, true, false);

        if (interactionResult != InteractionResult.SUCCESS)
            return interactionResult;

        if (plot == null)
            return InteractionResult.SUCCESS;

        BlockState blockState = block.getState();
        EntityType spawnType = usedItem == null ? EntityType.UNKNOWN : BukkitItems.getEntityType(usedItem);

        PlotPrivilege plotPrivilege;

        if (spawnType != EntityType.UNKNOWN) {
            plotPrivilege = BukkitEntities.getCategory(spawnType).getSpawnPrivilege();
        } else if (usedItem != null && Materials.isMinecart(usedItem.getType()) ? Materials.isRail(blockType) : Materials.isBoat(blockType)) {
            plotPrivilege = PlotPrivileges.MINECART_PLACE;
        } else if (Materials.isChest(blockType)) {
            plotPrivilege = PlotPrivileges.CHEST_ACCESS;
        } else if (blockState instanceof InventoryHolder) {
            plotPrivilege = PlotPrivileges.USE;
        } else if (blockState instanceof Sign) {
            plotPrivilege = PlotPrivileges.SIGN_INTERACT;
        } else if (blockType == Materials.SPAWNER.toBukkitType()) {
            plotPrivilege = PlotPrivileges.SPAWNER_BREAK;
        } else if (blockType == FARMLAND) {
            plotPrivilege = action == Action.PHYSICAL ? PlotPrivileges.FARM_TRAMPING : PlotPrivileges.BUILD;
        } else if (blockType == TURTLE_EGG) {
            plotPrivilege = action == Action.PHYSICAL ? PlotPrivileges.TURTLE_EGG_TRAMPING : PlotPrivileges.BUILD;
        } else if (blockType == SWEET_BERRY_BUSH && action == Action.RIGHT_CLICK_BLOCK) {
            plotPrivilege = Materials.BONE_MEAL.toBukkitItem().isSimilar(usedItem) ? PlotPrivileges.FERTILIZE : PlotPrivileges.FARM_TRAMPING;
        } else if (stackedBlockAmount > 1) {
            plotPrivilege = PlotPrivileges.BREAK;
        } else if (blockType == Material.PUMPKIN) {
            plotPrivilege = PlotPrivileges.BREAK;
        } else if (blockType == LECTERN) {
            plotPrivilege = PlotPrivileges.PICKUP_LECTERN_BOOK;
        } else {
            plotPrivilege = PlotPrivileges.INTERACT;
        }

        return handleInteractionInternal(superiorPlayer, blockLocation, plot, plotPrivilege,
                0, false, false);
    }

    @Override
    public InteractionResult handleBlockFertilize(SuperiorPlayer superiorPlayer, Block block) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(block, "block cannot be null");

        Location blockLocation = block.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(blockLocation);

        return handleInteractionInternal(superiorPlayer, blockLocation, plot, PlotPrivileges.FERTILIZE,
                0, true, true);
    }

    @Override
    public InteractionResult handleEntityInteract(SuperiorPlayer superiorPlayer, Entity entity, @Nullable ItemStack usedItem) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(entity, "entity cannot be null");

        Location entityLocation = entity.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);

        InteractionResult interactionResult = handleInteractionInternal(superiorPlayer, entityLocation, plot,
                null, 0, true, false);

        if (interactionResult != InteractionResult.SUCCESS)
            return interactionResult;

        if (plot == null)
            return InteractionResult.SUCCESS;

        boolean closeInventory = false;

        PlotPrivilege plotPrivilege;

        if (entity instanceof ArmorStand) {
            plotPrivilege = PlotPrivileges.INTERACT;
        } else if (usedItem != null && entity instanceof Animals &&
                plugin.getNMSEntities().isAnimalFood(usedItem, (Animals) entity)) {
            plotPrivilege = PlotPrivileges.ANIMAL_BREED;
        } else if (usedItem != null && usedItem.getType() == Material.NAME_TAG) {
            plotPrivilege = PlotPrivileges.NAME_ENTITY;
        } else if (entity instanceof Villager) {
            plotPrivilege = PlotPrivileges.VILLAGER_TRADING;
            closeInventory = true;
        } else if (entity instanceof Horse || (ServerVersion.isAtLeast(ServerVersion.v1_11) && (
                entity instanceof Mule || entity instanceof Donkey))) {
            plotPrivilege = PlotPrivileges.HORSE_INTERACT;
            closeInventory = true;
        } else if (usedItem != null && entity instanceof Creeper &&
                usedItem.getType() == Material.FLINT_AND_STEEL) {
            plotPrivilege = PlotPrivileges.IGNITE_CREEPER;
        } else if (usedItem != null && ServerVersion.isAtLeast(ServerVersion.v1_17) &&
                usedItem.getType() == Material.WATER_BUCKET && entity.getType() == AXOLOTL_TYPE) {
            plotPrivilege = PlotPrivileges.PICKUP_AXOLOTL;
        } else if (entity instanceof ItemFrame) {
            plotPrivilege = PlotPrivileges.ITEM_FRAME;
        } else if (entity instanceof Painting) {
            plotPrivilege = PlotPrivileges.PAINTING;
        } else if (entity instanceof Fish && !ServerVersion.isLegacy()) {
            plotPrivilege = PlotPrivileges.PICKUP_FISH;
        } else {
            return InteractionResult.SUCCESS;
        }

        interactionResult = handleInteractionInternal(superiorPlayer, entityLocation, plot, plotPrivilege,
                0, false, false);

        if (closeInventory && interactionResult != InteractionResult.SUCCESS) {
            BukkitExecutor.sync(() -> {
                Player player = superiorPlayer.asPlayer();
                if (player != null && player.isOnline()) {
                    Inventory openInventory = player.getOpenInventory().getTopInventory();
                    if (openInventory != null && (openInventory.getType() == InventoryType.MERCHANT ||
                            openInventory.getType() == InventoryType.CHEST))
                        player.closeInventory();
                }
            }, 1L);
        }

        return interactionResult;
    }

    @Override
    public InteractionResult handleEntityDamage(Entity damager, Entity entity) {
        Preconditions.checkNotNull(damager, "damager cannot be null");
        Preconditions.checkNotNull(entity, "entity cannot be null");

        Optional<SuperiorPlayer> damagerSource = BukkitEntities.getPlayerSource(damager).map(plugin.getPlayers()::getSuperiorPlayer);

        if (!damagerSource.isPresent())
            return InteractionResult.SUCCESS;

        Location entityLocation = entity.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);
        PlotPrivilege plotPrivilege = BukkitEntities.getCategory(entity.getType()).getDamagePrivilege();

        InteractionResult interactionResult = handleInteractionInternal(damagerSource.get(), entityLocation, plot, plotPrivilege,
                0, true, false);

        if (interactionResult != InteractionResult.SUCCESS && damager instanceof Arrow && entity.getFireTicks() > 0)
            entity.setFireTicks(0);

        return interactionResult;
    }

    @Override
    public InteractionResult handleEntityShear(SuperiorPlayer superiorPlayer, Entity entity) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(entity, "entity cannot be null");

        Location entityLocation = entity.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);

        return handleInteractionInternal(superiorPlayer, entityLocation, plot, PlotPrivileges.ANIMAL_SHEAR,
                0, true, false);
    }

    @Override
    public InteractionResult handleEntityLeash(SuperiorPlayer superiorPlayer, Entity entity) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(entity, "entity cannot be null");

        Location entityLocation = entity.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(entityLocation);

        return handleInteractionInternal(superiorPlayer, entityLocation, plot, PlotPrivileges.LEASH,
                0, true, false);
    }

    @Override
    public InteractionResult handlePlayerPickupItem(SuperiorPlayer superiorPlayer, Item item) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(item, "item cannot be null");

        if (plugin.getNMSPlayers().wasThrownByPlayer(item, superiorPlayer))
            return InteractionResult.SUCCESS;

        Location itemLocation = item.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(itemLocation);

        return handleInteractionInternal(superiorPlayer, itemLocation, plot, PlotPrivileges.PICKUP_DROPS,
                MAX_PICKUP_DISTANCE, true, false);
    }

    @Override
    public InteractionResult handlePlayerDropItem(SuperiorPlayer superiorPlayer, Item item) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(item, "item cannot be null");

        Location itemLocation = item.getLocation();
        Plot plot = plugin.getGrid().getPlotAt(itemLocation);

        return handleInteractionInternal(superiorPlayer, itemLocation, plot, PlotPrivileges.DROP_ITEMS,
                0, true, false);
    }

    @Override
    public InteractionResult handlePlayerEnderPearl(SuperiorPlayer superiorPlayer, Location destination) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(destination, "destination cannot be null");
        Preconditions.checkArgument(destination.getWorld() != null, "destination's world cannot be null");

        Plot plot = plugin.getGrid().getPlotAt(destination);

        InteractionResult interactionResult = handleInteractionInternal(superiorPlayer, destination, plot,
                null, 0, true, false);

        if (interactionResult != null)
            return interactionResult;

        if (plot == null)
            return InteractionResult.SUCCESS;

        return handleInteractionInternal(superiorPlayer, destination, plot, PlotPrivileges.ENDER_PEARL,
                0, false, false);
    }

    @Override
    public InteractionResult handleCustomInteraction(SuperiorPlayer superiorPlayer, Location location, PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(location, "location cannot be null");
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege cannot be null");

        Plot plot = plugin.getGrid().getPlotAt(location);
        return handleInteractionInternal(superiorPlayer, location, plot, plotPrivilege,
                0, true, false);
    }

    private InteractionResult handleInteractionInternal(SuperiorPlayer superiorPlayer, Location location,
                                                        @Nullable Plot plot, @Nullable PlotPrivilege plotPrivilege,
                                                        int extraRadius, boolean checkPlotBoundaries, boolean checkRecalculation) {
        if (superiorPlayer.hasBypassModeEnabled())
            return InteractionResult.SUCCESS;

        if (checkPlotBoundaries) {
            if (plot == null && plugin.getGrid().isPlotsWorld(superiorPlayer.getWorld()))
                return InteractionResult.OUTSIDE_PLOT;

            if (plot != null && !plot.isInsideRange(location, extraRadius))
                return InteractionResult.OUTSIDE_PLOT;
        }

        if (plot != null) {
            if (plotPrivilege != null && !plot.hasPermission(superiorPlayer, plotPrivilege))
                return InteractionResult.MISSING_PRIVILEGE;

            if (checkRecalculation && plot.isBeingRecalculated())
                return InteractionResult.PLOT_RECALCULATE;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public MoveResult handlePlayerMove(SuperiorPlayer superiorPlayer, Location from, Location to) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(from, "from cannot be null");
        Preconditions.checkNotNull(to, "to cannot be null");

        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            // Handle moving while in teleport warmup.
            BukkitTask teleportTask = superiorPlayer.getTeleportTask();
            if (teleportTask != null) {
                teleportTask.cancel();
                superiorPlayer.setTeleportTask(null);
                Message.TELEPORT_WARMUP_CANCEL.send(superiorPlayer);
            }

            //Checking for out of distance from preview location.
            PlotPreview plotPreview = plugin.getGrid().getPlotPreview(superiorPlayer);
            if (plotPreview != null && (!plotPreview.getLocation().getWorld().equals(to.getWorld()) ||
                    plotPreview.getLocation().distanceSquared(to) > 10000)) {
                plotPreview.handleEscape();
                return MoveResult.PLOT_PREVIEW_MOVED_TOO_FAR;
            }

            MoveResult moveResult;

            Plot toPlot = plugin.getGrid().getPlotAt(to);
            if (toPlot != null) {
                moveResult = handlePlayerEnterPlotInternal(superiorPlayer, toPlot, from, to, PlotEnterEvent.EnterCause.PLAYER_MOVE);
                if (moveResult != MoveResult.SUCCESS)
                    return moveResult;
            }

            Plot fromPlot = plugin.getGrid().getPlotAt(from);
            if (fromPlot != null) {
                moveResult = handlePlayerLeavePlotInternal(superiorPlayer, fromPlot, from, to, PlotLeaveEvent.LeaveCause.PLAYER_MOVE);
                if (moveResult != MoveResult.SUCCESS)
                    return moveResult;
            }
        }

        if (from.getBlockY() != to.getBlockY() && to.getBlockY() <= plugin.getNMSWorld().getMinHeight(to.getWorld()) - 5) {
            Plot plot = plugin.getGrid().getPlotAt(from);

            if (plot == null || (plot.isVisitor(superiorPlayer, false) ?
                    !plugin.getSettings().getVoidTeleport().isVisitors() : !plugin.getSettings().getVoidTeleport().isMembers()))
                return MoveResult.SUCCESS;

            Log.debug(Debug.VOID_TELEPORT, superiorPlayer.getName());

            superiorPlayer.setPlayerStatus(PlayerStatus.VOID_TELEPORT);

            superiorPlayer.teleport(plot, result -> {
                if (!result) {
                    Message.TELEPORTED_FAILED.send(superiorPlayer);
                    superiorPlayer.teleport(plugin.getGrid().getSpawnPlot());
                }

                if (superiorPlayer.getPlayerStatus() == PlayerStatus.VOID_TELEPORT)
                    superiorPlayer.setPlayerStatus(PlayerStatus.NONE);
            });

            return MoveResult.VOID_TELEPORT;
        }

        return MoveResult.SUCCESS;
    }

    @Override
    public MoveResult handlePlayerTeleport(SuperiorPlayer superiorPlayer, Location from, Location to) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(from, "from cannot be null");
        Preconditions.checkArgument(from.getWorld() != null, "from world cannot be null");
        Preconditions.checkNotNull(to, "to cannot be null");
        Preconditions.checkArgument(to.getWorld() != null, "from world cannot be null");

        Plot toPlot = plugin.getGrid().getPlotAt(to);
        if (toPlot != null) {
            return handlePlayerEnterPlotInternal(superiorPlayer, toPlot, from, to, PlotEnterEvent.EnterCause.PLAYER_TELEPORT);
        }

        Plot fromPlot = plugin.getGrid().getPlotAt(from);
        if (fromPlot != null) {
            return handlePlayerLeavePlotInternal(superiorPlayer, fromPlot, from, to, PlotLeaveEvent.LeaveCause.PLAYER_TELEPORT);
        }

        return MoveResult.SUCCESS;
    }

    @Override
    public MoveResult handlePlayerTeleportByPortal(SuperiorPlayer superiorPlayer, Location portalLocation, Location teleportLocation) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(portalLocation, "portalLocation cannot be null");
        Preconditions.checkNotNull(portalLocation.getWorld(), "portalLocation world cannot be null");
        Preconditions.checkNotNull(teleportLocation, "teleportLocation cannot be null");
        Preconditions.checkNotNull(teleportLocation.getWorld(), "teleportLocation world cannot be null");

        Plot plot = plugin.getGrid().getPlotAt(teleportLocation);
        if (plot != null) {
            return handlePlayerEnterPlotInternal(superiorPlayer, plot, portalLocation, teleportLocation,
                    PlotEnterEvent.EnterCause.PORTAL);
        }

        return MoveResult.SUCCESS;
    }

    @Override
    public MoveResult handlePlayerJoin(SuperiorPlayer superiorPlayer, Location location) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");
        Preconditions.checkNotNull(location, "location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "location's world cannot be null");

        Plot plot = plugin.getGrid().getPlotAt(location);

        return plot == null ? MoveResult.SUCCESS : handlePlayerEnterPlotInternal(superiorPlayer, plot, null,
                location, PlotEnterEvent.EnterCause.PLAYER_JOIN);
    }

    @Override
    public MoveResult handlePlayerQuit(SuperiorPlayer superiorPlayer, Location location) {
        Preconditions.checkNotNull(superiorPlayer, "superiorPlayer cannot be null");

        Plot plot = plugin.getGrid().getPlotAt(location);
        if (plot == null)
            return MoveResult.SUCCESS;

        plot.setPlayerInside(superiorPlayer, false);
        return handlePlayerLeavePlotInternal(superiorPlayer, plot, location, null, PlotLeaveEvent.LeaveCause.PLAYER_QUIT);
    }

    private MoveResult handlePlayerEnterPlotInternal(SuperiorPlayer superiorPlayer, Plot toPlot,
                                                       @Nullable Location from, Location to,
                                                       PlotEnterEvent.EnterCause enterCause) {
        // This can happen after the leave event is cancelled.
        if (superiorPlayer.getPlayerStatus() == PlayerStatus.LEAVING_PLOT) {
            superiorPlayer.setPlayerStatus(PlayerStatus.NONE);
            return MoveResult.SUCCESS;
        }

        // Checking if the player is banned from the plot.
        if (toPlot.isBanned(superiorPlayer) && !superiorPlayer.hasBypassModeEnabled() &&
                !superiorPlayer.hasPermissionWithoutOP("superior.admin.ban.bypass")) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.BANNED_FROM_PLOT);
            Message.BANNED_FROM_PLOT.send(superiorPlayer);
            return MoveResult.BANNED_FROM_PLOT;
        }

        // Checking if the player is locked to visitors.
        if (toPlot.isLocked() && !toPlot.hasPermission(superiorPlayer, PlotPrivileges.CLOSE_BYPASS)) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.LOCKED_PLOT);
            Message.NO_CLOSE_BYPASS.send(superiorPlayer);
            return MoveResult.PLOT_LOCKED;
        }

        Plot fromPlot = from == null ? null : plugin.getGrid().getPlotAt(from);

        boolean equalPlots = toPlot.equals(fromPlot);
        boolean toInsideRange = toPlot.isInsideRange(to);
        boolean fromInsideRange = from != null && fromPlot != null && fromPlot.isInsideRange(from);
        boolean equalWorlds = from != null && to.getWorld().equals(from.getWorld());

        if (toInsideRange && (!equalPlots || !fromInsideRange) &&
                !plugin.getEventsBus().callPlotEnterProtectedEvent(superiorPlayer, toPlot, enterCause)) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.ENTER_PROTECTED_EVENT_CANCELLED);
            return MoveResult.ENTER_EVENT_CANCELLED;
        }

        if (equalPlots) {
            if (!equalWorlds) {
                BukkitExecutor.sync(() -> plugin.getNMSWorld().setWorldBorder(superiorPlayer, toPlot), 1L);
                superiorPlayer.setPlayerStatus(PlayerStatus.PORTALS_IMMUNED);
                BukkitExecutor.sync(() -> {
                    if (superiorPlayer.getPlayerStatus() == PlayerStatus.PORTALS_IMMUNED)
                        superiorPlayer.setPlayerStatus(PlayerStatus.NONE);
                }, 100L);
            }

            return MoveResult.SUCCESS;
        }

        if (!plugin.getEventsBus().callPlotEnterEvent(superiorPlayer, toPlot, enterCause)) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.ENTER_EVENT_CANCELLED);
            return MoveResult.ENTER_EVENT_CANCELLED;
        }

        toPlot.setPlayerInside(superiorPlayer, true);

        if (!toPlot.isMember(superiorPlayer) && toPlot.hasSettingsEnabled(PlotFlags.PVP)) {
            Message.ENTER_PVP_PLOT.send(superiorPlayer);
            if (plugin.getSettings().isImmuneToPvPWhenTeleport()) {
                superiorPlayer.setPlayerStatus(PlayerStatus.PVP_IMMUNED);
                BukkitExecutor.sync(() -> {
                    if (superiorPlayer.getPlayerStatus() == PlayerStatus.PVP_IMMUNED)
                        superiorPlayer.setPlayerStatus(PlayerStatus.NONE);
                }, 200L);
            }
        }

        superiorPlayer.setPlayerStatus(PlayerStatus.PORTALS_IMMUNED);
        BukkitExecutor.sync(() -> {
            if (superiorPlayer.getPlayerStatus() == PlayerStatus.PORTALS_IMMUNED)
                superiorPlayer.setPlayerStatus(PlayerStatus.NONE);
        }, 100L);

        Player player = superiorPlayer.asPlayer();
        if (player != null && (plugin.getSettings().getSpawn().isProtected() || !toPlot.isSpawn())) {
            BukkitExecutor.sync(() -> {
                // Update player time and player weather with a delay.
                // Fixes https://github.com/BG-Software-LLC/SuperiorSkyblock2/issues/1260
                if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_DAY)) {
                    player.setPlayerTime(0, false);
                } else if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_MIDDLE_DAY)) {
                    player.setPlayerTime(6000, false);
                } else if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_NIGHT)) {
                    player.setPlayerTime(14000, false);
                } else if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_MIDDLE_NIGHT)) {
                    player.setPlayerTime(18000, false);
                }

                if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_SHINY)) {
                    player.setPlayerWeather(WeatherType.CLEAR);
                } else if (toPlot.hasSettingsEnabled(PlotFlags.ALWAYS_RAIN)) {
                    player.setPlayerWeather(WeatherType.DOWNFALL);
                }
            }, 1L);
        }

        if (superiorPlayer.hasPlotFlyEnabled() && !superiorPlayer.hasFlyGamemode()) {
            BukkitExecutor.sync(() -> {
                if (player != null)
                    toPlot.updatePlotFly(superiorPlayer);
            }, 5L);
        }

        BukkitExecutor.sync(() -> {
            toPlot.applyEffects(superiorPlayer);
            plugin.getNMSWorld().setWorldBorder(superiorPlayer, toPlot);
        }, 1L);

        return MoveResult.SUCCESS;
    }

    private MoveResult handlePlayerLeavePlotInternal(SuperiorPlayer superiorPlayer, Plot fromPlot,
                                                       Location from, @Nullable Location to,
                                                       PlotLeaveEvent.LeaveCause leaveCause) {
        Plot toPlot = to == null ? null : plugin.getGrid().getPlotAt(to);

        boolean equalWorlds = to != null && from.getWorld().equals(to.getWorld());
        boolean equalPlots = fromPlot.equals(toPlot);
        boolean fromInsideRange = fromPlot.isInsideRange(from);
        boolean toInsideRange = to != null && toPlot != null && toPlot.isInsideRange(to);

        //Checking for the stop leaving feature.
        if (plugin.getSettings().isStopLeaving() && fromInsideRange && !toInsideRange &&
                !superiorPlayer.hasBypassModeEnabled() && !fromPlot.isSpawn() && equalWorlds) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.LEAVE_PLOT_TO_OUTSIDE);
            superiorPlayer.setPlayerStatus(PlayerStatus.LEAVING_PLOT);
            return MoveResult.LEAVE_PLOT_TO_OUTSIDE;
        }

        // Handling the leave protected event
        if (fromInsideRange && (!equalPlots || !toInsideRange)) {
            if (!plugin.getEventsBus().callPlotLeaveProtectedEvent(superiorPlayer, fromPlot, leaveCause, to)) {
                plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.LEAVE_PROTECTED_EVENT_CANCELLED);
                return MoveResult.ENTER_EVENT_CANCELLED;
            }
        }

        if (equalPlots)
            return MoveResult.SUCCESS;

        if (!plugin.getEventsBus().callPlotLeaveEvent(superiorPlayer, fromPlot, leaveCause, to)) {
            plugin.getEventsBus().callPlotRestrictMoveEvent(superiorPlayer, PlotRestrictMoveEvent.RestrictReason.LEAVE_EVENT_CANCELLED);
            return MoveResult.ENTER_EVENT_CANCELLED;
        }

        fromPlot.setPlayerInside(superiorPlayer, false);

        Player player = superiorPlayer.asPlayer();
        if (player != null) {
            player.resetPlayerTime();
            player.resetPlayerWeather();
            fromPlot.removeEffects(superiorPlayer);

            if (superiorPlayer.hasPlotFlyEnabled() && (toPlot == null || toPlot.isSpawn()) && !superiorPlayer.hasFlyGamemode()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                Message.PLOT_FLY_DISABLED.send(player);
            }
        }

        if (toPlot == null)
            plugin.getNMSWorld().setWorldBorder(superiorPlayer, null);

        return MoveResult.SUCCESS;
    }

    @Nullable
    private static EntityType getSafeEntityType(String entityType) {
        try {
            return EntityType.valueOf(entityType);
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

}

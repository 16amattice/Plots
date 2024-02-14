package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.common.annotations.Size;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.data.IDatabaseBridgeHolder;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.enums.SyncStatus;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.plot.bank.PlotBank;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.missions.IMissionsHolder;
import com.bgsoftware.superiorskyblock.api.missions.Mission;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.persistence.IPersistentDataHolder;
import com.bgsoftware.superiorskyblock.api.service.message.IMessageComponent;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.upgrades.UpgradeLevel;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Plot extends Comparable<Plot>, IMissionsHolder, IPersistentDataHolder, IDatabaseBridgeHolder {

    /*
     *  General methods
     */

    /**
     * Get the owner of the plot.
     */
    SuperiorPlayer getOwner();

    /**
     * Get the unique-id of the plot.
     */
    UUID getUniqueId();

    /**
     * Get the creation time of the plot.
     */
    long getCreationTime();

    /**
     * Get the creation time of the plot, in a formatted string.
     */
    String getCreationTimeDate();

    /**
     * Re-sync the plot with a new dates formatter.
     */
    void updateDatesFormatter();

    /*
     *  Player related methods
     */

    /**
     * Get the list of members of the plot.
     *
     * @param includeOwner Whether the owner should be returned.
     */
    List<SuperiorPlayer> getPlotMembers(boolean includeOwner);

    /**
     * Get the list of members of the plot with specific roles.
     *
     * @param playerRoles The roles to filter with.
     */
    List<SuperiorPlayer> getPlotMembers(PlayerRole... playerRoles);

    /**
     * Get the list of all banned players.
     */
    List<SuperiorPlayer> getBannedPlayers();

    /**
     * Get the list of all visitors that are on the plot, including vanished ones.
     */
    List<SuperiorPlayer> getPlotVisitors();

    /**
     * Get the list of all visitors that are on the plot.
     *
     * @param vanishPlayers Should vanish players be included?
     */
    List<SuperiorPlayer> getPlotVisitors(boolean vanishPlayers);

    /**
     * Get the list of all the players that are on the plot.
     */
    List<SuperiorPlayer> getAllPlayersInside();

    /**
     * Get all the visitors that visited the plot until now.
     */
    List<SuperiorPlayer> getUniqueVisitors();

    /**
     * Get all the visitors that visited the plot until now, with the time they last visited.
     */
    List<Pair<SuperiorPlayer, Long>> getUniqueVisitorsWithTimes();

    /**
     * Invite a player to the plot.
     *
     * @param superiorPlayer The player to invite.
     */
    void inviteMember(SuperiorPlayer superiorPlayer);

    /**
     * Revoke an invitation of a player.
     *
     * @param superiorPlayer The player to revoke his invite.
     */
    void revokeInvite(SuperiorPlayer superiorPlayer);

    /**
     * Checks whether the player has been invited to the plot.
     */
    boolean isInvited(SuperiorPlayer superiorPlayer);

    /**
     * Get all the invited players of the plot.
     */
    List<SuperiorPlayer> getInvitedPlayers();

    /**
     * Add a player to the plot.
     *
     * @param superiorPlayer The player to add.
     * @param playerRole     The role to give to the player.
     */
    void addMember(SuperiorPlayer superiorPlayer, PlayerRole playerRole);

    /**
     * Kick a member from the plot.
     *
     * @param superiorPlayer The player to kick.
     */
    void kickMember(SuperiorPlayer superiorPlayer);

    /**
     * Check whether a player is a member of the plot.
     *
     * @param superiorPlayer The player to check.
     */
    boolean isMember(SuperiorPlayer superiorPlayer);

    /**
     * Ban a member from the plot.
     *
     * @param superiorPlayer The player to ban.
     */
    void banMember(SuperiorPlayer superiorPlayer);

    /**
     * Ban a member from the plot.
     *
     * @param superiorPlayer The player to ban.
     * @param whom           The player that executed the ban command.
     *                       If null, CONSOLE will be chosen as the banner.
     */
    void banMember(SuperiorPlayer superiorPlayer, @Nullable SuperiorPlayer whom);

    /**
     * Unban a player from the plot.
     *
     * @param superiorPlayer The player to unban.
     */
    void unbanMember(SuperiorPlayer superiorPlayer);

    /**
     * Checks whether a player is banned from the plot.
     *
     * @param superiorPlayer The player to check.
     */
    boolean isBanned(SuperiorPlayer superiorPlayer);

    /**
     * Add a player to the plot as a co-op member.
     *
     * @param superiorPlayer The player to add.
     */
    void addCoop(SuperiorPlayer superiorPlayer);

    /**
     * Remove a player from being a co-op member.
     *
     * @param superiorPlayer The player to remove.
     */
    void removeCoop(SuperiorPlayer superiorPlayer);

    /**
     * Check whether a player is a co-op member of the plot.
     *
     * @param superiorPlayer The player to check.
     */
    boolean isCoop(SuperiorPlayer superiorPlayer);

    /**
     * Get the list of all co-op players.
     */
    List<SuperiorPlayer> getCoopPlayers();

    /**
     * Get the coop players limit of the plot.
     */
    int getCoopLimit();

    /**
     * Get the coop players limit of the plot that was set using a command.
     */
    int getCoopLimitRaw();

    /**
     * Set the coop players limit of the plot.
     *
     * @param coopLimit The coop players limit to set.
     */
    void setCoopLimit(int coopLimit);

    /**
     * Update status of a player if he's inside the plot or not.
     *
     * @param superiorPlayer The player to add.
     */
    void setPlayerInside(SuperiorPlayer superiorPlayer, boolean inside);

    /**
     * Check whether a player is a visitor of the plot or not.
     *
     * @param superiorPlayer  The player to check.
     * @param checkCoopStatus Whether to check for coop status or not.
     *                        If enabled, coops will not be considered as visitors.
     */
    boolean isVisitor(SuperiorPlayer superiorPlayer, boolean checkCoopStatus);

    /*
     *  Location related methods
     */

    /**
     * Get the center location of the plot, depends on the world environment.
     *
     * @param environment The environment.
     */
    Location getCenter(World.Environment environment);

    /**
     * Get the center position of the plot.
     */
    BlockPosition getCenterPosition();

    /**
     * Get the members' teleport location of the plot, depends on the world environment.
     * Similar to {@link #getPlotHome(World.Environment)}
     *
     * @param environment The environment.
     */
    @Nullable
    Location getTeleportLocation(World.Environment environment);

    /**
     * Get all the teleport locations of the plot.
     * Similar to {@link #getPlotHomes()}
     */
    Map<World.Environment, Location> getTeleportLocations();

    /**
     * Set the members' teleport location of the plot.
     * Similar to {@link #setPlotHome(Location)}
     *
     * @param teleportLocation The new teleport location.
     */
    void setTeleportLocation(Location teleportLocation);

    /**
     * Set the members' teleport location of the plot.
     * Similar to {@link #setPlotHome(org.bukkit.World.Environment, Location)}
     *
     * @param environment      The environment to change teleport location for.
     * @param teleportLocation The new teleport location.
     */
    void setTeleportLocation(World.Environment environment, @Nullable Location teleportLocation);

    /**
     * Get the members' home location of the plot, depends on the world environment.
     *
     * @param environment The environment.
     */
    @Nullable
    Location getPlotHome(World.Environment environment);

    /**
     * Get all the home locations of the plot.
     */
    Map<World.Environment, Location> getPlotHomes();

    /**
     * Set the members' teleport location of the plot.
     *
     * @param homeLocation The new home location.
     */
    void setPlotHome(Location homeLocation);

    /**
     * Set the members' teleport location of the plot.
     *
     * @param environment  The environment to change teleport location for.
     * @param homeLocation The new home location.
     */
    void setPlotHome(World.Environment environment, @Nullable Location homeLocation);

    /**
     * Get the visitors' teleport location of the plot.
     *
     * @deprecated See {@link #getVisitorsLocation(World.Environment)}
     */
    @Nullable
    @Deprecated
    Location getVisitorsLocation();

    /**
     * Get the visitors' teleport location of the plot.
     *
     * @param environment The environment to get the visitors-location from.
     *                    Currently unused, it has no effect.
     */
    @Nullable
    Location getVisitorsLocation(World.Environment environment);

    /**
     * Set the visitors' teleport location of the plot.
     *
     * @param visitorsLocation The new visitors location.
     */
    void setVisitorsLocation(@Nullable Location visitorsLocation);

    /**
     * Get the minimum location of the plot.
     */
    Location getMinimum();

    /**
     * Get the minimum location of the plot.
     */
    BlockPosition getMinimumPosition();

    /**
     * Get the minimum protected location of the plot.
     */
    Location getMinimumProtected();

    /**
     * Get the minimum location of the plot.
     */
    BlockPosition getMinimumProtectedPosition();

    /**
     * Get the maximum location of the plot.
     */
    Location getMaximum();

    /**
     * Get the maximum location of the plot.
     */
    BlockPosition getMaximumPosition();

    /**
     * Get the minimum protected location of the plot.
     */
    Location getMaximumProtected();

    /**
     * Get the minimum protected location of the plot.
     */
    BlockPosition getMaximumProtectedPosition();

    /**
     * Get all the chunks of the plot from all the environments.
     * Similar to {@link #getAllChunks(int)} with 0 as flags parameter.
     */
    List<Chunk> getAllChunks();

    /**
     * Get all the chunks of the plot from all the environments.
     *
     * @param flags See {@link PlotChunkFlags}
     */
    List<Chunk> getAllChunks(@PlotChunkFlags int flags);

    /**
     * Get all the chunks of the plot.
     * Similar to {@link #getAllChunks(org.bukkit.World.Environment, int)} with 0 as flags parameter.
     *
     * @param environment The environment to get the chunks from.
     */
    List<Chunk> getAllChunks(World.Environment environment);

    /**
     * Get all the chunks of the plot.
     *
     * @param environment The environment to get the chunks from.
     * @param flags       See {@link PlotChunkFlags}
     */
    List<Chunk> getAllChunks(World.Environment environment, @PlotChunkFlags int flags);

    /**
     * Get all the chunks of the plot from all the environments.
     *
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @deprecated See {@link #getAllChunks(int)}
     */
    @Deprecated
    List<Chunk> getAllChunks(boolean onlyProtected);

    /**
     * Get all the chunks of the plot, including empty ones.
     *
     * @param environment   The environment to get the chunks from.
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @deprecated See {@link #getAllChunks(World.Environment, int)}
     */
    @Deprecated
    List<Chunk> getAllChunks(World.Environment environment, boolean onlyProtected);

    /**
     * Get all the chunks of the plot.
     *
     * @param environment   The environment to get the chunks from.
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @param noEmptyChunks Should empty chunks be loaded or not?
     * @deprecated See {@link #getAllChunks(World.Environment, int)}
     */
    @Deprecated
    List<Chunk> getAllChunks(World.Environment environment, boolean onlyProtected, boolean noEmptyChunks);

    /**
     * Get all the loaded chunks of the plot.
     * Similar to {@link #getLoadedChunks(int)} with 0 as flags parameter.
     */
    List<Chunk> getLoadedChunks();

    /**
     * Get all the loaded chunks of the plot.
     *
     * @param flags See {@link PlotChunkFlags}
     */
    List<Chunk> getLoadedChunks(@PlotChunkFlags int flags);

    /**
     * Get all the loaded chunks of the plot.
     * Similar to {@link #getLoadedChunks(World.Environment, int)} with 0 as flags parameter.
     *
     * @param environment The environment to get the chunks from.
     */
    List<Chunk> getLoadedChunks(World.Environment environment);

    /**
     * Get all the loaded chunks of the plot.
     *
     * @param environment The environment to get the chunks from.
     * @param flags       See {@link PlotChunkFlags}
     */
    List<Chunk> getLoadedChunks(World.Environment environment, @PlotChunkFlags int flags);

    /**
     * Get all the loaded chunks of the plot.
     *
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @param noEmptyChunks Should empty chunks be loaded or not?
     * @deprecated See {@link #getLoadedChunks(int)}
     */
    @Deprecated
    List<Chunk> getLoadedChunks(boolean onlyProtected, boolean noEmptyChunks);

    /**
     * Get all the loaded chunks of the plot.
     *
     * @param environment   The environment to get the chunks from.
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @param noEmptyChunks Should empty chunks be loaded or not?
     * @deprecated See {@link #getLoadedChunks(World.Environment, int)}
     */
    @Deprecated
    List<Chunk> getLoadedChunks(World.Environment environment, boolean onlyProtected, boolean noEmptyChunks);

    /**
     * Get all the chunks of the plot asynchronized, including empty chunks.
     * Similar to {@link #getAllChunksAsync(World.Environment, int, Consumer)}, with 0 as flags parameter.
     *
     * @param environment The environment to get the chunks from.
     */
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment);

    /**
     * Get all the chunks of the plot asynchronized, including empty chunks.
     *
     * @param environment The environment to get the chunks from.
     * @param flags       See {@link PlotChunkFlags}
     */
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, @PlotChunkFlags int flags);

    /**
     * Get all the chunks of the plot asynchronized, including empty chunks.
     * Similar to {@link #getAllChunksAsync(World.Environment, int, Consumer)}, with 0 as flags parameter.
     *
     * @param environment The environment to get the chunks from.
     * @param onChunkLoad A consumer that will be ran when the chunk is loaded. Can be null.
     */
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, @Nullable Consumer<Chunk> onChunkLoad);

    /**
     * Get all the chunks of the plot asynchronized, including empty chunks.
     *
     * @param environment The environment to get the chunks from.
     * @param flags       See {@link PlotChunkFlags}
     * @param onChunkLoad A consumer that will be ran when the chunk is loaded. Can be null.
     */
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, @PlotChunkFlags int flags,
                                                     @Nullable Consumer<Chunk> onChunkLoad);

    /**
     * Get all the chunks of the plot asynchronized, including empty chunks.
     *
     * @param environment   The environment to get the chunks from.
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @param onChunkLoad   A consumer that will be ran when the chunk is loaded. Can be null.
     * @deprecated See {@link #getAllChunksAsync(World.Environment, int, Consumer)}
     */
    @Deprecated
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, boolean onlyProtected,
                                                     @Nullable Consumer<Chunk> onChunkLoad);

    /**
     * Get all the chunks of the plot asynchronized.
     *
     * @param environment   The environment to get the chunks from.
     * @param onlyProtected Whether only chunks inside the protected area should be returned.
     * @param noEmptyChunks Should empty chunks be loaded or not?
     * @param onChunkLoad   A consumer that will be ran when the chunk is loaded. Can be null.
     * @deprecated See {@link #getAllChunksAsync(World.Environment, int, Consumer)}
     */
    @Deprecated
    List<CompletableFuture<Chunk>> getAllChunksAsync(World.Environment environment, boolean onlyProtected, boolean noEmptyChunks, @Nullable Consumer<Chunk> onChunkLoad);

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     * Similar to {@link #resetChunks(int)}, with 0 as flags parameter.
     */
    void resetChunks();

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     * Similar to {@link #resetChunks(int, Runnable)}, with 0 as flags parameter.
     *
     * @param onFinish Callback runnable.
     */
    void resetChunks(@Nullable Runnable onFinish);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     * Similar to {@link #resetChunks(World.Environment, int)}, with 0 as flags parameter.
     *
     * @param environment The environment to reset chunks in.
     */
    void resetChunks(World.Environment environment);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     *
     * @param environment The environment to reset chunks in.
     * @param onFinish    Callback runnable.
     */
    void resetChunks(World.Environment environment, @Nullable Runnable onFinish);

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     *
     * @param flags See {@link PlotChunkFlags}
     */
    void resetChunks(@PlotChunkFlags int flags);

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     *
     * @param flags    See {@link PlotChunkFlags}
     * @param onFinish Callback runnable.
     */
    void resetChunks(@PlotChunkFlags int flags, @Nullable Runnable onFinish);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     *
     * @param environment The environment to reset chunks in.
     * @param flags       See {@link PlotChunkFlags}
     */
    void resetChunks(World.Environment environment, @PlotChunkFlags int flags);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     *
     * @param environment The environment to reset chunks in.
     * @param flags       See {@link PlotChunkFlags}
     * @param onFinish    Callback runnable.
     */
    void resetChunks(World.Environment environment, @PlotChunkFlags int flags, @Nullable Runnable onFinish);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     *
     * @param environment   The environment to reset chunks in.
     * @param onlyProtected Whether only chunks inside the protected area should be reset.
     * @deprecated See {@link #resetChunks(World.Environment, int)}
     */
    @Deprecated
    void resetChunks(World.Environment environment, boolean onlyProtected);

    /**
     * Reset all the chunks of the plot (will make all chunks empty).
     *
     * @param environment   The environment to reset chunks in.
     * @param onlyProtected Whether only chunks inside the protected area should be reset.
     * @param onFinish      Callback runnable.
     * @deprecated See {@link #resetChunks(World.Environment, int, Runnable)}
     */
    @Deprecated
    void resetChunks(World.Environment environment, boolean onlyProtected, @Nullable Runnable onFinish);

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     *
     * @param onlyProtected Whether only chunks inside the protected area should be reset.
     * @deprecated See {@link #resetChunks(int)}
     */
    @Deprecated
    void resetChunks(boolean onlyProtected);

    /**
     * Reset all the chunks of the plot from all the worlds (will make all chunks empty).
     *
     * @param onlyProtected Whether only chunks inside the protected area should be reset.
     * @param onFinish      Callback runnable.
     * @deprecated See {@link #resetChunks(int, Runnable)}
     */
    @Deprecated
    void resetChunks(boolean onlyProtected, @Nullable Runnable onFinish);

    /**
     * Check if the location is inside the plot's area.
     *
     * @param location The location to check.
     */
    boolean isInside(Location location);

    /**
     * Check if a chunk location is inside the plot's area.
     *
     * @param world  The world of the chunk.
     * @param chunkX The x-coords of the chunk.
     * @param chunkZ The z-coords of the chunk.
     */
    boolean isInside(World world, int chunkX, int chunkZ);

    /**
     * Check if the location is inside the plot's protected area.
     *
     * @param location The location to check.
     */
    boolean isInsideRange(Location location);

    /**
     * Check if the location is inside the plot's protected area.
     *
     * @param location    The location to check.
     * @param extraRadius Add extra radius to the protected range.
     */
    boolean isInsideRange(Location location, int extraRadius);

    /**
     * Check if the chunk is inside the plot's protected area.
     *
     * @param chunk The chunk to check.
     */
    boolean isInsideRange(Chunk chunk);

    /**
     * Is the normal world enabled for the plot?
     */
    boolean isNormalEnabled();

    /**
     * Enable/disable the normal world for the plot.
     */
    void setNormalEnabled(boolean enabled);

    /**
     * Is the nether world enabled for the plot?
     */
    boolean isNetherEnabled();

    /**
     * Enable/disable the nether world for the plot.
     */
    void setNetherEnabled(boolean enabled);

    /**
     * Is the end world enabled for the plot?
     */
    boolean isEndEnabled();

    /**
     * Enable/disable the end world for the plot.
     */
    void setEndEnabled(boolean enabled);

    /**
     * Get the unlocked worlds flag.
     */
    int getUnlockedWorldsFlag();

    /*
     *  Permissions related methods
     */

    /**
     * Check if a CommandSender has a permission.
     *
     * @param sender          The command-sender to check.
     * @param plotPrivilege The permission to check.
     */
    boolean hasPermission(CommandSender sender, PlotPrivilege plotPrivilege);

    /**
     * Check if a player has a permission.
     *
     * @param superiorPlayer  The player to check.
     * @param plotPrivilege The permission to check.
     */
    boolean hasPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege);

    /**
     * Check if a role has a permission.
     *
     * @param playerRole      The role to check.
     * @param plotPrivilege The permission to check.
     */
    boolean hasPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege);

    /**
     * Set a permission to a specific role.
     *
     * @param playerRole      The role to set the permission to.
     * @param plotPrivilege The permission to set.
     * @param value           The value to give the permission (Unused)
     * @deprecated See {@link #setPermission(PlayerRole, PlotPrivilege)}
     */
    @Deprecated
    void setPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege, boolean value);

    /**
     * Set a permission to a specific role.
     *
     * @param playerRole      The role to set the permission to.
     * @param plotPrivilege The permission to set.
     */
    void setPermission(PlayerRole playerRole, PlotPrivilege plotPrivilege);

    /**
     * Reset the roles permissions to default values.
     */
    void resetPermissions();

    /**
     * Set a permission to a specific player.
     *
     * @param superiorPlayer  The player to set the permission to.
     * @param plotPrivilege The permission to set.
     * @param value           The value to give the permission.
     */
    void setPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege, boolean value);

    /**
     * Reset player permissions to default values.
     */
    void resetPermissions(SuperiorPlayer superiorPlayer);

    /**
     * Get the permission-node of a player.
     *
     * @param superiorPlayer The player to check.
     */
    PermissionNode getPermissionNode(SuperiorPlayer superiorPlayer);

    /**
     * Get the required role for a specific permission.
     *
     * @param plotPrivilege The permission to check.
     */
    PlayerRole getRequiredPlayerRole(PlotPrivilege plotPrivilege);

    /**
     * Get all the custom player permissions of the plot.
     */
    Map<SuperiorPlayer, PermissionNode> getPlayerPermissions();

    /**
     * Get the permissions and their required roles.
     */
    Map<PlotPrivilege, PlayerRole> getRolePermissions();

    /*
     *  General methods
     */

    /**
     * Checks whether the plot is the spawn plot.
     */
    boolean isSpawn();

    /**
     * Get the name of the plot.
     */
    String getName();

    /**
     * Set the name of the plot.
     *
     * @param plotName The name to set.
     */
    void setName(String plotName);

    /**
     * Get the name of the plot, unformatted.
     */
    String getRawName();

    /**
     * Get the description of the plot.
     */
    String getDescription();

    /**
     * Set the description of the plot.
     *
     * @param description The description to set.
     */
    void setDescription(String description);

    /**
     * Disband the plot.
     */
    void disbandPlot();

    /**
     * Transfer the plot's leadership to another player.
     *
     * @param superiorPlayer The player to transfer the leadership to.
     * @return True if the transfer was succeed, otherwise false.
     */
    boolean transferPlot(SuperiorPlayer superiorPlayer);

    /**
     * Replace a player with a new player.
     *
     * @param originalPlayer The old player to be replaced.
     * @param newPlayer      The new player.
     *                       If null, the original player should just be removed.
     *                       If this is the owner of the plot, the plot will be disbanded.
     */
    void replacePlayers(SuperiorPlayer originalPlayer, @Nullable SuperiorPlayer newPlayer);

    /**
     * Recalculate the plot's worth value.
     *
     * @param asker The player who makes the operation.
     */
    void calcPlotWorth(@Nullable SuperiorPlayer asker);

    /**
     * Recalculate the plot's worth value.
     *
     * @param asker    The player who makes the operation.
     * @param callback Runnable which will be ran when process is finished.
     */
    void calcPlotWorth(@Nullable SuperiorPlayer asker, @Nullable Runnable callback);

    /**
     * Get the calculation algorithm used by this plot.
     */
    PlotCalculationAlgorithm getCalculationAlgorithm();

    /**
     * Update the border of all the players inside the plot.
     */
    void updateBorder();

    /**
     * Update the fly status for a player on this plot.
     *
     * @param superiorPlayer The player to update.
     */
    void updatePlotFly(SuperiorPlayer superiorPlayer);

    /**
     * Get the plot radius of the plot.
     */
    int getPlotSize();

    /**
     * Set the radius of the plot.
     *
     * @param plotSize The radius for the plot.
     */
    void setPlotSize(int plotSize);

    /**
     * Get the plot radius of the plot that was set with a command.
     */
    int getPlotSizeRaw();

    /**
     * Get the discord that is associated with the plot.
     */
    String getDiscord();

    /**
     * Set the discord that will be associated with the plot.
     */
    void setDiscord(String discord);

    /**
     * Get the paypal that is associated with the plot.
     */
    String getPaypal();

    /**
     * Get the paypal that will be associated with the plot.
     */
    void setPaypal(String paypal);

    /**
     * The current biome of the plot.
     */
    Biome getBiome();

    /**
     * Change the biome of the plot's area.
     */
    void setBiome(Biome biome);

    /**
     * Change the biome of the plot's area.
     *
     * @param updateBlocks Whether the blocks get updated or not.
     */
    void setBiome(Biome biome, boolean updateBlocks);

    /**
     * Check whether the plot is locked to visitors.
     */
    boolean isLocked();

    /**
     * Lock or unlock the plot to visitors.
     *
     * @param locked Whether the plot should be locked to visitors.
     */
    void setLocked(boolean locked);

    /**
     * Checks whether the plot is ignored in the top plots.
     */
    boolean isIgnored();

    /**
     * Set whether the plot should be ignored in the top plots.
     */
    void setIgnored(boolean ignored);

    /**
     * Send a plain message to all the members of the plot.
     *
     * @param message        The message to send
     * @param ignoredMembers An array of ignored members.
     */
    void sendMessage(String message, UUID... ignoredMembers);

    /**
     * Send a message to all the members of the plot.
     *
     * @param messageComponent The message to send
     * @param args             Arguments for the component.
     */
    void sendMessage(IMessageComponent messageComponent, Object... args);

    /**
     * Send a message to all the members of the plot.
     *
     * @param messageComponent The message to send
     * @param ignoredMembers   An array of ignored members.
     * @param args             Arguments for the component.
     */
    void sendMessage(IMessageComponent messageComponent, List<UUID> ignoredMembers, Object... args);

    /**
     * Send a plain message to all the members of the plot.
     *
     * @param title          The main title to send.
     * @param subtitle       The sub title to send.
     * @param fadeIn         The fade-in duration in ticks.
     * @param duration       The title duration in ticks.
     * @param fadeOut        The fade-out duration in ticks.
     * @param ignoredMembers An array of ignored members.
     */
    void sendTitle(@Nullable String title, @Nullable String subtitle, int fadeIn, int duration, int fadeOut, UUID... ignoredMembers);

    /**
     * Execute a command on all the members of the plot.
     * You can use {player-name} as a placeholder for the member's name.
     *
     * @param command           The command to execute.
     * @param onlyOnlineMembers Whether the command should be executed only for online members.
     * @param ignoredMembers    An array of ignored members.
     */
    void executeCommand(String command, boolean onlyOnlineMembers, UUID... ignoredMembers);

    /**
     * Checks whether the plot is being recalculated currently.
     */
    boolean isBeingRecalculated();

    /**
     * Update the last time the plot was used.
     */
    void updateLastTime();

    /**
     * Flag the plot as a currently active plot.
     */
    void setCurrentlyActive();

    /**
     * Set whether the plot is currently active.
     * Active plots are plots that have at least one plot member online.
     *
     * @param active Whether the plot is active.
     */
    void setCurrentlyActive(boolean active);

    /**
     * Check whether the plot is currently active.
     * Active plots are plots that have at least one plot member online.
     */
    boolean isCurrentlyActive();

    /**
     * Get the last time the plot was updated.
     * In case the plot is active, -1 will be returned.
     */
    long getLastTimeUpdate();

    /**
     * Set the last time the plot was updated.
     *
     * @param lastTimeUpdate The last time the plot was updated.
     */
    void setLastTimeUpdate(long lastTimeUpdate);

    /*
     *  Bank related methods
     */

    /**
     * Get the bank of the plot.
     */
    PlotBank getPlotBank();

    /**
     * Get the limit of the bank.
     */
    BigDecimal getBankLimit();

    /**
     * Set a new limit for the bank.
     *
     * @param bankLimit The limit to set. Use -1 to remove the limit.
     */
    void setBankLimit(BigDecimal bankLimit);

    /**
     * Get the limit of the bank that was set using a command.
     */
    BigDecimal getBankLimitRaw();

    /**
     * Give the bank interest to this plot.
     *
     * @param checkOnlineOwner Check if the plot-owner was online recently.
     * @return Whether the money was given.
     */
    boolean giveInterest(boolean checkOnlineOwner);

    /**
     * Get the last time that the bank interest was given.
     */
    long getLastInterestTime();

    /**
     * Set the last time that the bank interest was given.
     *
     * @param lastInterest The time it was given.
     */
    void setLastInterestTime(long lastInterest);

    /**
     * Get the duration until the bank interest will be given again, in seconds
     */
    long getNextInterest();

    /*
     *  Worth related methods
     */

    /**
     * Handle a placement of a block.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block The block that was placed.
     */
    void handleBlockPlace(Block block);

    /**
     * Handle a placement of a block.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block The block that was placed.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Block block);

    /**
     * Handle a placement of a block's key.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key The block's key that was placed.
     */
    void handleBlockPlace(Key key);

    /**
     * Handle a placement of a block's key.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key The block's key that was placed.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Key key);

    /**
     * Handle a placement of a block with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block  The block that was placed.
     * @param amount The amount of the block.
     */
    void handleBlockPlace(Block block, @Size int amount);

    /**
     * Handle a placement of a block with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block  The block that was placed.
     * @param amount The amount of the block.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Block block, @Size int amount);

    /**
     * Handle a placement of a block's key with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     */
    void handleBlockPlace(Key key, @Size int amount);

    /**
     * Handle a placement of a block's key with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Key key, @Size int amount);

    /**
     * Handle a placement of a block with a specific amount.
     *
     * @param block  The block that was placed.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlockPlace(Block block, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a placement of a block with a specific amount.
     *
     * @param block  The block that was placed.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Block block, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a placement of a block's key with a specific amount.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlockPlace(Key key, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a placement of a block's key with a specific amount.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockPlaceWithResult(Key key, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a placement of a block with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param block  The block that was placed.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into database.
     */
    @Deprecated
    void handleBlockPlace(Block block, @Size int amount, boolean save);

    /**
     * Handle a placement of a block's key with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into database.
     */
    @Deprecated
    void handleBlockPlace(Key key, @Size int amount, boolean save);

    /**
     * Handle a placement of a block's key with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param key    The block's key that was placed.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into database.
     */
    @Deprecated
    void handleBlockPlace(Key key, @Size BigInteger amount, boolean save);

    /**
     * Handle a placement of a block's key with a specific amount.
     *
     * @param key                  The block's key that was placed.
     * @param amount               The amount of the block.
     * @param save                 Whether the block counts should be saved into database.
     * @param updateLastTimeStatus Whether to update last time plot was updated or not.
     */
    @Deprecated
    void handleBlockPlace(Key key, @Size BigInteger amount, boolean save, boolean updateLastTimeStatus);

    /**
     * Handle placements of many blocks in one time.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param blocks All the blocks to place.
     */
    void handleBlocksPlace(Map<Key, Integer> blocks);

    /**
     * Handle placements of many blocks in one time.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param blocks All the blocks to place.
     * @return Results per block key. Only non-successful results will be returned.
     */
    Map<Key, BlockChangeResult> handleBlocksPlaceWithResult(Map<Key, Integer> blocks);

    /**
     * Handle placements of many blocks in one time.
     *
     * @param blocks All the blocks to place.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlocksPlace(Map<Key, Integer> blocks, @PlotBlockFlags int flags);

    /**
     * Handle placements of many blocks in one time.
     *
     * @param blocks All the blocks to place.
     * @param flags  See {@link PlotBlockFlags}
     * @return Results per block key. Only non-successful results will be returned.
     */
    Map<Key, BlockChangeResult> handleBlocksPlaceWithResult(Map<Key, Integer> blocks, @PlotBlockFlags int flags);

    /**
     * Handle a break of a block.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block The block that was broken.
     */
    void handleBlockBreak(Block block);

    /**
     * Handle a break of a block.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block The block that was broken.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockBreakWithResult(Block block);

    /**
     * Handle a break of a block's key.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key The block's key that was broken.
     */
    void handleBlockBreak(Key key);

    /**
     * Handle a break of a block's key.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key The block's key that was broken.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockBreakWithResult(Key key);

    /**
     * Handle a break of a block with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block  The block that was broken.
     * @param amount The amount of the block.
     */
    void handleBlockBreak(Block block, @Size int amount);

    /**
     * Handle a break of a block with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param block  The block that was broken.
     * @param amount The amount of the block.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockBreakWithResult(Block block, @Size int amount);

    /**
     * Handle a break of a block's key with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     */
    void handleBlockBreak(Key key, @Size int amount);

    /**
     * Handle a break of a block's key with a specific amount.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     * @return The result of the block place.
     */
    BlockChangeResult handleBlockBreakWithResult(Key key, @Size int amount);

    /**
     * Handle a break of a block with a specific amount.
     *
     * @param block  The block that was broken.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlockBreak(Block block, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a break of a block with a specific amount.
     *
     * @param block  The block that was broken.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    BlockChangeResult handleBlockBreakWithResult(Block block, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a break of a block's key with a specific amount.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlockBreak(Key key, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a break of a block's key with a specific amount.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     * @param flags  See {@link PlotBlockFlags}
     */
    BlockChangeResult handleBlockBreakWithResult(Key key, @Size int amount, @PlotBlockFlags int flags);

    /**
     * Handle a break of a block with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param block  The block that was broken.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into the database.
     */
    @Deprecated
    void handleBlockBreak(Block block, @Size int amount, boolean save);

    /**
     * Handle a break of a block with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into the database.
     */
    @Deprecated
    void handleBlockBreak(Key key, @Size int amount, boolean save);

    /**
     * Handle a break of a block with a specific amount.
     * This will update the last time status of the plot.
     *
     * @param key    The block's key that was broken.
     * @param amount The amount of the block.
     * @param save   Whether the block counts should be saved into the database.
     */
    @Deprecated
    void handleBlockBreak(Key key, @Size BigInteger amount, boolean save);

    /**
     * Handle break of many blocks in one time.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param blocks All the blocks to break.
     */
    void handleBlocksBreak(Map<Key, Integer> blocks);

    /**
     * Handle break of many blocks in one time.
     * This will save the block counts and update the last time status of the plot.
     *
     * @param blocks All the blocks to break.
     * @return Results per block key. Only non-successful results will be returned.
     */
    Map<Key, BlockChangeResult> handleBlocksBreakWithResult(Map<Key, Integer> blocks);

    /**
     * Handle break of many blocks in one time.
     *
     * @param blocks All the blocks to break.
     * @param flags  See {@link PlotBlockFlags}
     */
    void handleBlocksBreak(Map<Key, Integer> blocks, @PlotBlockFlags int flags);

    /**
     * Handle break of many blocks in one time.
     *
     * @param blocks All the blocks to break.
     * @param flags  See {@link PlotBlockFlags}
     * @return Results per block key. Only non-successful results will be returned.
     */
    Map<Key, BlockChangeResult> handleBlocksBreakWithResult(Map<Key, Integer> blocks, @PlotBlockFlags int flags);

    /**
     * Check whether a chunk has blocks inside it.
     *
     * @param world  The world of the chunk.
     * @param chunkX The x-coords of the chunk.
     * @param chunkZ The z-coords of the chunk.
     */
    boolean isChunkDirty(World world, int chunkX, int chunkZ);

    /**
     * Check whether a chunk has blocks inside it.
     *
     * @param worldName The name of the world of the chunk.
     * @param chunkX    The x-coords of the chunk.
     * @param chunkZ    The z-coords of the chunk.
     */
    boolean isChunkDirty(String worldName, int chunkX, int chunkZ);

    /**
     * Mark a chunk as it has blocks inside it.
     *
     * @param world  The world of the chunk.
     * @param chunkX The x-coords of the chunk.
     * @param chunkZ The z-coords of the chunk.
     * @param save   Whether to save the changes to database.
     */
    void markChunkDirty(World world, int chunkX, int chunkZ, boolean save);

    /**
     * Mark a chunk as it has no blocks inside it.
     *
     * @param world  The world of the chunk.
     * @param chunkX The x-coords of the chunk.
     * @param chunkZ The z-coords of the chunk.
     * @param save   Whether to save the changes to database.
     */
    void markChunkEmpty(World world, int chunkX, int chunkZ, boolean save);

    /**
     * Get the amount of blocks that are on the plot.
     *
     * @param key The block's key to check.
     */
    BigInteger getBlockCountAsBigInteger(Key key);

    /**
     * Get all the blocks that are on the plot.
     */
    Map<Key, BigInteger> getBlockCountsAsBigInteger();

    /**
     * Get the amount of blocks that are on the plot.
     * Unlike getBlockCount(Key), this method returns the count for
     * the exactly block that is given as a parameter.
     *
     * @param key The block's key to check.
     */
    BigInteger getExactBlockCountAsBigInteger(Key key);

    /**
     * Clear all the block counts of the plot.
     */
    void clearBlockCounts();

    /**
     * Get the blocks-tracker used by this plot.
     */
    PlotBlocksTrackerAlgorithm getBlocksTracker();

    /**
     * Get the worth value of the plot, including the money in the bank.
     */
    BigDecimal getWorth();

    /**
     * Get the worth value of the plot, excluding bonus worth and the money in the bank.
     */
    BigDecimal getRawWorth();

    /**
     * Get the bonus worth of the plot.
     */
    BigDecimal getBonusWorth();

    /**
     * Set a bonus worth for the plot.
     *
     * @param bonusWorth The bonus to give.
     */
    void setBonusWorth(BigDecimal bonusWorth);

    /**
     * Get the bonus level of the plot.
     */
    BigDecimal getBonusLevel();

    /**
     * Set a bonus level for the plot.
     *
     * @param bonusLevel The bonus to give.
     */
    void setBonusLevel(BigDecimal bonusLevel);

    /**
     * Get the level of the plot.
     */
    BigDecimal getPlotLevel();

    /**
     * Get the level value of the plot, excluding the bonus level.
     */
    BigDecimal getRawLevel();

    /*
     *  Upgrades related methods
     */

    /**
     * Get the level of an upgrade for the plot.
     *
     * @param upgrade The upgrade to check.
     */
    UpgradeLevel getUpgradeLevel(Upgrade upgrade);

    /**
     * Set the level of an upgrade for the plot.
     *
     * @param upgrade The upgrade to set the level.
     * @param level   The level to set.
     */
    void setUpgradeLevel(Upgrade upgrade, int level);

    /**
     * Get all the upgrades with their levels.
     */
    Map<String, Integer> getUpgrades();

    /**
     * Sync all the upgrade values again.
     * This will remove custom values that were set using the set commands.
     */
    void syncUpgrades();

    /**
     * Update the upgrade values from default values of config & upgrades file.
     */
    void updateUpgrades();

    /**
     * Get the last time the plot was upgraded.
     */
    long getLastTimeUpgrade();

    /**
     * Check if the plot has an active upgrade cooldown.
     */
    boolean hasActiveUpgradeCooldown();

    /**
     * Get the crop-growth multiplier for the plot.
     */
    double getCropGrowthMultiplier();

    /**
     * Set the crop-growth multiplier for the plot.
     *
     * @param cropGrowth The multiplier to set.
     */
    void setCropGrowthMultiplier(double cropGrowth);

    /**
     * Get the crop-growth multiplier for the plot that was set using a command.
     */
    double getCropGrowthRaw();

    /**
     * Get the spawner-rates multiplier for the plot.
     */
    double getSpawnerRatesMultiplier();

    /**
     * Set the spawner-rates multiplier for the plot.
     *
     * @param spawnerRates The multiplier to set.
     */
    void setSpawnerRatesMultiplier(double spawnerRates);

    /**
     * Get the spawner-rates multiplier for the plot that was set using a command.
     */
    double getSpawnerRatesRaw();

    /**
     * Get the mob-drops multiplier for the plot.
     */
    double getMobDropsMultiplier();

    /**
     * Set the mob-drops multiplier for the plot.
     *
     * @param mobDrops The multiplier to set.
     */
    void setMobDropsMultiplier(double mobDrops);

    /**
     * Get the mob-drops multiplier for the plot that was set using a command.
     */
    double getMobDropsRaw();

    /**
     * Get the block limit of a block.
     *
     * @param key The block's key to check.
     */
    int getBlockLimit(Key key);

    /**
     * Get the block limit of a block.
     * Unlike getBlockLimit(Key), this method returns the count for
     * the exactly block that is given as a parameter.
     *
     * @param key The block's key to check.
     */
    int getExactBlockLimit(Key key);

    /**
     * Get the block key used as a limit for another block key.
     *
     * @param key The block's key to check.
     */
    Key getBlockLimitKey(Key key);

    /**
     * Get all the blocks limits for the plot.
     */
    Map<Key, Integer> getBlocksLimits();

    /**
     * Get all the custom blocks limits for the plot.
     */
    Map<Key, Integer> getCustomBlocksLimits();

    /**
     * Clear all the block limits of the plot.
     */
    void clearBlockLimits();

    /**
     * Set the block limit of a block.
     *
     * @param key   The block's key to set the limit to.
     * @param limit The limit to set.
     */
    void setBlockLimit(Key key, int limit);

    /**
     * Remove the limit of a block.
     *
     * @param key The block's key to remove it's limit.
     */
    void removeBlockLimit(Key key);

    /**
     * A method to check if a specific block has reached the limit.
     * This method checks for the block and it's global block key.
     *
     * @param key The block's key to check.
     */
    boolean hasReachedBlockLimit(Key key);

    /**
     * A method to check if a specific block has reached the limit.
     * This method checks for the block and it's global block key.
     *
     * @param key    The block's key to check.
     * @param amount Amount of the block to be placed.
     */
    boolean hasReachedBlockLimit(Key key, int amount);

    /**
     * Get the entity limit of an entity.
     *
     * @param entityType The entity's type to check.
     */
    int getEntityLimit(EntityType entityType);

    /**
     * Get the entity limit of an entity.
     *
     * @param key The key of the entity to check.
     */
    int getEntityLimit(Key key);

    /**
     * Get all the entities limits for the plot.
     */
    Map<Key, Integer> getEntitiesLimitsAsKeys();

    /**
     * Get all the custom entities limits for the plot.
     */
    Map<Key, Integer> getCustomEntitiesLimits();

    /**
     * Clear all the entities limits from the plot.
     */
    void clearEntitiesLimits();

    /**
     * Set the entity limit of an entity.
     *
     * @param entityType The entity's type to set the limit to.
     * @param limit      The limit to set.
     */
    void setEntityLimit(EntityType entityType, int limit);

    /**
     * Set the entity limit of an entity.
     *
     * @param key   The key of the entity to set the limit to.
     * @param limit The limit to set.
     */
    void setEntityLimit(Key key, int limit);

    /**
     * A method to check if a specific entity has reached the limit.
     *
     * @param entityType The entity's type to check.
     */
    CompletableFuture<Boolean> hasReachedEntityLimit(EntityType entityType);

    /**
     * A method to check if a specific entity has reached the limit.
     *
     * @param key The key of the entity to check.
     */
    CompletableFuture<Boolean> hasReachedEntityLimit(Key key);

    /**
     * A method to check if a specific entity has reached the limit.
     *
     * @param amount     The amount of entities that were added.
     * @param entityType The entity's type to check.
     */
    CompletableFuture<Boolean> hasReachedEntityLimit(EntityType entityType, int amount);

    /**
     * A method to check if a specific entity has reached the limit.
     *
     * @param amount The amount of entities that were added.
     * @param key    The key of the entity to check.
     */
    CompletableFuture<Boolean> hasReachedEntityLimit(Key key, int amount);

    /**
     * Get the entities tracker used by the plot.
     */
    PlotEntitiesTrackerAlgorithm getEntitiesTracker();

    /**
     * Get the team limit of the plot.
     */
    int getTeamLimit();

    /**
     * Set the team limit of the plot.
     *
     * @param teamLimit The team limit to set.
     */
    void setTeamLimit(int teamLimit);

    /**
     * Get the team limit of the plot that was set with a command.
     */
    int getTeamLimitRaw();

    /**
     * Get the warps limit of the plot.
     */
    int getWarpsLimit();

    /**
     * Set the warps limit for the plot.
     *
     * @param warpsLimit The limit to set.
     */
    void setWarpsLimit(int warpsLimit);

    /**
     * Get the warps limit of the plot that was set using a command.
     */
    int getWarpsLimitRaw();

    /**
     * Add a potion effect to the plot.
     *
     * @param type  The potion effect to add.
     * @param level The level of the potion effect.
     *              If the level is 0 or below, then the effect will be removed.
     */
    void setPotionEffect(PotionEffectType type, int level);

    /**
     * Remove a potion effect from the plot.
     *
     * @param type The potion effect to remove.
     */
    void removePotionEffect(PotionEffectType type);

    /**
     * Get the level of an plot effect.
     *
     * @param type The potion to check.
     * @return The level of the potion. If 0, it means that this is not an active effect on the plot.
     */
    int getPotionEffectLevel(PotionEffectType type);

    /**
     * Get a list of all active plot effects with their levels.
     */
    Map<PotionEffectType, Integer> getPotionEffects();

    /**
     * Give all the plot effects to a player.
     * If the player is offline, nothing will happen.
     *
     * @param superiorPlayer The player to give the effect to.
     */
    void applyEffects(SuperiorPlayer superiorPlayer);

    /**
     * Give all the plot effects to the players inside the plot.
     */
    void applyEffects();

    /**
     * Remove all the plot effects from a player.
     * If the player is offline, nothing will happen.
     *
     * @param superiorPlayer The player to remove the effects to.
     */
    void removeEffects(SuperiorPlayer superiorPlayer);

    /**
     * Remove all the plot effects from the players inside the plot.
     */
    void removeEffects();

    /**
     * Remove all the effects from the plot.
     */
    void clearEffects();

    /**
     * Set the limit of the amount of players that can have the role in the plot.
     *
     * @param playerRole The role to set the limit to.
     * @param limit      The limit to set.
     */
    void setRoleLimit(PlayerRole playerRole, int limit);

    /**
     * Remove the limit of the amount of players that can have the role in the plot.
     *
     * @param playerRole The role to remove the limit.
     */
    void removeRoleLimit(PlayerRole playerRole);

    /**
     * Get the limit of players that can have the same role at a time.
     *
     * @param playerRole The role to check.
     */
    int getRoleLimit(PlayerRole playerRole);

    /**
     * Get the limit of players that can have the same role at a time that was set using a command.
     *
     * @param playerRole The role to check.
     */
    int getRoleLimitRaw(PlayerRole playerRole);

    /**
     * Get all the role limits for the plot.
     */
    Map<PlayerRole, Integer> getRoleLimits();

    /**
     * Get all the custom role limits for the plot.
     */
    Map<PlayerRole, Integer> getCustomRoleLimits();

    /*
     *  Warps related methods
     */

    /**
     * Create a new warp category.
     * If a category already exists, it will be returned instead of a new created one.
     *
     * @param name The name of the category.
     */
    WarpCategory createWarpCategory(String name);

    /**
     * Get a warp category.
     *
     * @param name The name of the category.
     */
    @Nullable
    WarpCategory getWarpCategory(String name);

    /**
     * Get a warp category by the slot inside the manage menu.
     *
     * @param slot The slot to check.
     */
    @Nullable
    WarpCategory getWarpCategory(int slot);

    /**
     * Rename a category.
     *
     * @param warpCategory The category to rename.
     * @param newName      A new name to set.
     */
    void renameCategory(WarpCategory warpCategory, String newName);

    /**
     * Delete a warp category.
     * All the warps inside it will be deleted as well.
     *
     * @param warpCategory The category to delete.
     */
    void deleteCategory(WarpCategory warpCategory);

    /**
     * Get all the warp categories of the plot.
     */
    Map<String, WarpCategory> getWarpCategories();

    /**
     * Create a warp for the plot.
     *
     * @param name         The name of the warp.
     * @param location     The location of the warp.
     * @param warpCategory The category to add the plot.
     * @return The new plot warp object.
     */
    PlotWarp createWarp(String name, Location location, @Nullable WarpCategory warpCategory);

    /**
     * Rename a warp.
     *
     * @param plotWarp The warp to rename.
     * @param newName    A new name to set.
     */
    void renameWarp(PlotWarp plotWarp, String newName);

    /**
     * Get an plot warp in a specific location.
     *
     * @param location The location to check.
     */
    @Nullable
    PlotWarp getWarp(Location location);

    /**
     * Get an plot warp by it's name..
     *
     * @param name The name to check.
     */
    @Nullable
    PlotWarp getWarp(String name);

    /**
     * Teleport a player to a warp.
     *
     * @param superiorPlayer The player to teleport.
     * @param warp           The warp's name to teleport the player to.
     */
    void warpPlayer(SuperiorPlayer superiorPlayer, String warp);

    /**
     * Delete a warp from the plot.
     *
     * @param superiorPlayer The player who requested the operation.
     * @param location       The location of the warp.
     */
    void deleteWarp(@Nullable SuperiorPlayer superiorPlayer, Location location);

    /**
     * Delete a warp from the plot.
     *
     * @param name The warp's name to delete.
     */
    void deleteWarp(String name);

    /**
     * Get all the warps of the plot.
     */
    Map<String, PlotWarp> getPlotWarps();

    /*
     *  Ratings related methods
     */

    /**
     * Get the rating that a player has given the plot.
     *
     * @param superiorPlayer The player to check.
     */
    Rating getRating(SuperiorPlayer superiorPlayer);

    /**
     * Set a rating of a player.
     *
     * @param superiorPlayer The player that sets the rating.
     * @param rating         The rating to set.
     */
    void setRating(SuperiorPlayer superiorPlayer, Rating rating);

    /**
     * Remove a rating of a player.
     *
     * @param superiorPlayer The player to remove the rating of.
     */
    void removeRating(SuperiorPlayer superiorPlayer);

    /**
     * Get the total rating of the plot.
     */
    double getTotalRating();

    /**
     * Get the amount of ratings that have have been given to the plot.
     */
    int getRatingAmount();

    /**
     * Get all the ratings of the plot.
     */
    Map<UUID, Rating> getRatings();

    /**
     * Remove all the ratings of the plot.
     */
    void removeRatings();

    /*
     *  Settings related methods
     */

    /**
     * Check whether a settings is enabled or not.
     *
     * @param plotFlag The settings to check.
     */
    boolean hasSettingsEnabled(PlotFlag plotFlag);

    /**
     * Get all the settings of the plot.
     * If the byte value is 1, the setting is enabled. Otherwise, it's disabled.
     */
    Map<PlotFlag, Byte> getAllSettings();

    /**
     * Enable an plot settings.
     *
     * @param plotFlag The settings to enable.
     */
    void enableSettings(PlotFlag plotFlag);

    /**
     * Disable an plot settings.
     *
     * @param plotFlag The settings to disable.
     */
    void disableSettings(PlotFlag plotFlag);

    /*
     *  Generator related methods
     */

    /**
     * Set a percentage for a specific key in a specific world.
     * Percentage can be between 0 and 100 (0 will remove the key from the list).
     * Calling this method will not make events get fired.
     * <p>
     * This function sets the amount of the key using the following formula:
     * amount = (percentage * total_amount) / (1 - percentage)
     * <p>
     * If the percentage is 100, the rest of the amounts will be cleared and
     * the material's amount will be set to 1.
     * <p>
     * The amount is rounded to ensure a smaller loss, and currently it's 1%~ loss.
     *
     * @param key         The block to change the generator rate of.
     * @param percentage  The percentage to set the new rate.
     * @param environment The world to change the rates in.
     */
    void setGeneratorPercentage(Key key, int percentage, World.Environment environment);

    /**
     * Set a percentage for a specific key in a specific world.
     * Percentage can be between 0 and 100 (0 will remove the key from the list).
     * <p>
     * This function sets the amount of the key using the following formula:
     * amount = (percentage * total_amount) / (1 - percentage)
     * <p>
     * If the percentage is 100, the rest of the amounts will be cleared and
     * the material's amount will be set to 1.
     * <p>
     * The amount is rounded to ensure a smaller loss, and currently it's 1%~ loss.
     *
     * @param key         The block to change the generator rate of.
     * @param percentage  The percentage to set the new rate.
     * @param environment The world to change the rates in.
     * @param caller      The player that changes the percentages (used for the event).
     *                    If null, it means the console did the operation.
     * @param callEvent   Whether to call the {@link com.bgsoftware.superiorskyblock.api.events.PlotChangeGeneratorRateEvent}
     * @return Whether the operation succeed.
     * The operation may fail if callEvent is true and the event was cancelled.
     */
    boolean setGeneratorPercentage(Key key, int percentage, World.Environment environment,
                                   @Nullable SuperiorPlayer caller, boolean callEvent);

    /**
     * Get the percentage for a specific key in a specific world.
     * The formula is (amount * 100) / total_amount.
     *
     * @param key         The material key
     * @param environment The world environment.
     */
    int getGeneratorPercentage(Key key, World.Environment environment);

    /**
     * Get the percentages of the materials for the cobblestone generator in the plot for a specific world.
     */
    Map<String, Integer> getGeneratorPercentages(World.Environment environment);

    /**
     * Set an amount for a specific key in a specific world.
     */
    void setGeneratorAmount(Key key, @Size int amount, World.Environment environment);

    /**
     * Remove a rate for a specific key in a specific world.
     */
    void removeGeneratorAmount(Key key, World.Environment environment);

    /**
     * Get the amount of a specific key in a specific world.
     */
    int getGeneratorAmount(Key key, World.Environment environment);

    /**
     * Get the total amount of all the generator keys together.
     */
    int getGeneratorTotalAmount(World.Environment environment);

    /**
     * Get the amounts of the materials for the cobblestone generator in the plot.
     */
    Map<String, Integer> getGeneratorAmounts(World.Environment environment);

    /**
     * Get the custom amounts of the materials for the cobblestone generator in the plot.
     */
    Map<Key, Integer> getCustomGeneratorAmounts(World.Environment environment);

    /**
     * Clear all the custom generator amounts for this plot.
     */
    void clearGeneratorAmounts(World.Environment environment);

    /**
     * Generate a block at a specified location.
     * The method calculates a block to generate from {@link #getGeneratorAmounts(World.Environment)}.
     * It doesn't look for any conditions for generating it - lava, water, etc are not required.
     * The method will fail if there are no valid generator rates for the environment.
     *
     * @param location            The location to generate block at.
     * @param optimizeCobblestone When set to true and cobblestone needs to be generated, the plugin will
     *                            not play effects, count the block towards the block counts or set the block.
     *                            This is useful when calling the method from BlockFromToEvent, and instead of letting
     *                            the player do the logic of vanilla, the plugin lets the game do it.
     * @return The block type that was generated, null if failed.
     */
    @Nullable
    Key generateBlock(Location location, boolean optimizeCobblestone);


    /**
     * Generate a block at a specified location.
     * The method calculates a block to generate from {@link #getGeneratorAmounts(World.Environment)}.
     * It doesn't look for any conditions for generating it - lava, water, etc are not required.
     * The method will fail if there are no valid generator rates for the environment.
     *
     * @param location            The location to generate block at.
     * @param environment         The world to get generator rates from.
     * @param optimizeCobblestone When set to true and cobblestone needs to be generated, the plugin will
     *                            not play effects, count the block towards the block counts or set the block.
     *                            This is useful when calling the method from BlockFromToEvent, and instead of letting
     *                            the player do the logic of vanilla, the plugin lets the game do it.
     * @return The block type that was generated, null if failed.
     */
    @Nullable
    Key generateBlock(Location location, World.Environment environment, boolean optimizeCobblestone);

    /*
     *  Schematic methods
     */

    /**
     * Checks if a schematic was generated already.
     *
     * @param environment The environment to check.
     */
    boolean wasSchematicGenerated(World.Environment environment);

    /**
     * Set schematic generated flag to true.
     *
     * @param environment The environment to set.
     */
    void setSchematicGenerate(World.Environment environment);

    /**
     * Set schematic generated flag.
     *
     * @param environment The environment to set.
     * @param generated   The flag to set.
     */
    void setSchematicGenerate(World.Environment environment, boolean generated);

    /**
     * Get the generated schematics flag.
     */
    int getGeneratedSchematicsFlag();

    /**
     * Get the schematic that was used to create the plot.
     */
    String getSchematicName();

    /*
     *  Plot top methods
     */

    int getPosition(SortingType sortingType);

    /*
     *  Vault related methods
     */

    /**
     * Get the plot chest.
     */
    PlotChest[] getChest();

    /**
     * Get the amount of pages the plot chest has.
     */
    int getChestSize();

    /**
     * Set the amount of rows for the chest in a specific index.
     *
     * @param index The index of the page (0 or above)
     * @param rows  The amount of rows for that page.
     */
    void setChestRows(int index, int rows);

    /**
     * Create a new builder for a {@link Plot} object.
     */
    static Builder newBuilder() {
        return SuperiorSkyblockAPI.getFactory().createPlotBuilder();
    }

    /**
     * The {@link Builder} interface is used to create {@link Plot} objects with predefined values.
     * All of its methods are setters for all the values possible to create an plot with.
     * Use {@link Builder#build()} to create the new {@link Plot} object. You must set
     * {@link Builder#setOwner(SuperiorPlayer)}, {@link Builder#setUniqueId(UUID)} and
     * {@link Builder#setCenter(Location)} before creating a new {@link Plot}
     */
    interface Builder {

        Builder setOwner(@Nullable SuperiorPlayer owner);

        @Nullable
        SuperiorPlayer getOwner();

        Builder setUniqueId(UUID uuid);

        UUID getUniqueId();

        Builder setCenter(Location center);

        Location getCenter();

        Builder setName(String plotName);

        String getName();

        Builder setSchematicName(String schematicName);

        String getScehmaticName();

        Builder setCreationTime(long creationTime);

        long getCreationTime();

        Builder setDiscord(String discord);

        String getDiscord();

        Builder setPaypal(String paypal);

        String getPaypal();

        Builder setBonusWorth(BigDecimal bonusWorth);

        BigDecimal getBonusWorth();

        Builder setBonusLevel(BigDecimal bonusLevel);

        BigDecimal getBonusLevel();

        Builder setLocked(boolean isLocked);

        boolean isLocked();

        Builder setIgnored(boolean isIgnored);

        boolean isIgnored();

        Builder setDescription(String description);

        String getDescription();

        Builder setGeneratedSchematics(int generatedSchematicsMask);

        int getGeneratedSchematicsMask();

        Builder setUnlockedWorlds(int unlockedWorldsMask);

        int getUnlockedWorldsMask();

        Builder setLastTimeUpdated(long lastTimeUpdated);

        long getLastTimeUpdated();

        Builder setDirtyChunk(String worldName, int chunkX, int chunkZ);

        boolean isDirtyChunk(String worldName, int chunkX, int chunkZ);

        Builder setBlockCount(Key block, BigInteger count);

        KeyMap<BigInteger> getBlockCounts();

        Builder setPlotHome(Location location, World.Environment environment);

        Map<World.Environment, Location> getPlotHomes();

        Builder addPlotMember(SuperiorPlayer superiorPlayer);

        List<SuperiorPlayer> getPlotMembers();

        Builder addBannedPlayer(SuperiorPlayer superiorPlayer);

        List<SuperiorPlayer> getBannedPlayers();

        Builder setPlayerPermission(SuperiorPlayer superiorPlayer, PlotPrivilege plotPrivilege, boolean value);

        Map<SuperiorPlayer, PermissionNode> getPlayerPermissions();

        Builder setRolePermission(PlotPrivilege plotPrivilege, PlayerRole requiredRole);

        Map<PlotPrivilege, PlayerRole> getRolePermissions();

        Builder setUpgrade(Upgrade upgrade, int level);

        Map<Upgrade, Integer> getUpgrades();

        Builder setBlockLimit(Key block, int limit);

        KeyMap<Integer> getBlockLimits();

        Builder setRating(SuperiorPlayer superiorPlayer, Rating rating);

        Map<SuperiorPlayer, Rating> getRatings();

        Builder setCompletedMission(Mission<?> mission, int finishCount);

        Map<Mission<?>, Integer> getCompletedMissions();

        Builder setPlotFlag(PlotFlag plotFlag, boolean value);

        Map<PlotFlag, SyncStatus> getPlotFlags();

        Builder setGeneratorRate(Key block, int rate, World.Environment environment);

        Map<World.Environment, KeyMap<Integer>> getGeneratorRates();

        Builder addUniqueVisitor(SuperiorPlayer superiorPlayer, long visitTime);

        Map<SuperiorPlayer, Long> getUniqueVisitors();

        Builder setEntityLimit(Key entity, int limit);

        KeyMap<Integer> getEntityLimits();

        Builder setPlotEffect(PotionEffectType potionEffectType, int level);

        Map<PotionEffectType, Integer> getPlotEffects();

        Builder setPlotChest(int index, ItemStack[] contents);

        List<ItemStack[]> getPlotChests();

        Builder setRoleLimit(PlayerRole playerRole, int limit);

        Map<PlayerRole, Integer> getRoleLimits();

        Builder setVisitorHome(Location location, World.Environment environment);

        Map<World.Environment, Location> getVisitorHomes();

        Builder setPlotSize(int plotSize);

        int getPlotSize();

        Builder setTeamLimit(int teamLimit);

        int getTeamLimit();

        Builder setWarpsLimit(int warpsLimit);

        int getWarpsLimit();

        Builder setCropGrowth(double cropGrowth);

        double getCropGrowth();

        Builder setSpawnerRates(double spawnerRates);

        double getSpawnerRates();

        Builder setMobDrops(double mobDrops);

        double getMobDrops();

        Builder setCoopLimit(int coopLimit);

        int getCoopLimit();

        Builder setBankLimit(BigDecimal bankLimit);

        BigDecimal getBankLimit();

        Builder setBalance(BigDecimal balance);

        BigDecimal getBalance();

        Builder setLastInterestTime(long lastInterestTime);

        long getLastInterestTime();

        Builder addWarp(String name, String category, Location location, boolean isPrivate, @Nullable ItemStack icon);

        boolean hasWarp(String name);

        boolean hasWarp(Location location);

        Builder addWarpCategory(String name, int slot, @Nullable ItemStack icon);

        boolean hasWarpCategory(String name);

        Builder addBankTransaction(BankTransaction bankTransaction);

        List<BankTransaction> getBankTransactions();

        Builder setPersistentData(byte[] persistentData);

        byte[] getPersistentData();

        Plot build();


    }

}
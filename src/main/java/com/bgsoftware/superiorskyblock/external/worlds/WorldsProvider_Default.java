package com.bgsoftware.superiorskyblock.external.worlds;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.hooks.WorldsProvider;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.service.dragon.DragonBattleService;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.SBlockPosition;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;

import java.util.EnumMap;
import java.util.Set;
import java.util.UUID;

public class WorldsProvider_Default implements WorldsProvider {

    private final Set<BlockPosition> servedPositions = Sets.newHashSet();
    private final EnumMap<World.Environment, World> plotWorlds = new EnumMap<>(World.Environment.class);
    private final SuperiorSkyblockPlugin plugin;

    private final LazyReference<DragonBattleService> dragonBattleService = new LazyReference<DragonBattleService>() {
        @Override
        protected DragonBattleService create() {
            return plugin.getServices().getService(DragonBattleService.class);
        }
    };

    public WorldsProvider_Default(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void prepareWorlds() {
        Difficulty difficulty = Difficulty.valueOf(plugin.getSettings().getWorlds().getDifficulty());
        if (plugin.getSettings().getWorlds().getNormal().isEnabled())
            loadWorld(plugin.getSettings().getWorlds().getWorldName(), difficulty, World.Environment.NORMAL);
        if (plugin.getSettings().getWorlds().getNether().isEnabled())
            loadWorld(plugin.getSettings().getWorlds().getNether().getName(), difficulty, World.Environment.NETHER);
        if (plugin.getSettings().getWorlds().getEnd().isEnabled()) {
            World endWorld = loadWorld(plugin.getSettings().getWorlds().getEnd().getName(), difficulty, World.Environment.THE_END);
            if (plugin.getSettings().getWorlds().getEnd().isDragonFight())
                dragonBattleService.get().prepareEndWorld(endWorld);
        }
    }

    @Override
    public World getPlotsWorld(Plot plot, World.Environment environment) {
        Preconditions.checkNotNull(environment, "environment parameter cannot be null.");
        return plotWorlds.get(environment);
    }

    @Override
    public boolean isPlotsWorld(World world) {
        Preconditions.checkNotNull(world, "world parameter cannot be null.");
        World plotsWorld = getPlotsWorld(null, world.getEnvironment());
        return plotsWorld != null && world.getUID().equals(plotsWorld.getUID());
    }

    @Override
    public Location getNextLocation(Location previousLocation, int plotsHeight, int maxPlotSize, UUID plotOwner, UUID plotUUID) {
        Preconditions.checkNotNull(previousLocation, "previousLocation parameter cannot be null.");

        Location location = previousLocation.clone();
        location.setY(plotsHeight);
        BlockFace plotFace = getPlotFace(location);

        int plotRange = maxPlotSize * 3;

        if (plotFace == BlockFace.NORTH) {
            location.add(plotRange, 0, 0);
        } else if (plotFace == BlockFace.EAST) {
            if (location.getX() == -location.getZ())
                location.add(plotRange, 0, 0);
            else if (location.getX() == location.getZ())
                location.subtract(plotRange, 0, 0);
            else
                location.add(0, 0, plotRange);
        } else if (plotFace == BlockFace.SOUTH) {
            if (location.getX() == -location.getZ())
                location.subtract(0, 0, plotRange);
            else
                location.subtract(plotRange, 0, 0);
        } else if (plotFace == BlockFace.WEST) {
            if (location.getX() == location.getZ())
                location.add(plotRange, 0, 0);
            else
                location.subtract(0, 0, plotRange);
        }

        BlockPosition blockPosition = new SBlockPosition(location);

        if (servedPositions.contains(blockPosition) || plugin.getGrid().getPlotAt(location) != null) {
            return getNextLocation(location.clone(), plotsHeight, maxPlotSize, plotOwner, plotUUID);
        }

        servedPositions.add(blockPosition);

        return location;
    }

    @Override
    public void finishPlotCreation(Location plotLocation, UUID plotOwner, UUID plotUUID) {
        Preconditions.checkNotNull(plotLocation, "plotLocation parameter cannot be null.");
        servedPositions.remove(new SBlockPosition(plotLocation));
    }

    @Override
    public void prepareTeleport(Plot plot, Location location, Runnable finishCallback) {
        finishCallback.run();
    }

    @Override
    public boolean isNormalEnabled() {
        return plugin.getSettings().getWorlds().getNormal().isEnabled();
    }

    @Override
    public boolean isNormalUnlocked() {
        return isNormalEnabled() && plugin.getSettings().getWorlds().getNormal().isUnlocked();
    }

    @Override
    public boolean isNetherEnabled() {
        return plugin.getSettings().getWorlds().getNether().isEnabled();
    }

    @Override
    public boolean isNetherUnlocked() {
        return isNetherEnabled() && plugin.getSettings().getWorlds().getNether().isUnlocked();
    }

    @Override
    public boolean isEndEnabled() {
        return plugin.getSettings().getWorlds().getEnd().isEnabled();
    }

    @Override
    public boolean isEndUnlocked() {
        return isEndEnabled() && plugin.getSettings().getWorlds().getEnd().isUnlocked();
    }

    private BlockFace getPlotFace(Location location) {
        //Possibilities: North / East
        if (location.getX() >= location.getZ()) {
            return -location.getX() > location.getZ() ? BlockFace.NORTH : BlockFace.EAST;
        }
        //Possibilities: South / West
        else {
            return -location.getX() > location.getZ() ? BlockFace.WEST : BlockFace.SOUTH;
        }
    }

    private World loadWorld(String worldName, Difficulty difficulty, World.Environment environment) {
        if (Bukkit.getWorld(worldName) != null) {
            throw new RuntimeException("The world " + worldName + " is already loaded. This can occur by one of the following reasons:\n" +
                    "- Another plugin loaded it manually before SuperiorSkyblock.\n" +
                    "- Your level-name property in server.properties is set to " + worldName + ".");
        }

        World world = WorldCreator.name(worldName)
                .type(WorldType.NORMAL)
                .environment(environment)
                .generator(plugin.getGenerator())
                .createWorld();

        world.setDifficulty(difficulty);
        plotWorlds.put(environment, world);

        plugin.getNMSWorld().removeAntiXray(world);

        if (Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + worldName + " normal -g " + plugin.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv modify set generator " + plugin.getName() + " " + worldName);
        }

        return world;
    }

}

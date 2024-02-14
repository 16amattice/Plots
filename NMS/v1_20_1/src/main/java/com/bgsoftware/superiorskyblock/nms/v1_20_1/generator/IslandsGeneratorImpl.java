package com.bgsoftware.superiorskyblock.nms.v1_20_1.generator;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.world.generator.PlotsGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"unused", "NullableProblems"})
public class PlotsGeneratorImpl extends PlotsGenerator {

    private final SuperiorSkyblockPlugin plugin;

    public PlotsGeneratorImpl(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                @NotNull ChunkData chunkData) {
        if (chunkX == 0 && chunkZ == 0 && worldInfo.getEnvironment() ==
                plugin.getSettings().getWorlds().getDefaultWorld()) {
            chunkData.setBlock(0, 99, 0, Material.BEDROCK);
        }
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new BiomeProvider() {
            @Override
            public @NotNull
            Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                return PlotUtils.getDefaultWorldBiome(worldInfo.getEnvironment());
            }

            @Override
            public @NotNull
            List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                return PlotUtils.getDefaultWorldBiomes();
            }

        };
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 100, 0);
    }

}

package com.bgsoftware.superiorskyblock.nms.v1_18;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.common.reflection.ReflectField;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.nms.NMSDragonFight;
import com.bgsoftware.superiorskyblock.nms.v1_18.dragon.EndWorldEndDragonFightHandler;
import com.bgsoftware.superiorskyblock.nms.v1_18.dragon.PlotEndDragonFight;
import com.bgsoftware.superiorskyblock.nms.v1_18.dragon.PlotEntityEnderDragon;
import com.bgsoftware.superiorskyblock.nms.v1_18.dragon.SpikesCache;
import com.google.common.cache.LoadingCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;

import java.lang.reflect.Modifier;
import java.util.List;

public class NMSDragonFightImpl implements NMSDragonFight {

    private static final ReflectField<EntityType.EntityFactory<?>> ENTITY_TYPES_BUILDER = new ReflectField<EntityType.EntityFactory<?>>(
            EntityType.class, EntityType.EntityFactory.class, Modifier.PRIVATE | Modifier.FINAL, 1)
            .removeFinal();

    private static final ReflectField<EndDragonFight> WORLD_DRAGON_BATTLE = new ReflectField<EndDragonFight>(
            ServerLevel.class, EndDragonFight.class, Modifier.PRIVATE | Modifier.FINAL, 1)
            .removeFinal();

    private static final ReflectField<LoadingCache<Long, List<SpikeFeature.EndSpike>>> SPIKE_CACHE = new ReflectField<LoadingCache<Long, List<SpikeFeature.EndSpike>>>(
            SpikeFeature.class, LoadingCache.class, Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL, 1)
            .removeFinal();

    private static boolean firstWorldPreparation = true;

    static {
        ENTITY_TYPES_BUILDER.set(EntityType.ENDER_DRAGON, (EntityType.EntityFactory<EnderDragon>) PlotEntityEnderDragon::fromEntityTypes);
    }

    @Override
    public void prepareEndWorld(World bukkitWorld) {
        ServerLevel serverLevel = ((CraftWorld) bukkitWorld).getHandle();
        WORLD_DRAGON_BATTLE.set(serverLevel, new EndWorldEndDragonFightHandler(serverLevel));

        if (firstWorldPreparation) {
            firstWorldPreparation = false;
            SPIKE_CACHE.set(null, SpikesCache.getInstance());
        }
    }

    @Nullable
    @Override
    public org.bukkit.entity.EnderDragon getEnderDragon(Plot plot) {
        World bukkitWorld = plot.getCenter(World.Environment.THE_END).getWorld();

        if (bukkitWorld == null)
            return null;

        ServerLevel serverLevel = ((CraftWorld) bukkitWorld).getHandle();

        if (!(serverLevel.dragonFight() instanceof EndWorldEndDragonFightHandler dragonFightHandler))
            return null;

        PlotEndDragonFight enderDragonBattle = dragonFightHandler.getDragonFight(plot.getUniqueId());
        return enderDragonBattle == null ? null : enderDragonBattle.getEnderDragon().getBukkitEntity();
    }

    @Override
    public void startDragonBattle(Plot plot, Location location) {
        World bukkitWorld = location.getWorld();

        if (bukkitWorld == null)
            return;

        ServerLevel serverLevel = ((CraftWorld) bukkitWorld).getHandle();

        if (!(serverLevel.dragonFight() instanceof EndWorldEndDragonFightHandler dragonFightHandler))
            return;

        dragonFightHandler.addDragonFight(plot.getUniqueId(), new PlotEndDragonFight(plot, serverLevel, location));
    }

    @Override
    public void removeDragonBattle(Plot plot) {
        World bukkitWorld = plot.getCenter(World.Environment.THE_END).getWorld();

        if (bukkitWorld == null)
            return;

        ServerLevel serverLevel = ((CraftWorld) bukkitWorld).getHandle();

        if (!(serverLevel.dragonFight() instanceof EndWorldEndDragonFightHandler dragonFightHandler))
            return;

        EndDragonFight endDragonFight = dragonFightHandler.removeDragonFight(plot.getUniqueId());

        if (endDragonFight instanceof PlotEndDragonFight plotEndDragonFight) {
            plotEndDragonFight.removeBattlePlayers();
            plotEndDragonFight.getEnderDragon().getBukkitEntity().remove();
        }
    }

    @Override
    public void awardTheEndAchievement(Player player) {
        Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft("end/root"));
        if (advancement != null)
            player.getAdvancementProgress(advancement).awardCriteria("");
    }

}

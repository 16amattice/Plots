package com.bgsoftware.superiorskyblock.plot.flag;

import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;

public class PlotFlags {

    public static final PlotFlag ALWAYS_DAY = register("ALWAYS_DAY");
    public static final PlotFlag ALWAYS_MIDDLE_DAY = register("ALWAYS_MIDDLE_DAY");
    public static final PlotFlag ALWAYS_NIGHT = register("ALWAYS_NIGHT");
    public static final PlotFlag ALWAYS_MIDDLE_NIGHT = register("ALWAYS_MIDDLE_NIGHT");
    public static final PlotFlag ALWAYS_RAIN = register("ALWAYS_RAIN");
    public static final PlotFlag ALWAYS_SHINY = register("ALWAYS_SHINY");
    public static final PlotFlag CREEPER_EXPLOSION = register("CREEPER_EXPLOSION");
    public static final PlotFlag CROPS_GROWTH = register("CROPS_GROWTH");
    public static final PlotFlag EGG_LAY = register("EGG_LAY");
    public static final PlotFlag ENDERMAN_GRIEF = register("ENDERMAN_GRIEF");
    public static final PlotFlag FIRE_SPREAD = register("FIRE_SPREAD");
    public static final PlotFlag GHAST_FIREBALL = register("GHAST_FIREBALL");
    public static final PlotFlag LAVA_FLOW = register("LAVA_FLOW");
    public static final PlotFlag NATURAL_ANIMALS_SPAWN = register("NATURAL_ANIMALS_SPAWN");
    public static final PlotFlag NATURAL_MONSTER_SPAWN = register("NATURAL_MONSTER_SPAWN");
    public static final PlotFlag PVP = register("PVP");
    public static final PlotFlag SPAWNER_ANIMALS_SPAWN = register("SPAWNER_ANIMALS_SPAWN");
    public static final PlotFlag SPAWNER_MONSTER_SPAWN = register("SPAWNER_MONSTER_SPAWN");
    public static final PlotFlag TNT_EXPLOSION = register("TNT_EXPLOSION");
    public static final PlotFlag TREE_GROWTH = register("TREE_GROWTH");
    public static final PlotFlag WATER_FLOW = register("WATER_FLOW");
    public static final PlotFlag WITHER_EXPLOSION = register("WITHER_EXPLOSION");

    private PlotFlags() {

    }

    public static void registerFlags() {
        // Do nothing, only trigger all the register calls
    }

    private static PlotFlag register(String name) {
        PlotFlag.register(name);
        return PlotFlag.getByName(name);
    }

}

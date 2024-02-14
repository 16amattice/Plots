package com.bgsoftware.superiorskyblock.plot.privilege;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.core.ServerVersion;

import java.util.Objects;

public class PlotPrivileges {

    public static final PlotPrivilege ALL = register("ALL");
    public static final PlotPrivilege ANIMAL_BREED = register("ANIMAL_BREED");
    public static final PlotPrivilege ANIMAL_DAMAGE = register("ANIMAL_DAMAGE");
    public static final PlotPrivilege ANIMAL_SHEAR = register("ANIMAL_SHEAR");
    public static final PlotPrivilege ANIMAL_SPAWN = register("ANIMAL_SPAWN");
    public static final PlotPrivilege BAN_MEMBER = register("BAN_MEMBER");
    public static final PlotPrivilege BREAK = register("BREAK");
    public static final PlotPrivilege BUILD = register("BUILD");
    public static final PlotPrivilege CHANGE_NAME = register("CHANGE_NAME");
    public static final PlotPrivilege CHEST_ACCESS = register("CHEST_ACCESS");
    public static final PlotPrivilege CLOSE_BYPASS = register("CLOSE_BYPASS");
    public static final PlotPrivilege CLOSE_PLOT = register("CLOSE_PLOT");
    public static final PlotPrivilege COOP_MEMBER = register("COOP_MEMBER");
    public static final PlotPrivilege DELETE_WARP = register("DELETE_WARP");
    public static final PlotPrivilege DEMOTE_MEMBERS = register("DEMOTE_MEMBERS");
    public static final PlotPrivilege DEPOSIT_MONEY = register("DEPOSIT_MONEY");
    public static final PlotPrivilege DISBAND_PLOT = register("DISBAND_PLOT");
    public static final PlotPrivilege DISCORD_SHOW = register("DISCORD_SHOW");
    public static final PlotPrivilege DROP_ITEMS = register("DROP_ITEMS");
    public static final PlotPrivilege ENDER_PEARL = register("ENDER_PEARL");
    public static final PlotPrivilege EXPEL_BYPASS = register("EXPEL_BYPASS");
    public static final PlotPrivilege EXPEL_PLAYERS = register("EXPEL_PLAYERS");
    public static final PlotPrivilege FARM_TRAMPING = register("FARM_TRAMPING");
    public static final PlotPrivilege FERTILIZE = register("FERTILIZE");
    public static final PlotPrivilege FISH = register("FISH");
    public static final PlotPrivilege FLY = register("FLY");
    public static final PlotPrivilege HORSE_INTERACT = register("HORSE_INTERACT");
    public static final PlotPrivilege IGNITE_CREEPER = register("IGNITE_CREEPER");
    public static final PlotPrivilege INTERACT = register("INTERACT");
    public static final PlotPrivilege INVITE_MEMBER = register("INVITE_MEMBER");
    public static final PlotPrivilege PLOT_CHEST = register("PLOT_CHEST");
    public static final PlotPrivilege ITEM_FRAME = register("ITEM_FRAME");
    public static final PlotPrivilege KICK_MEMBER = register("KICK_MEMBER");
    public static final PlotPrivilege LEASH = register("LEASH");
    public static final PlotPrivilege MINECART_DAMAGE = register("MINECART_DAMAGE");
    public static final PlotPrivilege MINECART_ENTER = register("MINECART_ENTER");
    public static final PlotPrivilege MINECART_OPEN = register("MINECART_OPEN");
    public static final PlotPrivilege MINECART_PLACE = register("MINECART_PLACE");
    public static final PlotPrivilege MONSTER_DAMAGE = register("MONSTER_DAMAGE");
    public static final PlotPrivilege MONSTER_SPAWN = register("MONSTER_SPAWN");
    public static final PlotPrivilege NAME_ENTITY = register("NAME_ENTITY");
    public static final PlotPrivilege OPEN_PLOT = register("OPEN_PLOT");
    public static final PlotPrivilege PAINTING = register("PAINTING");
    public static final PlotPrivilege PAYPAL_SHOW = register("PAYPAL_SHOW");
    @Nullable
    public static final PlotPrivilege PICKUP_AXOLOTL = register("PICKUP_AXOLOTL", ServerVersion.isAtLeast(ServerVersion.v1_17));
    public static final PlotPrivilege PICKUP_DROPS = register("PICKUP_DROPS");
    @Nullable
    public static final PlotPrivilege PICKUP_FISH = register("PICKUP_FISH", !ServerVersion.isLegacy());
    @Nullable
    public static final PlotPrivilege PICKUP_LECTERN_BOOK = register("PICKUP_LECTERN_BOOK", ServerVersion.isAtLeast(ServerVersion.v1_14));
    public static final PlotPrivilege PROMOTE_MEMBERS = register("PROMOTE_MEMBERS");
    public static final PlotPrivilege RANKUP = register("RANKUP");
    public static final PlotPrivilege RATINGS_SHOW = register("RATINGS_SHOW");
    public static final PlotPrivilege SET_BIOME = register("SET_BIOME");
    public static final PlotPrivilege SET_DISCORD = register("SET_DISCORD");
    public static final PlotPrivilege SET_HOME = register("SET_HOME");
    public static final PlotPrivilege SET_PAYPAL = register("SET_PAYPAL");
    public static final PlotPrivilege SET_PERMISSION = register("SET_PERMISSION");
    public static final PlotPrivilege SET_ROLE = register("SET_ROLE");
    public static final PlotPrivilege SET_SETTINGS = register("SET_SETTINGS");
    public static final PlotPrivilege SET_WARP = register("SET_WARP");
    public static final PlotPrivilege SIGN_INTERACT = register("SIGN_INTERACT");
    public static final PlotPrivilege SPAWNER_BREAK = register("SPAWNER_BREAK");
    @Nullable
    public static final PlotPrivilege TURTLE_EGG_TRAMPING = register("TURTLE_EGG_TRAMPING", !ServerVersion.isLegacy());
    public static final PlotPrivilege UNCOOP_MEMBER = register("UNCOOP_MEMBER");
    public static final PlotPrivilege USE = register("USE");
    public static final PlotPrivilege VALUABLE_BREAK = register("VALUABLE_BREAK");
    public static final PlotPrivilege VILLAGER_TRADING = register("VILLAGER_TRADING");
    public static final PlotPrivilege WITHDRAW_MONEY = register("WITHDRAW_MONEY");

    private PlotPrivileges() {

    }

    public static void registerPrivileges() {
        // Do nothing, only trigger all the register calls
    }

    @NotNull
    private static PlotPrivilege register(String name) {
        return Objects.requireNonNull(register(name, true));
    }

    @Nullable
    private static PlotPrivilege register(String name, boolean registerCheck) {
        if (!registerCheck)
            return null;

        PlotPrivilege.register(name);
        return PlotPrivilege.getByName(name);
    }

}

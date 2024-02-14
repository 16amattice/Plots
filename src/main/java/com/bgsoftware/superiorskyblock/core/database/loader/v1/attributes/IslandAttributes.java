package com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes;

public class PlotAttributes extends AttributesRegistry<PlotAttributes.Field> {

    public PlotAttributes() {
        super(Field.class);
    }

    @Override
    public PlotAttributes setValue(Field field, Object value) {
        return (PlotAttributes) super.setValue(field, value);
    }

    public enum Field {

        UUID,
        OWNER,
        CENTER,
        CREATION_TIME,
        PLOT_TYPE,
        DISCORD,
        PAYPAL,
        WORTH_BONUS,
        LEVELS_BONUS,
        LOCKED,
        IGNORED,
        NAME,
        DESCRIPTION,
        GENERATED_SCHEMATICS,
        UNLOCKED_WORLDS,
        LAST_TIME_UPDATED,
        DIRTY_CHUNKS,
        BLOCK_COUNTS,
        HOMES,
        MEMBERS,
        BANS,
        PLAYER_PERMISSIONS,
        ROLE_PERMISSIONS,
        UPGRADES,
        WARPS,
        BLOCK_LIMITS,
        RATINGS,
        MISSIONS,
        PLOT_FLAGS,
        GENERATORS,
        VISITORS,
        ENTITY_LIMITS,
        EFFECTS,
        PLOT_CHESTS,
        ROLE_LIMITS,
        WARP_CATEGORIES,
        BANK_BALANCE,
        BANK_LAST_INTEREST,
        VISITOR_HOMES,
        PLOT_SIZE,
        TEAM_LIMIT,
        WARPS_LIMIT,
        CROP_GROWTH_MULTIPLIER,
        SPAWNER_RATES_MULTIPLIER,
        MOB_DROPS_MULTIPLIER,
        COOP_LIMIT,
        BANK_LIMIT
    }

}

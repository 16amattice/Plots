package com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes;

public class PlotWarpAttributes extends AttributesRegistry<PlotWarpAttributes.Field> {

    public PlotWarpAttributes() {
        super(Field.class);
    }

    @Override
    public PlotWarpAttributes setValue(Field field, Object value) {
        return (PlotWarpAttributes) super.setValue(field, value);
    }

    public enum Field {

        NAME,
        CATEGORY,
        LOCATION,
        PRIVATE_STATUS,
        ICON

    }

}

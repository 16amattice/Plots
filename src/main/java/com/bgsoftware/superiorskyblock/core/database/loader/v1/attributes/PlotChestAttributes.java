package com.bgsoftware.superiorskyblock.core.database.loader.v1.attributes;

public class PlotChestAttributes extends AttributesRegistry<PlotChestAttributes.Field> {

    public PlotChestAttributes() {
        super(Field.class);
    }

    @Override
    public PlotChestAttributes setValue(Field field, Object value) {
        return (PlotChestAttributes) super.setValue(field, value);
    }

    public enum Field {

        INDEX,
        CONTENTS

    }

}

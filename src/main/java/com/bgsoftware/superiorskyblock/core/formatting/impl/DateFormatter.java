package com.bgsoftware.superiorskyblock.core.formatting.impl;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.core.formatting.IFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter implements IFormatter<Date> {

    private static final DateFormatter INSTANCE = new DateFormatter();

    private static SimpleDateFormat dateFormatter;

    public static void setDateFormatter(SuperiorSkyblockPlugin plugin, String dateFormat) {
        dateFormatter = new SimpleDateFormat(dateFormat);
        try {
            for (Plot plot : plugin.getGrid().getPlots()) {
                plot.updateDatesFormatter();
            }
        } catch (Exception ignored) {
        }
    }

    public static DateFormatter getInstance() {
        return INSTANCE;
    }

    private DateFormatter() {

    }

    @Override
    public String format(Date value) {
        return dateFormatter.format(value);
    }

}

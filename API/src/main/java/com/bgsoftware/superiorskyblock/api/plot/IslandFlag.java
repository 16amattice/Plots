package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.objects.Enumerable;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlotFlag implements Enumerable {

    private static final Map<String, PlotFlag> plotFlags = new HashMap<>();
    private static int ordinalCounter = 0;

    private final String name;
    private final int ordinal;

    private PlotFlag(String name) {
        this.name = name.toUpperCase(Locale.ENGLISH);
        this.ordinal = ordinalCounter++;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    /**
     * Get all the plot flags.
     */
    public static Collection<PlotFlag> values() {
        return plotFlags.values();
    }

    /**
     * Get an plot flag by it's name.
     *
     * @param name The name to check.
     */
    public static PlotFlag getByName(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        PlotFlag plotFlag = plotFlags.get(name.toUpperCase(Locale.ENGLISH));

        Preconditions.checkNotNull(plotFlag, "Couldn't find an PlotFlag with the name " + name + ".");

        return plotFlag;
    }

    /**
     * Register a new plot flag.
     *
     * @param name The name for the plot flag.
     */
    public static void register(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        name = name.toUpperCase(Locale.ENGLISH);

        Preconditions.checkState(!plotFlags.containsKey(name), "PlotFlag with the name " + name + " already exists.");

        plotFlags.put(name, new PlotFlag(name));
    }

    /**
     * Get the name of the plot flag.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PlotFlag{name=" + name + "}";
    }

}

package com.bgsoftware.superiorskyblock.api.plot;

import com.bgsoftware.superiorskyblock.api.objects.Enumerable;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlotPrivilege implements Enumerable {

    private static final Map<String, PlotPrivilege> plotPrivileges = new HashMap<>();
    private static int ordinalCounter = 0;

    private final String name;
    private final int ordinal;

    private PlotPrivilege(String name) {
        this.name = name.toUpperCase(Locale.ENGLISH);
        this.ordinal = ordinalCounter++;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    /**
     * Get all the plot privileges.
     */
    public static Collection<PlotPrivilege> values() {
        return plotPrivileges.values();
    }

    /**
     * Get an plot privilege by it's name.
     *
     * @param name The name to check.
     */
    public static PlotPrivilege getByName(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        PlotPrivilege plotPrivilege = plotPrivileges.get(name.toUpperCase(Locale.ENGLISH));

        Preconditions.checkNotNull(plotPrivilege, "Couldn't find an PlotPrivilege with the name " + name + ".");

        return plotPrivilege;
    }

    /**
     * Register a new plot privilege.
     *
     * @param name The name for the plot privilege.
     */
    public static void register(String name) {
        Preconditions.checkNotNull(name, "name parameter cannot be null.");

        name = name.toUpperCase(Locale.ENGLISH);

        Preconditions.checkState(!plotPrivileges.containsKey(name), "PlotPrivilege with the name " + name + " already exists.");

        plotPrivileges.put(name, new PlotPrivilege(name));
    }

    /**
     * Get the name of the plot privilege.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PlotPrivilege{name=" + name + "}";
    }

}

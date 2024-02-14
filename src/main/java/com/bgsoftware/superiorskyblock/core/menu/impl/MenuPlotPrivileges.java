package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PlotPrivilegePagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MenuPlotPrivileges extends AbstractPagedMenu<
        MenuPlotPrivileges.View, MenuPlotPrivileges.Args, MenuPlotPrivileges.PlotPrivilegeInfo> {

    private final List<MenuPlotPrivileges.PlotPrivilegeInfo> plotPrivileges;
    private final String noRolePermission;
    private final String exactRolePermission;
    private final String higherRolePermission;

    private MenuPlotPrivileges(MenuParseResult<View> parseResult, List<PlotPrivilegeInfo> plotPrivileges,
                                 String noRolePermission, String exactRolePermission, String higherRolePermission) {
        super(MenuIdentifiers.MENU_PLOT_PRIVILEGES, parseResult, false);
        this.plotPrivileges = plotPrivileges;
        this.noRolePermission = noRolePermission;
        this.exactRolePermission = exactRolePermission;
        this.higherRolePermission = higherRolePermission;
    }

    public String getNoRolePermission() {
        return noRolePermission;
    }

    public String getExactRolePermission() {
        return exactRolePermission;
    }

    public String getHigherRolePermission() {
        return higherRolePermission;
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, Args args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Plot plot) {
        refreshViews(view -> view.plot.equals(plot));
    }

    public void refreshViews(Plot plot, Object permissionHolder) {
        refreshViews(view -> view.plot.equals(plot) && view.permissionHolder.equals(permissionHolder));
    }

    @Nullable
    public static MenuPlotPrivileges createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("permissions.yml",
                MenuPlotPrivileges::convertOldGUI, new PlotPrivilegePagedObjectButton.Builder());

        if (menuParseResult == null)
            return null;

        YamlConfiguration cfg = menuParseResult.getConfig();

        String noRolePermission = cfg.getString("messages.no-role-permission", "");
        String exactRolePermission = cfg.getString("messages.exact-role-permission", "");
        String higherRolePermission = cfg.getString("messages.higher-role-permission", "");

        List<MenuPlotPrivileges.PlotPrivilegeInfo> plotPrivileges = new LinkedList<>();

        Optional.ofNullable(cfg.getConfigurationSection("permissions")).ifPresent(permissionsSection -> {
            for (String plotPrivilegeName : permissionsSection.getKeys(false)) {
                Optional.ofNullable(permissionsSection.getConfigurationSection(plotPrivilegeName)).ifPresent(plotPrivilegeSection -> {
                    if (plotPrivilegeSection.getBoolean("display-menu", true)) {
                        plotPrivileges.add(loadPlotPrivilegeInfo(plotPrivilegeSection, plotPrivilegeName, plotPrivileges.size()));
                    }
                });
            }
        });

        return new MenuPlotPrivileges(menuParseResult, plotPrivileges, noRolePermission,
                exactRolePermission, higherRolePermission);
    }

    private static PlotPrivilegeInfo loadPlotPrivilegeInfo(ConfigurationSection plotPrivilegeSection, String plotPrivilegeName, int position) {
        TemplateItem enabledPlotPrivilegeItem = null;
        TemplateItem disabledPlotPrivilegeItem = null;
        TemplateItem rolePrivilegeItem = null;
        GameSound accessSound = null;
        GameSound noAccessSound = null;
        List<String> accessCommands = null;
        List<String> noAccessCommands = null;

        if (plotPrivilegeSection != null) {
            enabledPlotPrivilegeItem = MenuParserImpl.getInstance().getItemStack("permissions.yml",
                    plotPrivilegeSection.getConfigurationSection("permission-enabled"));
            disabledPlotPrivilegeItem = MenuParserImpl.getInstance().getItemStack("permissions.yml",
                    plotPrivilegeSection.getConfigurationSection("permission-disabled"));
            rolePrivilegeItem = MenuParserImpl.getInstance().getItemStack("permissions.yml",
                    plotPrivilegeSection.getConfigurationSection("role-permission"));
            accessSound = MenuParserImpl.getInstance().getSound(plotPrivilegeSection.getConfigurationSection("has-access.sound"));
            noAccessSound = MenuParserImpl.getInstance().getSound(plotPrivilegeSection.getConfigurationSection("no-access.sound"));
            accessCommands = plotPrivilegeSection.getStringList("has-access.commands");
            noAccessCommands = plotPrivilegeSection.getStringList("no-access.commands");
        }

        return new MenuPlotPrivileges.PlotPrivilegeInfo(plotPrivilegeName, enabledPlotPrivilegeItem,
                disabledPlotPrivilegeItem, rolePrivilegeItem, accessSound, noAccessSound, accessCommands,
                noAccessCommands, position);
    }

    public static class Args implements ViewArgs {

        private final Plot plot;
        private final Object permissionHolder;

        public Args(Plot plot, Object permissionHolder) {
            this.plot = plot;
            this.permissionHolder = permissionHolder;
        }

    }

    public class View extends AbstractPagedMenuView<MenuPlotPrivileges.View, MenuPlotPrivileges.Args, PlotPrivilegeInfo> {

        private final Plot plot;
        private final Object permissionHolder;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, MenuPlotPrivileges.Args> menu, Args args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plot = args.plot;
            this.permissionHolder = args.permissionHolder;
        }

        public Plot getPlot() {
            return plot;
        }

        public Object getPermissionHolder() {
            return permissionHolder;
        }

        @Override
        protected List<PlotPrivilegeInfo> requestObjects() {
            return Collections.unmodifiableList(plotPrivileges);
        }

    }

    public static class PlotPrivilegeInfo implements Comparable<PlotPrivilegeInfo> {

        private final LazyReference<PlotPrivilege> plotPrivilege = new LazyReference<PlotPrivilege>() {
            @Override
            protected PlotPrivilege create() {
                try {
                    return PlotPrivilege.getByName(PlotPrivilegeInfo.this.plotPrivilegeName);
                } catch (Exception error) {
                    return null;
                }
            }
        };

        private final String plotPrivilegeName;
        private final TemplateItem enabledPlotPrivilegeItem;
        private final TemplateItem disabledPlotPrivilegeItem;
        private final TemplateItem rolePlotPrivilegeItem;
        private final GameSound accessSound;
        private final GameSound noAccessSound;
        private final List<String> accessCommands;
        private final List<String> noAccessCommands;
        private final int position;

        public PlotPrivilegeInfo(String plotPrivilegeName, TemplateItem enabledPlotPrivilegeItem,
                                   TemplateItem disabledPlotPrivilegeItem, TemplateItem rolePlotPrivilegeItem,
                                   GameSound accessSound, GameSound noAccessSound, List<String> accessCommands,
                                   List<String> noAccessCommands, int position) {
            this.plotPrivilegeName = plotPrivilegeName;
            this.enabledPlotPrivilegeItem = enabledPlotPrivilegeItem;
            this.disabledPlotPrivilegeItem = disabledPlotPrivilegeItem;
            this.rolePlotPrivilegeItem = rolePlotPrivilegeItem;
            this.accessSound = accessSound;
            this.noAccessSound = noAccessSound;
            this.accessCommands = accessCommands == null ? Collections.emptyList() : accessCommands;
            this.noAccessCommands = noAccessCommands == null ? Collections.emptyList() : noAccessCommands;
            this.position = position;
        }

        public PlotPrivilege getPlotPrivilege() {
            return plotPrivilege.get();
        }

        public ItemBuilder getEnabledPlotPrivilegeItem() {
            return enabledPlotPrivilegeItem.getBuilder();
        }

        public ItemBuilder getDisabledPlotPrivilegeItem() {
            return disabledPlotPrivilegeItem.getBuilder();
        }

        @Nullable
        public ItemBuilder getRolePlotPrivilegeItem() {
            return rolePlotPrivilegeItem == null ? null : rolePlotPrivilegeItem.getBuilder();
        }

        @Nullable
        public GameSound getAccessSound() {
            return accessSound;
        }

        @Nullable
        public GameSound getNoAccessSound() {
            return noAccessSound;
        }

        public List<String> getAccessCommands() {
            return accessCommands;
        }

        public List<String> getNoAccessCommands() {
            return noAccessCommands;
        }

        @Override
        public int compareTo(@NotNull MenuPlotPrivileges.PlotPrivilegeInfo other) {
            return Integer.compare(position, other.position);
        }

    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/permissions-gui.yml");

        if (!oldFile.exists())
            return false;

        //We want to reset the items of newMenu.
        ConfigurationSection itemsSection = newMenu.createSection("items");
        ConfigurationSection soundsSection = newMenu.createSection("sounds");
        ConfigurationSection commandsSection = newMenu.createSection("commands");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(oldFile);

        newMenu.set("title", cfg.getString("permissions-gui.title"));

        int size = cfg.getInt("permissions-gui.size");

        char[] patternChars = new char[size * 9];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("permissions-gui.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("permissions-gui.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        char slotsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];

        MenuConverter.convertPagedButtons(cfg.getConfigurationSection("permissions-gui"), newMenu,
                patternChars, slotsChar, AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++], AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                itemsSection, commandsSection, soundsSection);

        newMenu.set("permissions", cfg.getConfigurationSection("permissions-gui.permissions"));
        newMenu.set("sounds", null);
        newMenu.set("commands", null);

        newMenu.set("pattern", MenuConverter.buildPattern(size, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }

}

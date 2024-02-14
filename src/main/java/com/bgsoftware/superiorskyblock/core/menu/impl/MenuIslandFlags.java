package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.LazyReference;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PlotFlagPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MenuPlotFlags extends AbstractPagedMenu<MenuPlotFlags.View, PlotViewArgs, MenuPlotFlags.PlotFlagInfo> {

    private final List<MenuPlotFlags.PlotFlagInfo> plotFlags;

    private MenuPlotFlags(MenuParseResult<View> parseResult, List<MenuPlotFlags.PlotFlagInfo> plotFlags) {
        super(MenuIdentifiers.MENU_PLOT_FLAGS, parseResult, false);
        this.plotFlags = plotFlags;
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, PlotViewArgs args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Plot plot) {
        refreshViews(view -> view.plot.equals(plot));
    }

    @Nullable
    public static MenuPlotFlags createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("settings.yml",
                MenuPlotFlags::convertOldGUI, new PlotFlagPagedObjectButton.Builder());

        if (menuParseResult == null) {
            return null;
        }

        YamlConfiguration cfg = menuParseResult.getConfig();

        List<MenuPlotFlags.PlotFlagInfo> plotFlags = new LinkedList<>();

        Optional.ofNullable(cfg.getConfigurationSection("settings")).ifPresent(settingsSection -> {
            for (String plotFlagName : settingsSection.getKeys(false)) {
                Optional.ofNullable(settingsSection.getConfigurationSection(plotFlagName)).ifPresent(plotFlagSection -> {
                    plotFlags.add(loadPlotFlagInfo(plotFlagSection, plotFlagName, plotFlags.size()));
                });
            }
        });

        return new MenuPlotFlags(menuParseResult, plotFlags);
    }

    private static PlotFlagInfo loadPlotFlagInfo(ConfigurationSection plotFlagSection, String plotFlagName, int position) {
        TemplateItem enabledPlotFlagItem = null;
        TemplateItem disabledPlotFlagItem = null;
        GameSound clickSound = null;

        if (plotFlagSection != null) {
            enabledPlotFlagItem = MenuParserImpl.getInstance().getItemStack("settings.yml",
                    plotFlagSection.getConfigurationSection("settings-enabled"));
            disabledPlotFlagItem = MenuParserImpl.getInstance().getItemStack("settings.yml",
                    plotFlagSection.getConfigurationSection("settings-disabled"));
            clickSound = MenuParserImpl.getInstance().getSound(plotFlagSection.getConfigurationSection("sound"));
        }

        return new MenuPlotFlags.PlotFlagInfo(plotFlagName, enabledPlotFlagItem,
                disabledPlotFlagItem, clickSound, position);
    }

    public class View extends AbstractPagedMenuView<MenuPlotFlags.View, PlotViewArgs, PlotFlagInfo> {

        private final Plot plot;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, PlotViewArgs> menu, PlotViewArgs args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plot = args.getPlot();
        }

        public Plot getPlot() {
            return plot;
        }

        @Override
        protected List<PlotFlagInfo> requestObjects() {
            return Collections.unmodifiableList(plotFlags);
        }

    }

    public static class PlotFlagInfo implements Comparable<MenuPlotFlags.PlotFlagInfo> {

        private final LazyReference<PlotFlag> plotFlag = new LazyReference<PlotFlag>() {
            @Override
            protected PlotFlag create() {
                try {
                    return PlotFlag.getByName(PlotFlagInfo.this.plotFlagName);
                } catch (Exception error) {
                    return null;
                }
            }
        };

        private final String plotFlagName;
        private final TemplateItem enabledPlotFlagItem;
        private final TemplateItem disabledPlotFlagItem;
        private final GameSound clickSound;
        private final int position;


        public PlotFlagInfo(String plotFlagName, TemplateItem enabledPlotFlagItem,
                              TemplateItem disabledPlotFlagItem, GameSound clickSound, int position) {
            this.plotFlagName = plotFlagName;
            this.enabledPlotFlagItem = enabledPlotFlagItem;
            this.disabledPlotFlagItem = disabledPlotFlagItem;
            this.clickSound = clickSound;
            this.position = position;
        }

        @Nullable
        public PlotFlag getPlotFlag() {
            return plotFlag.get();
        }

        public ItemBuilder getEnabledPlotFlagItem() {
            return enabledPlotFlagItem.getBuilder();
        }

        public ItemBuilder getDisabledPlotFlagItem() {
            return disabledPlotFlagItem.getBuilder();
        }

        public GameSound getClickSound() {
            return clickSound;
        }

        @Override
        public int compareTo(@NotNull MenuPlotFlags.PlotFlagInfo other) {
            return Integer.compare(position, other.position);
        }

    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/settings-gui.yml");

        if (!oldFile.exists())
            return false;

        //We want to reset the items of newMenu.
        ConfigurationSection itemsSection = newMenu.createSection("items");
        ConfigurationSection soundsSection = newMenu.createSection("sounds");
        ConfigurationSection commandsSection = newMenu.createSection("commands");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(oldFile);

        newMenu.set("title", cfg.getString("settings-gui.title"));

        int size = cfg.getInt("settings-gui.size");

        char[] patternChars = new char[size * 9];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("settings-gui.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("settings-gui.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        char slotsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];

        MenuConverter.convertPagedButtons(cfg.getConfigurationSection("settings-gui"), newMenu,
                patternChars, slotsChar, AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++], AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                itemsSection, commandsSection, soundsSection);

        newMenu.set("settings", cfg.getConfigurationSection("settings-gui.settings"));
        newMenu.set("sounds", null);
        newMenu.set("commands", null);

        newMenu.set("pattern", MenuConverter.buildPattern(size, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }


}

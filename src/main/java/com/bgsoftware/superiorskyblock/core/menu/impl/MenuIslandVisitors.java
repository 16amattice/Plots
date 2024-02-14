package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.OpenUniqueVisitorsButton;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.VisitorPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MenuPlotVisitors extends AbstractPagedMenu<MenuPlotVisitors.View, PlotViewArgs, SuperiorPlayer> {

    private MenuPlotVisitors(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_PLOT_VISITORS, parseResult, false);
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
    public static MenuPlotVisitors createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("visitors.yml",
                MenuPlotVisitors::convertOldGUI, new VisitorPagedObjectButton.Builder());

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<View> patternBuilder = menuParseResult.getLayoutBuilder();

        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "unique-visitors", menuPatternSlots),
                new OpenUniqueVisitorsButton.Builder());

        return new MenuPlotVisitors(menuParseResult);
    }

    public static class View extends AbstractPagedMenuView<View, PlotViewArgs, SuperiorPlayer> {

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
        protected List<SuperiorPlayer> requestObjects() {
            return plot.getPlotVisitors(false);
        }

    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/panel-gui.yml");

        if (!oldFile.exists())
            return false;

        //We want to reset the items of newMenu.
        ConfigurationSection itemsSection = newMenu.createSection("items");
        ConfigurationSection soundsSection = newMenu.createSection("sounds");
        ConfigurationSection commandsSection = newMenu.createSection("commands");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(oldFile);

        newMenu.set("title", cfg.getString("visitors-panel.title"));

        int size = cfg.getInt("visitors-panel.size");

        char[] patternChars = new char[size * 9];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("visitors-panel.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("visitors-panel.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        char slotsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];

        MenuConverter.convertPagedButtons(cfg.getConfigurationSection("visitors-panel"),
                cfg.getConfigurationSection("visitors-panel.visitor-item"), newMenu, patternChars,
                slotsChar, AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++], AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                itemsSection, commandsSection, soundsSection);

        newMenu.set("pattern", MenuConverter.buildPattern(size, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }

}

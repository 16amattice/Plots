package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.RatingsPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MenuPlotRatings extends AbstractPagedMenu<MenuPlotRatings.View, PlotViewArgs, MenuPlotRatings.RatingInfo> {

    private static final Function<Map.Entry<UUID, Rating>, RatingInfo> RATING_INFO_MAPPER =
            entry -> new RatingInfo(entry.getKey(), entry.getValue());

    private MenuPlotRatings(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_PLOT_RATINGS, parseResult, false);
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
    public static MenuPlotRatings createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("plot-ratings.yml",
                MenuPlotRatings::convertOldGUI, new RatingsPagedObjectButton.Builder());
        return menuParseResult == null ? null : new MenuPlotRatings(menuParseResult);
    }

    public static class View extends AbstractPagedMenuView<MenuPlotRatings.View, PlotViewArgs, MenuPlotRatings.RatingInfo> {

        private final Plot plot;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, PlotViewArgs> menu, PlotViewArgs args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plot = args.getPlot();
        }

        @Override
        protected List<RatingInfo> requestObjects() {
            return new SequentialListBuilder<RatingInfo>()
                    .build(plot.getRatings().entrySet(), RATING_INFO_MAPPER);
        }

    }

    public static class RatingInfo {

        private final UUID playerUUID;
        private final Rating rating;

        public RatingInfo(UUID playerUUID, Rating rating) {
            this.playerUUID = playerUUID;
            this.rating = rating;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public Rating getRating() {
            return rating;
        }

    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/ratings-gui.yml");

        if (!oldFile.exists())
            return false;

        //We want to reset the items of newMenu.
        ConfigurationSection itemsSection = newMenu.createSection("items");
        ConfigurationSection soundsSection = newMenu.createSection("sounds");
        ConfigurationSection commandsSection = newMenu.createSection("commands");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(oldFile);

        newMenu.set("title", cfg.getString("ratings-gui.title"));

        int size = cfg.getInt("ratings-gui.size");

        char[] patternChars = new char[size * 9];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("ratings-gui.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("ratings-gui.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        char slotsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];

        MenuConverter.convertPagedButtons(cfg.getConfigurationSection("ratings-gui"),
                cfg.getConfigurationSection("ratings-gui.rate-item"), newMenu, patternChars,
                slotsChar, AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++], AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++],
                itemsSection, commandsSection, soundsSection);

        newMenu.set("pattern", MenuConverter.buildPattern(size, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }


}

package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.enums.Rating;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.RatePlotButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;

public class MenuPlotRate extends AbstractMenu<PlotMenuView, PlotViewArgs> {

    private MenuPlotRate(MenuParseResult<PlotMenuView> parseResult) {
        super(MenuIdentifiers.MENU_PLOT_RATE, parseResult);
    }

    @Override
    protected PlotMenuView createViewInternal(SuperiorPlayer superiorPlayer, PlotViewArgs args,
                                                @Nullable MenuView<?, ?> previousMenuView) {
        return new PlotMenuView(superiorPlayer, previousMenuView, this, args);
    }

    @Nullable
    public static MenuPlotRate createInstance() {
        MenuParseResult<PlotMenuView> menuParseResult = MenuParserImpl.getInstance().loadMenu("plot-rate.yml",
                MenuPlotRate::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<PlotMenuView> patternBuilder = menuParseResult.getLayoutBuilder();

        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "zero-stars", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.ZERO_STARS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "one-star", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.ONE_STAR));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "two-stars", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.TWO_STARS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "three-stars", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.THREE_STARS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "four-stars", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.FOUR_STARS));
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "five-stars", menuPatternSlots),
                new RatePlotButton.Builder().setRating(Rating.FIVE_STARS));

        return new MenuPlotRate(menuParseResult);
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

        newMenu.set("title", cfg.getString("rate-gui.title"));
        newMenu.set("type", "HOPPER");

        char[] patternChars = new char[5];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("rate-gui.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("rate-gui.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        char oneStarChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];
        char twoStarsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];
        char threeStarsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];
        char fourStarsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];
        char fiveStarsChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];

        MenuConverter.convertItem(cfg.getConfigurationSection("rate-gui.one_star"), patternChars, oneStarChar,
                itemsSection, commandsSection, soundsSection);
        MenuConverter.convertItem(cfg.getConfigurationSection("rate-gui.two_stars"), patternChars, twoStarsChar,
                itemsSection, commandsSection, soundsSection);
        MenuConverter.convertItem(cfg.getConfigurationSection("rate-gui.three_stars"), patternChars, threeStarsChar,
                itemsSection, commandsSection, soundsSection);
        MenuConverter.convertItem(cfg.getConfigurationSection("rate-gui.four_stars"), patternChars, fourStarsChar,
                itemsSection, commandsSection, soundsSection);
        MenuConverter.convertItem(cfg.getConfigurationSection("rate-gui.five_stars"), patternChars, fiveStarsChar,
                itemsSection, commandsSection, soundsSection);

        newMenu.set("one-star", oneStarChar + "");
        newMenu.set("two-stars", twoStarsChar + "");
        newMenu.set("three-stars", threeStarsChar + "");
        newMenu.set("four-stars", fourStarsChar + "");
        newMenu.set("five-stars", fiveStarsChar + "");

        newMenu.set("pattern", MenuConverter.buildPattern(1, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }

}

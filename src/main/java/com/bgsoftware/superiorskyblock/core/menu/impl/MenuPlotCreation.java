package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PlotCreationButton;
import com.bgsoftware.superiorskyblock.core.menu.converter.MenuConverter;
import com.bgsoftware.superiorskyblock.core.menu.layout.AbstractMenuLayout;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MenuPlotCreation extends AbstractMenu<MenuPlotCreation.View, MenuPlotCreation.Args> {

    private MenuPlotCreation(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_PLOT_CREATION, parseResult);
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, Args args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, previousMenuView, this, args);
    }

    public void simulateClick(SuperiorPlayer clickedPlayer, String plotName, String schematic,
                              boolean isPreviewMode, @Nullable MenuView<?, ?> menuView) {
        menuLayout.getButtons().stream()
                .filter(button -> PlotCreationButton.class.equals(button.getViewButtonType()) &&
                        ((PlotCreationButton.Template) button).getSchematic().getName().equals(schematic))
                .map(button -> (PlotCreationButton.Template) button)
                .findFirst().ifPresent(template -> simulateClick(clickedPlayer, plotName, template, isPreviewMode, menuView));
    }

    public void simulateClick(SuperiorPlayer clickedPlayer, String plotName,
                              PlotCreationButton.Template template, boolean isPreviewMode,
                              @Nullable MenuView<?, ?> menuView) {
        String schematic = template.getSchematic().getName();

        // Checking for preview of plots.
        if (isPreviewMode) {
            Location previewLocation = plugin.getSettings().getPreviewPlots().get(schematic);
            if (previewLocation != null) {
                plugin.getGrid().startPlotPreview(clickedPlayer, schematic, plotName);
                return;
            }
        }

        Player whoClicked = clickedPlayer.asPlayer();

        GameSoundImpl.playSound(whoClicked, template.getAccessSound());

        template.getAccessCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", clickedPlayer.getName())));

        Message.PLOT_CREATE_PROCCESS_REQUEST.send(clickedPlayer);

        if (menuView != null)
            menuView.closeView();

        World.Environment environment = plugin.getSettings().getWorlds().getDefaultWorld();
        boolean offset = template.isOffset() || (environment == World.Environment.NORMAL ?
                plugin.getSettings().getWorlds().getNormal().isSchematicOffset() :
                environment == World.Environment.NETHER ? plugin.getSettings().getWorlds().getNether().isSchematicOffset() :
                        plugin.getSettings().getWorlds().getEnd().isSchematicOffset());

        plugin.getGrid().createPlot(clickedPlayer, schematic, template.getBonusWorth(),
                template.getBonusLevel(), template.getBiome(), plotName, offset);
    }

    public void openMenu(SuperiorPlayer superiorPlayer, @Nullable MenuView<?, ?> previousMenu, String plotName) {
        if (isSkipOneItem()) {
            List<String> schematicButtons = menuLayout.getButtons().stream()
                    .filter(button -> PlotCreationButton.class.equals(button.getViewButtonType()))
                    .map(button -> ((PlotCreationButton.Template) button).getSchematic().getName())
                    .collect(Collectors.toList());

            if (schematicButtons.size() == 1) {
                simulateClick(superiorPlayer, plotName, schematicButtons.get(0), false, superiorPlayer.getOpenedView());
                return;
            }
        }

        plugin.getMenus().openPlotCreation(superiorPlayer, MenuViewWrapper.fromView(previousMenu), plotName);
    }

    @Nullable
    public static MenuPlotCreation createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("plot-creation.yml",
                MenuPlotCreation::convertOldGUI);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<View> patternBuilder = menuParseResult.getLayoutBuilder();

        if (cfg.isConfigurationSection("items")) {
            for (String itemSectionName : cfg.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection itemSection = cfg.getConfigurationSection("items." + itemSectionName);

                if (!itemSection.isString("schematic"))
                    continue;

                Schematic schematic = plugin.getSchematics().getSchematic(itemSection.getString("schematic"));

                if (schematic == null) {
                    Log.warnFromFile("plot-creation.yml", "Invalid schematic for item ", itemSectionName);
                    continue;
                }

                PlotCreationButton.Builder buttonBuilder = new PlotCreationButton.Builder(schematic);

                {
                    String biomeName = itemSection.getString("biome", "PLAINS");
                    try {
                        Biome biome = Biome.valueOf(biomeName.toUpperCase(Locale.ENGLISH));
                        buttonBuilder.setBiome(biome);
                    } catch (IllegalArgumentException error) {
                        Log.warnFromFile("plot-creation.yml", "Invalid biome name for item ",
                                itemSectionName, ": ", biomeName);
                        continue;
                    }
                }

                {
                    Object bonusWorth = itemSection.get("bonus", itemSection.get("bonus-worth", 0D));
                    if (bonusWorth instanceof Double) {
                        buttonBuilder.setBonusWorth(BigDecimal.valueOf((double) bonusWorth));
                    } else if (bonusWorth instanceof String) {
                        buttonBuilder.setBonusWorth(new BigDecimal((String) bonusWorth));
                    }
                }

                {
                    Object bonusLevel = itemSection.get("bonus-level", 0D);
                    if (bonusLevel instanceof Double) {
                        buttonBuilder.setBonusLevel(BigDecimal.valueOf((double) bonusLevel));
                    } else if (bonusLevel instanceof String) {
                        buttonBuilder.setBonusLevel(new BigDecimal((String) bonusLevel));
                    }
                }

                ConfigurationSection soundSection = cfg.getConfigurationSection("sounds." + itemSectionName);
                if (soundSection != null) {
                    buttonBuilder.setAccessSound(MenuParserImpl.getInstance().getSound(soundSection.getConfigurationSection("access")));
                    buttonBuilder.setNoAccessSound(MenuParserImpl.getInstance().getSound(soundSection.getConfigurationSection("no-access")));
                }

                ConfigurationSection commandSection = cfg.getConfigurationSection("commands." + itemSectionName);
                if (commandSection != null) {
                    buttonBuilder.setAccessCommands(commandSection.getStringList("access"));
                    buttonBuilder.setNoAccessCommands(commandSection.getStringList("no-access"));
                }

                buttonBuilder.setOffset(itemSection.getBoolean("offset", false));
                buttonBuilder.setAccessItem(MenuParserImpl.getInstance().getItemStack("plot-creation.yml",
                        itemSection.getConfigurationSection("access")));
                buttonBuilder.setNoAccessItem(MenuParserImpl.getInstance().getItemStack("plot-creation.yml",
                        itemSection.getConfigurationSection("no-access")));

                patternBuilder.mapButtons(menuPatternSlots.getSlots(itemSectionName), buttonBuilder);
            }
        }

        return new MenuPlotCreation(menuParseResult);
    }

    public static class Args implements ViewArgs {

        private final String plotName;

        public Args(String plotName) {
            this.plotName = plotName;
        }

    }

    public static class View extends AbstractMenuView<View, Args> {

        private final String plotName;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, Args> menu, Args args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plotName = args.plotName;
        }

        public String getPlotName() {
            return plotName;
        }

    }

    private static boolean convertOldGUI(SuperiorSkyblockPlugin plugin, YamlConfiguration newMenu) {
        File oldFile = new File(plugin.getDataFolder(), "guis/creation-gui.yml");

        if (!oldFile.exists())
            return false;

        //We want to reset the items of newMenu.
        ConfigurationSection itemsSection = newMenu.createSection("items");
        ConfigurationSection soundsSection = newMenu.createSection("sounds");
        ConfigurationSection commandsSection = newMenu.createSection("commands");

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(oldFile);

        newMenu.set("title", cfg.getString("creation-gui.title"));

        int size = cfg.getInt("creation-gui.size");

        char[] patternChars = new char[size * 9];
        Arrays.fill(patternChars, '\n');

        int charCounter = 0;

        if (cfg.contains("creation-gui.fill-items")) {
            charCounter = MenuConverter.convertFillItems(cfg.getConfigurationSection("creation-gui.fill-items"),
                    charCounter, patternChars, itemsSection, commandsSection, soundsSection);
        }

        if (cfg.contains("creation-gui.schematics")) {
            for (String schemName : cfg.getConfigurationSection("creation-gui.schematics").getKeys(false)) {
                ConfigurationSection section = cfg.getConfigurationSection("creation-gui.schematics." + schemName);
                char itemChar = AbstractMenuLayout.BUTTON_SYMBOLS[charCounter++];
                section.set("schematic", schemName);
                MenuConverter.convertItemAccess(section, patternChars, itemChar, itemsSection, commandsSection, soundsSection);
            }
        }

        newMenu.set("pattern", MenuConverter.buildPattern(size, patternChars,
                AbstractMenuLayout.BUTTON_SYMBOLS[charCounter]));

        return true;
    }

}

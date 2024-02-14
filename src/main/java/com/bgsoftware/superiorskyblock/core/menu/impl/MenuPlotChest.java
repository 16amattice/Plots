package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.PlotChestPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.layout.PagedMenuLayoutImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;

public class MenuPlotChest extends AbstractPagedMenu<MenuPlotChest.View, PlotViewArgs, PlotChest> {

    private MenuPlotChest(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_PLOT_CHEST, parseResult, false);
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, PlotViewArgs args,
                                      @Nullable MenuView<?, ?> previousMenuView) {
        return new View(superiorPlayer, previousMenuView, this, args);
    }

    public void refreshViews(Plot plot) {
        refreshViews(view -> view.plot.equals(plot));
    }

    public void openMenu(SuperiorPlayer superiorPlayer, @Nullable MenuView<?, ?> previousMenu, Plot plot) {
        if (isSkipOneItem()) {
            PlotChest[] plotChest = plot.getChest();
            if (plotChest.length == 1) {
                plotChest[0].openChest(superiorPlayer);
                return;
            }
        }

        plugin.getMenus().openPlotChest(superiorPlayer, MenuViewWrapper.fromView(previousMenu), plot);
    }

    @Nullable
    public static MenuPlotChest createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("plot-chest.yml",
                null, new PlotChestPagedObjectButton.Builder());

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        PagedMenuLayoutImpl.Builder<View, PlotChest> patternBuilder = (PagedMenuLayoutImpl.Builder<View, PlotChest>) menuParseResult.getLayoutBuilder();

        if (cfg.isString("slots")) {
            for (char slotChar : cfg.getString("slots", "").toCharArray()) {
                List<Integer> slots = menuPatternSlots.getSlots(slotChar);

                ConfigurationSection validPageSection = cfg.getConfigurationSection("items." + slotChar + ".valid-page");
                ConfigurationSection invalidPageSection = cfg.getConfigurationSection("items." + slotChar + ".invalid-page");

                if (validPageSection == null) {
                    Log.warnFromFile("plot-chest.yml", "The slot char ", slotChar, " is missing the valid-page section.");
                    continue;
                }

                if (invalidPageSection == null) {
                    Log.warnFromFile("plot-chest.yml", "&cThe slot char ", slotChar, " is missing the invalid-page section.");
                    continue;
                }

                PlotChestPagedObjectButton.Builder buttonBuilder = new PlotChestPagedObjectButton.Builder();
                buttonBuilder.setButtonItem(MenuParserImpl.getInstance().getItemStack("plot-chest.yml", validPageSection));
                buttonBuilder.setNullItem(MenuParserImpl.getInstance().getItemStack("plot-chest.yml", invalidPageSection));

                patternBuilder.mapButtons(slots, buttonBuilder);
            }
        }

        return new MenuPlotChest(menuParseResult);
    }

    public static class View extends AbstractPagedMenuView<MenuPlotChest.View, PlotViewArgs, PlotChest> {

        private final Plot plot;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<View, PlotViewArgs> menu, PlotViewArgs args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plot = args.getPlot();
        }

        @Override
        protected List<PlotChest> requestObjects() {
            return new SequentialListBuilder<PlotChest>()
                    .build(Arrays.asList(plot.getChest()));
        }

    }

}

package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractPagedMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.CoopsPagedObjectButton;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractPagedMenuView;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;

import java.util.List;

public class MenuCoops extends AbstractPagedMenu<MenuCoops.View, PlotViewArgs, SuperiorPlayer> {

    private MenuCoops(MenuParseResult<View> parseResult) {
        super(MenuIdentifiers.MENU_COOPS, parseResult, false);
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
    public static MenuCoops createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("coops.yml",
                null, new CoopsPagedObjectButton.Builder());
        return menuParseResult == null ? null : new MenuCoops(menuParseResult);
    }

    public static class View extends AbstractPagedMenuView<MenuCoops.View, PlotViewArgs, SuperiorPlayer> {

        private final Plot plot;

        View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
             Menu<MenuCoops.View, PlotViewArgs> menu, PlotViewArgs args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plot = args.getPlot();
        }

        @Override
        public String replaceTitle(String title) {
            return title.replace("{0}", String.valueOf(this.plot.getCoopPlayers().size()))
                    .replace("{1}", String.valueOf(this.plot.getCoopLimit()));
        }

        @Override
        protected List<SuperiorPlayer> requestObjects() {
            return plot.getCoopPlayers();
        }

    }

}

package com.bgsoftware.superiorskyblock.core.menu.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.AbstractMenu;
import com.bgsoftware.superiorskyblock.core.menu.MenuIdentifiers;
import com.bgsoftware.superiorskyblock.core.menu.MenuParseResult;
import com.bgsoftware.superiorskyblock.core.menu.MenuPatternSlots;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.WarpManageIconButton;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.WarpManageLocationButton;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.WarpManagePrivateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.impl.WarpManageRenameButton;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;
import org.bukkit.configuration.file.YamlConfiguration;

public class MenuWarpManage extends AbstractMenu<MenuWarpManage.View, MenuWarpManage.Args> {

    private final GameSound successUpdateSound;

    private MenuWarpManage(MenuParseResult<View> parseResult, GameSound successUpdateSound) {
        super(MenuIdentifiers.MENU_WARP_MANAGE, parseResult);
        this.successUpdateSound = successUpdateSound;
    }

    public GameSound getSuccessUpdateSound() {
        return successUpdateSound;
    }

    @Override
    protected View createViewInternal(SuperiorPlayer superiorPlayer, Args args, @Nullable MenuView<?, ?> previousMenu) {
        return new View(superiorPlayer, previousMenu, this, args);
    }

    public void refreshViews(PlotWarp plotWarp) {
        refreshViews(view -> view.plotWarp.equals(plotWarp));
    }

    @Nullable
    public static MenuWarpManage createInstance() {
        MenuParseResult<View> menuParseResult = MenuParserImpl.getInstance().loadMenu("warp-manage.yml",
                null);

        if (menuParseResult == null) {
            return null;
        }

        MenuPatternSlots menuPatternSlots = menuParseResult.getPatternSlots();
        YamlConfiguration cfg = menuParseResult.getConfig();
        MenuLayout.Builder<View> patternBuilder = menuParseResult.getLayoutBuilder();

        GameSound successUpdateSound = cfg.isConfigurationSection("success-update-sound") ?
                MenuParserImpl.getInstance().getSound(cfg.getConfigurationSection("success-update-sound")) : null;

        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "warp-rename", menuPatternSlots),
                new WarpManageRenameButton.Builder());
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "warp-icon", menuPatternSlots),
                new WarpManageIconButton.Builder());
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "warp-location", menuPatternSlots),
                new WarpManageLocationButton.Builder());
        patternBuilder.mapButtons(MenuParserImpl.getInstance().parseButtonSlots(cfg, "warp-private", menuPatternSlots),
                new WarpManagePrivateButton.Builder());

        return new MenuWarpManage(menuParseResult, successUpdateSound);
    }

    public static class Args implements ViewArgs {

        private final PlotWarp plotWarp;

        public Args(PlotWarp plotWarp) {
            this.plotWarp = plotWarp;
        }

    }

    public static class View extends AbstractMenuView<View, Args> {

        private final PlotWarp plotWarp;

        protected View(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView, Menu<View, Args> menu, Args args) {
            super(inventoryViewer, previousMenuView, menu);
            this.plotWarp = args.plotWarp;
        }

        public PlotWarp getPlotWarp() {
            return plotWarp;
        }

        @Override
        public String replaceTitle(String title) {
            return title.replace("{0}", plotWarp.getName());
        }

    }

}
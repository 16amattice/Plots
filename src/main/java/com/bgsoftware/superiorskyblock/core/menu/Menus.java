package com.bgsoftware.superiorskyblock.core.menu;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuBankLogs;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuBiomes;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuBorderColor;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmBan;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmDisband;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmLeave;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuControlPanel;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuCoops;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuCounts;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuGlobalWarps;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotBank;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotChest;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotCreation;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotFlags;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotMembers;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotPrivileges;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotRate;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotRatings;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotUniqueVisitors;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotUpgrades;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotValues;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotVisitors;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMemberManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMemberRole;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissions;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissionsCategory;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlayerLanguage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuTopPlots;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpCategories;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpCategoryIconEdit;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpCategoryManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpIconEdit;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarps;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuBlank;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuConfigEditor;
import com.bgsoftware.superiorskyblock.core.menu.view.AbstractMenuView;

public class Menus {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    public static final MenuBlank MENU_BLANK = MenuBlank.createInstance();
    public static final MenuConfigEditor MENU_CONFIG_EDITOR = MenuConfigEditor.createInstance();

    public static MenuBankLogs MENU_BANK_LOGS;
    public static MenuBiomes MENU_BIOMES;
    public static MenuBorderColor MENU_BORDER_COLOR;
    public static MenuConfirmBan MENU_CONFIRM_BAN;
    public static MenuConfirmDisband MENU_CONFIRM_DISBAND;
    public static MenuConfirmKick MENU_CONFIRM_KICK;
    public static MenuConfirmLeave MENU_CONFIRM_LEAVE;
    public static MenuControlPanel MENU_CONTROL_PANEL;
    public static MenuCoops MENU_COOPS;
    public static MenuCounts MENU_COUNTS;
    public static MenuGlobalWarps MENU_GLOBAL_WARPS;
    public static MenuPlotBank MENU_PLOT_BANK;
    public static MenuPlotChest MENU_PLOT_CHEST;
    public static MenuPlotCreation MENU_PLOT_CREATION;
    public static MenuPlotFlags MENU_PLOT_FLAGS;
    public static MenuPlotMembers MENU_PLOT_MEMBERS;
    public static MenuPlotPrivileges MENU_PLOT_PRIVILEGES;
    public static MenuPlotRate MENU_PLOT_RATE;
    public static MenuPlotRatings MENU_PLOT_RATINGS;
    public static MenuPlotUniqueVisitors MENU_PLOT_UNIQUE_VISITORS;
    public static MenuPlotUpgrades MENU_PLOT_UPGRADES;
    public static MenuPlotValues MENU_PLOT_VALUES;
    public static MenuPlotVisitors MENU_PLOT_VISITORS;
    public static MenuMemberManage MENU_MEMBER_MANAGE;
    public static MenuMemberRole MENU_MEMBER_ROLE;
    public static MenuMissions MENU_MISSIONS;
    public static MenuMissionsCategory MENU_MISSIONS_CATEGORY;
    public static MenuPlayerLanguage MENU_PLAYER_LANGUAGE;
    public static MenuTopPlots MENU_TOP_PLOTS;
    public static MenuWarpCategories MENU_WARP_CATEGORIES;
    public static MenuWarpCategoryIconEdit MENU_WARP_CATEGORY_ICON_EDIT;
    public static MenuWarpCategoryManage MENU_WARP_CATEGORY_MANAGE;
    public static MenuWarpIconEdit MENU_WARP_ICON_EDIT;
    public static MenuWarpManage MENU_WARP_MANAGE;
    public static MenuWarps MENU_WARPS;

    private Menus() {

    }

    public static void registerMenus() {
        // We register the internal menus
        createMenu(MENU_BLANK);
        createMenu(MENU_CONFIG_EDITOR);
        // Load menus from files
        MENU_BANK_LOGS = createMenu(MenuBankLogs.createInstance());
        MENU_BIOMES = createMenu(MenuBiomes.createInstance());
        MENU_BORDER_COLOR = createMenu(MenuBorderColor.createInstance());
        MENU_CONFIRM_BAN = createMenu(MenuConfirmBan.createInstance());
        MENU_CONFIRM_DISBAND = createMenu(MenuConfirmDisband.createInstance());
        MENU_CONFIRM_KICK = createMenu(MenuConfirmKick.createInstance());
        MENU_CONFIRM_LEAVE = createMenu(MenuConfirmLeave.createInstance());
        MENU_CONTROL_PANEL = createMenu(MenuControlPanel.createInstance());
        MENU_COOPS = createMenu(MenuCoops.createInstance());
        MENU_COUNTS = createMenu(MenuCounts.createInstance());
        MENU_GLOBAL_WARPS = createMenu(MenuGlobalWarps.createInstance());
        MENU_PLOT_BANK = createMenu(MenuPlotBank.createInstance());
        MENU_PLOT_CHEST = createMenu(MenuPlotChest.createInstance());
        MENU_PLOT_CREATION = createMenu(MenuPlotCreation.createInstance());
        MENU_PLOT_FLAGS = createMenu(MenuPlotFlags.createInstance());
        MENU_PLOT_MEMBERS = createMenu(MenuPlotMembers.createInstance());
        MENU_PLOT_PRIVILEGES = createMenu(MenuPlotPrivileges.createInstance());
        MENU_PLOT_RATE = createMenu(MenuPlotRate.createInstance());
        MENU_PLOT_RATINGS = createMenu(MenuPlotRatings.createInstance());
        MENU_PLOT_UNIQUE_VISITORS = createMenu(MenuPlotUniqueVisitors.createInstance());
        MENU_PLOT_UPGRADES = createMenu(MenuPlotUpgrades.createInstance());
        MENU_PLOT_VALUES = createMenu(MenuPlotValues.createInstance());
        MENU_PLOT_VISITORS = createMenu(MenuPlotVisitors.createInstance());
        MENU_MEMBER_MANAGE = createMenu(MenuMemberManage.createInstance());
        MENU_MEMBER_ROLE = createMenu(MenuMemberRole.createInstance());
        MENU_MISSIONS = createMenu(MenuMissions.createInstance());
        MENU_MISSIONS_CATEGORY = createMenu(MenuMissionsCategory.createInstance());
        MENU_PLAYER_LANGUAGE = createMenu(MenuPlayerLanguage.createInstance());
        MENU_TOP_PLOTS = createMenu(MenuTopPlots.createInstance());
        MENU_WARP_CATEGORIES = createMenu(MenuWarpCategories.createInstance());
        MENU_WARP_CATEGORY_ICON_EDIT = createMenu(MenuWarpCategoryIconEdit.createInstance());
        MENU_WARP_CATEGORY_MANAGE = createMenu(MenuWarpCategoryManage.createInstance());
        MENU_WARP_ICON_EDIT = createMenu(MenuWarpIconEdit.createInstance());
        MENU_WARP_MANAGE = createMenu(MenuWarpManage.createInstance());
        MENU_WARPS = createMenu(MenuWarps.createInstance());
    }

    private static <M extends AbstractMenu<V, A>, V extends AbstractMenuView<V, A>, A extends ViewArgs> M createMenu(M menu) {
        if (menu == null)
            throw new IllegalStateException("Menu could not be initialized.");

        plugin.getMenus().registerMenu(menu);
        return menu;
    }

}

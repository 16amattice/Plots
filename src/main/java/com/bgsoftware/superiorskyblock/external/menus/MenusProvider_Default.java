package com.bgsoftware.superiorskyblock.external.menus;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.hooks.MenusProvider;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.menu.ISuperiorMenu;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.errors.ManagerLoadException;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmBan;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuConfirmKick;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotCreation;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotPrivileges;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuMissionsCategory;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuTopPlots;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpCategoryIconEdit;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpCategoryManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpIconEdit;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarpManage;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuWarps;
import com.bgsoftware.superiorskyblock.core.menu.impl.internal.MenuCustom;
import com.bgsoftware.superiorskyblock.core.menu.view.args.EmptyViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlayerViewArgs;
import com.google.common.base.Preconditions;

import java.io.File;

public class MenusProvider_Default implements MenusProvider {

    private final SuperiorSkyblockPlugin plugin;

    public MenusProvider_Default(SuperiorSkyblockPlugin plugin) {
        this.plugin = plugin;
    }

    private static void handleExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            ManagerLoadException handlerError = new ManagerLoadException(ex, ManagerLoadException.ErrorLevel.CONTINUE);
            Log.error(handlerError, "An unexpected error occurred while loading menu:");
        }
    }

    @Override
    public void initializeMenus() {
        File guiFolder = new File(plugin.getDataFolder(), "guis");
        if (guiFolder.exists()) {
            File oldGuisFolder = new File(plugin.getDataFolder(), "old-guis");
            //noinspection ResultOfMethodCallIgnored
            guiFolder.renameTo(oldGuisFolder);
        }

        // We first want to unregister all menus
        plugin.getMenus().unregisterMenus();

        Menus.registerMenus();

        File customMenusFolder = new File(plugin.getDataFolder(), "menus/custom");

        if (!customMenusFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            customMenusFolder.mkdirs();
            return;
        }

        File[] customMenuFiles = customMenusFolder.listFiles();
        if (customMenuFiles != null) {
            for (File menuFile : customMenuFiles) {
                handleExceptions(() -> plugin.getMenus().registerMenu(MenuCustom.createInstance(menuFile)));
            }
        }
    }

    @Override
    public void openBankLogs(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_BANK_LOGS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshBankLogs(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_BANK_LOGS.refreshViews(plot);
    }

    @Override
    public void openBiomes(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_BIOMES.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_BORDER_COLOR.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openConfirmBan(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer bannedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Preconditions.checkNotNull(bannedPlayer, "bannedPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_BAN.createView(targetPlayer, new MenuConfirmBan.Args(targetPlot, bannedPlayer), previousMenu);
    }

    @Override
    public void openConfirmDisband(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_CONFIRM_DISBAND.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void openConfirmKick(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer kickedPlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Preconditions.checkNotNull(kickedPlayer, "kickedPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_KICK.createView(targetPlayer, new MenuConfirmKick.Args(targetPlot, kickedPlayer), previousMenu);
    }

    @Override
    public void openConfirmLeave(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_CONFIRM_LEAVE.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openControlPanel(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_CONTROL_PANEL.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void openCoops(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_COOPS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshCoops(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_COOPS.refreshViews(plot);
    }

    @Override
    public void openCounts(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_COUNTS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshCounts(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_COUNTS.refreshViews(plot);
    }

    @Override
    public void openGlobalWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_GLOBAL_WARPS.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void refreshGlobalWarps() {
        Menus.MENU_GLOBAL_WARPS.refreshViews();
    }

    @Override
    public void openPlotBank(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_BANK.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshPlotBank(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_BANK.refreshViews(plot);
    }

    @Override
    public void openPlotChest(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_CHEST.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshPlotChest(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_CHEST.refreshViews(plot);
    }

    @Override
    public void openPlotCreation(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, String plotName) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        Menus.MENU_PLOT_CREATION.createView(targetPlayer, new MenuPlotCreation.Args(plotName), previousMenu);
    }

    @Override
    public void openPlotRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_RATE.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void openPlotRatings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_RATINGS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshPlotRatings(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_RATINGS.refreshViews(plot);
    }

    @Override
    public void openMemberManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotMember, "plotMember parameter cannot be null.");
        Menus.MENU_MEMBER_MANAGE.createView(targetPlayer, new PlayerViewArgs(plotMember), previousMenu);
    }

    @Override
    public void destroyMemberManage(SuperiorPlayer plotMember) {
        Preconditions.checkNotNull(plotMember, "plotMember parameter cannot be null.");
        Menus.MENU_MEMBER_MANAGE.closeViews(plotMember);
    }

    @Override
    public void openMemberRole(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(plotMember, "plotMember parameter cannot be null.");
        Menus.MENU_MEMBER_ROLE.createView(targetPlayer, new PlayerViewArgs(plotMember), previousMenu);
    }

    @Override
    public void destroyMemberRole(SuperiorPlayer plotMember) {
        Preconditions.checkNotNull(plotMember, "plotMember parameter cannot be null.");
        Menus.MENU_MEMBER_ROLE.closeViews(plotMember);
    }

    @Override
    public void openMembers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_MEMBERS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshMembers(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_MEMBERS.refreshViews(plot);
    }

    @Override
    public void openMissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_MISSIONS.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        Menus.MENU_MISSIONS_CATEGORY.createView(targetPlayer, new MenuMissionsCategory.Args(missionCategory), previousMenu);
    }

    @Override
    public void refreshMissionsCategory(MissionCategory missionCategory) {
        Preconditions.checkNotNull(missionCategory, "missionCategory parameter cannot be null.");
        Menus.MENU_MISSIONS_CATEGORY.refreshViews(missionCategory);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer permissiblePlayer) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Preconditions.checkNotNull(permissiblePlayer, "permissiblePlayer parameter cannot be null.");
        Menus.MENU_PLOT_PRIVILEGES.createView(targetPlayer, new MenuPlotPrivileges.Args(targetPlot, permissiblePlayer), previousMenu);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, PlayerRole permissibleRole) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Preconditions.checkNotNull(permissibleRole, "permissibleRole parameter cannot be null.");
        Menus.MENU_PLOT_PRIVILEGES.createView(targetPlayer, new MenuPlotPrivileges.Args(targetPlot, permissibleRole), previousMenu);
    }

    @Override
    public void refreshPermissions(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_PRIVILEGES.refreshViews(plot);
    }

    @Override
    public void refreshPermissions(Plot plot, SuperiorPlayer permissiblePlayer) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(permissiblePlayer, "permissiblePlayer parameter cannot be null.");
        Menus.MENU_PLOT_PRIVILEGES.refreshViews(plot, permissiblePlayer);
    }

    @Override
    public void refreshPermissions(Plot plot, PlayerRole permissibleRole) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Preconditions.checkNotNull(permissibleRole, "permissibleRole parameter cannot be null.");
        Menus.MENU_PLOT_PRIVILEGES.refreshViews(plot, permissibleRole);
    }

    @Override
    public void updatePermission(PlotPrivilege plotPrivilege) {
        // The default implementation does not care if the plot privilege is valid for showing the plot
        // privileges in the menu. If the plot privilege is not valid at the time of opening the menu, it
        // will show it as it was disabled. This is the responsibility of the server owners to properly
        // configure the menu.
    }

    @Override
    public void openPlayerLanguage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Menus.MENU_PLAYER_LANGUAGE.createView(targetPlayer, EmptyViewArgs.INSTANCE, previousMenu);
    }

    @Override
    public void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_FLAGS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshSettings(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_FLAGS.refreshViews(plot);
    }

    @Override
    public void updateSettings(PlotFlag plotFlag) {
        // The default implementation does not care if the plot flag is valid for showing the plot flags
        // in the menu. If the plot flag is not valid at the time of opening the menu, it will show it as
        // it was disabled. This is the responsibility of the server owners to properly configure the menu.
    }

    @Override
    public void openTopPlots(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SortingType sortingType) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        Menus.MENU_TOP_PLOTS.createView(targetPlayer, new MenuTopPlots.Args(sortingType), previousMenu);
    }

    @Override
    public void refreshTopPlots(SortingType sortingType) {
        Preconditions.checkNotNull(sortingType, "sortingType parameter cannot be null.");
        Menus.MENU_TOP_PLOTS.refreshViews(sortingType);
    }

    @Override
    public void openUniqueVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_UNIQUE_VISITORS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshUniqueVisitors(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_UNIQUE_VISITORS.refreshViews(plot);
    }

    @Override
    public void openUpgrades(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_UPGRADES.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshUpgrades(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_UPGRADES.refreshViews(plot);
    }

    @Override
    public void openValues(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_VALUES.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshValues(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_VALUES.refreshViews(plot);
    }

    @Override
    public void openVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_PLOT_VISITORS.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshVisitors(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_PLOT_VISITORS.refreshViews(plot);
    }

    @Override
    public void openWarpCategories(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetPlot, "targetPlot parameter cannot be null.");
        Menus.MENU_WARP_CATEGORIES.createView(targetPlayer, new PlotViewArgs(targetPlot), previousMenu);
    }

    @Override
    public void refreshWarpCategories(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_WARP_CATEGORIES.refreshViews(plot);
    }

    @Override
    public void destroyWarpCategories(Plot plot) {
        Preconditions.checkNotNull(plot, "plot parameter cannot be null.");
        Menus.MENU_WARP_CATEGORIES.closeViews(plot);
    }

    @Override
    public void openWarpCategoryIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        Menus.MENU_WARP_CATEGORY_ICON_EDIT.createView(targetPlayer, new MenuWarpCategoryIconEdit.Args(targetCategory), previousMenu);
    }

    @Override
    public void openWarpCategoryManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        Menus.MENU_WARP_CATEGORY_MANAGE.createView(targetPlayer, new MenuWarpCategoryManage.Args(targetCategory), previousMenu);
    }

    @Override
    public void refreshWarpCategoryManage(WarpCategory warpCategory) {
        Preconditions.checkNotNull(warpCategory, "warpCategory parameter cannot be null.");
        Menus.MENU_WARP_CATEGORY_MANAGE.refreshViews(warpCategory);
    }

    @Override
    public void openWarpIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetWarp, "targetWarp parameter cannot be null.");
        Menus.MENU_WARP_ICON_EDIT.createView(targetPlayer, new MenuWarpIconEdit.Args(targetWarp), previousMenu);
    }

    @Override
    public void openWarpManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetWarp, "targetWarp parameter cannot be null.");
        Menus.MENU_WARP_MANAGE.createView(targetPlayer, new MenuWarpManage.Args(targetWarp), previousMenu);
    }

    @Override
    public void refreshWarpManage(PlotWarp plotWarp) {
        Preconditions.checkNotNull(plotWarp, "plotWarp parameter cannot be null.");
        Menus.MENU_WARP_MANAGE.refreshViews(plotWarp);
    }

    @Override
    public void openWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        Preconditions.checkNotNull(targetPlayer, "targetPlayer parameter cannot be null.");
        Preconditions.checkNotNull(targetCategory, "targetCategory parameter cannot be null.");
        Menus.MENU_WARPS.createView(targetPlayer, new MenuWarps.Args(targetCategory), previousMenu);
    }

    @Override
    public void refreshWarps(WarpCategory warpCategory) {
        Preconditions.checkNotNull(warpCategory, "warpCategory parameter cannot be null.");
        Menus.MENU_WARPS.refreshViews(warpCategory);
    }

    @Override
    public void destroyWarps(WarpCategory warpCategory) {
        Preconditions.checkNotNull(warpCategory, "warpCategory parameter cannot be null.");
        Menus.MENU_WARPS.closeViews(warpCategory);
    }

}

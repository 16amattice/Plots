package com.bgsoftware.superiorskyblock.core.menu;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.handlers.MenusManager;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.plot.warps.WarpCategory;
import com.bgsoftware.superiorskyblock.api.menu.ISuperiorMenu;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.MenuCommands;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.layout.MenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.layout.PagedMenuLayout;
import com.bgsoftware.superiorskyblock.api.menu.parser.MenuParser;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.PagedMenuView;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;
import com.bgsoftware.superiorskyblock.api.missions.MissionCategory;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.Manager;
import com.bgsoftware.superiorskyblock.core.io.MenuParserImpl;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.layout.PagedMenuLayoutImpl;
import com.bgsoftware.superiorskyblock.core.menu.layout.RegularMenuLayoutImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenusManagerImpl extends Manager implements MenusManager {

    private final Map<String, Menu<?, ?>> registeredMenus = new HashMap<>();

    public MenusManagerImpl(SuperiorSkyblockPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadData() {
        plugin.getProviders().getMenusProvider().initializeMenus();
    }

    @Override
    public void openBankLogs(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openBankLogs(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshBankLogs(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshBankLogs(plot);
    }

    @Override
    public void openBiomes(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openBiomes(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotBiomesMenu(SuperiorPlayer superiorPlayer) {
        openBiomes(superiorPlayer, null, superiorPlayer.getPlot());
    }

    @Override
    public void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        plugin.getProviders().getMenusProvider().openBorderColor(targetPlayer, previousMenu);
    }

    @Override
    public void openBorderColorMenu(SuperiorPlayer superiorPlayer) {
        openBorderColor(superiorPlayer, null);
    }

    @Override
    public void openConfirmBan(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer bannedPlayer) {
        plugin.getProviders().getMenusProvider().openConfirmBan(targetPlayer, previousMenu, targetPlot, bannedPlayer);
    }

    @Override
    public void openConfirmDisband(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openConfirmDisband(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openConfirmDisbandMenu(SuperiorPlayer superiorPlayer) {
        openConfirmDisband(superiorPlayer, null, superiorPlayer.getPlot());
    }

    @Override
    public void openConfirmKick(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer kickedPlayer) {
        plugin.getProviders().getMenusProvider().openConfirmKick(targetPlayer, previousMenu, targetPlot, kickedPlayer);
    }

    @Override
    public void openConfirmLeave(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        plugin.getProviders().getMenusProvider().openConfirmLeave(targetPlayer, previousMenu);
    }

    @Override
    public void openControlPanel(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openControlPanel(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotPanelMenu(SuperiorPlayer superiorPlayer) {
        openControlPanel(superiorPlayer, null, superiorPlayer.getPlot());
    }

    @Override
    public void openCoops(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openCoops(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshCoops(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshCoops(plot);
    }

    @Override
    public void openCounts(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openCounts(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotCountsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openCounts(superiorPlayer, null, plot);
    }

    @Override
    public void refreshCounts(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshCounts(plot);
    }

    @Override
    public void openGlobalWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        plugin.getProviders().getMenusProvider().openGlobalWarps(targetPlayer, previousMenu);
    }

    @Override
    public void openGlobalWarpsMenu(SuperiorPlayer superiorPlayer) {
        openGlobalWarps(superiorPlayer, null);
    }

    @Override
    public void refreshGlobalWarps() {
        plugin.getProviders().getMenusProvider().refreshGlobalWarps();
    }

    @Override
    public void openPlotBank(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openPlotBank(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshPlotBank(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshPlotBank(plot);
    }

    @Override
    public void openPlotChest(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openPlotChest(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshPlotChest(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshPlotChest(plot);
    }

    @Override
    public void openPlotCreation(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, String plotName) {
        plugin.getProviders().getMenusProvider().openPlotCreation(targetPlayer, previousMenu, plotName);
    }

    @Override
    public void openPlotCreationMenu(SuperiorPlayer superiorPlayer, String plotName) {
        openPlotCreation(superiorPlayer, null, plotName);
    }

    @Override
    public void openPlotRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openPlotRate(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotRateMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openPlotRate(superiorPlayer, null, plot);
    }

    @Override
    public void openPlotRatings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openPlotRatings(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotRatingsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openPlotRatings(superiorPlayer, null, plot);
    }

    @Override
    public void refreshPlotRatings(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshPlotRatings(plot);
    }

    @Override
    public void openMemberManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember) {
        plugin.getProviders().getMenusProvider().openMemberManage(targetPlayer, previousMenu, plotMember);
    }

    @Override
    public void openMemberManageMenu(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer) {
        openMemberManage(superiorPlayer, null, targetPlayer);
    }

    @Override
    public void destroyMemberManage(SuperiorPlayer plotMember) {
        plugin.getProviders().getMenusProvider().destroyMemberManage(plotMember);
    }

    @Override
    public void openMemberRole(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember) {
        plugin.getProviders().getMenusProvider().openMemberRole(targetPlayer, previousMenu, plotMember);
    }

    @Override
    public void openMemberRoleMenu(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer) {
        openMemberRole(superiorPlayer, null, targetPlayer);
    }

    @Override
    public void destroyMemberRole(SuperiorPlayer plotMember) {
        plugin.getProviders().getMenusProvider().destroyMemberRole(plotMember);
    }

    @Override
    public void openMembers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openMembers(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotMembersMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openMembers(superiorPlayer, null, plot);
    }

    @Override
    public void refreshMembers(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshMembers(plot);
    }

    @Override
    public void openMissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        plugin.getProviders().getMenusProvider().openMissions(targetPlayer, previousMenu);
    }

    @Override
    public void openPlotMainMissionsMenu(SuperiorPlayer superiorPlayer) {
        openMissions(superiorPlayer, null);
    }

    @Override
    public void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory) {
        plugin.getProviders().getMenusProvider().openMissionsCategory(targetPlayer, previousMenu, missionCategory);
    }

    @Override
    public void openPlotMissionsMenu(SuperiorPlayer superiorPlayer, boolean plotMissions) {
        // Menu doesn't exist anymore.
    }

    @Override
    public void refreshMissionsCategory(MissionCategory missionCategory) {
        plugin.getProviders().getMenusProvider().refreshMissionsCategory(missionCategory);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer permissiblePlayer) {
        plugin.getProviders().getMenusProvider().openPermissions(targetPlayer, previousMenu, targetPlot, permissiblePlayer);
    }

    @Override
    public void openPlotPermissionsMenu(SuperiorPlayer superiorPlayer, Plot plot, SuperiorPlayer targetPlayer) {
        openPermissions(superiorPlayer, null, plot, targetPlayer);
    }

    @Override
    public void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, PlayerRole permissibleRole) {
        plugin.getProviders().getMenusProvider().openPermissions(targetPlayer, previousMenu, targetPlot, permissibleRole);
    }

    @Override
    public void openPlotPermissionsMenu(SuperiorPlayer superiorPlayer, Plot plot, PlayerRole playerRole) {
        openPermissions(superiorPlayer, null, plot, playerRole);
    }

    @Override
    public void refreshPermissions(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshPermissions(plot);
    }

    @Override
    public void refreshPermissions(Plot plot, SuperiorPlayer permissiblePlayer) {
        plugin.getProviders().getMenusProvider().refreshPermissions(plot, permissiblePlayer);
    }

    @Override
    public void refreshPermissions(Plot plot, PlayerRole permissibleRole) {
        plugin.getProviders().getMenusProvider().refreshPermissions(plot, permissibleRole);
    }

    @Override
    public void updatePermission(PlotPrivilege plotPrivilege) {
        plugin.getProviders().getMenusProvider().updatePermission(plotPrivilege);
    }

    @Override
    public void openPlayerLanguage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu) {
        plugin.getProviders().getMenusProvider().openPlayerLanguage(targetPlayer, previousMenu);
    }

    @Override
    public void openPlayerLanguageMenu(SuperiorPlayer superiorPlayer) {
        openPlayerLanguage(superiorPlayer, null);
    }

    @Override
    public void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openSettings(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotSettingsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openSettings(superiorPlayer, null, plot);
    }

    @Override
    public void refreshSettings(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshSettings(plot);
    }

    @Override
    public void updateSettings(PlotFlag plotFlag) {
        plugin.getProviders().getMenusProvider().updateSettings(plotFlag);
    }

    @Override
    public void openTopPlots(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SortingType sortingType) {
        plugin.getProviders().getMenusProvider().openTopPlots(targetPlayer, previousMenu, sortingType);
    }

    @Override
    public void openPlotsTopMenu(SuperiorPlayer superiorPlayer, SortingType sortingType) {
        openTopPlots(superiorPlayer, null, sortingType);
    }

    @Override
    public void refreshTopPlots(SortingType sortingType) {
        plugin.getProviders().getMenusProvider().refreshTopPlots(sortingType);
    }

    @Override
    public void openUniqueVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openUniqueVisitors(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openUniqueVisitorsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openUniqueVisitors(superiorPlayer, null, plot);
    }

    @Override
    public void refreshUniqueVisitors(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshUniqueVisitors(plot);
    }

    @Override
    public void openUpgrades(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openUpgrades(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotUpgradeMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openUpgrades(superiorPlayer, null, plot);
    }

    @Override
    public void refreshUpgrades(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshUpgrades(plot);
    }

    @Override
    public void openValues(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openValues(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void openPlotValuesMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openValues(superiorPlayer, null, plot);
    }

    @Override
    public void refreshValues(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshValues(plot);
    }

    @Override
    public void openVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openVisitors(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshVisitors(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshVisitors(plot);
    }

    @Override
    public void openPlotVisitorsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openVisitors(superiorPlayer, null, plot);
    }

    @Override
    public void openWarpCategories(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot) {
        plugin.getProviders().getMenusProvider().openWarpCategories(targetPlayer, previousMenu, targetPlot);
    }

    @Override
    public void refreshWarpCategories(Plot plot) {
        plugin.getProviders().getMenusProvider().refreshWarpCategories(plot);
    }

    @Override
    public void destroyWarpCategories(Plot plot) {
        plugin.getProviders().getMenusProvider().destroyWarpCategories(plot);
    }

    @Override
    public void openWarpCategoryIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        plugin.getProviders().getMenusProvider().openWarpCategoryIconEdit(targetPlayer, previousMenu, targetCategory);
    }

    @Override
    public void openWarpCategoryManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        plugin.getProviders().getMenusProvider().openWarpCategoryManage(targetPlayer, previousMenu, targetCategory);
    }

    @Override
    public void refreshWarpCategoryManage(WarpCategory warpCategory) {
        plugin.getProviders().getMenusProvider().refreshWarpCategoryManage(warpCategory);
    }

    @Override
    public void openWarpIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp) {
        plugin.getProviders().getMenusProvider().openWarpIconEdit(targetPlayer, previousMenu, targetWarp);
    }

    @Override
    public void openWarpManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp) {
        plugin.getProviders().getMenusProvider().openWarpManage(targetPlayer, previousMenu, targetWarp);
    }

    @Override
    public void refreshWarpManage(PlotWarp plotWarp) {
        plugin.getProviders().getMenusProvider().refreshWarpManage(plotWarp);
    }

    @Override
    public void openWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory) {
        plugin.getProviders().getMenusProvider().openWarps(targetPlayer, previousMenu, targetCategory);
    }

    @Override
    public void openPlotWarpsMenu(SuperiorPlayer superiorPlayer, Plot plot) {
        openWarps(superiorPlayer, null, plot.getWarpCategories().values()
                .stream().findFirst().orElseGet(() -> plot.createWarpCategory("Default Category")));
    }

    @Override
    public void refreshWarps(WarpCategory warpCategory) {
        plugin.getProviders().getMenusProvider().refreshWarps(warpCategory);
    }

    @Override
    public void destroyWarps(WarpCategory warpCategory) {
        plugin.getProviders().getMenusProvider().destroyWarps(warpCategory);
    }

    @Override
    public void registerMenu(Menu<?, ?> menu) {
        Preconditions.checkNotNull(menu, "menu parameter cannot be null");
        Preconditions.checkState(getMenu(menu.getIdentifier()) == null, "There is already a menu with a similar identifier: " + menu.getIdentifier());
        this.registeredMenus.put(menu.getIdentifier().toLowerCase(Locale.ENGLISH), menu);
    }

    public void unregisterMenus() {
        this.registeredMenus.clear();
    }

    @Nullable
    @Override
    public <V extends MenuView<V, A>, A extends ViewArgs> Menu<V, A> getMenu(String identifier) {
        Preconditions.checkNotNull(identifier, "identifier parameter cannot be null");
        return (Menu<V, A>) this.registeredMenus.get(identifier.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Map<String, Menu<?, ?>> getMenus() {
        return Collections.unmodifiableMap(this.registeredMenus);
    }

    @Override
    public <V extends MenuView<V, ?>> MenuLayout.Builder<V> createPatternBuilder() {
        return RegularMenuLayoutImpl.newBuilder();
    }

    @Override
    public <V extends PagedMenuView<V, ?, E>, E> PagedMenuLayout.Builder<V, E> createPagedPatternBuilder() {
        return PagedMenuLayoutImpl.newBuilder();
    }

    @Override
    public <V extends MenuView<V, ?>> MenuTemplateButton.Builder<V> createButtonBuilder(
            Class<?> viewButtonType, MenuTemplateButton.MenuViewButtonCreator<V> viewButtonCreator) {
        return AbstractMenuTemplateButton.newBuilder(viewButtonType, viewButtonCreator);
    }

    @Override
    public <V extends MenuView<V, ?>, E> PagedMenuTemplateButton.Builder<V, E> createPagedButtonBuilder(
            Class<?> viewButtonType, PagedMenuTemplateButton.PagedMenuViewButtonCreator<V, E> viewButtonCreator) {
        return PagedMenuTemplateButtonImpl.newBuilder(viewButtonType, viewButtonCreator);
    }

    @Override
    public MenuParser getParser() {
        return MenuParserImpl.getInstance();
    }

    @Override
    public MenuCommands getMenuCommands() {
        return MenuCommandsImpl.getInstance();
    }

    @Override
    @Deprecated
    public ISuperiorMenu getOldMenuFromView(MenuView<?, ?> menuView) {
        return MenuViewWrapper.fromView(menuView);
    }

}

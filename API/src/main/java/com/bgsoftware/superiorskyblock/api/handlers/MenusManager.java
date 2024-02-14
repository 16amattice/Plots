package com.bgsoftware.superiorskyblock.api.handlers;

import com.bgsoftware.common.annotations.Nullable;
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

import java.util.Map;

public interface MenusManager {

    /**
     * Open the bank-logs menu.
     * Used to display all logs of bank transactions.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to display bank logs for.
     */
    void openBankLogs(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the bank-logs menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshBankLogs(Plot plot);

    /**
     * Open the biomes-menu.
     * Used to display and choose biomes for the plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to change biomes for.
     */
    void openBiomes(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot biomes menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openBiomes(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotBiomesMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the border-color menu.
     * Used to change the color of the world border for a player.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the border color menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openBorderColor(SuperiorPlayer, ISuperiorMenu)}
     */
    @Deprecated
    void openBorderColorMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the confirm-ban menu.
     * Used to confirm a ban of an plot member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to ban the player from.
     * @param bannedPlayer The player that will be banned.
     */
    void openConfirmBan(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer bannedPlayer);

    /**
     * Open the confirm-disband menu.
     * Used to confirm disband of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to disband.
     */
    void openConfirmDisband(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the confirm disband menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openConfirmDisband(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openConfirmDisbandMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the confirm-kick menu.
     * Used to confirm a kick of an plot member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to kick the player from.
     * @param kickedPlayer The player that will be kicked.
     */
    void openConfirmKick(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot, SuperiorPlayer kickedPlayer);

    /**
     * Open the confirm-leave menu.
     * Used to confirm leaving of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openConfirmLeave(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the control-panel menu.
     * Used when opening the control panel of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to open the control panel of.
     */
    void openControlPanel(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot panel menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated {@link #openControlPanel(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotPanelMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the coops menu.
     * Used when opening the coops menu of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get coop-members from.
     */
    void openCoops(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the coops-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshCoops(Plot plot);

    /**
     * Open the block-counts menu.
     * Used when opening the counts menu of an plot (using /is counts, for example)
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get block counts from.
     */
    void openCounts(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot counts menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the block counts from.
     * @deprecated see {@link #openCounts(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotCountsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the counts-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshCounts(Plot plot);

    /**
     * Open the global-warps menu.
     * Used when running the /is warp command.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openGlobalWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the global warps menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openGlobalWarps(SuperiorPlayer, ISuperiorMenu)}
     */
    @Deprecated
    void openGlobalWarpsMenu(SuperiorPlayer superiorPlayer);

    /**
     * Refresh the global-warps menu.
     */
    void refreshGlobalWarps();

    /**
     * Open the plot-bank menu.
     * Used when running the /is bank command.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to open the bank for.
     */
    void openPlotBank(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the plot bank menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshPlotBank(Plot plot);

    /**
     * Open the plot-chests menu.
     * Used to open the shared chests menu of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to open the shared-chests menu for.
     */
    void openPlotChest(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the plot-chests menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshPlotChest(Plot plot);

    /**
     * Open the plots-creation menu.
     * Used when creating a new plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param plotName   The desired name of the new plot.
     */
    void openPlotCreation(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, String plotName);

    /**
     * Open the plot creation menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plotName     The name to give the plot if the player creates a new plot.
     * @deprecated see {@link #openPlotCreation(SuperiorPlayer, ISuperiorMenu, String)}
     */
    @Deprecated
    void openPlotCreationMenu(SuperiorPlayer superiorPlayer, String plotName);

    /**
     * Open the rate-menu.
     * Used when giving a rating for an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to give a rating.
     */
    void openPlotRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot rate menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The target plot to give the rating.
     * @deprecated see {@link #openPlotRate(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotRateMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Open the ratings-menu.
     * Used when checking given ratings of an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get ratings from.
     */
    void openPlotRatings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot ratings menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the ratings from.
     * @deprecated see {@link #openPlotRatings(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotRatingsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the ratings-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshPlotRatings(Plot plot);

    /**
     * Open the member-manage menu.
     * Used when managing an plot member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param plotMember The plot member to manage.
     */
    void openMemberManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember);

    /**
     * Open the member manage menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param targetPlayer   The target player to manage.
     * @deprecated see {@link #openMemberManage(SuperiorPlayer, ISuperiorMenu, SuperiorPlayer)}
     */
    @Deprecated
    void openMemberManageMenu(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer);

    /**
     * Destroy the member-manage menus for a specific plot member.
     *
     * @param plotMember The plot member to close menus of.
     */
    void destroyMemberManage(SuperiorPlayer plotMember);

    /**
     * Used to open the member-role menu.
     * Used when changing a role of an plot member.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param plotMember The plot member to change role for.
     */
    void openMemberRole(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SuperiorPlayer plotMember);

    /**
     * Open the member role menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param targetPlayer   The target player to manage their role.
     * @deprecated see {@link #openMemberRole(SuperiorPlayer, ISuperiorMenu, SuperiorPlayer)}
     */
    @Deprecated
    void openMemberRoleMenu(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer);

    /**
     * Destroy the member-role menus for a specific plot member.
     *
     * @param plotMember The plot member to close menus of.
     */
    void destroyMemberRole(SuperiorPlayer plotMember);

    /**
     * Open the members-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to check the members of.
     */
    void openMembers(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot members menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the members from.
     * @deprecated {@link #openMembers(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotMembersMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the members-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshMembers(Plot plot);

    /**
     * Open the missions-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openMissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the main plot missions menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openMissions(SuperiorPlayer, ISuperiorMenu)}
     */
    @Deprecated
    void openPlotMainMissionsMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the missions-menu of a specific category.
     *
     * @param targetPlayer    The player to open the menu for.
     * @param previousMenu    The previous menu that was opened, if exists.
     * @param missionCategory The category to get missions from.
     */
    void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory);

    /**
     * Open the plot missions menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plotMissions Should plot missions be displayed or player missions?
     * @deprecated Unused menu.
     */
    @Deprecated
    void openPlotMissionsMenu(SuperiorPlayer superiorPlayer, boolean plotMissions);

    /**
     * Refresh the missions-menu for a specific category.
     *
     * @param missionCategory The category to refresh the menus for.
     */
    void refreshMissionsCategory(MissionCategory missionCategory);

    /**
     * Open the permissions-menu.
     * Used when changing plot-permissions of a player on an plot.
     *
     * @param targetPlayer      The player to open the menu for.
     * @param previousMenu      The previous menu that was opened, if exists.
     * @param targetPlot      The plot to change permissions in.
     * @param permissiblePlayer The player to change permissions for.
     */
    void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu,
                         Plot targetPlot, SuperiorPlayer permissiblePlayer);

    /**
     * Open the plot permissions menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot that holds the permissions.
     * @param targetPlayer   The target player to see their permissions.
     * @deprecated see {@link #openPermissions(SuperiorPlayer, ISuperiorMenu, Plot, SuperiorPlayer)}
     */
    @Deprecated
    void openPlotPermissionsMenu(SuperiorPlayer superiorPlayer, Plot plot, SuperiorPlayer targetPlayer);

    /**
     * Open the permissions-menu.
     * Used when changing plot-permissions of an plot-role on an plot.
     *
     * @param targetPlayer    The player to open the menu for.
     * @param previousMenu    The previous menu that was opened, if exists.
     * @param targetPlot    The plot to change permissions in.
     * @param permissibleRole The plot-role to change permissions for.
     */
    void openPermissions(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu,
                         Plot targetPlot, PlayerRole permissibleRole);

    /**
     * Open the plot permissions menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot that holds the permissions.
     * @param playerRole     The target role to see their permissions.
     * @deprecated see {@link #openPermissions(SuperiorPlayer, ISuperiorMenu, Plot, PlayerRole)}
     */
    @Deprecated
    void openPlotPermissionsMenu(SuperiorPlayer superiorPlayer, Plot plot, PlayerRole playerRole);

    /**
     * Refresh the permissions-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshPermissions(Plot plot);

    /**
     * Refresh the permissions-menu of a player for a specific plot.
     *
     * @param plot            The plot to refresh the menus for.
     * @param permissiblePlayer The player to change permissions.
     */
    void refreshPermissions(Plot plot, SuperiorPlayer permissiblePlayer);

    /**
     * Refresh the permissions-menu of an plot role for a specific plot.
     *
     * @param plot          The plot to refresh the menus for.
     * @param permissibleRole The plot role to change permissions for.
     */
    void refreshPermissions(Plot plot, PlayerRole permissibleRole);

    /**
     * Update the plot permission in the menu.
     *
     * @param plotPrivilege The permission to update.
     */
    void updatePermission(PlotPrivilege plotPrivilege);

    /**
     * Open the player-language menu.
     * Used when a player changes his language.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openPlayerLanguage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

    /**
     * Open the language menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @deprecated see {@link #openPlayerLanguage(SuperiorPlayer, ISuperiorMenu)}
     */
    @Deprecated
    void openPlayerLanguageMenu(SuperiorPlayer superiorPlayer);

    /**
     * Open the plot-settings menu.
     * Used when changing plot-settings.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to change settings for.
     */
    void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot settings menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the settings from.
     * @deprecated see {@link #openSettings(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotSettingsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the plot-settings menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshSettings(Plot plot);

    /**
     * Update the plot settings in the menu.
     *
     * @param plotFlag The settings to update.
     */
    @Deprecated
    void updateSettings(PlotFlag plotFlag);

    /**
     * Open the top-plots menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param sortingType  The type of sorting of plots to use.
     */
    void openTopPlots(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, SortingType sortingType);

    /**
     * Open the plots top menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param sortingType    The sorting type you want to open.
     * @deprecated see {@link #openTopPlots(SuperiorPlayer, ISuperiorMenu, SortingType)}
     */
    @Deprecated
    void openPlotsTopMenu(SuperiorPlayer superiorPlayer, SortingType sortingType);

    /**
     * Refresh the top-plots menu for a specific sorting type.
     *
     * @param sortingType The sorting type to refresh.
     */
    void refreshTopPlots(SortingType sortingType);

    /**
     * Open the unique-visitors menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get visitors from.
     */
    void openUniqueVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the unique plot visitors menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the visitors from.
     * @deprecated see {@link #openUniqueVisitors(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openUniqueVisitorsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the unique-visitors menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshUniqueVisitors(Plot plot);

    /**
     * Open the upgrades-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get upgrade levels from.
     */
    void openUpgrades(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot upgrade menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the upgrades from.
     * @deprecated see {@link #openUpgrades(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotUpgradeMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the upgrades-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshUpgrades(Plot plot);

    /**
     * Open the values-menu.
     * Used when right-clicking an plot in the top-plots menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get values from.
     */
    void openValues(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Open the plot values menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the values from.
     * @deprecated see {@link #openValues(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotValuesMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the values-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshValues(Plot plot);

    /**
     * Open the visitors-menu.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get visitors from.
     */
    void openVisitors(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the visitors-menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshVisitors(Plot plot);

    /**
     * Open the plot visitors menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the visitors from.
     * @deprecated see {@link #openVisitors(SuperiorPlayer, ISuperiorMenu, Plot)}
     */
    @Deprecated
    void openPlotVisitorsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Open the warp categories menu
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to get warp categories from.
     */
    void openWarpCategories(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

    /**
     * Refresh the warp categories menu for a specific plot.
     *
     * @param plot The plot to refresh the menus for.
     */
    void refreshWarpCategories(Plot plot);

    /**
     * Destroy the warp-categories menus for a specific plot.
     *
     * @param plot The plot to close menus of.
     */
    void destroyWarpCategories(Plot plot);

    /**
     * Open the warp-category icon edit menu.
     * Used when editing an icon of a warp category.
     *
     * @param targetPlayer   The player to open the menu for.
     * @param previousMenu   The previous menu that was opened, if exists.
     * @param targetCategory The warp category to edit the icon for.
     */
    void openWarpCategoryIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory);

    /**
     * Open the warp category manage menu.
     * Used when managing a warp category.
     *
     * @param targetPlayer   The player to open the menu for.
     * @param previousMenu   The previous menu that was opened, if exists.
     * @param targetCategory The warp category to manage.
     */
    void openWarpCategoryManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory);

    /**
     * Refresh the warp category manage menu for a specific warp category.
     *
     * @param warpCategory The warp category to refresh the menus for.
     */
    void refreshWarpCategoryManage(WarpCategory warpCategory);

    /**
     * Open the warp icon edit menu.
     * Used when editing an icon of a warp.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetWarp   The warp to edit the icon for.
     */
    void openWarpIconEdit(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp);

    /**
     * Open the warp manage menu.
     * Used when managing a warp.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetWarp   The warp to manage.
     */
    void openWarpManage(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, PlotWarp targetWarp);

    /**
     * Refresh the warp manage menu for a specific warp.
     *
     * @param plotWarp The warp to refresh the menus for.
     */
    void refreshWarpManage(PlotWarp plotWarp);

    /**
     * Open the warps menu.
     * Used to look for all warps in a category.
     *
     * @param targetPlayer   The player to open the menu for.
     * @param previousMenu   The previous menu that was opened, if exists.
     * @param targetCategory The category to get warps from.
     */
    void openWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory);

    /**
     * Open the plot warps menu for a player.
     *
     * @param superiorPlayer The player to open the menu for.
     * @param plot         The plot to get the warps from.
     * @deprecated see {@link #openWarps(SuperiorPlayer, ISuperiorMenu, WarpCategory)}
     */
    @Deprecated
    void openPlotWarpsMenu(SuperiorPlayer superiorPlayer, Plot plot);

    /**
     * Refresh the warps-menu for a specific plot.
     *
     * @param warpCategory The warp category to refresh the menus for.
     */
    void refreshWarps(WarpCategory warpCategory);

    /**
     * Destroy the warp-categories menus for a specific warp category.
     *
     * @param warpCategory The warp category to close menus of.
     */
    void destroyWarps(WarpCategory warpCategory);

    /**
     * Register a new menu to the plugin.
     *
     * @param menu The menu to register.
     */
    void registerMenu(Menu<?, ?> menu);

    /**
     * Get a menu by its identifier.
     *
     * @param identifier The identifier of the menu.
     */
    @Nullable
    <V extends MenuView<V, A>, A extends ViewArgs> Menu<V, A> getMenu(String identifier);

    /**
     * Get all the registered menus.
     */
    Map<String, Menu<?, ?>> getMenus();

    /**
     * Create a new pattern builder for building a menu.
     */
    <V extends MenuView<V, ?>> MenuLayout.Builder<V> createPatternBuilder();

    /**
     * Create a new pattern builder for building a paged-based menu.
     */
    <V extends PagedMenuView<V, ?, E>, E> PagedMenuLayout.Builder<V, E> createPagedPatternBuilder();

    /**
     * Create a new button builder.
     */
    <V extends MenuView<V, ?>> MenuTemplateButton.Builder<V> createButtonBuilder(
            Class<?> viewButtonType, MenuTemplateButton.MenuViewButtonCreator<V> viewButtonCreator);

    /**
     * Create a new button builder.
     */
    <V extends MenuView<V, ?>, E> PagedMenuTemplateButton.Builder<V, E> createPagedButtonBuilder(
            Class<?> viewButtonType, PagedMenuTemplateButton.PagedMenuViewButtonCreator<V, E> viewButtonCreator);

    /**
     * Get the parser instance.
     */
    MenuParser getParser();

    /**
     * Get the commands executor instance.
     */
    MenuCommands getMenuCommands();


    /**
     * Helper method to cast the new {@link MenuView} object to the old {@link ISuperiorMenu} object.
     */
    @Deprecated
    ISuperiorMenu getOldMenuFromView(MenuView<?, ?> menuView);

}

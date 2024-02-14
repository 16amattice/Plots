package com.bgsoftware.superiorskyblock.api.hooks;

import com.bgsoftware.common.annotations.Nullable;
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

public interface MenusProvider {

    /**
     * Initialize the menus.
     */
    void initializeMenus();

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
     * Open the border-color menu.
     * Used to change the color of the world border for a player.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     */
    void openBorderColor(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu);

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
     * Open the rate-menu.
     * Used when giving a rating for an plot.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to give a rating.
     */
    void openPlotRate(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

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
     * Open the missions-menu of a specific category.
     *
     * @param targetPlayer    The player to open the menu for.
     * @param previousMenu    The previous menu that was opened, if exists.
     * @param missionCategory The category to get missions from.
     */
    void openMissionsCategory(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, MissionCategory missionCategory);

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
     * Open the plot-settings menu.
     * Used when changing plot-settings.
     *
     * @param targetPlayer The player to open the menu for.
     * @param previousMenu The previous menu that was opened, if exists.
     * @param targetPlot The plot to change settings for.
     */
    void openSettings(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, Plot targetPlot);

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
     * Open the warps-menu.
     * Used to look for all warps in a category.
     *
     * @param targetPlayer   The player to open the menu for.
     * @param previousMenu   The previous menu that was opened, if exists.
     * @param targetCategory The category to get warps from.
     */
    void openWarps(SuperiorPlayer targetPlayer, @Nullable ISuperiorMenu previousMenu, WarpCategory targetCategory);

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

}

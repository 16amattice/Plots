package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PermissionNode;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotPrivileges;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlotPrivilegePagedObjectButton extends AbstractPagedMenuButton<MenuPlotPrivileges.View, MenuPlotPrivileges.PlotPrivilegeInfo> {

    private PlotPrivilegePagedObjectButton(MenuTemplateButton<MenuPlotPrivileges.View> templateButton, MenuPlotPrivileges.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        Plot plot = menuView.getPlot();
        Object permissionHolder = menuView.getPermissionHolder();

        if (permissionHolder instanceof PlayerRole) {
            onRoleButtonClick(plot, menuView.getInventoryViewer(), clickEvent);
        } else if (permissionHolder instanceof SuperiorPlayer) {
            onPlayerButtonClick(plot, menuView.getInventoryViewer(), (SuperiorPlayer) permissionHolder);
        }
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Plot targetPlot = menuView.getPlot();
        Object permissionHolder = menuView.getPermissionHolder();

        ItemBuilder permissionItem = new ItemBuilder(Material.AIR);

        if (permissionHolder instanceof PlayerRole) {
            if (pagedObject.getRolePlotPrivilegeItem() != null) {
                permissionItem = modifyRoleButtonItem(targetPlot);
            }
        } else if (permissionHolder instanceof SuperiorPlayer) {
            PlotPrivilege plotPrivilege = pagedObject.getPlotPrivilege();
            boolean hasPermission = plotPrivilege != null && targetPlot.getPermissionNode(
                    (SuperiorPlayer) permissionHolder).hasPermission(plotPrivilege);
            permissionItem = hasPermission ? pagedObject.getEnabledPlotPrivilegeItem() :
                    pagedObject.getDisabledPlotPrivilegeItem();
        }

        return permissionItem.build(inventoryViewer);
    }

    private void onRoleButtonClick(Plot plot, SuperiorPlayer clickedPlayer, InventoryClickEvent clickEvent) {
        PlotPrivilege plotPrivilege = pagedObject.getPlotPrivilege();

        if (plotPrivilege == null)
            return;

        PlayerRole currentRole = plot.getRequiredPlayerRole(plotPrivilege);

        if (clickedPlayer.getPlayerRole().isLessThan(currentRole)) {
            onFailurePermissionChange(clickedPlayer, false);
            return;
        }

        PlayerRole newRole = null;

        if (clickEvent.getClick().isLeftClick()) {
            newRole = SPlayerRole.of(currentRole.getWeight() - 1);

            if (!plugin.getSettings().isCoopMembers() && newRole == SPlayerRole.coopRole()) {
                assert newRole != null;
                newRole = SPlayerRole.of(newRole.getWeight() - 1);
            }

            if (newRole == null)
                newRole = clickedPlayer.getPlayerRole();
        } else {
            if (clickedPlayer.getPlayerRole().isHigherThan(currentRole)) {
                newRole = SPlayerRole.of(currentRole.getWeight() + 1);
            }

            if (!plugin.getSettings().isCoopMembers() && newRole == SPlayerRole.coopRole()) {
                assert newRole != null;
                newRole = SPlayerRole.of(newRole.getWeight() + 1);
            }

            if (newRole == null)
                newRole = SPlayerRole.guestRole();
        }

        if (plugin.getEventsBus().callPlotChangeRolePrivilegeEvent(plot, clickedPlayer, newRole)) {
            plot.setPermission(newRole, plotPrivilege);
            onSuccessfulPermissionChange(clickedPlayer, Formatters.CAPITALIZED_FORMATTER.format(plotPrivilege.getName()));
        }
    }

    private void onPlayerButtonClick(Plot plot, SuperiorPlayer clickedPlayer, SuperiorPlayer permissionHolder) {
        PlotPrivilege plotPrivilege = pagedObject.getPlotPrivilege();

        if (plotPrivilege == null || !plot.hasPermission(clickedPlayer, plotPrivilege))
            return;

        PermissionNode permissionNode = plot.getPermissionNode(permissionHolder);

        String permissionHolderName = permissionHolder.getName();

        boolean currentValue = permissionNode.hasPermission(plotPrivilege);

        if (!plugin.getEventsBus().callPlotChangePlayerPrivilegeEvent(plot, clickedPlayer, permissionHolder, !currentValue))
            return;

        plot.setPermission(permissionHolder, plotPrivilege, !currentValue);

        onSuccessfulPermissionChange(clickedPlayer, permissionHolderName);
    }

    private void onSuccessfulPermissionChange(SuperiorPlayer clickedPlayer, String permissionHolderName) {
        Player player = clickedPlayer.asPlayer();

        if (player == null)
            return;

        Message.UPDATED_PERMISSION.send(clickedPlayer, permissionHolderName);

        GameSoundImpl.playSound(player, pagedObject.getAccessSound());

        pagedObject.getAccessCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", clickedPlayer.getName())));

        Menus.MENU_PLOT_PRIVILEGES.refreshViews();
    }

    private void onFailurePermissionChange(SuperiorPlayer clickedPlayer, boolean sendFailMessage) {
        Player player = clickedPlayer.asPlayer();

        if (player == null)
            return;

        if (sendFailMessage)
            Message.LACK_CHANGE_PERMISSION.send(clickedPlayer);

        GameSoundImpl.playSound(player, pagedObject.getNoAccessSound());

        pagedObject.getNoAccessCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", clickedPlayer.getName())));
    }

    private ItemBuilder modifyRoleButtonItem(Plot plot) {
        Preconditions.checkNotNull(pagedObject.getRolePlotPrivilegeItem(), "role item cannot be null.");

        PlotPrivilege plotPrivilege = pagedObject.getPlotPrivilege();

        PlayerRole requiredRole = plotPrivilege == null ? null : plot.getRequiredPlayerRole(plotPrivilege);
        ItemBuilder permissionItem = pagedObject.getRolePlotPrivilegeItem()
                .replaceAll("{}", requiredRole == null ? "" : requiredRole.toString());

        if (!Menus.MENU_PLOT_PRIVILEGES.getNoRolePermission().isEmpty() &&
                !Menus.MENU_PLOT_PRIVILEGES.getExactRolePermission().isEmpty() &&
                !Menus.MENU_PLOT_PRIVILEGES.getHigherRolePermission().isEmpty()) {
            List<String> roleString = new ArrayList<>();

            int roleWeight = requiredRole == null ? Integer.MAX_VALUE : requiredRole.getWeight();
            PlayerRole currentRole;

            for (int i = -2; (currentRole = SPlayerRole.of(i)) != null; i++) {
                if (!plugin.getSettings().isCoopMembers() && currentRole == SPlayerRole.coopRole())
                    continue;

                if (i < roleWeight) {
                    roleString.add(Menus.MENU_PLOT_PRIVILEGES.getNoRolePermission().replace("{}", currentRole + ""));
                } else if (i == roleWeight) {
                    roleString.add(Menus.MENU_PLOT_PRIVILEGES.getExactRolePermission().replace("{}", currentRole + ""));
                } else {
                    roleString.add(Menus.MENU_PLOT_PRIVILEGES.getHigherRolePermission().replace("{}", currentRole + ""));
                }
            }

            ItemMeta itemMeta = permissionItem.getItemMeta();

            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();

                for (int i = 0; i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.equals("{0}")) {
                        lore.set(i, roleString.get(0));
                        for (int j = 1; j < roleString.size(); j++) {
                            lore.add(i + j, roleString.get(j));
                        }
                        i += roleString.size();
                    }
                }

                permissionItem.withLore(lore);
            }
        }

        return permissionItem;
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotPrivileges.View, MenuPlotPrivileges.PlotPrivilegeInfo> {

        @Override
        public PagedMenuTemplateButton<MenuPlotPrivileges.View, MenuPlotPrivileges.PlotPrivilegeInfo> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), PlotPrivilegePagedObjectButton.class,
                    PlotPrivilegePagedObjectButton::new);
        }

    }

}

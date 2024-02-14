package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotMembers;
import com.bgsoftware.superiorskyblock.core.menu.view.MenuViewWrapper;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MembersPagedObjectButton extends AbstractPagedMenuButton<MenuPlotMembers.View, SuperiorPlayer> {

    private MembersPagedObjectButton(MenuTemplateButton<MenuPlotMembers.View> templateButton, MenuPlotMembers.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        plugin.getMenus().openMemberManage(menuView.getInventoryViewer(), MenuViewWrapper.fromView(menuView), pagedObject);
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", pagedObject.getName())
                .replaceAll("{1}", pagedObject.getPlayerRole() + "")
                .asSkullOf(pagedObject)
                .build(pagedObject);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotMembers.View, SuperiorPlayer> {

        @Override
        public PagedMenuTemplateButton<MenuPlotMembers.View, SuperiorPlayer> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), MembersPagedObjectButton.class,
                    MembersPagedObjectButton::new);
        }

    }

}

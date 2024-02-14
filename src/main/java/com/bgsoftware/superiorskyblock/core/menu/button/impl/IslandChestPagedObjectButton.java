package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.PlotChest;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotChest;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlotChestPagedObjectButton extends AbstractPagedMenuButton<MenuPlotChest.View, PlotChest> {

    private PlotChestPagedObjectButton(MenuTemplateButton<MenuPlotChest.View> templateButton, MenuPlotChest.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        menuView.setPreviousMove(false);
        pagedObject.openChest(menuView.getInventoryViewer());
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", (pagedObject.getIndex() + 1) + "")
                .replaceAll("{1}", (pagedObject.getRows() * 9) + "")
                .build(menuView.getInventoryViewer());
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotChest.View, PlotChest> {

        @Override
        public PagedMenuTemplateButton<MenuPlotChest.View, PlotChest> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), PlotChestPagedObjectButton.class,
                    PlotChestPagedObjectButton::new);
        }

    }

}

package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotVisitors;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VisitorPagedObjectButton extends AbstractPagedMenuButton<MenuPlotVisitors.View, SuperiorPlayer> {

    private VisitorPagedObjectButton(MenuTemplateButton<MenuPlotVisitors.View> templateButton, MenuPlotVisitors.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        String subCommandToExecute;

        if (clickEvent.getClick().isRightClick())
            subCommandToExecute = "invite";
        else if (clickEvent.getClick().isLeftClick())
            subCommandToExecute = "expel";
        else return;

        plugin.getCommands().dispatchSubCommand(clickEvent.getWhoClicked(), subCommandToExecute, pagedObject.getName());
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        Plot plot = pagedObject.getPlot();

        String plotOwner = plot != null ? plot.getOwner().getName() : "None";
        String plotName = plot != null ? plot.getName().isEmpty() ? plotOwner : plot.getName() : "None";

        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", pagedObject.getName())
                .replaceAll("{1}", plotOwner)
                .replaceAll("{2}", plotName)
                .asSkullOf(pagedObject)
                .build(pagedObject);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotVisitors.View, SuperiorPlayer> {

        @Override
        public PagedMenuTemplateButton<MenuPlotVisitors.View, SuperiorPlayer> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), VisitorPagedObjectButton.class,
                    VisitorPagedObjectButton::new);
        }

    }

}

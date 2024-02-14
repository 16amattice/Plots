package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotUniqueVisitors;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class UniqueVisitorPagedObjectButton extends AbstractPagedMenuButton<MenuPlotUniqueVisitors.View, MenuPlotUniqueVisitors.UniqueVisitorInfo> {

    private UniqueVisitorPagedObjectButton(MenuTemplateButton<MenuPlotUniqueVisitors.View> templateButton, MenuPlotUniqueVisitors.View menuView) {
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

        plugin.getCommands().dispatchSubCommand(clickEvent.getWhoClicked(),
                subCommandToExecute, pagedObject.getVisitor().getName());
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        SuperiorPlayer visitor = pagedObject.getVisitor();
        Plot plot = visitor.getPlot();

        String plotOwner = plot != null ? plot.getOwner().getName() : "None";
        String plotName = plot != null ? plot.getName().isEmpty() ? plotOwner : plot.getName() : "None";

        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", visitor.getName())
                .replaceAll("{1}", plotOwner)
                .replaceAll("{2}", plotName)
                .replaceAll("{3}", Formatters.DATE_FORMATTER.format(new Date(pagedObject.getVisitTime())))
                .asSkullOf(visitor)
                .build(visitor);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotUniqueVisitors.View, MenuPlotUniqueVisitors.UniqueVisitorInfo> {

        @Override
        public PagedMenuTemplateButton<MenuPlotUniqueVisitors.View, MenuPlotUniqueVisitors.UniqueVisitorInfo> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), UniqueVisitorPagedObjectButton.class,
                    UniqueVisitorPagedObjectButton::new);
        }

    }

}

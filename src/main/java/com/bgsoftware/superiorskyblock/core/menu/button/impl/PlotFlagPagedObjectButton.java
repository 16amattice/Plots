package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.GameSoundImpl;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotFlags;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlotFlagPagedObjectButton extends AbstractPagedMenuButton<MenuPlotFlags.View, MenuPlotFlags.PlotFlagInfo> {

    private PlotFlagPagedObjectButton(MenuTemplateButton<MenuPlotFlags.View> templateButton, MenuPlotFlags.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();

        Plot plot = menuView.getPlot();

        PlotFlag plotFlag = pagedObject.getPlotFlag();

        if (plotFlag == null)
            return;

        if (plot.hasSettingsEnabled(plotFlag)) {
            if (!plugin.getEventsBus().callPlotDisableFlagEvent(inventoryViewer, plot, plotFlag))
                return;

            plot.disableSettings(plotFlag);
        } else {
            if (!plugin.getEventsBus().callPlotEnableFlagEvent(inventoryViewer, plot, plotFlag))
                return;

            plot.enableSettings(plotFlag);
        }

        GameSoundImpl.playSound(clickEvent.getWhoClicked(), pagedObject.getClickSound());

        Message.UPDATED_SETTINGS.send(inventoryViewer, Formatters.CAPITALIZED_FORMATTER.format(plotFlag.getName()));

        menuView.refreshView();
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Plot plot = menuView.getPlot();

        PlotFlag plotFlag = pagedObject.getPlotFlag();

        return plotFlag != null && plot.hasSettingsEnabled(plotFlag) ?
                pagedObject.getEnabledPlotFlagItem().build(inventoryViewer) :
                pagedObject.getDisabledPlotFlagItem().build(inventoryViewer);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotFlags.View, MenuPlotFlags.PlotFlagInfo> {

        @Override
        public PagedMenuTemplateButton<MenuPlotFlags.View, MenuPlotFlags.PlotFlagInfo> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), PlotFlagPagedObjectButton.class,
                    PlotFlagPagedObjectButton::new);
        }

    }

}

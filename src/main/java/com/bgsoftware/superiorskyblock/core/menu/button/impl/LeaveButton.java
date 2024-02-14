package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.BaseMenuView;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.List;

public class LeaveButton extends AbstractMenuViewButton<BaseMenuView> {

    private LeaveButton(AbstractMenuTemplateButton<BaseMenuView> templateButton, BaseMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Plot plot = inventoryViewer.getPlot();

        if (getTemplate().leavePlot && plot != null && plugin.getEventsBus().callPlotQuitEvent(inventoryViewer, plot)) {
            plot.kickMember(inventoryViewer);

            PlotUtils.sendMessage(plot, Message.LEAVE_ANNOUNCEMENT, Collections.emptyList(), inventoryViewer.getName());

            Message.LEFT_PLOT.send(inventoryViewer);
        }

        BukkitExecutor.sync(menuView::closeView, 1L);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<BaseMenuView> {

        private boolean leavePlot;

        public Builder setLeavePlot(boolean leavePlot) {
            this.leavePlot = leavePlot;
            return this;
        }

        @Override
        public MenuTemplateButton<BaseMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, leavePlot);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<BaseMenuView> {

        private final boolean leavePlot;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound, boolean leavePlot) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    LeaveButton.class, LeaveButton::new);
            this.leavePlot = leavePlot;
        }

    }

}

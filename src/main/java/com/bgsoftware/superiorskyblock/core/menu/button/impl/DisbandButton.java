package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DisbandButton extends AbstractMenuViewButton<PlotMenuView> {

    private DisbandButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer inventoryViewer = menuView.getInventoryViewer();
        Plot targetPlot = menuView.getPlot();

        if (getTemplate().disbandPlot && plugin.getEventsBus().callPlotDisbandEvent(inventoryViewer, targetPlot)) {
            PlotUtils.sendMessage(targetPlot, Message.DISBAND_ANNOUNCEMENT, Collections.emptyList(), inventoryViewer.getName());

            Message.DISBANDED_PLOT.send(inventoryViewer);

            if (BuiltinModules.BANK.disbandRefund > 0) {
                Message.DISBAND_PLOT_BALANCE_REFUND.send(targetPlot.getOwner(), Formatters.NUMBER_FORMATTER.format(
                        targetPlot.getPlotBank().getBalance().multiply(BigDecimal.valueOf(BuiltinModules.BANK.disbandRefund))));
            }

            inventoryViewer.setDisbands(inventoryViewer.getDisbands() - 1);

            targetPlot.disbandPlot();
        }

        BukkitExecutor.sync(menuView::closeView, 1L);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        private boolean disbandPlot;

        public Builder setDisbandPlot(boolean disbandPlot) {
            this.disbandPlot = disbandPlot;
            return this;
        }

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new Template(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound, disbandPlot);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<PlotMenuView> {

        private final boolean disbandPlot;

        Template(@Nullable TemplateItem buttonItem, @Nullable GameSound clickSound, @Nullable List<String> commands,
                 @Nullable String requiredPermission, @Nullable GameSound lackPermissionSound, boolean disbandPlot) {
            super(buttonItem, clickSound, commands, requiredPermission, lackPermissionSound,
                    DisbandButton.class, DisbandButton::new);
            this.disbandPlot = disbandPlot;
        }

    }

}

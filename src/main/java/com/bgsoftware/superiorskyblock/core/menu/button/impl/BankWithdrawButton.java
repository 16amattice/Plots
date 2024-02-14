package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.world.GameSound;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.Menus;
import com.bgsoftware.superiorskyblock.core.menu.TemplateItem;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuTemplateButton;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractMenuViewButton;
import com.bgsoftware.superiorskyblock.core.menu.button.MenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.view.PlotMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class BankWithdrawButton extends AbstractMenuViewButton<PlotMenuView> {

    private BankWithdrawButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        SuperiorPlayer clickedPlayer = plugin.getPlayers().getSuperiorPlayer(clickEvent.getWhoClicked());
        Plot plot = menuView.getPlot();

        BigDecimal amount = plot.getPlotBank().getBalance()
                .multiply(getTemplate().withdrawValue);

        BankTransaction bankTransaction = plot.getPlotBank().withdrawMoney(clickedPlayer, amount, getTemplate().withdrawCommands);
        Menus.MENU_PLOT_BANK.handleWithdraw(clickedPlayer, plot, bankTransaction,
                getTemplate().successSound, getTemplate().failSound, amount);
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        private final double withdrawValue;
        private final List<String> withdrawCommands;
        private GameSound successSound;
        private GameSound failSound;

        public Builder(double withdrawValue) {
            this.withdrawValue = withdrawValue;
            this.withdrawCommands = null;
        }

        public Builder(List<String> withdrawCommands) {
            this.withdrawValue = 100D;
            this.withdrawCommands = withdrawCommands;
        }

        public BankWithdrawButton.Builder setSuccessSound(GameSound successSound) {
            this.successSound = successSound;
            return this;
        }

        public BankWithdrawButton.Builder setFailSound(GameSound failSound) {
            this.failSound = failSound;
            return this;
        }

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new Template(buttonItem, commands, requiredPermission, lackPermissionSound,
                    successSound, failSound, withdrawValue, withdrawCommands);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<PlotMenuView> {

        @Nullable
        private final GameSound successSound;
        @Nullable
        private final GameSound failSound;
        private final BigDecimal withdrawValue;
        private final List<String> withdrawCommands;

        Template(@Nullable TemplateItem buttonItem, @Nullable List<String> commands, @Nullable String requiredPermission,
                 @Nullable GameSound lackPermissionSound, @Nullable GameSound successSound,
                 @Nullable GameSound failSound, double withdrawValue, @Nullable List<String> withdrawCommands) {
            super(buttonItem, null, commands, requiredPermission, lackPermissionSound,
                    BankWithdrawButton.class, BankWithdrawButton::new);
            this.successSound = successSound;
            this.failSound = failSound;
            this.withdrawValue = BigDecimal.valueOf(withdrawValue / 100D);
            this.withdrawCommands = withdrawCommands == null ? Collections.emptyList() : withdrawCommands;
        }

    }

}

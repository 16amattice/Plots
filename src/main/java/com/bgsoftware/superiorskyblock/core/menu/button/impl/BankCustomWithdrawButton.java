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
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.player.chat.PlayerChat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;
import java.util.List;

public class BankCustomWithdrawButton extends AbstractMenuViewButton<PlotMenuView> {

    private BankCustomWithdrawButton(AbstractMenuTemplateButton<PlotMenuView> templateButton, PlotMenuView menuView) {
        super(templateButton, menuView);
    }

    @Override
    public Template getTemplate() {
        return (Template) super.getTemplate();
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        SuperiorPlayer clickedPlayer = plugin.getPlayers().getSuperiorPlayer(player);

        Plot plot = menuView.getPlot();

        Message.BANK_WITHDRAW_CUSTOM.send(clickedPlayer);

        menuView.closeView();

        PlayerChat.listen(player, message -> {
            try {
                BigDecimal newAmount = BigDecimal.valueOf(Double.parseDouble(message));
                BankTransaction bankTransaction = plot.getPlotBank().withdrawMoney(clickedPlayer, newAmount, null);
                Menus.MENU_PLOT_BANK.handleWithdraw(clickedPlayer, plot, bankTransaction,
                        getTemplate().successSound, getTemplate().failSound, newAmount);
            } catch (IllegalArgumentException ex) {
                Message.INVALID_AMOUNT.send(clickedPlayer, message);
            }

            PlayerChat.remove(player);

            menuView.refreshView();

            return true;
        });
    }

    public static class Builder extends AbstractMenuTemplateButton.AbstractBuilder<PlotMenuView> {

        private GameSound successSound;
        private GameSound failSound;

        public Builder setSuccessSound(GameSound successSound) {
            this.successSound = successSound;
            return this;
        }

        public Builder setFailSound(GameSound failSound) {
            this.failSound = failSound;
            return this;
        }

        @Override
        public MenuTemplateButton<PlotMenuView> build() {
            return new Template(buttonItem, commands, requiredPermission, lackPermissionSound, successSound, failSound);
        }

    }

    public static class Template extends MenuTemplateButtonImpl<PlotMenuView> {

        @Nullable
        private final GameSound successSound;
        @Nullable
        private final GameSound failSound;

        Template(@Nullable TemplateItem buttonItem, @Nullable List<String> commands, @Nullable String requiredPermission,
                 @Nullable GameSound lackPermissionSound, @Nullable GameSound successSound, @Nullable GameSound failSound) {
            super(buttonItem, null, commands, requiredPermission, lackPermissionSound,
                    BankCustomWithdrawButton.class, BankCustomWithdrawButton::new);
            this.successSound = successSound;
            this.failSound = failSound;
        }

    }

}

package com.bgsoftware.superiorskyblock.core.menu.button.impl;

import com.bgsoftware.superiorskyblock.api.menu.button.MenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.menu.button.PagedMenuTemplateButton;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.itemstack.ItemBuilder;
import com.bgsoftware.superiorskyblock.core.menu.button.AbstractPagedMenuButton;
import com.bgsoftware.superiorskyblock.core.menu.button.PagedMenuTemplateButtonImpl;
import com.bgsoftware.superiorskyblock.core.menu.impl.MenuPlotRatings;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RatingsPagedObjectButton extends AbstractPagedMenuButton<MenuPlotRatings.View, MenuPlotRatings.RatingInfo> {

    private RatingsPagedObjectButton(MenuTemplateButton<MenuPlotRatings.View> templateButton, MenuPlotRatings.View menuView) {
        super(templateButton, menuView);
    }

    @Override
    public void onButtonClick(InventoryClickEvent clickEvent) {
        // Dummy button
    }

    @Override
    public ItemStack modifyViewItem(ItemStack buttonItem) {
        SuperiorPlayer ratingPlayer = plugin.getPlayers().getSuperiorPlayer(pagedObject.getPlayerUUID());

        return new ItemBuilder(buttonItem)
                .replaceAll("{0}", ratingPlayer.getName())
                .replaceAll("{1}", Formatters.RATING_FORMATTER.format(pagedObject.getRating().getValue(), ratingPlayer.getUserLocale()))
                .asSkullOf(ratingPlayer)
                .build(ratingPlayer);
    }

    public static class Builder extends PagedMenuTemplateButtonImpl.AbstractBuilder<MenuPlotRatings.View, MenuPlotRatings.RatingInfo> {

        @Override
        public PagedMenuTemplateButton<MenuPlotRatings.View, MenuPlotRatings.RatingInfo> build() {
            return new PagedMenuTemplateButtonImpl<>(buttonItem, clickSound, commands, requiredPermission,
                    lackPermissionSound, nullItem, getButtonIndex(), RatingsPagedObjectButton.class,
                    RatingsPagedObjectButton::new);
        }

    }

}

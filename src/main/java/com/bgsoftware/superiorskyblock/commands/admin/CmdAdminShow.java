package com.bgsoftware.superiorskyblock.commands.admin;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.IAdminPlotCommand;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.plot.PlotUtils;
import com.bgsoftware.superiorskyblock.module.BuiltinModules;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeBlockLimits;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeCropGrowth;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeEntityLimits;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypePlotEffects;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeMobDrops;
import com.bgsoftware.superiorskyblock.module.upgrades.type.UpgradeTypeSpawnerRates;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmdAdminShow implements IAdminPlotCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("show", "info");
    }

    @Override
    public String getPermission() {
        return "superior.admin.show";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "admin show <" +
                Message.COMMAND_ARGUMENT_PLAYER_NAME.getMessage(locale) + "/" +
                Message.COMMAND_ARGUMENT_PLOT_NAME.getMessage(locale) + ">";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_ADMIN_SHOW.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }

    @Override
    public boolean supportMultiplePlots() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, @Nullable SuperiorPlayer targetPlayer, Plot plot, String[] args) {
        java.util.Locale locale = PlayerLocales.getLocale(sender);
        long lastTime = plot.getLastTimeUpdate();

        StringBuilder infoMessage = new StringBuilder();

        if (!Message.PLOT_INFO_HEADER.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_HEADER.getMessage(locale)).append("\n");

        // Plot owner
        if (!Message.PLOT_INFO_OWNER.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_OWNER.getMessage(locale, plot.getOwner().getName())).append("\n");

        // Plot name
        if (!Message.PLOT_INFO_NAME.isEmpty(locale) && !plot.getName().isEmpty())
            infoMessage.append(Message.PLOT_INFO_NAME.getMessage(locale, plot.getName())).append("\n");

        // Plot location
        if (!Message.PLOT_INFO_LOCATION.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_LOCATION.getMessage(locale, Formatters.LOCATION_FORMATTER.format(
                    plot.getCenter(World.Environment.NORMAL)))).append("\n");

        // Plot last time updated
        if (lastTime != -1) {
            if (!Message.PLOT_INFO_LAST_TIME_UPDATED.isEmpty(locale)) {

                infoMessage.append(Message.PLOT_INFO_LAST_TIME_UPDATED.getMessage(locale, Formatters.TIME_FORMATTER.format(
                        Duration.ofMillis(System.currentTimeMillis() - (lastTime * 1000)), locale))).append("\n");
            }
        } else {
            if (!Message.PLOT_INFO_LAST_TIME_UPDATED_CURRENTLY_ACTIVE.isEmpty(locale)) {
                infoMessage.append(Message.PLOT_INFO_LAST_TIME_UPDATED_CURRENTLY_ACTIVE.getMessage(locale)).append("\n");
            }
        }

        // Plot rate
        if (!Message.PLOT_INFO_RATE.isEmpty(locale)) {
            double rating = plot.getTotalRating();
            infoMessage.append(Message.PLOT_INFO_RATE.getMessage(locale, Formatters.RATING_FORMATTER.format(rating, locale),
                    Formatters.NUMBER_FORMATTER.format(rating), plot.getRatingAmount())).append("\n");
        }

        if (BuiltinModules.BANK.isEnabled()) {
            // Plot balance
            if (!Message.PLOT_INFO_BANK.isEmpty(locale))
                infoMessage.append(Message.PLOT_INFO_BANK.getMessage(locale, plot.getPlotBank().getBalance())).append("\n");
        }

        // Plot bonus worth
        if (!Message.PLOT_INFO_BONUS.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_BONUS.getMessage(locale, plot.getBonusWorth())).append("\n");

        // Plot bonus level
        if (!Message.PLOT_INFO_BONUS_LEVEL.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_BONUS_LEVEL.getMessage(locale, plot.getBonusLevel())).append("\n");

        // Plot worth
        if (!Message.PLOT_INFO_WORTH.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_WORTH.getMessage(locale, plot.getWorth())).append("\n");

        // Plot level
        if (!Message.PLOT_INFO_LEVEL.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_LEVEL.getMessage(locale, plot.getPlotLevel())).append("\n");

        // Plot discord
        if (!Message.PLOT_INFO_DISCORD.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_DISCORD.getMessage(locale, plot.getDiscord())).append("\n");

        // Plot paypal
        if (!Message.PLOT_INFO_PAYPAL.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_PAYPAL.getMessage(locale, plot.getPaypal())).append("\n");

        boolean upgradesModule = BuiltinModules.UPGRADES.isEnabled();

        if (upgradesModule) {
            // Plot upgrades
            if (!Message.PLOT_INFO_ADMIN_UPGRADES.isEmpty(locale) && !Message.PLOT_INFO_ADMIN_UPGRADE_LINE.isEmpty(locale)) {
                StringBuilder upgradesString = new StringBuilder();
                for (Upgrade upgrade : plugin.getUpgrades().getUpgrades()) {
                    upgradesString.append(Message.PLOT_INFO_ADMIN_UPGRADE_LINE.getMessage(locale, upgrade.getName(), plot.getUpgradeLevel(upgrade).getLevel())).append("\n");
                }
                infoMessage.append(Message.PLOT_INFO_ADMIN_UPGRADES.getMessage(locale, upgradesString));
            }
        }

        // Plot admin size
        if (!Message.PLOT_INFO_ADMIN_SIZE.isEmpty(locale)) {
            infoMessage.append(Message.PLOT_INFO_ADMIN_SIZE.getMessage(locale, plot.getPlotSize()));
            if (plot.getPlotSizeRaw() != plot.getPlotSize())
                infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
            infoMessage.append("\n");
        }

        // Plot team limit
        if (!Message.PLOT_INFO_ADMIN_TEAM_LIMIT.isEmpty(locale)) {
            infoMessage.append(Message.PLOT_INFO_ADMIN_TEAM_LIMIT.getMessage(locale, plot.getTeamLimit()));
            if (plot.getTeamLimitRaw() != plot.getTeamLimit())
                infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
            infoMessage.append("\n");
        }

        // Plot warps limit
        if (!Message.PLOT_INFO_ADMIN_WARPS_LIMIT.isEmpty(locale)) {
            infoMessage.append(Message.PLOT_INFO_ADMIN_WARPS_LIMIT.getMessage(locale, plot.getWarpsLimit()));
            if (plot.getWarpsLimitRaw() != plot.getWarpsLimit())
                infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
            infoMessage.append("\n");
        }

        // Plot coop limit
        if (plugin.getSettings().isCoopMembers() && !Message.PLOT_INFO_ADMIN_COOP_LIMIT.isEmpty(locale)) {
            infoMessage.append(Message.PLOT_INFO_ADMIN_COOP_LIMIT.getMessage(locale, plot.getCoopLimit()));
            if (plot.getCoopLimitRaw() != plot.getCoopLimit())
                infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
            infoMessage.append("\n");
        }

        // Plot bank limit
        if (!Message.PLOT_INFO_ADMIN_BANK_LIMIT.isEmpty(locale)) {
            infoMessage.append(Message.PLOT_INFO_ADMIN_BANK_LIMIT.getMessage(locale,
                    Formatters.NUMBER_FORMATTER.format(plot.getBankLimit())));
            if (!plot.getBankLimitRaw().equals(plot.getBankLimit()))
                infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
            infoMessage.append("\n");
        }

        if (upgradesModule) {
            // Plot spawners multiplier
            if (!Message.PLOT_INFO_ADMIN_SPAWNERS_MULTIPLIER.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeSpawnerRates.class)) {
                infoMessage.append(Message.PLOT_INFO_ADMIN_SPAWNERS_MULTIPLIER.getMessage(locale, plot.getSpawnerRatesMultiplier()));
                if (plot.getSpawnerRatesRaw() != plot.getSpawnerRatesMultiplier())
                    infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                infoMessage.append("\n");
            }

            // Plot drops multiplier
            if (!Message.PLOT_INFO_ADMIN_DROPS_MULTIPLIER.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeMobDrops.class)) {
                infoMessage.append(Message.PLOT_INFO_ADMIN_DROPS_MULTIPLIER.getMessage(locale, plot.getMobDropsMultiplier()));
                if (plot.getMobDropsRaw() != plot.getMobDropsMultiplier())
                    infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                infoMessage.append("\n");
            }

            // Plot crops multiplier
            if (!Message.PLOT_INFO_ADMIN_CROPS_MULTIPLIER.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeCropGrowth.class)) {
                infoMessage.append(Message.PLOT_INFO_ADMIN_CROPS_MULTIPLIER.getMessage(locale, plot.getCropGrowthMultiplier()));
                if (plot.getCropGrowthRaw() != plot.getCropGrowthMultiplier())
                    infoMessage.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                infoMessage.append("\n");
            }

            // Plot entity limits
            if (!Message.PLOT_INFO_ADMIN_ENTITIES_LIMITS.isEmpty(locale) &&
                    !Message.PLOT_INFO_ADMIN_ENTITIES_LIMITS_LINE.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeEntityLimits.class)) {
                StringBuilder entitiesString = new StringBuilder();
                for (Map.Entry<Key, Integer> entry : plot.getEntitiesLimitsAsKeys().entrySet()) {
                    entitiesString.append(Message.PLOT_INFO_ADMIN_ENTITIES_LIMITS_LINE.getMessage(locale,
                            Formatters.CAPITALIZED_FORMATTER.format(entry.getKey().toString()), entry.getValue()));
                    if (!plot.getCustomEntitiesLimits().containsKey(entry.getKey()))
                        entitiesString.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                    entitiesString.append("\n");
                }
                infoMessage.append(Message.PLOT_INFO_ADMIN_ENTITIES_LIMITS.getMessage(locale, entitiesString));
            }

            // Plot block limits
            if (!Message.PLOT_INFO_ADMIN_BLOCKS_LIMITS.isEmpty(locale) &&
                    !Message.PLOT_INFO_ADMIN_BLOCKS_LIMITS_LINE.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypeBlockLimits.class)) {
                StringBuilder blocksString = new StringBuilder();
                for (Map.Entry<Key, Integer> entry : plot.getBlocksLimits().entrySet()) {
                    blocksString.append(Message.PLOT_INFO_ADMIN_BLOCKS_LIMITS_LINE.getMessage(locale,
                            Formatters.CAPITALIZED_FORMATTER.format(entry.getKey().toString()), entry.getValue()));
                    if (!plot.getCustomBlocksLimits().containsKey(entry.getKey()))
                        blocksString.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                    blocksString.append("\n");
                }
                infoMessage.append(Message.PLOT_INFO_ADMIN_BLOCKS_LIMITS.getMessage(locale, blocksString));
            }
        }

        if (BuiltinModules.GENERATORS.isEnabled()) {
            // Plot generator rates
            if (!Message.PLOT_INFO_ADMIN_GENERATOR_RATES.isEmpty(locale) && !Message.PLOT_INFO_ADMIN_GENERATOR_RATES_LINE.isEmpty(locale)) {
                for (World.Environment environment : World.Environment.values()) {
                    Map<Key, Integer> customGeneratorValues = plot.getCustomGeneratorAmounts(environment);
                    StringBuilder generatorString = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : plot.getGeneratorPercentages(environment).entrySet()) {
                        Key key = Keys.ofMaterialAndData(entry.getKey());
                        generatorString.append(Message.PLOT_INFO_ADMIN_GENERATOR_RATES_LINE.getMessage(locale,
                                Formatters.CAPITALIZED_FORMATTER.format(entry.getKey()),
                                Formatters.NUMBER_FORMATTER.format(PlotUtils.getGeneratorPercentageDecimal(plot, key, environment)),
                                plot.getGeneratorAmount(key, environment))
                        );
                        if (!customGeneratorValues.containsKey(key))
                            generatorString.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                        generatorString.append("\n");
                    }
                    infoMessage.append(Message.PLOT_INFO_ADMIN_GENERATOR_RATES.getMessage(locale, generatorString,
                            Formatters.CAPITALIZED_FORMATTER.format(environment.name())));
                }
            }
        }

        if (upgradesModule) {
            // Plot effects
            if (!Message.PLOT_INFO_ADMIN_PLOT_EFFECTS.isEmpty(locale) &&
                    !Message.PLOT_INFO_ADMIN_PLOT_EFFECTS_LINE.isEmpty(locale) &&
                    BuiltinModules.UPGRADES.isUpgradeTypeEnabled(UpgradeTypePlotEffects.class)) {
                StringBuilder blocksString = new StringBuilder();
                for (Map.Entry<PotionEffectType, Integer> entry : plot.getPotionEffects().entrySet()) {
                    blocksString.append(Message.PLOT_INFO_ADMIN_PLOT_EFFECTS_LINE.getMessage(locale,
                            Formatters.CAPITALIZED_FORMATTER.format(entry.getKey().getName()), entry.getValue())).append("\n");
                }
                infoMessage.append(Message.PLOT_INFO_ADMIN_PLOT_EFFECTS.getMessage(locale, blocksString));
            }
        }

        // Plot entity limits
        if (!Message.PLOT_INFO_ADMIN_ROLE_LIMITS.isEmpty(locale) && !Message.PLOT_INFO_ADMIN_ROLE_LIMITS_LINE.isEmpty(locale)) {
            StringBuilder entitiesString = new StringBuilder();
            for (Map.Entry<PlayerRole, Integer> entry : plot.getRoleLimits().entrySet()) {
                entitiesString.append(Message.PLOT_INFO_ADMIN_ROLE_LIMITS_LINE.getMessage(locale, entry.getKey(), entry.getValue()));
                if (!plot.getCustomRoleLimits().containsKey(entry.getKey()))
                    entitiesString.append(" ").append(Message.PLOT_INFO_ADMIN_VALUE_SYNCED.getMessage(locale));
                entitiesString.append("\n");
            }
            infoMessage.append(Message.PLOT_INFO_ADMIN_ROLE_LIMITS.getMessage(locale, entitiesString));
        }

        // Plot members
        if (!Message.PLOT_INFO_ROLES.isEmpty(locale)) {
            Map<PlayerRole, StringBuilder> rolesStrings = new HashMap<>();

            List<SuperiorPlayer> members = plot.getPlotMembers(false);

            if (!Message.PLOT_INFO_PLAYER_LINE.isEmpty(locale)) {
                members.forEach(superiorPlayer -> rolesStrings.computeIfAbsent(superiorPlayer.getPlayerRole(), role -> new StringBuilder())
                        .append(Message.PLOT_INFO_PLAYER_LINE.getMessage(locale, superiorPlayer.getName())).append("\n"));
            }

            rolesStrings.keySet().stream()
                    .sorted(Collections.reverseOrder(Comparator.comparingInt(PlayerRole::getWeight)))
                    .forEach(playerRole ->
                            infoMessage.append(Message.PLOT_INFO_ROLES.getMessage(locale, playerRole, rolesStrings.get(playerRole))));
        }

        if (!Message.PLOT_INFO_FOOTER.isEmpty(locale))
            infoMessage.append(Message.PLOT_INFO_FOOTER.getMessage(locale));

        Message.CUSTOM.send(sender, infoMessage.toString(), false);
    }

}

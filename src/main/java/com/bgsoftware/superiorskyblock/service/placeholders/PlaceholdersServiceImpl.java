package com.bgsoftware.superiorskyblock.service.placeholders;

import com.bgsoftware.common.annotations.NotNull;
import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.SortingType;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.api.service.placeholders.PlotPlaceholderParser;
import com.bgsoftware.superiorskyblock.api.service.placeholders.PlaceholdersService;
import com.bgsoftware.superiorskyblock.api.service.placeholders.PlayerPlaceholderParser;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.formatting.Formatters;
import com.bgsoftware.superiorskyblock.core.key.ConstantKeys;
import com.bgsoftware.superiorskyblock.core.key.Keys;
import com.bgsoftware.superiorskyblock.external.placeholders.PlaceholdersProvider;
import com.bgsoftware.superiorskyblock.plot.privilege.PlotPrivileges;
import com.bgsoftware.superiorskyblock.plot.top.SortingTypes;
import com.bgsoftware.superiorskyblock.service.IService;
import com.google.common.collect.ImmutableMap;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholdersServiceImpl implements PlaceholdersService, IService {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private static final Pattern PLOT_PLACEHOLDER_PATTERN = Pattern.compile("plot_(.+)");
    private static final Pattern PLAYER_PLACEHOLDER_PATTERN = Pattern.compile("player_(.+)");
    private static final Pattern PERMISSION_PLACEHOLDER_PATTERN = Pattern.compile("plot_permission_(.+)");
    private static final Pattern PERMISSION_ROLE_PLACEHOLDER_PATTERN = Pattern.compile("plot_permission_role_(.+)");
    private static final Pattern UPGRADE_PLACEHOLDER_PATTERN = Pattern.compile("plot_upgrade_(.+)");
    private static final Pattern COUNT_PLACEHOLDER_PATTERN = Pattern.compile("plot_count_(.+)");
    private static final Pattern BLOCK_LIMIT_PLACEHOLDER_PATTERN = Pattern.compile("plot_block_limit_(.+)");
    private static final Pattern ENTITY_LIMIT_PLACEHOLDER_PATTERN = Pattern.compile("plot_entity_limit_(.+)");
    private static final Pattern ENTITY_COUNT_PLACEHOLDER_PATTERN = Pattern.compile("plot_entity_count_(.+)");
    private static final Pattern TOP_PLACEHOLDER_PATTERN = Pattern.compile("plot_top_(.+)");
    private static final Pattern TOP_WORTH_PLACEHOLDER_PATTERN = Pattern.compile("worth_(.+)");
    private static final Pattern TOP_LEVEL_PLACEHOLDER_PATTERN = Pattern.compile("level_(.+)");
    private static final Pattern TOP_RATING_PLACEHOLDER_PATTERN = Pattern.compile("rating_(.+)");
    private static final Pattern TOP_PLAYERS_PLACEHOLDER_PATTERN = Pattern.compile("players_(.+)");
    private static final Pattern TOP_VALUE_FORMAT_PLACEHOLDER_PATTERN = Pattern.compile("value_format_(.+)");
    private static final Pattern TOP_VALUE_RAW_PLACEHOLDER_PATTERN = Pattern.compile("value_raw_(.+)");
    private static final Pattern TOP_VALUE_PLACEHOLDER_PATTERN = Pattern.compile("value_(.+)");
    private static final Pattern TOP_LEADER_PLACEHOLDER_PATTERN = Pattern.compile("leader_(.+)");
    private static final Pattern TOP_CUSTOM_PLACEHOLDER_PATTERN = Pattern.compile("(\\d+)_(.+)");
    private static final Pattern MEMBER_PLACEHOLDER_PATTERN = Pattern.compile("member_(.+)");
    private static final Pattern VISITOR_LAST_JOIN_PLACEHOLDER_PATTERN = Pattern.compile("visitor_last_join_(.+)");
    private static final Pattern PLOT_FLAG_PLACEHOLDER_PATTERN = Pattern.compile("flag_(.+)");
    private static final Pattern MISSIONS_COMPLETED_PATTERN = Pattern.compile("missions_completed_(.+)");

    private static final Map<String, PlayerPlaceholderParser> PLAYER_PARSES =
            new ImmutableMap.Builder<String, PlayerPlaceholderParser>()
                    .put("texture", SuperiorPlayer::getTextureValue)
                    .put("role", superiorPlayer -> superiorPlayer.getPlayerRole().toString())
                    .put("role_display", superiorPlayer -> superiorPlayer.getPlayerRole().getDisplayName())
                    .put("locale", superiorPlayer -> Formatters.LOCALE_FORMATTER.format(superiorPlayer.getUserLocale()))
                    .put("world_border", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasWorldBorderEnabled(), superiorPlayer.getUserLocale()))
                    .put("blocks_stacker", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasBlocksStackerEnabled(), superiorPlayer.getUserLocale()))
                    .put("schematics", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasSchematicModeEnabled(), superiorPlayer.getUserLocale()))
                    .put("team_chat", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasTeamChatEnabled(), superiorPlayer.getUserLocale()))
                    .put("bypass", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasBypassModeEnabled(), superiorPlayer.getUserLocale()))
                    .put("disbands", superiorPlayer -> superiorPlayer.getDisbands() + "")
                    .put("panel", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasToggledPanel(), superiorPlayer.getUserLocale()))
                    .put("fly", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasPlotFlyEnabled(), superiorPlayer.getUserLocale()))
                    .put("chat_spy", superiorPlayer -> Formatters.BOOLEAN_FORMATTER.format(superiorPlayer.hasAdminSpyEnabled(), superiorPlayer.getUserLocale()))
                    .put("border_color", superiorPlayer ->
                            Formatters.BORDER_COLOR_FORMATTER.format(superiorPlayer.getBorderColor(), superiorPlayer.getUserLocale()))
                    .put("missions_completed", superiorPlayer -> superiorPlayer.getCompletedMissions().size() + "")
                    .build();

    @SuppressWarnings("ConstantConditions")
    private static final Map<String, PlotPlaceholderParser> PLOT_PARSES =
            new ImmutableMap.Builder<String, PlotPlaceholderParser>()
                    .put("center", (plot, superiorPlayer) ->
                            Formatters.LOCATION_FORMATTER.format(plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld())))
                    .put("x", (plot, superiorPlayer) ->
                            plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).getBlockX() + "")
                    .put("y", (plot, superiorPlayer) ->
                            plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).getBlockY() + "")
                    .put("z", (plot, superiorPlayer) ->
                            plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).getBlockZ() + "")
                    .put("world", (plot, superiorPlayer) ->
                            plot.getCenter(plugin.getSettings().getWorlds().getDefaultWorld()).getWorld().getName())
                    .put("team_size", (plot, superiorPlayer) -> plot.getPlotMembers(true).size() + "")
                    .put("team_size_online", (plot, superiorPlayer) ->
                            plot.getPlotMembers(true).stream().filter(SuperiorPlayer::isShownAsOnline).count() + "")
                    .put("team_limit", (plot, superiorPlayer) -> plot.getTeamLimit() + "")
                    .put("coop_limit", (plot, superiorPlayer) -> plot.getCoopLimit() + "")
                    .put("leader", (plot, superiorPlayer) -> plot.getOwner().getName())
                    .put("size_format", (plot, superiorPlayer) -> {
                        int size = plot.getPlotSize() * 2 + 1;
                        int rounded = 5 * (Math.round(size / 5.0F));
                        if (Math.abs(size - rounded) == 1)
                            size = rounded;
                        return size + " x " + size;
                    })
                    .put("size", (plot, superiorPlayer) -> {
                        int size = plot.getPlotSize() * 2 + 1;
                        return size + " x " + size;
                    })
                    .put("radius", (plot, superiorPlayer) -> plot.getPlotSize() + "")
                    .put("biome", (plot, superiorPlayer) -> Formatters.CAPITALIZED_FORMATTER.format(plot.getBiome().name()))
                    .put("level", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getPlotLevel()))
                    .put("level_raw", (plot, superiorPlayer) -> plot.getPlotLevel().toString())
                    .put("level_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getPlotLevel(), superiorPlayer.getUserLocale()))
                    .put("level_int", (plot, superiorPlayer) -> plot.getPlotLevel().toBigInteger().toString())
                    .put("worth", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getWorth()))
                    .put("worth_raw", (plot, superiorPlayer) -> plot.getWorth().toString())
                    .put("worth_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getWorth(), superiorPlayer.getUserLocale()))
                    .put("worth_int", (plot, superiorPlayer) -> plot.getWorth().toBigInteger().toString())
                    .put("raw_worth", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getRawWorth()))
                    .put("raw_worth_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getRawWorth(), superiorPlayer.getUserLocale()))
                    .put("bank", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getPlotBank().getBalance()))
                    .put("bank_raw", (plot, superiorPlayer) -> plot.getPlotBank().getBalance().toString())
                    .put("bank_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getPlotBank().getBalance(), superiorPlayer.getUserLocale()))
                    .put("bank_next_interest", (plot, superiorPlayer) ->
                            Formatters.TIME_FORMATTER.format(Duration.ofSeconds(plot.getNextInterest()), superiorPlayer.getUserLocale()))
                    .put("hoppers_limit", (plot, superiorPlayer) -> plot.getBlockLimit(ConstantKeys.HOPPER) + "")
                    .put("crops_multiplier", (plot, superiorPlayer) -> plot.getCropGrowthMultiplier() + "")
                    .put("spawners_multiplier", (plot, superiorPlayer) -> plot.getSpawnerRatesMultiplier() + "")
                    .put("drops_multiplier", (plot, superiorPlayer) -> plot.getMobDropsMultiplier() + "")
                    .put("discord", (plot, superiorPlayer) ->
                            plot.hasPermission(superiorPlayer, PlotPrivileges.DISCORD_SHOW) ? plot.getDiscord() : "None")
                    .put("paypal", (plot, superiorPlayer) ->
                            plot.hasPermission(superiorPlayer, PlotPrivileges.PAYPAL_SHOW) ? plot.getPaypal() : "None")
                    .put("discord_all", (plot, superiorPlayer) -> plot.getDiscord())
                    .put("paypal_all", (plot, superiorPlayer) -> plot.getPaypal())
                    .put("exists", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot != null, superiorPlayer.getUserLocale()))
                    .put("locked", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot.isLocked(), superiorPlayer.getUserLocale()))
                    .put("name", (plot, superiorPlayer) -> {
                        return plugin.getSettings().getPlotNames().isColorSupport() ?
                                Formatters.COLOR_FORMATTER.format(plot.getName()) : plot.getName();
                    })
                    .put("name_leader", (plot, superiorPlayer) -> {
                        return plot.getName().isEmpty() ? plot.getOwner().getName() :
                                plugin.getSettings().getPlotNames().isColorSupport() ?
                                        Formatters.COLOR_FORMATTER.format(plot.getName()) : plot.getName();
                    })
                    .put("is_leader", (plot, superiorPlayer) ->
                            Formatters.BOOLEAN_FORMATTER.format(plot.getOwner().equals(superiorPlayer), superiorPlayer.getUserLocale()))
                    .put("is_member", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot.isMember(superiorPlayer), superiorPlayer.getUserLocale()))
                    .put("is_coop", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot.isCoop(superiorPlayer), superiorPlayer.getUserLocale()))
                    .put("rating", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getTotalRating()))
                    .put("rating_amount", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getRatingAmount()))
                    .put("rating_stars", (plot, superiorPlayer) ->
                            Formatters.RATING_FORMATTER.format(plot.getTotalRating(), superiorPlayer.getUserLocale()))
                    .put("warps_limit", (plot, superiorPlayer) -> plot.getWarpsLimit() + "")
                    .put("warps", (plot, superiorPlayer) -> plot.getPlotWarps().size() + "")
                    .put("creation_time", (plot, superiorPlayer) -> plot.getCreationTimeDate() + "")
                    .put("total_worth", (plot, superiorPlayer) ->
                            Formatters.NUMBER_FORMATTER.format(plugin.getGrid().getTotalWorth()))
                    .put("total_worth_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plugin.getGrid().getTotalWorth(), superiorPlayer.getUserLocale()))
                    .put("total_level", (plot, superiorPlayer) ->
                            Formatters.NUMBER_FORMATTER.format(plugin.getGrid().getTotalLevel()))
                    .put("total_level_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plugin.getGrid().getTotalLevel(), superiorPlayer.getUserLocale()))
                    .put("nether_unlocked", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot.isNetherEnabled(), superiorPlayer.getUserLocale()))
                    .put("end_unlocked", (plot, superiorPlayer) -> Formatters.BOOLEAN_FORMATTER.format(plot.isEndEnabled(), superiorPlayer.getUserLocale()))
                    .put("visitors_count", (plot, superiorPlayer) -> {
                        return plot.getPlotVisitors(false).size() + "";
                    })
                    .put("bank_limit", (plot, superiorPlayer) -> Formatters.NUMBER_FORMATTER.format(plot.getBankLimit()))
                    .put("bank_limit_format", (plot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(plot.getBankLimit(), superiorPlayer.getUserLocale()))
                    .put("uuid", (plot, superiorPlayer) -> plot.getUniqueId() + "")
                    .build();

    private static final Map<SortingType, BiFunction<Plot, SuperiorPlayer, String>> TOP_VALUE_FORMAT_FUNCTIONS =
            new ImmutableMap.Builder<SortingType, BiFunction<Plot, SuperiorPlayer, String>>()
                    .put(SortingTypes.BY_WORTH, (targetPlot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(targetPlot.getWorth(), superiorPlayer.getUserLocale()))
                    .put(SortingTypes.BY_LEVEL, (targetPlot, superiorPlayer) ->
                            Formatters.FANCY_NUMBER_FORMATTER.format(targetPlot.getPlotLevel(), superiorPlayer.getUserLocale()))
                    .put(SortingTypes.BY_RATING, (targetPlot, superiorPlayer) ->
                            Formatters.NUMBER_FORMATTER.format(targetPlot.getTotalRating()))
                    .put(SortingTypes.BY_PLAYERS, (targetPlot, superiorPlayer) ->
                            Formatters.NUMBER_FORMATTER.format(targetPlot.getAllPlayersInside().size()))
                    .build();

    private static final Map<SortingType, Function<Plot, String>> TOP_VALUE_RAW_FUNCTIONS =
            new ImmutableMap.Builder<SortingType, Function<Plot, String>>()
                    .put(SortingTypes.BY_WORTH, targetPlot -> targetPlot.getWorth().toString())
                    .put(SortingTypes.BY_LEVEL, targetPlot -> targetPlot.getPlotLevel().toString())
                    .put(SortingTypes.BY_RATING, targetPlot -> targetPlot.getTotalRating() + "")
                    .put(SortingTypes.BY_PLAYERS, targetPlot -> targetPlot.getAllPlayersInside().size() + "")
                    .build();

    private static final Map<SortingType, Function<Plot, String>> TOP_VALUE_FUNCTIONS =
            new ImmutableMap.Builder<SortingType, Function<Plot, String>>()
                    .put(SortingTypes.BY_WORTH, targetPlot -> Formatters.NUMBER_FORMATTER.format(targetPlot.getWorth()))
                    .put(SortingTypes.BY_LEVEL, targetPlot -> Formatters.NUMBER_FORMATTER.format(targetPlot.getPlotLevel()))
                    .put(SortingTypes.BY_RATING, targetPlot -> Formatters.NUMBER_FORMATTER.format(targetPlot.getTotalRating()))
                    .put(SortingTypes.BY_PLAYERS, targetPlot -> Formatters.NUMBER_FORMATTER.format(targetPlot.getAllPlayersInside().size()))
                    .build();

    private final Map<String, PlotPlaceholderParser> CUSTOM_PLOT_PARSERS = new HashMap<>();
    private final Map<String, PlayerPlaceholderParser> CUSTOM_PLAYER_PARSERS = new HashMap<>();

    private final List<PlaceholdersProvider> placeholdersProviders = new LinkedList<>();

    public PlaceholdersServiceImpl() {
    }

    @Override
    public Class<?> getAPIClass() {
        return PlaceholdersService.class;
    }

    public void register(List<PlaceholdersProvider> placeholdersProviders) {
        this.placeholdersProviders.addAll(placeholdersProviders);
    }

    public String parsePlaceholders(@Nullable OfflinePlayer offlinePlayer, String str) {
        for (PlaceholdersProvider placeholdersProvider : placeholdersProviders)
            str = placeholdersProvider.parsePlaceholders(offlinePlayer, str);

        return str;
    }

    public String handlePluginPlaceholder(@Nullable OfflinePlayer offlinePlayer, String placeholder) {
        SuperiorPlayer superiorPlayer = offlinePlayer == null ? null :
                plugin.getPlayers().getSuperiorPlayer(offlinePlayer.getUniqueId());

        Optional<String> placeholderResult = Optional.empty();

        Matcher matcher;

        if (superiorPlayer != null) {
            PlayerPlaceholderParser customPlayerParser = CUSTOM_PLAYER_PARSERS.get(placeholder);
            if (customPlayerParser != null) {
                placeholderResult = Optional.ofNullable(customPlayerParser.apply(superiorPlayer));
            } else {
                boolean isLocationPlaceholder = placeholder.startsWith("location_");
                PlotPlaceholderParser customPlotParser = CUSTOM_PLOT_PARSERS.get(
                        isLocationPlaceholder ? placeholder.substring(9) : placeholder);
                if (customPlotParser != null) {
                    Plot plot = isLocationPlaceholder ? plugin.getGrid().getPlotAt(superiorPlayer.getLocation()) :
                            superiorPlayer.getPlot();
                    placeholderResult = Optional.ofNullable(customPlotParser.apply(plot, superiorPlayer));
                }
            }
        }

        if (!placeholderResult.isPresent()) {
            if ((matcher = PLAYER_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                String subPlaceholder = matcher.group(1).toLowerCase(Locale.ENGLISH);
                placeholderResult = parsePlaceholdersForPlayer(superiorPlayer, subPlaceholder);
            } else if ((matcher = PLOT_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                String subPlaceholder = matcher.group(1).toLowerCase(Locale.ENGLISH);
                Plot plot = superiorPlayer == null ? null : subPlaceholder.startsWith("location_") ?
                        plugin.getGrid().getPlotAt(superiorPlayer.getLocation()) : superiorPlayer.getPlot();
                placeholderResult = parsePlaceholdersForPlot(plot, superiorPlayer,
                        placeholder.replace("location_", ""),
                        subPlaceholder.replace("location_", ""));
            }
        }

        return placeholderResult.orElse(plugin.getSettings().getDefaultPlaceholders()
                .getOrDefault(placeholder, ""));
    }

    @Override
    public void registerPlaceholder(String placeholderName, PlayerPlaceholderParser placeholderFunction) {
        CUSTOM_PLAYER_PARSERS.put(placeholderName, placeholderFunction);
    }

    @Override
    public void registerPlaceholder(String placeholderName, PlotPlaceholderParser placeholderFunction) {
        CUSTOM_PLOT_PARSERS.put(placeholderName, placeholderFunction);
    }

    private static Optional<String> parsePlaceholdersForPlayer(@Nullable SuperiorPlayer superiorPlayer,
                                                               String subPlaceholder) {
        Matcher matcher;

        if (superiorPlayer != null) {
            if ((matcher = MISSIONS_COMPLETED_PATTERN.matcher(subPlaceholder)).matches()) {
                String categoryName = matcher.group(1);
                return Optional.of(superiorPlayer.getCompletedMissions().stream().filter(mission ->
                        mission.getMissionCategory().getName().equalsIgnoreCase(categoryName)).count() + "");
            }
        }

        return Optional.ofNullable(PLAYER_PARSES.get(subPlaceholder))
                .map(placeholderParser -> placeholderParser.apply(superiorPlayer));
    }

    private static Optional<String> parsePlaceholdersForPlot(@Nullable Plot plot,
                                                               @Nullable SuperiorPlayer superiorPlayer,
                                                               String placeholder, String subPlaceholder) {
        Matcher matcher;

        if (plot != null) {
            if ((matcher = PERMISSION_ROLE_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                return handlePermissionRolesPlaceholder(plot, matcher.group(1));
            }

            if (superiorPlayer != null) {
                if ((matcher = PERMISSION_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    return handlePermissionsPlaceholder(plot, superiorPlayer, matcher.group(1));
                } else if ((matcher = UPGRADE_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    String upgradeName = matcher.group(1);
                    return Optional.of(plot.getUpgradeLevel(plugin.getUpgrades()
                            .getUpgrade(upgradeName)).getLevel() + "");
                } else if ((matcher = COUNT_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    String keyName = matcher.group(1);
                    return Optional.of(Formatters.NUMBER_FORMATTER.format(plot
                            .getBlockCountAsBigInteger(Keys.ofMaterialAndData(keyName))));
                } else if ((matcher = BLOCK_LIMIT_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    String keyName = matcher.group(1);
                    return Optional.of(plot.getBlockLimit(Keys.ofMaterialAndData(keyName)) + "");
                } else if ((matcher = ENTITY_LIMIT_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    String keyName = matcher.group(1);
                    return Optional.of(plot.getEntityLimit(Keys.ofEntityType(keyName)) + "");
                } else if ((matcher = ENTITY_COUNT_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
                    String keyName = matcher.group(1);
                    return Optional.of(Formatters.NUMBER_FORMATTER.format(plot.getEntitiesTracker().getEntityCount(Keys.ofEntityType(keyName))));
                } else if ((matcher = MEMBER_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
                    return handleMembersPlaceholder(plot, matcher.group(1));
                } else if ((matcher = VISITOR_LAST_JOIN_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
                    String visitorName = matcher.group(1);
                    return Optional.of(plot.getUniqueVisitorsWithTimes().stream()
                            .filter(uniqueVisitor -> uniqueVisitor.getKey().getName().equalsIgnoreCase(visitorName))
                            .findFirst()
                            .map(Pair::getValue).map(value -> Formatters.DATE_FORMATTER.format(new Date(value)))
                            .orElse("Haven't Joined"));
                } else if ((matcher = PLOT_FLAG_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
                    return handlePlotFlagsPlaceholder(plot, matcher.group(1));
                }
            }
        }

        if ((matcher = TOP_PLACEHOLDER_PATTERN.matcher(placeholder)).matches()) {
            return handleTopPlotsPlaceholder(plot, superiorPlayer, matcher.group(1));
        } else {
            try {
                return Optional.ofNullable(PLOT_PARSES.get(subPlaceholder))
                        .map(placeholderParser -> placeholderParser.apply(plot, superiorPlayer));
            } catch (NullPointerException ignored) {
                // One of the plot parses failed due to invalid plot being sent.
            }
        }

        return Optional.empty();
    }

    private static Optional<String> handlePermissionRolesPlaceholder(@NotNull Plot plot,
                                                                     String placeholder) {
        try {
            PlotPrivilege plotPrivilege = PlotPrivilege.getByName(placeholder);
            return Optional.of(plot.getRequiredPlayerRole(plotPrivilege).getDisplayName());
        } catch (NullPointerException ex) {
            return Optional.empty();
        }
    }

    private static Optional<String> handlePermissionsPlaceholder(@NotNull Plot plot,
                                                                 @NotNull SuperiorPlayer superiorPlayer,
                                                                 String placeholder) {
        try {
            PlotPrivilege plotPrivilege = PlotPrivilege.getByName(placeholder);
            return Optional.of(plot.hasPermission(superiorPlayer, plotPrivilege) + "");
        } catch (NullPointerException ex) {
            return Optional.empty();
        }
    }

    private static Optional<String> handlePlotFlagsPlaceholder(@NotNull Plot plot, String placeholder) {
        try {
            PlotFlag plotFlag = PlotFlag.getByName(placeholder);
            return Optional.of(plot.hasSettingsEnabled(plotFlag) + "");
        } catch (NullPointerException ex) {
            return Optional.empty();
        }
    }

    private static Optional<String> handleTopPlotsPlaceholder(@Nullable Plot plot,
                                                                @Nullable SuperiorPlayer superiorPlayer,
                                                                String subPlaceholder) {
        Matcher matcher;
        SortingType sortingType;

        if ((matcher = TOP_WORTH_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
            sortingType = SortingTypes.BY_WORTH;
        } else if ((matcher = TOP_LEVEL_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
            sortingType = SortingTypes.BY_LEVEL;
        } else if ((matcher = TOP_RATING_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
            sortingType = SortingTypes.BY_RATING;
        } else if ((matcher = TOP_PLAYERS_PLACEHOLDER_PATTERN.matcher(subPlaceholder)).matches()) {
            sortingType = SortingTypes.BY_PLAYERS;
        } else {
            String sortingTypeName = subPlaceholder.split("_")[0];
            sortingType = SortingType.getByName(sortingTypeName);
        }

        if (sortingType == null)
            return Optional.empty();

        String placeholderValue = matcher.group(1);

        if (placeholderValue.equals("position"))
            return plot == null ? Optional.empty() : Optional.of((plugin.getGrid().getPlotPosition(plot, sortingType) + 1) + "");

        Function<Plot, String> getValueFunction;

        if ((matcher = TOP_VALUE_FORMAT_PLACEHOLDER_PATTERN.matcher(placeholderValue)).matches()) {
            getValueFunction = Optional.ofNullable(TOP_VALUE_FORMAT_FUNCTIONS.get(sortingType)).map(function ->
                    (Function<Plot, String>) targetPlot -> function.apply(targetPlot, superiorPlayer)).orElse(null);
        } else if ((matcher = TOP_VALUE_RAW_PLACEHOLDER_PATTERN.matcher(placeholderValue)).matches()) {
            getValueFunction = TOP_VALUE_RAW_FUNCTIONS.get(sortingType);
        } else if ((matcher = TOP_VALUE_PLACEHOLDER_PATTERN.matcher(placeholderValue)).matches()) {
            getValueFunction = TOP_VALUE_FUNCTIONS.get(sortingType);
        } else if ((matcher = TOP_LEADER_PLACEHOLDER_PATTERN.matcher(placeholderValue)).matches()) {
            getValueFunction = targetPlot -> targetPlot.getOwner().getName();
        } else if ((matcher = TOP_CUSTOM_PLACEHOLDER_PATTERN.matcher(placeholderValue)).matches()) {
            String customPlaceholder = matcher.group(2);
            getValueFunction = targetPlot -> parsePlaceholdersForPlot(targetPlot, superiorPlayer,
                    "superior_plot_" + customPlaceholder,
                    customPlaceholder).orElse(null);
        } else {
            getValueFunction = targetPlot -> targetPlot.getName().isEmpty() ?
                    targetPlot.getOwner().getName() : targetPlot.getName();
        }

        if (getValueFunction == null)
            return Optional.empty();

        int targetPosition;

        try {
            targetPosition = Integer.parseInt(matcher.matches() ? matcher.group(1) : placeholderValue);
        } catch (NumberFormatException error) {
            return Optional.empty();
        }

        Plot targetPlot = plugin.getGrid().getPlot(targetPosition - 1, sortingType);

        return Optional.ofNullable(targetPlot).map(getValueFunction);
    }

    private static Optional<String> handleMembersPlaceholder(@NotNull Plot plot, String placeholder) {
        List<SuperiorPlayer> members = plot.getPlotMembers(false);

        int targetMemberIndex = -1;

        try {
            targetMemberIndex = Integer.parseInt(placeholder) - 1;
        } catch (NumberFormatException ignored) {
        }

        if (targetMemberIndex < 0 || targetMemberIndex >= members.size())
            return Optional.empty();

        return Optional.of(members.get(targetMemberIndex).getName());
    }

}

package com.bgsoftware.superiorskyblock.plot.top;

import com.bgsoftware.superiorskyblock.api.enums.TopPlotMembersSorting;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.plot.SPlot;

import java.util.Comparator;

public class SortingComparators {

    public final static Comparator<SuperiorPlayer> PLAYER_NAMES_COMPARATOR = Comparator.comparing(SuperiorPlayer::getName);
    public final static Comparator<SPlot.UniqueVisitor> PAIRED_PLAYERS_NAMES_COMPARATOR =
            Comparator.comparing(o -> o.getSuperiorPlayer().getName());
    public final static Comparator<BankTransaction> BANK_TRANSACTIONS_COMPARATOR =
            Comparator.comparingInt(BankTransaction::getPosition);
    private final static Comparator<Plot> PLOT_NAMES_COMPARATOR = (o1, o2) -> {
        String firstName = o1.getName().isEmpty() ? o1.getOwner().getName() : o1.getName();
        String secondName = o2.getName().isEmpty() ? o2.getOwner().getName() : o2.getName();
        return firstName.compareTo(secondName);
    };
    public final static Comparator<Plot> WORTH_COMPARATOR = (o1, o2) -> {
        int compare = o2.getWorth().compareTo(o1.getWorth());
        return compare == 0 ? PLOT_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };
    public final static Comparator<Plot> LEVEL_COMPARATOR = (o1, o2) -> {
        int compare = o2.getPlotLevel().compareTo(o1.getPlotLevel());
        return compare == 0 ? PLOT_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };
    public final static Comparator<Plot> RATING_COMPARATOR = (o1, o2) -> {
        int totalRatingsCompare = Double.compare(o2.getTotalRating() * o2.getRatingAmount(), o1.getTotalRating() * o1.getRatingAmount());

        if (totalRatingsCompare == 0) {
            int ratingsAmountCompare = Integer.compare(o2.getRatingAmount(), o1.getRatingAmount());
            return ratingsAmountCompare == 0 ? PLOT_NAMES_COMPARATOR.compare(o1, o2) : ratingsAmountCompare;
        }

        return totalRatingsCompare;
    };
    public final static Comparator<Plot> PLAYERS_COMPARATOR = (o1, o2) -> {
        int compare = Integer.compare(o2.getAllPlayersInside().size(), o1.getAllPlayersInside().size());
        return compare == 0 ? PLOT_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };
    public final static Comparator<SuperiorPlayer> PLOT_ROLES_COMPARATOR = (o1, o2) -> {
        // Comparison is between o2 and o1 as the lower the weight is, the higher the player is.
        int compare = Integer.compare(o2.getPlayerRole().getWeight(), o1.getPlayerRole().getWeight());
        return compare == 0 ? PLAYER_NAMES_COMPARATOR.compare(o1, o2) : compare;
    };

    private SortingComparators() {

    }

    public static void initializeTopPlotMembersSorting() throws IllegalArgumentException {
        TopPlotMembersSorting.NAMES.setComparator(PLAYER_NAMES_COMPARATOR);
        TopPlotMembersSorting.ROLES.setComparator(PLOT_ROLES_COMPARATOR);
    }


}

package com.bgsoftware.superiorskyblock.plot.privilege;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.collections.EnumerateMap;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import com.google.common.base.Preconditions;

public class PlayerPrivilegeNode extends PrivilegeNodeAbstract {

    protected final SuperiorPlayer superiorPlayer;
    protected Plot plot;

    public PlayerPrivilegeNode(SuperiorPlayer superiorPlayer, Plot plot) {
        this.superiorPlayer = superiorPlayer;
        this.plot = plot;
    }

    public PlayerPrivilegeNode(SuperiorPlayer superiorPlayer, Plot plot, String permissions) {
        this.superiorPlayer = superiorPlayer;
        this.plot = plot;
        setPermissions(permissions, false);
    }

    private PlayerPrivilegeNode(@Nullable EnumerateMap<PlotPrivilege, PrivilegeStatus> privileges,
                                SuperiorPlayer superiorPlayer, Plot plot) {
        super(privileges);
        this.superiorPlayer = superiorPlayer;
        this.plot = plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    @Override
    public boolean hasPermission(PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
        return getStatus(PlotPrivileges.ALL) == PrivilegeStatus.ENABLED || getStatus(plotPrivilege) == PrivilegeStatus.ENABLED;
    }

    @Override
    public PrivilegeNodeAbstract clone() {
        return new PlayerPrivilegeNode(privileges, superiorPlayer, plot);
    }

    public void loadPrivilege(PlotPrivilege plotPrivilege, byte status) {
        privileges.put(plotPrivilege, PrivilegeStatus.of(status));
    }

    protected PrivilegeStatus getStatus(PlotPrivilege plotPrivilege) {
        PrivilegeStatus cachedStatus = privileges.getOrDefault(plotPrivilege, PrivilegeStatus.DEFAULT);

        if (cachedStatus != PrivilegeStatus.DEFAULT)
            return cachedStatus;

        PlayerRole playerRole = plot.isMember(superiorPlayer) ? superiorPlayer.getPlayerRole() :
                plot.isCoop(superiorPlayer) ? SPlayerRole.coopRole() : SPlayerRole.guestRole();

        return plot.hasPermission(playerRole, plotPrivilege) ? PrivilegeStatus.ENABLED : PrivilegeStatus.DISABLED;
    }

    public static class EmptyPlayerPermissionNode extends PlayerPrivilegeNode {

        public static final EmptyPlayerPermissionNode INSTANCE;

        static {
            INSTANCE = new EmptyPlayerPermissionNode();
        }

        EmptyPlayerPermissionNode() {
            this(null, null);
        }

        EmptyPlayerPermissionNode(SuperiorPlayer superiorPlayer, Plot plot) {
            super(null, superiorPlayer, plot);
        }

        @Override
        public boolean hasPermission(PlotPrivilege plotPrivilege) {
            Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
            return superiorPlayer != null && plot != null && super.hasPermission(plotPrivilege);
        }

        @Override
        protected PrivilegeStatus getStatus(PlotPrivilege plotPrivilege) {
            PlayerRole playerRole = plot.isMember(superiorPlayer) ? superiorPlayer.getPlayerRole() : plot.isCoop(superiorPlayer) ? SPlayerRole.coopRole() : SPlayerRole.guestRole();

            if (plot.hasPermission(playerRole, plotPrivilege))
                return PrivilegeStatus.ENABLED;

            return PrivilegeStatus.DISABLED;
        }

        @Override
        public void setPermission(PlotPrivilege permission, boolean value) {
            // Do nothing.
        }

    }

}

package com.bgsoftware.superiorskyblock.plot.privilege;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.collections.EnumerateMap;
import com.bgsoftware.superiorskyblock.core.threads.BukkitExecutor;
import com.bgsoftware.superiorskyblock.plot.role.SPlayerRole;
import com.google.common.base.Preconditions;

import java.util.LinkedList;
import java.util.List;

public class RolePrivilegeNode extends PrivilegeNodeAbstract {

    @Nullable
    private final SPlayerRole playerRole;
    private final List<RolePrivilegeNode> linkedNodes = new LinkedList<>();

    public RolePrivilegeNode(@Nullable SPlayerRole playerRole, @Nullable RolePrivilegeNode linkedNode) {
        this(playerRole, linkedNode, null);
    }

    public RolePrivilegeNode(@Nullable SPlayerRole playerRole, @Nullable RolePrivilegeNode linkedNode, @Nullable String permissions) {
        this.playerRole = playerRole;
        if (linkedNode != null)
            this.linkedNodes.add(linkedNode);
        if (!Text.isBlank(permissions))
            BukkitExecutor.sync(() -> setPermissions(permissions, playerRole != null), 1L);
    }

    private RolePrivilegeNode(EnumerateMap<PlotPrivilege, PrivilegeStatus> privileges,
                              @Nullable SPlayerRole playerRole, List<RolePrivilegeNode> linkedNodes) {
        super(privileges);
        this.playerRole = playerRole;
        for (RolePrivilegeNode linkedNode : linkedNodes)
            this.linkedNodes.add(linkedNode.clone());
    }

    public void linkNode(RolePrivilegeNode otherNode) {
        this.linkedNodes.add(otherNode);
    }

    @Override
    public boolean hasPermission(PlotPrivilege plotPrivilege) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");

        PrivilegeStatus status = getStatus(plotPrivilege);

        if (status != PrivilegeStatus.DEFAULT) {
            return status == PrivilegeStatus.ENABLED;
        }

        for (RolePrivilegeNode linkedNode : this.linkedNodes) {
            status = linkedNode.getStatus(plotPrivilege);

            if (status != PrivilegeStatus.DEFAULT) {
                return status == PrivilegeStatus.ENABLED;
            }
        }

        return playerRole != null && playerRole.getDefaultPermissions().hasPermission(plotPrivilege);
    }

    @Override
    public void setPermission(PlotPrivilege plotPrivilege, boolean value) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
        setPermission(plotPrivilege, value, true);
    }

    @Override
    public RolePrivilegeNode clone() {
        return new RolePrivilegeNode(privileges, playerRole, linkedNodes);
    }

    @Override
    protected boolean isDefault(PlotPrivilege plotPrivilege) {
        if (playerRole != null) {
            return playerRole.getDefaultPermissions().isDefault(plotPrivilege);
        }

        for (RolePrivilegeNode linkedNode : this.linkedNodes) {
            if (linkedNode.isDefault(plotPrivilege))
                return true;
        }

        return privileges.containsKey(plotPrivilege);
    }

    public PrivilegeStatus getStatus(PlotPrivilege permission) {
        return privileges.getOrDefault(permission, PrivilegeStatus.DEFAULT);
    }

    public void setPermission(PlotPrivilege permission, boolean value, boolean keepDisable) {
        if (!value && !keepDisable) {
            privileges.remove(permission);
        } else {
            super.setPermission(permission, value);
        }

        for (RolePrivilegeNode linkedNode : this.linkedNodes)
            linkedNode.setPermission(permission, false, false);
    }

    @Override
    public String toString() {
        return "RolePermissionNode" + privileges;
    }

    public static class EmptyRolePermissionNode extends RolePrivilegeNode {

        public static final EmptyRolePermissionNode INSTANCE;

        static {
            INSTANCE = new EmptyRolePermissionNode();
        }

        EmptyRolePermissionNode() {
            super(null, null);
        }

        @Override
        public boolean hasPermission(PlotPrivilege permission) {
            return false;
        }

        @Override
        public void setPermission(PlotPrivilege permission, boolean value) {
            // Do nothing.
        }

    }

}

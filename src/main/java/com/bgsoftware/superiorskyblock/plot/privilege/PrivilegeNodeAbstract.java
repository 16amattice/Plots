package com.bgsoftware.superiorskyblock.plot.privilege;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.PlotPrivilege;
import com.bgsoftware.superiorskyblock.api.plot.PermissionNode;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.collections.EnumerateMap;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.google.common.base.Preconditions;

import java.util.Map;

public abstract class PrivilegeNodeAbstract implements PermissionNode {

    protected final EnumerateMap<PlotPrivilege, PrivilegeStatus> privileges;

    protected PrivilegeNodeAbstract() {
        this.privileges = new EnumerateMap<>(PlotPrivilege.values());
    }

    protected PrivilegeNodeAbstract(EnumerateMap<PlotPrivilege, PrivilegeStatus> privileges) {
        this.privileges = new EnumerateMap<>(privileges);
    }

    protected void setPermissions(@Nullable String permissions, boolean checkDefaults) {
        if (Text.isBlank(permissions))
            return;

        String[] permission = permissions.split(";");
        for (String perm : permission) {
            String[] permissionSections = perm.split(":");
            try {
                PlotPrivilege plotPrivilege = PlotPrivilege.getByName(permissionSections[0]);
                if (permissionSections.length == 2) {
                    privileges.put(plotPrivilege, PrivilegeStatus.of(permissionSections[1]));
                } else {
                    if (!checkDefaults || !isDefault(plotPrivilege))
                        privileges.put(plotPrivilege, PrivilegeStatus.ENABLED);
                }
            } catch (NullPointerException ignored) {
                // Ignored - invalid privilege.
            } catch (Exception error) {
                Log.error(error, "An unexpected error while loading permissions for '", perm, "':");
            }
        }
    }

    @Override
    public abstract boolean hasPermission(PlotPrivilege permission);

    @Override
    public void setPermission(PlotPrivilege plotPrivilege, boolean value) {
        Preconditions.checkNotNull(plotPrivilege, "plotPrivilege parameter cannot be null.");
        this.privileges.put(plotPrivilege, value ? PrivilegeStatus.ENABLED : PrivilegeStatus.DISABLED);
    }

    @Override
    public Map<PlotPrivilege, Boolean> getCustomPermissions() {
        return this.privileges.collect(PlotPrivilege.values(),
                privilegeStatus -> privilegeStatus == PrivilegeStatus.ENABLED);
    }

    @Override
    public abstract PrivilegeNodeAbstract clone();

    protected boolean isDefault(PlotPrivilege plotPrivilege) {
        return false;
    }

    protected enum PrivilegeStatus {

        ENABLED,
        DISABLED,
        DEFAULT;

        static PrivilegeStatus of(String value) throws IllegalArgumentException {
            switch (value) {
                case "0":
                    return DISABLED;
                case "1":
                    return ENABLED;
                default:
                    return valueOf(value);
            }
        }

        static PrivilegeStatus of(byte value) throws IllegalArgumentException {
            switch (value) {
                case 0:
                    return DISABLED;
                case 1:
                    return ENABLED;
                default:
                    throw new IllegalArgumentException("Invalid privilege status: " + value);
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case ENABLED:
                    return "1";
                case DISABLED:
                    return "0";
                default:
                    return name();
            }
        }

    }


}

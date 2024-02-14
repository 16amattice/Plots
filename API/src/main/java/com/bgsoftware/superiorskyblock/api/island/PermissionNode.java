package com.bgsoftware.superiorskyblock.api.plot;

import java.util.Map;

public interface PermissionNode extends Cloneable {

    /**
     * Check whether or not the node has a permission.
     *
     * @param plotPrivilege The privilege to check.
     */
    boolean hasPermission(PlotPrivilege plotPrivilege);

    /**
     * Set whether or not the node should have a permission.
     *
     * @param plotPrivilege The privilege to set.
     * @param value           The value to set.
     */
    void setPermission(PlotPrivilege plotPrivilege, boolean value);

    /**
     * Get all permissions set using the provided method.
     * This does not include default permissions.
     */
    Map<PlotPrivilege, Boolean> getCustomPermissions();

}

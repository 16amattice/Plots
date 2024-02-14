package com.bgsoftware.superiorskyblock.api.plot;

import java.util.Map;

public class DelegatePermissionNode implements PermissionNode {

    protected final PermissionNode handle;

    protected DelegatePermissionNode(PermissionNode handle) {
        this.handle = handle;
    }

    @Override
    public boolean hasPermission(PlotPrivilege plotPrivilege) {
        return this.handle.hasPermission(plotPrivilege);
    }

    @Override
    public void setPermission(PlotPrivilege plotPrivilege, boolean value) {
        this.handle.setPermission(plotPrivilege, value);
    }

    @Override
    public Map<PlotPrivilege, Boolean> getCustomPermissions() {
        return this.handle.getCustomPermissions();
    }

}

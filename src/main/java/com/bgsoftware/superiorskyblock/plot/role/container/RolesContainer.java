package com.bgsoftware.superiorskyblock.plot.role.container;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.PlayerRole;

import java.util.List;

public interface RolesContainer {

    @Nullable
    PlayerRole getPlayerRole(int index);

    @Nullable
    PlayerRole getPlayerRoleFromId(int id);

    PlayerRole getPlayerRole(String name);

    List<PlayerRole> getRoles();

    void addPlayerRole(PlayerRole playerRole);

}

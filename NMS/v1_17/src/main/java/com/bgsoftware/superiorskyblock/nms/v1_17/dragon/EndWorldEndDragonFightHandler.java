package com.bgsoftware.superiorskyblock.nms.v1_17.dragon;

import com.bgsoftware.common.annotations.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EndDragonFight;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EndWorldEndDragonFightHandler extends EndDragonFight {

    private final Map<UUID, PlotEndDragonFight> worldDragonFightsMap = new HashMap<>();
    private final List<PlotEndDragonFight> worldDragonFightsList = new LinkedList<>();

    public EndWorldEndDragonFightHandler(ServerLevel serverLevel) {
        super(serverLevel, serverLevel.getSeed(), new CompoundTag());
    }

    @Override
    public void tick() {
        this.worldDragonFightsList.forEach(EndDragonFight::tick);
    }

    public void addDragonFight(UUID uuid, PlotEndDragonFight endDragonFight) {
        PlotEndDragonFight oldDragonFight = this.worldDragonFightsMap.put(uuid, endDragonFight);
        if (oldDragonFight != null)
            this.worldDragonFightsList.remove(oldDragonFight);
        this.worldDragonFightsList.add(endDragonFight);
    }

    @Nullable
    public PlotEndDragonFight getDragonFight(UUID uuid) {
        return this.worldDragonFightsMap.get(uuid);
    }

    @Nullable
    public PlotEndDragonFight removeDragonFight(UUID uuid) {
        PlotEndDragonFight endDragonFight = this.worldDragonFightsMap.remove(uuid);
        if (endDragonFight != null)
            this.worldDragonFightsList.remove(endDragonFight);
        return endDragonFight;
    }

}

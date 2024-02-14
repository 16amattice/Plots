package com.bgsoftware.superiorskyblock.plot.container.value;

public class FixedSyncedValue<V extends Number> implements SyncedValue<V> {

    private final V value;

    FixedSyncedValue(V value) {
        this.value = value;
    }

    @Override
    public V get() {
        return value;
    }

}

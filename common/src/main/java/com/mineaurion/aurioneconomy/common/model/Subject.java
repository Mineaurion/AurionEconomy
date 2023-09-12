package com.mineaurion.aurioneconomy.common.model;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class Subject {
    private @Nullable String name;
    private @Nullable UUID uuid;

    public Subject(@Nullable String name, @Nullable UUID uuid){
        this.name = name;
        this.uuid = uuid;
    }

    public  @Nullable String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
    }

    public  @Nullable UUID getUUID() {
        return uuid;
    }

    public @Nullable String getUUIDString(){
        return uuid != null ? uuid.toString() : "";
    }

    public Subject setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
}

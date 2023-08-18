package com.mineaurion.aurioneconomy.common.action;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.UUID;

public interface Action extends Comparable<Action> {

    @NonNull Instant getTimestamp();

    @NonNull Source getSource();

    @NonNull Target getTarget();

    @NonNull String getDescription();

    @NonNull Type getType();

    enum Type {
        ADD, WITHDRAW, SET
    };

    interface Source {
        UUID getUUID();
        String getName();
    }

    interface Target {
        UUID getUUID();
        String getName();
    }

    interface Builder {
        @NonNull Builder timestamp(@NonNull Instant timestamp);

        @NonNull Builder source(@NonNull UUID actor);

        @NonNull Builder sourceName(@NonNull String actorName);

        @NonNull Builder type(Type type);


        @NonNull Builder target(UUID acted);


        @NonNull Builder targetName(@NonNull String actedName);


        @NonNull Builder description(@NonNull String action);

        @NonNull Action build();

    }
}

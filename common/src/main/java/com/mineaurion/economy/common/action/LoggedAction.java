package com.mineaurion.economy.common.action;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class LoggedAction implements Action {

    public static Builder build() {
        return new Builder();
    }
    private final long timestamp;
    private final SourceImpl source;
    private final TargetImpl target;
    private final Type type;
    private final String description;

    private LoggedAction(long timestamp, UUID sourceUUID, String sourceName, UUID targetUUID, String targetName, Type type, String description) {
        this.timestamp = timestamp;
        this.source = new SourceImpl(sourceUUID, sourceName);
        this.target = new TargetImpl(targetUUID, targetName);
        this.type = type;
        this.description = description;
    }

    @Override
    public @NonNull Instant getTimestamp() {
        return Instant.ofEpochSecond(this.timestamp);
    }

    public Duration getDurationSince(){
        return Duration.between(getTimestamp(), Instant.now());
    }

    @Override
    public @NonNull Source getSource(){
        return this.source;
    }

    @Override
    public @NonNull Target getTarget(){
        return this.target;
    }

    @Override
    public @NonNull Type getType(){
        return this.type;
    }

    @Override
    public @NonNull String getDescription(){
        return this.description;
    }

    @Override
    public int compareTo(@NonNull Action other) {
        Objects.requireNonNull(other, "other");
        return ActionComparator.INSTANCE.compare(this, other);
    }

    public boolean matchesSearch(String query) {
        query = Objects.requireNonNull(query, "query").toLowerCase(Locale.ROOT);
        return this.source.name.toLowerCase(Locale.ROOT).contains(query) ||
                this.target.name.toLowerCase(Locale.ROOT).contains(query) ||
                this.description.toLowerCase(Locale.ROOT).contains(query);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Action)) return false;
        final Action that = (Action) o;

        return getTimestamp().equals(that.getTimestamp()) &&
                getSource().equals(that.getSource()) &&
                getTarget().equals(that.getTarget()) &&
                getDescription().equals(that.getDescription()) &&
                getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getSource(), getTarget(), getDescription(), getType());
    }

    private static final class SourceImpl implements Source {
        private final UUID uuid;
        private final String name;

        private SourceImpl(UUID uuid, String name){
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj == null || getClass() != obj.getClass()) return false;
            SourceImpl source = (SourceImpl) obj;
            return this.uuid.equals(source.uuid) && this.name.equals(source.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.uuid, this.name);
        }
    }

    private static final class TargetImpl implements Target {
        private final UUID uuid;
        private final String name;

        private TargetImpl(UUID uuid, String name){
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public UUID getUUID() {
            return this.uuid;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TargetImpl target = (TargetImpl) o;
            return Objects.equals(this.uuid, target.uuid) && this.name.equals(target.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.uuid, this.name);
        }
    }

    public static class Builder implements Action.Builder {
        private long timestamp = 0L;
        private UUID sourceUniqueId = null;
        private String sourceName = null;
        private UUID targetUniqueId = null;
        private String targetName = null;
        private Type targetType = null;
        private String description = null;

        @Override
        public @NonNull Builder timestamp(@NonNull Instant timestamp) {
            this.timestamp = timestamp.getEpochSecond();
            return this;
        }

        @Override
        public @NonNull Builder source(@NonNull UUID source) {
            this.sourceUniqueId = Objects.requireNonNull(source, "source");
            return this;
        }

        @Override
        public @NonNull Builder sourceName(@NonNull String sourceName) {
            this.sourceName = Objects.requireNonNull(sourceName, "sourceName");
            return this;
        }

        @Override
        public @NonNull Builder type(Type type) {
            this.targetType = Objects.requireNonNull(type, "type");
            return this;
        }

        @Override
        public @NonNull Builder target(UUID target) {
            this.targetUniqueId = target; // nullable
            return this;
        }

        @Override
        public @NonNull Builder targetName(@NonNull String targetName) {
            this.targetName = Objects.requireNonNull(targetName, "targetName");
            return this;
        }

        @Override
        public @NonNull Builder description(@NonNull String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        public Builder description(Object... args) {
            List<String> parts = new ArrayList<>();
            for (Object o : args) {
                if (o instanceof Duration) {
                    parts.add(o.toString());
                } else {
                    parts.add(String.valueOf(o));
                }
            }
            description(String.join(" ", parts));
            return this;
        }

        @Override
        public @NonNull LoggedAction build() {
            if (this.timestamp == 0L) {
                timestamp(Instant.now());
            }

            Objects.requireNonNull(this.sourceUniqueId, "sourceUniqueId");
            Objects.requireNonNull(this.sourceName, "sourceName");
            Objects.requireNonNull(this.targetType, "targetType");
            Objects.requireNonNull(this.targetName, "targetName");
            Objects.requireNonNull(this.description, "description");

            return new LoggedAction(this.timestamp, this.sourceUniqueId, this.sourceName, this.targetUniqueId, this.targetName, this.targetType, this.description);
        }
    }
}

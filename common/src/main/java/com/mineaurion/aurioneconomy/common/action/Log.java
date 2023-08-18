package com.mineaurion.aurioneconomy.common.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.mineaurion.aurioneconomy.common.misc.ImmutableCollectors;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

public class Log {

    private static final Log EMPTY = new Log(ImmutableList.of());

    public static Builder builder(){
        return new Builder();
    }

    public static Log empty(){
        return EMPTY;
    }

    private final SortedSet<LoggedAction> content;

    public Log(List<LoggedAction> content) {
        this.content = ImmutableSortedSet.copyOf(content);
    }

    public SortedSet<LoggedAction> getContent(){
        return this.content;
    }

    public SortedSet<LoggedAction> getContent(UUID actor){
        return this.content.stream()
                .filter(e -> e.getSource().getUUID().equals(actor))
                .collect(ImmutableCollectors.toSortedSet());
    }

    public SortedSet<LoggedAction> getUserHistory(UUID uuid){
        return this.content.stream()
                .filter(e -> e.getTarget().getUUID().equals(uuid))
                .collect(ImmutableCollectors.toSortedSet());
    }

    public static class Builder {
        private final List<LoggedAction> content = new ArrayList<>();

        public Builder add(LoggedAction e) {
            this.content.add(e);
            return this;
        }

        public Log build() {
            if (this.content.isEmpty()) {
                return EMPTY;
            }
            return new Log(this.content);
        }
    }


}

package com.mineaurion.aurioneconomy.common.action;

import java.util.Comparator;

final class ActionComparator implements Comparator<Action> {
    public static final Comparator<Action> INSTANCE = new ActionComparator();

    private ActionComparator() {

    }

    @Override
    public int compare(Action o1, Action o2) {
        int cmp = o1.getTimestamp().compareTo(o2.getTimestamp());
        if (cmp != 0) {
            return cmp;
        }

        Action.Source o1Source = o1.getSource();
        Action.Source o2Source = o2.getSource();

        cmp = o1Source.getUUID().compareTo(o2Source.getUUID());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1Source.getName().compareTo(o2Source.getName());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1Source.getName().compareTo(o2Source.getName());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1.getTarget().getUUID().compareTo(o2.getTarget().getUUID());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1.getType().compareTo(o2.getType());
        if (cmp != 0) {
            return cmp;
        }

        return o1.getDescription().compareTo(o2.getDescription());
    }
}
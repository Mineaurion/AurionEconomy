package com.mineaurion.aurioneconomy.common.storage;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum StorageType {

    // Remote databases
    MONGODB("MongoDB", "mongodb"), // Not implemented
    MARIADB("MariaDB", "mariadb"), // Not implemented
    MYSQL("MySQL", "mysql"),
    POSTGRESQL("PostgreSQL", "postgresql"), // Not implemented

    // Local databases
    SQLITE("SQLite", "sqlite"), // Not implemented
    H2("H2", "h2"); // Not implemented

    private final String name;

    private final List<String> identifiers;

    StorageType(String name, String... identifiers) {
        this.name = name;
        this.identifiers = ImmutableList.copyOf(identifiers);
    }

    public static StorageType parse(String name, StorageType def) {
        for (StorageType t : values()) {
            for (String id : t.getIdentifiers()) {
                if (id.equalsIgnoreCase(name)) {
                    return t;
                }
            }
        }
        return def;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIdentifiers() {
        return this.identifiers;
    }
}

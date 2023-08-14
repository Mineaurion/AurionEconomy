package com.mineaurion.economy.common.storage.database;

import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(EconomyPlugin plugin);

    void shutdown() throws Exception;

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;
}

package com.mineaurion.aurioneconomy.common.storage.database;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public interface ConnectionFactory {

    String getImplementationName();

    void init(AurionEconomyPlugin plugin);

    void shutdown() throws Exception;

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;
}

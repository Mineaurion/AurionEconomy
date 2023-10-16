package com.mineaurion.aurioneconomy.common.storage.database.hikari;

import com.mineaurion.aurioneconomy.common.misc.StorageCredentials;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public abstract class DriverBasedConnectionFactory extends HikariConnectionFactory {

    protected DriverBasedConnectionFactory(StorageCredentials configuration){
        super(configuration);
    }

    protected abstract String driverClassName();

    protected abstract String driverJdbcIdentifier();

    @Override
    protected void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password){
        config.setDriverClassName(driverClassName());
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", driverJdbcIdentifier(), address, port, databaseName));
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    protected void postInitialize(){
        super.postInitialize();

        // Calling Class.forName("<driver class name>") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver(driverClassName());
    }

    private static void deregisterDriver(String driverClassName) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}

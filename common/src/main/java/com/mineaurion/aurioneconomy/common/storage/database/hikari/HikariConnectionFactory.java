package com.mineaurion.aurioneconomy.common.storage.database.hikari;

import com.mineaurion.aurioneconomy.common.misc.StorageCredentials;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.storage.database.ConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract {@link  ConnectionFactory} using a {@link HikariDataSource}
 */
public abstract class HikariConnectionFactory implements ConnectionFactory {

    private final StorageCredentials configuration;
    private HikariDataSource hikari;

    public HikariConnectionFactory(StorageCredentials configuration) {
        this.configuration = configuration;
    }

    protected abstract  String defaultPort();

    /**
     * Config the {@link HikariConfig} with db properties
     * <p>Each driver do this with little difference</p>
     */
    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    /**
     * Allows the connection factory instance to override few properties before they are set
    */
    protected void overrideProperties(Map<String, Object> properties){
        // see https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Sets the given connection properties onto the config
     */
    protected void setProperties(HikariConfig config, Map<String, Object> properties){
        for(Map.Entry<String, Object> property: properties.entrySet()){
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * Called after the hikari pool has been initialised
     */
    protected void postInitialize(){
    }

    @Override
    public void init(AurionEconomyPlugin plugin){
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError e) {
            // TODO: better logging
            System.out.println("A " + e.getClass().getSimpleName() + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.");
            throw e;
        }

        // Pool name to have better logging output and to identify this is us
        config.setPoolName("economy-hikari");

        String[] addressSplit = this.configuration.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1]: defaultPort();

        try {
            configureDatabase(config, address, port, this.configuration.getDatabase(), this.configuration.getUsername(), this.configuration.getPassword());
        } catch (NoSuchMethodError e) {
            // TODO: better logging
            System.out.println("A " + e.getClass().getSimpleName() + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.");
        }

        // Extra connection properties from cfg
        Map<String, Object> properties = new HashMap<>(this.configuration.getProperties());

        // implementation make change/override on properties
        overrideProperties(properties);

        setProperties(config, properties);

        // connection pool config
        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setKeepaliveTime(this.configuration.getKeepAliveTime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());

        // don't perform any init connection validation - we call #getConnection to setup schema anyway
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);

        postInitialize();
    }

    @Override
    public void shutdown(){
        if(this.hikari != null){
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(this.hikari == null){
            throw new SQLException("Unable to get a connection from the pool. (hikari is null");
        }

        Connection connection = this.hikari.getConnection();
        if(connection == null){
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }
}

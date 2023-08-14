package com.mineaurion.economy.common.storage.database;

import com.mineaurion.economy.common.action.Action;
import com.mineaurion.economy.common.action.Log;
import com.mineaurion.economy.common.action.LoggedAction;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String USER_ACCOUNT_SELECT = "SELECT uuid, balance FROM '{prefix}account' WHERE uuid= ?";
    private static final String USER_ACCOUNT_SET = "UPDATE '{prefix}account' SET balance=? WHERE uuid=?";

    private static final String ACTION_INSERT = "INSERT INTO '{prefix}actions' (time, actor_uuid, actor_name, type, acted_uuid, acted_name, action) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String ACTION_SELECT_ALL = "SELECT * FROM '{prefix}actions'";

    private final EconomyPlugin plugin;

    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;

    public SqlStorage(EconomyPlugin plugin, ConnectionFactory connectionFactory, String tablePrefix){
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public EconomyPlugin getPlugin(){
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    public ConnectionFactory getConnectionFactory(){
        return this.connectionFactory;
    }

    public Function<String, String> getStatementProcessor(){
        return this.statementProcessor;
    }

    @Override
    public void init() throws Exception {
        this.connectionFactory.init(this.plugin);

        boolean tableExists;
        try(Connection c = this.connectionFactory.getConnection()){
            tableExists = tableExists(c, this.statementProcessor.apply("{prefix}account"));
        }

        if(!tableExists){
            applySchema();
        }
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "com/mineaurion/economy/schema/" + this.connectionFactory.getImplementationName().toLowerCase(Locale.ROOT) + ".sql";
        try(InputStream is = this.plugin.getBootstrap().getResourceStream(schemaFileName)) {
            if( is == null){
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is).stream().map(this.statementProcessor).collect(Collectors.toList());

            try(Connection connection = this.connectionFactory.getConnection()){
                boolean utf8mb4Unsupported = false;

                try(Statement s = connection.createStatement()){
                    for(String query: statements){
                        s.addBatch(query);
                    }

                    try {
                        s.executeBatch();
                    } catch (BatchUpdateException e){
                        if(e.getMessage().contains("Unknown character set")){
                            utf8mb4Unsupported = true;
                        } else {
                            throw  e;
                        }
                    }
                }

                // try again
                if(utf8mb4Unsupported) {
                    try(Statement s = connection.createStatement()){
                        for(String query: statements){
                            s.addBatch(query.replace("utf8mb4", "utf8"));
                        }
                        s.executeBatch();
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            // TODO: better log
            System.out.println("Exception whilst disable SQL Storage");
        }
    }

    @Override
    public Log getLog() throws SQLException {
        final Log.Builder log = Log.builder();
        try(Connection c = this.connectionFactory.getConnection()) {
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(ACTION_SELECT_ALL))) {
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        log.add(readAction(rs));
                    }
                }
            }
        }
        return log.build();
    }

    @Override
    public void logAction(Action entry) throws SQLException {
        try(Connection c = this.connectionFactory.getConnection()) {
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(ACTION_INSERT))) {
                writeAction(entry, ps);
                ps.execute();
            }
        }
    }

    private static void writeAction(Action action, PreparedStatement ps) throws SQLException {
        ps.setLong(1, action.getTimestamp().getEpochSecond());
        ps.setString(2, action.getSource().getUUID().toString());
        ps.setString(3, action.getSource().getName());
        ps.setString(4, action.getType().toString());
        ps.setString(5, action.getTarget().getUUID().toString());
        ps.setString(6, action.getTarget().getName());
        ps.setString(7, action.getDescription());
    }

    private static LoggedAction readAction(ResultSet rs) throws SQLException {
        final String actedUUID = rs.getString("acted_uuid");
        return LoggedAction.build()
                .timestamp(Instant.ofEpochSecond(rs.getLong("time")))
                .source(UUID.fromString(rs.getString("actor_uuid")))
                .sourceName(rs.getString("actor_name"))
                .type(Action.Type.valueOf(rs.getString("type")))
                .target(UUID.fromString(actedUUID))
                .targetName(rs.getString("acted_name"))
                .description(rs.getString("action"))
                .build();
    }

    @Override
    public Integer getBalance(UUID uuid) throws SQLException {
        try(Connection c = this.connectionFactory.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(USER_ACCOUNT_SELECT))){
                ps.setString(1, uuid.toString());
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return rs.getInt("balance");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void setAmount(UUID uuid, int amount) throws Exception {
        try(Connection c = this.connectionFactory.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(USER_ACCOUNT_SET))){
                ps.setInt(1, amount);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    @Override
    public void addAmount(UUID uuid, int amount) throws Exception {
        Integer currentBalance = this.getBalance(uuid);
        if(currentBalance == null){
            System.out.println("The player was not found");
            return;
        }
        this.setAmount(uuid, currentBalance + amount);
    }

    @Override
    public void withdrawAmount(UUID uuid, int amount) throws Exception {
        Integer currentBalance = this.getBalance(uuid);
        if(currentBalance == null){
            System.out.println("The player was not found");
            return;
        }
        this.setAmount(uuid, currentBalance - amount);
    }

    @Override
    public boolean checkHasEnough(UUID uuid, int amountToCheck) throws Exception {
        Integer currentBalance = this.getBalance(uuid);
        return currentBalance >= amountToCheck;
    }

    private static boolean tableExists(Connection connection, String table) throws SQLException {
        try(ResultSet rs = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)){
            while (rs.next()) {
                if(rs.getString(3).equalsIgnoreCase(table)){
                    return true;
                }
            }
            return false;
        }
    }
}

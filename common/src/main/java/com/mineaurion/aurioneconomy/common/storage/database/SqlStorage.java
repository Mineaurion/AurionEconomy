package com.mineaurion.aurioneconomy.common.storage.database;

import com.mineaurion.aurioneconomy.common.model.Account;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String USER_ACCOUNT_CREATE = "INSERT INTO '{prefix}account' (uuid, balance) VALUES(?,?)";
    private static final String USER_ACCOUNT_SELECT = "SELECT uuid, balance FROM '{prefix}account' WHERE uuid= ?";
    private static final String USER_ACCOUNT_SET = "UPDATE '{prefix}account' SET balance=? WHERE uuid=?";
    private static final String USER_ACCOUNT_SELECT_ALL = "SELECT uuid, balance FROM '{prefix}account'";

    private static final String ACTION_INSERT = "INSERT INTO '{prefix}actions' (time, actor_uuid, actor_name, type, acted_uuid, acted_name, action) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String ACTION_SELECT_ALL = "SELECT * FROM '{prefix}actions'";

    private final AurionEconomyPlugin plugin;
    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;

    public SqlStorage(AurionEconomyPlugin plugin, ConnectionFactory connectionFactory, String tablePrefix){
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public AurionEconomyPlugin getPlugin(){
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
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

        String schemaFileName = "com/mineaurion/aurioneconomy/schema/" + this.connectionFactory.getImplementationName().toLowerCase(Locale.ROOT) + ".sql";
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
            this.plugin.getLogger().severe("Exception whilst disable SQL Storage");
        }
    }

    @Override
    public void createAccount(UUID uuid) throws Exception {
        try(Connection c = this.connectionFactory.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(USER_ACCOUNT_CREATE))){
                ps.setString(1, uuid.toString());
                ps.setInt(2, 0);
                ps.execute();
            }
        }
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

    @Override
    public List<Account> listAccounts() throws Exception {
        List<Account> accounts = new ArrayList<>();
        try(Connection c = this.connectionFactory.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(USER_ACCOUNT_SELECT_ALL))) {
                try(ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        accounts.add(
                                new Account(rs.getString("uuid"), rs.getInt("balance"))
                        );
                    }
                }
            }
        }
        return accounts;
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

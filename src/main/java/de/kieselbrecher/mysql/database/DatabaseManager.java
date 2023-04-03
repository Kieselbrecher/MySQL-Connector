package de.kieselbrecher.mysql.database;

import de.kieselbrecher.mysql.table.Table;
import de.kieselbrecher.mysql.table.TableBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private final int MAX_CONNECTIONS = 10;
    private final int MAX_ATTEMPTS = 3;

    private final DatabaseInfo info;
    private final BlockingQueue<Connection> connectionPool;

    private final List<Table> tables;

    public DatabaseManager(DatabaseInfo info) {
        this.info = info;
        this.connectionPool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
        for(int i = 1; i < MAX_CONNECTIONS; i++) {
            connectionPool.add(openConnection());
        }
        this.tables = new ArrayList<>();
    }

    private Connection openConnection() {
        try {
            return DriverManager.getConnection(info.getUrl(), info.getUser(), info.getPassword());
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to create a database connection.", exception);
        }
    }

    private Connection getConnection() throws InterruptedException {
        return connectionPool.take();
    }

    private void releaseConnection(Connection connection) {
        connectionPool.add(connection);
    }

    private void runSecureOperation(DatabaseOperation operation) {
        for(int i = 1; i < MAX_ATTEMPTS; i++) {
            Connection connection = null;
            try {
                connection = getConnection();
                operation.executeOperation(connection);
                releaseConnection(connection);
                return;
            } catch (SQLException | InterruptedException exception) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "mysql secure operation failed at attempt " + i + " from " + MAX_ATTEMPTS + " with " + exception.getClass().getSimpleName());
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    releaseConnection(openConnection());
                }
            }
        }
        throw new RuntimeException("mysql secure operation failed in all " + MAX_ATTEMPTS + " attempts.");
    }

    private void setStatementParameters(PreparedStatement statement, Object... values) {
        for(int i = 0; i < values.length; i++) {
            try {
                statement.setObject(i + 1, values[i]);
            } catch (SQLException exception) {
                throw new RuntimeException("An error occurred while setting the parameters for the prepared statement.", exception);
            }
        }
    }

    private String getErrorMessage(String query, Object... values) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < values.length; i++) {
            builder.append(values[i]);
            if(i < values.length - 1)
                builder.append(", ");
        }
        return String.format("An error occurred while executing the query '%s' with parameters '%s'", query, builder.toString());
    }

    public TableBuilder createTable(String name) {
        return new TableBuilder(this, name);
    }

    public Table getTable(String name, String identifier) {
        for(Table table : tables) {
            if(table.getName().equals(name))
                return table;
        }
        return new Table(this, name, identifier);
    }

    public CompletableFuture<DatabaseResult> executeQuery(String query, Object... values) {
        if(values == null)
            throw new IllegalArgumentException("The values array cannot be null");
        return CompletableFuture.supplyAsync(() -> {
            final DatabaseResult[] result = new DatabaseResult[1];
            try {
                runSecureOperation(connection -> {
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        setStatementParameters(statement, values);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            result[0] = new DatabaseResult(resultSet);
                        }
                    }
                });
            } catch (Exception exception) {
                throw new CompletionException(getErrorMessage(query, values), exception);
            }
            return result[0];
        });
    }

    public CompletableFuture<Void> executeUpdate(String query, Object... values) {
        if(values == null)
            throw new IllegalArgumentException("The values array cannot be null");
        return CompletableFuture.runAsync(() -> {
            try {
                runSecureOperation(connection -> {
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        setStatementParameters(statement, values);
                        statement.executeUpdate();
                    }
                });
            } catch (Exception exception) {
                throw new CompletionException(getErrorMessage(query, values), exception);
            }
        });
    }

    public void shutdown() {
        try {
            for(Connection connection : connectionPool) {
                connection.close();
            }
            connectionPool.clear();
        } catch (SQLException exception) {
            throw new RuntimeException("Error while closing database connections during shutdown.", exception);
        }
    }
}

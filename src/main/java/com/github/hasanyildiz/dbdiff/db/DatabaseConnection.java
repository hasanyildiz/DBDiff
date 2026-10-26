package com.github.hasanyildiz.dbdiff.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Açık bir Oracle bağlantısı ve bağlanılan şemanın adı. Kaynak ve hedef veritabanı için ayrı birer örnek kullanılır.
 *
 * @author EROLULKER
 */
public final class DatabaseConnection implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

    private final Connection connection;
    private final String schemaName;

    private DatabaseConnection(Connection connection, String schemaName) {
        this.connection = connection;
        this.schemaName = schemaName;
    }

    public static DatabaseConnection open(ConnectionSettings settings) throws SQLException {
        loadDriver();
        Connection connection = DriverManager.getConnection(
                settings.getJdbcUrl(), settings.getUsername(), settings.getPassword());
        LOGGER.fine(() -> "Bağlantı kuruldu: " + settings.getJdbcUrl());
        return new DatabaseConnection(connection, settings.getSchemaName());
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Oracle JDBC sürücüsü bulunamadı: " + DRIVER_CLASS, ex);
        }
    }

    /**
     * Bağlantının kullanılabilir olduğunu DUAL tablosundan basit bir sorgu yaparak denetler.
     *
     * @return sorgu bir satır döndürürse true
     */
    public boolean testConnection() throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT 1 FROM DUAL")) {
            return resultSet.next();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}

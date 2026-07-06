package com.krish.oibsip.reservation.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();
    private static String dbUrl;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties on classpath");
            }
            properties.load(input);
            dbUrl = properties.getProperty("db.url", "jdbc:sqlite:database/railnexus.db");
            // Load the SQLite JDBC driver class explicitly
            Class.forName("org.sqlite.JDBC");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load application configuration: " + e.getMessage());
            dbUrl = "jdbc:sqlite:database/railnexus.db";
        }
    }

    /**
     * Obtains a Connection to the SQLite database.
     * Automatically creates parent directories of the database file if needed,
     * and executes PRAGMA foreign_keys = ON; on the connection.
     */
    public static Connection getConnection() throws SQLException {
        // Extract file path from JDBC URL to create directories if they do not exist
        if (dbUrl.startsWith("jdbc:sqlite:")) {
            String filePath = dbUrl.substring("jdbc:sqlite:".length());
            File dbFile = new File(filePath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
        }
        
        Connection connection = DriverManager.getConnection(dbUrl);
        // Explicitly enable foreign keys for this connection
        try (var stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }
}

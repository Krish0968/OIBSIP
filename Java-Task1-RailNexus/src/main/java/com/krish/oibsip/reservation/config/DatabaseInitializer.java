package com.krish.oibsip.reservation.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    /**
     * Checks if the database is already initialized (specifically, checking if 'users' table has data).
     * If not, reads and executes schema.sql and seed.sql to configure the SQLite database.
     */
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (isDatabaseInitialized(conn)) {
                System.out.println("Database is already initialized and seeded.");
                return;
            }
            
            System.out.println("Initializing database structure...");
            String schemaSql = readSqlFile("database/schema.sql");
            executeSqlScript(conn, schemaSql);
            
            System.out.println("Seeding initial database records...");
            String seedSql = readSqlFile("database/seed.sql");
            executeSqlScript(conn, seedSql);
            
            System.out.println("Seeding user accounts programmatically...");
            seedUserAccounts(conn);
            
            System.out.println("Database initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("Critical Error initializing database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed.", e);
        }
    }

    private static void seedUserAccounts(Connection conn) throws Exception {
        String sql = "INSERT OR IGNORE INTO users (username, password_hash, full_name, created_at) VALUES (?, ?, ?, ?)";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            // User 1: krish / demo123
            ps.setString(1, "krish");
            ps.setString(2, com.krish.oibsip.reservation.util.PasswordUtil.hashPassword("demo123"));
            ps.setString(3, "Krish Sharma");
            ps.setString(4, java.time.LocalDateTime.now().toString());
            ps.addBatch();

            // User 2: passenger1 / pass123
            ps.setString(1, "passenger1");
            ps.setString(2, com.krish.oibsip.reservation.util.PasswordUtil.hashPassword("pass123"));
            ps.setString(3, "John Doe");
            ps.setString(4, java.time.LocalDateTime.now().toString());
            ps.addBatch();

            ps.executeBatch();
        }
    }

    private static boolean isDatabaseInitialized(Connection conn) {
        try {
            // Check if 'users' table exists and has rows
            try (Statement stmt = conn.createStatement();
                 ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (countRs.next() && countRs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Table doesn't exist, or select failed
        }
        return false;
    }

    private static String readSqlFile(String filePath) throws Exception {
        // Try reading from relative file path first
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
        
        // Fallback to classpath resource
        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                // If it is in src/main/resources, the jar packaging puts it in root.
                // Let's try filename fallback
                String resourcePath = filePath.contains("/") ? filePath.substring(filePath.lastIndexOf("/") + 1) : filePath;
                try (InputStream fallbackIn = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath)) {
                    if (fallbackIn == null) {
                        throw new RuntimeException("SQL file not found on disk or classpath: " + filePath);
                    }
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(fallbackIn))) {
                        return reader.lines().collect(Collectors.joining("\n"));
                    }
                }
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

    private static void executeSqlScript(Connection conn, String sqlScript) throws Exception {
        // Simple command-splitter by semicolon
        // We replace comment lines first to avoid statement issues
        String cleanedSql = sqlScript.replaceAll("(?m)^--.*$", "");
        String[] statements = cleanedSql.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }
}

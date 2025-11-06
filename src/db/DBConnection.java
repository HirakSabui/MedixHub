package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection
 *
 * Provides a reusable method to obtain a JDBC Connection to the
 * local MySQL database `medical_store`.
 *
 * Update the DB_URL, USER and PASS if your local MySQL credentials differ.
 */
public class DBConnection {

    // Defaults come from environment variables if present, otherwise reasonable local defaults.
    private static final String DEFAULT_DB_URL = System.getenv().getOrDefault(
        "DB_URL",
        "jdbc:mysql://localhost:3305/medical_store?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"
    );
    private static final String DEFAULT_DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DEFAULT_DB_PASS = System.getenv().getOrDefault("DB_PASS", "Hir@k2003");

    // JDBC URL, username and password of MySQL server (mutable at runtime if needed)
    private static volatile String DB_URL = DEFAULT_DB_URL;
    private static volatile String USER = DEFAULT_DB_USER;
    private static volatile String PASS = DEFAULT_DB_PASS;

    // Flag to indicate whether the JDBC driver class was successfully loaded
    private static boolean DRIVER_AVAILABLE = false;

    // Load the MySQL JDBC driver (optional for modern drivers but kept for clarity)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DRIVER_AVAILABLE = true;
        } catch (ClassNotFoundException e) {
            // Do not print stack traces here; we'll surface a helpful message when getConnection is called.
            DRIVER_AVAILABLE = false;
        }
    }

    /**
     * Returns a Connection to the medical_store database.
     *
     * @return Connection if successful, otherwise null
     */
    public static Connection getConnection() {
        if (!DRIVER_AVAILABLE) {
            // Inform the user with actionable steps
            String msg = "MySQL JDBC driver is not available on the classpath.\n" +
                    "Please download the MySQL Connector/J JAR and place it into the project's lib folder (lib/mysql-connector-j.jar).\n" +
                    "Then run the application with the connector on the classpath, for example (PowerShell):\n\n" +
                    "javac -cp \".;lib/mysql-connector-j.jar\" src\\Main.java src\\db\\DBConnection.java src\\ui\\*.java\n" +
                    "java -cp \".;lib/mysql-connector-j.jar;src\" Main\n\n" +
                    "If you already have the JAR, ensure the path is correct.\n" +
                    "Current DB URL: " + DB_URL;
            try {
                javax.swing.JOptionPane.showMessageDialog(null, msg, "JDBC Driver Missing", javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (Exception ignore) {
                System.err.println(msg);
            }
            return null;
        }

        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            String err = "Failed to create DB connection: " + e.getMessage();
            try {
                javax.swing.JOptionPane.showMessageDialog(null, err, "DB Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (Exception ignore) {
                System.err.println(err);
            }
            return null;
        }
    }

    // Update DB configuration at runtime
    public static synchronized void setConfig(String url, String user, String pass) {
        if (url != null && !url.isEmpty()) DB_URL = url;
        if (user != null) USER = user;
        if (pass != null) PASS = pass;
    }

    // Test connection with current settings. Returns null on success, otherwise error message.
    public static String testConnection() {
        if (!DRIVER_AVAILABLE) return "JDBC driver not found on classpath.";
        try (Connection c = DriverManager.getConnection(DB_URL, USER, PASS)) {
            if (c != null && !c.isClosed()) return null;
            return "Connection returned null or closed.";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    // Test connection with explicit parameters without changing saved config
    public static String testConnection(String url, String user, String pass) {
        if (!DRIVER_AVAILABLE) return "JDBC driver not found on classpath.";
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            if (c != null && !c.isClosed()) return null;
            return "Connection returned null or closed.";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    // Getters so other UI code can prefill fields
    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return USER;
    }

    public static String getDbPass() {
        return PASS;
    }
}

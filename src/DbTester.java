import db.DBConnection;

/**
 * DbTester
 *
 * Small utility to test DB connectivity from the command line and print a single-line result.
 */
public class DbTester {
    public static void main(String[] args) {
        String res = DBConnection.testConnection();
        if (res == null) {
            System.out.println("OK: connected");
        } else {
            System.out.println("ERROR: " + res);
        }
    }
}

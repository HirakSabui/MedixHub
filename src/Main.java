import javax.swing.SwingUtilities;
import ui.LoginFrame;
import ui.UIUtils;

/**
 * Main
 *
 * Application entry point: launches the LoginFrame.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize UI theme once
        UIUtils.init();

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}

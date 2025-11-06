package ui;

import java.awt.*;
import javax.swing.*;

/**
 * UIUtils
 *
 * Helper to apply a consistent look and feel and style frames.
 */
public class UIUtils {

    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // Apply theme once at application start
    public static void init() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback silently
        }

        // Set some default fonts
        UIManager.put("Label.font", DEFAULT_FONT);
        UIManager.put("Button.font", DEFAULT_FONT);
        UIManager.put("TextField.font", DEFAULT_FONT);
        UIManager.put("PasswordField.font", DEFAULT_FONT);
        UIManager.put("Table.font", DEFAULT_FONT);
        UIManager.put("Table.headerFont", DEFAULT_FONT);
    }

    // Standardize frame size, title and centering
    public static void styleFrame(JFrame frame, String title) {
        if (title != null) frame.setTitle(title);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
    }

    // Overload to style any Window (including JDialog)
    public static void styleWindow(java.awt.Window window, String title) {
        if (window instanceof JFrame) {
            styleFrame((JFrame) window, title);
            return;
        }
        window.setSize(600, 400);
        window.setLocationRelativeTo(null);
        if (window instanceof JDialog && title != null) {
            ((JDialog) window).setTitle(title);
        }
    }
}

package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * LoginFrame
 *
 * Simple login window that checks username and password against
 * the `users` table in the `medical_store` database.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Medical Store - Login");
    }

    // Initialize UI components and layout
    private void initComponents() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(12, 12));

    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Username row
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
    centerPanel.add(new JLabel("Username:"), gbc);
    gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER;
    usernameField = new JTextField(20);
    centerPanel.add(usernameField, gbc);

    // Password row
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
    centerPanel.add(new JLabel("Password:"), gbc);
    gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER;
    passwordField = new JPasswordField(20);
    centerPanel.add(passwordField, gbc);

    JButton loginBtn = new JButton("Login");
    JButton exitBtn = new JButton("Exit");
    JButton settingsBtn = new JButton("DB Settings");
    statusLabel = new JLabel(" ", SwingConstants.CENTER);
    statusLabel.setForeground(Color.GRAY);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
    buttonPanel.add(loginBtn);
    buttonPanel.add(settingsBtn);
    buttonPanel.add(exitBtn);

    add(centerPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    add(statusLabel, BorderLayout.NORTH);

        // Action listeners using named listeners to avoid unused-lambda warnings
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });

        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBSettingsDialog dlg = new DBSettingsDialog(LoginFrame.this);
                dlg.setVisible(true);
                // update status after dialog closes
                checkDbConnection();
            }
        });

        // Default button
        getRootPane().setDefaultButton(loginBtn);

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Check DB connection on startup
        checkDbConnection();
    }

    // Check DB connectivity and display status
    private void checkDbConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null || conn.isClosed()) {
                statusLabel.setText("DB: Not connected");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("DB: Connected");
                statusLabel.setForeground(new Color(0, 128, 0));
            }
        } catch (SQLException ex) {
            statusLabel.setText("DB Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    // Authenticate user against the users table using PreparedStatement
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use try-with-resources to ensure resources are closed
        try (Connection conn = DBConnection.getConnection() ) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Unable to connect to database.\nPlease check that MySQL is running and that DB_USER/DB_PASS environment variables (if used) are correct.", "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, password);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        // Login successful
                        JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Open Dashboard on EDT
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            new Dashboard().setVisible(true);
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (SQLException ex) {
            // show the SQL error message to help debugging
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // For quick manual testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame lf = new LoginFrame();
                lf.setVisible(true);
            }
        });
    }
}

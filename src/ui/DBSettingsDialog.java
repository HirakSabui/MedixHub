package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
// no direct ActionEvent/ActionListener imports needed when using lambdas or inner classes

/**
 * DBSettingsDialog
 *
 * Simple dialog to update DB URL/user/password at runtime and test the connection.
 */
public class DBSettingsDialog extends JDialog {

    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel resultLabel;

    public DBSettingsDialog(JFrame parent) {
        super(parent, "DB Settings", true);
        initComponents();
        pack();
        UIUtils.styleWindow(this, "DB Settings");
    }

    private void initComponents() {
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("JDBC URL:"), gbc);
        gbc.gridx=1; gbc.gridy=0; urlField = new JTextField(40); form.add(urlField, gbc);

        gbc.gridx=0; gbc.gridy=1; form.add(new JLabel("DB User:"), gbc);
        gbc.gridx=1; gbc.gridy=1; userField = new JTextField(20); form.add(userField, gbc);

        gbc.gridx=0; gbc.gridy=2; form.add(new JLabel("DB Password:"), gbc);
        gbc.gridx=1; gbc.gridy=2; passField = new JPasswordField(20); form.add(passField, gbc);

        add(form, BorderLayout.CENTER);

        resultLabel = new JLabel(" ");
        add(resultLabel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton testBtn = new JButton("Test Connection");
        JButton saveBtn = new JButton("Save and Close");
        JButton cancelBtn = new JButton("Cancel");

        btnPanel.add(testBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);

    // load current settings into fields from DBConnection
    urlField.setText(DBConnection.getDbUrl());
    userField.setText(DBConnection.getDbUser());

        testBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                doTest();
            }
        });

        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                DBConnection.setConfig(urlField.getText().trim(), userField.getText().trim(), new String(passField.getPassword()));
                String res = DBConnection.testConnection();
                if (res == null) {
                    JOptionPane.showMessageDialog(DBSettingsDialog.this, "Connection successful. Settings saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(DBSettingsDialog.this, "Failed: " + res, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }

    private void doTest() {
        String url = urlField.getText().trim();
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        String res = DBConnection.testConnection(url, user, pass);
        if (res == null) {
            resultLabel.setText("Connection successful");
            resultLabel.setForeground(new Color(0,128,0));
        } else {
            resultLabel.setText("Failed: " + res);
            resultLabel.setForeground(Color.RED);
        }
    }
}

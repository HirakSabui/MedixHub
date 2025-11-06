package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * AddMedicineFrame
 *
 * A simple form to insert a new medicine into the `medicines` table.
 */
public class AddMedicineFrame extends JFrame {

    private JTextField nameField;
    private JTextField companyField;
    private JTextField priceField;
    private JTextField stockField;

    public AddMedicineFrame() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Add New Medicine");
    }

    private void initComponents() {
    setLayout(new BorderLayout(12,12));

    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6,6,6,6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx=0; gbc.gridy=0; gbc.weightx = 0; form.add(new JLabel("Name:"), gbc);
    gbc.gridx=1; gbc.gridy=0; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; nameField = new JTextField(20); form.add(nameField, gbc);

    gbc.gridx=0; gbc.gridy=1; gbc.weightx = 0; gbc.gridwidth = 1; form.add(new JLabel("Company:"), gbc);
    gbc.gridx=1; gbc.gridy=1; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; companyField = new JTextField(20); form.add(companyField, gbc);

    gbc.gridx=0; gbc.gridy=2; gbc.weightx = 0; gbc.gridwidth = 1; form.add(new JLabel("Price:"), gbc);
    gbc.gridx=1; gbc.gridy=2; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; priceField = new JTextField(10); form.add(priceField, gbc);

    gbc.gridx=0; gbc.gridy=3; gbc.weightx = 0; gbc.gridwidth = 1; form.add(new JLabel("Stock:"), gbc);
    gbc.gridx=1; gbc.gridy=3; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; stockField = new JTextField(10); form.add(stockField, gbc);

    JButton addBtn = new JButton("Add Medicine");
    JButton cancelBtn = new JButton("Cancel");

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.add(addBtn);
    btnPanel.add(cancelBtn);

    add(form, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicine();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // Insert medicine into DB using PreparedStatement
    private void addMedicine() {
        String name = nameField.getText().trim();
        String company = companyField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Price are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        int stock = 0;
        try {
            price = Double.parseDouble(priceText);
            if (!stockText.isEmpty()) {
                stock = Integer.parseInt(stockText);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number and Stock must be an integer.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database.", "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO medicine(name, company, price, stock) VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, company);
            pst.setDouble(3, price);
            pst.setInt(4, stock);

            int affected = pst.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Medicine added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // clear fields
                nameField.setText("");
                companyField.setText("");
                priceField.setText("");
                stockField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add medicine.", "Failure", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (pst != null) pst.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

}

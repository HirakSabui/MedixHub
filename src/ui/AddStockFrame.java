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
 * AddStockFrame
 *
 * Simple UI to add stock quantity to an existing medicine by name.
 */
public class AddStockFrame extends JFrame {

    private JTextField nameField;
    private JTextField qtyField;

    public AddStockFrame() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Add Stock");
    }

    private void initComponents() {
    setLayout(new BorderLayout(12,12));

    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6,6,6,6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx=0; gbc.gridy=0; gbc.weightx = 0; form.add(new JLabel("Medicine Name:"), gbc);
    gbc.gridx=1; gbc.gridy=0; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; nameField = new JTextField(20); form.add(nameField, gbc);

    gbc.gridx=0; gbc.gridy=1; gbc.weightx = 0; gbc.gridwidth = 1; form.add(new JLabel("Quantity to Add:"), gbc);
    gbc.gridx=1; gbc.gridy=1; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; qtyField = new JTextField(10); form.add(qtyField, gbc);

    JButton addBtn = new JButton("Update Stock");
    JButton cancelBtn = new JButton("Cancel");

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.add(addBtn);
    btnPanel.add(cancelBtn);

    add(form, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStock();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // Update stock for a medicine using PreparedStatement
    private void updateStock() {
        String name = nameField.getText().trim();
        String qtyText = qtyField.getText().trim();

        if (name.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter medicine name and quantity.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive integer.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer.", "Validation", JOptionPane.WARNING_MESSAGE);
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

            String sql = "UPDATE medicine SET stock = stock + ? WHERE name = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, qty);
            pst.setString(2, name);

            int affected = pst.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Stock updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                qtyField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found. Check the name.", "Not Found", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (pst != null) pst.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    // Quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AddStockFrame().setVisible(true);
            }
        });
    }
}

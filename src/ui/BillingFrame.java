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
 * BillingFrame
 *
 * Handles selling a medicine: fetches price, calculates total, updates stock,
 * and inserts a record into the `bills` table.
 */
public class BillingFrame extends JFrame {

    private JTextField nameField;
    private JTextField qtyField;

    public BillingFrame() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Billing");
    }

    private void initComponents() {
    setLayout(new BorderLayout(12,12));

    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6,6,6,6);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx=0; gbc.gridy=0; gbc.weightx = 0; form.add(new JLabel("Medicine Name:"), gbc);
    gbc.gridx=1; gbc.gridy=0; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; nameField = new JTextField(20); form.add(nameField, gbc);

    gbc.gridx=0; gbc.gridy=1; gbc.weightx = 0; gbc.gridwidth = 1; form.add(new JLabel("Quantity Sold:"), gbc);
    gbc.gridx=1; gbc.gridy=1; gbc.weightx = 1.0; gbc.gridwidth = GridBagConstraints.REMAINDER; qtyField = new JTextField(10); form.add(qtyField, gbc);

    JButton sellBtn = new JButton("Process Sale");
    JButton cancelBtn = new JButton("Cancel");

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    btnPanel.add(sellBtn);
    btnPanel.add(cancelBtn);

    add(form, BorderLayout.CENTER);
    add(btnPanel, BorderLayout.SOUTH);

        sellBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSale();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // Process the sale: check stock, deduct, insert bill (transactional)
    private void processSale() {
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
        PreparedStatement pstSelect = null;
        PreparedStatement pstUpdate = null;
        PreparedStatement pstInsert = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database.", "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch price and stock for the medicine
            String selectSql = "SELECT id, price, stock FROM medicine WHERE name = ?";
            pstSelect = conn.prepareStatement(selectSql);
            pstSelect.setString(1, name);
            rs = pstSelect.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Medicine not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = rs.getInt("id");
            double price = rs.getDouble("price");
            int stock = rs.getInt("stock");

            if (stock < qty) {
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + stock, "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double total = price * qty;

            // Transaction: deduct stock and insert bill
            conn.setAutoCommit(false);

            String updateSql = "UPDATE medicine SET stock = stock - ? WHERE id = ?";
            pstUpdate = conn.prepareStatement(updateSql);
            pstUpdate.setInt(1, qty);
            pstUpdate.setInt(2, id);
            int upd = pstUpdate.executeUpdate();

            String insertSql = "INSERT INTO bills(medicine_name, quantity, total) VALUES (?, ?, ?)";
            pstInsert = conn.prepareStatement(insertSql);
            pstInsert.setString(1, name);
            pstInsert.setInt(2, qty);
            pstInsert.setDouble(3, total);
            int ins = pstInsert.executeUpdate();

            if (upd > 0 && ins > 0) {
                conn.commit();
                JOptionPane.showMessageDialog(this, "Sale processed. Total: " + total, "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                qtyField.setText("");
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Failed to process sale.", "Failure", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstSelect != null) pstSelect.close(); } catch (SQLException ignored) {}
            try { if (pstUpdate != null) pstUpdate.close(); } catch (SQLException ignored) {}
            try { if (pstInsert != null) pstInsert.close(); } catch (SQLException ignored) {}
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    // Quick test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BillingFrame().setVisible(true);
            }
        });
    }
}

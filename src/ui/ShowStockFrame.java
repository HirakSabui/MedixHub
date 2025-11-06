package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ShowStockFrame
 *
 * Displays all medicines in a JTable with columns: ID, Name, Company, Price, Stock.
 * Includes a simple search by name filter.
 */
public class ShowStockFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    public ShowStockFrame() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Show Stocks");
        loadData("");
    }

    @SuppressWarnings("Convert2Lambda")
    private void initComponents() {
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Top: search
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search by name:"));
    searchField = new JTextField(20);
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        top.add(searchBtn);
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Company", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Actions
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData(searchField.getText().trim());
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                loadData("");
            }
        });
    }

    // Load data from medicines table; if nameFilter is empty, load all
    private void loadData(String nameFilter) {
        tableModel.setRowCount(0);

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Cannot connect to database.", "DB Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nameFilter == null || nameFilter.isEmpty()) {
                String sql = "SELECT id, name, company, price, stock FROM medicine";
                pst = conn.prepareStatement(sql);
            } else {
                String sql = "SELECT id, name, company, price, stock FROM medicine WHERE name LIKE ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, "%" + nameFilter + "%");
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("name");
                row[2] = rs.getString("company");
                row[3] = rs.getDouble("price");
                row[4] = rs.getInt("stock");
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pst != null) pst.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    // Quick test
    @SuppressWarnings("Convert2Lambda")
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShowStockFrame().setVisible(true);
            }
        });
    }
}

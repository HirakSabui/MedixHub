package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dashboard
 *
 * Main menu window with four buttons to open other frames.
 */
public class Dashboard extends JFrame {

    public Dashboard() {
        super();
        UIUtils.init();
        initComponents();
        UIUtils.styleFrame(this, "Medical Store - Dashboard");
    }

    // Initialize UI components and layout
    private void initComponents() {

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(12,12));

    JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));

    JButton addMedBtn = new JButton("Add New Medicine");
    JButton addStockBtn = new JButton("Add Stock");
    JButton billingBtn = new JButton("Billing");
    JButton showStockBtn = new JButton("Show Stocks");

    addMedBtn.setPreferredSize(new java.awt.Dimension(180, 80));
    addStockBtn.setPreferredSize(new java.awt.Dimension(180, 80));
    billingBtn.setPreferredSize(new java.awt.Dimension(180, 80));
    showStockBtn.setPreferredSize(new java.awt.Dimension(180, 80));

    panel.add(addMedBtn);
    panel.add(addStockBtn);
    panel.add(billingBtn);
    panel.add(showStockBtn);

    add(panel, BorderLayout.CENTER);

        // Button actions: open respective frames
        addMedBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMedicineFrame().setVisible(true);
            }
        });

        addStockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddStockFrame().setVisible(true);
            }
        });

        billingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BillingFrame().setVisible(true);
            }
        });

        showStockBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ShowStockFrame().setVisible(true);
            }
        });
    }

    // For quick manual testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dashboard dash = new Dashboard();
                dash.setVisible(true);
            }
        });
    }
}
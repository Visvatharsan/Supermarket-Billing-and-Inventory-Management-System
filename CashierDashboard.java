import java.awt.*;
import javax.swing.*;

public class CashierDashboard extends JFrame {
    public CashierDashboard(String username) {
        setTitle("Cashier Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome Cashier, " + username, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        // All the buttons for the Cashier Dashboard
        JPanel buttonPanel = new JPanel();
        JButton billingBtn = new JButton("Start Billing");
        JButton historyBtn = new JButton("View Sales History");
        

        billingBtn.addActionListener(e -> new BillingForm(username));
        historyBtn.addActionListener(e -> new SalesHistory());

        buttonPanel.add(billingBtn);
        buttonPanel.add(historyBtn);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}

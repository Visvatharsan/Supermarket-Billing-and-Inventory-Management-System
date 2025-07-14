import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard(String username) {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome Admin, " + username, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        JButton productMgmtBtn = new JButton("Manage Products");
        productMgmtBtn.addActionListener(e -> new ProductManagement());

        JButton historyBtn = new JButton("View Sales History");
        historyBtn.addActionListener(e -> new SalesHistory());

        JButton logBtn = new JButton("View System Logs");
        logBtn.addActionListener(e -> new ViewLogs());
        
        
        JPanel panel = new JPanel();
        panel.add(productMgmtBtn);
        panel.add(historyBtn);
        panel.add(logBtn);

        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }
}

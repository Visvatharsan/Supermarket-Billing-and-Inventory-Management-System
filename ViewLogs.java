import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ViewLogs extends JFrame {
    private JTable logTable;
    private DefaultTableModel model;

    public ViewLogs() {
        setTitle("System Activity Logs");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // Spacing

        model = new DefaultTableModel(new String[]{"Username", "Role", "Message", "Timestamp"}, 0);
        logTable = new JTable(model);

        logTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logTable.setRowHeight(24);

        JTableHeader header = logTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);

        loadLogs();

        setVisible(true);
    }

    private void loadLogs() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, role, message, timestamp FROM logs ORDER BY timestamp DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String user = rs.getString("username");
                String role = rs.getString("role");
                String msg = rs.getString("message");
                String time = rs.getString("timestamp");

                model.addRow(new Object[]{user, role, msg, time});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

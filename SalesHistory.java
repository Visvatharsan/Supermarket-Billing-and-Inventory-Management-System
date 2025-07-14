import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SalesHistory extends JFrame {
    private JTable salesTable, itemsTable;
    private DefaultTableModel salesModel, itemsModel;

    public SalesHistory() {
        setTitle("Sales History");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // spacing

        Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 15);

        // Sales Table (Top)
        salesModel = new DefaultTableModel(new String[]{"Sale ID", "Date", "Total Amount"}, 0);
        salesTable = new JTable(salesModel);
        salesTable.setFont(uiFont);
        salesTable.setRowHeight(24);
        JTableHeader salesHeader = salesTable.getTableHeader();
        salesHeader.setFont(headerFont);

        JScrollPane salesScroll = new JScrollPane(salesTable);
        salesScroll.setBorder(BorderFactory.createTitledBorder("Sales Overview"));

        // Items Table (Bottom)
        itemsModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Quantity", "Price"}, 0);
        itemsTable = new JTable(itemsModel);
        itemsTable.setFont(uiFont);
        itemsTable.setRowHeight(24);
        JTableHeader itemsHeader = itemsTable.getTableHeader();
        itemsHeader.setFont(headerFont);

        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setBorder(BorderFactory.createTitledBorder("Sale Details"));

        // Panel combining both scroll panes
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.add(salesScroll);
        panel.add(itemsScroll);

        add(panel, BorderLayout.CENTER);

        // Load sales on startup
        loadSales();

        // Load items on sale row click
        salesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row != -1) {
                    int saleId = Integer.parseInt(salesModel.getValueAt(row, 0).toString());
                    loadSaleItems(saleId);
                }
            }
        });

        setVisible(true);
    }

    private void loadSales() {
        salesModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, datetime, total_amount FROM sales ORDER BY datetime DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("datetime");
                double total = rs.getDouble("total_amount");
                salesModel.addRow(new Object[]{id, date, total});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSaleItems(int saleId) {
        itemsModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT si.product_id, p.name, si.quantity, si.price " +
                         "FROM sale_items si JOIN products p ON si.product_id = p.id " +
                         "WHERE si.sale_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, saleId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int pid = rs.getInt("product_id");
                String name = rs.getString("name");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                itemsModel.addRow(new Object[]{pid, name, qty, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

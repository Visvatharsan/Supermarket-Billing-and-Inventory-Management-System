import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class BillingForm extends JFrame {
    private JTextField txtProductId, txtQuantity, txtTotal;
    private JLabel lblProductName, lblPrice;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JButton btnAdd, btnFinalize, btnClear;

    public BillingForm(String cashierName) {
        setTitle("Billing - Logged in as: " + cashierName);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10)); // spacing

        Font uiFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        txtProductId = new JTextField();
        txtProductId.setFont(uiFont);

        txtQuantity = new JTextField();
        txtQuantity.setFont(uiFont);

        lblProductName = new JLabel();
        lblProductName.setFont(boldFont);

        lblPrice = new JLabel();
        lblPrice.setFont(boldFont);

        inputPanel.add(new JLabel("Product ID:", SwingConstants.RIGHT)).setFont(boldFont);
        inputPanel.add(txtProductId);
        inputPanel.add(new JLabel("Product Name:", SwingConstants.RIGHT)).setFont(boldFont);
        inputPanel.add(lblProductName);

        inputPanel.add(new JLabel("Quantity:", SwingConstants.RIGHT)).setFont(boldFont);
        inputPanel.add(txtQuantity);
        inputPanel.add(new JLabel("Price:", SwingConstants.RIGHT)).setFont(boldFont);
        inputPanel.add(lblPrice);

        btnAdd = new JButton("Add to Cart");
        btnAdd.setFont(boldFont);
        inputPanel.add(new JLabel()); // spacer
        inputPanel.add(btnAdd);

        // Cart Table
        cartModel = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Qty", "Line Total"}, 0);
        cartTable = new JTable(cartModel);
        cartTable.setFont(uiFont);
        cartTable.setRowHeight(24);
        JTableHeader header = cartTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JScrollPane cartScroll = new JScrollPane(cartTable);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        txtTotal = new JTextField(10);
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTotal.setEditable(false);

        btnFinalize = new JButton("Finalize Sale");
        btnFinalize.setFont(boldFont);
        btnClear = new JButton("Clear Cart");
        btnClear.setFont(boldFont);

        bottomPanel.add(new JLabel("Grand Total:"));
        bottomPanel.add(txtTotal);
        bottomPanel.add(btnFinalize);
        bottomPanel.add(btnClear);

        add(inputPanel, BorderLayout.NORTH);
        add(cartScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        txtProductId.addActionListener(e -> fetchProductDetails());
        txtProductId.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                fetchProductDetails();
            }
        });

        btnAdd.addActionListener(e -> {
            fetchProductDetails();
            addToCart();
        });

        btnClear.addActionListener(e -> clearCart());
        btnFinalize.addActionListener(e -> finalizeSale());

        setVisible(true);
        LoggerUtil.log(cashierName, "cashier", "Completed a sale of amount " + txtTotal.getText());
    }

    private void fetchProductDetails() {
        String productId = txtProductId.getText().trim();
        if (productId.isEmpty()) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name, price FROM products WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(productId));
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblProductName.setText(rs.getString("name"));
                lblPrice.setText(String.valueOf(rs.getDouble("price")));
            } else {
                lblProductName.setText("Not found");
                lblPrice.setText("0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        String productId = txtProductId.getText().trim();
        String productName = lblProductName.getText().trim();
        String priceStr = lblPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (productId.isEmpty() || productName.isEmpty() || productName.equals("Not found")
                || priceStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid product or quantity.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(qtyStr);

            if (price <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be positive.");
                return;
            }

            double lineTotal = price * quantity;
            cartModel.addRow(new Object[]{productId, productName, price, quantity, lineTotal});
            updateTotal();

            // Reset input
            txtProductId.setText("");
            txtQuantity.setText("");
            lblProductName.setText("");
            lblPrice.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price format.");
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += (Double) cartModel.getValueAt(i, 4);
        }
        txtTotal.setText(String.valueOf(total));
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        txtTotal.setText("");
    }

    private void finalizeSale() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert sale
            String saleSQL = "INSERT INTO sales (total_amount) VALUES (?)";
            PreparedStatement salePst = conn.prepareStatement(saleSQL, Statement.RETURN_GENERATED_KEYS);
            salePst.setDouble(1, Double.parseDouble(txtTotal.getText()));
            salePst.executeUpdate();

            ResultSet rs = salePst.getGeneratedKeys();
            if (!rs.next()) throw new SQLException("Failed to retrieve sale ID");

            int saleId = rs.getInt(1);

            // Insert items + update stock
            String itemSQL = "INSERT INTO sale_items (sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            String stockSQL = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            PreparedStatement itemPst = conn.prepareStatement(itemSQL);
            PreparedStatement stockPst = conn.prepareStatement(stockSQL);

            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int pid = Integer.parseInt(cartModel.getValueAt(i, 0).toString());
                int qty = Integer.parseInt(cartModel.getValueAt(i, 3).toString());
                double price = Double.parseDouble(cartModel.getValueAt(i, 2).toString());

                itemPst.setInt(1, saleId);
                itemPst.setInt(2, pid);
                itemPst.setInt(3, qty);
                itemPst.setDouble(4, price);
                itemPst.addBatch();

                stockPst.setInt(1, qty);
                stockPst.setInt(2, pid);
                stockPst.addBatch();
            }

            itemPst.executeBatch();
            stockPst.executeBatch();
            conn.commit();

            JOptionPane.showMessageDialog(this, "Sale completed!");
            clearCart();


        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Sale failed: " + e.getMessage());
        }
    }
}

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ProductManagement extends JFrame {
    private JTextField txtID, txtName, txtPrice, txtQuantity, txtCategory;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTable productTable;
    private DefaultTableModel model;

    public ProductManagement() {
        setTitle("Product Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Font settings
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);

        // Main container panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        getContentPane().add(mainPanel);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        txtID = new JTextField(); 
        txtID.setEnabled(false); 
        txtID.setFont(font);
        txtName = new JTextField(); 
        txtName.setFont(font);
        txtPrice = new JTextField(); 
        txtPrice.setFont(font);
        txtQuantity = new JTextField(); 
        txtQuantity.setFont(font);
        txtCategory = new JTextField(); 
        txtCategory.setFont(font);

        inputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        inputPanel.add(createLabel("Product ID (for update/delete):", boldFont));
        inputPanel.add(txtID);
        inputPanel.add(createLabel("Name:", boldFont));
        inputPanel.add(txtName);
        inputPanel.add(createLabel("Price:", boldFont));
        inputPanel.add(txtPrice);
        inputPanel.add(createLabel("Quantity:", boldFont));
        inputPanel.add(txtQuantity);
        inputPanel.add(createLabel("Category:", boldFont));
        inputPanel.add(txtCategory);

        mainPanel.add(inputPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnAdd = new JButton("Add Product");
        btnUpdate = new JButton("Update Product");
        btnDelete = new JButton("Delete Product");
        btnClear = new JButton("Clear");

        for (JButton btn : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear}) {
            btn.setFont(boldFont);
            buttonPanel.add(btn);
        }

        mainPanel.add(buttonPanel);

        // Table Panel
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity", "Category"}, 0);
        productTable = new JTable(model);
        productTable.setFont(font);
        productTable.setRowHeight(24);
        JTableHeader header = productTable.getTableHeader();
        header.setFont(boldFont);

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setPreferredSize(new Dimension(780, 300));
        mainPanel.add(Box.createVerticalStrut(5)); // Small space between button and table
        mainPanel.add(tableScroll);

        loadProducts();

        // Actions
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearFields());

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtPrice.setText(model.getValueAt(row, 2).toString());
                txtQuantity.setText(model.getValueAt(row, 3).toString());
                txtCategory.setText(model.getValueAt(row, 4).toString());
            }
        });

        setVisible(true);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private void loadProducts() {
        model.setRowCount(0);  // Clear existing rows
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM products";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getString("category")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void addProduct() {
        String name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String quantityStr = txtQuantity.getText().trim();
        String category = txtCategory.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO products (name, price, quantity, category) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setDouble(2, price);
                pst.setInt(3, quantity);
                pst.setString(4, category);
                int inserted = pst.executeUpdate();
                if (inserted > 0) {
                    JOptionPane.showMessageDialog(this, "Product added successfully.");
                    loadProducts();
                    clearFields();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        String idStr = txtID.getText().trim();
        String name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String quantityStr = txtQuantity.getText().trim();
        String category = txtCategory.getText().trim();

        if (idStr.isEmpty() || name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select and fill all fields to update.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE products SET name=?, price=?, quantity=?, category=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setDouble(2, price);
                pst.setInt(3, quantity);
                pst.setString(4, category);
                pst.setInt(5, id);
                int updated = pst.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Product updated successfully.");
                    loadProducts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        String idStr = txtID.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM products WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                int deleted = pst.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Product deleted.");
                    loadProducts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Deletion failed.");
                }
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtID.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtCategory.setText("");
        productTable.clearSelection();
    }
}

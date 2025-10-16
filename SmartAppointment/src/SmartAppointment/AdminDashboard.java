package SmartAppointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;

    // Constructor
    public AdminDashboard() {
        setTitle("Admin Dashboard - Smart Appointment");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Admin Dashboard - User Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table model for users
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Password", "Role"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addUserBtn = new JButton("Add User");
        JButton editUserBtn = new JButton("Edit Selected User");
        JButton removeUserBtn = new JButton("Remove Selected User");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addUserBtn);
        buttonPanel.add(editUserBtn);
        buttonPanel.add(removeUserBtn);
        buttonPanel.add(logoutBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load users from DB
        loadUsers();

        // Button actions
        addUserBtn.addActionListener(e -> showAddUserDialog());
        editUserBtn.addActionListener(e -> showEditUserDialog());
        removeUserBtn.addActionListener(e -> removeSelectedUser());
        logoutBtn.addActionListener(e -> {
            dispose();
            LoginScreen.showLoginScreen(); // return to login
        });
    }

    // Load all users
    private void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                tableModel.addRow(new Object[]{id, username, password, role});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users from database!", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add user dialog
    private void showAddUserDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField roleField = new JTextField();

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Role:", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {

                ps.setString(1, usernameField.getText());
                ps.setString(2, new String(passwordField.getPassword()));
                ps.setString(3, roleField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User added successfully!");
                loadUsers();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Edit user dialog
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to edit!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String currentUsername = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPassword = (String) tableModel.getValueAt(selectedRow, 2);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField usernameField = new JTextField(currentUsername);
        JPasswordField passwordField = new JPasswordField(currentPassword);
        JTextField roleField = new JTextField(currentRole);

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Role:", roleField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE users SET username=?, password=?, role=? WHERE id=?")) {

                ps.setString(1, usernameField.getText());
                ps.setString(2, new String(passwordField.getPassword()));
                ps.setString(3, roleField.getText());
                ps.setInt(4, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsers();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Remove user
    private void removeSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to remove!");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "User removed successfully!");
                loadUsers();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to remove user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // This lets the LoginScreen open the dashboard easily
    public static void showDashboard() {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }

    // Run directly (for testing)
}

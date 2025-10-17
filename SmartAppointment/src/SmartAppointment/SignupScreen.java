package SmartAppointment;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignupScreen {
    public static void showSignupScreen() {
        JFrame frame = new JFrame("Smart Appointment - Sign Up");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Create New Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 25));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 25));

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(300, 25));

        String[] roles = {"Patient"};
        JComboBox<String> roleSelector = new JComboBox<>(roles);
        roleSelector.setMaximumSize(new Dimension(300, 25));

        JButton signupButton = new JButton("Sign Up");
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            String role = (String) roleSelector.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!");
                return;
            }

            // 🔄 Insert into MySQL database
            String url = "jdbc:mysql://localhost:3306/SmartAppointmentDB";
            String dbUser = "root";
            String dbPass = "Gadha@123";

            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                stmt.setString(1, username);
                stmt.setString(2, password); // Consider hashing later
                stmt.setString(3, role);

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Signup successful! Please login.");
                frame.dispose();
                LoginScreen.showLoginScreen();

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(frame, "Username already exists. Choose another one.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Select Role:"));
        panel.add(roleSelector);
        panel.add(Box.createVerticalStrut(20));
        panel.add(signupButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}


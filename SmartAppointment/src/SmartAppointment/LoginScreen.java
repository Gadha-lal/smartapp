package SmartAppointment;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginScreen {
    public static void main(String[] args) {
        showLoginScreen();
    }

    public static void showLoginScreen() {
        JFrame frame = new JFrame("Smart Appointment - Login");

        // Full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Login Portal", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] roles = {"Patient", "Doctor", "Admin"};
        JComboBox<String> roleSelector = new JComboBox<>(roles);
        roleSelector.setMaximumSize(new Dimension(300, 25));

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 25));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 25));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> {
            String role = (String) roleSelector.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            boolean authenticated = checkCredentials(username, password, role);

            if (authenticated) {
                frame.dispose();
                switch (role) {
                    case "Patient":
                        PatientDashboard.showDashboard(); // You can enhance this later
                        break;
                    case "Doctor":
                        DoctorDashboard.showDashboard(username); // ✅ Pass username
                        break;
                    case "Admin":
                        AdminDashboard.showDashboard(); // You can enhance this too
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Invalid credentials for " + role + ".",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton signupButton = new JButton("Sign Up");
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.addActionListener(e -> {
            frame.dispose();
            SignupScreen.showSignupScreen();
        });

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("Select Role:"));
        panel.add(roleSelector);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(signupButton);
        panel.add(Box.createVerticalGlue());

        frame.add(panel);
        frame.setVisible(true);
    }

    private static boolean checkCredentials(String username, String password, String role) {
        String url = "jdbc:mysql://localhost:3306/SmartAppointmentDB"; // DB URL
        String dbUser = "root";   // Use your MySQL username
        String dbPass = "Gadha@123"; // Use your MySQL password

        String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If a row is found, login successful

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            return false;
        }
    }
}

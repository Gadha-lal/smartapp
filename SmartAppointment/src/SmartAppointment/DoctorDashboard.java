package SmartAppointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorDashboard {

    public static void showDashboard(String doctorName) {
        JFrame frame = new JFrame("Doctor Dashboard - " + doctorName);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel headerLabel = new JLabel("Welcome, Dr. " + doctorName, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table model
        String[] columnNames = {"No", "Patient Name", "Date", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable table = new JTable(model) {
            // To make the last column buttons
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        // Load appointments from DB
        loadAppointmentsForDoctor(doctorName, model);

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), model, doctorName));

        JScrollPane scrollPane = new JScrollPane(table);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            LoginScreen.showLoginScreen();
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        frame.add(headerLabel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void loadAppointmentsForDoctor(String doctorName, DefaultTableModel model) {
        String url = "jdbc:mysql://localhost:3306/SmartAppointmentDB";
        String dbUser = "root";
        String dbPass = "Gadha@123";

        String sql = "SELECT id, patient_name, appointment_date FROM appointments WHERE doctor_name = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctorName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String patient = rs.getString("patient_name");
                Date date = rs.getDate("appointment_date");
                model.addRow(new Object[]{id, patient, date.toString(), "Delete"});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading appointments: " + e.getMessage());
        }
    }
}

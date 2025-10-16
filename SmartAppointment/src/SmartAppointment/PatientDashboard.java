package SmartAppointment;


import javax.swing.*;
import java.awt.*;

public class PatientDashboard {
    public static void showDashboard() {
        JFrame frame = new JFrame("Patient Dashboard");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to Patient Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        JButton book = new JButton("Book Appointment");
        JButton cancel = new JButton("Cancel Appointment");
        JButton history = new JButton("View History");
        JButton logout = new JButton("Logout");

        book.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(frame,
                    "Do you want to book this appointment?",
                    "Confirm Booking",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                System.out.println("Appointment booked!");
            } else {
                System.out.println("Booking cancelled.");
            }
        });

        logout.addActionListener(e -> {
            frame.dispose();
            LoginScreen.showLoginScreen();
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.add(label);
        panel.add(book);
        panel.add(cancel);
        panel.add(history);
        panel.add(logout);

        frame.add(panel);
        frame.setVisible(true);
    }
}


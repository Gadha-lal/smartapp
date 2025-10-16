package SmartAppointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean clicked;
    private int selectedRow;
    private DefaultTableModel model;
    private String doctorName;

    public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, String doctorName) {
        super(checkBox);
        this.model = model;
        this.doctorName = doctorName;
        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "Delete" : value.toString();
        button.setText(label);
        clicked = true;
        selectedRow = row;
        return button;
    }

    public Object getCellEditorValue() {
        if (clicked) {
            int appointmentId = (int) model.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Delete appointment #" + appointmentId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteAppointmentFromDB(appointmentId);
                model.removeRow(selectedRow);
            }
        }
        clicked = false;
        return label;
    }

    private void deleteAppointmentFromDB(int id) {
        String url = "jdbc:mysql://localhost:3306/SmartAppointmentDB";
        String dbUser = "root";
        String dbPass = "Gadha@123";

        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting appointment: " + e.getMessage());
        }
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientHistoryScreen extends JPanel {
    private MainApp mainApp;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public ClientHistoryScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        String role = mainApp.getCurrentUserRole();
        setLayout(new BorderLayout());

        JLabel title = new JLabel(role + " History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        if (role.equals("Client")) {
            String[] columns = {"Technician Name", "Specialty", "Date", "Time", "Status"};
            tableModel = new DefaultTableModel(columns, 0);
        } else {
            String[] columns = {"Client Name", "Email", "Phone", "Date", "Time", "Status"};
            tableModel = new DefaultTableModel(columns, 0);
        }

        historyTable = new JTable(tableModel);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        loadHistory(role);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if ("Client".equalsIgnoreCase(role)) {
                mainApp.switchScreen(new ClientActionScreen(mainApp));
            } else {
                mainApp.switchScreen(new TechnicianActionScreen(mainApp));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadHistory(String role) {
        int clientId = mainApp.getCurrentUserId(); // αλλαξε "userId" σε "clientId"
        String query;

        if (role.equals("Client")) {
            query = """
                SELECT u.name AS tech_name, t.specialty, a.appointment_date, a.appointment_time, a.status
                FROM appointments a
                JOIN technicians t ON a.technician_id = t.user_id
                JOIN users u ON t.user_id = u.id
                WHERE a.client_id = ?
                ORDER BY a.appointment_date DESC, a.appointment_time DESC
            """;
        } else {
            query = """
                SELECT u.name AS client_name, u.email, u.phone, a.appointment_date, a.appointment_time, a.status
                FROM appointments a
                JOIN clients c ON a.client_id = c.user_id
                JOIN users u ON c.user_id = u.id
                WHERE a.technician_id = ?
                ORDER BY a.appointment_date DESC, a.appointment_time DESC
            """;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (role.equals("Client")) {
                    tableModel.addRow(new Object[] {
                        rs.getString("tech_name"),
                        rs.getString("specialty"),
                        rs.getDate("appointment_date").toString(),
                        rs.getTime("appointment_time").toString(),
                        rs.getString("status")
                    });
                } else {
                    tableModel.addRow(new Object[] {
                        rs.getString("client_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("appointment_date").toString(),
                        rs.getTime("appointment_time").toString(),
                        rs.getString("status")
                    });
                }
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading history", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserHistoryScreen extends JPanel {
    private MainApp mainApp;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    public UserHistoryScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        String role = mainApp.getCurrentUserRole();
        setLayout(new BorderLayout());

        JLabel title = new JLabel(role + " History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        if (role.equals("Client")) {
            String[] columns = {"Technician Name", "Specialty", "Date"};
            tableModel = new DefaultTableModel(columns, 0);
        } else {
            String[] columns = {"Client Name", "Email", "Phone", "Date"};
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
        int userId = mainApp.getCurrentUserId();
        String query = "";

        if (role.equals("Client")) {
            query = """
                SELECT u.name AS tech_name, t.specialty, h.date
                FROM history h
                JOIN technicians t ON h.technician_id = t.user_id
                JOIN users u ON h.technician_id = u.id
                WHERE h.client_id = ?
                ORDER BY h.date DESC
            """;
        } else {
            query = """
                SELECT u.name AS client_name, u.email, u.phone, h.date
                FROM history h
                JOIN clients c ON h.client_id = c.user_id
                JOIN users u ON h.client_id = u.id
                WHERE h.technician_id = ?
                ORDER BY h.date DESC
            """;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (role.equals("Client")) {
                    tableModel.addRow(new Object[] {
                        rs.getString("tech_name"),
                        rs.getString("specialty"),
                        rs.getTimestamp("date").toString()
                    });
                } else {
                    tableModel.addRow(new Object[] {
                        rs.getString("client_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getTimestamp("date").toString()
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
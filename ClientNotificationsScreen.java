import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ClientNotificationsScreen extends JPanel {
    private MainApp mainApp;
    private JPanel notificationsPanel;
    private JButton backButton;

    public ClientNotificationsScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Ειδοποιήσεις Ραντεβού", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        add(scrollPane, BorderLayout.CENTER);

        backButton = new JButton("Πίσω");
        backButton.addActionListener(e -> mainApp.switchScreen(new ClientActionScreen(mainApp)));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadNotifications();
    }

    private void loadNotifications() {
        notificationsPanel.removeAll();

        try {
            int clientId = mainApp.getCurrentUserId();

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");

            // Επιλέγουμε αιτήματα που έχουν απαντηθεί (Confirmed ή Rejected)
            String query = "SELECT ar.id, ar.service_type, ar.requested_date, ar.requested_time, ar.status, " +
                    "t.name AS technician_name " +
                    "FROM appointment_requests ar " +
                    "LEFT JOIN users t ON ar.preferred_technician_id = t.id " +
                    "WHERE ar.client_id = ? AND ar.status IN ('Confirmed', 'Rejected') " +
                    "ORDER BY ar.requested_date DESC, ar.requested_time DESC";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, clientId);

            ResultSet rs = pstmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;

                int requestId = rs.getInt("id");
                String serviceType = rs.getString("service_type");
                Date date = rs.getDate("requested_date");
                Time time = rs.getTime("requested_time");
                String status = rs.getString("status");
                String technicianName = rs.getString("technician_name");

                JPanel notification = new JPanel(new BorderLayout(5,5));
                notification.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                notification.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                String statusText = status.equals("Confirmed") ? "ΑΠΟΔΕΧΤΗΚΕ" : "ΑΠΕΡΡΙΨΕ";

                JTextArea info = new JTextArea(
                        "Αίτημα Ραντεβού #" + requestId + "\n" +
                                "Υπηρεσία: " + serviceType + "\n" +
                                "Ημερομηνία: " + date + " Ώρα: " + time + "\n" +
                                "Τεχνικός: " + (technicianName != null ? technicianName : "Άγνωστος") + "\n" +
                                "Κατάσταση: " + statusText
                );
                info.setEditable(false);
                info.setFont(new Font("Monospaced", Font.PLAIN, 14));
                notification.add(info, BorderLayout.CENTER);

                notificationsPanel.add(notification);
                notificationsPanel.add(Box.createVerticalStrut(10));
            }

            if (!hasResults) {
                JLabel noNotifLabel = new JLabel("Δεν υπάρχουν ειδοποιήσεις για εσάς.");
                noNotifLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noNotifLabel.setHorizontalAlignment(SwingConstants.CENTER);
                notificationsPanel.add(noNotifLabel);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά τη φόρτωση των ειδοποιήσεων.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }
}
 

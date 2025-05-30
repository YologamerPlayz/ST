import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnicianNotificationsScreen extends JPanel {
    private MainApp mainApp;
    private JPanel notificationsPanel;
    private JButton backButton;

    public TechnicianNotificationsScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Appointment Notifications", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        add(scrollPane, BorderLayout.CENTER);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> mainApp.switchScreen(new TechnicianActionScreen(mainApp)));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadNotifications();
    }

    private void loadNotifications() {
        notificationsPanel.removeAll();

        try {
            int technicianId = mainApp.getCurrentUserId();

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");

            String query = "SELECT ar.id, ar.client_id, ar.service_type, ar.requested_date, ar.requested_time, " +
                    "u.name AS client_name, u.email AS client_email, u.phone AS client_phone " +
                    "FROM appointment_requests ar " +
                    "JOIN clients c ON ar.client_id = c.user_id " +
                    "JOIN users u ON c.user_id = u.id " +
                    "WHERE ar.preferred_technician_id = ? AND ar.status = 'Pending'";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, technicianId);

            ResultSet rs = pstmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;

                int requestId = rs.getInt("id");
                String clientName = rs.getString("client_name");
                String clientEmail = rs.getString("client_email");
                String clientPhone = rs.getString("client_phone");
                String serviceType = rs.getString("service_type");
                Date requestedDate = rs.getDate("requested_date");
                Time requestedTime = rs.getTime("requested_time");

                JPanel requestPanel = new JPanel(new BorderLayout(5,5));
                requestPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                requestPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                JTextArea infoArea = new JTextArea();
                infoArea.setEditable(false);
                infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                infoArea.setText(
                        "Appointment Request #" + requestId + "\n" +
                                "Client: " + clientName + "\n" +
                                "Email: " + clientEmail + "\n" +
                                "Phone: " + clientPhone + "\n" +
                                "Service: " + serviceType + "\n" +
                                "Date: " + requestedDate + " Time: " + requestedTime + "\n"
                );
                requestPanel.add(infoArea, BorderLayout.CENTER);

                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                JButton acceptBtn = new JButton("Accept");
                JButton rejectBtn = new JButton("Reject");

                acceptBtn.addActionListener(e -> {
                    handleAcceptRequest(requestId, technicianId, clientName, requestedDate, requestedTime);
                });

                rejectBtn.addActionListener(e -> {
                    handleRejectRequest(requestId);
                });

                buttonsPanel.add(acceptBtn);
                buttonsPanel.add(rejectBtn);

                requestPanel.add(buttonsPanel, BorderLayout.SOUTH);

                notificationsPanel.add(requestPanel);
                notificationsPanel.add(Box.createVerticalStrut(10));
            }

            if (!hasResults) {
                JLabel noRequestsLabel = new JLabel("No new appointment requests found for this technician.");
                noRequestsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noRequestsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                notificationsPanel.add(noRequestsLabel);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error while loading notifications.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }

    private void handleAcceptRequest(int requestId, int technicianId, String clientName, Date date, Time time) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");
            conn.setAutoCommit(false);

            String updateRequest = "UPDATE appointment_requests SET status = 'Confirmed' WHERE id = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(updateRequest);
            pstmtUpdate.setInt(1, requestId);
            pstmtUpdate.executeUpdate();

            String selectClientId = "SELECT client_id FROM appointment_requests WHERE id = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(selectClientId);
            pstmtSelect.setInt(1, requestId);
            ResultSet rs = pstmtSelect.executeQuery();

            int clientId = -1;
            if (rs.next()) {
                clientId = rs.getInt("client_id");
            } else {
                JOptionPane.showMessageDialog(this, "Client not found for the request.", "Error", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                conn.close();
                return;
            }
            rs.close();
            pstmtSelect.close();

            String insertAppointment = "INSERT INTO appointments (request_id, client_id, technician_id, appointment_date, appointment_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'Confirmed')";
            PreparedStatement pstmtInsert = conn.prepareStatement(insertAppointment);
            pstmtInsert.setInt(1, requestId);
            pstmtInsert.setInt(2, clientId);
            pstmtInsert.setInt(3, technicianId);
            pstmtInsert.setDate(4, new java.sql.Date(date.getTime()));
            pstmtInsert.setTime(5, time);
            pstmtInsert.executeUpdate();
            pstmtInsert.close();

            String insertHistory = "INSERT INTO history (client_id, technician_id) VALUES (?, ?)";
            PreparedStatement pstmtHistory = conn.prepareStatement(insertHistory);
            pstmtHistory.setInt(1, clientId);
            pstmtHistory.setInt(2, technicianId);
            pstmtHistory.executeUpdate();
            pstmtHistory.close();

            conn.commit();
            conn.close();

            JOptionPane.showMessageDialog(this, "The appointment request was accepted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadNotifications();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while accepting the appointment request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRejectRequest(int requestId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");

            String updateRequest = "UPDATE appointment_requests SET status = 'Rejected' WHERE id = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(updateRequest);
            pstmtUpdate.setInt(1, requestId);

            int rows = pstmtUpdate.executeUpdate();

            pstmtUpdate.close();
            conn.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "The appointment request was rejected.", "Info", JOptionPane.INFORMATION_MESSAGE);
                loadNotifications();
            } else {
                JOptionPane.showMessageDialog(this, "Appointment request not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while rejecting the appointment request.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

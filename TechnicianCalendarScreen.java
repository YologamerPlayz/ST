import com.toedter.calendar.JCalendar;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TechnicianCalendarScreen extends JPanel {

    private MainApp mainApp;
    private JCalendar calendar;
    private JTextArea appointmentsArea;

    public TechnicianCalendarScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Technician Work Calendar", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        calendar = new JCalendar();
        add(calendar, BorderLayout.CENTER);

        appointmentsArea = new JTextArea(10, 30);
        appointmentsArea.setEditable(false);
        
        // Κάτω πάνελ που περιέχει το appointmentsArea και το κουμπί Back
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // ScrollPane για τα ραντεβού
        JScrollPane scrollPane = new JScrollPane(appointmentsArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // Κουμπί Back κάτω δεξιά
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainApp.setContentPanel(new TechnicianActionScreen(mainApp)));
        rightPanel.add(backButton);

        bottomPanel.add(rightPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        
        // Φόρτωση ραντεβού για την αρχική ημερομηνία
        loadAppointmentsForDate(calendar.getDate());

        // Όταν αλλάζει ημερομηνία στο ημερολόγιο, ενημερώνουμε τα ραντεβού
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            Date selectedDate = calendar.getDate();
            loadAppointmentsForDate(selectedDate);
        });
    }

    private void loadAppointmentsForDate(Date date) {
        appointmentsArea.setText("");  // Καθαρισμός προηγούμενων

        int userId = mainApp.getCurrentUserId();
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT a.appointment_time, a.status, ar.service_type " +
                    "FROM appointments a " +
                    "JOIN appointment_requests ar ON a.request_id = ar.id " +
                    "WHERE a.technician_id = ? AND a.appointment_date = ? AND a.status = 'Confirmed' " +
                    "ORDER BY a.appointment_time";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(localDate));
            ResultSet rs = ps.executeQuery();

            List<String> appointments = new ArrayList<>();
            while (rs.next()) {
                Time time = rs.getTime("appointment_time");
                String service = rs.getString("service_type");
                String status = rs.getString("status");
                appointments.add(time.toString() + " - " + service + " (" + status + ")");
            }

            if (appointments.isEmpty()) {
                appointmentsArea.setText("No appointments for " + localDate.toString());
            } else {
                for (String appt : appointments) {
                    appointmentsArea.append(appt + "\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            appointmentsArea.setText("Error loading appointments.");
        }
    }
}

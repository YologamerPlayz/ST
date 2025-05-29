import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class AppointmentRequestScreen extends JPanel {
    private MainApp mainApp;
    private int technicianId;
    private int clientId;

    private JDateChooser dateChooser;
    private JComboBox<String> timeCombo;
    private JTextField serviceField;

    public AppointmentRequestScreen(MainApp mainApp, int technicianId, int clientId) {
        this.mainApp = mainApp;
        this.technicianId = technicianId;
        this.clientId = clientId;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Προγραμματισμός Ραντεβού", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        formPanel.add(new JLabel("Επιλέξτε ημερομηνία:"));
        dateChooser = new JDateChooser();
        dateChooser.setMinSelectableDate(new Date()); // Από σήμερα και μετά
        formPanel.add(dateChooser);

        formPanel.add(new JLabel("Επιλέξτε ώρα:"));
        timeCombo = new JComboBox<>(new String[] {
                "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00"
        });
        formPanel.add(timeCombo);

        formPanel.add(new JLabel("Είδος υπηρεσίας:"));
        serviceField = new JTextField();
        formPanel.add(serviceField);

        add(formPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("Υποβολή Αιτήματος");
        submitButton.addActionListener(e -> submitAppointmentRequest());

        JButton backButton = new JButton("Πίσω");
        backButton.addActionListener(e -> mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianId)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void submitAppointmentRequest() {
        Date selectedDate = dateChooser.getDate();
        String selectedTime = (String) timeCombo.getSelectedItem();
        String serviceType = serviceField.getText().trim();

        if (selectedDate == null || selectedTime == null || serviceType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Συμπληρώστε όλα τα πεδία.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String insertSql = """
                INSERT INTO appointment_requests
                (client_id, service_type, preferred_technician_id, requested_date, requested_time, status)
                VALUES (?, ?, ?, ?, ?, 'Pending')
            """;
            PreparedStatement stmt = conn.prepareStatement(insertSql);

            stmt.setInt(1, clientId);
            stmt.setString(2, serviceType);
            stmt.setInt(3, technicianId);
            stmt.setDate(4, new java.sql.Date(selectedDate.getTime()));
            stmt.setTime(5, java.sql.Time.valueOf(selectedTime + ":00"));

            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(this, "Το αίτημα καταχωρήθηκε με επιτυχία.");
            mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianId));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά την υποβολή του αιτήματος.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }
}

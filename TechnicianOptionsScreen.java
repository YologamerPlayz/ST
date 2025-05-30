import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TechnicianOptionsScreen extends JPanel {
    private MainApp mainApp;
    private int technicianId;
    private String technicianName = "";

    public TechnicianOptionsScreen(MainApp mainApp, int technicianId) {
        this.mainApp = mainApp;
        this.technicianId = technicianId;
        this.setLayout(new BorderLayout());

        fetchTechnicianName();  // Get name from DB

        JLabel titleLabel = new JLabel("Choose action for technician: " + technicianName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        this.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

        JButton realTimeButton = createLargeButton("Schedule Appointment");
        JButton assistanceButton = createLargeButton("Immediate Assistance (Call or Message)");
        JButton reviewButton = createLargeButton("Create and View Reviews");

        buttonPanel.add(realTimeButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(assistanceButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(reviewButton);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(buttonPanel);
        this.add(centerWrapper, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> mainApp.switchScreen(new FindTechnicianScreen(mainApp)));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        assistanceButton.addActionListener(e -> {
            mainApp.switchScreen(new TechnicianContactScreen(mainApp, technicianId));
        });

        reviewButton.addActionListener(e -> {
            mainApp.switchScreen(new ReviewScreen(mainApp, technicianId));
        });

        realTimeButton.addActionListener(e -> {
            mainApp.switchScreen(new AppointmentRequestScreen(mainApp, technicianId, mainApp.getCurrentUserId()));
        });
    }

    private void fetchTechnicianName() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT name FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                technicianName = rs.getString("name");
            } else {
                technicianName = "Unknown";
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            technicianName = "Error retrieving name";
            e.printStackTrace();
        }
    }

    private JButton createLargeButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(350, 55));
        button.setMaximumSize(new Dimension(350, 55));
        return button;
    }
}

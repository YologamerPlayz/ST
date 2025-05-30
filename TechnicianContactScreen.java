import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TechnicianContactScreen extends JPanel {
    private MainApp mainApp;
    private int technicianId;
    private String technicianName = "";
    private String technicianPhone = "";

    public TechnicianContactScreen(MainApp mainApp, int technicianId) {
        this.mainApp = mainApp;
        this.technicianId = technicianId;
        this.setLayout(new BorderLayout());

        fetchTechnicianName();
        fetchTechnicianPhone();

        JLabel titleLabel = new JLabel("Contact Technician: " + technicianName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        this.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

        JButton callButton = createLargeButton("Phone Call");
        JButton messageButton = createLargeButton("Send Message");

        buttonPanel.add(callButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(messageButton);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(buttonPanel);
        this.add(centerWrapper, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianId)));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Calling technician: " + technicianName + " (" + technicianPhone + ")",
                "Phone Call", JOptionPane.INFORMATION_MESSAGE));

        messageButton.addActionListener(e -> mainApp.switchScreen(new ClientMessageTechnicianScreen(mainApp, technicianId)));
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

    private void fetchTechnicianPhone() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT phone FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                technicianPhone = rs.getString("phone");
            } else {
                technicianPhone = "Unknown";
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            technicianPhone = "Error retrieving phone";
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

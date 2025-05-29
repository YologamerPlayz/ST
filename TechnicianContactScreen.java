import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TechnicianContactScreen extends JPanel {
    private MainApp mainApp;
    private int technicianId;
    private String technicianName = "";

    public TechnicianContactScreen(MainApp mainApp, int technicianId) {
        this.mainApp = mainApp;
        this.technicianId = technicianId;
        this.setLayout(new BorderLayout());

        fetchTechnicianName();

        JLabel titleLabel = new JLabel("Επικοινωνία με τον τεχνικό: " + technicianName, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        this.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

        JButton callButton = createLargeButton("Τηλεφωνική Κλήση");
        JButton messageButton = createLargeButton("Αποστολή Μηνύματος");

        buttonPanel.add(callButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(messageButton);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(buttonPanel);
        this.add(centerWrapper, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Πίσω");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianId)));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Κλήση στον τεχνικό: " + technicianName,
                "Τηλεφωνική Κλήση", JOptionPane.INFORMATION_MESSAGE));

        messageButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Αποστολή μηνύματος στον τεχνικό: " + technicianName,
                "Αποστολή Μηνύματος", JOptionPane.INFORMATION_MESSAGE));
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
                technicianName = "Άγνωστος";
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            technicianName = "Σφάλμα ανάκτησης ονόματος";
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
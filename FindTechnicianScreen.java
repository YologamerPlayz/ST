import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FindTechnicianScreen extends JPanel {
    private MainApp mainApp;
    private JComboBox<String> serviceComboBox;
    private JTextArea resultsArea;

    public FindTechnicianScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Find Available Technicians", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout());

        searchPanel.add(new JLabel("Choose service:"));
        serviceComboBox = new JComboBox<>();
        loadServiceTypes();
        searchPanel.add(serviceComboBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchTechnicians());
        searchPanel.add(searchBtn);

        add(searchPanel, BorderLayout.CENTER);

        resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.SOUTH);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainApp.switchScreen(new ClientActionScreen(mainApp)));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void loadServiceTypes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT DISTINCT service FROM technician_services";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                serviceComboBox.addItem(rs.getString("service"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading service types", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTechnicians() {
        resultsArea.setText("");

        String selectedService = (String) serviceComboBox.getSelectedItem();
        if (selectedService == null) {
            resultsArea.setText("No service selected.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT u.name, u.email, u.phone " +
                           "FROM users u " +
                           "JOIN technicians t ON u.id = t.user_id " +
                           "JOIN technician_services ts ON t.user_id = ts.technician_id " +
                           "WHERE ts.service = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedService);
            ResultSet rs = stmt.executeQuery();

            List<String> results = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                results.add(name + " - " + email + " - " + phone);
            }

            if (results.isEmpty()) {
                resultsArea.setText("No technicians available for this service.");
            } else {
                for (String res : results) {
                    resultsArea.append(res + "\n");
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resultsArea.setText("Error searching technicians.");
        }
    }
}

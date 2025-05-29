import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class FindTechnicianScreen extends JPanel {
    private MainApp mainApp;
    private JComboBox<String> serviceComboBox;
    private DefaultListModel<String> listModel;
    private JList<String> techniciansList;
    private int selectedTechnicianId = -1;

    public FindTechnicianScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Find Available Technicians", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(title, BorderLayout.NORTH);

        // Top Panel with service selection
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Choose specialty:"));

        serviceComboBox = new JComboBox<>();
        loadServiceTypes();
        searchPanel.add(serviceComboBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchTechnicians());
        searchPanel.add(searchBtn);

        this.add(searchPanel, BorderLayout.NORTH);

        // Center Panel with list of technicians
        listModel = new DefaultListModel<>();
        techniciansList = new JList<>(listModel);
        techniciansList.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(techniciansList);
        this.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton selectBtn = new JButton("Select Technician");
        selectBtn.addActionListener(e -> selectTechnician());
        bottomPanel.add(selectBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainApp.switchScreen(new ClientActionScreen(mainApp)));
        bottomPanel.add(backBtn);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadServiceTypes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT DISTINCT specialty FROM technicians";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String service = rs.getString("specialty");
                serviceComboBox.addItem(service);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading specialties", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTechnicians() {
        listModel.clear();
        selectedTechnicianId = -1;

        String selectedSpecialty = (String) serviceComboBox.getSelectedItem();
        if (selectedSpecialty == null) {
            JOptionPane.showMessageDialog(this, "Please select a specialty first.", "No Specialty Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = """
                SELECT t.user_id, u.name, t.rating
                FROM technicians t
                JOIN users u ON t.user_id = u.id
                WHERE t.specialty = ?
                ORDER BY t.rating DESC
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedSpecialty);
            ResultSet rs = stmt.executeQuery();

            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String display = name + " (Rating: " + rating + ")";
                listModel.addElement(display);
            }

            if (!foundAny) {
                JOptionPane.showMessageDialog(this, "No technicians available for this specialty.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching technicians.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectTechnician() {
        String selectedDisplay = techniciansList.getSelectedValue();
        if (selectedDisplay == null) {
            JOptionPane.showMessageDialog(this, "Please select a technician from the list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String technicianName = selectedDisplay.split(" \\(")[0];
        String selectedSpecialty = (String) serviceComboBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = """
                SELECT t.user_id
                FROM technicians t
                JOIN users u ON t.user_id = u.id
                WHERE u.name = ? AND t.specialty = ?
                LIMIT 1
            """;

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, technicianName);
            stmt.setString(2, selectedSpecialty);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                selectedTechnicianId = rs.getInt("user_id");
                JOptionPane.showMessageDialog(this, "Technician selected. ID: " + selectedTechnicianId, "Success", JOptionPane.INFORMATION_MESSAGE);
                mainApp.switchScreen(new TechnicianBookingScreen(mainApp, selectedTechnicianId));
            } else {
                JOptionPane.showMessageDialog(this, "Technician not found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving technician ID.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

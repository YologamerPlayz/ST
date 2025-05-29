import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FindTechnicianScreen extends JPanel {
    private MainApp mainApp;
    private JComboBox<String> serviceComboBox;
    private DefaultListModel<String> listModel;
    private JList<String> techniciansList;

    public FindTechnicianScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        this.setLayout(new BorderLayout());

        JLabel title = new JLabel("Find Available Technicians", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Choose service:"));

        serviceComboBox = new JComboBox<>();
        loadServiceTypes();
        searchPanel.add(serviceComboBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchTechnicians());
        searchPanel.add(searchBtn);

        this.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        techniciansList = new JList<>(listModel);
        techniciansList.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(techniciansList);
        this.add(scrollPane, BorderLayout.CENTER);

        JButton selectBtn = new JButton("Select Technician");
        selectBtn.addActionListener(e -> selectTechnician());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(selectBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainApp.switchScreen(new ClientActionScreen(mainApp)));
        bottomPanel.add(backBtn);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadServiceTypes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT DISTINCT service FROM technician_services";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String service = rs.getString("service");
                serviceComboBox.addItem(service);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading service types", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTechnicians() {
        listModel.clear();
        String selectedService = (String) serviceComboBox.getSelectedItem();

        if (selectedService == null) {
            JOptionPane.showMessageDialog(this, "Please select a service first.", "No Service Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT u.name, t.rating FROM users u " +
                    "JOIN technicians t ON u.id = t.user_id " +
                    "JOIN technician_services ts ON t.user_id = ts.technician_id " +
                    "WHERE ts.service = ? " +
                    "ORDER BY t.rating DESC";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedService);
            ResultSet rs = stmt.executeQuery();

            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String technicianInfo = name + " (Rating: " + rating + ")";
                listModel.addElement(technicianInfo);
            }

            if (!foundAny) {
                JOptionPane.showMessageDialog(this, "No technicians available for this service.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching technicians.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectTechnician() {
        String selected = techniciansList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a technician from the list.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String technicianName = selected.split(" \\(")[0]; // Μόνο το όνομα
        mainApp.switchScreen(new TechnicianOptionsScreen(mainApp, technicianName));
    }
}

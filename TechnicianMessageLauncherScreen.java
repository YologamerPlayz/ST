import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TechnicianMessageLauncherScreen extends JPanel {
    private final MainApp mainApp;
    private final int technicianId;

    private JComboBox<String> clientDropdown;
    private final Map<String, Integer> nameToIdMap = new LinkedHashMap<>();

    public TechnicianMessageLauncherScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        this.technicianId = mainApp.getCurrentUserId();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Select Client to Message", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());

        clientDropdown = new JComboBox<>();
        loadClientList();
        centerPanel.add(clientDropdown);

        JButton openChatBtn = new JButton("Open Chat");
        openChatBtn.addActionListener(e -> {
            String selected = (String) clientDropdown.getSelectedItem();
            if (selected != null && nameToIdMap.containsKey(selected)) {
                int clientId = nameToIdMap.get(selected);
                mainApp.switchScreen(new TechnicianMessageClientScreen(mainApp, clientId));
            }
        });

        centerPanel.add(openChatBtn);
        add(centerPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainApp.switchScreen(new TechnicianActionScreen(mainApp)));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadClientList() {
        nameToIdMap.clear();
        clientDropdown.removeAllItems();

        String query = """
            SELECT DISTINCT u.id, u.name
            FROM users u
            JOIN conversation_users cu ON cu.user_id = u.id
            WHERE cu.conversation_id IN (
                SELECT cu2.conversation_id
                FROM conversation_users cu2
                WHERE cu2.user_id = ?
            )
            AND u.id != ?
            ORDER BY u.name
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, technicianId);
            stmt.setInt(2, technicianId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                nameToIdMap.put(name, id);
                clientDropdown.addItem(name);
            }

            rs.close();
            stmt.close();

            if (nameToIdMap.isEmpty()) {
                clientDropdown.addItem("No past messages");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load clients.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

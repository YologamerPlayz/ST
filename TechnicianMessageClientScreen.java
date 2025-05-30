import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TechnicianMessageClientScreen extends JPanel {
    private final MainApp mainApp;
    private final int technicianId;
    private final int clientId;
    private int conversationId;

    private JTextArea chatArea;
    private JTextField inputField;

    public TechnicianMessageClientScreen(MainApp mainApp, int clientId) {
        this.mainApp = mainApp;
        this.technicianId = mainApp.getCurrentUserId();
        this.clientId = clientId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        backBtn.addActionListener(e -> mainApp.switchScreen(new TechnicianMessageLauncherScreen(mainApp)));

        JLabel title = new JLabel("Chat with Client", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // Optional padding panel to balance layout visually
        JPanel emptyRight = new JPanel(); 

        topPanel.add(backBtn, BorderLayout.EAST);
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(emptyRight, BorderLayout.WEST); // optional
        add(topPanel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> {
            String msg = inputField.getText().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                inputField.setText("");
                loadMessages();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        initializeConversation();
        loadMessages();
    }

    private void initializeConversation() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = """
                SELECT c.id
                FROM conversations c
                JOIN conversation_users cu1 ON cu1.conversation_id = c.id AND cu1.user_id = ?
                JOIN conversation_users cu2 ON cu2.conversation_id = c.id AND cu2.user_id = ?
                """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, clientId);
            stmt.setInt(2, technicianId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                conversationId = rs.getInt("id");
            } else {
                String createConv = "INSERT INTO conversations () VALUES ()";
                PreparedStatement createStmt = conn.prepareStatement(createConv, Statement.RETURN_GENERATED_KEYS);
                createStmt.executeUpdate();
                ResultSet genKeys = createStmt.getGeneratedKeys();
                if (genKeys.next()) {
                    conversationId = genKeys.getInt(1);
                }

                PreparedStatement linkUsers = conn.prepareStatement("INSERT INTO conversation_users (conversation_id, user_id) VALUES (?, ?)");
                linkUsers.setInt(1, conversationId);
                linkUsers.setInt(2, clientId);
                linkUsers.executeUpdate();
                linkUsers.setInt(2, technicianId);
                linkUsers.executeUpdate();

                createStmt.close();
                genKeys.close();
                linkUsers.close();
            }

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing conversation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMessages() {
        chatArea.setText("");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = """
                SELECT u.name, m.content, m.created_at
                FROM messages m
                JOIN users u ON u.id = m.sender_id
                WHERE m.conversation_id = ?
                ORDER BY m.created_at
                """;
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, conversationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                chatArea.append("[" + rs.getTimestamp("created_at") + "] " + rs.getString("name") + ": " + rs.getString("content") + "\n");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String content) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String insert = "INSERT INTO messages (conversation_id, sender_id, content) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insert);
            stmt.setInt(1, conversationId);
            stmt.setInt(2, technicianId);
            stmt.setString(3, content);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

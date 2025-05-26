import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TechnicianActionScreen extends JPanel {
    private MainApp mainApp;
    private JPanel userInfoPanel;
    private boolean userInfoVisible = false;

    public TechnicianActionScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        
        setLayout(new BorderLayout());
        
        // Top panel for the Show User Info button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton showUserInfoBtn = new JButton("Show User Info");
        showUserInfoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleUserInfo();
            }
        });
        topPanel.add(showUserInfoBtn);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel for main content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;

        JLabel title = new JLabel("Technician Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        centerPanel.add(title, gbc);

        String[] actions = {"Notifications", "View Calendar", "View History"};
        for (int i = 0; i < actions.length; i++) {
            JButton btn = new JButton(actions[i]);
            gbc.gridy = i + 1;
            centerPanel.add(btn, gbc);
        }
        
        add(centerPanel, BorderLayout.CENTER);
        
        // User info panel (initially hidden)
        userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        userInfoPanel.setVisible(false);
        JPanel eastWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        eastWrapper.add(userInfoPanel);
        add(eastWrapper, BorderLayout.EAST);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("hammer_time.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
    }
    
    private void toggleUserInfo() {
        if (userInfoVisible) {
            userInfoPanel.setVisible(false);
            userInfoVisible = false;
        } else {
            loadAndDisplayUserInfo();
            userInfoPanel.setVisible(true);
            userInfoVisible = true;
        }
        revalidate();
        repaint();
    }
    
    private void loadAndDisplayUserInfo() {
        userInfoPanel.removeAll();
        
        try {
            // Get user ID - replace this with your method to get the current user's ID
            int userId = mainApp.getCurrentUserId();
            
            // Database connection - adjust connection parameters as needed
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");
            
            // Query to get user info and technician specialty
            String query = "SELECT u.name, u.email, u.phone, t.specialty, t.rating " +
                          "FROM users u " +
                          "JOIN technicians t ON u.id = t.user_id " +
                          "WHERE u.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String specialty = rs.getString("specialty");
                double rating = rs.getDouble("rating");
                
                userInfoPanel.add(new JLabel("Name: " + (name != null ? name : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Email: " + (email != null ? email : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Phone: " + (phone != null ? phone : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Specialty: " + (specialty != null ? specialty : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Rating: " + rating + "/5.0"));
            } else {
                userInfoPanel.add(new JLabel("User not found"));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            userInfoPanel.add(new JLabel("Error loading user info"));
            e.printStackTrace();
        }
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ClientActionScreen extends JPanel {
    private MainApp mainApp;
    private JPanel userInfoPanel;
    private boolean userInfoVisible = false;
    private JLabel addressLabel;

    public ClientActionScreen(MainApp mainApp) {
        this.mainApp = mainApp;
        
        setLayout(new BorderLayout());
        
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
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;

        JLabel title = new JLabel("Client Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        centerPanel.add(title, gbc);

        String[] actions = {"Notifications", "Find Your Technician"};
        for (int i = 0; i < actions.length; i++) {
            JButton btn = new JButton(actions[i]);
            gbc.gridy = i + 1;
            centerPanel.add(btn, gbc);
        }
        
        JButton changeAddressBtn = new JButton("Change Address");
        gbc.gridy = actions.length + 1;
        centerPanel.add(changeAddressBtn, gbc);
        changeAddressBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptAndChangeAddress();
            }
        });

        JButton viewHistoryBtn = new JButton("View History");
        gbc.gridy = actions.length + 2;
        centerPanel.add(viewHistoryBtn, gbc);

        add(centerPanel, BorderLayout.CENTER);
        
        userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        userInfoPanel.setVisible(false);
        JPanel eastWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        eastWrapper.add(userInfoPanel);
        add(eastWrapper, BorderLayout.EAST);
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
            int userId = mainApp.getCurrentUserId();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345");

            // Join users and clients to get all info
            String query = "SELECT u.name, u.email, u.phone, c.address " +
                           "FROM users u LEFT JOIN clients c ON u.id = c.user_id " +
                           "WHERE u.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                userInfoPanel.add(new JLabel("Name: " + (name != null ? name : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Email: " + (email != null ? email : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Phone: " + (phone != null ? phone : "N/A")));
                userInfoPanel.add(Box.createVerticalStrut(5));
                userInfoPanel.add(new JLabel("Address: " + (address != null ? phone : "N/A")));
                
                userInfoPanel.add(Box.createVerticalStrut(10)); // spacing
                JButton logoutButton = new JButton("Logout");
                logoutButton.addActionListener(e -> {
                    mainApp.setCurrentUserId(-1); // reset session
                    mainApp.setCurrentUserRole(null); // reset role if stored
                    mainApp.switchScreen(new LoginScreen(mainApp)); // go back to login
                });
                JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                logoutWrapper.add(logoutButton);
                userInfoPanel.add(logoutWrapper);
                
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

    private void promptAndChangeAddress() {
        int userId = mainApp.getCurrentUserId();
        
        String currentAddress = null;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
            String query = "SELECT address FROM clients WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentAddress = rs.getString("address");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving current address", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        
        String newAddress = (String) JOptionPane.showInputDialog(
                this,
                "Enter new address:",
                "Change Address",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                currentAddress != null ? currentAddress : ""
        );
        
        if (newAddress != null && !newAddress.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/TL", "root", "12345")) {
                // Check if client row exists, if not insert it (clients table might be empty)
                String checkClientSql = "SELECT user_id FROM clients WHERE user_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkClientSql);
                checkStmt.setInt(1, userId);
                ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next()) {
                    // Update existing address
                    String updateSql = "UPDATE clients SET address = ? WHERE user_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, newAddress);
                    updateStmt.setInt(2, userId);
                    int rows = updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    // Insert new client record (address only, assuming no history yet)
                    String insertSql = "INSERT INTO clients (user_id, address) VALUES (?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, newAddress);
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
                
                checkRs.close();
                checkStmt.close();

                JOptionPane.showMessageDialog(this, "Address updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (userInfoVisible) {
                    loadAndDisplayUserInfo();
                    revalidate();
                    repaint();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating address.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}

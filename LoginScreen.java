import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JPanel {
    private MainApp mainApp;

    public LoginScreen(MainApp mainApp) {
        this.mainApp = mainApp;

        setLayout(new GridBagLayout());
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.insets = new Insets(10, 10, 10, 10);
        outerGbc.gridx = 0;
        outerGbc.fill = GridBagConstraints.NONE;
        outerGbc.anchor = GridBagConstraints.CENTER;

        // Header
        JLabel headerLabel = new JLabel("Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        outerGbc.gridy = 0;
        add(headerLabel, outerGbc);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JLabel statusLabel = new JLabel(" ");

        // Add fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);

        // Sign up link
        JButton signupLink = new JButton("Don't have an account? Sign Up");
        signupLink.setBorderPainted(false);
        signupLink.setOpaque(false);
        signupLink.setBackground(Color.WHITE);
        signupLink.setForeground(Color.BLUE);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signupLink.addActionListener(e -> mainApp.switchToSignup());

        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(signupLink, gbc);

        // Add form panel to outer layout
        outerGbc.gridy = 1;
        add(formPanel, outerGbc);

        // Login logic
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if (username.equals("admin") && password.equals("password")) {
                    mainApp.switchToActions();
                } else {
                    statusLabel.setText("Invalid credentials.");
                }
            }
        });
    }
}
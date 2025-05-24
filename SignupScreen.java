import javax.swing.*;
import java.awt.*;

public class SignupScreen extends JPanel {
    private MainApp mainApp;

    private JPanel formPanel;
    private JComboBox<String> userTypeCombo;
    private JTextField nameField, emailField, phoneField;
    private JComboBox<String> specialtyCombo;

    public SignupScreen(MainApp mainApp) {
        this.mainApp = mainApp;

        setLayout(new GridBagLayout());
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.insets = new Insets(10, 10, 10, 10);
        outerGbc.gridx = 0;
        outerGbc.fill = GridBagConstraints.NONE;
        outerGbc.anchor = GridBagConstraints.CENTER;

        // Header
        JLabel header = new JLabel("Sign Up", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        outerGbc.gridy = 0;
        add(header, outerGbc);

        // Account type selector
        JPanel selectorPanel = new JPanel();
        selectorPanel.add(new JLabel("Account Type:"));
        userTypeCombo = new JComboBox<>(new String[]{"Client", "Technician"});
        selectorPanel.add(userTypeCombo);
        outerGbc.gridy = 1;
        add(selectorPanel, outerGbc);

        // Form Panel
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 200));
        updateForm("Client");
        outerGbc.gridy = 2;
        add(formPanel, outerGbc);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        outerGbc.gridy = 3;
        add(submitButton, outerGbc);

        // Switch between forms
        userTypeCombo.addActionListener(e -> updateForm((String) userTypeCombo.getSelectedItem()));

        // On submit: show confirmation and go back to login
        submitButton.addActionListener(e -> {
            String type = (String) userTypeCombo.getSelectedItem();
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String specialty = (type.equals("Technician") ? (String) specialtyCombo.getSelectedItem() : "N/A");

            JOptionPane.showMessageDialog(this,
                String.format("%s Registered!\nName: %s\nEmail: %s\nPhone: %s\nSpecialty: %s",
                    type, name, email, phone, specialty));

            mainApp.switchToLogin(); // Back to login
        });
    }

    private void updateForm(String type) {
        formPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Name
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Specialty (only for technician)
        if (type.equals("Technician")) {
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Specialty:"), gbc);
            gbc.gridx = 1;
            specialtyCombo = new JComboBox<>(new String[] {
            	    "Plumber", "Electrician", "Steamfitter"
            });
            formPanel.add(specialtyCombo, gbc);
        }

        revalidate();
        repaint();
    }
}

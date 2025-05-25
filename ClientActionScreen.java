import javax.swing.*;
import java.awt.*;

public class ClientActionScreen extends JPanel {

    public ClientActionScreen() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;

        JLabel title = new JLabel("Client Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(title, gbc);

        String[] actions = {"Notifications", "Find Your Technician", "View History"};
        for (int i = 0; i < actions.length; i++) {
            JButton btn = new JButton(actions[i]);
            gbc.gridy = i + 1;
            add(btn, gbc);
        }
    }
}
import javax.swing.*;
import java.awt.*;

public class ActionScreen extends JPanel {

    public ActionScreen() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel title = new JLabel("Actions", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(title, gbc);

        // Add 5 placeholder buttons
        for (int i = 1; i <= 5; i++) {
            JButton button = new JButton("Placeholder" + i);
            gbc.gridy = i;
            add(button, gbc);
        }
    }
}
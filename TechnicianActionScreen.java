import javax.swing.*;
import java.awt.*;

public class TechnicianActionScreen extends JPanel {

    public TechnicianActionScreen() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;

        JLabel title = new JLabel("Technician Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 0;
        add(title, gbc);

        String[] actions = {"Notifications", "View Calendar", "View History"};
        for (int i = 0; i < actions.length; i++) {
            JButton btn = new JButton(actions[i]);
            gbc.gridy = i + 1;
            add(btn, gbc);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("hammer_time.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}
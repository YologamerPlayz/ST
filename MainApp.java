import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("Μαστοράκος");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Start with the login screen
        setContentPane(new LoginScreen(this));
        setVisible(true);
    }
    
    private int currentUserId;
    public void setCurrentUserId(int id) { this.currentUserId = id; }
    public int getCurrentUserId() { return currentUserId; }
    
    private String currentUserRole;
    public void setCurrentUserRole(String role) { this.currentUserRole = role; }
    public String getCurrentUserRole() { return currentUserRole; }
    
    public void switchScreen(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
    public void setContentPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }
}

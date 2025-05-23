import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("User System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Start with the login screen
        setContentPane(new LoginScreen(this));
        setVisible(true);
    }

    public void switchToLogin() {
        setContentPane(new LoginScreen(this));
        revalidate();
        repaint();
    }

    public void switchToSignup() {
        setContentPane(new SignupScreen(this));
        revalidate();
        repaint();
    }

    public void switchToActions() {
        setContentPane(new ActionScreen());
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
public class MainApp{
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginScreen loginWindow = new LoginScreen();
            loginWindow.setVisible(true);
        });
    }
}
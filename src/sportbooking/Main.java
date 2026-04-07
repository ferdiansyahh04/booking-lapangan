package sportbooking;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sportbooking.model.User;
import sportbooking.ui.LoginFrame;
import sportbooking.ui.MainFrame;

public class Main {

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "MySQL JDBC driver tidak ditemukan!\n"
                    + "Tambahkan file mysql-connector-j-x.x.x.jar ke Libraries project.\n\n"
                    + "Contoh nama file: mysql-connector-j-9.3.0.jar",
                    "Driver Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        // Show login
        SwingUtilities.invokeLater(() -> showLogin());
    }

    public static void showLogin() {
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);

        User user = loginFrame.getLoggedInUser();
        if (user != null) {
            MainFrame frame = new MainFrame(user);
            frame.setVisible(true);
        } else {
            System.exit(0);
        }
    }
}

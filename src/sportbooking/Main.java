package sportbooking;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sportbooking.model.User;
import sportbooking.ui.LoginFrame;
import sportbooking.ui.MainFrame;

public class Main {

    public static void main(String[] args) {
        // Load SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "SQLite JDBC driver tidak ditemukan!\n"
                    + "Pastikan file sqlite-jdbc-3.x.x.jar sudah ditambahkan ke Libraries project.\n\n"
                    + "Download di: https://github.com/xerial/sqlite-jdbc/releases",
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

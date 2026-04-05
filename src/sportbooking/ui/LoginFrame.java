package sportbooking.ui;

import java.awt.*;
import javax.swing.*;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.User;

public class LoginFrame extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private User loggedInUser;

    public LoginFrame() {
        setTitle("Login - Sport Booking System");
        setModal(true);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(33, 97, 140));
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("SPORT BOOKING SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogin.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblLogin, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(fieldFont);
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(fieldFont);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(fieldFont);
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(fieldFont);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(txtPassword, gbc);

        // Buttons

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(39, 174, 96));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBackground(new Color(52, 152, 219));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setOpaque(true);
        btnRegister.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnBatal = new JButton("Keluar");
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBatal.setBackground(new Color(231, 76, 60));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setContentAreaFilled(false);
        btnBatal.setOpaque(true);
        btnBatal.setBorder(BorderFactory.createEmptyBorder(10, 35, 10, 35));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);
        btnPanel.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gbc);

        // Default info
        JLabel lblInfo = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(150, 150, 150));
        gbc.gridy = 4; gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(lblInfo, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Events

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> {
            RegisterFrame reg = new RegisterFrame(SwingUtilities.getWindowAncestor(this));
            reg.setVisible(true);
        });
        btnBatal.addActionListener(e -> {
            loggedInUser = null;
            dispose();
        });
        txtPassword.addActionListener(e -> doLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        getRootPane().setDefaultButton(btnLogin);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password harus diisi!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatabaseHelper.getInstance();
        User user = DatabaseHelper.getInstance().authenticateUser(username, password);
        if (user != null) {
            loggedInUser = user;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah!",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}

package sportbooking.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.User;

public class RegisterFrame extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNamaLengkap;

    public RegisterFrame(Window parent) {
        super(parent, "Registrasi Pelanggan", ModalityType.APPLICATION_MODAL);
        setSize(450, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(java.awt.Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Registrasi Pelanggan", SwingConstants.CENTER);
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtUsername, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtPassword, gbc);

        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Nama Lengkap:"), gbc);

        txtNamaLengkap = new JTextField(20);
        txtNamaLengkap.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtNamaLengkap, gbc);

        javax.swing.JButton btnRegister = new javax.swing.JButton("Daftar");
        javax.swing.JButton btnBatal = new javax.swing.JButton("Batal");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(java.awt.Color.WHITE);
        btnPanel.add(btnRegister);
        btnPanel.add(btnBatal);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(btnPanel, gbc);

        setContentPane(panel);

        btnRegister.addActionListener(e -> doRegister());
        btnBatal.addActionListener(e -> dispose());
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String nama = txtNamaLengkap.getText().trim();

        if (username.isEmpty() || password.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User(username, password, nama, "Pelanggan");
        boolean success = DatabaseHelper.getInstance().insertUser(user);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal! Username mungkin sudah digunakan.", "Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
}

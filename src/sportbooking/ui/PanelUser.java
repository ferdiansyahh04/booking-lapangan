package sportbooking.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.User;

public class PanelUser extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtNamaLengkap;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBatal, btnResetPassword;
    private int selectedId = -1;

    public PanelUser(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        " Form User ",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(44, 62, 80)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        txtUsername = new JTextField(15); txtUsername.setFont(fieldFont);
        txtPassword = new JPasswordField(15); txtPassword.setFont(fieldFont);
        txtNamaLengkap = new JTextField(15); txtNamaLengkap.setFont(fieldFont);
        cmbRole = new JComboBox<>(new String[]{"Staff", "Admin"});
        cmbRole.setFont(fieldFont);

        addFormRow(formPanel, gbc, 0, "Username:", txtUsername);
        addFormRow(formPanel, gbc, 1, "Password:", txtPassword);
        addFormRow(formPanel, gbc, 2, "Nama Lengkap:", txtNamaLengkap);
        addFormRow(formPanel, gbc, 3, "Role:", cmbRole);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);
        btnSimpan = createButton("Simpan", new Color(39, 174, 96));
        btnUpdate = createButton("Update", new Color(41, 128, 185));
        btnResetPassword = createButton("Reset Password", new Color(243, 156, 18));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnResetPassword.setEnabled(false);
        btnPanel.add(btnSimpan); btnPanel.add(btnUpdate); btnPanel.add(btnResetPassword);
        btnPanel.add(btnHapus); btnPanel.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gbc);

        // Table
        String[] columns = {"ID", "Username", "Nama Lengkap", "Role", "Dibuat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(214, 234, 248));
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadSelectedRow();
        });
        btnSimpan.addActionListener(e -> simpan());
        btnUpdate.addActionListener(e -> update());
        btnResetPassword.addActionListener(e -> resetPassword());
        btnHapus.addActionListener(e -> hapus());
        btnBatal.addActionListener(e -> clearForm());

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            txtUsername.setText(tableModel.getValueAt(row, 1).toString());
            txtNamaLengkap.setText(tableModel.getValueAt(row, 2).toString());
            cmbRole.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            txtPassword.setText("");
            txtPassword.setEnabled(false);
            btnSimpan.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnHapus.setEnabled(true);
            btnResetPassword.setEnabled(true);
        }
    }

    private void simpan() {
        if (!validateForm(true)) return;
        User user = new User();
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(new String(txtPassword.getPassword()));
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setRole(cmbRole.getSelectedItem().toString());
        if (DatabaseHelper.getInstance().insertUser(user)) {
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user!\nUsername mungkin sudah digunakan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        if (selectedId == -1 || !validateForm(false)) return;
        User user = new User();
        user.setId(selectedId);
        user.setUsername(txtUsername.getText().trim());
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setRole(cmbRole.getSelectedItem().toString());
        if (DatabaseHelper.getInstance().updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        if (selectedId == -1) return;
        String newPass = JOptionPane.showInputDialog(this, "Masukkan password baru:", "Reset Password", JOptionPane.QUESTION_MESSAGE);
        if (newPass != null && !newPass.trim().isEmpty()) {
            if (DatabaseHelper.getInstance().updateUserPassword(selectedId, newPass.trim())) {
                JOptionPane.showMessageDialog(this, "Password berhasil direset!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void hapus() {
        if (selectedId == -1) return;
        if (selectedId == 1) {
            JOptionPane.showMessageDialog(this, "User admin default tidak dapat dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus user ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().deleteUser(selectedId)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); refreshData();
            }
        }
    }

    private boolean validateForm(boolean requirePassword) {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus(); return false;
        }
        if (requirePassword && new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus(); return false;
        }
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaLengkap.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txtUsername.setText(""); txtPassword.setText(""); txtNamaLengkap.setText("");
        cmbRole.setSelectedIndex(0);
        txtPassword.setEnabled(true);
        selectedId = -1; table.clearSelection();
        btnSimpan.setEnabled(true); btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false); btnResetPassword.setEnabled(false);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<User> list = DatabaseHelper.getInstance().getAllUsers();
        for (User u : list) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getNamaLengkap(), u.getRole(), u.getCreatedAt()});
        }
    }
}

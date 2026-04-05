package sportbooking.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Pelanggan;

public class PanelPelanggan extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtNoTelepon, txtEmail, txtAlamat;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBatal;
    private int selectedId = -1;

    public PanelPelanggan(MainFrame mainFrame) {
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
                        " Form Pelanggan ",
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
        txtNama = new JTextField(15); txtNama.setFont(fieldFont);
        txtNoTelepon = new JTextField(15); txtNoTelepon.setFont(fieldFont);
        txtEmail = new JTextField(15); txtEmail.setFont(fieldFont);
        txtAlamat = new JTextField(15); txtAlamat.setFont(fieldFont);

        addFormRow(formPanel, gbc, 0, "Nama Pelanggan:", txtNama);
        addFormRow(formPanel, gbc, 1, "No. Telepon:", txtNoTelepon);
        addFormRow(formPanel, gbc, 2, "Email:", txtEmail);
        addFormRow(formPanel, gbc, 3, "Alamat:", txtAlamat);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);
        btnSimpan = createButton("Simpan", new Color(39, 174, 96));
        btnUpdate = createButton("Update", new Color(41, 128, 185));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnPanel.add(btnSimpan); btnPanel.add(btnUpdate); btnPanel.add(btnHapus); btnPanel.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gbc);

        // Table
        String[] columns = {"ID", "Nama", "No. Telepon", "Email", "Alamat"};
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadSelectedRow();
        });
        btnSimpan.addActionListener(e -> simpan());
        btnUpdate.addActionListener(e -> update());
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
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            txtNoTelepon.setText(val(tableModel.getValueAt(row, 2)));
            txtEmail.setText(val(tableModel.getValueAt(row, 3)));
            txtAlamat.setText(val(tableModel.getValueAt(row, 4)));
            btnSimpan.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnHapus.setEnabled(true);
        }
    }

    private String val(Object o) { return o == null ? "" : o.toString(); }

    private void simpan() {
        if (!validateForm()) return;
        Pelanggan p = new Pelanggan();
        p.setNama(txtNama.getText().trim());
        p.setNoTelepon(txtNoTelepon.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        if (DatabaseHelper.getInstance().insertPelanggan(p)) {
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        if (selectedId == -1 || !validateForm()) return;
        Pelanggan p = new Pelanggan();
        p.setId(selectedId);
        p.setNama(txtNama.getText().trim());
        p.setNoTelepon(txtNoTelepon.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        if (DatabaseHelper.getInstance().updatePelanggan(p)) {
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapus() {
        if (selectedId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus pelanggan ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().deletePelanggan(selectedId)) {
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); refreshData();
            }
        }
    }

    private boolean validateForm() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pelanggan harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txtNama.setText(""); txtNoTelepon.setText(""); txtEmail.setText(""); txtAlamat.setText("");
        selectedId = -1; table.clearSelection();
        btnSimpan.setEnabled(true); btnUpdate.setEnabled(false); btnHapus.setEnabled(false);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Pelanggan> list = DatabaseHelper.getInstance().getAllPelanggan();
        for (Pelanggan p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getNama(), p.getNoTelepon(), p.getEmail(), p.getAlamat()});
        }
    }
}

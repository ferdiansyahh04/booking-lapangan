package sportbooking.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Lapangan;

public class PanelLapangan extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama;
    private JComboBox<String> cmbJenis;
    private JTextField txtHarga;
    private JComboBox<String> cmbStatus;
    private JButton btnSimpan, btnUpdate, btnHapus, btnBatal;
    private int selectedId = -1;

    public PanelLapangan(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        " Form Lapangan ",
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

        txtNama = new JTextField(15);
        cmbJenis = new JComboBox<>(new String[]{"Futsal", "Basket", "Bulutangkis"});
        txtHarga = new JTextField(15);
        cmbStatus = new JComboBox<>(new String[]{"Tersedia", "Tidak Tersedia"});

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        txtNama.setFont(fieldFont);
        txtHarga.setFont(fieldFont);
        cmbJenis.setFont(fieldFont);
        cmbStatus.setFont(fieldFont);

        addFormRow(formPanel, gbc, 0, "Nama Lapangan:", txtNama);
        addFormRow(formPanel, gbc, 1, "Jenis:", cmbJenis);
        addFormRow(formPanel, gbc, 2, "Harga per Jam (Rp):", txtHarga);
        addFormRow(formPanel, gbc, 3, "Status:", cmbStatus);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);

        btnSimpan = createButton("Simpan", new Color(39, 174, 96));
        btnUpdate = createButton("Update", new Color(41, 128, 185));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));

        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);

        btnPanel.add(btnSimpan);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);
        btnPanel.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gbc);

        // === Table ===
        String[] columns = {"ID", "Nama Lapangan", "Jenis", "Harga/Jam", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(214, 234, 248));
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRow();
            }
        });

        btnSimpan.addActionListener(e -> simpanLapangan());
        btnUpdate.addActionListener(e -> updateLapangan());
        btnHapus.addActionListener(e -> hapusLapangan());
        btnBatal.addActionListener(e -> clearForm());

        // Layout
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
            cmbJenis.setSelectedItem(tableModel.getValueAt(row, 2).toString());
            String harga = tableModel.getValueAt(row, 3).toString().replace("Rp ", "").replace(",", "").replace(".", "");
            txtHarga.setText(harga);
            cmbStatus.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            btnSimpan.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnHapus.setEnabled(true);
        }
    }

    private void simpanLapangan() {
        if (!validateForm()) return;

        Lapangan lap = new Lapangan();
        lap.setNama(txtNama.getText().trim());
        lap.setJenis(cmbJenis.getSelectedItem().toString());
        lap.setHargaPerJam(Double.parseDouble(txtHarga.getText().trim()));
        lap.setStatus(cmbStatus.getSelectedItem().toString());

        if (DatabaseHelper.getInstance().insertLapangan(lap)) {
            JOptionPane.showMessageDialog(this, "Lapangan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan lapangan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLapangan() {
        if (selectedId == -1 || !validateForm()) return;

        Lapangan lap = new Lapangan();
        lap.setId(selectedId);
        lap.setNama(txtNama.getText().trim());
        lap.setJenis(cmbJenis.getSelectedItem().toString());
        lap.setHargaPerJam(Double.parseDouble(txtHarga.getText().trim()));
        lap.setStatus(cmbStatus.getSelectedItem().toString());

        if (DatabaseHelper.getInstance().updateLapangan(lap)) {
            JOptionPane.showMessageDialog(this, "Lapangan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate lapangan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusLapangan() {
        if (selectedId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus lapangan ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().deleteLapangan(selectedId)) {
                JOptionPane.showMessageDialog(this, "Lapangan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus lapangan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lapangan harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return false;
        }
        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            if (harga <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtHarga.requestFocus();
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtNama.setText("");
        cmbJenis.setSelectedIndex(0);
        txtHarga.setText("");
        cmbStatus.setSelectedIndex(0);
        selectedId = -1;
        table.clearSelection();
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Lapangan> list = DatabaseHelper.getInstance().getAllLapangan();
        for (Lapangan lap : list) {
            tableModel.addRow(new Object[]{
                    lap.getId(),
                    lap.getNama(),
                    lap.getJenis(),
                    "Rp " + String.format("%,.0f", lap.getHargaPerJam()),
                    lap.getStatus()
            });
        }
    }
}

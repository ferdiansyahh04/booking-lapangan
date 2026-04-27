package sportbooking.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Pembayaran;
import sportbooking.model.Reservasi;

public class PanelPembayaran extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<ReservasiItem> cmbReservasi;
    private JTextField txtJumlah;
    private JComboBox<String> cmbMetode;
    private JTextField txtKeterangan;
    private JLabel lblInfoReservasi;

    public PanelPembayaran(MainFrame mainFrame) {
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
                        " Form Pembayaran ",
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

        cmbReservasi = new JComboBox<>();
        cmbReservasi.setFont(fieldFont);

        lblInfoReservasi = new JLabel("-");
        lblInfoReservasi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfoReservasi.setForeground(new Color(41, 128, 185));

        txtJumlah = new JTextField(15);
        txtJumlah.setFont(fieldFont);

        cmbMetode = new JComboBox<>(new String[]{"Tunai", "Transfer Bank", "E-Wallet"});
        cmbMetode.setFont(fieldFont);

        txtKeterangan = new JTextField(15);
        txtKeterangan.setFont(fieldFont);

        addFormRow(formPanel, gbc, 0, "Reservasi:", cmbReservasi);
        addFormRow(formPanel, gbc, 1, "Info Reservasi:", lblInfoReservasi);
        addFormRow(formPanel, gbc, 2, "Jumlah Bayar (Rp):", txtJumlah);
        addFormRow(formPanel, gbc, 3, "Metode Pembayaran:", cmbMetode);
        addFormRow(formPanel, gbc, 4, "Keterangan:", txtKeterangan);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnBayar = createButton("Proses Pembayaran", new Color(39, 174, 96));
        JButton btnHapus = createButton("Hapus", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(41, 128, 185));
        btnPanel.add(btnBayar); btnPanel.add(btnHapus); btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gbc);

        // Table
        String[] columns = {"ID", "Nama Pemesan", "Lapangan", "Jumlah Bayar", "Metode", "Tgl Bayar", "Keterangan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        DefaultTableCellRenderer centerRenderer = TableStyler.createCenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        TableStyler.styleTable(table, scrollPane, 30);

        // Events
        cmbReservasi.addActionListener(e -> onReservasiChanged());
        btnBayar.addActionListener(e -> prosesBayar());
        btnHapus.addActionListener(e -> hapusPembayaran());
        btnRefresh.addActionListener(e -> refreshData());

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

    private void onReservasiChanged() {
        ReservasiItem item = (ReservasiItem) cmbReservasi.getSelectedItem();
        if (item != null) {
            Reservasi r = item.reservasi;
            lblInfoReservasi.setText(r.getNamaLapangan() + " | " + r.getTanggal() + " | "
                    + r.getJamMulai() + "-" + r.getJamSelesai() + " | Rp " + String.format("%,.0f", r.getTotalHarga()));
            txtJumlah.setText(String.format("%.0f", r.getTotalHarga()));
        } else {
            lblInfoReservasi.setText("-");
            txtJumlah.setText("");
        }
    }

    private void prosesBayar() {
        ReservasiItem item = (ReservasiItem) cmbReservasi.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus(); return;
        }
        double jumlah;
        try {
            jumlah = Double.parseDouble(txtJumlah.getText().trim());
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus angka positif!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus(); return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Proses pembayaran Rp " + String.format("%,.0f", jumlah) + " untuk reservasi " + item.reservasi.getNamaPemesan() + "?",
                "Konfirmasi Pembayaran", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Pembayaran p = new Pembayaran();
            p.setReservasiId(item.reservasi.getId());
            p.setJumlahBayar(jumlah);
            p.setMetodePembayaran(cmbMetode.getSelectedItem().toString());
            p.setTanggalBayar(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            p.setKeterangan(txtKeterangan.getText().trim());
            if (DatabaseHelper.getInstance().insertPembayaran(p)) {
                DatabaseHelper.getInstance().updateStatusReservasi(item.reservasi.getId(), "dibayar");
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil diproses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                txtKeterangan.setText("");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memproses pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusPembayaran() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data pembayaran terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus data pembayaran ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().deletePembayaran(id)) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        }
    }

    public void refreshData() {
        // Refresh combo reservasi belum bayar
        cmbReservasi.removeAllItems();
        List<Reservasi> belumBayar = DatabaseHelper.getInstance().getReservasiBelumBayar();
        for (Reservasi r : belumBayar) {
            cmbReservasi.addItem(new ReservasiItem(r));
        }

        // Refresh table pembayaran
        tableModel.setRowCount(0);
        List<Pembayaran> list = DatabaseHelper.getInstance().getAllPembayaran();
        for (Pembayaran p : list) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getNamaPemesan(), p.getNamaLapangan(),
                "Rp " + String.format("%,.0f", p.getJumlahBayar()),
                p.getMetodePembayaran(), p.getTanggalBayar(), p.getKeterangan()
            });
        }
    }

    // Helper class for combo box display
    private static class ReservasiItem {
        Reservasi reservasi;
        ReservasiItem(Reservasi r) { this.reservasi = r; }
        @Override
        public String toString() {
            return "#" + reservasi.getId() + " - " + reservasi.getNamaPemesan()
                    + " (" + reservasi.getNamaLapangan() + ", " + reservasi.getTanggal() + ")";
        }
    }
}

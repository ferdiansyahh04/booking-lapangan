package sportbooking.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Lapangan;
import sportbooking.model.Reservasi;

public class PanelReservasi extends JPanel {

    private static final Color BTN_PRIMARY = new Color(25, 135, 84);
    private static final Color BTN_PRIMARY_HOVER = new Color(20, 108, 67);
    private static final Color BTN_SECONDARY = new Color(108, 117, 125);
    private static final Color BTN_SECONDARY_HOVER = new Color(84, 91, 98);

    private MainFrame mainFrame;
    private JTextField txtNamaPemesan;
    private JTextField txtNoTelepon;
    private JComboBox<String> cmbJenis;
    private JComboBox<LapanganItem> cmbLapangan;
    private JComboBox<String> cmbTanggal;
    private JComboBox<String> cmbJamMulai;
    private JComboBox<Integer> cmbDurasi;
    private JLabel lblJamSelesai;
    private JLabel lblHargaPerJam;
    private JLabel lblTotalHarga;

    public PanelReservasi(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        " Form Reservasi Baru ",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(44, 62, 80)
                ),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Nama Pemesan
        txtNamaPemesan = new JTextField(20);
        txtNamaPemesan.setFont(fieldFont);

        // No Telepon
        txtNoTelepon = new JTextField(20);
        txtNoTelepon.setFont(fieldFont);

        // Jenis Lapangan
        cmbJenis = new JComboBox<>(new String[]{"-- Pilih Jenis --", "Futsal", "Basket", "Bulutangkis"});
        cmbJenis.setFont(fieldFont);

        // Lapangan
        cmbLapangan = new JComboBox<>();
        cmbLapangan.setFont(fieldFont);
        cmbLapangan.setEnabled(false);

        // Tanggal (7 hari kedepan)
        cmbTanggal = new JComboBox<>();
        cmbTanggal.setFont(fieldFont);
        populateTanggal();

        // Jam Mulai
        cmbJamMulai = new JComboBox<>();
        cmbJamMulai.setFont(fieldFont);
        for (int h = 7; h <= 22; h++) {
            cmbJamMulai.addItem(String.format("%02d:00", h));
        }

        // Durasi
        cmbDurasi = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        cmbDurasi.setFont(fieldFont);

        // Labels info
        lblJamSelesai = new JLabel("-");
        lblJamSelesai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJamSelesai.setForeground(new Color(41, 128, 185));

        lblHargaPerJam = new JLabel("-");
        lblHargaPerJam.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHargaPerJam.setForeground(new Color(39, 174, 96));

        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalHarga.setForeground(new Color(192, 57, 43));

        // Section: Data Pemesan
        addSectionHeader(formPanel, gbc, 0, "DATA PEMESAN");
        addFormRow(formPanel, gbc, 1, "Nama Pemesan *", txtNamaPemesan);
        addFormRow(formPanel, gbc, 2, "No. Telepon *", txtNoTelepon);

        // Section: Pilih Lapangan
        addSectionHeader(formPanel, gbc, 3, "PILIH LAPANGAN");
        addFormRow(formPanel, gbc, 4, "Jenis Lapangan *", cmbJenis);
        addFormRow(formPanel, gbc, 5, "Lapangan *", cmbLapangan);
        addFormRow(formPanel, gbc, 6, "Harga per Jam", lblHargaPerJam);

        // Section: Waktu
        addSectionHeader(formPanel, gbc, 7, "WAKTU BOOKING");
        addFormRow(formPanel, gbc, 8, "Tanggal *", cmbTanggal);
        addFormRow(formPanel, gbc, 9, "Jam Mulai *", cmbJamMulai);
        addFormRow(formPanel, gbc, 10, "Durasi (jam) *", cmbDurasi);
        addFormRow(formPanel, gbc, 11, "Jam Selesai", lblJamSelesai);

        // Total dan Button
        JPanel totalPanel = new JPanel(new BorderLayout(10, 10));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel totalBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalBox.setBackground(new Color(253, 237, 236));
        totalBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 76, 60)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        JLabel lblTotalTitle = new JLabel("TOTAL BAYAR:  ");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalBox.add(lblTotalTitle);
        totalBox.add(lblTotalHarga);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBackground(Color.WHITE);

        RoundedButton btnReset = createActionButton("Reset Form", BTN_SECONDARY, BTN_SECONDARY_HOVER, new Dimension(150, 42), 13);
        RoundedButton btnBooking = createActionButton("Booking Sekarang", BTN_PRIMARY, BTN_PRIMARY_HOVER, new Dimension(210, 44), 14);

        btnPanel.add(btnReset);
        btnPanel.add(btnBooking);

        totalPanel.add(totalBox, BorderLayout.WEST);
        totalPanel.add(btnPanel, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(totalPanel, gbc);

        // Events
        cmbJenis.addActionListener(e -> onJenisChanged());
        cmbLapangan.addActionListener(e -> onLapanganChanged());
        cmbJamMulai.addActionListener(e -> updateJamSelesaiDanTotal());
        cmbDurasi.addActionListener(e -> updateJamSelesaiDanTotal());
        btnBooking.addActionListener(e -> prosesBooking());
        btnReset.addActionListener(e -> resetForm());

        // Scroll
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedButton createActionButton(String text, Color baseColor, Color hoverColor, Dimension size, int fontSize) {
        RoundedButton button = new RoundedButton(text, baseColor, hoverColor, 18);
        button.setButtonSize(size);
        button.setButtonFontSize(fontSize);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setHover(true);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setHover(false);
            }
        });
        return button;
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(row == 0 ? 5 : 15, 8, 5, 8);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(33, 97, 140));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(33, 97, 140)));
        panel.add(lbl, gbc);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void populateTanggal() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (EEEE)");
        SimpleDateFormat sdfValue = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            String display = sdf.format(cal.getTime());
            cmbTanggal.addItem(display);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private String getSelectedTanggal() {
        String selected = (String) cmbTanggal.getSelectedItem();
        if (selected != null && selected.length() >= 10) {
            return selected.substring(0, 10);
        }
        return "";
    }

    private void onJenisChanged() {
        cmbLapangan.removeAllItems();
        String jenis = (String) cmbJenis.getSelectedItem();
        if (jenis != null && !jenis.startsWith("--")) {
            cmbLapangan.setEnabled(true);
            List<Lapangan> list = DatabaseHelper.getInstance().getLapanganByJenis(jenis);
            for (Lapangan lap : list) {
                cmbLapangan.addItem(new LapanganItem(lap));
            }
            if (list.isEmpty()) {
                cmbLapangan.setEnabled(false);
                lblHargaPerJam.setText("Tidak ada lapangan tersedia");
            }
        } else {
            cmbLapangan.setEnabled(false);
            lblHargaPerJam.setText("-");
        }
        updateJamSelesaiDanTotal();
    }

    private void onLapanganChanged() {
        LapanganItem item = (LapanganItem) cmbLapangan.getSelectedItem();
        if (item != null) {
            lblHargaPerJam.setText("Rp " + String.format("%,.0f", item.lapangan.getHargaPerJam()) + " / jam");
        } else {
            lblHargaPerJam.setText("-");
        }
        updateJamSelesaiDanTotal();
    }

    private void updateJamSelesaiDanTotal() {
        String jamMulai = (String) cmbJamMulai.getSelectedItem();
        Integer durasi = (Integer) cmbDurasi.getSelectedItem();

        if (jamMulai != null && durasi != null) {
            int jam = Integer.parseInt(jamMulai.split(":")[0]) + durasi;
            if (jam > 24) jam = 24;
            lblJamSelesai.setText(String.format("%02d:00", jam));
        }

        LapanganItem item = (LapanganItem) cmbLapangan.getSelectedItem();
        if (item != null && durasi != null) {
            double total = item.lapangan.getHargaPerJam() * durasi;
            lblTotalHarga.setText("Rp " + String.format("%,.0f", total));
        } else {
            lblTotalHarga.setText("Rp 0");
        }
    }

    private void prosesBooking() {
        // Validasi
        if (txtNamaPemesan.getText().trim().isEmpty()) {
            showWarning("Nama pemesan harus diisi!"); txtNamaPemesan.requestFocus(); return;
        }
        if (txtNoTelepon.getText().trim().isEmpty()) {
            showWarning("No. telepon harus diisi!"); txtNoTelepon.requestFocus(); return;
        }
        if (!txtNoTelepon.getText().trim().matches("\\d{8,15}")) {
            showWarning("No. telepon harus berupa 8-15 digit angka!"); txtNoTelepon.requestFocus(); return;
        }
        if (cmbJenis.getSelectedIndex() == 0) {
            showWarning("Pilih jenis lapangan!"); return;
        }
        LapanganItem lapItem = (LapanganItem) cmbLapangan.getSelectedItem();
        if (lapItem == null) {
            showWarning("Pilih lapangan!"); return;
        }

        String tanggal = getSelectedTanggal();
        String jamMulai = (String) cmbJamMulai.getSelectedItem();
        int durasi = (Integer) cmbDurasi.getSelectedItem();
        int jamMulaiInt = Integer.parseInt(jamMulai.split(":")[0]);
        int jamSelesaiInt = jamMulaiInt + durasi;

        if (jamSelesaiInt > 24) {
            showWarning("Jam selesai melebihi batas waktu (24:00)! Kurangi durasi.");
            return;
        }

        String jamSelesai = String.format("%02d:00", jamSelesaiInt);

        // Cek konflik jadwal
        if (DatabaseHelper.getInstance().isLapanganBooked(lapItem.lapangan.getId(), tanggal, jamMulai, jamSelesai)) {
            showWarning("Lapangan sudah dibooking pada waktu tersebut!\nSilakan pilih waktu atau lapangan lain.");
            return;
        }

        double total = lapItem.lapangan.getHargaPerJam() * durasi;

        // Konfirmasi
        String pesan = String.format(
                "Konfirmasi Booking:\n\n"
                + "Pemesan  : %s\n"
                + "Telepon  : %s\n"
                + "Lapangan : %s\n"
                + "Tanggal  : %s\n"
                + "Waktu    : %s - %s (%d jam)\n"
                + "Total    : Rp %s\n\n"
                + "Lanjutkan booking?",
                txtNamaPemesan.getText().trim(),
                txtNoTelepon.getText().trim(),
                lapItem.lapangan.getNama(),
                tanggal,
                jamMulai, jamSelesai, durasi,
                String.format("%,.0f", total)
        );

        int confirm = JOptionPane.showConfirmDialog(this, pesan, "Konfirmasi Booking",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Reservasi res = new Reservasi();
            res.setNamaPemesan(txtNamaPemesan.getText().trim());
            res.setNoTelepon(txtNoTelepon.getText().trim());
            res.setLapanganId(lapItem.lapangan.getId());
            res.setTanggal(tanggal);
            res.setJamMulai(jamMulai);
            res.setJamSelesai(jamSelesai);
            res.setDurasiJam(durasi);
            res.setTotalHarga(total);
            res.setStatus("menunggu");

            if (DatabaseHelper.getInstance().insertReservasi(res)) {
                JOptionPane.showMessageDialog(this,
                        "Booking berhasil!\n\nLapangan: " + lapItem.lapangan.getNama()
                        + "\nTanggal: " + tanggal + " | " + jamMulai + " - " + jamSelesai
                        + "\nTotal: Rp " + String.format("%,.0f", total),
                        "Booking Berhasil", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                mainFrame.refreshAllPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal membuat booking!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private void resetForm() {
        txtNamaPemesan.setText("");
        txtNoTelepon.setText("");
        cmbJenis.setSelectedIndex(0);
        cmbLapangan.removeAllItems();
        cmbLapangan.setEnabled(false);
        cmbTanggal.setSelectedIndex(0);
        cmbJamMulai.setSelectedIndex(0);
        cmbDurasi.setSelectedIndex(0);
        lblJamSelesai.setText("-");
        lblHargaPerJam.setText("-");
        lblTotalHarga.setText("Rp 0");
    }

    public void refreshData() {
        // Refresh jenis selection if needed
        int selectedIndex = cmbJenis.getSelectedIndex();
        if (selectedIndex > 0) {
            onJenisChanged();
        }
    }

    // Helper class for combo box items
    private static class LapanganItem {
        Lapangan lapangan;
        LapanganItem(Lapangan lapangan) {
            this.lapangan = lapangan;
        }
        @Override
        public String toString() {
            return lapangan.getNama() + " - Rp " + String.format("%,.0f", lapangan.getHargaPerJam()) + "/jam";
        }
    }
}

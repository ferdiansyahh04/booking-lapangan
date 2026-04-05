package sportbooking.ui;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Lapangan;
import sportbooking.model.Reservasi;
import sportbooking.model.User;

public class BookingDialog extends JDialog {

    private final Lapangan lapangan;
    private final User currentUser;

    private JTextField txtNama, txtNoTelepon, txtTanggal;
    private JComboBox<String> cmbJamMulai, cmbDurasi;
    private JLabel lblHargaPerJam, lblTotalHarga, lblNamaLapangan, lblJenis, lblStatus;
    private JButton btnBooking, btnBatal;

    private static final String[] JAM_OPTIONS = {
        "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00", "22:00"
    };
    private static final String[] DURASI_OPTIONS = {"1 Jam", "2 Jam", "3 Jam", "4 Jam", "5 Jam"};

    public BookingDialog(Window parent, Lapangan lapangan, User currentUser) {
        super(parent, "Booking Lapangan", ModalityType.APPLICATION_MODAL);
        this.lapangan = lapangan;
        this.currentUser = currentUser;
        setSize(520, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // ── Header info lapangan ──
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBackground(new Color(33, 97, 140));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        lblNamaLapangan = makeInfoLabel(lapangan.getNama(), true);
        lblJenis        = makeInfoLabel("Jenis: " + lapangan.getJenis(), false);
        lblStatus       = makeInfoLabel("Status: " + lapangan.getStatus(), false);
        lblHargaPerJam  = makeInfoLabel("Harga: " + formatRupiah(lapangan.getHargaPerJam()) + " / jam", false);

        infoPanel.add(lblNamaLapangan);
        infoPanel.add(new JLabel()); // spacer
        infoPanel.add(lblJenis);
        infoPanel.add(lblHargaPerJam);
        infoPanel.add(lblStatus);
        infoPanel.add(new JLabel());

        // ── Form ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Nama pemesan — pre-filled dari user login
        txtNama = new JTextField(currentUser != null ? currentUser.getNamaLengkap() : "", 20);
        txtNama.setFont(fieldFont);

        // No telepon
        txtNoTelepon = new JTextField(20);
        txtNoTelepon.setFont(fieldFont);

        // Tanggal — default hari ini
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtTanggal = new JTextField(today, 20);
        txtTanggal.setFont(fieldFont);
        txtTanggal.setToolTipText("Format: yyyy-MM-dd, contoh: " + today);

        // Jam mulai
        cmbJamMulai = new JComboBox<>(JAM_OPTIONS);
        cmbJamMulai.setFont(fieldFont);

        // Durasi
        cmbDurasi = new JComboBox<>(DURASI_OPTIONS);
        cmbDurasi.setFont(fieldFont);

        // Total harga label
        lblTotalHarga = new JLabel(formatRupiah(lapangan.getHargaPerJam()), SwingConstants.LEFT);
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalHarga.setForeground(new Color(39, 174, 96));

        // Update total saat durasi berubah
        cmbDurasi.addActionListener(e -> updateTotalHarga());
        cmbJamMulai.addActionListener(e -> updateTotalHarga());

        addRow(formPanel, gbc, 0, "Nama Pemesan *", txtNama, labelFont);
        addRow(formPanel, gbc, 1, "No. Telepon *", txtNoTelepon, labelFont);
        addRow(formPanel, gbc, 2, "Tanggal (yyyy-MM-dd) *", txtTanggal, labelFont);
        addRow(formPanel, gbc, 3, "Jam Mulai *", cmbJamMulai, labelFont);
        addRow(formPanel, gbc, 4, "Durasi *", cmbDurasi, labelFont);
        addRow(formPanel, gbc, 5, "Total Harga", lblTotalHarga, labelFont);

        // ── Tombol ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);

        btnBooking = createButton("Booking Sekarang", new Color(39, 174, 96));
        btnBatal   = createButton("Batal", new Color(149, 165, 166));

        btnPanel.add(btnBooking);
        btnPanel.add(btnBatal);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        btnBooking.addActionListener(e -> doBooking());
        btnBatal.addActionListener(e -> dispose());

        updateTotalHarga();
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field, Font labelFont) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(labelFont);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JLabel makeInfoLabel(String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, bold ? 15 : 13));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateTotalHarga() {
        int durasi = cmbDurasi.getSelectedIndex() + 1;
        double total = lapangan.getHargaPerJam() * durasi;
        lblTotalHarga.setText(formatRupiah(total));
    }

    private void doBooking() {
        String nama      = txtNama.getText().trim();
        String noTelp    = txtNoTelepon.getText().trim();
        String tanggal   = txtTanggal.getText().trim();
        String jamMulai  = (String) cmbJamMulai.getSelectedItem();
        int durasi       = cmbDurasi.getSelectedIndex() + 1;

        // Validasi input
        if (nama.isEmpty() || noTelp.isEmpty() || tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field bertanda * harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi format tanggal
        if (!tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd\nContoh: 2025-07-15", "Format Salah", JOptionPane.WARNING_MESSAGE);
            txtTanggal.requestFocus();
            return;
        }

        // Validasi tanggal tidak di masa lalu
        try {
            LocalDate tgl = LocalDate.parse(tanggal);
            if (tgl.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh di masa lalu!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtTanggal.requestFocus();
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak valid!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hitung jam selesai
        int jamMulaiInt  = Integer.parseInt(jamMulai.replace(":00", ""));
        int jamSelesaiInt = jamMulaiInt + durasi;
        if (jamSelesaiInt > 23) {
            JOptionPane.showMessageDialog(this, "Jam selesai melebihi batas waktu operasional (max 23:00)!\nKurangi durasi atau pilih jam mulai lebih awal.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String jamSelesai = String.format("%02d:00", jamSelesaiInt);

        // Cek ketersediaan
        boolean sudahDibooking = DatabaseHelper.getInstance()
                .isLapanganBooked(lapangan.getId(), tanggal, jamMulai, jamSelesai);
        if (sudahDibooking) {
            JOptionPane.showMessageDialog(this,
                    "Lapangan sudah dibooking pada waktu tersebut!\nSilakan pilih jam atau tanggal lain.",
                    "Tidak Tersedia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalHarga = lapangan.getHargaPerJam() * durasi;

        // Konfirmasi booking
        String konfirmasi = String.format(
                "Konfirmasi Booking:\n\n" +
                "Lapangan  : %s\n" +
                "Tanggal   : %s\n" +
                "Jam       : %s - %s (%d jam)\n" +
                "Pemesan   : %s\n" +
                "Total     : %s\n\n" +
                "Lanjutkan booking?",
                lapangan.getNama(), tanggal, jamMulai, jamSelesai, durasi,
                nama, formatRupiah(totalHarga)
        );

        int confirm = JOptionPane.showConfirmDialog(this, konfirmasi, "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Simpan reservasi
        Reservasi res = new Reservasi();
        res.setNamaPemesan(nama);
        res.setNoTelepon(noTelp);
        res.setLapanganId(lapangan.getId());
        res.setTanggal(tanggal);
        res.setJamMulai(jamMulai);
        res.setJamSelesai(jamSelesai);
        res.setDurasiJam(durasi);
        res.setTotalHarga(totalHarga);
        res.setStatus("Aktif");

        boolean success = DatabaseHelper.getInstance().insertReservasi(res);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Booking berhasil!\n\n" +
                    lapangan.getNama() + "\n" +
                    tanggal + " | " + jamMulai + " - " + jamSelesai + "\n" +
                    "Total: " + formatRupiah(totalHarga) + "\n\n" +
                    "Silakan lakukan pembayaran di kasir.",
                    "Booking Berhasil", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Booking gagal! Silakan coba lagi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(amount).replace(",00", "");
    }
}

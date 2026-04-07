package sportbooking.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Lapangan;
import sportbooking.model.Reservasi;
import sportbooking.model.User;

public class BookingDialog extends JDialog {

    private static final Color BTN_PRIMARY = new Color(25, 135, 84);
    private static final Color BTN_PRIMARY_HOVER = new Color(20, 108, 67);
    private static final Color BTN_SECONDARY = new Color(108, 117, 125);
    private static final Color BTN_SECONDARY_HOVER = new Color(84, 91, 98);

    private final Lapangan lapangan;
    private final User currentUser;

    private JTextField txtNama;
    private JTextField txtNoTelepon;
    private JTextField txtTanggal;
    private JComboBox<String> cmbJamMulai;
    private JComboBox<String> cmbDurasi;
    private JLabel lblHargaPerJam;
    private JLabel lblTotalHarga;
    private JLabel lblNamaLapangan;
    private JLabel lblJenis;
    private JLabel lblStatus;
    private JButton btnBooking;
    private JButton btnBatal;

    private static final String[] JAM_OPTIONS = {
        "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
        "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
        "18:00", "19:00", "20:00", "21:00", "22:00"
    };

    private static final String[] DURASI_OPTIONS = {
        "1 Jam", "2 Jam", "3 Jam", "4 Jam", "5 Jam"
    };

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

        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setBackground(new Color(33, 97, 140));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        lblNamaLapangan = makeInfoLabel(lapangan.getNama(), true);
        lblJenis = makeInfoLabel("Jenis: " + lapangan.getJenis(), false);
        lblStatus = makeInfoLabel("Status: " + lapangan.getStatus(), false);
        lblHargaPerJam = makeInfoLabel("Harga: " + formatRupiah(lapangan.getHargaPerJam()) + " / jam", false);

        infoPanel.add(lblNamaLapangan);
        infoPanel.add(new JLabel());
        infoPanel.add(lblJenis);
        infoPanel.add(lblHargaPerJam);
        infoPanel.add(lblStatus);
        infoPanel.add(new JLabel());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        txtNama = new JTextField(currentUser != null ? currentUser.getNamaLengkap() : "", 20);
        txtNama.setFont(fieldFont);

        txtNoTelepon = new JTextField(20);
        txtNoTelepon.setFont(fieldFont);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtTanggal = new JTextField(today, 20);
        txtTanggal.setFont(fieldFont);
        txtTanggal.setToolTipText("Format: yyyy-MM-dd, contoh: " + today);

        cmbJamMulai = new JComboBox<>(JAM_OPTIONS);
        cmbJamMulai.setFont(fieldFont);

        cmbDurasi = new JComboBox<>(DURASI_OPTIONS);
        cmbDurasi.setFont(fieldFont);

        lblTotalHarga = new JLabel(formatRupiah(lapangan.getHargaPerJam()), SwingConstants.LEFT);
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalHarga.setForeground(new Color(39, 174, 96));

        cmbDurasi.addActionListener(e -> updateTotalHarga());
        cmbJamMulai.addActionListener(e -> updateTotalHarga());

        addRow(formPanel, gbc, 0, "Nama Pemesan *", txtNama, labelFont);
        addRow(formPanel, gbc, 1, "No. Telepon *", txtNoTelepon, labelFont);
        addRow(formPanel, gbc, 2, "Tanggal (yyyy-MM-dd) *", txtTanggal, labelFont);
        addRow(formPanel, gbc, 3, "Jam Mulai *", cmbJamMulai, labelFont);
        addRow(formPanel, gbc, 4, "Durasi *", cmbDurasi, labelFont);
        addRow(formPanel, gbc, 5, "Total Harga", lblTotalHarga, labelFont);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(4, 16, 16, 16));

        btnBooking = createButton("Booking Sekarang", BTN_PRIMARY, BTN_PRIMARY_HOVER, new Dimension(190, 42), 14);
        btnBatal = createButton("Batal", BTN_SECONDARY, BTN_SECONDARY_HOVER, new Dimension(130, 42), 13);

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
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(labelFont);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JLabel makeInfoLabel(String text, boolean bold) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, bold ? 15 : 13));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private RoundedButton createButton(String text, Color bg, Color hoverBg, Dimension size, int fontSize) {
        RoundedButton btn = new RoundedButton(text, bg, hoverBg, 18);
        btn.setButtonSize(size);
        btn.setButtonFontSize(fontSize);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setHover(true);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setHover(false);
            }
        });
        return btn;
    }

    private void updateTotalHarga() {
        int durasi = cmbDurasi.getSelectedIndex() + 1;
        double total = lapangan.getHargaPerJam() * durasi;
        lblTotalHarga.setText(formatRupiah(total));
    }

    private void doBooking() {
        String nama = txtNama.getText().trim();
        String noTelp = txtNoTelepon.getText().trim();
        String tanggal = txtTanggal.getText().trim();
        String jamMulai = (String) cmbJamMulai.getSelectedItem();
        int durasi = cmbDurasi.getSelectedIndex() + 1;

        if (nama.isEmpty() || noTelp.isEmpty() || tanggal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field bertanda * harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd\nContoh: 2025-07-15", "Format Salah", JOptionPane.WARNING_MESSAGE);
            txtTanggal.requestFocus();
            return;
        }

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

        int jamMulaiInt = Integer.parseInt(jamMulai.replace(":00", ""));
        int jamSelesaiInt = jamMulaiInt + durasi;
        if (jamSelesaiInt > 23) {
            JOptionPane.showMessageDialog(this,
                    "Jam selesai melebihi batas waktu operasional (max 23:00)!\nKurangi durasi atau pilih jam mulai lebih awal.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String jamSelesai = String.format("%02d:00", jamSelesaiInt);

        boolean sudahDibooking = DatabaseHelper.getInstance()
                .isLapanganBooked(lapangan.getId(), tanggal, jamMulai, jamSelesai);
        if (sudahDibooking) {
            JOptionPane.showMessageDialog(this,
                    "Lapangan sudah dibooking pada waktu tersebut!\nSilakan pilih jam atau tanggal lain.",
                    "Tidak Tersedia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalHarga = lapangan.getHargaPerJam() * durasi;

        String konfirmasi = String.format(
                "Konfirmasi Booking:\n\n"
                + "Lapangan  : %s\n"
                + "Tanggal   : %s\n"
                + "Jam       : %s - %s (%d jam)\n"
                + "Pemesan   : %s\n"
                + "Total     : %s\n\n"
                + "Lanjutkan booking?",
                lapangan.getNama(), tanggal, jamMulai, jamSelesai, durasi,
                nama, formatRupiah(totalHarga)
        );

        int confirm = JOptionPane.showConfirmDialog(this, konfirmasi, "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

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
                    "Booking berhasil!\n\n"
                    + lapangan.getNama() + "\n"
                    + tanggal + " | " + jamMulai + " - " + jamSelesai + "\n"
                    + "Total: " + formatRupiah(totalHarga) + "\n\n"
                    + "Silakan lakukan pembayaran di kasir.",
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

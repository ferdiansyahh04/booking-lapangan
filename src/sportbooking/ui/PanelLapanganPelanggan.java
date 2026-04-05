package sportbooking.ui;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Lapangan;
import sportbooking.model.User;

public class PanelLapanganPelanggan extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnBooking, btnRefresh;
    private JComboBox<String> cmbFilterJenis;
    private JLabel lblInfo;
    private User currentUser;

    public PanelLapanganPelanggan() {
        this(null);
    }

    public PanelLapanganPelanggan(User currentUser) {
        this.currentUser = currentUser;
        initComponents();
        refreshData();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // ── Top: judul + filter ──
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("Daftar Lapangan Tersedia");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblJudul.setForeground(new Color(33, 97, 140));

        lblInfo = new JLabel("Pilih lapangan lalu klik Booking");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        JPanel judulPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        judulPanel.setOpaque(false);
        judulPanel.add(lblJudul);
        judulPanel.add(lblInfo);

        // Filter jenis
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Jenis:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterJenis = new JComboBox<>(new String[]{"Semua", "Futsal", "Basket", "Bulutangkis"});
        cmbFilterJenis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterJenis.setPreferredSize(new Dimension(140, 30));
        cmbFilterJenis.addActionListener(e -> refreshData());

        btnRefresh = createButton("↻ Refresh", new Color(41, 128, 185));
        btnRefresh.addActionListener(e -> refreshData());

        filterPanel.add(lblFilter);
        filterPanel.add(cmbFilterJenis);
        filterPanel.add(btnRefresh);

        topPanel.add(judulPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // ── Tabel lapangan ──
        String[] columns = {"ID", "Nama Lapangan", "Jenis", "Harga/Jam", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // Header
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Kolom lebar
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(230);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Center renderer untuk ID dan Status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        // Highlight baris saat dipilih
        table.setSelectionBackground(new Color(214, 234, 248));
        table.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // ── Bottom: tombol booking ──
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 248, 250));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnBooking = createButton("🏟  Booking Lapangan Ini", new Color(39, 174, 96));
        btnBooking.setEnabled(false);
        btnBooking.setPreferredSize(new Dimension(220, 42));
        btnBooking.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblHint = new JLabel("* Pilih baris lapangan terlebih dahulu");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(130, 130, 130));

        bottomPanel.add(lblHint, BorderLayout.WEST);
        bottomPanel.add(btnBooking, BorderLayout.EAST);

        // Events
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String status = tableModel.getValueAt(row, 4).toString();
                    btnBooking.setEnabled("Tersedia".equalsIgnoreCase(status));
                    lblInfo.setText(row >= 0
                            ? "Lapangan dipilih: " + tableModel.getValueAt(row, 1)
                            : "Pilih lapangan lalu klik Booking");
                }
            }
        });

        btnBooking.addActionListener(e -> openBookingDialog());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void openBookingDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int lapanganId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        Lapangan lapangan = DatabaseHelper.getInstance().getLapanganById(lapanganId);

        if (lapangan == null) {
            JOptionPane.showMessageDialog(this, "Data lapangan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        BookingDialog dialog = new BookingDialog(parentWindow, lapangan, currentUser);
        dialog.setVisible(true);

        // Refresh tabel setelah dialog ditutup
        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        String filterJenis = (String) cmbFilterJenis.getSelectedItem();

        List<Lapangan> list;
        if ("Semua".equals(filterJenis)) {
            list = DatabaseHelper.getInstance().getAllLapangan();
        } else {
            list = DatabaseHelper.getInstance().getLapanganByJenis(filterJenis);
        }

        int tersedia = 0;
        for (Lapangan lap : list) {
            tableModel.addRow(new Object[]{
                lap.getId(),
                lap.getNama(),
                lap.getJenis(),
                formatRupiah(lap.getHargaPerJam()),
                lap.getStatus()
            });
            if ("Tersedia".equalsIgnoreCase(lap.getStatus())) tersedia++;
        }

        lblInfo.setText(String.format("Total: %d lapangan | %d tersedia | Pilih lapangan lalu klik Booking",
                list.size(), tersedia));

        btnBooking.setEnabled(false);
        table.clearSelection();
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return nf.format(amount).replace(",00", "");
    }

    // ── Renderer warna status ──
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        public StatusCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value != null ? value.toString() : "";
            if (!isSelected) {
                if ("Tersedia".equalsIgnoreCase(status)) {
                    setBackground(new Color(212, 247, 223));
                    setForeground(new Color(30, 130, 76));
                } else {
                    setBackground(new Color(253, 228, 207));
                    setForeground(new Color(180, 80, 20));
                }
            }
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }
}

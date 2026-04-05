package sportbooking.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Reservasi;

public class PanelRiwayat extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilter;

    public PanelRiwayat(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // === Top Panel (Filter + Buttons) ===
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cmbFilter = new JComboBox<>(new String[]{"Semua", "Aktif", "Selesai", "Dibatalkan"});
        cmbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilter.setPreferredSize(new Dimension(150, 30));

        filterPanel.add(lblFilter);
        filterPanel.add(cmbFilter);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnSelesai = createButton("Tandai Selesai", new Color(39, 174, 96));
        JButton btnBatalkan = createButton("Batalkan", new Color(243, 156, 18));
        JButton btnHapus = createButton("Hapus", new Color(231, 76, 60));
        JButton btnRefresh = createButton("Refresh", new Color(41, 128, 185));

        actionPanel.add(btnSelesai);
        actionPanel.add(btnBatalkan);
        actionPanel.add(btnHapus);
        actionPanel.add(btnRefresh);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        // === Table ===
        String[] columns = {"ID", "Nama Pemesan", "No. Telepon", "Lapangan", "Jenis",
                "Tanggal", "Jam", "Durasi", "Total Harga", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(33, 97, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(214, 234, 248));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(7).setMaxWidth(60);

        // Center align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // Status column with color
        table.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String status = value != null ? value.toString() : "";
                    switch (status) {
                        case "Aktif":
                            setBackground(new Color(212, 239, 223));
                            setForeground(new Color(30, 130, 76));
                            break;
                        case "Selesai":
                            setBackground(new Color(214, 234, 248));
                            setForeground(new Color(41, 128, 185));
                            break;
                        case "Dibatalkan":
                            setBackground(new Color(250, 219, 216));
                            setForeground(new Color(192, 57, 43));
                            break;
                        default:
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // === Summary Panel ===
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Events
        cmbFilter.addActionListener(e -> refreshData());
        btnSelesai.addActionListener(e -> updateStatus("Selesai"));
        btnBatalkan.addActionListener(e -> updateStatus("Dibatalkan"));
        btnHapus.addActionListener(e -> hapusReservasi());
        btnRefresh.addActionListener(e -> refreshData());

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = tableModel.getValueAt(row, 9).toString();
        if (!currentStatus.equals("Aktif")) {
            JOptionPane.showMessageDialog(this,
                    "Hanya reservasi dengan status 'Aktif' yang dapat diubah!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String nama = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Ubah status reservasi " + nama + " menjadi '" + newStatus + "'?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().updateStatusReservasi(id, newStatus)) {
                JOptionPane.showMessageDialog(this,
                        "Status berhasil diubah menjadi '" + newStatus + "'!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                mainFrame.refreshAllPanels();
            }
        }
    }

    private void hapusReservasi() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih reservasi terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String nama = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus reservasi atas nama '" + nama + "'?\nData yang dihapus tidak dapat dikembalikan.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseHelper.getInstance().deleteReservasi(id)) {
                JOptionPane.showMessageDialog(this, "Reservasi berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                mainFrame.refreshAllPanels();
            }
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        DatabaseHelper db = DatabaseHelper.getInstance();
        List<Reservasi> list;

        String filter = (String) cmbFilter.getSelectedItem();
        if (filter != null && !filter.equals("Semua")) {
            list = db.getReservasiByStatus(filter);
        } else {
            list = db.getAllReservasi();
        }

        for (Reservasi res : list) {
            tableModel.addRow(new Object[]{
                    res.getId(),
                    res.getNamaPemesan(),
                    res.getNoTelepon(),
                    res.getNamaLapangan(),
                    res.getJenisLapangan(),
                    res.getTanggal(),
                    res.getJamMulai() + " - " + res.getJamSelesai(),
                    res.getDurasiJam() + " jam",
                    "Rp " + String.format("%,.0f", res.getTotalHarga()),
                    res.getStatus()
            });
        }
    }
}

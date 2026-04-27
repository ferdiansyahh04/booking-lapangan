package sportbooking.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.Reservasi;

public class PanelJadwal extends JPanel {

    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbTanggal;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PanelJadwal(MainFrame mainFrame) {
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

        JLabel lblFilter = new JLabel("Pilih Tanggal:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cmbTanggal = new JComboBox<>();
        cmbTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        populateTanggal();

        filterPanel.add(lblFilter);
        filterPanel.add(cmbTanggal);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Cari:"));
        txtSearch = ReportTableHelper.createSearchField();
        searchPanel.add(txtSearch);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // === Table ===
        String[] columns = {"No", "Nama Lapangan", "Jenis", "Tanggal", "Jam Mulai", "Jam Selesai", "Durasi", "Pemesan", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(80);

        // Center align certain columns
        DefaultTableCellRenderer centerRenderer = TableStyler.createCenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Status column with color
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    String status = value != null ? value.toString() : "";
                    switch (status) {
                        case "Menunggu":
                            setBackground(new Color(252, 243, 207));
                            setForeground(new Color(211, 84, 0));
                            break;
                        case "Selesai":
                            setBackground(new Color(212, 239, 223));
                            setForeground(new Color(39, 174, 96));
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

        rowSorter = ReportTableHelper.installSearchFilter(tableModel, txtSearch, 1, 2, 7, 8);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        TableStyler.styleTable(table, scrollPane, 30);

        // Events
        cmbTanggal.addActionListener(e -> tampilkanData());

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateTanggal() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7); // Mulai dari 7 hari lalu

        cmbTanggal.addItem("Semua Tanggal");
        for (int i = 0; i < 30; i++) { // Sampai 30 hari ke depan
            cmbTanggal.addItem(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cmbTanggal.setSelectedIndex(0); // Pilih "Semua Tanggal"
    }

    private void tampilkanData() {
        tableModel.setRowCount(0);
        String filterTanggal = (String) cmbTanggal.getSelectedItem();
        
        List<Reservasi> list;
        if (filterTanggal == null || filterTanggal.equals("Semua Tanggal")) {
            list = DatabaseHelper.getInstance().getAllReservasi();
        } else {
            // Kita bisa menggunakan getReservasiByPeriode karena sudah ada di DatabaseHelper
            list = DatabaseHelper.getInstance().getReservasiByPeriode(filterTanggal, filterTanggal);
        }

        int no = 1;
        for (Reservasi r : list) {
            tableModel.addRow(new Object[]{
                no++,
                r.getNamaLapangan(),
                r.getJenisLapangan(),
                r.getTanggal(),
                r.getJamMulai(),
                r.getJamSelesai(),
                r.getDurasiJam() + " jam",
                r.getNamaPemesan(),
                r.getStatus()
            });
        }
    }

    public void refreshData() {
        tampilkanData();
    }
}

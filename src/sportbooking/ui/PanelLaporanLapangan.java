package sportbooking.ui;

import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import sportbooking.database.DatabaseHelper;

public class PanelLaporanLapangan extends JPanel {

    private JComboBox<String> cmbDari, cmbSampai;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PanelLaporanLapangan() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 248, 250));

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        " Laporan Penggunaan Lapangan ",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(44, 62, 80)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        cmbDari = new JComboBox<>();
        cmbSampai = new JComboBox<>();
        DateComboBoxStyler.style(cmbDari);
        DateComboBoxStyler.style(cmbSampai);
        populateDates(cmbDari);
        populateDates(cmbSampai);
        cmbSampai.setSelectedIndex(cmbSampai.getItemCount() - 1);

        JButton btnTampilkan = createButton("Tampilkan", new Color(41, 128, 185));
        JButton btnCetak = createButton("Cetak", new Color(142, 68, 173));
        JButton btnPdf = createButton("Export PDF", new Color(192, 57, 43));
        JButton btnExcel = createButton("Export Excel", new Color(39, 174, 96));
        JButton btnJasperPdf = createButton("Jasper Report", new Color(230, 126, 34));
        txtSearch = ReportTableHelper.createSearchField();

        filterPanel.add(new JLabel("Dari:"));
        filterPanel.add(cmbDari);
        filterPanel.add(new JLabel("Sampai:"));
        filterPanel.add(cmbSampai);
        filterPanel.add(btnTampilkan);
        filterPanel.add(btnCetak);
        filterPanel.add(btnPdf);
        filterPanel.add(btnExcel);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(btnJasperPdf);
        filterPanel.add(new JLabel("Cari:"));
        filterPanel.add(txtSearch);

        // Table
        String[] columns = {"No", "Nama Lapangan", "Jenis", "Status", "Total Booking", "Total Jam Pakai"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        DefaultTableCellRenderer centerRenderer = TableStyler.createCenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        rowSorter = ReportTableHelper.installSearchFilter(tableModel, txtSearch, 1, 2, 3);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        TableStyler.styleTable(table, scrollPane, 30);

        // Summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        lblTotal = new JLabel("Total: 0 lapangan");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(new Color(44, 62, 80));
        summaryPanel.add(lblTotal);

        // Events
        btnTampilkan.addActionListener(e -> tampilkanData());
        btnCetak.addActionListener(e -> cetakLaporan());
        btnPdf.addActionListener(e -> ReportExporter.exportTableToPdf(this, table, "Laporan Penggunaan Lapangan"));
        btnExcel.addActionListener(e -> ReportExporter.exportTableToExcel(this, table, "Laporan Penggunaan Lapangan"));

        // JasperReports Events
        btnJasperPdf.addActionListener(e -> exportJasperPdf());

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void populateDates(JComboBox<String> cmb) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i <= 60; i++) {
            Calendar c = (Calendar) cal.clone();
            c.add(Calendar.DAY_OF_MONTH, -30 + i);
            cmb.addItem(sdf.format(c.getTime()));
        }
        cmb.setSelectedIndex(0);
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

    private void tampilkanData() {
        String dari = (String) cmbDari.getSelectedItem();
        String sampai = (String) cmbSampai.getSelectedItem();
        if (dari == null || sampai == null) return;

        tableModel.setRowCount(0);
        List<Object[]> list = DatabaseHelper.getInstance().getPenggunaanLapangan(dari, sampai);
        int totalBooking = 0;
        int totalJam = 0;
        int no = 1;
        for (Object[] row : list) {
            int booking = (Integer) row[3];
            int jam = (Integer) row[4];
            tableModel.addRow(new Object[]{
                no++, row[0], row[1], row[2], booking, jam + " jam"
            });
            totalBooking += booking;
            totalJam += jam;
        }
        lblTotal.setText("Total: " + list.size() + " lapangan | " + totalBooking + " booking | " + totalJam + " jam terpakai");
    }

    private void cetakLaporan() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("Laporan Penggunaan Lapangan - Sport Booking System"),
                    new java.text.MessageFormat("Halaman {0}"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak laporan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        tampilkanData();
    }

    // ==================== JasperReports Methods ====================

    private void exportJasperPdf() {
        try {
            String dari = (String) cmbDari.getSelectedItem();
            String sampai = (String) cmbSampai.getSelectedItem();
            if (dari == null || sampai == null) {
                JOptionPane.showMessageDialog(this, "Pilih periode terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            System.out.println("=== Jasper Report Export ===");
            System.out.println("Dari: " + dari);
            System.out.println("Sampai: " + sampai);

            // Check template first
            if (!JasperReportHelper.reportExists("laporan_lapangan.jrxml")) {
                JOptionPane.showMessageDialog(this,
                    "Template laporan tidak ditemukan!\n" +
                    "Letakkan file 'laporan_lapangan.jrxml' di folder 'reports/'",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection conn = DatabaseHelper.getInstance().getConnection();
            Map<String, Object> params = JasperReportHelper.createParams(
                "Laporan Penggunaan Lapangan", dari, sampai);

            // Print params for debug
            System.out.println("Params: " + params);

            JasperReportHelper.exportToPdf("laporan_lapangan.jrxml", params, conn, this,
                "Laporan_Lapangan");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Gagal export laporan:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

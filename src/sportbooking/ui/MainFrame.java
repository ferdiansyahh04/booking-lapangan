package sportbooking.ui;

import java.awt.*;
import javax.swing.*;
import sportbooking.database.DatabaseHelper;
import sportbooking.model.User;
import sportbooking.ui.PanelLapanganPelanggan;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel lblUserInfo;

    private PanelDashboard panelDashboard;
    private PanelReservasi panelReservasi;
    private PanelLapangan panelLapangan;
    private PanelRiwayat panelRiwayat;
    private PanelPelanggan panelPelanggan;
    private PanelUser panelUser;
    private PanelPembayaran panelPembayaran;
    private PanelLaporanReservasi panelLaporanReservasi;
    private PanelLaporanPendapatan panelLaporanPendapatan;
    private PanelLaporanPelanggan panelLaporanPelanggan;
    private PanelLaporanLapangan panelLaporanLapangan;
    private PanelLapanganPelanggan panelLapanganPelanggan;

    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Sport Booking - Reservasi Lapangan Olahraga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(950, 650));

        // Initialize database
        DatabaseHelper.getInstance();

        // Menu Bar
        setJMenuBar(createMenuBar());

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Content with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        panelDashboard = new PanelDashboard();
        panelReservasi = new PanelReservasi(this);
        panelLapangan = new PanelLapangan(this);
        panelRiwayat = new PanelRiwayat(this);
        panelPelanggan = new PanelPelanggan(this);
        panelUser = new PanelUser(this);
        panelPembayaran = new PanelPembayaran(this);
        panelLaporanReservasi = new PanelLaporanReservasi();
        panelLaporanPendapatan = new PanelLaporanPendapatan();
        panelLaporanPelanggan = new PanelLaporanPelanggan();
        panelLaporanLapangan = new PanelLaporanLapangan();
        panelLapanganPelanggan = new PanelLapanganPelanggan(currentUser);

        if (currentUser != null && "Pelanggan".equalsIgnoreCase(currentUser.getRole())) {
            contentPanel.add(panelLapanganPelanggan, "lapangan_pelanggan");
        } else {
            contentPanel.add(panelDashboard, "dashboard");
            contentPanel.add(panelReservasi, "reservasi");
            contentPanel.add(panelLapangan, "lapangan");
            contentPanel.add(panelRiwayat, "riwayat");
            contentPanel.add(panelPelanggan, "pelanggan");
            contentPanel.add(panelUser, "user");
            contentPanel.add(panelPembayaran, "pembayaran");
            contentPanel.add(panelLaporanReservasi, "lap_reservasi");
            contentPanel.add(panelLaporanPendapatan, "lap_pendapatan");
            contentPanel.add(panelLaporanPelanggan, "lap_pelanggan");
            contentPanel.add(panelLaporanLapangan, "lap_lapangan");
        }

        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 248, 250));
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        Font menuFont = new Font("Segoe UI", Font.BOLD, 14);
        Font itemFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color menuColor = new Color(44, 62, 80);

        // Dashboard
        JMenu menuDashboard = new JMenu("Dashboard");
        menuDashboard.setFont(menuFont);
        menuDashboard.setForeground(menuColor);
        JMenuItem miDashboard = new JMenuItem("Dashboard");
        miDashboard.setFont(itemFont);
        miDashboard.addActionListener(e -> showPanel("dashboard"));
        menuDashboard.add(miDashboard);

        // Master
        JMenu menuMaster = new JMenu("Master");
        menuMaster.setFont(menuFont);
        menuMaster.setForeground(menuColor);

        JMenuItem miLapangan = new JMenuItem("Data Lapangan");
        miLapangan.setFont(itemFont);
        miLapangan.addActionListener(e -> { panelLapangan.refreshData(); showPanel("lapangan"); });

        JMenuItem miPelanggan = new JMenuItem("Data Pelanggan");
        miPelanggan.setFont(itemFont);
        miPelanggan.addActionListener(e -> { panelPelanggan.refreshData(); showPanel("pelanggan"); });

        JMenuItem miUser = new JMenuItem("Data User");
        miUser.setFont(itemFont);
        miUser.addActionListener(e -> { panelUser.refreshData(); showPanel("user"); });

        menuMaster.add(miLapangan);
        menuMaster.add(miPelanggan);
        menuMaster.add(miUser);

        // Transaksi
        JMenu menuTransaksi = new JMenu("Transaksi");
        menuTransaksi.setFont(menuFont);
        menuTransaksi.setForeground(menuColor);

        JMenuItem miReservasi = new JMenuItem("Reservasi Baru");
        miReservasi.setFont(itemFont);
        miReservasi.addActionListener(e -> { panelReservasi.refreshData(); showPanel("reservasi"); });

        JMenuItem miRiwayat = new JMenuItem("Riwayat Booking");
        miRiwayat.setFont(itemFont);
        miRiwayat.addActionListener(e -> { panelRiwayat.refreshData(); showPanel("riwayat"); });

        JMenuItem miPembayaran = new JMenuItem("Pembayaran");
        miPembayaran.setFont(itemFont);
        miPembayaran.addActionListener(e -> { panelPembayaran.refreshData(); showPanel("pembayaran"); });

        menuTransaksi.add(miReservasi);
        menuTransaksi.add(miRiwayat);
        menuTransaksi.add(miPembayaran);

        // Laporan
        JMenu menuLaporan = new JMenu("Laporan");
        menuLaporan.setFont(menuFont);
        menuLaporan.setForeground(menuColor);

        JMenuItem miLapReservasi = new JMenuItem("Laporan Reservasi");
        miLapReservasi.setFont(itemFont);
        miLapReservasi.addActionListener(e -> showPanel("lap_reservasi"));

        JMenuItem miLapPendapatan = new JMenuItem("Laporan Pendapatan");
        miLapPendapatan.setFont(itemFont);
        miLapPendapatan.addActionListener(e -> showPanel("lap_pendapatan"));

        JMenuItem miLapPelanggan = new JMenuItem("Laporan Pelanggan");
        miLapPelanggan.setFont(itemFont);
        miLapPelanggan.addActionListener(e -> showPanel("lap_pelanggan"));

        JMenuItem miLapLapangan = new JMenuItem("Laporan Penggunaan Lapangan");
        miLapLapangan.setFont(itemFont);
        miLapLapangan.addActionListener(e -> showPanel("lap_lapangan"));

        menuLaporan.add(miLapReservasi);
        menuLaporan.add(miLapPendapatan);
        menuLaporan.add(miLapPelanggan);
        menuLaporan.add(miLapLapangan);

        // Logout
        JMenu menuFile = new JMenu("Logout");
        menuFile.setFont(menuFont);
        menuFile.setForeground(menuColor);

        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.setFont(itemFont);
        miLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                sportbooking.Main.showLogin();
            }
        });


        menuFile.add(miLogout);

        if (currentUser != null && "Pelanggan".equalsIgnoreCase(currentUser.getRole())) {
            JMenu menuLapangan = new JMenu("Lapangan");
            menuLapangan.setFont(menuFont);
            menuLapangan.setForeground(menuColor);
            JMenuItem miLapanganReady = new JMenuItem("Lihat Lapangan Ready");
            miLapanganReady.setFont(itemFont);
            miLapanganReady.addActionListener(e -> { panelLapanganPelanggan.refreshData(); showPanel("lapangan_pelanggan"); });
            menuLapangan.add(miLapanganReady);
            menuBar.add(menuLapangan);
            menuBar.add(menuFile);
        } else {
            menuBar.add(menuDashboard);
            menuBar.add(menuMaster);
            menuBar.add(menuTransaksi);
            menuBar.add(menuLaporan);
            menuBar.add(menuFile);
        }

        return menuBar;
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 97, 140));
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("SPORT BOOKING SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Reservasi Lapangan Futsal, Basket & Bulutangkis");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(214, 234, 248));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        // User info
        lblUserInfo = new JLabel("Login: " + (currentUser != null ? currentUser.getNamaLengkap() + " (" + currentUser.getRole() + ")" : "-"));
        lblUserInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUserInfo.setForeground(new Color(214, 234, 248));

        panel.add(textPanel, BorderLayout.WEST);
        panel.add(lblUserInfo, BorderLayout.EAST);
        return panel;
    }

    public void refreshAllPanels() {
        panelDashboard.refreshData();
        panelReservasi.refreshData();
        panelLapangan.refreshData();
        panelRiwayat.refreshData();
        panelPembayaran.refreshData();
    }

    public void switchToTab(int index) {
        // Kept for backward compatibility
        switch (index) {
            case 0: showPanel("dashboard"); break;
            case 1: showPanel("reservasi"); break;
            case 2: showPanel("lapangan"); break;
            case 3: showPanel("riwayat"); break;
        }
    }
}

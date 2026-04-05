package sportbooking.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import sportbooking.database.DatabaseHelper;

public class PanelDashboard extends JPanel {

    private JLabel lblTotalLapangan;
    private JLabel lblReservasiAktif;
    private JLabel lblTotalPendapatan;

    public PanelDashboard() {
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 248, 250));

        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(245, 248, 250));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("Selamat Datang di Sport Booking System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel("Kelola reservasi lapangan olahraga dengan mudah dan cepat.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(100, 100, 100));
        welcomePanel.add(descLabel, BorderLayout.SOUTH);

        // Stats cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setBackground(new Color(245, 248, 250));

        lblTotalLapangan = new JLabel("0", SwingConstants.CENTER);
        lblReservasiAktif = new JLabel("0", SwingConstants.CENTER);
        lblTotalPendapatan = new JLabel("Rp 0", SwingConstants.CENTER);

        cardsPanel.add(createStatCard("Total Lapangan", lblTotalLapangan, new Color(41, 128, 185)));
        cardsPanel.add(createStatCard("Reservasi Aktif", lblReservasiAktif, new Color(39, 174, 96)));
        cardsPanel.add(createStatCard("Total Pendapatan", lblTotalPendapatan, new Color(142, 68, 173)));

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        infoPanel.setBackground(new Color(245, 248, 250));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        infoPanel.add(createInfoCard("Futsal",
                "Lapangan futsal standar dengan rumput sintetis berkualitas.",
                "Rp 150.000 - 175.000 / jam", new Color(231, 76, 60)));
        infoPanel.add(createInfoCard("Basket",
                "Lapangan basket indoor dengan lantai kayu profesional.",
                "Rp 200.000 / jam", new Color(243, 156, 18)));
        infoPanel.add(createInfoCard("Bulutangkis",
                "Lapangan bulutangkis standar BWF dengan pencahayaan baik.",
                "Rp 75.000 - 100.000 / jam", new Color(26, 188, 156)));

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 248, 250));
        topPanel.add(welcomePanel, BorderLayout.NORTH);
        topPanel.add(cardsPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Color accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(color);
        accentBar.setPreferredSize(new Dimension(0, 4));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(120, 120, 120));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(titleLbl, BorderLayout.SOUTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInfoCard(String sport, String description, String price, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel dot = new JLabel("\u25CF ");
        dot.setForeground(color);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel titleLbl = new JLabel(sport);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(new Color(44, 62, 80));

        headerPanel.add(dot);
        headerPanel.add(titleLbl);

        JLabel descLbl = new JLabel("<html><body style='width:200px'>" + description + "</body></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(new Color(100, 100, 100));

        JLabel priceLbl = new JLabel(price);
        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        priceLbl.setForeground(color);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        card.add(priceLbl, BorderLayout.SOUTH);

        return card;
    }

    public void refreshData() {
        DatabaseHelper db = DatabaseHelper.getInstance();
        lblTotalLapangan.setText(String.valueOf(db.getTotalLapangan()));
        lblReservasiAktif.setText(String.valueOf(db.getTotalReservasiAktif()));
        double pendapatan = db.getTotalPendapatan();
        lblTotalPendapatan.setText("Rp " + String.format("%,.0f", pendapatan));
    }
}

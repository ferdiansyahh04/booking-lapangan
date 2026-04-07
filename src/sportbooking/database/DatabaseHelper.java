package sportbooking.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import sportbooking.model.Lapangan;
import sportbooking.model.Pelanggan;
import sportbooking.model.Pembayaran;
import sportbooking.model.Reservasi;
import sportbooking.model.User;

public class DatabaseHelper {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "sportbooking";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    private static final String JDBC_PARAMS = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8";
    private static final String SERVER_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/?" + JDBC_PARAMS;
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?" + JDBC_PARAMS;
    private static DatabaseHelper instance;

    private DatabaseHelper() {
        initializeDatabase();
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) instance = new DatabaseHelper();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1");
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        } catch (SQLException e) {
            logError("Gagal menguji koneksi MySQL", e);
            return false;
        }
    }

    private void initializeDatabase() {
        createDatabaseIfNotExists();
        createTables();
        insertDefaultLapangan();
        insertDefaultAdmin();
    }

    private void createDatabaseIfNotExists() {
        String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Connection conn = DriverManager.getConnection(SERVER_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logError("Gagal membuat database MySQL", e);
        }
    }

    private void createTables() {
        String sqlLapangan = "CREATE TABLE IF NOT EXISTS lapangan (id INT AUTO_INCREMENT PRIMARY KEY,nama VARCHAR(100) NOT NULL,jenis VARCHAR(50) NOT NULL,harga_per_jam DECIMAL(12,2) NOT NULL,status VARCHAR(30) NOT NULL DEFAULT 'Tersedia') ENGINE=InnoDB";
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY,username VARCHAR(50) NOT NULL UNIQUE,password VARCHAR(255) NOT NULL,nama_lengkap VARCHAR(100) NOT NULL,role VARCHAR(30) NOT NULL DEFAULT 'Staff',created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB";
        String sqlPelanggan = "CREATE TABLE IF NOT EXISTS pelanggan (id INT AUTO_INCREMENT PRIMARY KEY,nama VARCHAR(100) NOT NULL,no_telepon VARCHAR(20),email VARCHAR(100),alamat VARCHAR(255),created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB";
        String sqlReservasi = "CREATE TABLE IF NOT EXISTS reservasi (id INT AUTO_INCREMENT PRIMARY KEY,nama_pemesan VARCHAR(100) NOT NULL,no_telepon VARCHAR(20) NOT NULL,lapangan_id INT NOT NULL,tanggal VARCHAR(10) NOT NULL,jam_mulai VARCHAR(5) NOT NULL,jam_selesai VARCHAR(5) NOT NULL,durasi_jam INT NOT NULL,total_harga DECIMAL(12,2) NOT NULL,status VARCHAR(30) NOT NULL DEFAULT 'Aktif',created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,CONSTRAINT fk_reservasi_lapangan FOREIGN KEY (lapangan_id) REFERENCES lapangan(id) ON UPDATE CASCADE ON DELETE RESTRICT) ENGINE=InnoDB";
        String sqlPembayaran = "CREATE TABLE IF NOT EXISTS pembayaran (id INT AUTO_INCREMENT PRIMARY KEY,reservasi_id INT NOT NULL,jumlah_bayar DECIMAL(12,2) NOT NULL,metode_pembayaran VARCHAR(50) NOT NULL,tanggal_bayar VARCHAR(10) NOT NULL,keterangan VARCHAR(255),created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,CONSTRAINT fk_pembayaran_reservasi FOREIGN KEY (reservasi_id) REFERENCES reservasi(id) ON UPDATE CASCADE ON DELETE RESTRICT) ENGINE=InnoDB";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlLapangan);
            stmt.executeUpdate(sqlUsers);
            stmt.executeUpdate(sqlPelanggan);
            stmt.executeUpdate(sqlReservasi);
            stmt.executeUpdate(sqlPembayaran);
        } catch (SQLException e) {
            logError("Gagal membuat tabel MySQL", e);
        }
    }

    private void insertDefaultLapangan() {
        String countSql = "SELECT COUNT(*) FROM lapangan";
        String insertSql = "INSERT INTO lapangan (nama, jenis, harga_per_jam, status) VALUES (?, ?, ?, ?)";
        Object[][] rows = {
            {"Lapangan Futsal A", "Futsal", 150000d, "Tersedia"},
            {"Lapangan Futsal B", "Futsal", 175000d, "Tersedia"},
            {"Lapangan Basket A", "Basket", 200000d, "Tersedia"},
            {"Lapangan Basket B", "Basket", 200000d, "Tersedia"},
            {"Lapangan Bulutangkis 1", "Bulutangkis", 75000d, "Tersedia"},
            {"Lapangan Bulutangkis 2", "Bulutangkis", 75000d, "Tersedia"},
            {"Lapangan Bulutangkis 3", "Bulutangkis", 100000d, "Tersedia"}
        };
        try (Connection conn = getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countSql);
             ResultSet rs = countStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) return;
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Object[] row : rows) {
                    insertStmt.setString(1, (String) row[0]);
                    insertStmt.setString(2, (String) row[1]);
                    insertStmt.setDouble(3, (Double) row[2]);
                    insertStmt.setString(4, (String) row[3]);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            logError("Gagal menambahkan data lapangan default", e);
        }
    }

    private void insertDefaultAdmin() {
        String countSql = "SELECT COUNT(*) FROM users";
        String insertSql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countSql);
             ResultSet rs = countStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) return;
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, "admin");
                insertStmt.setString(2, hashPassword("admin123"));
                insertStmt.setString(3, "Administrator");
                insertStmt.setString(4, "Admin");
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logError("Gagal menambahkan admin default", e);
        }
    }

    public List<Lapangan> getAllLapangan() {
        List<Lapangan> list = new ArrayList<>();
        String sql = "SELECT * FROM lapangan ORDER BY jenis, nama";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapLapangan(rs));
        } catch (SQLException e) { logError("Gagal mengambil data lapangan", e); }
        return list;
    }

    public List<Lapangan> getLapanganByJenis(String jenis) {
        List<Lapangan> list = new ArrayList<>();
        String sql = "SELECT * FROM lapangan WHERE jenis = ? AND status = 'Tersedia' ORDER BY nama";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jenis);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapLapangan(rs)); }
        } catch (SQLException e) { logError("Gagal mengambil lapangan berdasarkan jenis", e); }
        return list;
    }

    public Lapangan getLapanganById(int id) {
        String sql = "SELECT * FROM lapangan WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return mapLapangan(rs); }
        } catch (SQLException e) { logError("Gagal mengambil detail lapangan", e); }
        return null;
    }

    public boolean insertLapangan(Lapangan lap) {
        String sql = "INSERT INTO lapangan (nama, jenis, harga_per_jam, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lap.getNama());
            pstmt.setString(2, lap.getJenis());
            pstmt.setDouble(3, lap.getHargaPerJam());
            pstmt.setString(4, lap.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menambahkan lapangan", e); }
        return false;
    }

    public boolean updateLapangan(Lapangan lap) {
        String sql = "UPDATE lapangan SET nama = ?, jenis = ?, harga_per_jam = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lap.getNama());
            pstmt.setString(2, lap.getJenis());
            pstmt.setDouble(3, lap.getHargaPerJam());
            pstmt.setString(4, lap.getStatus());
            pstmt.setInt(5, lap.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal mengubah lapangan", e); }
        return false;
    }

    public boolean deleteLapangan(int id) {
        String sql = "DELETE FROM lapangan WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menghapus lapangan", e); }
        return false;
    }

    public boolean insertReservasi(Reservasi res) {
        String sql = "INSERT INTO reservasi (nama_pemesan, no_telepon, lapangan_id, tanggal, jam_mulai, jam_selesai, durasi_jam, total_harga, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getNamaPemesan());
            pstmt.setString(2, res.getNoTelepon());
            pstmt.setInt(3, res.getLapanganId());
            pstmt.setString(4, res.getTanggal());
            pstmt.setString(5, res.getJamMulai());
            pstmt.setString(6, res.getJamSelesai());
            pstmt.setInt(7, res.getDurasiJam());
            pstmt.setDouble(8, res.getTotalHarga());
            pstmt.setString(9, res.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menambahkan reservasi", e); }
        return false;
    }

    public List<Reservasi> getAllReservasi() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id ORDER BY r.tanggal DESC, r.jam_mulai DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) { logError("Gagal mengambil semua reservasi", e); }
        return list;
    }

    public List<Reservasi> getReservasiByStatus(String status) {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id WHERE r.status = ? ORDER BY r.tanggal DESC, r.jam_mulai DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservasi res = mapReservasi(rs);
                    res.setNamaLapangan(rs.getString("nama_lapangan"));
                    res.setJenisLapangan(rs.getString("jenis_lapangan"));
                    list.add(res);
                }
            }
        } catch (SQLException e) { logError("Gagal mengambil reservasi berdasarkan status", e); }
        return list;
    }

    public boolean isLapanganBooked(int lapanganId, String tanggal, String jamMulai, String jamSelesai) {
        String sql = "SELECT COUNT(*) FROM reservasi WHERE lapangan_id = ? AND tanggal = ? AND status = 'Aktif' AND NOT (jam_selesai <= ? OR jam_mulai >= ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lapanganId);
            pstmt.setString(2, tanggal);
            pstmt.setString(3, jamMulai);
            pstmt.setString(4, jamSelesai);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1) > 0; }
        } catch (SQLException e) { logError("Gagal mengecek bentrok jadwal lapangan", e); }
        return false;
    }

    public boolean updateStatusReservasi(int id, String status) {
        String sql = "UPDATE reservasi SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal mengubah status reservasi", e); }
        return false;
    }

    public boolean deleteReservasi(int id) {
        String sql = "DELETE FROM reservasi WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menghapus reservasi", e); }
        return false;
    }

    public int getTotalReservasiAktif() { return countByQuery("SELECT COUNT(*) FROM reservasi WHERE status = 'Aktif'"); }
    public int getTotalLapangan() { return countByQuery("SELECT COUNT(*) FROM lapangan"); }

    public double getTotalPendapatan() {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) FROM reservasi WHERE status IN ('Aktif', 'Selesai')";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { logError("Gagal mengambil total pendapatan", e); }
        return 0;
    }

    private int countByQuery(String sql) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { logError("Gagal menjalankan query hitung", e); }
        return 0;
    }

    private Lapangan mapLapangan(ResultSet rs) throws SQLException {
        Lapangan lap = new Lapangan();
        lap.setId(rs.getInt("id"));
        lap.setNama(rs.getString("nama"));
        lap.setJenis(rs.getString("jenis"));
        lap.setHargaPerJam(rs.getDouble("harga_per_jam"));
        lap.setStatus(rs.getString("status"));
        return lap;
    }

    private Reservasi mapReservasi(ResultSet rs) throws SQLException {
        Reservasi res = new Reservasi();
        res.setId(rs.getInt("id"));
        res.setNamaPemesan(rs.getString("nama_pemesan"));
        res.setNoTelepon(rs.getString("no_telepon"));
        res.setLapanganId(rs.getInt("lapangan_id"));
        res.setTanggal(rs.getString("tanggal"));
        res.setJamMulai(rs.getString("jam_mulai"));
        res.setJamSelesai(rs.getString("jam_selesai"));
        res.setDurasiJam(rs.getInt("durasi_jam"));
        res.setTotalHarga(rs.getDouble("total_harga"));
        res.setStatus(rs.getString("status"));
        res.setCreatedAt(rs.getString("created_at"));
        return res;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return password; }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setNamaLengkap(rs.getString("nama_lengkap"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getString("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) { logError("Gagal melakukan autentikasi user", e); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (SQLException e) { logError("Gagal mengambil data user", e); }
        return list;
    }

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashPassword(user.getPassword()));
            pstmt.setString(3, user.getNamaLengkap());
            pstmt.setString(4, user.getRole());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menambahkan user", e); }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, nama_lengkap = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getNamaLengkap());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal mengubah user", e); }
        return false;
    }

    public boolean updateUserPassword(int id, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal mengubah password user", e); }
        return false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menghapus user", e); }
        return false;
    }

    public List<Pelanggan> getAllPelanggan() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY nama";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapPelanggan(rs));
        } catch (SQLException e) { logError("Gagal mengambil data pelanggan", e); }
        return list;
    }

    public boolean insertPelanggan(Pelanggan p) {
        String sql = "INSERT INTO pelanggan (nama, no_telepon, email, alamat) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNama());
            pstmt.setString(2, p.getNoTelepon());
            pstmt.setString(3, p.getEmail());
            pstmt.setString(4, p.getAlamat());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menambahkan pelanggan", e); }
        return false;
    }

    public boolean updatePelanggan(Pelanggan p) {
        String sql = "UPDATE pelanggan SET nama = ?, no_telepon = ?, email = ?, alamat = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNama());
            pstmt.setString(2, p.getNoTelepon());
            pstmt.setString(3, p.getEmail());
            pstmt.setString(4, p.getAlamat());
            pstmt.setInt(5, p.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal mengubah pelanggan", e); }
        return false;
    }

    public boolean deletePelanggan(int id) {
        String sql = "DELETE FROM pelanggan WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menghapus pelanggan", e); }
        return false;
    }

    private Pelanggan mapPelanggan(ResultSet rs) throws SQLException {
        Pelanggan p = new Pelanggan();
        p.setId(rs.getInt("id"));
        p.setNama(rs.getString("nama"));
        p.setNoTelepon(rs.getString("no_telepon"));
        p.setEmail(rs.getString("email"));
        p.setAlamat(rs.getString("alamat"));
        p.setCreatedAt(rs.getString("created_at"));
        return p;
    }

    public boolean insertPembayaran(Pembayaran p) {
        String sql = "INSERT INTO pembayaran (reservasi_id, jumlah_bayar, metode_pembayaran, tanggal_bayar, keterangan) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getReservasiId());
            pstmt.setDouble(2, p.getJumlahBayar());
            pstmt.setString(3, p.getMetodePembayaran());
            pstmt.setString(4, p.getTanggalBayar());
            pstmt.setString(5, p.getKeterangan());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menambahkan pembayaran", e); }
        return false;
    }

    public List<Pembayaran> getAllPembayaran() {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_pemesan, l.nama AS nama_lapangan FROM pembayaran p JOIN reservasi r ON p.reservasi_id = r.id JOIN lapangan l ON r.lapangan_id = l.id ORDER BY p.tanggal_bayar DESC, p.id DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Pembayaran p = new Pembayaran();
                p.setId(rs.getInt("id"));
                p.setReservasiId(rs.getInt("reservasi_id"));
                p.setJumlahBayar(rs.getDouble("jumlah_bayar"));
                p.setMetodePembayaran(rs.getString("metode_pembayaran"));
                p.setTanggalBayar(rs.getString("tanggal_bayar"));
                p.setKeterangan(rs.getString("keterangan"));
                p.setCreatedAt(rs.getString("created_at"));
                p.setNamaPemesan(rs.getString("nama_pemesan"));
                p.setNamaLapangan(rs.getString("nama_lapangan"));
                list.add(p);
            }
        } catch (SQLException e) { logError("Gagal mengambil data pembayaran", e); }
        return list;
    }

    public List<Reservasi> getReservasiBelumBayar() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id WHERE r.status IN ('Aktif', 'Selesai') AND r.id NOT IN (SELECT reservasi_id FROM pembayaran) ORDER BY r.tanggal DESC, r.id DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) { logError("Gagal mengambil reservasi yang belum dibayar", e); }
        return list;
    }

    public boolean deletePembayaran(int id) {
        String sql = "DELETE FROM pembayaran WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { logError("Gagal menghapus pembayaran", e); }
        return false;
    }

    public List<Reservasi> getReservasiByPeriode(String dari, String sampai) {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id WHERE r.tanggal BETWEEN ? AND ? ORDER BY r.tanggal, r.jam_mulai";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservasi res = mapReservasi(rs);
                    res.setNamaLapangan(rs.getString("nama_lapangan"));
                    res.setJenisLapangan(rs.getString("jenis_lapangan"));
                    list.add(res);
                }
            }
        } catch (SQLException e) { logError("Gagal mengambil laporan reservasi", e); }
        return list;
    }

    public List<Object[]> getPendapatanPerLapangan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT l.nama, l.jenis, COUNT(r.id) AS total_booking, COALESCE(SUM(r.total_harga), 0) AS total_pendapatan FROM lapangan l LEFT JOIN reservasi r ON l.id = r.lapangan_id AND r.tanggal BETWEEN ? AND ? AND r.status IN ('Aktif', 'Selesai') GROUP BY l.id, l.nama, l.jenis ORDER BY total_pendapatan DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new Object[]{rs.getString("nama"), rs.getString("jenis"), rs.getInt("total_booking"), rs.getDouble("total_pendapatan")});
            }
        } catch (SQLException e) { logError("Gagal mengambil laporan pendapatan", e); }
        return list;
    }

    public List<Object[]> getTopPelanggan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT nama_pemesan, no_telepon, COUNT(*) AS total_booking, SUM(total_harga) AS total_bayar FROM reservasi WHERE tanggal BETWEEN ? AND ? AND status IN ('Aktif', 'Selesai') GROUP BY nama_pemesan, no_telepon ORDER BY total_booking DESC, total_bayar DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new Object[]{rs.getString("nama_pemesan"), rs.getString("no_telepon"), rs.getInt("total_booking"), rs.getDouble("total_bayar")});
            }
        } catch (SQLException e) { logError("Gagal mengambil laporan pelanggan", e); }
        return list;
    }

    public List<Object[]> getPenggunaanLapangan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT l.nama, l.jenis, l.status, COUNT(r.id) AS total_booking, COALESCE(SUM(r.durasi_jam), 0) AS total_jam FROM lapangan l LEFT JOIN reservasi r ON l.id = r.lapangan_id AND r.tanggal BETWEEN ? AND ? AND r.status IN ('Aktif', 'Selesai') GROUP BY l.id, l.nama, l.jenis, l.status ORDER BY total_jam DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new Object[]{rs.getString("nama"), rs.getString("jenis"), rs.getString("status"), rs.getInt("total_booking"), rs.getInt("total_jam")});
            }
        } catch (SQLException e) { logError("Gagal mengambil laporan penggunaan lapangan", e); }
        return list;
    }

    public int getTotalPelanggan() { return countByQuery("SELECT COUNT(*) FROM pelanggan"); }

    public double getTotalPembayaran() {
        String sql = "SELECT COALESCE(SUM(jumlah_bayar), 0) FROM pembayaran";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { logError("Gagal mengambil total pembayaran", e); }
        return 0;
    }

    private void logError(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
    }
}

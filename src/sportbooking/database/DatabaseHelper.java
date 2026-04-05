package sportbooking.database;

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

    private static final String DB_URL = "jdbc:sqlite:sportbooking.db";
    private static DatabaseHelper instance;

    private DatabaseHelper() {
        createTables();
        insertDefaultLapangan();
        insertDefaultAdmin();
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTables() {
        String sqlLapangan = "CREATE TABLE IF NOT EXISTS lapangan ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama TEXT NOT NULL, "
                + "jenis TEXT NOT NULL, "
                + "harga_per_jam REAL NOT NULL, "
                + "status TEXT DEFAULT 'Tersedia'"
                + ")";

        String sqlReservasi = "CREATE TABLE IF NOT EXISTS reservasi ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama_pemesan TEXT NOT NULL, "
                + "no_telepon TEXT NOT NULL, "
                + "lapangan_id INTEGER NOT NULL, "
                + "tanggal TEXT NOT NULL, "
                + "jam_mulai TEXT NOT NULL, "
                + "jam_selesai TEXT NOT NULL, "
                + "durasi_jam INTEGER NOT NULL, "
                + "total_harga REAL NOT NULL, "
                + "status TEXT DEFAULT 'Aktif', "
                + "created_at TEXT DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY (lapangan_id) REFERENCES lapangan(id)"
                + ")";

        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "nama_lengkap TEXT NOT NULL, "
                + "role TEXT DEFAULT 'Staff', "
                + "created_at TEXT DEFAULT (datetime('now','localtime'))"
                + ")";

        String sqlPelanggan = "CREATE TABLE IF NOT EXISTS pelanggan ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nama TEXT NOT NULL, "
                + "no_telepon TEXT, "
                + "email TEXT, "
                + "alamat TEXT, "
                + "created_at TEXT DEFAULT (datetime('now','localtime'))"
                + ")";

        String sqlPembayaran = "CREATE TABLE IF NOT EXISTS pembayaran ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "reservasi_id INTEGER NOT NULL, "
                + "jumlah_bayar REAL NOT NULL, "
                + "metode_pembayaran TEXT NOT NULL, "
                + "tanggal_bayar TEXT NOT NULL, "
                + "keterangan TEXT, "
                + "created_at TEXT DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY (reservasi_id) REFERENCES reservasi(id)"
                + ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLapangan);
            stmt.execute(sqlReservasi);
            stmt.execute(sqlUsers);
            stmt.execute(sqlPelanggan);
            stmt.execute(sqlPembayaran);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultLapangan() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM lapangan");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] inserts = {
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Futsal A', 'Futsal', 150000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Futsal B', 'Futsal', 175000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Basket A', 'Basket', 200000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Basket B', 'Basket', 200000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Bulutangkis 1', 'Bulutangkis', 75000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Bulutangkis 2', 'Bulutangkis', 75000)",
                    "INSERT INTO lapangan (nama, jenis, harga_per_jam) VALUES ('Lapangan Bulutangkis 3', 'Bulutangkis', 100000)"
                };
                for (String sql : inserts) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== LAPANGAN CRUD ====================

    public List<Lapangan> getAllLapangan() {
        List<Lapangan> list = new ArrayList<>();
        String sql = "SELECT * FROM lapangan ORDER BY jenis, nama";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapLapangan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Lapangan> getLapanganByJenis(String jenis) {
        List<Lapangan> list = new ArrayList<>();
        String sql = "SELECT * FROM lapangan WHERE jenis = ? AND status = 'Tersedia' ORDER BY nama";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jenis);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapLapangan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Lapangan getLapanganById(int id) {
        String sql = "SELECT * FROM lapangan WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapLapangan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertLapangan(Lapangan lap) {
        String sql = "INSERT INTO lapangan (nama, jenis, harga_per_jam, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lap.getNama());
            pstmt.setString(2, lap.getJenis());
            pstmt.setDouble(3, lap.getHargaPerJam());
            pstmt.setString(4, lap.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateLapangan(Lapangan lap) {
        String sql = "UPDATE lapangan SET nama = ?, jenis = ?, harga_per_jam = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lap.getNama());
            pstmt.setString(2, lap.getJenis());
            pstmt.setDouble(3, lap.getHargaPerJam());
            pstmt.setString(4, lap.getStatus());
            pstmt.setInt(5, lap.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteLapangan(int id) {
        String sql = "DELETE FROM lapangan WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== RESERVASI CRUD ====================

    public boolean insertReservasi(Reservasi res) {
        String sql = "INSERT INTO reservasi (nama_pemesan, no_telepon, lapangan_id, tanggal, "
                + "jam_mulai, jam_selesai, durasi_jam, total_harga, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reservasi> getAllReservasi() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan "
                + "FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id "
                + "ORDER BY r.tanggal DESC, r.jam_mulai DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservasi> getReservasiByStatus(String status) {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan "
                + "FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id "
                + "WHERE r.status = ? "
                + "ORDER BY r.tanggal DESC, r.jam_mulai DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isLapanganBooked(int lapanganId, String tanggal, String jamMulai, String jamSelesai) {
        String sql = "SELECT COUNT(*) FROM reservasi "
                + "WHERE lapangan_id = ? AND tanggal = ? AND status = 'Aktif' "
                + "AND NOT (jam_selesai <= ? OR jam_mulai >= ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lapanganId);
            pstmt.setString(2, tanggal);
            pstmt.setString(3, jamMulai);
            pstmt.setString(4, jamSelesai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatusReservasi(int id, String status) {
        String sql = "UPDATE reservasi SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReservasi(int id) {
        String sql = "DELETE FROM reservasi WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== STATISTICS ====================

    public int getTotalReservasiAktif() {
        return countByQuery("SELECT COUNT(*) FROM reservasi WHERE status = 'Aktif'");
    }

    public int getTotalLapangan() {
        return countByQuery("SELECT COUNT(*) FROM lapangan");
    }

    public double getTotalPendapatan() {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) FROM reservasi WHERE status IN ('Aktif', 'Selesai')";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countByQuery(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== MAPPERS ====================

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

    // ==================== PASSWORD HASHING ====================

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    // ==================== DEFAULT ADMIN ====================

    private void insertDefaultAdmin() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, hashPassword("admin123"));
                    pstmt.setString(3, "Administrator");
                    pstmt.setString(4, "Admin");
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== USER CRUD ====================

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getString("created_at"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashPassword(user.getPassword()));
            pstmt.setString(3, user.getNamaLengkap());
            pstmt.setString(4, user.getRole());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, nama_lengkap = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getNamaLengkap());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserPassword(int id, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== PELANGGAN CRUD ====================

    public List<Pelanggan> getAllPelanggan() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY nama";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapPelanggan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertPelanggan(Pelanggan p) {
        String sql = "INSERT INTO pelanggan (nama, no_telepon, email, alamat) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNama());
            pstmt.setString(2, p.getNoTelepon());
            pstmt.setString(3, p.getEmail());
            pstmt.setString(4, p.getAlamat());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePelanggan(Pelanggan p) {
        String sql = "UPDATE pelanggan SET nama = ?, no_telepon = ?, email = ?, alamat = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNama());
            pstmt.setString(2, p.getNoTelepon());
            pstmt.setString(3, p.getEmail());
            pstmt.setString(4, p.getAlamat());
            pstmt.setInt(5, p.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePelanggan(int id) {
        String sql = "DELETE FROM pelanggan WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // ==================== PEMBAYARAN CRUD ====================

    public boolean insertPembayaran(Pembayaran p) {
        String sql = "INSERT INTO pembayaran (reservasi_id, jumlah_bayar, metode_pembayaran, tanggal_bayar, keterangan) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getReservasiId());
            pstmt.setDouble(2, p.getJumlahBayar());
            pstmt.setString(3, p.getMetodePembayaran());
            pstmt.setString(4, p.getTanggalBayar());
            pstmt.setString(5, p.getKeterangan());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Pembayaran> getAllPembayaran() {
        List<Pembayaran> list = new ArrayList<>();
        String sql = "SELECT p.*, r.nama_pemesan, l.nama AS nama_lapangan "
                + "FROM pembayaran p "
                + "JOIN reservasi r ON p.reservasi_id = r.id "
                + "JOIN lapangan l ON r.lapangan_id = l.id "
                + "ORDER BY p.tanggal_bayar DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservasi> getReservasiBelumBayar() {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan "
                + "FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id "
                + "WHERE r.status IN ('Aktif', 'Selesai') "
                + "AND r.id NOT IN (SELECT reservasi_id FROM pembayaran) "
                + "ORDER BY r.tanggal DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deletePembayaran(int id) {
        String sql = "DELETE FROM pembayaran WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== REPORT QUERIES ====================

    public List<Reservasi> getReservasiByPeriode(String dari, String sampai) {
        List<Reservasi> list = new ArrayList<>();
        String sql = "SELECT r.*, l.nama AS nama_lapangan, l.jenis AS jenis_lapangan "
                + "FROM reservasi r JOIN lapangan l ON r.lapangan_id = l.id "
                + "WHERE r.tanggal BETWEEN ? AND ? "
                + "ORDER BY r.tanggal, r.jam_mulai";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reservasi res = mapReservasi(rs);
                res.setNamaLapangan(rs.getString("nama_lapangan"));
                res.setJenisLapangan(rs.getString("jenis_lapangan"));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getPendapatanPerLapangan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT l.nama, l.jenis, COUNT(r.id) AS total_booking, "
                + "COALESCE(SUM(r.total_harga), 0) AS total_pendapatan "
                + "FROM lapangan l LEFT JOIN reservasi r ON l.id = r.lapangan_id "
                + "AND r.tanggal BETWEEN ? AND ? AND r.status IN ('Aktif', 'Selesai') "
                + "GROUP BY l.id ORDER BY total_pendapatan DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nama"),
                    rs.getString("jenis"),
                    rs.getInt("total_booking"),
                    rs.getDouble("total_pendapatan")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getTopPelanggan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT nama_pemesan, no_telepon, COUNT(*) AS total_booking, "
                + "SUM(total_harga) AS total_bayar "
                + "FROM reservasi WHERE tanggal BETWEEN ? AND ? "
                + "AND status IN ('Aktif', 'Selesai') "
                + "GROUP BY nama_pemesan, no_telepon ORDER BY total_booking DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nama_pemesan"),
                    rs.getString("no_telepon"),
                    rs.getInt("total_booking"),
                    rs.getDouble("total_bayar")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getPenggunaanLapangan(String dari, String sampai) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT l.nama, l.jenis, l.status, COUNT(r.id) AS total_booking, "
                + "COALESCE(SUM(r.durasi_jam), 0) AS total_jam "
                + "FROM lapangan l LEFT JOIN reservasi r ON l.id = r.lapangan_id "
                + "AND r.tanggal BETWEEN ? AND ? AND r.status IN ('Aktif', 'Selesai') "
                + "GROUP BY l.id ORDER BY total_jam DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dari);
            pstmt.setString(2, sampai);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nama"),
                    rs.getString("jenis"),
                    rs.getString("status"),
                    rs.getInt("total_booking"),
                    rs.getInt("total_jam")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalPelanggan() {
        return countByQuery("SELECT COUNT(*) FROM pelanggan");
    }

    public double getTotalPembayaran() {
        String sql = "SELECT COALESCE(SUM(jumlah_bayar), 0) FROM pembayaran";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

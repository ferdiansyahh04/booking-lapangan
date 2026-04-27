CREATE DATABASE IF NOT EXISTS sportbooking
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sportbooking;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'Staff',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pelanggan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    no_telepon VARCHAR(20) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    alamat VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS lapangan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    jenis VARCHAR(50) NOT NULL,
    harga_per_jam DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Tersedia'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservasi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_pemesan VARCHAR(100) NOT NULL,
    no_telepon VARCHAR(20) NOT NULL,
    lapangan_id INT NOT NULL,
    tanggal VARCHAR(10) NOT NULL,
    jam_mulai VARCHAR(5) NOT NULL,
    jam_selesai VARCHAR(5) NOT NULL,
    durasi_jam INT NOT NULL,
    total_harga DECIMAL(12,2) NOT NULL,
    status ENUM('menunggu', 'dibayar', 'selesai', 'dibatalkan') NOT NULL DEFAULT 'menunggu',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservasi_lapangan FOREIGN KEY (lapangan_id)
        REFERENCES lapangan(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pembayaran (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservasi_id INT NOT NULL,
    jumlah_bayar DECIMAL(12,2) NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    tanggal_bayar VARCHAR(10) NOT NULL,
    keterangan VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pembayaran_reservasi FOREIGN KEY (reservasi_id)
        REFERENCES reservasi(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

INSERT INTO users (username, password, nama_lengkap, role)
SELECT 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator', 'Admin'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Futsal A', 'Futsal', 150000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Futsal A');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Futsal B', 'Futsal', 175000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Futsal B');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Basket A', 'Basket', 200000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Basket A');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Basket B', 'Basket', 200000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Basket B');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Bulutangkis 1', 'Bulutangkis', 75000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Bulutangkis 1');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Bulutangkis 2', 'Bulutangkis', 75000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Bulutangkis 2');

INSERT INTO lapangan (nama, jenis, harga_per_jam, status)
SELECT 'Lapangan Bulutangkis 3', 'Bulutangkis', 100000, 'Tersedia'
WHERE NOT EXISTS (SELECT 1 FROM lapangan WHERE nama = 'Lapangan Bulutangkis 3');

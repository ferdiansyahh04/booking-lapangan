# Sport Booking System

Aplikasi desktop reservasi lapangan olahraga berbasis Java Swing untuk Futsal, Basket, dan Bulutangkis. Project ini menggunakan MySQL melalui XAMPP dan dirancang untuk dijalankan di NetBeans.

## Fitur

### Login dan Menu
- Login dengan autentikasi username dan password
- Password disimpan dengan hash SHA-256
- Default login: `admin / admin123`
- Menu utama menggunakan `JMenuBar`
- Informasi user aktif tampil di header
- Fitur logout

### Form Master
- Data Lapangan
- Data Pelanggan
- Data User

### Form Transaksi
- Reservasi Baru
- Riwayat Booking
- Pembayaran

### Laporan
- Laporan Reservasi
- Laporan Pendapatan
- Laporan Pelanggan
- Laporan Penggunaan Lapangan

## Teknologi

| Teknologi | Keterangan |
|-----------|------------|
| Java SE 8+ | Bahasa pemrograman utama |
| Java Swing | Antarmuka desktop |
| MySQL | Database utama |
| XAMPP | Menjalankan Apache dan MySQL lokal |
| JDBC MySQL Connector | Koneksi Java ke MySQL |
| NetBeans 8.x | IDE yang direkomendasikan |

## Struktur Project

```text
SportBooking/
|-- src/
|   `-- sportbooking/
|       |-- Main.java
|       |-- database/
|       |   `-- DatabaseHelper.java
|       |-- model/
|       |   |-- Lapangan.java
|       |   |-- Pelanggan.java
|       |   |-- Pembayaran.java
|       |   |-- Reservasi.java
|       |   `-- User.java
|       `-- ui/
|           |-- BookingDialog.java
|           |-- LoginFrame.java
|           |-- MainFrame.java
|           |-- PanelDashboard.java
|           |-- PanelLapangan.java
|           |-- PanelLapanganPelanggan.java
|           |-- PanelLaporanLapangan.java
|           |-- PanelLaporanPelanggan.java
|           |-- PanelLaporanPendapatan.java
|           |-- PanelLaporanReservasi.java
|           |-- PanelPelanggan.java
|           |-- PanelPembayaran.java
|           |-- PanelReservasi.java
|           |-- PanelRiwayat.java
|           |-- PanelUser.java
|           `-- RegisterFrame.java
|-- mysql_sportbooking.sql
`-- nbproject/
```

## Setup MySQL dengan XAMPP

### 1. Jalankan XAMPP
1. Buka XAMPP Control Panel.
2. Start `Apache`.
3. Start `MySQL`.

### 2. Siapkan Database
1. Buka `http://localhost/phpmyadmin`.
2. Import file `mysql_sportbooking.sql`.
3. Script akan:
   - membuat database `sportbooking`
   - membuat seluruh tabel
   - menambahkan data default admin dan lapangan

### 3. Tambahkan JDBC Driver MySQL
1. Download `mysql-connector-j`.
2. Simpan file JAR ke folder `lib/`.
3. Nama file yang digunakan di konfigurasi project:
   - `lib/mysql-connector-j-9.3.0.jar`
4. Di NetBeans, buka `Project Properties > Libraries`.
5. Pastikan MySQL Connector sudah ditambahkan.

### 4. Jalankan Project
1. Buka project di NetBeans.
2. Pastikan `sportbooking.Main` adalah Main Class.
3. Jalankan project dengan `F6`.

## Konfigurasi Database

Konfigurasi koneksi ada di `src/sportbooking/database/DatabaseHelper.java`:

- Host: `localhost`
- Port: `3306`
- Database: `sportbooking`
- Username: `root`
- Password: kosong

Project menggunakan JDBC MySQL dan query dijalankan dengan `PreparedStatement`.

## Skema Database

Entity yang digunakan:
- `users`
- `pelanggan`
- `lapangan`
- `reservasi`
- `pembayaran`

Seluruh `CREATE TABLE` dan data awal tersedia di file `mysql_sportbooking.sql`.

## Data Default

### User Default
- Username: `admin`
- Password: `admin123`
- Role: `Admin`

### Lapangan Default
- Lapangan Futsal A
- Lapangan Futsal B
- Lapangan Basket A
- Lapangan Basket B
- Lapangan Bulutangkis 1
- Lapangan Bulutangkis 2
- Lapangan Bulutangkis 3

## Catatan Build

- Project dikonfigurasi untuk Java 8.
- Project menggunakan MySQL, bukan database file lokal.
- File konfigurasi library sudah diarahkan ke MySQL Connector di `nbproject/project.properties`.

## Checklist Submission

| Item | Status |
|------|--------|
| Login dan menu | Siap |
| 6 form master + transaksi | Siap |
| 4 laporan | Siap |
| Integrasi database MySQL | Siap |
| SQL import untuk phpMyAdmin | Siap |

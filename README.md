# Sport Booking System
Aplikasi Reservasi Lapangan Olahraga (Futsal, Basket, Bulutangkis)
Dibuat menggunakan Java Swing + SQLite untuk NetBeans 8.

---

## Fitur Aplikasi

### Login & Menu
- Halaman login dengan autentikasi username & password (SHA-256 hashing)
- Default login: **admin** / **admin123**
- Navigasi menggunakan **JMenuBar** dengan menu: Dashboard, Master, Transaksi, Laporan, File
- Info user yang sedang login ditampilkan di header
- Fitur logout dan keluar aplikasi

### Dashboard
- Ringkasan statistik: **Total Lapangan**, **Reservasi Aktif**, dan **Total Pendapatan**
- Kartu informasi olahraga (Futsal, Basket, Bulutangkis) dengan deskripsi dan harga

---

### Form Master (3 Form)

#### 1. Data Lapangan
- CRUD data lapangan (Tambah, Update, Hapus)
- Form input: Nama, Jenis (Futsal/Basket/Bulutangkis), Harga per Jam, Status (Tersedia/Tidak Tersedia)
- Tabel data dengan kolom: ID, Nama Lapangan, Jenis, Harga/Jam, Status
- Validasi: nama wajib diisi, harga harus angka positif

#### 2. Data Pelanggan
- CRUD data pelanggan (Tambah, Update, Hapus)
- Form input: Nama, No. Telepon, Email, Alamat
- Tabel data dengan kolom: ID, Nama, No. Telepon, Email, Alamat
- Validasi: nama pelanggan wajib diisi

#### 3. Data User
- CRUD data user/pengguna (Tambah, Update, Hapus)
- Form input: Username, Password, Nama Lengkap, Role (Admin/Staff)
- Fitur **Reset Password** untuk mengubah password user
- User admin default tidak dapat dihapus
- Validasi: username, password, dan nama lengkap wajib diisi

---

### Form Transaksi (3 Form)

#### 4. Reservasi Baru
- Form booking dengan 3 section: Data Pemesan, Pilih Lapangan, Waktu Booking
- Dropdown lapangan otomatis berubah sesuai jenis yang dipilih (hanya yang **Tersedia**)
- Pilihan tanggal: **14 hari ke depan** dari hari ini
- Jam operasional: **07:00 - 22:00**, durasi sewa: **1 - 5 jam**
- Perhitungan total harga otomatis (harga per jam × durasi)
- Validasi: nama, telepon (8-15 digit), deteksi **jadwal bentrok**, jam selesai ≤ 24:00
- Dialog konfirmasi sebelum menyimpan

#### 5. Riwayat Booking
- Tabel riwayat dengan kolom: ID, Nama Pemesan, No. Telepon, Lapangan, Jenis, Tanggal, Jam, Durasi, Total Harga, Status
- Filter berdasarkan status: **Semua / Aktif / Selesai / Dibatalkan**
- Tombol aksi: Tandai Selesai, Batalkan, Hapus, Refresh
- Status ditampilkan warna: Aktif (hijau), Selesai (biru), Dibatalkan (merah)

#### 6. Pembayaran
- Form pembayaran reservasi yang belum dibayar
- Pilih reservasi dari dropdown (otomatis menampilkan info & total harga)
- Metode pembayaran: **Tunai / Transfer Bank / E-Wallet**
- Tabel riwayat pembayaran: ID, Nama Pemesan, Lapangan, Jumlah Bayar, Metode, Tgl Bayar, Keterangan

---

### Laporan (4 Report)

#### 1. Laporan Reservasi
- Filter berdasarkan periode tanggal (dari - sampai)
- Tabel: No, Tgl Booking, Nama Pemesan, No. Telepon, Lapangan, Jenis, Jam, Durasi, Total Harga, Status
- Ringkasan total reservasi dan total harga
- Fitur **Cetak** (print)

#### 2. Laporan Pendapatan
- Filter berdasarkan periode tanggal
- Tabel: No, Nama Lapangan, Jenis, Total Booking, Total Pendapatan
- Grand total pendapatan seluruh lapangan
- Fitur **Cetak** (print)

#### 3. Laporan Pelanggan
- Filter berdasarkan periode tanggal
- Tabel peringkat pelanggan: Peringkat, Nama, No. Telepon, Total Booking, Total Bayar
- Diurutkan berdasarkan jumlah booking terbanyak
- Fitur **Cetak** (print)

#### 4. Laporan Penggunaan Lapangan
- Filter berdasarkan periode tanggal
- Tabel: No, Nama Lapangan, Jenis, Status, Total Booking, Total Jam Pakai
- Ringkasan total booking dan jam terpakai
- Fitur **Cetak** (print)

---

## Arsitektur Aplikasi

```
sportbooking/
├── Main.java                       → Entry point, login flow, load JDBC driver
├── database/
│   └── DatabaseHelper.java         → Singleton, semua CRUD & query laporan
├── model/
│   ├── Lapangan.java               → Model data lapangan olahraga
│   ├── Reservasi.java              → Model data reservasi/booking
│   ├── Pelanggan.java              → Model data pelanggan
│   ├── User.java                   → Model data user/pengguna
│   └── Pembayaran.java             → Model data pembayaran
└── ui/
    ├── LoginFrame.java             → Dialog login (autentikasi)
    ├── MainFrame.java              → Frame utama dengan JMenuBar + CardLayout
    ├── PanelDashboard.java         → Statistik & info olahraga
    ├── PanelLapangan.java          → Master Lapangan (CRUD)
    ├── PanelPelanggan.java         → Master Pelanggan (CRUD)
    ├── PanelUser.java              → Master User (CRUD)
    ├── PanelReservasi.java         → Transaksi Reservasi Baru
    ├── PanelRiwayat.java           → Transaksi Riwayat Booking
    ├── PanelPembayaran.java        → Transaksi Pembayaran
    ├── PanelLaporanReservasi.java  → Laporan Reservasi per Periode
    ├── PanelLaporanPendapatan.java → Laporan Pendapatan per Lapangan
    ├── PanelLaporanPelanggan.java  → Laporan Pelanggan Terbanyak
    └── PanelLaporanLapangan.java   → Laporan Penggunaan Lapangan
```

**Design Pattern:**
- **Singleton** - `DatabaseHelper.getInstance()` untuk satu koneksi database
- **MVC-like** - Pemisahan Model, Database layer, dan UI panels
- **CardLayout** - Navigasi antar panel menggunakan menu

---

## Struktur Menu

```
┌─ Dashboard
├─ Master
│  ├── Data Lapangan
│  ├── Data Pelanggan
│  └── Data User
├─ Transaksi
│  ├── Reservasi Baru
│  ├── Riwayat Booking
│  └── Pembayaran
├─ Laporan
│  ├── Laporan Reservasi
│  ├── Laporan Pendapatan
│  ├── Laporan Pelanggan
│  └── Laporan Penggunaan Lapangan
└─ File
   ├── Logout
   └── Keluar
```

---

## Cara Setup di NetBeans 8

### 1. Download SQLite JDBC Driver
- Buka https://github.com/xerial/sqlite-jdbc/releases
- Download file **sqlite-jdbc-3.36.0.3.jar** (atau versi terbaru)
- Simpan file JAR tersebut

### 2. Buat Project Baru di NetBeans
1. Buka NetBeans 8
2. Pilih **File > New Project**
3. Pilih **Java > Java Application**
4. Beri nama project: **SportBooking**
5. Pastikan **Create Main Class** di-uncheck
6. Klik **Finish**

### 3. Tambahkan Source Code
1. Copy semua folder di dalam `src/` ke folder `src/` project NetBeans Anda
2. Struktur folder harus seperti ini:
   ```
   SportBooking/
   └── src/
       └── sportbooking/
           ├── Main.java
           ├── database/
           │   └── DatabaseHelper.java
           ├── model/
           │   ├── Lapangan.java
           │   ├── Reservasi.java
           │   ├── Pelanggan.java
           │   ├── User.java
           │   └── Pembayaran.java
           └── ui/
               ├── LoginFrame.java
               ├── MainFrame.java
               ├── PanelDashboard.java
               ├── PanelLapangan.java
               ├── PanelPelanggan.java
               ├── PanelUser.java
               ├── PanelReservasi.java
               ├── PanelRiwayat.java
               ├── PanelPembayaran.java
               ├── PanelLaporanReservasi.java
               ├── PanelLaporanPendapatan.java
               ├── PanelLaporanPelanggan.java
               └── PanelLaporanLapangan.java
   ```

### 4. Tambahkan Library SQLite
1. Klik kanan pada project **SportBooking** di panel Projects
2. Pilih **Properties**
3. Pilih **Libraries** di menu kiri
4. Klik **Add JAR/Folder**
5. Pilih file **sqlite-jdbc-3.x.x.jar** yang sudah didownload
6. Klik **OK**

### 5. Set Main Class
1. Klik kanan project > **Properties**
2. Pilih **Run**
3. Di field **Main Class**, klik **Browse** dan pilih `sportbooking.Main`
4. Klik **OK**

### 6. Jalankan Aplikasi
- Tekan **F6** atau klik tombol **Run Project** (tombol hijau)
- Database `sportbooking.db` akan otomatis dibuat di folder project saat pertama kali dijalankan

---

## Struktur Database (SQLite - Auto-create)

### Tabel `users`
| Kolom         | Tipe    | Keterangan                       |
|---------------|---------|----------------------------------|
| id            | INTEGER | Primary Key, Auto Increment      |
| username      | TEXT    | Username unik (NOT NULL, UNIQUE) |
| password      | TEXT    | Password hash SHA-256 (NOT NULL) |
| nama_lengkap  | TEXT    | Nama lengkap user (NOT NULL)     |
| role          | TEXT    | Admin / Staff (default: Staff)   |
| created_at    | TEXT    | Waktu pembuatan (auto)           |

### Tabel `lapangan`
| Kolom         | Tipe    | Keterangan                       |
|---------------|---------|----------------------------------|
| id            | INTEGER | Primary Key, Auto Increment      |
| nama          | TEXT    | Nama lapangan (NOT NULL)         |
| jenis         | TEXT    | Futsal / Basket / Bulutangkis (NOT NULL) |
| harga_per_jam | REAL    | Harga sewa per jam (NOT NULL)    |
| status        | TEXT    | Tersedia / Tidak Tersedia (default: Tersedia) |

### Tabel `pelanggan`
| Kolom         | Tipe    | Keterangan                       |
|---------------|---------|----------------------------------|
| id            | INTEGER | Primary Key, Auto Increment      |
| nama          | TEXT    | Nama pelanggan (NOT NULL)        |
| no_telepon    | TEXT    | Nomor telepon                    |
| email         | TEXT    | Alamat email                     |
| alamat        | TEXT    | Alamat pelanggan                 |
| created_at    | TEXT    | Waktu pembuatan (auto)           |

### Tabel `reservasi`
| Kolom         | Tipe    | Keterangan                       |
|---------------|---------|----------------------------------|
| id            | INTEGER | Primary Key, Auto Increment      |
| nama_pemesan  | TEXT    | Nama pelanggan (NOT NULL)        |
| no_telepon    | TEXT    | Nomor telepon pelanggan (NOT NULL) |
| lapangan_id   | INTEGER | Foreign Key ke tabel lapangan (NOT NULL) |
| tanggal       | TEXT    | Tanggal booking (yyyy-MM-dd) (NOT NULL) |
| jam_mulai     | TEXT    | Jam mulai (HH:00) (NOT NULL)    |
| jam_selesai   | TEXT    | Jam selesai (HH:00) (NOT NULL)  |
| durasi_jam    | INTEGER | Durasi sewa dalam jam (NOT NULL) |
| total_harga   | REAL    | Total biaya (NOT NULL)           |
| status        | TEXT    | Aktif / Selesai / Dibatalkan (default: Aktif) |
| created_at    | TEXT    | Waktu pembuatan (auto)           |

### Tabel `pembayaran`
| Kolom              | Tipe    | Keterangan                       |
|--------------------|---------|----------------------------------|
| id                 | INTEGER | Primary Key, Auto Increment      |
| reservasi_id       | INTEGER | Foreign Key ke tabel reservasi (NOT NULL) |
| jumlah_bayar       | REAL    | Jumlah pembayaran (NOT NULL)     |
| metode_pembayaran  | TEXT    | Tunai / Transfer Bank / E-Wallet (NOT NULL) |
| tanggal_bayar      | TEXT    | Tanggal bayar (NOT NULL)         |
| keterangan         | TEXT    | Catatan/keterangan pembayaran    |
| created_at         | TEXT    | Waktu pembuatan (auto)           |

### Alur Status Reservasi
```
Aktif → Selesai
Aktif → Dibatalkan
```
> Hanya reservasi berstatus **Aktif** yang dapat diubah statusnya.

---

## Data Default

### User Default
| Username | Password | Nama Lengkap  | Role  |
|----------|----------|---------------|-------|
| admin    | admin123 | Administrator | Admin |

### Lapangan Default

Aplikasi akan otomatis menambahkan data lapangan berikut saat pertama kali dijalankan:

| Nama                   | Jenis        | Harga/Jam   |
|------------------------|--------------|-------------|
| Lapangan Futsal A      | Futsal       | Rp 150.000  |
| Lapangan Futsal B      | Futsal       | Rp 175.000  |
| Lapangan Basket A      | Basket       | Rp 200.000  |
| Lapangan Basket B      | Basket       | Rp 200.000  |
| Lapangan Bulutangkis 1 | Bulutangkis  | Rp 75.000   |
| Lapangan Bulutangkis 2 | Bulutangkis  | Rp 75.000   |
| Lapangan Bulutangkis 3 | Bulutangkis  | Rp 100.000  |

---

## Teknologi
| Teknologi | Keterangan |
|-----------|------------|
| **Java SE 8+** | Bahasa pemrograman utama (`javac.source=1.8`) |
| **Java Swing** | GUI framework (JFrame, JMenuBar, CardLayout, JTable, GridBagLayout) |
| **SQLite** | Database embedded via `sqlite-jdbc-3.36.0.3.jar` |
| **JDBC** | Koneksi database dengan PreparedStatement (SQL injection safe) |
| **SHA-256** | Password hashing (java.security.MessageDigest) |
| **Apache Ant** | Build tool (via NetBeans 8.x) |
| **NetBeans 8.x** | IDE yang direkomendasikan |

## Output Build
- **JAR file**: `dist/SportBooking.jar`
- **Database file**: `sportbooking.db` (auto-created di folder project)

---

## Checklist Syarat Project

| Syarat | Status | Keterangan |
|--------|--------|------------|
| Java + Netbeans + Database | ✅ | Java Swing + SQLite + NetBeans 8 |
| Login dan Menu | ✅ | LoginFrame + JMenuBar |
| 6 Form (Master + Transaksi) | ✅ | 3 Master (Lapangan, Pelanggan, User) + 3 Transaksi (Reservasi, Riwayat, Pembayaran) |
| 4 Report (Laporan) | ✅ | Laporan Reservasi, Pendapatan, Pelanggan, Penggunaan Lapangan |

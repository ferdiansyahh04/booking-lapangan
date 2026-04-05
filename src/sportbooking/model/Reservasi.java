package sportbooking.model;

public class Reservasi {

    private int id;
    private String namaPemesan;
    private String noTelepon;
    private int lapanganId;
    private String tanggal;
    private String jamMulai;
    private String jamSelesai;
    private int durasiJam;
    private double totalHarga;
    private String status; // Aktif, Selesai, Dibatalkan
    private String createdAt;

    // Tambahan untuk display
    private String namaLapangan;
    private String jenisLapangan;

    public Reservasi() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaPemesan() { return namaPemesan; }
    public void setNamaPemesan(String namaPemesan) { this.namaPemesan = namaPemesan; }

    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }

    public int getLapanganId() { return lapanganId; }
    public void setLapanganId(int lapanganId) { this.lapanganId = lapanganId; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getJamMulai() { return jamMulai; }
    public void setJamMulai(String jamMulai) { this.jamMulai = jamMulai; }

    public String getJamSelesai() { return jamSelesai; }
    public void setJamSelesai(String jamSelesai) { this.jamSelesai = jamSelesai; }

    public int getDurasiJam() { return durasiJam; }
    public void setDurasiJam(int durasiJam) { this.durasiJam = durasiJam; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getNamaLapangan() { return namaLapangan; }
    public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }

    public String getJenisLapangan() { return jenisLapangan; }
    public void setJenisLapangan(String jenisLapangan) { this.jenisLapangan = jenisLapangan; }
}

package sportbooking.model;

public class Pembayaran {

    private int id;
    private int reservasiId;
    private double jumlahBayar;
    private String metodePembayaran; // Tunai, Transfer
    private String tanggalBayar;
    private String keterangan;
    private String createdAt;

    // Display fields from JOIN
    private String namaPemesan;
    private String namaLapangan;

    public Pembayaran() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservasiId() { return reservasiId; }
    public void setReservasiId(int reservasiId) { this.reservasiId = reservasiId; }

    public double getJumlahBayar() { return jumlahBayar; }
    public void setJumlahBayar(double jumlahBayar) { this.jumlahBayar = jumlahBayar; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getTanggalBayar() { return tanggalBayar; }
    public void setTanggalBayar(String tanggalBayar) { this.tanggalBayar = tanggalBayar; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getNamaPemesan() { return namaPemesan; }
    public void setNamaPemesan(String namaPemesan) { this.namaPemesan = namaPemesan; }

    public String getNamaLapangan() { return namaLapangan; }
    public void setNamaLapangan(String namaLapangan) { this.namaLapangan = namaLapangan; }
}

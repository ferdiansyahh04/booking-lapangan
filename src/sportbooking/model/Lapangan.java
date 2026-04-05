package sportbooking.model;

public class Lapangan {

    private int id;
    private String nama;
    private String jenis; // Futsal, Basket, Bulutangkis
    private double hargaPerJam;
    private String status; // Tersedia, Tidak Tersedia

    public Lapangan() {
    }

    public Lapangan(String nama, String jenis, double hargaPerJam, String status) {
        this.nama = nama;
        this.jenis = jenis;
        this.hargaPerJam = hargaPerJam;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public double getHargaPerJam() { return hargaPerJam; }
    public void setHargaPerJam(double hargaPerJam) { this.hargaPerJam = hargaPerJam; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return nama + " (" + jenis + ") - Rp " + String.format("%,.0f", hargaPerJam) + "/jam";
    }
}

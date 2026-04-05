package sportbooking.model;

public class Pelanggan {

    private int id;
    private String nama;
    private String noTelepon;
    private String email;
    private String alamat;
    private String createdAt;

    public Pelanggan() {
    }

    public Pelanggan(String nama, String noTelepon, String email, String alamat) {
        this.nama = nama;
        this.noTelepon = noTelepon;
        this.email = email;
        this.alamat = alamat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return nama + " (" + noTelepon + ")";
    }
}

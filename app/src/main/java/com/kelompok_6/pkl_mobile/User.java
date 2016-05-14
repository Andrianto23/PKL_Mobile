package com.kelompok_6.pkl_mobile;

public class User {

    private String user, nama, alamat, nohp, tanggallahir, produkunggulan;

    public User(String user, String nama, String alamat, String nohp, String tanggallahir, String produkunggulan){
        this.user = user;
        this.nama = nama;
        this.alamat = alamat;
        this.nohp = nohp;
        this.tanggallahir = tanggallahir;
        this.produkunggulan = produkunggulan;
    }

    public String getUser() {
        return user;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNohp() {
        return nohp;
    }

    public String getTanggallahir() {
        return tanggallahir;
    }

    public String getProdukunggulan() {
        return produkunggulan;
    }

    @Override
    public String toString() {
        return "User: " + user + " Nama: " + nama + " Alamat: " + alamat + " NoHP: " + nohp + " Tanggal Lahir: " + tanggallahir + " Produk Unggulan: " + produkunggulan;
    }
}

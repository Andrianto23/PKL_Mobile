package com.kelompok_6.pkl_mobile;

public class Produk {

    private long IDProduk;
    private String idUser, namaProduk;
    private int hargaPokok, hargaJual;

    public Produk(long IDProduk, String idUser, String namaProduk, int hargaPokok, int hargaJual){
        this.IDProduk = IDProduk;
        this.idUser = idUser;
        this.namaProduk = namaProduk;
        this.hargaPokok = hargaPokok;
        this.hargaJual = hargaJual;
    }

    public long getIDProduk() {
        return IDProduk;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public int getHargaPokok() {
        return hargaPokok;
    }

    public int getHargaJual() {
        return hargaJual;
    }

    @Override
    public String toString() {
        return "IDProduk: "+ IDProduk + " IDUser: " + idUser + " Nama Produk: " + namaProduk + " Harga Pokok: " + hargaPokok + " Harga Jual: "+ hargaJual;
    }
}

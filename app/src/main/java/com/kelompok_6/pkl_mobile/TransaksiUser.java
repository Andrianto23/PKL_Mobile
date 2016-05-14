package com.kelompok_6.pkl_mobile;


public class TransaksiUser {

    private long IDTransaksi, IDProduk;
    private String IDUser, namaProduk, tanggalJual;
    private int HargaJual, QtyJual;

    public TransaksiUser(long IDTransaksi, long IDProduk, String IDUser, String namaProduk, int hargaJual, int QtyJual, String tanggalJual){
        this.IDTransaksi = IDTransaksi;
        this.IDProduk =IDProduk;
        this.IDUser = IDUser;
        this.namaProduk = namaProduk;
        this.HargaJual = hargaJual;
        this.QtyJual = QtyJual;
        this.tanggalJual = tanggalJual;
    }

    public long getIDTransaksi() {
        return IDTransaksi;
    }

    public long getIDProduk() {
        return IDProduk;
    }

    public String getIDUser() {
        return IDUser;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public int getHargaJual() {
        return HargaJual;
    }

    public int getQtyJual() {
        return QtyJual;
    }

    public String getTanggalJual() {
        return tanggalJual;
    }

    @Override
    public String toString() {
        return "IDTransaksi: " + IDTransaksi + " IDProduk: " + IDProduk + " IDUser: " + IDUser + " Nama Produk: " + namaProduk +
                " Harga Jual: " + HargaJual + " Qty Jual: " + QtyJual + " Tanggal Jual: " + tanggalJual;
    }
}

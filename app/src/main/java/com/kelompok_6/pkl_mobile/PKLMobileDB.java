package com.kelompok_6.pkl_mobile;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class PKLMobileDB {

    public static final String DATABASE_NAME = "pkl.db";
    public static final int DATABASE_VERSION = 1;

    //user
    public static final String USER_TABLE_NAME = "user";
    public static final String COLUMN_USER = "email";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_ALAMAT = "alamat";
    public static final String COLUMN_NO_HP = "nohp";
    public static final String COLUMN_TANGGAL_LAHIR = "tanggallahir";
    public static final String COLUMN_PRODUK_UNGGULAN = "produkunggulan";

    private String[] allColumnsU = { COLUMN_USER, COLUMN_NAMA, COLUMN_ALAMAT, COLUMN_NO_HP, COLUMN_TANGGAL_LAHIR, COLUMN_PRODUK_UNGGULAN };

    public static final String CREATE_TABLE_USER = "create table " + USER_TABLE_NAME + " ( " + COLUMN_USER + " text not null primary key, "
            + COLUMN_NAMA + " text not null, " + COLUMN_ALAMAT + " text not null, " + COLUMN_NO_HP + " text not null, "
            + COLUMN_TANGGAL_LAHIR + " text not null, " + COLUMN_PRODUK_UNGGULAN + " text not null);";

    //produk
    public static final String PRODUK_TABLE_NAME = "produks";
    public static final String COLUMN_ID = "_IdProduk";
    public static final String COLUMN_IDUSER = "IdUser";
    public static final String COLUMN_NAMA_PRODUK = "NamaProduk";
    public static final String COLUMN_HARGA_POKOK = "HargaPokok";
    public static final String COLUMN_HARGA_JUAL = "HargaJual";

    private String[] allColumnsP = { COLUMN_ID, COLUMN_IDUSER, COLUMN_NAMA_PRODUK, COLUMN_HARGA_POKOK, COLUMN_HARGA_JUAL };

    public static final String CREATE_TABLE_PRODUK = "create table " + PRODUK_TABLE_NAME +
            " ( " + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_IDUSER + " text not null, " +
            COLUMN_NAMA_PRODUK + " text not null, " +
            COLUMN_HARGA_POKOK + " integer not null, " +
            COLUMN_HARGA_JUAL + " integer not null);";

    //transaksi
    public static final String TRANSAKSI_TABLE_NAME = "transaksi";
    public static final String COLUMN_ID_TRANSAKSI = "_IDTransaksi";
    public static final String COLUMN_ID_PRODUK = "IDProduk";
    public static final String COLUMN_ID_USER = "IDUser";
    public static final String COLUMN_NAMA_PRODUKT = "NamaProduk";
    public static final String COLUMN_HARGA_JUALT = "HargaJual";
    public static final String COLUMN_QTY_JUAL = "QtyJual";
    public static final String COLUMN_TANGGAL_JUAL = "TanggalJual";

    private String[] allColumnsT = { COLUMN_ID_TRANSAKSI, COLUMN_ID_PRODUK, COLUMN_ID_USER, COLUMN_NAMA_PRODUKT, COLUMN_HARGA_JUALT, COLUMN_QTY_JUAL, COLUMN_TANGGAL_JUAL };

    public static final String CREATE_TABLE_TRANSAKSI =
            "create table " + TRANSAKSI_TABLE_NAME + " ( " + COLUMN_ID_TRANSAKSI + " integer primary key autoincrement, "
                    + COLUMN_ID_PRODUK + " integer not null, " + COLUMN_ID_USER + " text not null, " + COLUMN_NAMA_PRODUKT + " text not null, "
                    + COLUMN_HARGA_JUALT + " integer not null," + COLUMN_QTY_JUAL + " integer not null, " + COLUMN_TANGGAL_JUAL + " text not null);";


    private SQLiteDatabase sqlDB;
    private Context context;

    private PKLMobileDatabaseHelper pklMobileDatabaseHelper;

    public PKLMobileDB(Context ctx){
        context = ctx;
    }

    public PKLMobileDB open() throws android.database.SQLException {
        pklMobileDatabaseHelper = new PKLMobileDatabaseHelper(context);
        sqlDB = pklMobileDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        sqlDB.close();
    }

    public void addUser(String user, String nama, String alamat, String nohp, String tanggallahir, String produkunggulan){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER, user);
        contentValues.put(COLUMN_NAMA, nama);
        contentValues.put(COLUMN_ALAMAT, alamat);
        contentValues.put(COLUMN_NO_HP, nohp);
        contentValues.put(COLUMN_TANGGAL_LAHIR, tanggallahir);
        contentValues.put(COLUMN_PRODUK_UNGGULAN, produkunggulan);

        sqlDB.insert(USER_TABLE_NAME, null, contentValues);
    }

    public User getUser(String user){
        User user1 = null;
        Cursor cursor = sqlDB.query(USER_TABLE_NAME, allColumnsU, COLUMN_USER + "='"+user+"'", null, null, null, null);

        if (cursor.getCount() < 1){
            cursor.close();
            System.out.println("User not exists");
        } else {
            cursor.moveToFirst();
            user1 = cursorToUser(cursor);
            cursor.close();
            return user1;
        }
        return user1;
    }

    private User cursorToUser(Cursor cursor){
        User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5));
        return user;
    }

    public Produk addProduk(String idUser, String namaProduk, int hargaPokok, int hargaJual){
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDUSER, idUser);
        values.put(COLUMN_NAMA_PRODUK, namaProduk);
        values.put(COLUMN_HARGA_POKOK, hargaPokok);
        values.put(COLUMN_HARGA_JUAL, hargaJual);

        long insertId = sqlDB.insert(PRODUK_TABLE_NAME, null, values);

        Cursor cursor = sqlDB.query(PRODUK_TABLE_NAME, allColumnsP, COLUMN_ID + "=" + insertId, null, null, null, null);

        cursor.moveToFirst();
        Produk newProduk = cursorToProduk(cursor);
        cursor.close();
        return newProduk;
    }

    public long deleteProduk(long idToDelete){
        return sqlDB.delete(PRODUK_TABLE_NAME, COLUMN_ID + "=" + idToDelete, null);
    }

    public long updateProduk(long idToUpdate, String namaProduk, int hargaPokok, int hargaJual){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_PRODUK, namaProduk);
        values.put(COLUMN_HARGA_POKOK, hargaPokok);
        values.put(COLUMN_HARGA_JUAL, hargaJual);

        return sqlDB.update(PRODUK_TABLE_NAME, values, COLUMN_ID + "=" + idToUpdate, null);
    }

    public ArrayList<Produk> getAllProduk(String idUser) {
        ArrayList<Produk> produks = new ArrayList<Produk>();

        //Log.d("cursor", sqlDB.query(PRODUK_TABLE_NAME, allColumnsP, COLUMN_IDUSER + " = '" + idUser+"'", null, null, null, COLUMN_NAMA_PRODUK + " DESC", null).toString());
        Cursor cursor = sqlDB.query(PRODUK_TABLE_NAME, allColumnsP, COLUMN_IDUSER + " = '" + idUser+"'", null, null, null, COLUMN_NAMA_PRODUK + " DESC", null);
        if (cursor.getCount() < 1){
            return produks;
        }

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Produk produk = cursorToProduk(cursor);
            produks.add(produk);
        }

        cursor.close();

        return produks;
    }

    public Produk getProduk(String idUser, String namaProduk) {

        //Log.d("cursor", sqlDB.query(PRODUK_TABLE_NAME, allColumnsP, COLUMN_IDUSER + " = '" + idUser+"' ", null, null, null, COLUMN_NAMA_PRODUK + " DESC", null).toString());
        Cursor cursor = sqlDB.query(PRODUK_TABLE_NAME, allColumnsP, COLUMN_IDUSER + " = '" + idUser+"' and " + COLUMN_NAMA_PRODUK + " = '" + namaProduk+"'" , null, null, null, COLUMN_NAMA_PRODUK + " DESC", null);

        Produk produk;

        if (cursor.getCount() < 1){
            produk = new Produk(0,"","",0,0);
            return produk;
        }

        produk = cursorToProduk(cursor);

        cursor.close();

        return produk;
    }

    public TransaksiUser getTransaksi(long IDTransaksi)
    {
        Cursor cursor=sqlDB.query(TRANSAKSI_TABLE_NAME,allColumnsT,COLUMN_ID_TRANSAKSI+"="+IDTransaksi,null,null,null,null);
        TransaksiUser transaksiUser;

        if(cursor.getCount()<1)
        {
            transaksiUser=new TransaksiUser(0,0,"","",0,0,"");
            return transaksiUser;
        }

        transaksiUser=cursorToTransaksiUser(cursor);

        cursor.close();

        return transaksiUser;
    }


    private Produk cursorToProduk(Cursor cursor){
        Produk newProduk = new Produk(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
        return newProduk;
    }

    public TransaksiUser addTransaksi(long IDProduk, String IDUser, String namaProduk, int hargaJual, int QtyJual, String tanggalJual){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_PRODUK, IDProduk);
        values.put(COLUMN_ID_USER, IDUser);
        values.put(COLUMN_NAMA_PRODUKT, namaProduk);
        values.put(COLUMN_HARGA_JUALT, hargaJual);
        values.put(COLUMN_QTY_JUAL, QtyJual);
        values.put(COLUMN_TANGGAL_JUAL, tanggalJual);

        long insertId = sqlDB.insert(TRANSAKSI_TABLE_NAME, null, values);

        Cursor cursor = sqlDB.query(TRANSAKSI_TABLE_NAME, allColumnsT, COLUMN_ID_TRANSAKSI + "=" + insertId, null, null, null, null);

        cursor.moveToFirst();
        TransaksiUser newTransaksiUser = cursorToTransaksiUser(cursor);
        cursor.close();
        return newTransaksiUser;
    }

    public long deleteTransaksiUser(long idToDelete){
        return sqlDB.delete(TRANSAKSI_TABLE_NAME, COLUMN_ID_TRANSAKSI + "=" + idToDelete, null);
    }

    public long updateTransaksiUser(long idToUpdate, long IDProduk, String IDUser, String namaProduk, int hargaJual, int QtyJual, String tanggalJual){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_PRODUK, IDProduk);
        values.put(COLUMN_ID_USER, IDUser);
        values.put(COLUMN_NAMA_PRODUKT, namaProduk);
        values.put(COLUMN_HARGA_JUALT, hargaJual);
        values.put(COLUMN_QTY_JUAL, QtyJual);
        values.put(COLUMN_TANGGAL_JUAL, tanggalJual);

        return sqlDB.update(TRANSAKSI_TABLE_NAME, values, COLUMN_ID_TRANSAKSI + "=" + idToUpdate, null);
    }

    public ArrayList<TransaksiUser> getAllTransaksiUser(String idUser) {
        ArrayList<TransaksiUser> TransaksiUsers = new ArrayList<TransaksiUser>();

        Cursor cursor = sqlDB.query(TRANSAKSI_TABLE_NAME, allColumnsT, COLUMN_ID_USER + " = '" + idUser +"'", null, null, null, COLUMN_NAMA_PRODUKT+" ASC, "+COLUMN_TANGGAL_JUAL+" DESC", null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){
            TransaksiUser TransaksiUser = cursorToTransaksiUser(cursor);
            TransaksiUsers.add(TransaksiUser);
        }

        cursor.close();

        return TransaksiUsers;
    }

    private TransaksiUser cursorToTransaksiUser(Cursor cursor){
        TransaksiUser newTransaksi = new TransaksiUser(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6));
        return newTransaksi;
    }
}

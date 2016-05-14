package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PKLMobileDatabaseHelper  extends SQLiteOpenHelper {

    PKLMobileDatabaseHelper(Context context){
        super(context, PKLMobileDB.DATABASE_NAME, null, PKLMobileDB.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PKLMobileDB.CREATE_TABLE_USER);
        db.execSQL(PKLMobileDB.CREATE_TABLE_PRODUK);
        db.execSQL(PKLMobileDB.CREATE_TABLE_TRANSAKSI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PKLMobileDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + PKLMobileDB.USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PKLMobileDB.PRODUK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PKLMobileDB.TRANSAKSI_TABLE_NAME);
        onCreate(db);
    }
}

package com.kelompok_6.pkl_mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashOut extends Activity {

    private PKLMobileDB pklMobileDB;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashout);

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");


        TextView tv = (TextView) findViewById(R.id.textViewSplash);
        ImageView iv = (ImageView) findViewById(R.id.imageViewPKL);

        Intent intent = getIntent();
        final String sid = intent.getExtras().getString(Login.SID_KEY, "");

        final WebService webService = new WebService();

        if(Settings.placeholder==2) {
            while (DetailProduk.waitToSyncProduk.isEmpty() == false) {
                pklMobileDB.open();
                Produk temp = pklMobileDB.getProduk(username, (String) DetailProduk.waitToSyncProduk.removeFirst());
                webService.regProduk(sid, temp.getNamaProduk(), temp.getHargaPokok() + "", temp.getHargaJual() + "");
                Log.d("Register Produk", webService.getResult());
                pklMobileDB.close();
            }
            while(DetailTransaksi.waitToSyncTransaksi.isEmpty()==false)
            {
                TransaksiUser temp= (TransaksiUser) DetailTransaksi.waitToSyncTransaksi.removeFirst();
                webService.regTransaksi(sid,temp.getNamaProduk(),temp.getHargaJual()+"",temp.getQtyJual()+"",temp.getTanggalJual());
                Log.d("ADD Transaksi", webService.getResult());
            }
        }

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent i = new Intent(Intent.ACTION_MAIN)
                            .addCategory(Intent.CATEGORY_HOME)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    webService.logout(sid);
                    Log.d("Log out", webService.getResult());
                    startActivity(i);
                    finish();
                    System.exit(0);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
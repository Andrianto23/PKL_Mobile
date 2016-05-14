package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mirage on 4/24/2016.
 */
public class Rekap extends AppCompatActivity implements SensorEventListener {

    private int totalTransaksi = 0;
    private String username;
    private String sid;

    //private TransaksiUser transaksiUser;
    private PKLMobileDB pklMobileDB;
    private WebService webService;

    private TextView nomor;
    private TextView isi;
    private TextView totalHarga;

    private TextView totalTransaksis;
    private TextView totalTransaksiText;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Rekap");

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");
        sid = intent.getExtras().getString(Login.SID_KEY, "");

        totalTransaksis = (TextView) findViewById(R.id.totalTransaksi);
        nomor = (TextView) findViewById(R.id.Nomor);
        isi = (TextView) findViewById(R.id.isi);
        totalHarga = (TextView) findViewById(R.id.totalHarga);
        totalTransaksiText = (TextView) findViewById(R.id.totalTransaksiText);

        totalTransaksis.setText("");
        nomor.setText("");
        isi.setText("");
        totalHarga.setText("");
        totalTransaksiText.setText("");

        webService = new WebService();

        if (webService.isConnectedToInternet(getBaseContext())) {
            if (equationOfData()){
                Log.d("Create Rekap", "create");
                this.createRekap();
            }
        } else {
            this.createRekap();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rekap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_transaksi) {
            Intent i = new Intent(Rekap.this, Transaksi.class);
            i.putExtra(Login.SID_KEY, sid);
            i.putExtra(Login.USER_KEY, username);
            startActivity(i);
            return true;
        } else if(id == R.id.action_katalog){
            Intent i = new Intent(Rekap.this, Katalog.class);
            i.putExtra(Login.SID_KEY, sid);
            i.putExtra(Login.USER_KEY, username);
            startActivity(i);
            return true;
        } else if (id == R.id.action_keluar) {
            Intent i = new Intent(Rekap.this, SplashOut.class);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            //Log.d("Sensor Light lux", lux+"");
            if (lux == 0.0) {
                Log.d("cahaya", lux + "");
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 0;
                getWindow().setAttributes(params);

            } else {
                Log.d("cahaya", lux + "");
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = -1;
                getWindow().setAttributes(params);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void createRekap() {
        totalTransaksis = (TextView) findViewById(R.id.totalTransaksi);
        nomor = (TextView) findViewById(R.id.Nomor);
        isi = (TextView) findViewById(R.id.isi);
        totalHarga = (TextView) findViewById(R.id.totalHarga);
        totalTransaksiText = (TextView) findViewById(R.id.totalTransaksiText);

        pklMobileDB = new PKLMobileDB(getBaseContext());
        pklMobileDB.open();
        ArrayList<TransaksiUser> transaksiUsers = pklMobileDB.getAllTransaksiUser(username);
        pklMobileDB.close();
        String nomorText = "";
        String isiText = "";
        String totalHargaText = "";
        int totalPenjualanSingle = 0;

        for (int i = 0; i < transaksiUsers.size(); i++ ){
            totalPenjualanSingle = transaksiUsers.get(i).getQtyJual()*transaksiUsers.get(i).getHargaJual();
            totalTransaksi += totalPenjualanSingle;
            nomorText+=(i+1)+".\n";
            isiText+= transaksiUsers.get(i).getTanggalJual() + " " + transaksiUsers.get(i).getNamaProduk() +
                    " " + transaksiUsers.get(i).getQtyJual() + "x" + transaksiUsers.get(i).getHargaJual()+ "\n";
            totalHargaText += "Rp."+totalPenjualanSingle+",-\n";
        }
        nomor.setText(nomorText);
        isi.setText(isiText);
        totalHarga.setText(totalHargaText);
        totalTransaksiText.setText("Total Transaksi");
        totalTransaksis.setText("Rp."+this.totalTransaksi+",-");
    }

    public boolean equationOfData(){
        webService.getTransaksi(sid, "20160101");
        String result = webService.getResult();
        boolean samedata = false;

        String[] hasilSplit = result.split("[^a-zA-Z 0-9]");
        ArrayList<String> datasplit = new ArrayList<String>();
        int count = 0;
        for (int i=0; i< hasilSplit.length; i++){
            Log.d("Data Transaksi", hasilSplit[i]);
            if (!hasilSplit[i].equals("")) {
                datasplit.add(hasilSplit[i]);
                count++;
            }
        }

        Log.d("hitung split", count+"");

        pklMobileDB = new PKLMobileDB(getBaseContext());
        pklMobileDB.open();
        ArrayList<TransaksiUser> transaksiUsers = pklMobileDB.getAllTransaksiUser(username);
        pklMobileDB.close();

        int j=transaksiUsers.size()-1;
        for (int i=0; i < count; i+=4){
            Log.d("J", j+"");
            Log.d("Data Transaksi", datasplit.get(i)+" "+datasplit.get(i+1)+" "+datasplit.get(i+2)+" "+datasplit.get(i+3));
            Log.d("Data Transaksi DB", transaksiUsers.get(j).getNamaProduk()+" "+transaksiUsers.get(j).getHargaJual()+" "+
                    transaksiUsers.get(j).getQtyJual()+" "+transaksiUsers.get(j).getTanggalJual());
            if (transaksiUsers.get(j).getNamaProduk().equals(datasplit.get(i))){
                Log.d("Sama", "Sama");
                String hargaJual = transaksiUsers.get(j).getHargaJual()+"";
                if (hargaJual.equals(datasplit.get(i+1))){
                    Log.d("Sama", "Sama");
                    String qtyJual = transaksiUsers.get(j).getQtyJual()+"";
                    if (qtyJual.equals(datasplit.get(i+2))){
                        Log.d("Sama", "Sama");
                        if (transaksiUsers.get(j).getTanggalJual().equals(datasplit.get(i+3))){
                            Log.d("Sama", "Sama");
                            samedata = true;
                        } else {
                            samedata = false;
                        }
                    } else {
                        samedata = false;
                    }
                }
                else {
                    samedata = false;
                }
            }
            else {
                samedata = false;
            }
            j--;
        }

        Log.d("Same Data", samedata+"");

        return samedata;
    }
}

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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mirage on 4/24/2016.
 */
public class Transaksi extends AppCompatActivity implements SensorEventListener {

    private String username;
    private String sid;

    private Produk produk;
    private PKLMobileDB pklMobileDB;

    private ArrayList<Produk> produks;
    private ArrayList<String> produksWeb = new ArrayList<String>();
    private ProdukAdapter produkAdapter;

    private ListView listview;

    private WebService webService;

    boolean dataSame = false;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Transaksi");

        TextView textViewTransaksi = (TextView) findViewById(R.id.textViewTransaksi);

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");
        sid = intent.getExtras().getString(Login.SID_KEY, "");

        textViewTransaksi.setText("TRANSAKSI PENJUALAN PKL\n"+username);

        webService = new WebService();

        listview = (ListView) findViewById(R.id.listTransaksi);

        pklMobileDB = new PKLMobileDB(getBaseContext());
        pklMobileDB.open();

        produks = pklMobileDB.getAllProduk(username);
        pklMobileDB.close();

        if (webService.isConnectedToInternet(getBaseContext())) {
            if (webService.getKatalog(sid)) {
                Log.d("get Katalog", webService.getResult());
                String hasil = webService.getResult();
                String temp="";
                if (!hasil.equals("")) {
                    for (int i = 0; i < hasil.length(); i++) {
                        if (hasil.charAt(i) != '(' && hasil.charAt(i) != '"' && hasil.charAt(i) != ',' && hasil.charAt(i) != ')') {
                            temp+= hasil.charAt(i);
                        }
                        if (hasil.charAt(i) == ')') {
                            Log.d("hasil", temp);
                            produksWeb.add(temp);
                            temp = "";
                        }
                    }
                }
            }
            if (produks != null) {

                if (produks.size() == produksWeb.size()) {
                    for (int i = 0; i < produks.size(); i++) {
                        if (produks.get(i).getNamaProduk().equals(produksWeb.get(i))) {
                            Log.d("Daftar Katalog", produksWeb.get(i));
                            dataSame = true;
                        } else {
                            dataSame = false;
                        }
                    }
                }

                if (dataSame) {

                    produkAdapter = new ProdukAdapter(getBaseContext(), produks);

                    listview.setAdapter(produkAdapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            produk = produkAdapter.getItem(position);

                            webService.getProduk(sid, produk.getNamaProduk());
                            String result = webService.getResult();
                            String hasil = "";
                            String[] arrayDProduk = new String[3];
                            int j = 0;
                            if (!result.equals("")) {
                                for (int i = 0; i < result.length(); i++) {
                                    if (result.charAt(i) != '(' && result.charAt(i) != '"' && result.charAt(i) != ',' && result.charAt(i) != ')') {
                                        hasil += result.charAt(i);
                                    }
                                    if (result.charAt(i) == ')' || result.charAt(i) == ',') {
                                        arrayDProduk[j] = hasil;
                                        j++;
                                        hasil = "";
                                    }
                                }
                            }

                            Log.d("Detail Produk", arrayDProduk[0]+" "+ arrayDProduk[1]+ " " + arrayDProduk[2]);
                            Log.d("Detail Produk db", produk.getNamaProduk()+" "+ produk.getHargaPokok()+ " " + produk.getHargaJual());

                            if (arrayDProduk[0].equals(produk.getNamaProduk()) && arrayDProduk[1].equals(produk.getHargaPokok()+"") && arrayDProduk[2].equals(produk.getHargaJual()+"")) {

                                Intent i = new Intent(Transaksi.this, DetailTransaksi.class);
                                i.putExtra(Login.USER_KEY, username);
                                i.putExtra(Login.SID_KEY, sid);
                                i.putExtra(Katalog.ID_PRODUK, produk.getIDProduk());
                                i.putExtra(Katalog.NAMA_PRODUK, produk.getNamaProduk());
                                i.putExtra(Katalog.HARGA_JUAL, produk.getHargaJual());
                                i.putExtra(Katalog.UPDATE_PRODUK, true);
                                startActivity(i);

                            }
                        }
                    });
                }
            }
        } else {
            if (produks != null) {
                produkAdapter = new ProdukAdapter(getBaseContext(), produks);

                listview.setAdapter(produkAdapter);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        produk = produkAdapter.getItem(position);

                        Intent i = new Intent(Transaksi.this, DetailTransaksi.class);
                        i.putExtra(Login.USER_KEY, username);
                        i.putExtra(Login.SID_KEY, sid);
                        i.putExtra(Katalog.ID_PRODUK, produk.getIDProduk());
                        i.putExtra(Katalog.NAMA_PRODUK, produk.getNamaProduk());
                        i.putExtra(Katalog.HARGA_JUAL, produk.getHargaJual());
                        i.putExtra(Katalog.UPDATE_PRODUK, true);
                        startActivity(i);
                    }
                });
            }
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_transaksi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rekap) {
            Intent i = new Intent(Transaksi.this, Rekap.class);
            i.putExtra(Login.USER_KEY, username);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        } else if(id == R.id.action_katalog){
            Intent i = new Intent(Transaksi.this, Katalog.class);
            i.putExtra(Login.USER_KEY, username);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        } else if (id == R.id.action_keluar) {
            Intent i = new Intent(Transaksi.this, SplashOut.class);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

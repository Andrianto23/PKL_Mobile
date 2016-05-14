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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Katalog extends AppCompatActivity implements SensorEventListener {

    public static final String UPDATE_PRODUK = "updateproduk";
    public static final String ID_PRODUK = "IdProduk";
    public static final String NAMA_PRODUK = "nama_produk";
    public static final String HARGA_POKOK = "harga_pokok";
    public static final String HARGA_JUAL = "harga_jual";

    private String username;

    private Produk produk;
    private Produk produkWeb;
    private PKLMobileDB pklMobileDB;

    private ArrayList<Produk> produks;
    private ArrayList<String> produksWeb = new ArrayList<String>();
    private ProdukAdapter produkAdapter;
    private String sid;

    private ListView listview;

    private WebService webService;

    boolean dataSame = false;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Katalog");

        TextView textView = (TextView) findViewById(R.id.textViewKatalog);

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");
        sid = intent.getExtras().getString(Login.SID_KEY, "");
        Log.d("SID", sid);
        textView.setText("KATALOG PRODUK PKL \n " + username);

        webService = new WebService();

        listview = (ListView) findViewById(R.id.listView);

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

                                Intent i = new Intent(Katalog.this, DetailProduk.class);
                                i.putExtra(Login.USER_KEY, username);
                                i.putExtra(Login.SID_KEY, sid);
                                i.putExtra(ID_PRODUK, produk.getIDProduk());
                                i.putExtra(NAMA_PRODUK, produk.getNamaProduk());
                                i.putExtra(HARGA_POKOK, produk.getHargaPokok());
                                i.putExtra(HARGA_JUAL, produk.getHargaJual());
                                i.putExtra(UPDATE_PRODUK, true);
                                startActivity(i);

                            } else {
                                Log.d("Status same data", "beda");
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

                        Intent i = new Intent(Katalog.this, DetailProduk.class);
                        i.putExtra(Login.USER_KEY, username);
                        i.putExtra(Login.SID_KEY, sid);
                        i.putExtra(ID_PRODUK, produk.getIDProduk());
                        i.putExtra(NAMA_PRODUK, produk.getNamaProduk());
                        i.putExtra(HARGA_POKOK, produk.getHargaPokok());
                        i.putExtra(HARGA_JUAL, produk.getHargaJual());
                        i.putExtra(UPDATE_PRODUK, true);
                        startActivity(i);
                    }
                });
            }
        }

        registerForContextMenu(listview);
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
        getMenuInflater().inflate(R.menu.menu_katalog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.long_press_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_transaksi) {
            Intent i = new Intent(Katalog.this, Transaksi.class);
            i.putExtra(Login.USER_KEY, username);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        } else if(id == R.id.action_tambah){
            Intent i = new Intent(Katalog.this, DetailProduk.class);
            i.putExtra(Login.USER_KEY, username);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        } else if (id == R.id.action_keluar) {
            Intent i = new Intent(Katalog.this, SplashOut.class);
            i.putExtra(Login.SID_KEY, sid);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = info.position;
        Produk produk = (Produk) listview.getAdapter().getItem(rowPosition);

        switch (item.getItemId()){
            case R.id.delete:
                pklMobileDB.open();
                pklMobileDB.deleteProduk(produk.getIDProduk());
                if (webService.isConnectedToInternet(getBaseContext())) {
                    webService.delProduk(sid, produk.getNamaProduk());
                }

                produks.clear();
                produks.addAll(pklMobileDB.getAllProduk(username));
                produkAdapter.notifyDataSetChanged();

                pklMobileDB.close();
        }
        return super.onContextItemSelected(item);
    }
}

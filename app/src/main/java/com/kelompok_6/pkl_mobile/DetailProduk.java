package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Deque;

/**
 * Created by GG.Sunshiners on 4/24/2016.
 */

public class DetailProduk extends AppCompatActivity implements SensorEventListener {

    public static Deque waitToSyncProduk;
    private String username;

    private EditText edNamaProduk;
    private EditText edHargaPokok;
    private EditText edHargaJual;

    private Produk produk;
    private PKLMobileDB pklMobileDB;

    private boolean update_produk = false;
    private long idProduk = 0;

    private String sid;

    private WebService webService;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_details);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Detail Produk");

        edNamaProduk = (EditText) findViewById(R.id.editTextNamaProduk);
        edHargaPokok = (EditText) findViewById(R.id.editTextHargaPokok);
        edHargaJual = (EditText) findViewById(R.id.editTextHargaJual);

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");
        sid =intent.getExtras().getString(Login.SID_KEY, "");
        edNamaProduk.setText(intent.getExtras().getString(Katalog.NAMA_PRODUK,""));
        if (intent.getExtras().getInt(Katalog.HARGA_POKOK) == 0){
            edHargaPokok.setText("");
        } else {
            edHargaPokok.setText(((Integer) intent.getExtras().getInt(Katalog.HARGA_POKOK)).toString());
        }
        if (intent.getExtras().getInt(Katalog.HARGA_JUAL) == 0){
            edHargaJual.setText("");
        } else {
            edHargaJual.setText(((Integer) intent.getExtras().getInt(Katalog.HARGA_JUAL)).toString());
        }
        update_produk = intent.getExtras().getBoolean(Katalog.UPDATE_PRODUK, false);
        idProduk = intent.getExtras().getLong(Katalog.ID_PRODUK, 0);

        pklMobileDB = new PKLMobileDB(getBaseContext());

        webService = new WebService();
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_prod_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        edNamaProduk = (EditText) findViewById(R.id.editTextNamaProduk);
        edHargaPokok = (EditText) findViewById(R.id.editTextHargaPokok);
        edHargaJual = (EditText) findViewById(R.id.editTextHargaJual);

        String namaProduk;
        int hargaPokok;
        int hargaJual;

        Intent i;
        switch (item.getItemId())
        {
            case R.id.action_tambah: //PLACEHOLDER
                namaProduk = edNamaProduk.getText().toString();
                if (edHargaPokok.getText().toString().equals("")){
                    hargaPokok = 0;
                } else {
                    hargaPokok = Integer.parseInt(edHargaPokok.getText().toString());
                }
                if (edHargaJual.getText().toString().equals("")){
                    hargaJual = 0;
                } else {
                    hargaJual = Integer.parseInt(edHargaJual.getText().toString());
                }

                if (!DPFieldValidation(namaProduk, hargaPokok, hargaJual)) {
                    pklMobileDB.open();
                    if (webService.isConnectedToInternet(getBaseContext())) {
                        if (update_produk == false) {
                            if (pklMobileDB.getProduk(username, namaProduk).getNamaProduk().equals("")) {
                                pklMobileDB.addProduk(username, namaProduk, hargaPokok, hargaJual);
                                webService.regProduk(sid, namaProduk, hargaPokok + "", hargaJual + "");
                                Log.d("Register Produk", webService.getResult());
                            }
                        } else {
                            pklMobileDB.updateProduk(idProduk, namaProduk, hargaPokok, hargaJual);
                            webService.regProduk(sid, namaProduk, hargaPokok + "", hargaJual + "");
                        }
                    }
                    pklMobileDB.close();

                    i = new Intent(getBaseContext(), DetailProduk.class);
                    i.putExtra(Login.USER_KEY, username);
                    i.putExtra(Login.SID_KEY, sid);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(),"Field Nama Produk, Harga Pokok, atau Harga jual masih ada yang kosong",Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_simpan: //PLACEHOLDER
                namaProduk = edNamaProduk.getText().toString();
                if (edHargaPokok.getText().toString().equals("")){
                    hargaPokok = 0;
                } else {
                    hargaPokok = Integer.parseInt(edHargaPokok.getText().toString());
                }
                if (edHargaJual.getText().toString().equals("")){
                    hargaJual = 0;
                } else {
                    hargaJual = Integer.parseInt(edHargaJual.getText().toString());
                }

                if (DPFieldValidation(namaProduk, hargaPokok, hargaJual) == false) {
                    pklMobileDB.open();
                    if (webService.isConnectedToInternet(getBaseContext())) {
                        if (update_produk == false) {
                            if (pklMobileDB.getProduk(username, namaProduk).getNamaProduk().equals("")) {
                                pklMobileDB.addProduk(username, namaProduk, hargaPokok, hargaJual);
                                webService.regProduk(sid, namaProduk, hargaPokok + "", hargaJual + "");
                                Log.d("Register Produk", webService.getResult());
                            }
                        } else {
                            pklMobileDB.updateProduk(idProduk, namaProduk, hargaPokok, hargaJual);
                            webService.regProduk(sid, namaProduk, hargaPokok + "", hargaJual + "");
                        }
                    }
                    pklMobileDB.close();
                    i = new Intent(DetailProduk.this,Katalog.class);
                    i.putExtra(Login.USER_KEY, username);
                    i.putExtra(Login.SID_KEY, sid);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(),"Field Nama Produk, Harga Pokok, atau Harga jual masih ada yang kosong",Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_kembali:
                i = new Intent(DetailProduk.this,Katalog.class);
                i.putExtra(Login.USER_KEY, username);
                i.putExtra(Login.SID_KEY, sid);
                startActivity(i);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean DPFieldValidation(String namaProduk, int hargaPokok, int hargaJual){
        if (namaProduk.equals("")){
            return true;
        } else if (hargaPokok == 0){
            return true;
        } else if (hargaJual == 0){
            return true;
        } else {
            return false;
        }
    }

    //Integrasi button default phone menyusul.

}

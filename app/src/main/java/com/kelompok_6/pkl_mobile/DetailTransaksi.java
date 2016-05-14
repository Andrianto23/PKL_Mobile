package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Deque;
import java.util.Locale;

/**
 * Created by GG.Sunshiners on 4/24/2016.
 */


public class DetailTransaksi extends AppCompatActivity implements SensorEventListener {

    public static Deque waitToSyncTransaksi;
    CountDownTimer cdt = null;
    private String username;
    private String tanggal;
    private String sid;

    private EditText edNamaProduk;
    private EditText edHargaJual;
    private EditText edKuantitas;

    private TransaksiUser transaksiUser;
    private PKLMobileDB pklMobileDB;

    private boolean update_produk = false;
    private long idProduk = 0;

    private SimpleDateFormat dateFormat;

    private WebService webService;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_details);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Transaksi Penjualan");

        Button btnBatal = (Button) findViewById(R.id.buttonBatal);
        Button btnProses = (Button) findViewById(R.id.buttonProses);

        edNamaProduk = (EditText) findViewById(R.id.editTextNamaProduk);
        edHargaJual = (EditText) findViewById(R.id.editTextHargaJual);
        edKuantitas = (EditText) findViewById(R.id.editTextKuantitas);

        Intent intent = getIntent();
        username = intent.getExtras().getString(Login.USER_KEY, "");
        sid = intent.getExtras().getString(Login.SID_KEY, "");
        Log.d("sid", sid);
        edNamaProduk.setText(intent.getExtras().getString(Katalog.NAMA_PRODUK,""));
        if (intent.getExtras().getInt(Katalog.HARGA_JUAL) == 0){
            edHargaJual.setText("");
        } else {
            edHargaJual.setText(((Integer) intent.getExtras().getInt(Katalog.HARGA_JUAL)).toString());
        }
        edKuantitas.setText("");
        update_produk = intent.getExtras().getBoolean(Katalog.UPDATE_PRODUK, false);
        idProduk = intent.getExtras().getLong(Katalog.ID_PRODUK, 0);

        tanggal = this.setTanggal();
        Log.d("Tanggal", tanggal);

        pklMobileDB = new PKLMobileDB(getBaseContext());

        webService = new WebService();

        //PLACEHOLDER
        if (btnProses != null) {
            btnProses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String namaproduk = edNamaProduk.getText().toString();
                    int hargaJual = Integer.parseInt(edHargaJual.getText().toString());
                    int kuantitas;
                    if (edKuantitas.getText().toString().equals("")){
                        kuantitas=0;
                    } else {
                        kuantitas = Integer.parseInt(edKuantitas.getText().toString());
                    }

                    if(kuantitas != 0) {
                        if (webService.isConnectedToInternet(getBaseContext())) {
                            webService.regTransaksi(sid, namaproduk, hargaJual+"", kuantitas+"", tanggal);
                            Log.d("ADD Transaksi", webService.getResult());
                            pklMobileDB.open();
                            pklMobileDB.addTransaksi(idProduk, username, namaproduk, hargaJual, kuantitas, tanggal);
                            pklMobileDB.close();

                            final Toast toastBerhasilProses = Toast.makeText(getBaseContext(), "Terima kasih anda telah bertransaksi produk " + namaproduk + " sebanyak " + kuantitas + " seharga " + (kuantitas * hargaJual), Toast.LENGTH_SHORT);
                            toastBerhasilProses.show();

                            cdt = new CountDownTimer(3000, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    toastBerhasilProses.show();
                                }

                                @Override
                                public void onFinish() {
                                    Intent i = new Intent(DetailTransaksi.this, Transaksi.class);
                                    i.putExtra(Login.USER_KEY, username);
                                    i.putExtra(Login.SID_KEY, sid);
                                    startActivity(i);
                                }
                            }.start();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Field kuantitas masih kosong, silahkan isi jumlah transaksi lebih dari 0", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        if (btnBatal != null) {
            btnBatal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailTransaksi.this,Transaksi.class);
                    i.putExtra(Login.USER_KEY, username);
                    i.putExtra(Login.SID_KEY, sid);
                    startActivity(i);
                }
            });
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


    private String setTanggal(){
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    //Integrasi button default phone menyusul.

}

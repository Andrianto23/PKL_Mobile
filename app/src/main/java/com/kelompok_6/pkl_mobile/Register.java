package com.kelompok_6.pkl_mobile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity implements SensorEventListener {

    private EditText username;
    private EditText nama;
    private EditText alamat;
    private EditText noHp;
    private EditText tanggalLahir;
    private EditText produkUnggulan;

    private DatePickerDialog dpd;

    private SimpleDateFormat dateFormat;

    private PKLMobileDB PKLMobileDB;

    CountDownTimer cdt = null;

    private WebService webService;

    private SensorManager sensorManager;
    private Sensor sensorLight;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        setTitle("Register");

        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);

        Button btnBatal = (Button) findViewById(R.id.buttonBatal);
        Button btnSimpan = (Button) findViewById(R.id.buttonProses);

        username = (EditText) findViewById(R.id.editTextEmail);
        nama = (EditText) findViewById(R.id.editTextNama);
        alamat = (EditText) findViewById(R.id.editTextAlamat);
        noHp = (EditText) findViewById(R.id.editTextNoHp);
        tanggalLahir = (EditText) findViewById(R.id.ediTextTtl);
        tanggalLahir.setInputType(InputType.TYPE_NULL);
        tanggalLahir.requestFocus();
        produkUnggulan = (EditText) findViewById(R.id.editTextPU);

        setTanggalLahir();

        PKLMobileDB = new PKLMobileDB(getBaseContext());
        PKLMobileDB.open();

        webService = new WebService();

        if (btnBatal != null) {
            btnBatal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Register.this, Login.class);
                    startActivity(i);
                }
            });
        }

        if (btnSimpan != null) {
            btnSimpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.d("Register", "Username:" + username.getText().toString() + "Nama Lengkap: " + nama.getText().toString() + "Alamat: " +
                            alamat.getText().toString() + " No.Hp: " + noHp.getText().toString() + " Tanggal Lahir: " + tanggalLahir.getText().toString()
                            + " Produk Unggulan: " + produkUnggulan.getText().toString());

                    if (registerValidation(username.getText().toString(), nama.getText().toString(), alamat.getText().toString(),
                            noHp.getText().toString(), tanggalLahir.getText().toString(), produkUnggulan.getText().toString())) {
                        Toast.makeText(getBaseContext(), "Masih Ada Field yang kosong atau Format No.Hp yang anda masukkan masih salah",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        Log.d("InternetConn", webService.isConnectedToInternet(getBaseContext())+"");
                        if (webService.isConnectedToInternet(getBaseContext())) {
                            if(!webService.login(username.getText().toString(),tanggalLahir.getText().toString())) {
                                webService.registerPKL(username.getText().toString(), nama.getText().toString(), alamat.getText().toString(),
                                        noHp.getText().toString(), tanggalLahir.getText().toString(), produkUnggulan.getText().toString());
                                Log.d("result register", webService.getResult());
                                final Toast toastBerhasilRegister = Toast.makeText(getBaseContext(), R.string.bregister, Toast.LENGTH_SHORT);
                                toastBerhasilRegister.show();

                                PKLMobileDB.addUser(username.getText().toString(), nama.getText().toString(), alamat.getText().toString(),
                                        noHp.getText().toString(), tanggalLahir.getText().toString(), produkUnggulan.getText().toString());

                                cdt = new CountDownTimer(3000, 100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        toastBerhasilRegister.show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        Intent i = new Intent(Register.this, Login.class);
                                        startActivity(i);
                                    }
                                }.start();
                            } else {
                                Log.d("result login", webService.getResult());
                                String sid = webService.getResult();
//                                webService.getPKL(webService.getResult());
//                                Log.d("Data PKL", webService.getResult());
//                                String result = webService.getResult();
//                                String[] hasilSplit = result.split("[^a-zA-Z 0-9@.]");
//                                ArrayList<String> dataPKL = new ArrayList<String>();
//                                int count = 0;
//                                for (int i=0; i< hasilSplit.length; i++){
//                                    Log.d("Data Transaksi", hasilSplit[i]);
//                                    if (!hasilSplit[i].equals("")) {
//                                        dataPKL.add(hasilSplit[i]);
//                                        count++;
//                                    }
//                                }
//                                PKLMobileDB.addUser(dataPKL.get(0),dataPKL.get(1),dataPKL.get(2), dataPKL.get(3), dataPKL.get(4),dataPKL.get(5));
//
//                                Log.d("hitung split", count+"");
                                webService.logout(sid);
                                Log.d("result logout", webService.getResult());
                                Toast.makeText(getBaseContext(),"User sudah terdaftar",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PKLMobileDB.close();
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

    private void setTanggalLahir(){
        tanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        dpd = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,monthOfYear,dayOfMonth);
                tanggalLahir.setText(dateFormat.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    private boolean registerValidation(String user, String nama, String alamat, String nohp, String tanggallahir, String produkunggulan){
        if (user.equals("")){
            Toast.makeText(getBaseContext(),"Masukkan Username dengan format email",Toast.LENGTH_SHORT).show();
            return true;
        } else if (nama.equals("")){
            Toast.makeText(getBaseContext(),"Masukkan Nama Lengkap",Toast.LENGTH_SHORT).show();
            return true;
        } else if (alamat.equals("")){
            Toast.makeText(getBaseContext(),"Masukkan Alamat Lapak",Toast.LENGTH_SHORT).show();
            return true;
        } else if (nohp.equals("")){
            Toast.makeText(getBaseContext(),"Masukkan No.HP dengan diikuti kode indonesia yaitu 62",Toast.LENGTH_SHORT).show();
            return true;
        } else if (nohp.charAt(0)!='6' && nohp.charAt(1) != '2'){
            Toast.makeText(getBaseContext(),"Format No.HP yang anda masukkan salah harus diawali dengan kode negara yaitu 62",Toast.LENGTH_SHORT).show();
            return true;
        } else if (tanggallahir.equals("")){
            Toast.makeText(getBaseContext(),"Masukkan Tanggal Lahir",Toast.LENGTH_SHORT).show();
            return true;
        } else if (produkunggulan.equals("")) {
            Toast.makeText(getBaseContext(),"Masukkan Produk Unggulan",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

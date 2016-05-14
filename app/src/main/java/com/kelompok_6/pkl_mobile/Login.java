package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Login extends AppCompatActivity implements SensorEventListener{

    public static final String USER_KEY = "com.kelompok_6.pkl_mobile.user";
    public static final String SID_KEY = "com.kelompok_6.pkl_mobile.sid";
    public static final String SYNC_KEY = "com.kelompok_6.pkl_mobile.sync";
    public static final String INTERVAL_VALUE = "com.kelompok_6.pkl_mobile.intervalvalue";

    public enum GET_SYNC_LAUNCH { INTERVAL, LOGINLOGOUT, DIRECTLY }

    private EditText edUsername;
    private EditText password;

    CountDownTimer cdt = null;

    private PKLMobileDB pklMobileDB;
    private User user;

    private WebService webService;
    private String sid;

    private SensorManager sensorManager;
    private Sensor sensorLight;
    private Sensor sensorAccel;
    private Sensor sensorStepCounter;
    private Sensor sensorStepDetector;

    public Vibrator v;

    private float lastX, lastY, lastZ;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

    private TextView step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        step = (TextView) findViewById(R.id.step);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mList= sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 1; i < mList.size(); i++) {
            Log.d("Sensor", "\n" + mList.get(i).getName() + "\n" + mList.get(i).getVendor() + "\n" + mList.get(i).getVersion());
        }

        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrateThreshold = sensorAccel.getMaximumRange()/2;
        sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setTitle("Login");

        Button btnLogin = (Button) findViewById(R.id.buttonLogin);
        Button btnRegister = (Button) findViewById(R.id.buttonRegister);

        edUsername = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextpassword);

        pklMobileDB = new PKLMobileDB(getBaseContext());
        pklMobileDB.open();

        webService = new WebService();

        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Login.this, Register.class);
                    startActivity(i);
                }
            });
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("InternetConn", webService.isConnectedToInternet(getBaseContext())+"");
                    if (webService.isConnectedToInternet(getBaseContext())) {
                        Log.d("Internet", webService.login(edUsername.getText().toString(), password.getText().toString()) + "");
                        if (webService.login(edUsername.getText().toString(), password.getText().toString())) {
                            Log.d("Login Web Service", webService.getResult());
                            sid = webService.getResult();
                            Log.d("Login", edUsername.getText().toString());
                            user = pklMobileDB.getUser(edUsername.getText().toString());
                            //pklMobileDB.close();
                            if (user != null) {
                                if (login(user.getUser(), user.getTanggallahir())) {

                                    final Toast toastBerhasilLogin = Toast.makeText(getBaseContext(), R.string.blogin, Toast.LENGTH_SHORT);
                                    toastBerhasilLogin.show();
                                    if(Settings.placeholder==2) {
                                        while (DetailProduk.waitToSyncProduk.isEmpty() == false) {
                                            Produk temp = pklMobileDB.getProduk(user.getUser(), (String) DetailProduk.waitToSyncProduk.removeFirst());
                                            webService.regProduk(sid, temp.getNamaProduk(), temp.getHargaPokok() + "", temp.getHargaJual() + "");
                                            Log.d("Register Produk", webService.getResult());
                                        }
                                        while(DetailTransaksi.waitToSyncTransaksi.isEmpty()==false)
                                        {
                                            TransaksiUser temp= (TransaksiUser) DetailTransaksi.waitToSyncTransaksi.removeFirst();
                                            webService.regTransaksi(sid,temp.getNamaProduk(),temp.getHargaJual()+"",temp.getQtyJual()+"",temp.getTanggalJual());
                                            Log.d("ADD Transaksi", webService.getResult());
                                        }
                                    }
                                    pklMobileDB.close();
                                    cdt = new CountDownTimer(3000, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            toastBerhasilLogin.show();
                                        }

                                        @Override
                                        public void onFinish() {
                                            Intent i = new Intent(Login.this, Katalog.class);
                                            i.putExtra(USER_KEY, user.getUser());
                                            i.putExtra(SID_KEY, sid);

                                            startActivity(i);
                                        }
                                    }.start();
                                } else {
                                    final Toast toastGagalLogin = Toast.makeText(getBaseContext(), R.string.glogin, Toast.LENGTH_SHORT);
                                    toastGagalLogin.show();

                                    cdt = new CountDownTimer(3000, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            toastGagalLogin.show();
                                        }

                                        @Override
                                        public void onFinish() {
                                            toastGagalLogin.cancel();
                                        }
                                    }.start();
                                }
                            } else {
                                final Toast toastGagalLogin = Toast.makeText(getBaseContext(), R.string.glogin, Toast.LENGTH_SHORT);
                                toastGagalLogin.show();

                                cdt = new CountDownTimer(3000, 100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        toastGagalLogin.show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        toastGagalLogin.cancel();
                                    }
                                }.start();
                            }
                        } else {
                            final Toast toastGagalLogin = Toast.makeText(getBaseContext(), R.string.glogin, Toast.LENGTH_SHORT);
                            toastGagalLogin.show();

                            cdt = new CountDownTimer(3000, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    toastGagalLogin.show();
                                }

                                @Override
                                public void onFinish() {
                                    toastGagalLogin.cancel();
                                }
                            }.start();
                        }
                    } else {
                        Log.d("Login", edUsername.getText().toString());
                        user = pklMobileDB.getUser(edUsername.getText().toString());
                        pklMobileDB.close();
                        if (user != null) {
                            if (login(user.getUser(), user.getTanggallahir())) {
                                final Toast toastBerhasilLogin = Toast.makeText(getBaseContext(), R.string.blogin, Toast.LENGTH_SHORT);
                                toastBerhasilLogin.show();
                                if(Settings.placeholder==2) {
                                    while (DetailProduk.waitToSyncProduk.isEmpty() == false) {
                                        Produk temp = pklMobileDB.getProduk(user.getUser(), (String) DetailProduk.waitToSyncProduk.removeFirst());
                                        webService.regProduk(sid, temp.getNamaProduk(), temp.getHargaPokok() + "", temp.getHargaJual() + "");
                                        Log.d("Register Produk", webService.getResult());
                                    }
                                    while(DetailTransaksi.waitToSyncTransaksi.isEmpty()==false)
                                    {
                                        TransaksiUser temp= pklMobileDB.getTransaksi((long) DetailTransaksi.waitToSyncTransaksi.removeFirst());
                                        webService.regTransaksi(sid,temp.getNamaProduk(),temp.getHargaJual()+"",temp.getQtyJual()+"",temp.getTanggalJual());
                                        Log.d("ADD Transaksi", webService.getResult());
                                    }
                                }
                                pklMobileDB.close();

                                cdt = new CountDownTimer(3000, 100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        toastBerhasilLogin.show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        Intent i = new Intent(Login.this, Katalog.class);
                                        i.putExtra(USER_KEY, user.getUser());
                                        startActivity(i);
                                    }
                                }.start();
                            } else {
                                final Toast toastGagalLogin = Toast.makeText(getBaseContext(), R.string.glogin, Toast.LENGTH_SHORT);
                                toastGagalLogin.show();

                                cdt = new CountDownTimer(3000, 100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        toastGagalLogin.show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        toastGagalLogin.cancel();
                                    }
                                }.start();
                            }
                        } else {
                            final Toast toastGagalLogin = Toast.makeText(getBaseContext(), R.string.glogin, Toast.LENGTH_SHORT);
                            toastGagalLogin.show();

                            cdt = new CountDownTimer(3000, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    toastGagalLogin.show();
                                }

                                @Override
                                public void onFinish() {
                                    toastGagalLogin.cancel();
                                }
                            }.start();
                        }
                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent i = new Intent(Login.this, Settings.class);
                startActivity(i);
                return true;
            case R.id.exit:
                Intent i1 = new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);
                finish();
                System.exit(0);
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
        } else
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY= Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);
            Log.d("Accel", deltaX+" "+deltaY+" "+deltaZ);

            if (deltaX < 2){
                deltaX = 0;
            }
            if (deltaY < 2){
                deltaX = 0;
            }
            if (deltaZ < 2){
                deltaX = 0;
            }

            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];

            vibrate();
        } else
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                if (counterSteps < 1){
                    counterSteps = (int) event.values[0];
                }

                stepCounter = (int) event.values[0] - counterSteps;

                step.setText("Step : "+stepCounter);
            } else
                if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
                    stepDetector++;
                }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void vibrate(){
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(3000);
        }
    }

    public boolean login(String username, String passwords) {
        edUsername = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextpassword);

        if (edUsername.getText().toString().equals(username) && password.getText().toString().equals(passwords)) {
            return true;
        } else if(edUsername.getText().toString().equals("")){
            return false;
        } else if (password.getText().toString().equals("")){
            return false;
        }

        return false;
    }
}

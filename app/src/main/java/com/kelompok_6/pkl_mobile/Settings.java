package com.kelompok_6.pkl_mobile;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.WindowManager;

public class Settings extends PreferenceActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorLight;

    public static int placeholder;
    public static int interval;

    @SuppressWarnings("deprecation")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        addPreferencesFromResource(R.xml.prefs_sync);
        ListPreference listPreference = (ListPreference) findPreference("periodically");
        CharSequence currText = listPreference.getEntry();
        this.interval = Integer.parseInt(listPreference.getValue());
        CheckBoxPreference boxLoginout = (CheckBoxPreference) findPreference("login_logout_sync");
        boolean status1 = boxLoginout.isChecked();
        CheckBoxPreference directly = (CheckBoxPreference) findPreference("update_data_directly");
        boolean status2 = directly.isChecked();
		CheckBoxPreference isEnabled= (CheckBoxPreference) findPreference("data_synchronization"); 
		
		if(isEnabled.isChecked() == true)
		{
			//user pilih login logout
			if(status1==true)
			{
				this.placeholder = 1;
			}
			//user pilih direct sync
			else if(status2 == true)
			{
				this.placeholder = 2;
			}
			//user pilih interval
			else
			{
				this.placeholder = 3;
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }
}

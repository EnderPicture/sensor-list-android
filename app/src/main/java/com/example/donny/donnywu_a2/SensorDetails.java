package com.example.donny.donnywu_a2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SensorDetails extends AppCompatActivity implements SensorEventListener {

    private static final String NAME_KEY = "Sensor Name";
    private static final String INDEX_KEY = "Sensor Index";

    private String mSensorName;
    private int mSensorIndex;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private TextView mEventText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        // get the intent that started this activity
        Intent i = getIntent();

        // see if there are any extra information in it
        Bundle extras = i.getExtras();

        // grab sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (extras != null && extras.containsKey(NAME_KEY) && extras.containsKey(INDEX_KEY)) {
            mSensorName = extras.getString(NAME_KEY);
            mSensorIndex = extras.getInt(INDEX_KEY);

            // grab all available sensor
            List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

            if (mSensorIndex < sensorList.size()) {
                Sensor sensor = sensorList.get(mSensorIndex);

                String sensorName = sensor.getName();

                if (sensorName.equals(mSensorName)) {
                    // if nothing has changed after the intent

                    mSensor = sensor;

                } else {
                    crashErrorToast("Sensor index shifted");
                }

            } else {
                crashErrorToast("Sensor list smaller than index");
            }

        } else {
            crashErrorToast("Extras are null");
        }

        // after sensor device as been loaded
        TextView name = findViewById(R.id.name);
        name.setText(mSensor.getName());

        EditText maximumRange = findViewById(R.id.maximumRange);
        maximumRange.setText(mSensor.getMaximumRange()+"");

        EditText minDelay = findViewById(R.id.minDelay);
        minDelay.setText(mSensor.getMinDelay()+"");

        EditText power = findViewById(R.id.power);
        power.setText(mSensor.getPower()+"");

        EditText resolution = findViewById(R.id.resolution);
        resolution.setText(mSensor.getResolution()+"");

        EditText type = findViewById(R.id.type);
        type.setText(mSensor.getType()+"");

        EditText vendor = findViewById(R.id.vendor);
        vendor.setText(mSensor.getVendor()+"");

        EditText version = findViewById(R.id.version);
        version.setText(mSensor.getVersion()+"");

        mEventText = findViewById(R.id.sensorEvent);
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);

        super.onPause();
    }

    private void crashErrorToast(String crashErrorMessage) {
        Toast.makeText(this, crashErrorMessage, Toast.LENGTH_SHORT).show();
        finish();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        // makes a blank string
        String s = "";

        // go through the value and build the string
        for (float f : event.values) {
            s += String.format("%.3f",f)+"\n";
        }

        // set the text
        mEventText.setText(s);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

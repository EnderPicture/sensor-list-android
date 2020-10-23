package com.example.donny.donnywu_a2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private Sensor mLightSensor;

    private ToneGenerator mToneGen;

    private boolean mLastFlat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // grab recycler view
        mRecyclerView = findViewById(R.id.recycler);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get all of the sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // set adapter
        mAdapter = new RecyclerAdapter(this, sensorList);
        mRecyclerView.setAdapter(mAdapter);

        // set sensors
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (mGravitySensor == null) {
            Toast.makeText(this, "This device does not have a gravity sensor", Toast.LENGTH_SHORT).show();
        }
        if (mLightSensor == null) {
            Toast.makeText(this, "This device does not have a light sensor", Toast.LENGTH_SHORT).show();
        }

        // make tone generator
        mToneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);


        // connect up the buttons
        Button motion = findViewById(R.id.motion);
        motion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MotionDetection.class);
                startActivity(i);
            }
        });

        Button noise = findViewById(R.id.noise);
        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NoiseDetection.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);

        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            boolean nowFlat = event.values[2] > 9.8;

            // only when it is flat now and was not flat before
            if (nowFlat && !mLastFlat) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (v.hasVibrator()) {
                    v.vibrate(5000); // 5000 milliseconds is 5 seconds
                    Toast.makeText(this, "device flat – beep", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Device does not have a Vibrator. Flat on table.", Toast.LENGTH_SHORT).show();
                }


            }
            mLastFlat = nowFlat;
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] == 0) {
                mToneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 250);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

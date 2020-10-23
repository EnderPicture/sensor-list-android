package com.example.donny.donnywu_a2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MotionDetection extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLinearAccSensor;
    private TextView mTextView;

    private Button mButton;

    private float mSensitivity = 0.1f;

    private boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_detection);

        if (savedInstanceState != null)
            start = savedInstanceState.getBoolean("start");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLinearAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mTextView = findViewById(R.id.textView);

        if (mLinearAccSensor == null) {
            mTextView.setText(R.string.no_l_acc);
        }

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start) {
                    mButton.setText(R.string.stop);
                    mSensorManager.registerListener(MotionDetection.this, mLinearAccSensor, SensorManager.SENSOR_DELAY_NORMAL);

                    start = true;


                } else {
                    mButton.setText(R.string.start);
                    start = false;
                    mSensorManager.unregisterListener(MotionDetection.this);
                    mTextView.setText(R.string.deactivated);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if (start)
            mSensorManager.registerListener(this, mLinearAccSensor, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);

        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float[] values = event.values;

            if (Math.abs(values[0]) > mSensitivity || Math.abs(values[1]) > mSensitivity || Math.abs(values[2]) > mSensitivity)
                mTextView.setText(R.string.moving);
            else
                mTextView.setText(R.string.not_moving);
        }

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putBoolean("start",start);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

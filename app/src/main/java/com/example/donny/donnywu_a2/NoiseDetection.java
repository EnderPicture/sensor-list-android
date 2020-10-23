package com.example.donny.donnywu_a2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class NoiseDetection extends AppCompatActivity implements SensorEventListener {

    // the permission id
    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10;

    // to use the media encoder
    private MediaRecorder mRecorder = null;

    private TextView mTextView;

    private int mLoopSpeed = 100;

    private boolean mHasMic;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean start = false;

    private Button mButton;

    // the handler that makes the loop
    private Handler handler = new Handler();
    // the runnable that the handler can run
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            updateInfo();
            handler.postDelayed(update, mLoopSpeed);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_detection);

        if (savedInstanceState != null)
            start = savedInstanceState.getBoolean("start");

        mTextView = findViewById(R.id.textView);

        PackageManager pm = getPackageManager();
        mHasMic = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);

        if (mHasMic) {

        } else {
            Toast.makeText(this, "This device does not have a mic", Toast.LENGTH_SHORT).show();
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (mSensor == null) {
                Toast.makeText(this, "this device does not have a proximity sensor", Toast.LENGTH_SHORT).show();
            }
        }

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!start) {

                    start = true;

                    mButton.setText(R.string.stop);

                    start();
                } else {
                    start = false;
                    mButton.setText(R.string.start);

                    mTextView.setText(R.string.deactivated);

                    stop();
                }
            }
        });


    }

    protected void setupMediaRecorder() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
            }
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception e) {
                Toast.makeText(this, "please now reenter the noise status activity", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupMediaRecorder();

                } else {
                    Toast.makeText(this, "Mic permision needed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    protected void updateInfo() {
        if (mRecorder != null)
            mTextView.setText(mRecorder.getMaxAmplitude() + "");
        else
            mTextView.setText("No recorder");
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (start)
            start();

    }

    protected void start() {
        if (!mHasMic)
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);


        if (mHasMic) {
            setupMediaRecorder();
            handler.postDelayed(update, mLoopSpeed);
        }


    }


    protected void stop() {
        if (!mHasMic)
            mSensorManager.unregisterListener(this);

        if (mHasMic)
            handler.removeCallbacks(update);


        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
            } catch (Exception e) {
            }


            mRecorder = null;
        }
    }


    @Override
    protected void onPause() {
        stop();


        super.onPause();
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putBoolean("start",start);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        mTextView.setText(event.values[0] > 0 ? "NO NOISE" : "NOISE");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

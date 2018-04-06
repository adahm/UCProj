package com.example.andreas.iridiumflares;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import junit.framework.Assert;

public class CompassActivity extends AppCompatActivity implements SensorEventListener{

    private ImageView compassImage;
    private ImageView blackelineImage;
    private ImageView dottedlineImage;
    private SensorManager sensorManager;
    private float degree = 0f;

    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] I = new float[9];

    private final float[] mOrientationAngles = new float[3];

    private float pitch = 0f;

    private int interval = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Intent i = getIntent();
        String angle = i.getStringExtra("Angle");
        Log.i("R",angle);
        compassImage = findViewById(R.id.compass); //TODO add watermark
        blackelineImage = findViewById(R.id.blackline);
        dottedlineImage = findViewById(R.id.dottedline);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            // Rotation of altitude indicator
            float altitudeAngle = 45;
            dottedlineImage.setRotationX(dottedlineImage.getX()/2);
            dottedlineImage.setRotationY(dottedlineImage.getY()/2);
            dottedlineImage.setRotation(altitudeAngle);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerReading = sensorEvent.values;

        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerReading = sensorEvent.values;
        }
        if(mMagnetometerReading != null && mAccelerometerReading != null){
            updateCompassRotation();

        }

    }

    public void updateCompassRotation() {
        sensorManager.getRotationMatrix(mRotationMatrix,I,
                mAccelerometerReading, mMagnetometerReading);
        sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        if(mAccelerometerReading[2]<0) //make sure that we can detect if the phone rotates over it´s x axis
            mOrientationAngles[1] = (float) (Math.PI - mOrientationAngles[1]);
        float degreeChange =  mOrientationAngles[0];



        degreeChange = (float) Math.toDegrees(degreeChange);

        if (degreeChange < 0.0f) {
            degreeChange += 360f;
        }
        degreeChange = Math.round(degreeChange);
 //       Log.i("i", "Azimuth " + degreeChange);
            RotateAnimation r = new RotateAnimation(degree,
                    -degreeChange,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            r.setDuration(230);

            compassImage.startAnimation(r);
            degree = -degreeChange;

        //change angle of the vertical line
        float pitchChange = mOrientationAngles[1];

        pitchChange = (float) Math.toDegrees(pitchChange);
        if (pitchChange < 0.0f) {
            pitchChange += 360f;
        }
        pitchChange = Math.round(pitchChange);
        RotateAnimation lineRot = new RotateAnimation(pitch,
                pitchChange,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        lineRot.setDuration(230);
        blackelineImage.startAnimation(lineRot);
        pitch = pitchChange;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

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
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import junit.framework.Assert;

public class CompassActivity extends AppCompatActivity implements SensorEventListener{

    private ImageView compassImage;
    private SensorManager sensorManager;
    private float degree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Intent i = getIntent();
        String angle = i.getStringExtra("Angle");
        Log.i("R",angle);
        compassImage = findViewById(R.id.compass); //TODO add watermark

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degreeChange =  Math.round(sensorEvent.values[0]);
        RotateAnimation r = new RotateAnimation( degree,
        degreeChange,
        int pivotXType,
        float pivotXValue,
        int pivotYType,
        float pivotYValue)
        sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

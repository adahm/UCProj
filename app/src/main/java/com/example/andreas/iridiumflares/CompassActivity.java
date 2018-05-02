package com.example.andreas.iridiumflares;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;

public class CompassActivity extends AppCompatActivity implements SensorEventListener{

    private ImageView compassImage;
    private ImageView blackelineImage;
    private ImageView dottedlineImage;
    private Button notifyButton;
    private SensorManager sensorManager;
    private float degree = 0f;

    private float[] mAccelerometerReading = new float[3];
    private float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] I = new float[9];

    private final float[] mOrientationAngles = new float[3];

    private float pitch = 0f;
    private String date;
    Context context = CompassActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        JodaTimeAndroid.init(this);

        // Get data for the flare
        Intent i = getIntent();
        float azimuth = (float) i.getIntExtra("Azimuth",0);
        float pitch = (float) i.getIntExtra("Pitch",0);
        long time = (long) i.getIntExtra("Time",0);
        date = i.getStringExtra("Date");


        Log.i("R","azi" + azimuth);
        Log.i("R","pitch" +pitch);
        Log.i("R","milli sec to flare"+time);

        // Find objects in view
        compassImage = (ImageView) findViewById(R.id.compass);
        blackelineImage = (ImageView) findViewById(R.id.blackline);
        dottedlineImage = (ImageView) findViewById(R.id.dottedline);
        notifyButton = (Button) findViewById(R.id.button_notify);

        // Place rotation and star indicators
        addStarToCompass(azimuth);
        rotateAltitudeIndicator(pitch);

        // Initialize countdown timer:
        initializeCountDownTimer(time);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);






        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) (azimuth + pitch);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, id, i, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Flare happens soon")
                        .setContentText("Flare will appear in 5 min")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);


                Notification notification = mBuilder.build();


                Intent notIntent = new Intent(context, NotificationCreator.class);

                LocalDateTime time = DateTimeFormat.forPattern("MMM dd yyyy HH:mm:ss").parseLocalDateTime(date);
                //format Tue Apr 10 03:26:19 GMT+00:00 2018

                //set cest/cet timezone
                //get the time left to the flare
                DateTimeZone zone = DateTimeZone.forID("Europe/Stockholm");
                LocalDateTime currTime = new LocalDateTime(zone);
                Period p = new Period(currTime, time, PeriodType.millis());

                notIntent.putExtra("notificationID",id);
                notIntent.putExtra("notification",notification);
                notIntent.setAction("NOTIFY");
                PendingIntent pIntent = PendingIntent.getBroadcast(context, id, notIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                Log.i("set","pendingIntent: " + notIntent.getAction());
                //set the notifcation to be sent when
                long Miliis = p.getValue(0);
                //subtract 5 miniutes so the notifcation is recived before the flare aperas
                Miliis = Miliis-1000*60*5;
                long futureInMillis = SystemClock.elapsedRealtime() + 1000*10;
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pIntent);
                notifyButton.setText("Notification set");
            }
        });


    }
    //Create countdown timer for when the flare will appear
    private void initializeCountDownTimer(long millis) {

        final TextView countDownView = (TextView) findViewById(R.id.countDownTimer);

        new CountDownTimer(millis,1000) {

            @Override
            public void onTick(long millis) {
                int seconds = (int) (millis / 1000) % 60 ;
                int minutes = (int) ((millis / (1000*60)) % 60);
                int hours   = (int) ((millis / (1000*60*60)) % 24);
                int days = (int) ((millis / (1000*60*60*24)));
                String text = String.format("%02d days, %02d hours, %02d minutes, %02d seconds",days,hours,minutes,seconds);
                countDownView.setText(text);
            }

            @Override
            public void onFinish() {
                countDownView.setText("Flare happened");
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
    }

    public void rotateAltitudeIndicator(float altitudeAngle){
        dottedlineImage.setRotation(-altitudeAngle);
    }

    public void addStarToCompass(float targetAzimuth){
        Drawable compassClone = compassImage.getDrawable();
        Bitmap compassBitmap = ((BitmapDrawable)compassClone).getBitmap();

        // Correct azimuth to correspond to rotation angle
        targetAzimuth = 90f - targetAzimuth;

        // Fetch height and width used for placement
        int w = compassBitmap.getWidth();
        int h = compassBitmap.getHeight();

        // Initialize drawing
        Bitmap result = Bitmap.createBitmap(w, h, compassBitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(compassBitmap, 0, 0, null);
        // Scale Text to be 10% of image
        int textSizeTarget = h/10;

        Paint paint = new Paint();
        paint.setColor(100);
        paint.setAlpha(100);
        paint.setTextSize(textSizeTarget);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);

        double locationX = ((((w/2)*0.75)*Math.cos(Math.toRadians(targetAzimuth)))+(w/2)-(textSizeTarget/4));
        double locationY = h-(((h/2)*0.75)*Math.sin(Math.toRadians(targetAzimuth))+(h/2)-(textSizeTarget/2));

        Log.i("Drawing", "sin value: " + (Math.sin(Math.toRadians(targetAzimuth))) +
                ", cos value: " + Math.cos(Math.toRadians(targetAzimuth)));

        // Place text and replace compass image with new compass image
        canvas.drawText("*", (float)locationX, (float)locationY, paint);
        compassImage.setImageBitmap(result);

        Log.i("Drawing", "drawing done and drawing * at location x: " + String.valueOf(locationX) + ", y: " + String.valueOf(locationY));
        Log.i("Drawing", "Size of image: " + String.valueOf(w) + ", by " + String.valueOf(h));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //get the data from the accelerometer and the magnetometer
    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerReading = sensorEvent.values;

        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerReading = sensorEvent.values;
        }
        if(mMagnetometerReading != null && mAccelerometerReading != null){
            updateIndicators();
        }

    }

    //Chnage the oritentation of the line and the compass depending on the chnage of readings from the Sensors
    public void updateIndicators() {
        sensorManager.getRotationMatrix(mRotationMatrix,I,
                mAccelerometerReading, mMagnetometerReading);
        sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        if(mAccelerometerReading[2]<0) //make sure that we can detect if the phone rotates over it´s x axis
            mOrientationAngles[1] = (float) (Math.PI - mOrientationAngles[1]);

        //convert the radinas to degrees
        float degreeChange =  mOrientationAngles[0];
        degreeChange = (float) Math.toDegrees(degreeChange);

        if (degreeChange < 0.0f) {
            degreeChange += 360f;
        }
        degreeChange = Math.round(degreeChange);

        //rotate the compass
        RotateAnimation r = new RotateAnimation(degree,
                -degreeChange,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        r.setDuration(230);

        compassImage.startAnimation(r);
        degree = -degreeChange;

        // Get the change in pitch
        float pitchChange = mOrientationAngles[1];

        //convert from radians to degrees
        pitchChange = (float) Math.toDegrees(pitchChange);
        if (pitchChange < 0.0f) {
            pitchChange += 360f;
        }
        pitchChange = Math.round(pitchChange);

        //rotate the line that indicates the pitch of the phone
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

package com.s23010255.waste_watcher;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GetpointintroActivity extends AppCompatActivity implements SensorEventListener {

    private Button startBtn;
    private TextView description;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpointintro);

        startBtn = findViewById(R.id.startBtn);
        description = findViewById(R.id.description);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GetpointintroActivity.this, GetpointbActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "No light sensor found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightLevel = event.values[0];

        if (lightLevel < 600) {
            description.setText("Not good for cleaning");
            playAudio(R.raw.dark_alart);
        } else {
            description.setText("Good for cleaning");
            playAudio(R.raw.light_safe);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void playAudio(int resId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.start();
    }
}

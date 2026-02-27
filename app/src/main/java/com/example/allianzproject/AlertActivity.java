package com.example.allianzproject;

import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {

    private MediaPlayer sirenPlayer;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;
    private Handler flashHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> {
            stopSiren();
            finish();
        });
    }

    // 🚨 START EMERGENCY HERE
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("LUMA_PREFS", MODE_PRIVATE);

        boolean sirenOn = prefs.getBoolean("SIREN_ON", true);
        boolean flashOn = prefs.getBoolean("FLASH_ON", true);

        if (sirenOn && sirenPlayer == null) {
            sirenPlayer = MediaPlayer.create(this, R.raw.siren);
            sirenPlayer.setLooping(true);
            sirenPlayer.start();
        }

        if (flashOn) {
            startFlashStrobe();
        }
    }

    // ❌ DO NOT STOP ON PAUSE

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSiren();   // Only stop when activity fully closes
    }

    private void startFlashStrobe() {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            flashHandler.post(flashRunnable);
        } catch (CameraAccessException e) { }
    }

    private Runnable flashRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                isFlashOn = !isFlashOn;
                cameraManager.setTorchMode(cameraId, isFlashOn);
            } catch (Exception e) { }
            flashHandler.postDelayed(this, 300);
        }
    };

    private void stopFlashStrobe() {
        flashHandler.removeCallbacks(flashRunnable);
        try {
            if (cameraManager != null) cameraManager.setTorchMode(cameraId, false);
        } catch (Exception e) { }
    }

    private void stopSiren() {
        if (sirenPlayer != null) {
            if (sirenPlayer.isPlaying()) sirenPlayer.stop();
            sirenPlayer.release();
            sirenPlayer = null;
        }
        stopFlashStrobe();
    }
}
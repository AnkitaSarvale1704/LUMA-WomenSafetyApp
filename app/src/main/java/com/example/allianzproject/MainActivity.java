package com.example.allianzproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean voiceEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermissionsIfNeeded();

        SharedPreferences prefs = getSharedPreferences("LUMA_PREFS", MODE_PRIVATE);
        voiceEnabled = prefs.getBoolean("VOICE_ON", false);

        if (voiceEnabled) {
            setupVoiceListener();
        }

        Button btnSOS = findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(v -> triggerSOS());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) return true;
            if (item.getItemId() == R.id.nav_safety) {
                startActivity(new Intent(MainActivity.this, SafetyActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void requestPermissionsIfNeeded() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECORD_AUDIO
                }, 101);
    }

    // 🚨 SOS FLOW
    private void triggerSOS() {

        Intent alertIntent = new Intent(MainActivity.this, AlertActivity.class);
        startActivity(alertIntent);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            double lat = 0;
            double lng = 0;

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
            String time = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a")
                    .format(new java.util.Date());
            String message = "🚨 CRITICAL EMERGENCY ALERT \nThis message was triggered by a voice or SOS activation.\nThe sender may be under threat and unable to respond.\nTap to open Current location:\nhttps://maps.google.com/?q=" + lat + "," + lng+"\nPlease attempt to contact immediately.\nTimestamp:"+time+"\nLUMA Personal Safety System";

            sendSMSToContacts(message);
        });
    }
    private void sendSMSToContacts(String message) {
        String data = getSharedPreferences("LUMA_PREFS", MODE_PRIVATE).getString("CONTACTS", "");
        StringBuilder numbers = new StringBuilder();

        for (String item : data.split(";")) {
            if (item.trim().isEmpty()) continue;
            String[] parts = item.split("\\|");
            if (parts.length < 2) continue;
            numbers.append(parts[1]).append(";");
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + numbers.toString()));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    // 🎤 VOICE LISTENER
    private void setupVoiceListener() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onResults(Bundle results) {
                detectSOS(results);
                restartListening();
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                detectSOS(partialResults);
            }

            @Override public void onError(int error) { restartListening(); }
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        startListening();
    }

    private void detectSOS(Bundle results) {
        ArrayList<String> matches =
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) return;

        String spoken = matches.get(0).toLowerCase();

        if (spoken.contains("help") ||
                spoken.contains("sos") ||
                spoken.contains("emergency")) {

            triggerSOS();
        }
    }

    private void startListening() {
        speechRecognizer.startListening(speechIntent);
    }

    private void restartListening() {
        if (voiceEnabled)
            new Handler().postDelayed(this::startListening, 700);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }
}
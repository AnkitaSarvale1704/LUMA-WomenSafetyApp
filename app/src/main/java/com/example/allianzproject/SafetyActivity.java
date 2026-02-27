package com.example.allianzproject;

import android.content.SharedPreferences;
import android.widget.Switch;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SafetyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        SharedPreferences prefs = getSharedPreferences("LUMA_PREFS", MODE_PRIVATE);

        // 🔊 Siren Toggle
        Switch switchSiren = findViewById(R.id.switchSiren);
        switchSiren.setChecked(prefs.getBoolean("SIREN_ON", false));
        switchSiren.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("SIREN_ON", isChecked).apply()
        );

        // 🔦 Flash Toggle
        Switch switchFlash = findViewById(R.id.switchFlash);
        switchFlash.setChecked(prefs.getBoolean("FLASH_ON", false));
        switchFlash.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("FLASH_ON", isChecked).apply()
        );

        // 🎤 VOICE ACTIVATION TOGGLE (NEW)
        Switch switchVoice = findViewById(R.id.switchVoice);
        switchVoice.setChecked(prefs.getBoolean("VOICE_ON", false));
        switchVoice.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("VOICE_ON", isChecked).apply()
        );

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_safety);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return true;
        });
    }
}
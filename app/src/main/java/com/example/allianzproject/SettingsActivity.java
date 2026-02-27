package com.example.allianzproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        View btnContacts = findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(v ->
                startActivity(new Intent(SettingsActivity.this, EmergencyContactsActivity.class)));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_settings) {
                return true;
            }

            if (id == R.id.nav_home) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                return true;
            }

            if (id == R.id.nav_safety) {
                startActivity(new Intent(SettingsActivity.this, SafetyActivity.class));
                return true;
            }

            return false;
        });
    }
}
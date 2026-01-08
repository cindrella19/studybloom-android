package com.example.studybloom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("StudyBloomPrefs", Context.MODE_PRIVATE);
        MaterialSwitch reminderSwitch = findViewById(R.id.reminderSwitch);
        Button resetBtn = findViewById(R.id.resetDataBtn);

        // Load Reminder state
        boolean areRemindersOn = prefs.getBoolean("studyReminders", true);
        reminderSwitch.setChecked(areRemindersOn);

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("studyReminders", isChecked).apply();
            String msg = isChecked ? "Reminders enabled" : "Reminders disabled";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        resetBtn.setOnClickListener(v -> {
            // Completely clear all keys from the preferences file
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Everything has been reset. Fresh start!", Toast.LENGTH_LONG).show();
            
            // Close the activity to return to home, which will reload with empty data
            finish();
        });
    }
}
package com.example.studybloom;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("StudyBloomPrefs", MODE_PRIVATE);
        int totalTasks = prefs.getInt("totalTasksCompleted", 0);
        int totalMinutes = prefs.getInt("totalStudyMinutes", 0);

        TextView taskText = findViewById(R.id.dashTotalTasks);
        TextView hoursText = findViewById(R.id.dashTotalHours);

        taskText.setText("Total Tasks Completed: " + totalTasks);
        
        double hours = totalMinutes / 60.0;
        hoursText.setText(String.format("Total Study Time: %.1f hrs", hours));
    }
}
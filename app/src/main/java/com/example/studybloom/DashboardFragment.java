package com.example.studybloom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("StudyBloomPrefs", Context.MODE_PRIVATE);
        int totalTasks = prefs.getInt("totalTasksCompleted", 0);
        int totalMinutes = prefs.getInt("totalStudyMinutes", 0);

        TextView taskText = view.findViewById(R.id.dashTotalTasks);
        TextView hoursText = view.findViewById(R.id.dashTotalHours);

        taskText.setText("Total Tasks Completed: " + totalTasks);
        double hours = totalMinutes / 60.0;
        hoursText.setText(String.format("Total Study Time: %.1f hrs", hours));

        return view;
    }
}
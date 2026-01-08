package com.example.studybloom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StudyFragment extends Fragment {

    private SharedPreferences prefs;
    private int completedCount = 0;
    private int totalStudyMinutes = 0;
    private int allTimeCompletedTasks = 0;
    private TextView completedCounter;
    private EditText subjectInput, timeInput;
    private LinearLayout taskContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        completedCounter = view.findViewById(R.id.completedCounter);
        subjectInput = view.findViewById(R.id.subjectInput);
        timeInput = view.findViewById(R.id.timeInput);
        taskContainer = view.findViewById(R.id.taskContainer);
        Button addTaskBtn = view.findViewById(R.id.addTaskBtn);

        prefs = getActivity().getSharedPreferences("StudyBloomPrefs", Context.MODE_PRIVATE);
        totalStudyMinutes = prefs.getInt("totalStudyMinutes", 0);
        allTimeCompletedTasks = prefs.getInt("totalTasksCompleted", 0);
        loadTasks();

        addTaskBtn.setOnClickListener(v -> {
            String subject = subjectInput.getText().toString().trim();
            String timeStr = timeInput.getText().toString().trim();
            if (!subject.isEmpty() && !timeStr.isEmpty()) {
                addTask(subject, timeStr);
                subjectInput.setText("");
                timeInput.setText("");
            }
        });

        return view;
    }

    private CheckBox createTask(String subject, String time, boolean isChecked) {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setText(subject + " (" + time + ")");
        checkBox.setTextSize(18);
        checkBox.setTypeface(null, android.graphics.Typeface.BOLD);
        checkBox.setTextColor(getResources().getColor(R.color.dark_text));
        checkBox.setPadding(8, 12, 8, 12);
        checkBox.setChecked(isChecked);

        checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
            int minutes = parseMinutes(time);
            if (checked) {
                completedCount++;
                allTimeCompletedTasks++;
                totalStudyMinutes += minutes;
            } else {
                completedCount--;
                allTimeCompletedTasks--;
                totalStudyMinutes -= minutes;
            }
            updateCounter();
            saveTasks();
        });

        checkBox.setOnLongClickListener(v -> {
            int minutes = parseMinutes(time);
            if (checkBox.isChecked()) {
                completedCount--;
                allTimeCompletedTasks--;
                totalStudyMinutes -= minutes;
            }
            updateCounter();
            taskContainer.removeView(checkBox);
            saveTasks();
            return true;
        });
        return checkBox;
    }

    private int parseMinutes(String time) {
        try {
            return Integer.parseInt(time.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private void addTask(String subject, String time) {
        CheckBox checkBox = createTask(subject, time, false);
        taskContainer.addView(checkBox);
        saveTasks();
    }

    private void loadTasks() {
        String savedTasks = prefs.getString("tasks_v3", "");
        completedCount = 0;
        taskContainer.removeAllViews();
        if (!savedTasks.isEmpty()) {
            String[] tasks = savedTasks.split(";;");
            for (String task : tasks) {
                if (task.isEmpty()) continue;
                String[] parts = task.split("\\|\\|");
                if (parts.length < 3) continue;
                String subject = parts[0];
                String time = parts[1];
                boolean isChecked = Boolean.parseBoolean(parts[2]);
                
                CheckBox checkBox = createTask(subject, time, isChecked);
                if (isChecked) completedCount++;
                taskContainer.addView(checkBox);
            }
        }
        updateCounter();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < taskContainer.getChildCount(); i++) {
            CheckBox cb = (CheckBox) taskContainer.getChildAt(i);
            String text = cb.getText().toString();
            String subject = text.substring(0, text.lastIndexOf(" ("));
            String time = text.substring(text.lastIndexOf(" (") + 2, text.lastIndexOf(")"));
            
            sb.append(subject).append("||")
              .append(time).append("||")
              .append(cb.isChecked()).append(";;");
        }
        editor.putString("tasks_v3", sb.toString());
        editor.putInt("totalTasksCompleted", allTimeCompletedTasks); 
        editor.putInt("totalStudyMinutes", totalStudyMinutes);
        editor.apply();
    }

    private void updateCounter() {
        if (completedCount < 0) completedCount = 0;
        completedCounter.setText("Completed today: " + completedCount);
    }
}

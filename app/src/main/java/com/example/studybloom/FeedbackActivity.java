package com.example.studybloom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        EditText input = findViewById(R.id.feedbackInput);
        Button sendBtn = findViewById(R.id.sendFeedbackBtn);

        sendBtn.setOnClickListener(v -> {
            String feedback = input.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Please enter some feedback", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@studybloom.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Study Bloom App Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, feedback);
                
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
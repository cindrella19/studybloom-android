package com.example.studybloom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText nameInput, gradeInput, emailInput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUid;
    private SharedPreferences localPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        localPrefs = getSharedPreferences("StudyBloomPrefs", MODE_PRIVATE);
        
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        currentUid = user.getUid();

        nameInput = findViewById(R.id.profileNameInput);
        gradeInput = findViewById(R.id.profileGradeInput);
        emailInput = findViewById(R.id.profileEmailInput);
        
        Button saveBtn = findViewById(R.id.saveProfileBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        // INSTANT LOAD: Load from local storage first so it never looks empty
        loadFromLocal();

        emailInput.setText(user.getEmail());

        // SYNC: Pull latest data from cloud in the background
        loadUserProfileFromCloud();

        saveBtn.setOnClickListener(v -> saveProfileData());

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            // Clear local data on logout for safety
            localPrefs.edit().clear().apply();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadFromLocal() {
        String savedName = localPrefs.getString("userName", "");
        String savedGrade = localPrefs.getString("userGrade", "");
        
        if (!savedName.isEmpty()) nameInput.setText(savedName);
        if (!savedGrade.isEmpty()) gradeInput.setText(savedGrade);
    }

    private void loadUserProfileFromCloud() {
        db.collection("users").document(currentUid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String name = doc.getString("name");
                            String grade = doc.getString("grade");

                            if (name != null) nameInput.setText(name);
                            if (grade != null) gradeInput.setText(grade);
                            
                            // Update local copy with cloud data
                            saveLocally(name, grade);
                        }
                    }
                });
    }

    private void saveProfileData() {
        if (nameInput.getText() == null || gradeInput.getText() == null) return;
        
        String name = nameInput.getText().toString().trim();
        String grade = gradeInput.getText().toString().trim();

        if (name.isEmpty() || grade.isEmpty()) {
            Toast.makeText(this, "Please enter your name and grade", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. SAVE LOCALLY (Instant)
        saveLocally(name, grade);

        // 2. SAVE TO CLOUD (Permanent)
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("grade", grade);

        db.collection("users").document(currentUid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile synced to cloud!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // If update fails, try set with merge
                    db.collection("users").document(currentUid).set(updates, com.google.firebase.firestore.SetOptions.merge());
                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveLocally(String name, String grade) {
        localPrefs.edit()
                .putString("userName", name)
                .putString("userGrade", grade)
                .apply();
    }

    public void onBackClick(View view) {
        finish();
    }
}

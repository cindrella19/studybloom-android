package com.example.studybloom;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // ✅ Save study session
    public static void saveStudySession(String subject, int minutes) {
        String uid = getUid();
        if (uid == null) return;

        Map<String, Object> session = new HashMap<>();
        session.put("subject", subject);
        session.put("durationMinutes", minutes);
        session.put("date", System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .collection("study_sessions")
                .add(session);
    }

    // ✅ Save study plan
    public static void saveStudyPlan(String subject, int targetHours, long deadline) {
        String uid = getUid();
        if (uid == null) return;

        Map<String, Object> plan = new HashMap<>();
        plan.put("subject", subject);
        plan.put("targetHours", targetHours);
        plan.put("deadline", deadline);

        db.collection("users")
                .document(uid)
                .collection("study_plan")
                .add(plan);
    }

    // ✅ Update stats
    public static void updateStats(int totalHours, int sessionsCount) {
        String uid = getUid();
        if (uid == null) return;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHours", totalHours);
        stats.put("sessionsCount", sessionsCount);
        stats.put("lastUpdated", System.currentTimeMillis());

        db.collection("users")
                .document(uid)
                .collection("stats")
                .document("summary")
                .set(stats);
    }
}

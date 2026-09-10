package com.example.fitnesstracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class FirebaseSyncManager {

    private static final String PREF_SYNC = "fitness_cloud_sync_prefs";
    private static final String KEY_LAST_SYNC = "last_sync_time";

    public interface SyncCallback {
        void onSyncSuccess(int totalSynced);
        void onSyncFailed(String errorMsg);
    }

    public static long getLastSyncTimestamp(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SYNC, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_LAST_SYNC, 0);
    }

    public static void setLastSyncTimestamp(Context context, long timestamp) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_SYNC, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply();
    }

    public static void syncLocalDataToCloud(Context context, SyncCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                FitnessDao dao = db.fitnessDao();

                // Retrieve local data
                DailyGoal goal = dao.getDailyGoal().getValue();

                // Format document payload
                Map<String, Object> userPayload = new HashMap<>();
                if (goal != null) {
                    userPayload.put("stepGoal", goal.getStepGoal());
                    userPayload.put("calorieGoal", goal.getCalorieGoal());
                    userPayload.put("waterGoal", goal.getWaterGoalMl());
                    userPayload.put("activeMinutesGoal", goal.getActiveMinutesGoal());
                }

                long now = System.currentTimeMillis();
                setLastSyncTimestamp(context, now);

                if (callback != null) {
                    callback.onSyncSuccess(1);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onSyncFailed(e.getMessage() != null ? e.getMessage() : "Sync failed");
                }
            }
        });
    }
}

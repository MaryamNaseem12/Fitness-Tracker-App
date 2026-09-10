package com.example.fitnesstracker.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {WorkoutLog.class, DailyGoal.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FitnessDao fitnessDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "fitness_tracker_db"
                            )
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Seed default goals and sample workout
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        FitnessDao dao = getInstance(context).fitnessDao();
                                        dao.setDailyGoal(new DailyGoal(10000, 2200, 2500, 45));
                                        
                                        long now = System.currentTimeMillis();
                                        dao.insertWorkout(new WorkoutLog("Morning Run", 30, 280, 4200, 3.5, now - 3600000 * 2, "Paced outdoor run"));
                                        dao.insertWorkout(new WorkoutLog("Gym / Weights", 45, 320, 1500, 0.0, now - 3600000 * 5, "Upper body workout"));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

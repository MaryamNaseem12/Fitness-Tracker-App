package com.example.fitnesstracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FitnessDao {

    @Insert
    void insertWorkout(WorkoutLog workout);

    @Delete
    void deleteWorkout(WorkoutLog workout);

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    LiveData<List<WorkoutLog>> getAllWorkouts();

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC LIMIT 10")
    LiveData<List<WorkoutLog>> getRecentWorkouts();

    @Query("SELECT SUM(caloriesBurned) FROM workouts WHERE timestamp >= :startOfDayTimestamp")
    LiveData<Integer> getTodayCalories(long startOfDayTimestamp);

    @Query("SELECT SUM(stepsTaken) FROM workouts WHERE timestamp >= :startOfDayTimestamp")
    LiveData<Integer> getTodaySteps(long startOfDayTimestamp);

    @Query("SELECT SUM(durationMinutes) FROM workouts WHERE timestamp >= :startOfDayTimestamp")
    LiveData<Integer> getTodayActiveMinutes(long startOfDayTimestamp);

    @Query("SELECT * FROM workouts WHERE timestamp >= :startTimestamp ORDER BY timestamp ASC")
    LiveData<List<WorkoutLog>> getWorkoutsSince(long startTimestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setDailyGoal(DailyGoal goal);

    @Query("SELECT * FROM daily_goals WHERE id = 1")
    LiveData<DailyGoal> getDailyGoal();
}

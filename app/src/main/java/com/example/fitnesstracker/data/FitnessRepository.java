package com.example.fitnesstracker.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FitnessRepository {

    private final FitnessDao fitnessDao;
    private final ExecutorService executor;

    public FitnessRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.fitnessDao = db.fitnessDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void insertWorkout(WorkoutLog workout) {
        executor.execute(() -> fitnessDao.insertWorkout(workout));
    }

    public void deleteWorkout(WorkoutLog workout) {
        executor.execute(() -> fitnessDao.deleteWorkout(workout));
    }

    public LiveData<List<WorkoutLog>> getAllWorkouts() {
        return fitnessDao.getAllWorkouts();
    }

    public LiveData<List<WorkoutLog>> getRecentWorkouts() {
        return fitnessDao.getRecentWorkouts();
    }

    public LiveData<Integer> getTodayCalories() {
        return fitnessDao.getTodayCalories(getStartOfDayTimestamp());
    }

    public LiveData<Integer> getTodaySteps() {
        return fitnessDao.getTodaySteps(getStartOfDayTimestamp());
    }

    public LiveData<Integer> getTodayActiveMinutes() {
        return fitnessDao.getTodayActiveMinutes(getStartOfDayTimestamp());
    }

    public LiveData<List<WorkoutLog>> getWorkoutsSince(long startTimestamp) {
        return fitnessDao.getWorkoutsSince(startTimestamp);
    }

    public LiveData<DailyGoal> getDailyGoal() {
        return fitnessDao.getDailyGoal();
    }

    public void setDailyGoal(DailyGoal goal) {
        executor.execute(() -> fitnessDao.setDailyGoal(goal));
    }

    public static long getStartOfDayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getStartOfWeekTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return calendar.getTimeInMillis();
    }
}

package com.example.fitnesstracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.FitnessRepository;
import com.example.fitnesstracker.data.WorkoutLog;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnalyticsFragment extends Fragment {

    private FitnessRepository repository;
    private WeeklyBarChartView barChartView;
    private TextView tvTotalWorkouts, tvWeeklyCalories, tvTotalDuration, tvAvgCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        repository = new FitnessRepository(requireContext());

        barChartView = view.findViewById(R.id.weekly_chart_view);
        tvTotalWorkouts = view.findViewById(R.id.tv_stat_total_workouts);
        tvWeeklyCalories = view.findViewById(R.id.tv_stat_weekly_calories);
        tvTotalDuration = view.findViewById(R.id.tv_stat_total_duration);
        tvAvgCalories = view.findViewById(R.id.tv_stat_avg_calories);

        observeData();

        return view;
    }

    private void observeData() {
        long startOfWeek = FitnessRepository.getStartOfWeekTimestamp();

        repository.getWorkoutsSince(startOfWeek).observe(getViewLifecycleOwner(), workouts -> {
            processWeeklyWorkouts(workouts);
        });
    }

    private void processWeeklyWorkouts(List<WorkoutLog> workouts) {
        float[] dailyCalories = new float[7];
        int totalWorkouts = 0;
        int totalCalories = 0;
        int totalDurationMins = 0;

        if (workouts != null) {
            totalWorkouts = workouts.size();
            Calendar calendar = Calendar.getInstance();

            for (WorkoutLog log : workouts) {
                totalCalories += log.getCaloriesBurned();
                totalDurationMins += log.getDurationMinutes();

                calendar.setTimeInMillis(log.getTimestamp());
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sun, 6 = Sat
                if (dayOfWeek >= 0 && dayOfWeek < 7) {
                    dailyCalories[dayOfWeek] += log.getCaloriesBurned();
                }
            }
        }

        barChartView.setWeeklyData(dailyCalories, 1200f);

        tvTotalWorkouts.setText(String.valueOf(totalWorkouts));
        tvWeeklyCalories.setText(String.format(Locale.getDefault(), "%,d kcal", totalCalories));

        double hours = totalDurationMins / 60.0;
        tvTotalDuration.setText(String.format(Locale.getDefault(), "%.1f hrs", hours));

        int avgCalories = totalWorkouts > 0 ? totalCalories / totalWorkouts : 0;
        tvAvgCalories.setText(String.format(Locale.getDefault(), "%d kcal", avgCalories));
    }
}

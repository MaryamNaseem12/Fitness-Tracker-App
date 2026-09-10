package com.example.fitnesstracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.FitnessRepository;
import com.example.fitnesstracker.data.WorkoutLog;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FitnessRepository repository;
    private WorkoutAdapter adapter;

    private TextView tvStepsCount, tvCaloriesCount, tvActiveMinutes, tvWaterCount, tvEmptyState;
    private CircularProgressView circularProgressView;

    private int currentSteps = 0;
    private int currentCalories = 0;
    private int stepGoal = 10000;
    private int calorieGoal = 2200;
    private int waterIntakeMl = 1250;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        repository = new FitnessRepository(requireContext());

        tvStepsCount = view.findViewById(R.id.tv_steps_count);
        tvCaloriesCount = view.findViewById(R.id.tv_calories_count);
        tvActiveMinutes = view.findViewById(R.id.tv_active_minutes);
        tvWaterCount = view.findViewById(R.id.tv_water_count);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        circularProgressView = view.findViewById(R.id.circular_progress_view);

        RecyclerView rvWorkouts = view.findViewById(R.id.rv_recent_workouts);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new WorkoutAdapter(workout -> {
            repository.deleteWorkout(workout);
            Toast.makeText(requireContext(), "Workout deleted", Toast.LENGTH_SHORT).show();
        });
        rvWorkouts.setAdapter(adapter);

        TextView btnLogActivity = view.findViewById(R.id.btn_log_workout_dash);
        btnLogActivity.setOnClickListener(v -> showLogWorkoutDialog());

        Button btnQuickWater = view.findViewById(R.id.btn_quick_water);
        btnQuickWater.setOnClickListener(v -> logQuickWater());

        observeData();

        return view;
    }

    private void observeData() {
        // Observe Daily Goals
        repository.getDailyGoal().observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                stepGoal = goal.getStepGoal();
                calorieGoal = goal.getCalorieGoal();
                updateProgressView();
            }
        });

        // Observe Today's Steps
        repository.getTodaySteps().observe(getViewLifecycleOwner(), steps -> {
            currentSteps = steps != null ? steps : 0;
            tvStepsCount.setText(String.format(Locale.getDefault(), "%,d / %,d steps", currentSteps, stepGoal));
            updateProgressView();
        });

        // Observe Today's Calories
        repository.getTodayCalories().observe(getViewLifecycleOwner(), calories -> {
            currentCalories = calories != null ? calories : 0;
            tvCaloriesCount.setText(String.format(Locale.getDefault(), "%,d / %,d kcal", currentCalories, calorieGoal));
            updateProgressView();
        });

        // Observe Today's Active Minutes
        repository.getTodayActiveMinutes().observe(getViewLifecycleOwner(), minutes -> {
            int count = minutes != null ? minutes : 0;
            tvActiveMinutes.setText(count + " mins");
        });

        // Observe Workouts List
        repository.getRecentWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts == null || workouts.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }
            adapter.setWorkouts(workouts);
        });

        updateWaterDisplay();
    }

    private void updateProgressView() {
        if (circularProgressView != null) {
            circularProgressView.setMetrics(currentSteps, stepGoal, currentCalories, calorieGoal);
        }
    }

    private void logQuickWater() {
        waterIntakeMl += 250;
        updateWaterDisplay();
        Toast.makeText(requireContext(), "+250ml Water logged!", Toast.LENGTH_SHORT).show();
    }

    private void updateWaterDisplay() {
        if (tvWaterCount != null) {
            tvWaterCount.setText(String.format(Locale.getDefault(), "%,d ml", waterIntakeMl));
        }
    }

    private void showLogWorkoutDialog() {
        LogWorkoutBottomSheetDialog dialog = new LogWorkoutBottomSheetDialog();
        dialog.show(getParentFragmentManager(), "LogWorkoutDialog");
    }
}

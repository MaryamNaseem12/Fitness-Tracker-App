package com.example.fitnesstracker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.DailyGoal;
import com.example.fitnesstracker.data.FitnessRepository;

public class GoalsFragment extends Fragment {

    private FitnessRepository repository;
    private EditText etStepsGoal, etCaloriesGoal, etWaterGoal, etActiveMinsGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        repository = new FitnessRepository(requireContext());

        etStepsGoal = view.findViewById(R.id.et_goal_steps);
        etCaloriesGoal = view.findViewById(R.id.et_goal_calories);
        etWaterGoal = view.findViewById(R.id.et_goal_water);
        etActiveMinsGoal = view.findViewById(R.id.et_goal_active_mins);

        Button btnSave = view.findViewById(R.id.btn_save_goals);
        btnSave.setOnClickListener(v -> saveGoals());

        observeGoals();

        return view;
    }

    private void observeGoals() {
        repository.getDailyGoal().observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                etStepsGoal.setText(String.valueOf(goal.getStepGoal()));
                etCaloriesGoal.setText(String.valueOf(goal.getCalorieGoal()));
                etWaterGoal.setText(String.valueOf(goal.getWaterGoalMl()));
                etActiveMinsGoal.setText(String.valueOf(goal.getActiveMinutesGoal()));
            }
        });
    }

    private void saveGoals() {
        String stepsStr = etStepsGoal.getText().toString().trim();
        String caloriesStr = etCaloriesGoal.getText().toString().trim();
        String waterStr = etWaterGoal.getText().toString().trim();
        String minsStr = etActiveMinsGoal.getText().toString().trim();

        if (TextUtils.isEmpty(stepsStr) || TextUtils.isEmpty(caloriesStr) ||
                TextUtils.isEmpty(waterStr) || TextUtils.isEmpty(minsStr)) {
            Toast.makeText(requireContext(), "Please fill in all goal fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int steps = Integer.parseInt(stepsStr);
        int calories = Integer.parseInt(caloriesStr);
        int water = Integer.parseInt(waterStr);
        int mins = Integer.parseInt(minsStr);

        DailyGoal goal = new DailyGoal(steps, calories, water, mins);
        repository.setDailyGoal(goal);

        Toast.makeText(requireContext(), "Daily goals updated successfully!", Toast.LENGTH_SHORT).show();
    }
}

package com.example.fitnesstracker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.FitnessRepository;
import com.example.fitnesstracker.data.WorkoutLog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LogWorkoutBottomSheetDialog extends BottomSheetDialogFragment {

    private Spinner spinnerWorkoutType;
    private EditText etDuration, etCalories, etSteps, etNotes;
    private FitnessRepository repository;

    private static final String[] WORKOUT_TYPES = {
            "Running", "Gym / Weights", "Walking", "Cycling", "Yoga", "HIIT", "Swimming"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_log_workout, container, false);

        repository = new FitnessRepository(requireContext());

        spinnerWorkoutType = view.findViewById(R.id.spinner_workout_type);
        etDuration = view.findViewById(R.id.et_duration);
        etCalories = view.findViewById(R.id.et_calories);
        etSteps = view.findViewById(R.id.et_steps);
        etNotes = view.findViewById(R.id.et_notes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                WORKOUT_TYPES
        );
        spinnerWorkoutType.setAdapter(adapter);

        Button btnSave = view.findViewById(R.id.btn_save_workout);
        btnSave.setOnClickListener(v -> saveWorkout());

        return view;
    }

    private void saveWorkout() {
        String type = (String) spinnerWorkoutType.getSelectedItem();
        String durationStr = etDuration.getText().toString().trim();
        String caloriesStr = etCalories.getText().toString().trim();
        String stepsStr = etSteps.getText().toString().trim();
        String notesStr = etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(durationStr) || TextUtils.isEmpty(caloriesStr)) {
            Toast.makeText(requireContext(), "Please enter duration and calories burned", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        int calories = Integer.parseInt(caloriesStr);
        int steps = TextUtils.isEmpty(stepsStr) ? 0 : Integer.parseInt(stepsStr);

        WorkoutLog workout = new WorkoutLog(
                type,
                duration,
                calories,
                steps,
                0.0,
                System.currentTimeMillis(),
                notesStr
        );

        repository.insertWorkout(workout);
        Toast.makeText(requireContext(), "Workout logged successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}

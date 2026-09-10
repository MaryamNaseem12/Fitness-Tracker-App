package com.example.fitnesstracker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.FirebaseSyncManager;
import com.example.fitnesstracker.security.BiometricSecurityHelper;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private EditText etHeight, etWeight;
    private TextView tvBmiResult, tvBmrResult;
    private SwitchCompat switchBiometric;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        tvBmiResult = view.findViewById(R.id.tv_bmi_result);
        tvBmrResult = view.findViewById(R.id.tv_bmr_result);
        switchBiometric = view.findViewById(R.id.switch_biometric);

        Button btnCalculate = view.findViewById(R.id.btn_calculate_bmi);
        btnCalculate.setOnClickListener(v -> calculateMetrics());

        Button btnSync = view.findViewById(R.id.btn_cloud_sync);
        btnSync.setOnClickListener(v -> syncCloudData());

        switchBiometric.setChecked(BiometricSecurityHelper.isBiometricEnabled(requireContext()));
        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !BiometricSecurityHelper.isBiometricAvailable(requireContext())) {
                Toast.makeText(requireContext(), "Biometric hardware not available or not configured", Toast.LENGTH_SHORT).show();
                switchBiometric.setChecked(false);
                return;
            }
            BiometricSecurityHelper.setBiometricEnabled(requireContext(), isChecked);
            String status = isChecked ? "Biometric lock enabled" : "Biometric lock disabled";
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void calculateMetrics() {
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(weightStr)) {
            Toast.makeText(requireContext(), "Please enter height and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        double heightCm = Double.parseDouble(heightStr);
        double weightKg = Double.parseDouble(weightStr);

        if (heightCm <= 0 || weightKg <= 0) {
            Toast.makeText(requireContext(), "Height and weight must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);

        String category;
        if (bmi < 18.5) {
            category = "Underweight";
        } else if (bmi < 25.0) {
            category = "Normal";
        } else if (bmi < 30.0) {
            category = "Overweight";
        } else {
            category = "Obese";
        }

        tvBmiResult.setText(String.format(Locale.getDefault(), "%.1f (%s)", bmi, category));

        // Mifflin-St Jeor BMR formula estimation (Age 25 assumption)
        double bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * 25) + 5;
        tvBmrResult.setText(String.format(Locale.getDefault(), "%,.0f kcal/day", bmr));
    }

    private void syncCloudData() {
        Toast.makeText(requireContext(), "Initiating Cloud Sync...", Toast.LENGTH_SHORT).show();
        FirebaseSyncManager.syncLocalDataToCloud(requireContext(), new FirebaseSyncManager.SyncCallback() {
            @Override
            public void onSyncSuccess(int totalSynced) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Cloud Sync completed successfully!", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onSyncFailed(String errorMsg) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Sync Status: Offline (Cached locally)", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}

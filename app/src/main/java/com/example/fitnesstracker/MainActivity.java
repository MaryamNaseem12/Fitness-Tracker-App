package com.example.fitnesstracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.fitnesstracker.databinding.ActivityMainBinding;
import com.example.fitnesstracker.security.BiometricSecurityHelper;
import com.example.fitnesstracker.ui.LogWorkoutBottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }

        binding.btnAddWorkoutHeader.setOnClickListener(v -> {
            LogWorkoutBottomSheetDialog dialog = new LogWorkoutBottomSheetDialog();
            dialog.show(getSupportFragmentManager(), "LogWorkoutDialog");
        });

        checkBiometricLock();
    }

    private void checkBiometricLock() {
        if (BiometricSecurityHelper.isBiometricEnabled(this)) {
            BiometricSecurityHelper.showBiometricPrompt(this, new BiometricSecurityHelper.BiometricCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Unlocked successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(MainActivity.this, "Authentication failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

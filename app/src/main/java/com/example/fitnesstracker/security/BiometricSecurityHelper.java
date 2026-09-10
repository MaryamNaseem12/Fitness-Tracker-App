package com.example.fitnesstracker.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricSecurityHelper {

    private static final String PREF_NAME = "fitness_security_prefs";
    private static final String KEY_BIOMETRIC_ENABLED = "biometric_lock_enabled";

    public interface BiometricCallback {
        void onSuccess();
        void onError(String errorMsg);
    }

    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static boolean isBiometricEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false);
    }

    public static void setBiometricEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply();
    }

    public static void showBiometricPrompt(FragmentActivity activity, BiometricCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (callback != null) {
                    callback.onError(errString.toString());
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                if (callback != null) {
                    callback.onError("Biometric verification failed. Please try again.");
                }
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fitness Tracker Security Lock")
                .setSubtitle("Authenticate to access your private health metrics")
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}

package com.example.mywebviewapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyConsentActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String CONSENT_GIVEN = "notification_consent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_consent);

        TextView privacyText = findViewById(R.id.privacy_text);
        Button acceptButton = findViewById(R.id.accept_button);
        Button settingsButton = findViewById(R.id.settings_button);

        acceptButton.setOnClickListener(v -> {
            // Save consent
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(CONSENT_GIVEN, true).apply();
            
            // Open notification access settings
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        });
    }

    public static boolean hasConsent(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(CONSENT_GIVEN, false);
    }
}
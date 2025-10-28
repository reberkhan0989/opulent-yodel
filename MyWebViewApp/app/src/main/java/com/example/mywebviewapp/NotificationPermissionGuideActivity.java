package com.example.mywebviewapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class NotificationPermissionGuideActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private ImageView menuIndicator;
    private boolean isCheckingPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_permission_guide);

        menuIndicator = findViewById(R.id.menu_indicator);
        TextView steps = findViewById(R.id.steps_text);
        
        // Get device manufacturer
        String manufacturer = Build.MANUFACTURER.toUpperCase();
        String[] deviceInstructions = getResources().getStringArray(R.array.device_specific_instructions);
        String instructions = null;

        // Find device-specific instructions
        for (String instruction : deviceInstructions) {
            if (instruction.toUpperCase().startsWith(manufacturer)) {
                instructions = instruction.split("\\|")[1];
                break;
            }
        }

        // If no specific instructions found, use default
        if (instructions == null) {
            if (Build.VERSION.SDK_INT >= 34) { // Android 14+
                instructions = "1. Tap 'Open App Info' below\n" +
                        "2. Look for the three-dot menu (⋮) in the top-right\n" +
                        "3. Tap 'All permissions' or 'App permissions'\n" +
                        "4. Find and enable 'Notification access'\n" +
                        "5. Toggle the permission switch to ON";
            } else {
                instructions = "1. Tap 'Open App Info' below\n" +
                        "2. Tap 'Permissions'\n" +
                        "3. Find and enable 'Notification access'\n" +
                        "4. Toggle the permission switch to ON";
            }
        }

        steps.setText(instructions + "\n\nNeed help? The blinking indicator shows where to tap!");

        // Setup animation for the menu indicator
        setupMenuIndicatorAnimation();

        Button openAppInfo = findViewById(R.id.open_settings_button);
        openAppInfo.setText("Open App Info");
        openAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct to app info settings where user can find permissions
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                
                // Start checking for permission after a delay
                isCheckingPermission = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isCheckingPermission) {
                            checkNotificationPermission();
                            handler.postDelayed(this, 1000); // Check every second
                        }
                    }
                }, 1000);
            }
        });

        Button helpButton = findViewById(R.id.open_app_settings_button);
        helpButton.setText("Need More Help?");
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show additional guidance or visual help
                findViewById(R.id.extended_help_layout).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupMenuIndicatorAnimation() {
        // Create blinking animation for the menu indicator
        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(750);
        blink.setStartOffset(250);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        menuIndicator.startAnimation(blink);
    }

    private void checkNotificationPermission() {
        String enabledListeners = Settings.Secure.getString(getContentResolver(),
                "enabled_notification_listeners");
        if (enabledListeners != null && enabledListeners.contains(getPackageName())) {
            // Permission granted! Show success and finish
            isCheckingPermission = false;
            showPermissionGrantedUI();
        }
    }

    private void showPermissionGrantedUI() {
        // Update UI to show success
        findViewById(R.id.success_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.guide_layout).setVisibility(View.GONE);
        
        // Return to main activity after a short delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isCheckingPermission = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermission();
    }
}

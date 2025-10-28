package com.example.mywebviewapp;

import android.app.Application;
import android.os.StrictMode;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MyWebViewApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable strict mode for debug builds
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }

        // Global uncaught exception handler that writes the stacktrace to internal storage
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                String stack = sw.toString();
                FileOutputStream fos = openFileOutput("last_crash.txt", MODE_PRIVATE);
                fos.write(stack.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                // ignore
            }

            // re-throw to let the system handle the crash after we've recorded it
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(2);
        });
    }
}
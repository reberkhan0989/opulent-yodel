package com.example.mywebviewapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If a crash log was saved by the global handler, show it so the user can share it
        java.io.File crashFile = new java.io.File(getFilesDir(), "last_crash.txt");
        if (crashFile.exists()) {
            Intent crashIntent = new Intent(this, CrashViewerActivity.class);
            startActivity(crashIntent);
            // do not finish here; allow user to clear or share, then proceed
            return;
        }

        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.splash_logo);
        logo.setImageResource(R.mipmap.ic_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1800); // 1.8 seconds
    }
}

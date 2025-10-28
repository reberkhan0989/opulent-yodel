package com.example.mywebviewapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CrashViewerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_viewer);

        TextView crashText = findViewById(R.id.crash_text);
        Button clearButton = findViewById(R.id.clear_button);
        Button shareButton = findViewById(R.id.share_button);

        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("last_crash.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            reader.close();
        } catch (Exception e) {
            sb.append("No crash log found.");
        }

        crashText.setText(sb.toString());

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = deleteFile("last_crash.txt");
                if (deleted) {
                    Toast.makeText(CrashViewerActivity.this, "Crash log cleared", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrashViewerActivity.this, "Nothing to clear", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = crashText.getText().toString();
                Intent send = new Intent();
                send.setAction(Intent.ACTION_SEND);
                send.putExtra(Intent.EXTRA_TEXT, text);
                send.setType("text/plain");
                startActivity(Intent.createChooser(send, "Share crash log"));
            }
        });
    }
}

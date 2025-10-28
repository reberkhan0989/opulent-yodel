package com.example.mywebviewapp;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Hide the action bar if present
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            // Make activity fullscreen
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            setContentView(R.layout.activity_main);

            WebView webView = findViewById(R.id.webview);
            final View progressBar = findViewById(R.id.progressBar);
            
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    progressBar.setVisibility(View.GONE);
                    view.loadData("<h2 style='color:red;text-align:center;padding:20px;'>Failed to load page.<br>Check your internet connection or configuration.</h2>", "text/html", "UTF-8");
                }
            });

            String url = readConfigValue("WEBVIEW_URL");
            if (url == null || url.isEmpty()) {
                url = "https://www.example.com";
            }

            progressBar.setVisibility(View.VISIBLE);
            try {
                webView.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                webView.loadData("<h2 style='color:red;text-align:center;padding:20px;'>Error loading page.</h2>", "text/html", "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // If something fails during setup, show a simple WebView with error message
            WebView errorView = new WebView(this);
            errorView.loadData("<h2 style='color:red;text-align:center;padding:20px;'>Error initializing application.</h2>", "text/html", "UTF-8");
            setContentView(errorView);
        }
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
}

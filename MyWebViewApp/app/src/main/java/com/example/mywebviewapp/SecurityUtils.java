package com.example.mywebviewapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import java.io.File;

public class SecurityUtils {
    static {
        System.loadLibrary("security");
    }

    // Native methods
    public native static boolean checkSecurity();
    public native static String getObfuscatedString(String key);

    // Root detection
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
                          "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
                          "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su" };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod2() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private static boolean checkRootMethod3() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    // Emulator detection
    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    // Debug detection
    public static boolean isBeingDebugged(Context context) {
        return android.os.Debug.isDebuggerConnected() 
               || (Settings.Secure.getInt(context.getContentResolver(), 
                                       Settings.Secure.ADB_ENABLED, 0) == 1);
    }

    // Tamper detection
    public static boolean isAppTampered(Context context) {
        try {
            String installerPackageName = context.getPackageManager()
                    .getInstallerPackageName(context.getPackageName());
            return installerPackageName == null || 
                   (!installerPackageName.startsWith("com.android.vending") && 
                    !installerPackageName.startsWith("com.google.android.feedback"));
        } catch (Exception e) {
            return true;
        }
    }

    // Code integrity check
    public static boolean verifyCodeIntegrity(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES)
                    .signatures[0]
                    .toCharsString()
                    .equals(getObfuscatedString("your_expected_signature_hash"));
        } catch (Exception e) {
            return false;
        }
    }
}
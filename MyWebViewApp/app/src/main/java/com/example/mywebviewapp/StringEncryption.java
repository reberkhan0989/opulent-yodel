package com.example.mywebviewapp;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class StringEncryption {
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static SecretKey getKey() {
        // This will be replaced at build time with a unique key
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String input) {
        try {
            byte[] inputBytes = input.getBytes();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);
            byte[] cipherText = cipher.doFinal(inputBytes);
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP);
        } catch (Exception e) {
            return input; // Fallback to original string in case of error
        }
    }

    public static String decrypt(String encrypted) {
        try {
            byte[] decoded = Base64.decode(encrypted, Base64.NO_WRAP);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            return encrypted; // Fallback to original string in case of error
        }
    }
}
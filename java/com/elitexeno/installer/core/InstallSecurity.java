package com.elitexeno.installer.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class InstallSecurity {

    private static final String PREFS = "install_security";
    private static final String KEY_TOKEN = "callback_token";
    private static final String KEY_SESSION_ID = "callback_session_id";
    private static final String KEY_ISSUED_AT = "callback_issued_at";
    private static final long TOKEN_MAX_AGE_MS = 20 * 60 * 1000L;

    private InstallSecurity() {
    }

    public static String issueToken(Context context, int sessionId) {
        byte[] nonce = new byte[32];
        new SecureRandom().nextBytes(nonce);
        String token = Base64.encodeToString(nonce, Base64.NO_WRAP);

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_SESSION_ID, sessionId)
                .putLong(KEY_ISSUED_AT, System.currentTimeMillis())
                .apply();

        return token;
    }

    public static boolean isValid(Context context, String token, int sessionId) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String expectedToken = prefs.getString(KEY_TOKEN, null);
        int expectedSessionId = prefs.getInt(KEY_SESSION_ID, -1);
        long issuedAt = prefs.getLong(KEY_ISSUED_AT, 0L);

        if (expectedToken == null || expectedSessionId != sessionId) {
            return false;
        }

        long age = System.currentTimeMillis() - issuedAt;
        if (age < 0 || age > TOKEN_MAX_AGE_MS) {
            return false;
        }

        return MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                expectedToken.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_SESSION_ID)
                .remove(KEY_ISSUED_AT)
                .apply();
    }
}

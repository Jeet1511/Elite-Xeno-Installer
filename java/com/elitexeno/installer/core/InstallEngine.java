package com.elitexeno.installer.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.elitexeno.installer.payload.PayloadReader;
import com.elitexeno.installer.security.Decryptor;
import com.elitexeno.installer.ui.InstallerUI;

import java.io.File;
import java.util.List;

public class InstallEngine {

    private static final String TAG = "InstallEngine";
    public static final String ACTION_INSTALL_PROGRESS = "com.elitexeno.installer.INSTALL_PROGRESS";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_STATE = "state";

    public static final String STATE_IDLE = "idle";
    public static final String STATE_INSTALLING = "installing";
    public static final String STATE_PENDING = "pending";
    public static final String STATE_SUCCESS = "success";
    public static final String STATE_ERROR = "error";

    public static void startInstall(Context context) {
        Log.d(TAG, "startInstall called");
        notifyProgress(context, 5, "Initializing secure installer", STATE_INSTALLING);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> InstallerUI.showInstalling(context));

        new Thread(() -> {
            try {
                notifyProgress(context, 15, "Reading encrypted payload", STATE_INSTALLING);
                List<File> apkFiles = PayloadReader.getPayloadFiles(context);
                Log.d(TAG, "Payload files count=" + apkFiles.size());

                // decrypt all files
                for (int i = 0; i < apkFiles.size(); i++) {
                    int decryptProgress = 25 + (int) (((i + 1f) / apkFiles.size()) * 35f);
                    notifyProgress(context, decryptProgress, "Decrypting payload", STATE_INSTALLING);
                    apkFiles.set(i, Decryptor.decrypt(context, apkFiles.get(i)));
                }

                notifyProgress(context, 65, "Preparing install session", STATE_INSTALLING);
                SessionManager.install(context, apkFiles);
                Log.d(TAG, "Session commit requested");
                notifyProgress(context, 90, "Waiting for system confirmation", STATE_PENDING);

            } catch (Exception e) {
                Log.e(TAG, "Install failed", e);
                String message = e.getMessage() == null ? "Install Failed" : e.getMessage();
                notifyProgress(context, 0, message, STATE_ERROR);
                mainHandler.post(() -> InstallerUI.showMessage(context, message));
            }
        }).start();
    }

    public static void notifyProgress(Context context, int progress, String message, String state) {
        Intent progressIntent = new Intent(ACTION_INSTALL_PROGRESS);
        progressIntent.setPackage(context.getPackageName());
        progressIntent.putExtra(EXTRA_PROGRESS, progress);
        progressIntent.putExtra(EXTRA_MESSAGE, message);
        progressIntent.putExtra(EXTRA_STATE, state);
        context.sendBroadcast(progressIntent);
    }
}
package com.elitexeno.installer.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

import com.elitexeno.installer.ui.InstallerUI;

public class InstallReceiver extends BroadcastReceiver {

    private static final String TAG = "InstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!SessionManager.ACTION_INSTALL_COMMIT.equals(intent.getAction())) {
            Log.w(TAG, "Ignoring unexpected action: " + intent.getAction());
            return;
        }

        int sessionId = intent.getIntExtra(
                SessionManager.EXTRA_LOCAL_SESSION_ID,
                intent.getIntExtra(PackageInstaller.EXTRA_SESSION_ID, -1)
        );
        String callbackToken = intent.getStringExtra(SessionManager.EXTRA_COMMIT_TOKEN);
        if (!InstallSecurity.isValid(context, callbackToken, sessionId)) {
            Log.w(TAG, "Rejected install callback: invalid token/session");
            InstallEngine.notifyProgress(context, 0, "Security check failed", InstallEngine.STATE_ERROR);
            InstallerUI.showMessage(context, "Security check failed");
            return;
        }

        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1);
        String statusMessage = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        Log.d(TAG, "status=" + status + " message=" + statusMessage);

        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            InstallEngine.notifyProgress(context, 95, "Confirm installation on system screen", InstallEngine.STATE_PENDING);
            Intent confirm = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirm != null) {
                confirm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(confirm);
            }
            return;
        }

        if (status == PackageInstaller.STATUS_SUCCESS) {
            InstallSecurity.clear(context);
            InstallEngine.notifyProgress(context, 100, "Installation complete", InstallEngine.STATE_SUCCESS);
            InstallerUI.showSuccess(context);
            return;
        }

        InstallSecurity.clear(context);

        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        if (message == null) {
            message = "Install Failed";
        }
        InstallEngine.notifyProgress(context, 0, message, InstallEngine.STATE_ERROR);
        InstallerUI.showMessage(context, message);
    }
}
package com.elitexeno.installer.core;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

public class SessionManager {

    public static final String ACTION_INSTALL_COMMIT = "com.elitexeno.installer.ACTION_INSTALL_COMMIT";
    public static final String EXTRA_COMMIT_TOKEN = "com.elitexeno.installer.extra.COMMIT_TOKEN";
    public static final String EXTRA_LOCAL_SESSION_ID = "com.elitexeno.installer.extra.LOCAL_SESSION_ID";

    public static void install(Context context, List<File> apks) throws Exception {

        PackageInstaller installer = context.getPackageManager().getPackageInstaller();

        PackageInstaller.SessionParams params =
                new PackageInstaller.SessionParams(
                        PackageInstaller.SessionParams.MODE_FULL_INSTALL);

        int sessionId = installer.createSession(params);
        PackageInstaller.Session session = installer.openSession(sessionId);

        long totalBytes = 0;
        for (File apk : apks) {
            totalBytes += apk.length();
        }
        long writtenBytes = 0;

        for (File apk : apks) {

            FileInputStream in = new FileInputStream(apk);
            OutputStream out = session.openWrite(apk.getName(), 0, apk.length());

            byte[] buffer = new byte[65536];
            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                writtenBytes += read;
                int progress = 65 + (int) ((writtenBytes * 20f) / Math.max(totalBytes, 1));
                session.setStagingProgress(Math.min(0.95f, progress / 100f));
                InstallEngine.notifyProgress(context, Math.min(progress, 85), "Writing install session", InstallEngine.STATE_INSTALLING);
            }

            session.fsync(out);
            in.close();
            out.close();
        }

        Intent intent = new Intent(context, InstallReceiver.class);
        intent.setAction(ACTION_INSTALL_COMMIT);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRA_LOCAL_SESSION_ID, sessionId);
        intent.putExtra(EXTRA_COMMIT_TOKEN, InstallSecurity.issueToken(context, sessionId));

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, sessionId, intent, flags
        );

        session.commit(pendingIntent.getIntentSender());
        session.close();
    }
}
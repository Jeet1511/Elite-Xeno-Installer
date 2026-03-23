package com.elitexeno.installer.payload;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

public class ApkParser {

    public static String getAppName(Context context, File apkFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);

        if (info != null) {
            info.applicationInfo.sourceDir = apkFile.getAbsolutePath();
            info.applicationInfo.publicSourceDir = apkFile.getAbsolutePath();
            return pm.getApplicationLabel(info.applicationInfo).toString();
        }

        return "Unknown";
    }
}
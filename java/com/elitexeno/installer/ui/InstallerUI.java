package com.elitexeno.installer.ui;

import android.content.Context;
import android.widget.Toast;

public class InstallerUI {

    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showInstalling(Context context) {
        showMessage(context, "Installing Elite Xeno...");
    }

    public static void showSuccess(Context context) {
        showMessage(context, "Install Success");
    }

    public static void showError(Context context) {
        showMessage(context, "Install Failed");
    }
}
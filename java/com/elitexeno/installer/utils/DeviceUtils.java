package com.elitexeno.installer.utils;

import android.os.Build;

public class DeviceUtils {

    public static String getDeviceInfo() {
        return Build.MODEL + "_" + Build.MANUFACTURER;
    }
}
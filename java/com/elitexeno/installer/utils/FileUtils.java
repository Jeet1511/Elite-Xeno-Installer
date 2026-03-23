package com.elitexeno.installer.utils;

import java.io.File;

public class FileUtils {

    public static void delete(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
package com.elitexeno.installer.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {

    public static void copy(InputStream in, OutputStream out) throws Exception {

        byte[] buffer = new byte[4096];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
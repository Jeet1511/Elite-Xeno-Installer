package com.elitexeno.installer.payload;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PayloadReader {

    public static List<File> getPayloadFiles(Context context) throws Exception {

        List<File> list = new ArrayList<>();

        InputStream in = context.getAssets().open("data.bin");

        File outFile = new File(context.getCacheDir(), "payload.tmp");
        FileOutputStream out = new FileOutputStream(outFile);

        byte[] buffer = new byte[4096];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        out.close();

        list.add(outFile);

        return list;
    }
}
package com.example.rudies.responsetijdtest.loging;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 * Created by rudies on 25-3-2016.
 */
public abstract class Logger {

    public static final Action2<String, Long> persistenceLog = new Action2<String, Long>() {
        @Override
        public void call(final String fileName, final Long value) {
            log(fileName, String.valueOf(value));
        }
    };

    public synchronized  static void log(final String fileName, final String text) {
        PrintWriter writer = null;

        try {
            final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            final File file = new File(path, fileName);

            path.mkdirs();

            writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            writer.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer == null) return;
            writer.flush();
            writer.close();
        }
    }
}

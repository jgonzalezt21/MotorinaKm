package jgonzalezt.motorina.utility;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jgonzalezt.motorina.R;

public class WRFile {
    public static final File pathExternalStorage = Environment.getExternalStorageDirectory();
    private static final File appDirectory = new File(pathExternalStorage.getAbsolutePath(), "/Kilometraje/");

    public static void saveFile(Context ctx, String fileName, String[] data) {
        try {
            boolean dir = appDirectory.mkdirs();

            if (dir | appDirectory.exists()) {
                File saveFile = new File(appDirectory, fileName);
                FileOutputStream fos = new FileOutputStream(saveFile);
                OutputStreamWriter file = new OutputStreamWriter(fos);
                for (String d : data) {
                    file.write(d + "\n");
                }
                file.flush();
                file.close();

                Toast.makeText(ctx, R.string.saveFile, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.d("SaveFile", e.toString());
        }
    }
}

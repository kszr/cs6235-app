package edu.gatech.lostandfound.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by abhishekchatterjee on 12/1/16.
 */
public class ImageUtil {
    private ImageUtil() {

    }

    public static void saveImage(Context context, Bitmap bmp, String dir, String filename) {
        File mydir = context.getDir(dir, Context.MODE_PRIVATE); //Creating an internal dir.
        File fileWithinMyDir = new File(mydir, filename); //Getting a file within the dir.
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(fileWithinMyDir);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                Log.d("SaveImage", "Saved image: " + fileWithinMyDir.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

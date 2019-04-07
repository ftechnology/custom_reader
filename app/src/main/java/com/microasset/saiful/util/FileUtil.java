/**
 * @author Mohammad Saiful Alam
 * File utility that handles the file operations.
 */
package com.microasset.saiful.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    /**
     * Copy into the destination from asset folder location.
     * @param context the application context.
     * @param srcFile the name of the source file from asset.
     * @param destFile the name of the output file location.
     */
    static public boolean copyFromAsset(Context context, String srcFile, String destFile) {
        try {
            InputStream input = context.getAssets().open(srcFile);
            //String outFileName = dbPath + dbName;
            OutputStream output = new FileOutputStream(destFile);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = input.read(mBuffer)) > 0) {
                output.write(mBuffer, 0, mLength);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Return true if the file exits in the given location.
     * @param fullPath
     * @return
     */
    static public boolean exists(String fullPath) {
        File file = new File(fullPath);

        return file.exists();
    }

    /**
     * Delete the file for given location.
     * @param fullPath
     * @return
     */
    static public boolean delete(String fullPath) {
        File file = new File(fullPath);
        return file.delete();
    }

    /**
     * Delete all files and folder in given path
     * Example uses: deleteFiles("/sdcard/uploads/");
     * @param path
     */
    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
            }
        }
    }

    /**
     * Get the file name from fullpath.
     * @param fullPath
     * @return
     */
    static public String getFileName(String fullPath) {
        File file = new File(fullPath);
        return file.getName();
    }

    /**
     * Get the file name without extension.
     * @param fullPath
     * @return
     */
    static public String getFileNameWithoutExtension(String fullPath) {
        File file = new File(fullPath);
        String fileName = file.getName();
        if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * Return the cache directory.
     * @return
     */
    static public String getCacheDirectoryPath(Context context) {
        File cache = null;

        //android.os.Environment.getExternalStorageDirectory(), FILE_FULLNAME
        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Android/data/" + context.getPackageName());
        }
        // Use internal storage
        else {
            cache = context.getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        boolean success = cache.mkdirs();
        return cache.getAbsolutePath();
    }
}

/**
 * @author Mohammad Saiful Alam
 * <p>
 * Useful for projects, when need some conversion. Update the file as required
 */
package com.microasset.saiful.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Convert {
    /**
     * Return the base64 data from file content
     *
     * @param fileName
     * @return The converted data
     */
    static public String toBase64FromFile(String fileName) {

        byte fileContent[] = toByteFromFile(fileName);
        String fileData = "";

        if (fileContent != null) {
            fileData = Base64.encodeToString(fileContent, Base64.DEFAULT);
        }

        return fileData;
    }

    /**
     * Return the byte array from file content
     *
     * @param Full path of the file with name, fileName
     * @return the byte array data
     */
    static public byte[] toByteFromFile(String fileName) {
        //
        File file = new File(fileName);
        FileInputStream fin = null;
        byte fileContent[] = null;

        if (!file.isFile()) {
            return fileContent;
        }

        try {
            fin = new FileInputStream(file);
            fileContent = new byte[(int) file.length()];
            fin.read(fileContent);

        } catch (FileNotFoundException e) {

        } catch (IOException ioe) {

        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                // System.out.println("Error while closing stream: " + ioe);

            }

        }
        return fileContent;

    }

    /***
     * Return the string data from InputStream
     *
     * @param instream
     *            The input stream to be converted
     * @return The converted stream for success
     */
    public static String toString(InputStream instream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                instream));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * Return the base64 string from string
     *
     * @param value
     * @return The converted base64 data
     */
    public static String toBase64(String value) {
        return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
    }

    /**
     * Compress bitmap using jpeg, convert to Base64 encoded string, and return
     * to JavaScript.
     *
     * @param bitmap
     * @param quality
     *            Compression quality hint (0-100: 0=low quality & high
     *            compression, 100=compress of max quality)
     */
    public static String toBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream jpeg_data = new ByteArrayOutputStream();
        String base64 = "";

        try {
            if (bitmap.compress(CompressFormat.JPEG, quality, jpeg_data)) {
                byte[] code = jpeg_data.toByteArray();
                byte[] output = Base64.encode(code, Base64.NO_WRAP);
                base64 = new String(output);

            }
        } catch (Exception e) {
            // this.failPicture("Error compressing image.");
        }

        return base64;
    }

    /**
     * Get bitmap from base64 image data.
     * @param base64ImageData
     * @return
     */
    public static Bitmap toBitmap(String base64ImageData) {

        if (base64ImageData != null && base64ImageData.length() > 1) {
            byte[] imgBytes = Base64.decode(base64ImageData, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            return bitmap;
        }

        return null;
    }

    /**
     * Return the string from int value
     *
     * @param value
     * @return the converted value
     */
    public static String toString(int value) {
        return String.valueOf(value);
    }

    /**
     * Return the string from long value
     *
     * @param value
     * @return the converted value
     */
    public static String toString(long value) {
        return String.valueOf(value);
    }

    /**
     * Return the byte array from file content
     *
     * @param instream
     * @return the byte array data
     */
    public static byte[] toByteArray(InputStream instream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead = 0;
        byte[] data = new byte[4 * 1024];

        try {
            while ((nRead = instream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

        } catch (IOException e) {
            // e.printStackTrace();
        }

        return buffer.toByteArray();
    }

    /**
     * Save the InputStream data to file. Create the directory if not exists
     * @param instream
     * @param fullPath
     */
    public static void toFile(InputStream instream, String fullPath) {
        byte[] buffer;

        try {
            File targetFile = new File(fullPath);
            // Create the directory if not exists
            String parentPath = targetFile.getAbsoluteFile().getParent();
            if (!FileUtil.exists(parentPath)) {
                new File(parentPath).mkdirs();
            }
            buffer = new byte[instream.available()];
            instream.read(buffer);

            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Save the InputStream data to file, which comes from the live server.
     * Create the directory if not exists.
     * @param instream
     * @param fileName
     */
    public static long toFileFromHttp(InputStream instream, String fileName) {
        //String msg = "Saved the content in the location:" + fileName;
        long content = 0;
        try {

            File targetFile = new File(fileName);
            // Create the directory if not exists
            String parentPath = targetFile.getAbsoluteFile().getParent();
            if (!FileUtil.exists(parentPath)) {
                new File(parentPath).mkdirs();
            }
            OutputStream outStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[4 * 1024];
            int bytesRead;

            while ((bytesRead = instream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
                content += bytesRead;
            }
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            //e.printStackTrace();
            return content;
        }

        return content;
    }

    /**
     * Return the integer from string.
     * @param value
     * @return
     */
    public static int toInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Return the integer from string.
     * @param value
     * @return
     */
    public static float toFloat(String value) {
        return Float.parseFloat(value);
    }


    /**
     * Return the Hash key for the machine.
     * @param context
     * @return
     */
    public static String toHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                //Log.d("KeyHash:", key);
                return key;
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        return "";
    }

    /**
     *
     * @param wm
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int toHeight(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        Display display = wm.getDefaultDisplay();
        int sideIndexHeight = display.getHeight();


        return sideIndexHeight;
    }

    /**
     *
     * @param wm
     * @return
     */
    public static int toWidth(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels;
    }
}

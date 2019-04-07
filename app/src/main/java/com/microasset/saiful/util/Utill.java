package com.microasset.saiful.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.web.FirebaseDbMeta;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utill {

    public static int REQUEST_THEME_SELSCTION = 1;

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * This method will recycle the bitmap
     *
     * @param b , bitmap
     */
    public static void recycleBitmap(Bitmap b) {
        if (b != null && !b.isRecycled()) {
            b.recycle();
        }
    }

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * This method check if network is avilable or not
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = getNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedToWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * This method check if the folder of this path already exist then create it
     * if not
     *
     * @param folderPath
     */
    public static void checkAndCreateFolder(String folderPath) {
        String directoryPath = Environment.getExternalStorageDirectory() + "/"
                + folderPath;
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
    }

    /* This method is for checking sim exists or not in device.
     * @param context Context to create Telephony Service.
     * @return true if sim exists otherwise false.
     * */
    public static boolean isSimExists(Context context) {
        if (context != null) {
            TelephonyManager telephony = (TelephonyManager) context.getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephony != null) {
                return (telephony.getSimState() != TelephonyManager.SIM_STATE_ABSENT);
            }
        }
        return false;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isMobileDataActive(Context context) {
        //TODO need to check mobile data state
        return false;
    }

    public static void toggleWiFi(Context context, boolean isWifi) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(!isWifi);
    }

    public static void toggleMobileData(boolean isData) {

        if (isData) {
        } else {
        }
    }

    public static int getBatteryPercentage(Context context) {
        if (context != null) {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.getApplicationContext().registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            float batteryPct = level / (float) scale;

            return (int) (batteryPct * 100);
        }
        return 0;
    }

    public interface InternetCallback {
        void onInternet(boolean hasInternet);
    }

    public static String getFormattedDate(){
        Date now = new Date();

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        String year = simpleDateformat.format(now);
        simpleDateformat = new SimpleDateFormat("EEEE", Locale.US);
        String day = simpleDateformat.format(now);
        String date = year + ", " + day;
        //
        return date;
    }

    public static String getFormattedDateWithoutDay(){
        Date now = new Date();

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        String year = simpleDateformat.format(now);
        simpleDateformat = new SimpleDateFormat("EEEE", Locale.US);
        String date = year;
        //
        return date;
    }

    public static void rateApp(Activity activity){
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public static void hideViewWithAnimation(Activity activity, int animId, final View view){
        if (view != null) {
            // Hide the Panel
            Animation anim = AnimationUtils.loadAnimation(activity,
                    animId);
            view.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    public static void showViewWithAnimation(Activity activity, int animId, final View view){
        if (view != null) {
            // Hide the Panel
            Animation anim = AnimationUtils.loadAnimation(activity,
                    animId);
            view.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    public static void openYoutubeLink(final Activity activity, final String youtubeID) {
        FirebaseDbMeta.getInstance().getMeta(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                FirebaseDbMeta.Meta meta =  (FirebaseDbMeta.Meta)response.getDataObject();
                if(meta.youtube_id == null || meta.youtube_id.isEmpty()){
                    meta.youtube_id = youtubeID;
                }
                Intent intentApp = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + meta.youtube_id));
                Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + meta.youtube_id));
                try {
                    activity.startActivity(intentApp);
                } catch (ActivityNotFoundException e) {
                    activity.startActivity(intentBrowser);
                }
            }
        });
    }

    public static void openYoutubeToturial(final Activity activity, final String youtubeID) {
        Intent intentApp = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID));
        Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeID));
        try {
            activity.startActivity(intentApp);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(intentBrowser);
        }
    }


    public static void  openFacebookPage(Activity activity, String pageId) {
        Intent intent = null;
        try {
            activity.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + pageId));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + pageId));
        }
        activity.startActivity(intent);
    }

    public static void sendEmail(Activity activity){
        String emailId = SharedPrefUtil.getSetting(activity, SharedPrefUtil.KEY_CALL_CENTER_EMAIL, "");
        if(emailId.isEmpty()){
            emailId = "studybuddybd.18@gmail.com";
        }
        String mailto = "mailto:" + emailId;
        String mobNum = SharedPrefUtil.getSetting(activity, SharedPrefUtil.KEY_MOBILE_NUMBER, "");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reader Support");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Mobile number: " + mobNum);
        try {
            activity.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setLocale(String lang, Context context) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    public static void isOnlineAsync(final NotifyObserver observer) {
        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int timeoutMs = 1500;
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                    sock.connect(sockaddr, timeoutMs);
                    sock.close();
                    /*Just to check Time delay*/
                   // boolean isOnline = true;
                    long t = Calendar.getInstance().getTimeInMillis();
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        /*Pinging to server*/
                        Process ipProcess = runtime.exec("/system/bin/ping -w 5 -c 1 1.1.1.1");
                        final int exitValue = ipProcess.waitFor();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                observer.update(new ResponseObject(exitValue, "", null));
                            }
                        });
                        return;
                        //isOnline = (exitValue == 0);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    } finally {
                        long t2 = Calendar.getInstance().getTimeInMillis();
                        //Log.e("NetWork_check_Time", (t2 - t) + "");
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            observer.update(new ResponseObject(-1, "OFFLINE", null));
                        }
                    });

                } catch (IOException e) {
                    //ignore exception
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            observer.update(new ResponseObject(-1, "OFFLINE", null));
                        }
                    });
                }
            }
        }).start();
    }
}

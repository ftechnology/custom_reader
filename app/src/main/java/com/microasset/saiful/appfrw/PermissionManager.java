package com.microasset.saiful.appfrw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

public class PermissionManager extends Object {
    static public PermissionManager mPermissionManager;
    //
    protected Context mContext;

    public String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE };
    public static int PERMISSION_ALL = 1;
    //
    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        if (mPermissionManager == null)
            mPermissionManager = new PermissionManager();
        return mPermissionManager;
    }

    /**
     * @param context
     */
    public void requestPermission(Context context) {
        mContext = context;
        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        if (!hasPermissions((Activity) mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
    }

    /**
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param context
     * @return
     */
    public boolean hasAllPermissions(Context context) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        return true;
    }
    /**
     * Launch setting screen to add write setting permission.
     */
    public void checkWriteSettingPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context.getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }
    }
}

package com.microasset.saiful.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;


import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.ResponseObject;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DeviceDetailModel extends BaseModel {

    public static DeviceDetailModel mDeviceDetailModel;

    public static final String KEY_DEVICE_SOFTWARE_VERSION_NAME = "KEY_DEVICE_SOFTWARE_VERSION_NAME";
    public static final String KEY_DEVICE_FIRMWARE_VERSION_CODE = "KEY_DEVICE_FIRMWARE_VERSION_CODE";
    public static final String KEY_DEVICE_IMEI_EMBEDDED = "KEY_DEVICE_IMEI_EMBEDDED";
    public static final String KEY_DEVICE_IMEI_SIM_SLOT = "KEY_DEVICE_IMEI_SIM_SLOT";
    public static final String KEY_DEVICE_ICCID_EMBEDDED = "KEY_DEVICE_ICCID_EMBEDDED";
    public static final String KEY_DEVICE_ICCID_SIM_SLOT = "KEY_DEVICE_ICCID_SIM_SLOT";
    public static final String KEY_DEVICE_MAC_ADDRESS = "KEY_DEVICE_MAC_ADDRESS";
    public static final String KEY_SIM_SERIAL_NUMBER = "SIMSerialNumber";
    public static final String KEY_ICCID_NUMBER = "KEY_ICCID_NUMBER";
    private static String TAG = DeviceDetailModel.class.getSimpleName();

    public static int EMBEDDED_SIM_SLOT = 1;
    public static int EXTERNAL_SIM_SLOT = 0;


    private DeviceDetailModel(Context context) {
        super(context);
    }

    @Override
    public ResponseObject execute() {
        this.clear(true);
        loadDeviceInfo();
        return null;
    }

    public static DeviceDetailModel getInstance(Context context) {
        if (mDeviceDetailModel == null)
            mDeviceDetailModel = new DeviceDetailModel(context);
        return mDeviceDetailModel;
    }


    public String getIccidNumber(Context context) {
        TelephonyManager telephoneMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return telephoneMgr.getSimSerialNumber();
    }

    /**
     * Called when the 'loadDeviceInfo' function is triggered.
     */
    public void loadDeviceInfo() {

            //Have an  object of TelephonyManager
            // Only One Object as all are settings..
            DataObject object = new DataObject(0);
            this.add(object);
            //
            object.setValue(KEY_ICCID_NUMBER, getIccidNumber(mContext));
            //Get Subscriber ID
            object.setValue(KEY_DEVICE_IMEI_EMBEDDED, getIMEI());

            //Get software version code
            String versionName = "";
            try {
                versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName + "";
                object.setValue(KEY_DEVICE_SOFTWARE_VERSION_NAME, versionName);
            } catch (Exception ex) {
                LogUtil.d(TAG, ex.getMessage());
            }

            //Get wifi mac address
            object.setValue(KEY_DEVICE_MAC_ADDRESS, getMacAddr(mContext));
    }

    public String getDeviceDetailsInfo(String predictedMethodName, TelephonyManager telephony, int slotID){
        String imsi = "";
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if (ob_phone != null) {
                imsi = ob_phone.toString();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }

    /**
     * Return the IccId number of SIM
     *
     * @return iccid for the SIM
     */

    public String getIMEI() {
        String imsi = "";
        try {
            TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                imsi = telephony.getDeviceId();
            }
        } catch (Exception e) {
            LogUtil.d(e.getMessage());
        }
        return imsi;
    }

    /*Brightness Settings*/
    public static void applyBrightness(Context activity, float brightnessValue) {
        try {
            if (activity != null) {
                Window window = getActivity(activity).getWindow();
                Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) brightnessValue);
                WindowManager.LayoutParams params = window.getAttributes();
               // params.screenBrightness = brightnessValue / (float) Constants.MAX_BRIGHTNESS;
                window.setAttributes(params);
                //SettingsManager.getInstance().setBrightness((int) brightnessValue);
            }
        }catch (Exception e){

        }
    }

    public static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) return getActivity(((ContextWrapper)context).getBaseContext());
        return null;
    }

    public static float getBrightnessValue(Context context) {
        float brightness = 0;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            LogUtil.d(TAG, "getBrightnessValue>>Error(): "+e.getMessage());
        }
        return (brightness * 100) /100;
    }

    public static String getMacAddr(Context context) {
        String macAddress ="";

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!(nif.getName().toLowerCase().contains("wlan"))) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b & 0xFF));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                macAddress = res1.toString();
                return macAddress;
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

    @SuppressLint("MissingPermission")
    public static List<SubscriptionInfo> getActiveSubscriptionInfoList(Context mContext){
        if (Build.VERSION.SDK_INT < 22) {
            return new LinkedList<>();
        }

        final SubscriptionManager subscriptionManager = SubscriptionManager.from(mContext);
        return subscriptionManager.getActiveSubscriptionInfoList();
    }
}

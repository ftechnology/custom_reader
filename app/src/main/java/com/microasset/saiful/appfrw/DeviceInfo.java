/**
 * 
 * @author Mohammad Saiful Alam
 * The device information
 *
 */
package com.microasset.saiful.appfrw;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.TimeZone;

public class DeviceInfo{
	
    private static final String ANDROID_PLATFORM = "Android";
    private static final String AMAZON_PLATFORM = "amazon-fireos";
    private static final String AMAZON_DEVICE = "Amazon";

    static DeviceInfo mDeviceInfo;

    public  static DeviceInfo getInstance(){
        if(mDeviceInfo == null){
            mDeviceInfo = new DeviceInfo();
        }
        return mDeviceInfo;
    }
    /**
     * Constructor.
     */
    public DeviceInfo() {
    }

    /**
     * Get the OS name.
     * 
     * @return
     */
    public String getPlatform() {
        String platform;
        if (isAmazonDevice()) {
            platform = AMAZON_PLATFORM;
        } else {
            platform = ANDROID_PLATFORM;
        }
        return platform;
    }

    /**
     * Get the device's Universally Unique Identifier (UUID).
     *
     * @return
     */
    public String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return uuid;
    }

    /**
     * Return the model information.
     * @return
     */
    public String getModel() {
        String model = android.os.Build.MODEL;
        return model;
    }

    /**
     * Return the product name.
     * @return
     */
    public String getProductName() {
        String productname = android.os.Build.PRODUCT;
        return productname;
    }

    /**
     * Get the OS version.
     *
     * @return
     */
    public String getOSVersion() {
        String osversion = android.os.Build.VERSION.RELEASE;
        return osversion;
    }

    public int getSDKVersion() {
       return Build.VERSION.SDK_INT;
    }

    /**
     * Return the time zone id.
     * @return
     */
    public String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return (tz.getID());
    }

    /**
     * Function to check if the device is manufactured by Amazon
     * 
     * @return
     */
    public boolean isAmazonDevice() {
        if (android.os.Build.MANUFACTURER.equals(AMAZON_DEVICE)) {
            return true;
        }
        return false;
    }
    
    /**
     * Example if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
     * @param version
     * @return
     */
    public boolean isSupportedVersion(int version) {
    	if(Build.VERSION.SDK_INT >= version) {
			return true;
		}
    	return false;
    }
}

package com.microasset.saiful.util;

import android.content.Context;
import android.provider.Settings;

public final class DisplayUtil {
    public static final int BRIGHTNESS_MAX = 255;
    public static final int BRIGHTNESS_MIN = 1;

    private DisplayUtil() {
    }

    /**
     * Read and return current brightness value from system configuration otherwise medium
     * between MAX and MIN
     *
     * @param context
     * @return
     */
    public static int getBrightness(Context context) {
        if (context != null) {
            try {
                return Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return (BRIGHTNESS_MIN + BRIGHTNESS_MAX) / 2;
    }

    /**
     * Write brightness value to system
     */
    public static void setBrightness(Context context, int brightness) {
        if (context != null) {
            try {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS,
                        brightness);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

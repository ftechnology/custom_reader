package com.microasset.saiful.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPrefUtil {

    public static final String KEY_APP_LOCAL = "app_local";
    public static final String KEY_BOOKMARK_SORT = "KEY_BOOKMARK_SORT";
    public static final String KEY_PAGE_EFFECT = "KEY_PAGE_EFFECT";
    public static final String KEY_BOOK_VERSION = "KEY_BOOK_VERSION";
    public static final String KEY_CATEGORY_CLASS = "KEY_CATEGORY_CLASS";
    public static final String KEY_DEVICE_UNIQUE_ID = "KEY_DEVICE_UNIQUE_ID";
    public static final String KEY_LAST_TIME = "KEY_LAST_TIME";
    public static final String KEY_RECENTLY_READ_STATE = "KEY_RECENTLY_READ_STATE";
    public static final String KEY_MOBILE_NUMBER = "KEY_MOBILE_NUMBER";
    public static String KEY_LICENCE = "KEY_LICENCE";
    public static String KEY_USER_ID = "KEY_USER_ID";
    public static String KEY_USER_PSW = "KEY_USER_PSW";
    public static String KEY_PAYMENT_NUMBER = "KEY_PAYMENT_NUMBER";
    public static String KEY_CALL_CENTER_NUMBER = "KEY_CALL_CENTER_NUMBER";
    public static String KEY_CALL_CENTER_EMAIL = "KEY_CALL_CENTER_EMAIL";
    public static String KEY_PRICE = "KEY_PRICE";
    public static String KEY_API_KEY = "KEY_API_KEY";
    public static String KEY_CURRENT_PLAN = "KEY_CURRENT_PLAN";
    public static String KEY_LANGUAGE_CODE = "KEY_LANGUAGE_CODE";
    public static final String KEY_CATEGORY_POS = "KEY_CATEGORY_POS";
    /**
     * Set value in shared preference
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setSetting(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Set boolean value in shared preference
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setSetting(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    /**
     * Set int value in shared preference
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setSetting(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    /**
     * get value from shared preference
     *
     * @param context
     * @param key
     * @return string
     */
    public static String getSetting(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, null);

    }

    /**
     * get value from shared preference if not found return given default value
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return string
     */
    public static String getSetting(Context context, String key, String defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defaultValue);

    }

    /**
     * get boolean value from shared preference if not found return given default value
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return boolean
     */
    public static boolean getBooleanSetting(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defaultValue);

    }

    /**
     * get integer value from shared preference if not found return given default value
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return boolean
     */
    public static int getIntSetting(Context context, String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, defaultValue);

    }

    /**
     * remove item from shared preference
     *
     * @param context
     * @param key
     */
    public static void removeSetting(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean isExistKey(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.contains(key);
    }

    public static long getLongSetting(Context context, String key, long defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(key, defaultValue);

    }

    public static void setSetting(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();

    }

    public static float getFloatSetting(Context context, String key, float defValue) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(key, defValue);
    }

    public static void setSetting(Context context, String key, float value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.commit();

    }

    public static void resetAllSetting(Context context){
        removeSetting(context, KEY_DEVICE_UNIQUE_ID);
        removeSetting(context, KEY_BOOKMARK_SORT);
        removeSetting(context, KEY_MOBILE_NUMBER);
        removeSetting(context, KEY_LICENCE);
        removeSetting(context, KEY_USER_ID);
        removeSetting(context, KEY_USER_PSW);
        removeSetting(context, KEY_PAYMENT_NUMBER);
        removeSetting(context, KEY_CALL_CENTER_NUMBER);
        removeSetting(context, KEY_CALL_CENTER_EMAIL);
        removeSetting(context, KEY_PRICE);
        removeSetting(context, KEY_API_KEY);
    }
}

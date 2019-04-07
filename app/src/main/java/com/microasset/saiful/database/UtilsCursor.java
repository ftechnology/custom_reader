/**
 * @author Mohammad Saiful Alam
 * Database cursor utility to provide ease access
 */
package com.microasset.saiful.database;

import android.database.Cursor;

/**
 * This class represent common utilities for Cursor
 */
public class UtilsCursor {
    /**
     * Get the column value
     *
     * @param key    -table column
     * @param cursor -Cursor
     * @return String
     */
    public static String getStringFromCursor(String key, Cursor cursor) {
        String value = "";
        int colIndex = cursor.getColumnIndex(key);
        if (colIndex > -1) {
            value = cursor.getString(colIndex);
        }
        return value;
    }

    /**
     * Get the column value
     *
     * @param key    -table column
     * @param cursor -Cursor
     * @return integer
     */
    public static int getIntFromCursor(String key, Cursor cursor) {
        int value = 0;
        int colIndex = cursor.getColumnIndex(key);
        if (colIndex > -1) {
            value = cursor.getInt(colIndex);
        }
        return value;
    }

    public static long getLongFromCursor(String key, Cursor cursor) {
        long value = 0;
        int colIndex = cursor.getColumnIndex(key);
        if (colIndex > -1) {
            value = cursor.getLong(colIndex);
        }
        return value;
    }
}
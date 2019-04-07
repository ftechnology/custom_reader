/**
 * @author Mohammad Saiful Alam
 * Create and hold the database. Manage the query.
 */
package com.microasset.saiful.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.microasset.saiful.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
public class DatabaseController {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    //
    public static String TAG = "DatabaseController";
    // TODO Database Name
    // Change this name
    public static String DATABASE_NAME = "com.microasset.saiful.easyreader.db";
    // The instance of this class
    static private DatabaseController mInstance = null;
    // The helper class for creating and updating the DB
    protected DatabaseHelper mHelper = null;
    // The actual database instance that execute the query.
    protected SQLiteDatabase mDatabase = null;
    //


    private DatabaseController() {
    }

    /**
     * Constructor function
     * // * @param context
     */

    synchronized static public DatabaseController getInstance() {
        if (mInstance == null) {
            mInstance = new DatabaseController();
        }

        return mInstance;
    }

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
            Log.d(TAG, "File lenght: " + file.length());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found" + e);
        } catch (IOException ioe) {
            Log.d(TAG, "Exception while reading file " + ioe);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                // System.out.println("Error while closing stream: " + ioe);
                Log.d(TAG, "Error while closing stream: " + ioe);
            }

        }
        return fileContent;

    }

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
            Log.d(TAG, e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Return true if the file exits in the given location.
     *
     * @param fullPath
     * @return
     */
    static public boolean exists(String fullPath) {
        File file = new File(fullPath);

        return file.exists();
    }

    /**
     * @param context
     */
    public void createDatabase(Context context, String dbName, int versionCode) {

        DATABASE_NAME = dbName;
        mHelper = new DatabaseHelper(context, DATABASE_NAME, versionCode);
        open();

    }

    public void upDateDatabase() {

    }

    /**
     * Delete the database.
     *
     * @param context // * @param name
     */
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    /**
     * @param context
     */
    //@SuppressLint("SdCardPath")
    public void createDatabaseFromExternalFile(Context context, String dbName, int versionCode) {
        //FIXME
        DATABASE_NAME = dbName;
        mHelper = new DatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION);
        mHelper.setDatabaseCreated(false);
        //
        String dbPath = Constants.PATH_APPLICAITON + "/databases/";

        //if (android.os.Build.VERSION.SDK_INT >= 17) {
        //    dbPath = context.getApplicationInfo().dataDir + "/databases/";
        //}

        String fullPath = dbPath + DATABASE_NAME;

        //context.getAssets().open(DATABASE_NAME);
        ////String fullPath ="file:///android_asset/" + DATABASE_NAME;
        //mHelper.setExternalDatabase(SQLiteDatabase.openDatabase(fullPath, null, SQLiteDatabase.OPEN_READWRITE));

        try {
           /* if (!DatabaseController.exists(fullPath)) {
                mHelper.getReadableDatabase();
                mHelper.close();
                DatabaseController.copyFromAsset(context, DATABASE_NAME, fullPath);
                mHelper.setDatabaseCreated(true);
                // TRIGGER on create to update this databse
                //SQLiteDatabase db = mHelper.getWritableDatabase();
                //copyDataBase(context, DATABASE_NAME, DB_PATH);
            }*/

            createDatabase(context, fullPath, versionCode );

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Open the database, this method must be called before executing any operation.
     * By default
     */
    private synchronized void open() {

        mDatabase = mHelper.getReadableDatabase();
    }

    /**
     * Close the database when not needed
     */
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mHelper.close();
    }


    /**
     * Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     * It has no means to return any data (such as the number of affected rows).
     *
     * @param sql
     */
    public void execSQL(String sql) {
        mDatabase.execSQL(sql);
    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @return A Cursor object, which is positioned before the first entry.
     */


    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {

        try {
            mDatabase = this.mHelper.getReadableDatabase();
            Cursor cursor = mDatabase.query(table,
                    columns,
                    selection,
                    selectionArgs,
                    groupBy,
                    having,
                    orderBy);

            return cursor;
            //response.setValues(0, "SuccesfulQueryMessage", cursor);
        } catch (Exception e) {
            //response.setResponseMsg(e.getMessage());
            Log.d(TAG, e.getMessage());
            //e.printStackTrace();
        }

        return null;
    }


    /**
     * Convenience method for inserting a row into the database.
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */

    public long insert(String table, String nullColumnHack, ContentValues values) {

        try {
            mDatabase = this.mHelper.getWritableDatabase();
            long ret = mDatabase.insert(table,
                    null,
                    values);

            return ret;

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return -1;
    }

    /**
     * Convenience method for deleting rows in the database.
     *
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */

    public int delete(String table, String whereClause, String[] whereArgs) {
        try {
            mDatabase = this.mHelper.getWritableDatabase();
            int ret = mDatabase.delete(table,
                    whereClause,
                    whereArgs);
            return ret;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            //e.printStackTrace();
        }
        return -1;
    }

    /**
     * Convenience method for updating rows in the database.
     *
     * @return the number of rows affected
     */

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        try {

            mDatabase = this.mHelper.getWritableDatabase();
            int ret = mDatabase.update(table,
                    values,
                    whereClause,
                    whereArgs);

            return ret;
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            //e.printStackTrace();
        }
        return -1;
    }

    /**
     * Runs the provided SQL and returns a Cursor over the result set.
     *
     * @return A Cursor object, which is positioned before the first entry.
     */

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        try {
            mDatabase = this.mHelper.getReadableDatabase();
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            return cursor;
        } catch (Exception e) {
        }

        return null;
    }
}

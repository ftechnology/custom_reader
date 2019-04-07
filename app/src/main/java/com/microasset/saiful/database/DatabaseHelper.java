/**
 * @author Mohammad Saiful Alam
 * Helper class for database controller
 */
package com.microasset.saiful.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // The tables that holds the create table and drop table script
    public static Tables mTables = new Tables();
    // Set false only for external database
    private boolean mDatabaseCreated = true;

    public DatabaseHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
    }

    /**
     * @return the mDatabaseCreated
     */
    public boolean isDatabaseCreated() {
        return mDatabaseCreated;
    }

    /**
     * // * @param mDatabaseCreated the mDatabaseCreated to set
     */
    public void setDatabaseCreated(boolean databaseCreated) {
        this.mDatabaseCreated = databaseCreated;
    }

    /**
     * Create the table from here
     */
    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
        // Create all tables here..
        // For external database we need another confirmation for this.
        if (mDatabaseCreated) {
            this.createTable(arg0);
        }
    }

    /**
     * Upgrade the tables
     */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
       // this.dropTable(arg0);
       // this.createTable(arg0);
    }

    /**
     * Create tables one by one from the mTables
     *
     * @param arg0
     */
    public void createTable(SQLiteDatabase arg0) {
        String sql = "";

        for (int i = 0; i < mTables.size(); i++) {
            sql = mTables.get(i);
            arg0.execSQL(sql);

        }
    }

    /**
     * Drop table one by one from the mTables
     *
     * @param arg0
     */
    public void dropTable(SQLiteDatabase arg0) {
        String sql = "";
        int numberOfTable = mTables.getDropTableList().size();
        for (int i = 0; i < numberOfTable; i++) {
            sql = mTables.getDropTableList().get(i);
            arg0.execSQL(sql);
        }
    }
}

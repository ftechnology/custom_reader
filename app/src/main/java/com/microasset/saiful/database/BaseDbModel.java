/**
 * @author Mohammad Saiful Alam
 * Base class for all database adapters
 */
package com.microasset.saiful.database;

import android.content.Context;
import android.database.Cursor;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;


import java.util.List;

// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
public abstract class BaseDbModel extends BaseModel {
    //protected BaseDbModel mInstance;
    //
    protected DatabaseController mDatabaseController;

    //
    public BaseDbModel(Context context) {
        super(context);
        mDatabaseController = DatabaseController.getInstance();
    }

    /**
     * Load the data from cursor
     *
     * @param cursor
     */
    protected abstract void loadData(Cursor cursor);

    /**
     * Insert the entity
     *
     * @param entity
     * @return
     */
    abstract public long insert(BaseEntity entity);

    //Insert list of entity using different thread. This notifyObserver for observer is posted using Handler.
    public void insertListAsync(final List<BaseEntity> entityList, final NotifyObserver observer) {
        this.setNotifyObserver(observer);
        final ResponseObject response = new ResponseObject();
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (BaseEntity entity : entityList) {
                        long res = ((BaseDbModel) mInstance).insert(entity);
                        response.setResponseMsg("Continue");
                        //mInstance.notifyObserver(response);
                    }
                    response.setResponseMsg("SuccessfulinsertListAsyncMessage");
                    mInstance.notifyObserver(response);
                } catch (Exception e) {
                    response.setResponseMsg(e.getMessage());
                    // Error: Need to handle
                    mInstance.notifyObserver(response);
                }
            }
        }).start();
    }

    /**
     * Retrieve all information from database in thread. This notifyObserver for observer is posted using Handler
     */
    public ResponseObject queryAsync(NotifyObserver observer) {
        this.setNotifyObserver(observer);
        final ResponseObject response = new ResponseObject();
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((BaseDbModel) mInstance).query();
                    response.setResponseMsg("SuccesfulQueryMessage");
                    mInstance.notifyObserver(response);
                } catch (Exception e) {
                    response.setResponseMsg(e.getMessage());
                    // Error: Need to handle
                    mInstance.notifyObserver(response);
                }
            }
        }).start();

        return null;
    }

    /**
     * Update the entity
     *
     * @param entity
     * @return
     */
    abstract public int update(BaseEntity entity);

    /**
     * Delete the entity
     * // * @param entity
     *
     * @return
     */
    public int delete(String ColID, int id, String table) {
        String[] whereArgs = new String[]{DatabaseController.toString(id)};
        // FIXME if required...
        String where = ColID + "=?";
        int row = mDatabaseController.delete(table, where, whereArgs);
        return row;
    }

    /**
     * Query the database
     *
     * @return
     */
    abstract public void query();

    /**
     * @param table
     * @return
     */
    public int clearTable(String table) {
        int row = mDatabaseController.delete(table, "", null);
        return row;
    }
}


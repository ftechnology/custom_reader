/**
 * 
 * @author Mohammad Saiful Alam
 * Setting BookmarkDbModel
 *
 */
package com.microasset.saiful.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.database.BaseDbModel;
import com.microasset.saiful.database.BaseEntity;
import com.microasset.saiful.database.UtilsCursor;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.BookEntity.Table;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.util.Convert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// MAP with SettingTable..

//Help from : http://www.tutorialspoint.com/android/android_sqlite_database.htm
public  class DrawingObjectDbModel extends BaseDbModel {

	static private DrawingObjectDbModel mSettingDbModel;

	public  static DrawingObjectDbModel getInstance(Context context){
		if(mSettingDbModel == null){
			mSettingDbModel = new DrawingObjectDbModel(context);
		}

		mSettingDbModel.mContext = context;
		return  mSettingDbModel;
	}

	@Override
	public ResponseObject execute() {
		return null;
	}

	/**
	 * Constructor function
	 * @param context
	 */
	public DrawingObjectDbModel(Context context) {
		super(context);
	}

	/**
	 * Update an entity into database
	 */
	@Override
	public int update(BaseEntity entity) {
		DrawingObjectEntity item = (DrawingObjectEntity) entity;
		ContentValues cv = new ContentValues();
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		cv.put(Table.PAGE_NUMBER, item.getPagenumber());
		cv.put(Table.TYPE, item.getType());
		cv.put(DrawingObjectEntity.Table.TABLE_DATA_JSON, item.getJsonData());
		//
		String where = Table.ID + "=?";
		String[] args = new String[]{ Convert.toString(entity.getId())};
		int c = mDatabaseController.update(DrawingObjectEntity.Table.TABLE_NAME, cv, where, args);

        return c;
	}

	/**
	 * Retrieve all information from database
	 */
	public void query() {
		Cursor c = mDatabaseController.query(DrawingObjectEntity.Table.TABLE_NAME, null, null, null, null, null, null);
		this.loadData(c);
	}

	public void rawQuery(String query) {
		Cursor c = mDatabaseController.rawQuery(query, null);
		this.loadData(c);
	}

	public int delete(long id) {
		return mDatabaseController.delete(DrawingObjectEntity.Table.TABLE_NAME, Table.ID + "=?", new String[]{id + ""});
	}


	/**
	 * Load all information from database
	 */
	@Override
	protected void loadData(Cursor cursor) {
        // Remove the old data
        this.clear(false);
		// TODO Auto-generated method stub
		if(cursor == null) return;
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
        	
        	// Remove the old data
        	this.clear(false);
        	
            do {
            	DrawingObjectEntity entity = new DrawingObjectEntity(0);
                //contact.setID(Integer.parseInt(cursor.getString(0)));
            	//LIKE ANY ONE ADD ITEMS..
            	entity.setId(UtilsCursor.getIntFromCursor(Table.ID, cursor));
            	entity.setBookId(UtilsCursor.getStringFromCursor(Table.BOOK_ID, cursor));
				entity.setType(UtilsCursor.getStringFromCursor(Table.TYPE, cursor));
				entity.setInsertDate(UtilsCursor.getStringFromCursor(Table.INSERT_DATE, cursor));
            	entity.setPageNumber(UtilsCursor.getStringFromCursor(Table.PAGE_NUMBER, cursor));
				entity.setJsonData(UtilsCursor.getStringFromCursor(DrawingObjectEntity.Table.TABLE_DATA_JSON, cursor));
                // Adding contact to list
                this.add(entity);
                
            } while (cursor.moveToNext());
        }
        
        // Must close this cursor
        if(!cursor.isClosed()) {
        	cursor.close();
        }   
	}

	@Override
	public long insert(BaseEntity entity) {
		DrawingObjectEntity item = (DrawingObjectEntity) entity;
		ContentValues cv = new ContentValues();
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		cv.put(Table.PAGE_NUMBER, item.getPagenumber());
		cv.put(Table.TYPE, item.getType());
		cv.put(DrawingObjectEntity.Table.TABLE_DATA_JSON, item.getJsonData());
		//
		long row = mDatabaseController.insert(DrawingObjectEntity.Table.TABLE_NAME, null, cv);

		return row;
	}

	public  ArrayList<DataObject> getDrawingList(String bookId, String pageNumber){

		String query = "SELECT * FROM "+
				DrawingObjectEntity.Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				"="+ "'"+
				bookId+
				"'"+
			    " AND "+
                Table.PAGE_NUMBER +
                "="+ "'"+
                pageNumber +
                "'";

		this.rawQuery(query);

		return mListItem;
	}

	public  ArrayList<DataObject> getDrawingList(String bookId){

		String query = "SELECT * FROM "+
				DrawingObjectEntity.Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				"="+ "'"+
				bookId+
				"'";
		this.rawQuery(query);

		return mListItem;
	}

	public  ArrayList<DataObject> getDrawingdListNonDuplicateUsingPageNumber(String bookId){

		String query = "SELECT * FROM "+
				DrawingObjectEntity.Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				"="+ "'"+
				bookId+
				"'"+
				" GROUP BY " +
				DrawingObjectEntity.Table.PAGE_NUMBER;

		this.rawQuery(query);

		return mListItem;
	}
}


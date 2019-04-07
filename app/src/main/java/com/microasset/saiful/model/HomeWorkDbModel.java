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
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.database.BaseDbModel;
import com.microasset.saiful.database.BaseEntity;
import com.microasset.saiful.database.UtilsCursor;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.entity.HomeWorkEntity.Table;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.Utill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// MAP with SettingTable..

//Help from : http://www.tutorialspoint.com/android/android_sqlite_database.htm
public  class HomeWorkDbModel extends BaseDbModel {

	static private HomeWorkDbModel mSettingDbModel;

	public  static HomeWorkDbModel getInstance(Context context){
		if(mSettingDbModel == null){
			mSettingDbModel = new HomeWorkDbModel(context);
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
	public HomeWorkDbModel(Context context) {
		super(context);
	}

	/**
	 * Update an entity into database
	 */
	@Override
	public int update(BaseEntity entity) {
		HomeWorkEntity item = (HomeWorkEntity) entity;
		ContentValues cv = new ContentValues();
		//cv.put(Table.ID, item.getId());
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		cv.put(Table.PAGE_CONTENT, item.getPageContent());
		cv.put(Table.PAGE_NUMBER, item.getPagenumber());
		//
		String[] args = new String[]{ Convert.toString(entity.getId())};

		// TODO FIXME we have to code...

        return -1;
	}

	/**
	 * Retrieve all information from database
	 */
	public void query() {
		Cursor c = mDatabaseController.query(Table.TABLE_NAME, null, null, null, null, null, null);
		this.loadData(c);
	}

	public void rawQuery(String query) {
		Cursor c = mDatabaseController.rawQuery(query, null);
		this.loadData(c);
	}

	public void delete(long id) {
		mDatabaseController.delete(Table.TABLE_NAME, Table.ID + "=?", new String[]{id + ""});
		// LOAD aagain...
		//query();
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
            	HomeWorkEntity entity = new HomeWorkEntity(0,"HomeWorkEntity");
                //contact.setID(Integer.parseInt(cursor.getString(0)));
            	//LIKE ANY ONE ADD ITEMS..
            	entity.setId(UtilsCursor.getIntFromCursor(Table.ID, cursor));
            	entity.setBookId(UtilsCursor.getStringFromCursor(Table.BOOK_ID, cursor));
				entity.setPageContent(UtilsCursor.getStringFromCursor(Table.PAGE_CONTENT, cursor));
				entity.setInsertDate(UtilsCursor.getStringFromCursor(Table.INSERT_DATE, cursor));
				entity.setPageNumber(UtilsCursor.getStringFromCursor(Table.PAGE_NUMBER, cursor));
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
		HomeWorkEntity item = (HomeWorkEntity) entity;
		ContentValues cv = new ContentValues();
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		cv.put(Table.PAGE_CONTENT, item.getPageContent());
		cv.put(Table.PAGE_NUMBER, item.getPagenumber());
		long row = mDatabaseController.insert(Table.TABLE_NAME, null, cv);

		return row;
	}

	public long insert(String book, String content, String datetime, String pageNum){
		// public HomeWorkEntity(String bookID, String pageContent, String insertDate)
		HomeWorkEntity homeWorkEntity = new HomeWorkEntity(book, content, Utill.getFormattedDate(), pageNum);
		return insert(homeWorkEntity);
	}

	public ArrayList<DataObject> getHomeworkList(String bookId){
		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				" = "+ "'"+
				bookId+
				"'";

		this.rawQuery(query);

		return  this.mListItem;
	}


	public HomeWorkEntity hasHomework(String bookId, String pageNumber){

		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
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

		for(int i = 0; i<this.getCount(); i++){
			HomeWorkEntity object = (HomeWorkEntity) this.getItem(i);
			String page = object.getPagenumber();
			int r1 = page.compareToIgnoreCase(pageNumber);
			if(r1 == 0){
				return object;
			}
		}
		return null;
	}

	public long getId(String book){
		// After add we have to update this Dummy value..
		long id = HomeWorkDbModel.getInstance(mContext).insert(new HomeWorkEntity(book,
				"Dummy",
				Utill.getFormattedDate(), "Dummy"));

		return id;
	}
}


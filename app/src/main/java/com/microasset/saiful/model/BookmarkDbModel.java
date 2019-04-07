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
import com.microasset.saiful.util.Convert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// MAP with SettingTable..

//Help from : http://www.tutorialspoint.com/android/android_sqlite_database.htm
public  class BookmarkDbModel extends BaseDbModel {

	static private BookmarkDbModel mSettingDbModel;

	public  static BookmarkDbModel getInstance(Context context){
		if(mSettingDbModel == null){
			mSettingDbModel = new BookmarkDbModel(context);
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
	public BookmarkDbModel(Context context) {
		super(context);
	}

	/**
	 * Update an entity into database
	 */
	@Override
	public int update(BaseEntity entity) {
		BookEntity item = (BookEntity) entity;
		ContentValues cv = new ContentValues();
		//cv.put(Table.ID, item.getId());
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
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
		mDatabaseController.delete(Table.TABLE_NAME, BookEntity.Table.ID + "=?", new String[]{id + ""});
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
            	BookEntity entity = new BookEntity(0,"EntitySetting");
                //contact.setID(Integer.parseInt(cursor.getString(0)));
            	//LIKE ANY ONE ADD ITEMS..
            	entity.setId(UtilsCursor.getIntFromCursor(Table.ID, cursor));
            	entity.setBookId(UtilsCursor.getStringFromCursor(Table.BOOK_ID, cursor));
				entity.setType(UtilsCursor.getStringFromCursor(Table.TYPE, cursor));
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
		BookEntity item = (BookEntity) entity;
		ContentValues cv = new ContentValues();
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		cv.put(Table.PAGE_NUMBER, item.getPagenumber());
		cv.put(Table.TYPE, item.getType());
		long row = mDatabaseController.insert(Table.TABLE_NAME, null, cv);

		return row;
	}


	public ArrayList<DataObject> getBookMarkList(String bookId){
		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				" = "+ "'"+
				bookId+
				"'"+
				" AND "+
				Table.TYPE +
				" = "+ "'"+
				BookEntity.Type_Bookmark+
				"'";

		this.rawQuery(query);

		return  this.mListItem;
	}

	public  BookEntity hasBookMark(String bookId, String pageNumber){

		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				"="+ "'"+
				bookId+
				"'"+
				" AND "+
				Table.TYPE +
				"="+ "'"+
				BookEntity.Type_Bookmark+
				"'" +
                " AND "+
                Table.PAGE_NUMBER +
                "="+ "'"+
                pageNumber +
                "'";

		this.rawQuery(query);

		for(int i = 0; i<this.getCount(); i++){
			BookEntity object = (BookEntity) this.getItem(i);
			String page = object.getPagenumber();
			int r1 = page.compareToIgnoreCase(pageNumber);
			if(r1 == 0){
				return object;
			}
		}
		return null;
	}

	public  BookEntity getLastReadingPosition(String bookId){

		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				" = "+ "'"+
				bookId+
				"'"+
				" AND "+
				Table.TYPE +
				" = "+ "'"+
				BookEntity.Type_LastReadingPosition+
				"'";
		//SELECT * FROM Customers WHERE City = 'London' AND Country = 'UK'
		this.rawQuery(query);

		for(int i = 0; i<this.getCount(); i++){
			BookEntity object = (BookEntity) this.getItem(i);
			String id = object.getBookId();
			String page = object.getPagenumber();
			int r = bookId.compareToIgnoreCase(id);

			if(r == 0){
				return object;
			}
		}
		return null;
	}

	public void saveLastReadingPosition(String bookID, String pageNumber){

		BookEntity entity = getLastReadingPosition(bookID);
		if(entity != null){
			delete(entity.getId());
		}
		Date now = new Date();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		String year = simpleDateformat.format(now);
		simpleDateformat = new SimpleDateFormat("EEEE",Locale.US);
		String day = simpleDateformat.format(now);
		String date = year + ", " + day;
		LogUtil.d("SS", "date = " + date);
		//
		insert(new BookEntity( bookID, pageNumber, date, BookEntity.Type_LastReadingPosition));
	}
}


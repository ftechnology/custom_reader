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
import com.microasset.saiful.entity.RecentlyOpenEntity;
import com.microasset.saiful.entity.RecentlyOpenEntity.Table;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import java.util.ArrayList;

// MAP with SettingTable..

//Help from : http://www.tutorialspoint.com/android/android_sqlite_database.htm
public  class RecentlyOpenDbModel extends BaseDbModel {

	static private RecentlyOpenDbModel mSettingDbModel;

	public  static RecentlyOpenDbModel getInstance(Context context){
		if(mSettingDbModel == null){
			mSettingDbModel = new RecentlyOpenDbModel(context);
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
	public RecentlyOpenDbModel(Context context) {
		super(context);
	}

	/**
	 * Update an entity into database
	 */
	@Override
	public int update(BaseEntity entity) {
		RecentlyOpenEntity item = (RecentlyOpenEntity) entity;
		ContentValues cv = new ContentValues();
		//cv.put(Table.ID, item.getId());
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.CLASS_NAME, item.getClassName());
		String version = item.getClassVersion();
		if(version.isEmpty())
			version = "B";
		cv.put(Table.CLASS_VERSION, version);
		cv.put(Table.IMAGE_PATH, item.getImagePath());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		//
		String where = Table.ID + "=?";
		String[] args = new String[]{ Convert.toString(entity.getId())};

		// TODO FIXME we have to code...
		int row = mDatabaseController.update(Table.TABLE_NAME, cv, where, args);

        return row;
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
				RecentlyOpenEntity entity = new RecentlyOpenEntity(0,"EntitySetting");
                //contact.setID(Integer.parseInt(cursor.getString(0)));
            	//LIKE ANY ONE ADD ITEMS..
            	entity.setId(UtilsCursor.getIntFromCursor(Table.ID, cursor));
            	entity.setBookId(UtilsCursor.getStringFromCursor(Table.BOOK_ID, cursor));
				entity.setClassName(UtilsCursor.getStringFromCursor(Table.CLASS_NAME, cursor));
				entity.setClassVersion(UtilsCursor.getStringFromCursor(Table.CLASS_VERSION, cursor));
				entity.setImagePath(UtilsCursor.getStringFromCursor(Table.IMAGE_PATH, cursor));
				entity.setInsertDate(UtilsCursor.getStringFromCursor(Table.INSERT_DATE, cursor));
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
		RecentlyOpenEntity item = (RecentlyOpenEntity) entity;
		ContentValues cv = new ContentValues();
		cv.put(Table.BOOK_ID, item.getBookId());
		cv.put(Table.CLASS_NAME, item.getClassName());
		String version = item.getClassVersion();
		if(version.isEmpty())
			version = "B";
		cv.put(Table.CLASS_VERSION, version);
		cv.put(Table.IMAGE_PATH, item.getImagePath());
		cv.put(Table.INSERT_DATE, item.getInsertDate());
		long row = mDatabaseController.insert(Table.TABLE_NAME, null, cv);

		//Delete extra items if exceed 5
		ArrayList<DataObject> list = getRecentlyOpenList(item.getClassName(), item.getClassVersion());
		if(list.size() > Constants.RECENTLY_OPEN_MAX_COUNT){
			for(int i= 0; i<(list.size() - Constants.RECENTLY_OPEN_MAX_COUNT); i++){
				RecentlyOpenEntity item1 = (RecentlyOpenEntity) list.get(i);
				long id = item1.getId();
				this.delete(id);
			}
		}
		return row;
	}

	public ArrayList<DataObject> getRecentlyOpenList(String className, String classVersion){
		if(classVersion.isEmpty())
			classVersion = "B";
		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.CLASS_NAME+
				" = "+ "'"+
				className+
				"'"+
				" AND "+
				Table.CLASS_VERSION +
				" = "+ "'"+
				classVersion+
				"'";

		this.rawQuery(query);

		return  this.mListItem;
	}

	public RecentlyOpenEntity hasRecentlyOpen(String bookId){

		String query = "SELECT * FROM "+
				Table.TABLE_NAME     +
				" WHERE "+
				Table.BOOK_ID+
				"="+ "'"+
				bookId+
				"'";

		this.rawQuery(query);

		for(int i = 0; i<this.getCount(); i++){
			RecentlyOpenEntity object = (RecentlyOpenEntity) this.getItem(i);
			String id = object.getBookId();
			int r1 = id.compareToIgnoreCase(bookId);
			if(r1 == 0){
				return object;
			}
		}
		return null;
	}
}


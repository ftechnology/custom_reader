/**
 * 
 * @author Mohammad Saiful Alam
 * FIXME
 *
 */
package com.microasset.saiful.appfrw;

import android.app.Application;
import android.content.SharedPreferences;

import com.microasset.saiful.database.DatabaseController;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;

import java.io.File;

// Put this in the manifest file
// android:name="com.microasset.saiful.appfrw.BaseApplication">
// Here for example:  com.bjit.alquran.mvc is the name of application package.
public class BaseApplication extends Application{

	 public void onCreate () {
		 super.onCreate();
		 Utill.setLocale(SharedPrefUtil.getSetting(this, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), this);
		 if(PermissionManager.getInstance().hasAllPermissions(this)){
			 this.init();
		 }
	}
	
	public void onTerminate ()  {
	    super.onTerminate() ;
	}
	
	/**
	 * We need a folder, localization, or other data that should be available in whole application.
	 */
	public void init() {

		// Create Download Folder.
		File file = new File(Constants.PATH_DOWNLOAD);
		boolean success = file.mkdirs();

		file = new File(Constants.PATH_BOOK);
		success = file.mkdirs();

		LogUtil.d("success");
		//
		//mDbHelper=new DatabaseHelper(this,"com.sourcenext.pocketalk.db",4);
		DatabaseController databaseConterller = DatabaseController.getInstance();
		databaseConterller.createDatabaseFromExternalFile(this, DatabaseController.DATABASE_NAME, getDataBaseCurrentVersion());

		BookmarkDbModel.getInstance(this).query();
	}

	public int getDataBaseCurrentVersion() {
		SharedPreferences preferences = getSharedPreferences("DB_SHARED_PREF", 0);
		return preferences.getInt("DB_VERSION", 1);
	}

}

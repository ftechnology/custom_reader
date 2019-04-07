package com.microasset.saiful.easyreader;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.PermissionManager;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.database.DatabaseController;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.model.CountryCodeModel;
import com.microasset.saiful.model.DeviceDetailModel;
import com.microasset.saiful.model.DictionaryEngToBanglaModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;

import java.io.File;
import java.util.Locale;

import static com.microasset.saiful.licence.RegistrationManager.NEW_VERSION_EXISTS;

public class LanucherActivity extends BaseActivity {
    private int SPLASH_TIME = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utill.setLocale(SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), mInstance);
    }

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.launcher_layout);

       /*
        final FirebaseDbMeta firebaseDbMeta = FirebaseDbMeta.getInstance();
        firebaseDbMeta.getMeta(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseMsg().equals(ResponseObject.mSuccess)){
                    FirebaseDbMeta.Meta meta =  (FirebaseDbMeta.Meta)response.getDataObject();
                    // TODO ..When Need to subscription
                    // When need to get support..
                    // When need to check app update...
                }
            }
        });
        */

        DictionaryEngToBanglaModel.getInstance(mInstance).executeAsyn(null);
        CountryCodeModel.getInstance(mInstance).executeAsyn(null);
        //
        if (PermissionManager.getInstance().hasAllPermissions(this)) {
            DeviceDetailModel.getInstance(mInstance).execute();
            RegistrationManager.getInstance(mInstance).checkForUpdate(new NotifyObserver() {
                @Override
                public void update(ResponseObject response) {
                    if (response.getResponseCode() == NEW_VERSION_EXISTS) {
                        showAppUpdateDialog();
                    } else {
                        makeDecision();
                    }
                }
            });
        } else {
            //THIS IS FIRST LAUNCH AFTER INSTALLATION...some devices store values even after uninstallation.
            SharedPrefUtil.resetAllSetting(mInstance);
            PermissionManager.getInstance().requestPermission(this);
        }
    }

    private void makeDecision() {
        Thread timer = new Thread() {
            public void run() {
                try {
                    RegistrationManager.getInstance(mInstance).hasRegistration();
                    sleep(SPLASH_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    String user = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_USER_ID, "");
                    if (user.isEmpty()) {
                        mInstance.startActivity(PhoneAuthActivity.class);
                        finish();
                    } else if (!SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_CATEGORY_CLASS, "").isEmpty()) {
                        mInstance.startActivity(MainActivity.class);
                        finish();
                    } else {
                        mInstance.startActivity(CategoryActivity.class);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }

    protected void showAppUpdateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mInstance);
        alertDialogBuilder.setTitle(R.string.STR_UPDATE_APP);
        alertDialogBuilder.setMessage(R.string.STR_NEW_VERSION_DOWNLOAD);
        alertDialogBuilder.setNegativeButton(R.string.STR_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeDecision();
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.STR_UPDATE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utill.rateApp(LanucherActivity.this);
            }
        });
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                makeDecision();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void initDb() {

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

    @Override
    protected void createAdapter() {
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void doUpdateRequest(ResponseObject response) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionManager.PERMISSION_ALL) {
            // 使用が許可された
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && PermissionManager.getInstance().hasAllPermissions(LanucherActivity.this)) {
                // SO NOW NEED TO LOAD THE DEVICE INFO...
                DeviceDetailModel.getInstance(mInstance).execute();
                initDb();
                RegistrationManager.getInstance(mInstance).checkForUpdate(new NotifyObserver() {
                    @Override
                    public void update(ResponseObject response) {
                        if (response.getResponseCode() == NEW_VERSION_EXISTS) {
                            showAppUpdateDialog();
                        } else {
                            makeDecision();
                        }
                    }
                });
            } else {
                finish();
            }
        }
    }
}


/**
 * @author Mohammad Saiful Alam
 * RegistrationManager model
 */

package com.microasset.saiful.licence;

import android.content.Context;
import android.net.ParseException;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.easyreader.BuildConfig;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.DeviceDetailModel;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.Firebase;
import com.microasset.saiful.web.FirebaseDb;
import com.microasset.saiful.web.FirebaseDbMeta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class RegistrationManager extends Object {
    String LICENCE_STANDARD = "LICENCE_STANDARD";
    public static String LICENCE_TRIAL = "LICENCE_TRIAL";
    public static String PAYMENT_TYPE_MANUAL = "PAYMENT_TYPE_MANUAL";
    //String LICENCE_STANDARD = "LICENCE_STANDARD";
    private static String mUiqueID = null;
    static public int SUCCESS_USER_REGISTRATION  = 0;
    static public int SUCCESS_USER_PAYMENT = 2;
    static public int USER_PAYMENT_NEEDED = 3;
    static public int USER_EXISTS  = 5;
    static public int NEW_VERSION_EXISTS  = 10;
    static public int UPDATE_USER_PLAN_SUCCESS  = 100;
    static public int UPDATE_USER_PLAN_FAILED  = -100;
    //
    static public int USER_USED_MAX_DEVICE_USING_CURRENT_PLAN  = -1000;
    static public int ERROR_USER_PAYMENT_NOT_VARIFIED  = -2;
    static public int ERROR_USER_PAYMENT_NEEDED  = -3;
    //
    private boolean isAllow = false;
    FirebaseDb.User mUser = null;

    static public RegistrationManager registrationManager;
    //
    protected Context mContext;
    protected DataObject mObject;
    //
    private RegistrationManager(Context context) {
        mObject = new DataObject(0);
        mContext = context;
    }

    public static RegistrationManager getInstance(Context context) {
        if (registrationManager == null) {
            registrationManager = new RegistrationManager(context);
        }
        return registrationManager;
    }

    public  DataObject getData(){
        return mObject;
    }

    public boolean isAllow(boolean skipFreeBook) {
        if(!skipFreeBook){
            if(BookInfoModel.getInstance(mContext).getmMetaData().Free.equals("true")){
                return true;
            }
        }
        if(mUser != null) {
            if (isSubcriptionValidToday(mUser.subscription_renew_date)) {
                isAllow = true;
            }
        }
        return isAllow;
    }

    public void setAllow(boolean allow) {
        isAllow = allow;
    }

    public  boolean hasRegistration(){
        String token = SharedPrefUtil.getSetting(mContext,SharedPrefUtil.KEY_LICENCE, "");
        if(isTimeChangedFromLastTime()){
            // MAY BE USER CHANFED THE TIME..SO NEED TO RECHECK THE REGISTRATION..AGAIN....FROM SERVER
            // AS USER MAY HAVE DUE IN AMOUNT ALSO..
            isAllow = false;
            //We need user data to validate with server...
            loadUserData();
            mUser =  new FirebaseDb.User().fromJson(token);
            // As the time is changed so we dont know about the renew status...
            mUser.subscription_renew_date = "";
            mObject.setValue(SharedPrefUtil.KEY_LICENCE, mUser);
            return false;
        }
        if(token.isEmpty()){
            return  false;
        } else if(mUser != null && mUiqueID != null){
           return true;
        }

        loadUserData();

        if(mUser != null && mUiqueID != null){
            return true;
        }
        //Token: YYYY-MM-DD..Validity of Licence..
        // How can we check May be need to check with server here...
        //IF token is invalid return false...
        //
        return false;
    }

    private void loadUserData(){
        String token = SharedPrefUtil.getSetting(mContext,SharedPrefUtil.KEY_LICENCE, "");

        try {
            if(token == ""){
                isAllow = false;
                return;
            }

            mObject = new DataObject(0);
            mUser =  new FirebaseDb.User().fromJson(token);
            mObject.setValue(SharedPrefUtil.KEY_LICENCE, mUser);
            if(isSubcriptionValidToday(mUser.subscription_renew_date)){
                isAllow = true;
            }

        }catch (Exception e){
            isAllow = false;
        }
    }

    private boolean isSubcriptionValidToday(String subscription_renew_date_by_user){
        try {
            // This means User can Register can for more then one 1 months
            int quantity = 1;
            quantity = Convert.toInt(mUser.quantity);

            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            Date today = null;
            Date subscription_renew_date = null;
            today = format.parse(Utill.getFormattedDate());
            subscription_renew_date = format.parse(subscription_renew_date_by_user);
            // ADD ONE MONTH
            Calendar calendar_subscription_renew_date = Calendar.getInstance();
            calendar_subscription_renew_date.setTimeInMillis(subscription_renew_date.getTime());
            calendar_subscription_renew_date.add(Calendar.MONTH, 1 + quantity);
            //calendar_subscription_renew_date.add(Calendar.DAY_OF_MONTH, 1);
            //calendar_subscription_renew_date.add(Calendar.HOUR_OF_DAY, 1 + quantity);
            Date after_one_montth = calendar_subscription_renew_date.getTime();
            //
            Calendar calendar_today = Calendar.getInstance();
            calendar_today.setTimeInMillis(today.getTime());
            if(calendar_today.compareTo(calendar_subscription_renew_date) < 0){
                return true;
            }

        }catch (Exception e){
            return false;
        }
        return false;
    }

    public String getSubscriptionValidDate(){
        try {
            // This means User can Register can for more then one 1 months
            int quantity = 1;
            quantity = Convert.toInt(mUser.quantity);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            Date subscription_renew_date = null;
            subscription_renew_date = format.parse(mUser.subscription_renew_date);
            // ADD ONE MONTH
            Calendar calendar_subscription_renew_date = Calendar.getInstance();
            calendar_subscription_renew_date.setTimeInMillis(subscription_renew_date.getTime());
            calendar_subscription_renew_date.add(Calendar.MONTH, 1 + quantity);
            String expireDate = "";
            try {
                expireDate = format.format(calendar_subscription_renew_date.getTime());
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            return expireDate;

        }catch (Exception e){
            return "";
        }
    }

    public  void hasPaymentAlready(final NotifyObserver observer){
        FirebaseDb.getInstance().getUser(mUser.user_id, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                // THIS IS latest user info..
                FirebaseDb.User user = (FirebaseDb.User) response.getDataObject();
                if(user != null){
                    SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_LICENCE, user.toString());
                    loadUserData();
                }
                if(response.getResponseCode() == 0){
                    if(isAllow){
                        onUserPaymentSuccess(response, observer);
                    }
                    else{
                        onUserPaymentNeeded(response, observer);
                    }
                }else {
                    onUserPaymentNeeded(response, observer);
                }
            }
        });
    }

    private void onUserPaymentSuccess(ResponseObject response, NotifyObserver observer){
        response.setResponseCode(SUCCESS_USER_PAYMENT);
        response.setResponseMsg(ResponseObject.mSuccess);
        observer.update(response);
    }

    private void onUserPaymentNeeded(ResponseObject response, NotifyObserver observer){
        response.setResponseCode(USER_PAYMENT_NEEDED);
        response.setResponseMsg("USER_PAYMENT_NEEDED");
        observer.update(response);
    }

    public  void payment(String token, final NotifyObserver observer){
        final String date = Utill.getFormattedDate();
        FirebaseDb.getInstance().addUser(   mUser.user_id, mUser.subscription_date,
                                            mUser.subscription_type, token,
                                            date,getQuantity(),getDiscount(),
                                            mUser.devices, mUser.current_plan,
                                            new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                mUser.subscription_renew_date = date;
                SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_LICENCE, mUser.toString());
                setAllow(true);
                // How I can check payment verified???
                response.setResponseCode(SUCCESS_USER_PAYMENT);
                response.setResponseMsg(ResponseObject.mSuccess);
                observer.update(response);
            }
        });
    }

    public String getQuantity(){
        //TODO FIXME GET THE QUANTITY FROM USER WHEN PAYMENT....NOW DEFAULT 0.
        return "0";
    }

    public String getDiscount(){
        //TODO FIXME GET THE DISCOUNT FROM USER WHEN PAYMENT....NOW DEFAULT 0.
        return "0";
    }

    public boolean completeRegistrationforMultipleDevices(final String mobNumber, final  String current_plan, final NotifyObserver observer){
        final String email = mobNumber + "@yahoo.com";
        final String passWord = mobNumber;
        final Firebase firebase = new Firebase();
        //Check server
        String userid = mobNumber;
        FirebaseDb.getInstance().getUser(userid, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == 0){
                    FirebaseDb.User user = (FirebaseDb.User) response.getDataObject();
					int max_device = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(user.current_plan);
                    boolean isThisDeviceRegistered = user.isThisDeviceRegistered(getDevieUniqueId(mContext));
                    if(isThisDeviceRegistered){
                        // This is existing device for current user..
                        updateRegDataforCurrentUser(user,email, passWord, response, observer);
                        return;
                    }
					//user.devices.split("|");
                    boolean used = user.hasUsedMaximumDevices(max_device);
                    if(used){
                     // Already registered Max Device....
                        observer.update(new ResponseObject(USER_USED_MAX_DEVICE_USING_CURRENT_PLAN, null, "" ));
                        return;
                    }
                    if(!isThisDeviceRegistered){
                        // Now its time to REGISTER....NEED TO KEEP USER OLD DATA....
                        registerUser(user.devices, firebase, email,
                                passWord, mobNumber, user.current_plan,
                                user.subscription_renew_date,user.payment,user.subscription_type,
                                user.discount,user.quantity,observer);
                        return;
                    }

                    updateRegDataforCurrentUser(user,email, passWord, response, observer);
                }
                registerAsNewUserIfNotExists(firebase, mobNumber, email, passWord, current_plan, observer);
            }
        });

        return false;
    }

    public void updateRegDataforCurrentUser(FirebaseDb.User user, String email,
                                            String passWord, ResponseObject response,
                                            NotifyObserver observer){
        SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_LICENCE, user.toString());
        loadUserData();
        if(isAllow){
            SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_ID, email);
            SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_PSW, passWord);
            onUserPaymentSuccess(response, observer);
            return;
        } else{
            if(user != null && !user.subscription_date.isEmpty()){
                onSuccessRegistration(response, email, passWord, observer);
                return;
            }
        }
    }

    public  boolean registration(final String mobNumber, final  String current_plan, final NotifyObserver observer){

        //Check local
        if(hasRegistration()){
            final ResponseObject responseObject = new ResponseObject();
            responseObject.setResponseCode(SUCCESS_USER_REGISTRATION);
            responseObject.setResponseMsg("hasRegistration");
            observer.update(responseObject);
            return true;
        }
        //
        final Firebase firebase = new Firebase();
        //
        final String email = mobNumber + "@yahoo.com";
        final String passWord = mobNumber;
        //String current_plan = "family_plan";
        int max_device = 1;

        /*if(!current_plan.isEmpty()){
            max_device = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(current_plan);
        }
        */
        //if(max_device > 1){
            // USER WANTS TO USE MULTIPLE DEVICESS......SO NEED TO DO SOMETHING....
            // SO USER ID WILL BE ..ONLY THE MOBILE NO...NOT DEVICE ID...
            return completeRegistrationforMultipleDevices(mobNumber, current_plan, observer);
        //}

        /*
        //Check server
        //String userid = "mobNumber->"+mobNumber + " - DeviveID->"+getDevieUniqueId(mContext);
        String userid = mobNumber;
        FirebaseDb.getInstance().getUser(userid, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == 0){
                    FirebaseDb.User user = (FirebaseDb.User) response.getDataObject();
                    // USer wants to use multiple devices..
                    if(!user.isThisDeviceRegistered(getDevieUniqueId(mContext))){
                        if(!current_plan.isEmpty()){
                            int max_device = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(current_plan);
                            if(max_device > 1){
                                completeRegistrationforMultipleDevices(max_device, mobNumber, current_plan, observer);
                                return;
                            }
                        }
                    }

                    SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_LICENCE, user.toString());
                    loadUserData();
                    if(isAllow){
                        SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_ID, email);
                        SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_PSW, passWord);
                        onUserPaymentSuccess(response, observer);
                        return;
                    } else{
                        if(user != null && !user.subscription_date.isEmpty()){
                            onSuccessRegistration(response, email, passWord, observer);
                            return;
                        }
                    }
                }
                registerAsNewUserIfNotExists(firebase, mobNumber, email, passWord, current_plan, observer);
            }
        });
        */

        //return false;
    }

    private void registerAsNewUserIfNotExists(final Firebase firebase, final String mobNumber,
                                              final String email, final String passWord,
                                              final String current_plan, final NotifyObserver observer){
        firebase.signOut();
        firebase.login(email, passWord, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == Firebase.USER_LOGIN_SUCCESS){
                    onUserExists(response, observer);
                }else{
                    // Now its time to REGISTER....
                    final String date = Utill.getFormattedDate();  // Initially one month free subscription
                    registerUser("", firebase, email,
                                    passWord, mobNumber,
                                    current_plan,date,
                                    "NG",LICENCE_TRIAL,
                                    "0","0",
                            observer);
                }
            }
        });

    }

    private void registerUser(final String devices, Firebase firebase,
                              final String email, final String passWord,
                              final String mobNumber, final String current_plan,
                              final String subscription_renew_date,
                              final String payment,
                              final String subscription_type,
                              final String discount,
                              final String quantity,
                              final NotifyObserver observer){
        //Complete Registration now..

        firebase.addUser(email, passWord, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                //Log.d(DEBUG_TAG,   "addUser!" + response.getResponseMsg());
                if(response.getResponseCode() == Firebase.USER_EMAIL_EXIST_FIREBASE){
                    String date = Utill.getFormattedDate();
                    String old_devices = devices;
                    if(devices.length() > 1){
                        old_devices += "|";
                    }

                    FirebaseDb.getInstance().addUser(
                        mobNumber, date,
                            subscription_type, payment,
                            subscription_renew_date, quantity,
                            discount,old_devices+getDevieUniqueId(mContext),
                            current_plan, new NotifyObserver() {
                        @Override
                        public void update(ResponseObject response) {
                            if(response.getResponseCode() == 0){
                                FirebaseDb.User user = (FirebaseDb.User) response.getDataObject();
                                SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_LICENCE, user.toString());
                                loadUserData();
                                onSuccessRegistration(response, email, passWord, observer);
                            }
                        }
                    });
                }
            }
        });
    }


    public void updateUserPlan(final String current_plan,
                                final NotifyObserver observer){

        FirebaseDb.getInstance().addUser(
            mUser.user_id, mUser.subscription_date,
            mUser.subscription_type, mUser.payment,
            mUser.subscription_renew_date, mUser.quantity,
            mUser.discount,mUser.devices,
            current_plan, new NotifyObserver() {
                @Override
                public void update(ResponseObject response) {
                    if(response.getResponseCode() == 0){
                        observer.update(new ResponseObject(UPDATE_USER_PLAN_SUCCESS, "UPDATE_USER_PLAN_SUCCESS", null));
                    }else{
                        observer.update(new ResponseObject(UPDATE_USER_PLAN_FAILED, "UPDATE_USER_PLAN_FAILED", null));
                    }
                }
            });

    }

    public void onUserExists(ResponseObject response, NotifyObserver observer){
        response.setResponseCode(USER_EXISTS);
        observer.update(response);
    }

    public void onSuccessRegistration(ResponseObject response, String email, String passWord, NotifyObserver observer){
        SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_ID, email);
        SharedPrefUtil.setSetting(mContext,SharedPrefUtil.KEY_USER_PSW, passWord);
        response.setResponseCode(SUCCESS_USER_REGISTRATION);
        observer.update(response);
    }

    public synchronized String getDevieUniqueId(Context context) {
        if (mUiqueID == null) {
            mUiqueID = SharedPrefUtil.getSetting(mContext, SharedPrefUtil.KEY_DEVICE_UNIQUE_ID);
            if (mUiqueID == null) {
                if(DeviceDetailModel.getInstance(mContext).getListItem().size() > 0){
                    DataObject object = DeviceDetailModel.getInstance(mContext).getListItem().get(0);
                    String KEY_ICCID_NUMBER = object.getString(DeviceDetailModel.KEY_ICCID_NUMBER);
                    String KEY_DEVICE_IMEI_EMBEDDED = object.getString(DeviceDetailModel.KEY_DEVICE_IMEI_EMBEDDED);
                    String KEY_DEVICE_MAC_ADDRESS = object.getString(DeviceDetailModel.KEY_DEVICE_MAC_ADDRESS);
                    String KEY_DEVICE_UNIQUE_ID = "";

                    if(KEY_ICCID_NUMBER == null){
                        KEY_ICCID_NUMBER = "";
                    }
                    if(KEY_DEVICE_IMEI_EMBEDDED == null){
                        KEY_DEVICE_IMEI_EMBEDDED = "";
                    }
                    if(KEY_DEVICE_MAC_ADDRESS == null){
                        KEY_DEVICE_MAC_ADDRESS = "";
                    }
                    KEY_DEVICE_UNIQUE_ID = KEY_ICCID_NUMBER + "-"+KEY_DEVICE_IMEI_EMBEDDED + "-"+KEY_DEVICE_MAC_ADDRESS;
                    if(KEY_DEVICE_IMEI_EMBEDDED == "" && KEY_DEVICE_MAC_ADDRESS == ""){
                        KEY_DEVICE_UNIQUE_ID = UUID.randomUUID().toString();
                    }
                    SharedPrefUtil.setSetting(mContext, SharedPrefUtil.KEY_DEVICE_UNIQUE_ID, KEY_DEVICE_UNIQUE_ID);
                    mUiqueID = KEY_DEVICE_UNIQUE_ID;
                }
            }
        }
        return mUiqueID;
    }

    public boolean isTimeChangedFromLastTime(){

        String KEY_LAST_TIME = SharedPrefUtil.getSetting(mContext, SharedPrefUtil.KEY_LAST_TIME);
        if(KEY_LAST_TIME == null){
            SharedPrefUtil.setSetting(mContext, SharedPrefUtil.KEY_LAST_TIME, Utill.getFormattedDate());
            return true;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        try {
            Date lastDay = null;
            lastDay = format.parse(KEY_LAST_TIME);
            //
            format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            Date today = null;
            today = format.parse(Utill.getFormattedDate());
            //
            Calendar calendar_today = Calendar.getInstance();
            calendar_today.setTimeInMillis(today.getTime());
            //
            Calendar calendar_lastDay = Calendar.getInstance();
            calendar_lastDay.setTimeInMillis(lastDay.getTime());
            //
            if(calendar_today.compareTo(calendar_lastDay) > 0){
                // Need to update again to keep the lasttime always this time.
                SharedPrefUtil.setSetting(mContext, SharedPrefUtil.KEY_LAST_TIME, Utill.getFormattedDate());
                return false;
            }
            return true;

        }catch (Exception e){
            LogUtil.d(e.getMessage());
            return true;
        }
    }

    public void checkForUpdate(final NotifyObserver notifyObserver){
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    notifyObserver.update(new ResponseObject());
                    return;
                }

                final FirebaseDbMeta firebaseDbMeta = FirebaseDbMeta.getInstance();
                firebaseDbMeta.getMeta(new NotifyObserver() {
                    @Override
                    public void update(ResponseObject response) {
                        if(response.getResponseMsg().equals(ResponseObject.mSuccess)){
                            FirebaseDbMeta.Meta meta =  (FirebaseDbMeta.Meta)response.getDataObject();
                            //WE CAN UPDATE api KEY WHEN TRY THIS....
                            SharedPrefUtil.setSetting(mContext, SharedPrefUtil.KEY_API_KEY, meta.api_key);
                            //
                            if(meta.version != null && !meta.version.isEmpty()){
                                //int ns = Convert.toInt(meta.version.replaceAll("\\.",""));
                                int ns = Convert.toInt(meta.version);
                                int cv = BuildConfig.VERSION_CODE;
                                if(ns > cv){
                                    response.setResponseCode(NEW_VERSION_EXISTS);
                                }
                            }
                            notifyObserver.update(response);
                        }
                    }
                });
            }
        });
    }

}

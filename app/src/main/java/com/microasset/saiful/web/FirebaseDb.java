package com.microasset.saiful.web;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FirebaseDb {

    public static class Table{
        public static String subscription_date = "subscription_date";
        public static String subscription_renew_date = "subscription_renew_date";
        public static String subscription_type = "subscription_type";
        public static String user_id = "user_id";
        public static String payment = "payment";
        public static String quantity = "quantity";
        public static String discount = "discount";
        public static String devices = "devices";
        public static String current_plan = "current_plan";
    }

    @IgnoreExtraProperties
    public static class User {
        public String subscription_date;
        public String subscription_renew_date;
        public String subscription_type;
        public String user_id;
        public String payment;
        public String discount;
        //HOW MANY TIMES THIS SUBSCRIPTION WILL CONTINUE ..LIKE 1,2,3....12 MONTHS.
        public String quantity;
        public String devices;
        public String current_plan;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String user_id,
                    String subscription_date,
                    String subscription_type,
                    String payment,
                    String subscription_renew_date,
                    String quantity,
                    String discount,
                    String devices,
                    String current_plan) {
            this.user_id = user_id;
            this.subscription_date = subscription_date;
            this.subscription_type = subscription_type;
            this.subscription_renew_date = subscription_renew_date;
            this.payment = payment;
            this.quantity = quantity;
            this.discount = discount;
            this.devices = devices;
            this.current_plan = current_plan;
        }

        public String getSubscription_date() {
            return subscription_date;
        }

        public String getSubscription_type (){
            return subscription_type;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getPayment() {
            return payment;
        }

        public String getSubscription_renew_date() {
            return subscription_renew_date;
        }

        public String getQuantity(){return  quantity;}
        public String getDiscount(){return  discount;}

        public String toString(){

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("subscription_type", subscription_type);
                jsonObject.put("subscription_date", subscription_date);
                jsonObject.put("subscription_renew_date", subscription_renew_date);
                jsonObject.put("user_id", user_id);
                jsonObject.put("payment", payment);
                jsonObject.put("quantity", quantity);
                jsonObject.put("discount", discount);
                jsonObject.put("devices", devices);
                jsonObject.put("current_plan", current_plan);
                return jsonObject.toString();

            }catch (Exception e){

            }

            return "";
        }

        public User fromJson(String jsonData){
            User user = new User();

            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                user.subscription_date = jsonObject.getString("subscription_date");
                user.subscription_type = jsonObject.getString("subscription_type");
                user.subscription_renew_date = jsonObject.getString("subscription_renew_date");
                user.user_id = jsonObject.getString("user_id");
                user.payment = jsonObject.getString("payment");
                user.quantity = jsonObject.getString("quantity");
                user.discount = jsonObject.getString("discount");
                user.devices = jsonObject.getString("devices");
                user.current_plan = jsonObject.getString("current_plan");

            }catch (Exception e){

            }

            return user;
        }

        public boolean hasUsedMaximumDevices(int allowLimit){
            if(this.devices.trim().length() > 1){
                String [] list = this.devices.split("\\|",-1);
                if(list.length >=allowLimit){
                    return true;
                }
            }
            return false;
        }

        public boolean isThisDeviceRegistered(String deviceID){
            String [] list = this.devices.split("\\|",-1);
            for(int i = 0; i<list.length; i++){
                if(list[i].equals(deviceID)){
                    return true;
                }
            }
            return false;
        }
    }

    private String TABLE_NAME = "user";
    private DatabaseReference mDatabase;
    static private FirebaseDb firebaseDb;

    static public FirebaseDb getInstance(){
        if(firebaseDb == null){
            firebaseDb = new FirebaseDb();
        }
        return firebaseDb;
    }

    public FirebaseDb() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void updateUser(final User user, final NotifyObserver notifyObserver) {
        mDatabase.child(TABLE_NAME).child(user.user_id).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyObserver(notifyObserver, ResponseObject.mSuccess,user,0);
                        LogUtil.d("OK");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notifyObserver(notifyObserver, ResponseObject.mFailed,user,-1);
                        LogUtil.d(e.getMessage());
                    }
                });
    }

    public void addUser(String user_id,
                        String subscription_date,
                        String subscription_type,
                        String payment,
                        String subscription_renew_date,
                        String quantity,
                        String discount,
                        String devices,
                        String current_plan,
                        final NotifyObserver notifyObserver) {
        final  User user = new User(user_id, subscription_date,
                                    subscription_type, payment,
                                    subscription_renew_date, quantity,
                                    discount, devices, current_plan);
        mDatabase.child(TABLE_NAME).child(user_id).setValue(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    notifyObserver(notifyObserver, ResponseObject.mSuccess,user,0);
                    LogUtil.d("OK");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    notifyObserver(notifyObserver, ResponseObject.mFailed,user,-1);
                    LogUtil.d(e.getMessage());
                }
            });
    }

    public void notifyObserver(NotifyObserver notifyObserver, String message, User object, int resCode){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseCode(resCode);
        responseObject.setResponseMsg(ResponseObject.mSuccess);
        responseObject.setDataObject(object);
        notifyObserver.update(responseObject);
    }

    public void getUser(String user_id, final  NotifyObserver notifyObserver) {
        mDatabase.child(TABLE_NAME).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = new User();
                    // TODO: handle the case where the data already exists
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Object object = postSnapshot.getValue();
                        if(postSnapshot.getKey().equals(Table.subscription_type)) {
                            user.subscription_type = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.subscription_date)) {
                            user.subscription_date = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.user_id)) {
                            user.user_id = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.payment)) {
                            user.payment = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.subscription_renew_date)) {
                            user.subscription_renew_date = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.quantity)) {
                            user.quantity = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.discount)) {
                            user.discount = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.devices)) {
                            user.devices = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.current_plan)) {
                            user.current_plan = postSnapshot.getValue().toString();
                        }
                    }
                    notifyObserver(notifyObserver, ResponseObject.mSuccess,user,0);
                }
                else {
                    // TODO: handle the case where the data does not yet exist
                    LogUtil.d("Not Exist");
                    notifyObserver(notifyObserver, ResponseObject.mFailed,null,-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ResponseObject responseObject = new ResponseObject();
                notifyObserver(notifyObserver, ResponseObject.mCancel,null,-1);
                LogUtil.d("Not Exist or Error");
            }
        });
    }


    public void updateUser(String fieldName, String value){
        mDatabase.child(TABLE_NAME).child(fieldName).setValue(value);
    }

    public void updateUserList(String user_id, String fieldName[], String value[], final NotifyObserver notifyObserver){

        for (int i = 0; i<fieldName.length; i++){
            mDatabase.child(TABLE_NAME).child(user_id).child(fieldName[i]).setValue(value[i]).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    notifyObserver(notifyObserver, ResponseObject.mSuccess,null,0);
                    LogUtil.d("onSuccess");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    notifyObserver(notifyObserver, ResponseObject.mFailed,null,-1);
                    LogUtil.d(e.getMessage());
                    return;
                }
            });
        }
    }
}

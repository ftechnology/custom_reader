package com.microasset.saiful.web;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Convert;

import org.json.JSONObject;

public class FirebaseDbMeta {

    public static class Table{
        public static String version = "version";
        public static String version_info = "version_info";
        public static String customer_care = "customer_care";
        public static String price = "price";
        public static String api_key = "api_key";
        public static String payment_no = "payment_no";
        public static String combo_plan = "combo_plan";
        public static String family_plan = "family_plan";
        public static String plan_type = "plan_type";
        public static String discount = "discount";
        public static String youtube_id = "youtube_id";
    }

    @IgnoreExtraProperties
    public static class Meta {
        public String version;
        public String version_info;
        public String customer_care;
        public String price;
        public String api_key;
        public String payment_no;
        //
        public String phone;
        public String email;
        //
        public String combo_plan;
        public String family_plan;
        public String plan_type;
        public String discount;
        public String youtube_id;

        public Meta() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Meta(String version,
                    String version_info,
                    String customer_care,
                    String price,
                    String api,
                    String payment_no,
                    String combo_plan,
                    String family_plan,
                    String plan_type,
                    String youtube_id
                   ) {

            this.version = version;
            this.customer_care = customer_care;
            this.version_info = version_info;
            this.price = price;
            this.api_key = api;
            this.payment_no = payment_no;
            this.combo_plan = combo_plan;
            this.family_plan = family_plan;
            this.plan_type = plan_type;
            this.youtube_id = youtube_id;

        }

        public String toString(){

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("version", version);
                jsonObject.put("version_info", version_info);
                jsonObject.put("customer_care", customer_care);
                jsonObject.put("price", price);
                jsonObject.put("api_key", api_key);
                jsonObject.put("payment_no", payment_no);
                jsonObject.put("combo_plan", combo_plan);
                jsonObject.put("family_plan", family_plan);
                jsonObject.put("plan_type", plan_type);
                jsonObject.put("discount", discount);
                jsonObject.put("youtube_id", youtube_id);

                return jsonObject.toString();

            }catch (Exception e){

            }

            return "";
        }

        public Meta fromJson(String jsonData){
            Meta meta = new Meta();

            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                meta.version = jsonObject.getString("version");
                meta.version_info = jsonObject.getString("version_info");
                meta.customer_care = jsonObject.getString("customer_care");
                meta.price = jsonObject.getString("price");
                meta.api_key = jsonObject.getString("api_key");
                meta.payment_no = jsonObject.getString("payment_no");
                meta.combo_plan = jsonObject.getString("combo_plan");
                meta.family_plan = jsonObject.getString("family_plan");
                meta.plan_type = jsonObject.getString("plan_type");
                meta.youtube_id = jsonObject.getString("youtube_id");

            }catch (Exception e){

            }
            return meta;
        }
    }

    private String TABLE_NAME = "meta";
    private DatabaseReference mDatabase;
    static private FirebaseDbMeta firebaseDb;
    Meta meta = new Meta();

    static public FirebaseDbMeta getInstance(){
        if(firebaseDb == null){
            firebaseDb = new FirebaseDbMeta();
        }
        return firebaseDb;
    }


    public int maxDeviceSupportedAsCurrentPlan(String plan){
        if(plan == null || plan.isEmpty()){
            return 1;
        }
        int max = 1;
        try {
            String [] list = this.meta.plan_type.split("\\|",-1);
            if(list.length > 0){
                for (int i = 0;i<list.length;i++){
                    String[] listVal = list[i].split("\\=",-1);
                    if(listVal.length > 0){
                        if(listVal[0].equals(plan)){
                            max = Convert.toInt(listVal[1]);
                            if(max>0)
                                return max;
                            return 1;//ALWAYS ENSURE 1 DEVICE...
                        }
                    }
                }
            }
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            return 1;
        }
        return 1;
    }

    public String[] getplanTypes(){
        String[] planArray = {"single_plan", "family_plan", "combo_plan"};
        try {
            String [] list = this.meta.plan_type.split("\\|",-1);
            if(list.length > 0){
                planArray = new String[list.length];
                for (int i = 0;i<list.length;i++){
                    String[] listVal = list[i].split("\\=",-1);
                    planArray[i] = listVal[0];
                }
            }
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            return planArray;
        }

        return planArray;
    }

    private FirebaseDbMeta() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void addMeta(final NotifyObserver notifyObserver) {
        /*final  Meta user = new Meta(Table.version, Table.version_info, Table.customer_care, Table.price, Table.api_key, Table.payment_no);
        mDatabase.child(TABLE_NAME).setValue(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    LogUtil.d("OK");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtil.d(e.getMessage());
                }
            });
            */
    }

    public void notifyObserver(NotifyObserver notifyObserver, String message, Meta object, int resCode){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseCode(resCode);
        responseObject.setResponseMsg(ResponseObject.mSuccess);
        responseObject.setDataObject(object);
        notifyObserver.update(responseObject);
    }

    public boolean getMeta(final  NotifyObserver notifyObserver) {

        if(meta.version != null && meta.version.length() > 1){
            notifyObserver(notifyObserver, ResponseObject.mSuccess,meta,0);
            return false;
        }

        mDatabase.child(TABLE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Meta meta = new Meta();
                    // TODO: handle the case where the data already exists
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Object object = postSnapshot.getValue();
                        if(postSnapshot.getKey().equals(Table.version)) {
                            meta.version = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.version_info)) {
                            meta.version_info = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.customer_care)) {
                            meta.customer_care = postSnapshot.getValue().toString();
                            String cc =  meta.customer_care.replace("Mobile:","").trim().replace("Email:","").trim();
                            int index = cc.indexOf("|");
                            meta.phone = cc.substring(0, index);
                            meta.email = cc.substring(index+1, cc.length());
                        }else if(postSnapshot.getKey().equals(Table.price)) {
                            meta.price = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.api_key)) {
                            meta.api_key = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.payment_no)) {
                            meta.payment_no = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.combo_plan)) {
                            meta.combo_plan = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.family_plan)) {
                            meta.family_plan = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.plan_type)) {
                            meta.plan_type = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.discount)) {
                            meta.discount = postSnapshot.getValue().toString();
                        }else if(postSnapshot.getKey().equals(Table.youtube_id)) {
                            meta.youtube_id = postSnapshot.getValue().toString();
                        }
                    }
                    notifyObserver(notifyObserver, ResponseObject.mSuccess,meta,0);
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
        return false;
    }

}

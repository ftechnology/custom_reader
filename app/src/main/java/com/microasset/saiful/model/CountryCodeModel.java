package com.microasset.saiful.model;

import android.content.Context;
import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CountryCodeModel extends BaseModel {

    public static CountryCodeModel mDeviceDetailModel;

    //HashMap<String, String> mCountryCodes = new HashMap<String, String>();
    String[] mCountryName = {};
    String[] mCountryCode = {};
    //Bangladesh
    private CountryCodeModel(Context context) {
        super(context);
    }

    @Override
    public ResponseObject execute() {
        this.clear(true);
        loadJSONFromAsset(mContext);
        return null;
    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        JSONObject obj;
        try {
            InputStream is = context.getAssets().open("country-code.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("countries");
            mCountryName = new String[jsonArray.length()];
            mCountryCode = new String[jsonArray.length()];

            for(int i = 0; i<jsonArray.length(); i++){
                String code =  ((JSONObject)jsonArray.get(i)).getString("code");
                String name =  ((JSONObject)jsonArray.get(i)).getString("name");
                mCountryName[i] = name;
                mCountryCode[i] = code;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public static CountryCodeModel getInstance(Context context) {
        if (mDeviceDetailModel == null)
            mDeviceDetailModel = new CountryCodeModel(context);
        return mDeviceDetailModel;
    }

    @Override
    public int getCount() {
        return this.mCountryName.length;
    }

    public String getCodeByCountryName(String name){
        for(int i = 0; i<mCountryName.length; i++){
            if(mCountryName[i].equals(name)){
                return mCountryCode[i];
            }
        }

        return "";
    }

    @Override
    public Object getItem(int position) {
        return "";
    }

    public String [] getListitems(){
        return mCountryName;
    }

    public int getPositionByName(String name){
        for(int i = 0; i<mCountryName.length; i++){
            if(mCountryName[i].equals(name)){
                return i;
            }
        }

        return 0;
    }
}

package com.microasset.saiful.model;

import android.content.Context;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DictionaryEngToBanglaModel extends BaseModel {

    public static DictionaryEngToBanglaModel mDeviceDetailModel;
    private HashMap<String, String> mDictionary;

    private DictionaryEngToBanglaModel(Context context) {
        super(context);
    }

    @Override
    public ResponseObject execute() {
        this.clear(true);
        //String location = "BengaliDictionary.txt";
        //load(location);
        return null;
    }

    public void load(String filePath){
        mDictionary = new HashMap<String, String>();
        try{
            InputStream input = mContext.getAssets().open(filePath);
            BufferedReader mainBR = new BufferedReader(new InputStreamReader(input));
            String line = mainBR.readLine();
            while(line != null){
                int seperator = line.indexOf('|', 1);
                mDictionary.put(line.substring(1, seperator), line.substring(seperator+1));
                line = mainBR.readLine();
            }
            mainBR.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static DictionaryEngToBanglaModel getInstance(Context context) {
        if (mDeviceDetailModel == null)
            mDeviceDetailModel = new DictionaryEngToBanglaModel(context);
        return mDeviceDetailModel;
    }

    @Override
    public int getCount() {
        return this.mDictionary.size();
    }

    @Override
    public Object getItem(int position) {
        return "";
    }

    public String searchItem(String word){
        String val = mDictionary.get(word);
        if(val == null){
            val = "";
        }
        return val;
    }
}

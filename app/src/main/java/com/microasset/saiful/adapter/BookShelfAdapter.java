package com.microasset.saiful.adapter;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.microasset.saiful.appfrw.AbsBaseAdapter;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ImageLoader;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.ViewHolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class BookShelfAdapter extends AbsBaseAdapter {

    String mClassName = "";
    int mSourceIndex = -1;
    int mDestIndex = -1;
    NotifyObserver mNotifyObserver;
    View mSourceDrag;
    int realCount = 0;

    public BookShelfAdapter(Context context) {
        super(context);
    }

    @Override
    protected void init() {

    }

    public ArrayList<DataObject> getDataList(){
        return mListItem;
    }

    public void setmNotifyObserver(NotifyObserver notifyObserver) {
        this.mNotifyObserver = notifyObserver;
    }

    public  void setClass(String className){
        mClassName = className;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView = null;
        RelativeLayout bar = null;
        TextView textView;
        View parentView = null;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gridview_item, null);

            //textView = (TextView) view.findViewById(R.id.CountryName);
            imageView = (ImageView) view.findViewById(R.id.id_img_book);
            bar = (RelativeLayout) view.findViewById(R.id.bar);
            parentView =  view.findViewById(R.id.id_parent);
            viewHolder.imageView = imageView;
            viewHolder.view = bar;
            viewHolder.parentView = parentView;
            //viewHolder.textView = textView;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.parentView.setTag(position);
        viewHolder.parentView.setOnDragListener(new DragListener());
        //viewHolder.parentView.setOnTouchListener(new TouchListener());

        viewHolder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int po = (int) v.getTag();
                if(po > (realCount -1)){
                    return false;
                }
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                mSourceDrag = v;
                return true;
            }
        });

        viewHolder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int po = (int) v.getTag();
                if(po > (realCount -1)){
                    return;
                }
                DataObject object = mListItem.get(po);
                ResponseObject responseObject = new ResponseObject();
                responseObject.setDataObject(object);
                responseObject.setResponseMsg("BookShelfAdapter->setOnClickListener");
                mNotifyObserver.update(responseObject);
            }
        });

        if(position > (realCount -1)){
            viewHolder.imageView.setImageResource(R.drawable.shape);
            viewHolder.view.setVisibility(View.GONE);
        }else{
            viewHolder.view.setVisibility(View.VISIBLE);
            final  BookShelfAdapter readingViewAdapter = this;
            String path = "file:///android_asset/"+ getImage(position);
            Picasso.get()
                    .load(path)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(viewHolder.imageView);
        }
        return view;
    }

    public ResponseObject loadData() {
        this.clear(true);
        //
        String version = BookInfoModel.getInstance(null).getmSelectedVersion();
        String orderKey = mClassName+version;
        String bookOrder = SharedPrefUtil.getSetting(mContext, orderKey, "");
        if(!bookOrder.isEmpty()){
            try {
                JSONArray jsonObject = new JSONArray(bookOrder);
                String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES+"/";
                for (int i = 0; i<jsonObject.length(); i++){
                    String name = jsonObject.get(i).toString();
                    JSONObject jsonObject1Name = new JSONObject(name);
                    String loc = jsonObject1Name.getString("IMAGE_LOCATION");
                    name = jsonObject1Name.getString("NAME");
                    DataObject obj = new DataObject(name,i);
                    obj.setValue("IMAGE_LOCATION",  loc);
                    obj.setValue("IMAGE_FROM", "ASSET");
                    add(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(this.getCount() > 0){
                realCount = this.mListItem.size();
                return null;
            }
        }

        loadImageNameFromAsset();
        //loadImageNameFromAssetTextFolder();
        //loadImageNameFromFile();
        realCount = this.mListItem.size();
        return null;
    }

    public int getTotalBooks(){
        return realCount;
    }

    public void loadImageNameFromAsset() {
        AssetManager assetManager = mContext.getAssets();
        JSONArray objList = new JSONArray();
        String orderKey = "";
        try {

            String version = BookInfoModel.getInstance(null).getmSelectedVersion();
            String[] files = assetManager.list(Constants.BOOK+"/"+mClassName+ version+"/" + Constants.COVER_IMAGES);
            String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES+"/";
            orderKey = mClassName+version;
            for (int i = 0; i < files.length; i++) {
                //String[] st = files[i].split("\\.");

                String name = files[i].substring(0, files[i].lastIndexOf('.'));
                //Log.i("i see ", "see log asset " + st[0] + " " + files[i]);
                DataObject obj = new DataObject(name,i);
                obj.setValue("IMAGE_LOCATION",  location+files[i]);
                obj.setValue("DESCRIPTION",  files[i]);
                obj.setValue("IMAGE_FROM", "ASSET");
                add(obj);

                JSONObject object = new JSONObject();
                object.put("NAME", name);
                object.put("IMAGE_LOCATION", location+files[i]);

                objList.put(object);

            }
            String bookOrder =  objList.toString();
            //JSONArray jsonObject = new JSONArray(bookOrder);
            //String it = jsonObject.get(0).toString();
            SharedPrefUtil.setSetting(mContext, orderKey, bookOrder);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void loadImageNameFromAssetTextFolder() {
        BufferedReader reader = null;
        try {
            String version = BookInfoModel.getInstance(null).getmSelectedVersion();
            String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES;

            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(location + ".txt")));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //
                mLine = mLine.trim();
                if(mLine.isEmpty())
                    continue;
                String name = mLine.substring(0, mLine.lastIndexOf('.'));
                DataObject obj = new DataObject(name, 0);
                obj.setValue("IMAGE_FROM", "ASSET");
                obj.setValue("IMAGE_LOCATION", location +"/"+ mLine);
                add(obj);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    public void loadImageNameFromFile() {

        String version = BookInfoModel.getInstance(null).getmSelectedVersion();
        String location = Constants.BOOK+"/"+mClassName+version+"/"+Constants.COVER_IMAGES;

        File files = new File(location + "/" + ".txt");
        if (files == null) {
            return;
        }
        File[] list = files.listFiles();
        if (list == null || list.length == 0) {
            return;
        }
        for (File listItem : list) {
            String[] st = listItem.getName().split("\\.");
            DataObject obj = new DataObject(st[0], listItem.getName());
            obj.setValue("DESCRIPTION", st[0]);
            obj.setValue("IMAGE_FROM", "FILE");
            add(obj);
        }

    }


    @Override
    public int getCount() {
        if(this.mListItem.size() % 3 == 0){
            return this.mListItem.size();
        }else if(this.mListItem.size() % 3 == 1){
            return this.mListItem.size() + 2;
        }else {
            return this.mListItem.size() + 1;
        }
    }

    public String getTitle(int position) {
        return mListItem.get(position).getValue("NAME").toString();
    }

    public String getDescription(int position) {
        return mListItem.get(position).getValue("DESCRIPTION").toString();
    }

    public String getImage(int position) {
        return String.valueOf(mListItem.get(position).getValue("IMAGE_LOCATION"));
    }

    public String getImageFrom(int position) {
        return String.valueOf(mListItem.get(position).getValue("IMAGE_FROM"));
    }


    class DragListener implements View.OnDragListener {
        Drawable enterShape = mContext.getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = mContext.getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int po = (int) v.getTag();
            if(po > (realCount -1)){
                return false;
            }
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    changeBackground(v, enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    if(mSourceDrag != v) {
                        changeBackground(v, normalShape);
                    }
                    break;
                case DragEvent.ACTION_DROP:

                    // Dropped, reassign View to ViewGroup
                    // Drop location
                    LinearLayout source = (LinearLayout) v;
                    View currentView = source.getChildAt(0);

                    // Drag location
                    LinearLayout target = (LinearLayout) event.getLocalState();
                    //ViewGroup owner = (ViewGroup) view.getParent();
                    View targetView = target.getChildAt(0);

                    if (currentView != null && (currentView != targetView)) {

                        int sourceIndex = (int) source.getTag();
                        int destIndex = (int) target.getTag();
                        Collections.swap(mListItem, sourceIndex, destIndex);
                        saveOrder();
                        reloadView();
                        //
                        /*source.removeView(currentView);
                        target.removeView(targetView);
                        //
                        target.addView(currentView);
                        source.addView(targetView);
                        changeBackground(v, normalShape);
                        */
                        //
                        //view.setVisibility(View.VISIBLE);
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    changeBackground(v, normalShape);
                    int k = 0;

                default:
                    break;
            }
            return true;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void changeBackground(View v, Drawable shape) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(shape);
        } else {
            v.setBackground(shape);
        }
    }

    public void saveOrder(){
        JSONArray objList = new JSONArray();
        String orderKey = "";
        //
        for (int i = 0; i<this.getCount(); i++){
            DataObject object = (DataObject) getItem(i);
            JSONObject jsonObject = new JSONObject();
            if(object != null){
                try {
                    jsonObject.put("NAME", object.getValue("NAME"));
                    jsonObject.put("IMAGE_LOCATION", object.getValue("IMAGE_LOCATION"));
                    objList.put(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        String version = BookInfoModel.getInstance(null).getmSelectedVersion();
        orderKey = mClassName+version;
        String bookOrder =  objList.toString();
        SharedPrefUtil.setSetting(mContext, orderKey, bookOrder);
    }

    public void reloadView(){
        this.notifyDataSetInvalidated();
    }

}

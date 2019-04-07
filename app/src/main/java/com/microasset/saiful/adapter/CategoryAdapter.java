package com.microasset.saiful.adapter;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microasset.saiful.appfrw.AbsBaseAdapter;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.ViewHolder;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.XMLParser;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Collections;

public class CategoryAdapter extends AbsBaseAdapter {

    String mClassName = "";
    NotifyObserver mNotifyObserver;
    public static final String value = "value";
    public static final String text = "text";
    public static final String item = "item";
    private int mSelectedIndex = -1;

    private String[] mClassArray;

    static CategoryAdapter categoryAdapter;

    public static CategoryAdapter getInstance() {
        if(categoryAdapter == null){
            categoryAdapter = new CategoryAdapter();
        }
        return categoryAdapter;
    }

    private CategoryAdapter() {

    }

    public void setContext(Context context){
        mContext = context;
        mClassArray = mContext.getResources().getStringArray(R.array.category_array);
    }

    @Override
    protected void init() {

    }

    public void setmNotifyObserver(NotifyObserver notifyObserver) {
        this.mNotifyObserver = notifyObserver;
    }

    public  void setClass(String className){
        mClassName = className;
    }

    public  void setmSelectedIndex(int index){
        mSelectedIndex = index;
    }

    public int getmSelectedIndex(){
        return mSelectedIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView = null;
        TextView textView;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.category_gridview_item, null);
            imageView = (ImageView) view.findViewById(R.id.id_img_bookshelf);
            viewHolder.item1 = view.findViewById(R.id.tv1);
            viewHolder.imageView = imageView;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String location = "";
        DataObject object = (DataObject) this.getItem(position);
        String className = object.getString("value");
        String image = object.getString("image");
        location = Constants.BOOK+"/"+className+"/"+Constants.COVER_IMAGES+"/"+image;

        String path = "file:///android_asset/"+ location;
        Picasso.get()
                .load(path)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(viewHolder.imageView);


        viewHolder.item1.setText(mClassArray[position]);
        return view;
    }

    public ResponseObject loadData() {
        this.clear(false);

        XMLParser parser = new XMLParser();
        parser.loadXmlContent(mContext,"category.xml");

        NodeList nodeList = parser.getElementsByTagName(item);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String value = node.getNodeValue();
            NamedNodeMap attList = node.getAttributes();
            DataObject obj = new DataObject(-1);
            for (int a = 0; a < attList.getLength(); a++) {
                Node att = attList.item(a);
                obj.setValue(att.getNodeName(), att.getNodeValue());
                //mListItem.add(obj);
            }
            this.add(obj);
        }

        return null;
    }


    public String getImage(int position) {
        return String.valueOf(mListItem.get(position).getValue("IMAGE_LOCATION"));
    }
}

package com.microasset.saiful.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microasset.saiful.appfrw.AbsBaseAdapter;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ImageLoader;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.ViewHolder;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeWorkListAdapter extends AbsBaseAdapter {
    Activity mContext;
    int actualPageIndex = 0;
    ImageLoader mImageLoader;
    int width = 95;
    int height = 115;
    private DeleteIconClickListener mDelListener = null;
    String mSelectedBook;
    private static HomeWorkListAdapter homeWorkListAdapter;

    int mSelectedIndex = -1;
    List<DataObject> mBookListItem = new ArrayList<>();

    public static HomeWorkListAdapter getInstance() {
        if(homeWorkListAdapter == null)
            homeWorkListAdapter = new HomeWorkListAdapter();
        return homeWorkListAdapter;
    }

    private HomeWorkListAdapter(){

    }

    @Override
    public ResponseObject loadData() {
        return null;
    }

    public void initData(Activity context, ArrayList<DataObject> list) {
        mContext = context;
        mListItem = list;
        actualPageIndex = Convert.toInt(BookInfoModel.getInstance(context).getmMetaData().StartPage);
        mImageLoader = new ImageLoader(context);
        mSelectedBook = BookInfoModel.getInstance(null).getmSelectedBook();
        mBookListItem = BookInfoModel.getInstance(mContext).getListItem();
    }

    public void setDelListener (DeleteIconClickListener listener){
        mDelListener = listener;
    }

    public interface DeleteIconClickListener{
        void onItemDelete(int pos);
    }

    public void setDataList(ArrayList<DataObject> list){
        mListItem = list;
    }


    @Override
    protected void init() {

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView tv_page, tv_date;
        ImageView iv_del, iv_page_thum;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, null);
            tv_page = (TextView) view.findViewById(R.id.tv1);
            tv_date = (TextView) view.findViewById(R.id.tv2);
            iv_del = (ImageView)view.findViewById(R.id.iv_del);
            iv_page_thum = (ImageView)view.findViewById(R.id.iv_pg_thum);
            viewHolder.item1 = tv_page;
            viewHolder.item2 = tv_date;
            viewHolder.imageView = iv_del;
            viewHolder.imageView2 = iv_page_thum;
            viewHolder.imageView.setVisibility(View.VISIBLE);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        HomeWorkEntity entity = (HomeWorkEntity)mListItem.get(position);

       // int index = Convert.toInt(entity.getPagenumber()) + 1;

        viewHolder.item2.setText((String)entity.getInsertDate());
        viewHolder.imageView2.setImageResource(R.drawable.ic_empty_bookmark);
        String path = "";
        String content = "";

        if(entity.getPageContent().isEmpty()){
            int index = Convert.toInt(entity.getPagenumber()) + 1;
            int pageNumber =index - actualPageIndex;
            content = "Page: " + pageNumber;
            path = getPageImagePath(index - 1);
        }else{
            try {
            JSONObject jsonObject = new JSONObject(entity.getPageContent());
            path = Constants.PATH_BOOK + "/"+ mSelectedBook +"/"+jsonObject.get("image");
            content = "" + jsonObject.get("content");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        viewHolder.item1.setText( content);
        File files = new File(path);
        if(!files.exists()){
            viewHolder.imageView2.setImageResource(R.drawable.ic_empty_bookmark);
        }else{
            Picasso.get()
                    .load(new File(path))
                    .resize(width, height)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(viewHolder.imageView2);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDelListener != null){
                    mDelListener.onItemDelete(position);
                }
            }
        });

        return view;
    }

    public int getmSelectedIndex(){
        return mSelectedIndex;
    }

    public void setmSelectedIndex(int index){
        mSelectedIndex = index;
    }

    public String getPageImagePath(int position) {
        return mBookListItem.get(position).getValue("PAGE_IMAGE_PATH").toString();
    }
}

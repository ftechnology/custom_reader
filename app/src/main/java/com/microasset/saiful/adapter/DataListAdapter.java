package com.microasset.saiful.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ImageLoader;
import com.microasset.saiful.appfrw.ViewHolder;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Convert;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataListAdapter extends BaseAdapter {
    Activity mContext;
    ArrayList<DataObject> mEntities = null;
    int actualPageIndex = 0;
    ImageLoader mImageLoader;
    int width = 95;
    int height = 115;
    List<DataObject> mBookListItem = new ArrayList<>();
    private DeleteIconClickListener mDelListener = null;


    public DataListAdapter(Activity context, ArrayList<DataObject> list) {
        mContext = context;
        mEntities = list;
        actualPageIndex = Convert.toInt(BookInfoModel.getInstance(context).getmMetaData().StartPage);
        mImageLoader = new ImageLoader(context);
        mBookListItem = BookInfoModel.getInstance(mContext).getListItem();
    }

    public void setDelListener (DeleteIconClickListener listener){
        mDelListener = listener;
    }

    public interface DeleteIconClickListener{
        void onItemDelete(int pos);
    }

    public void setDataList(ArrayList<DataObject> list){
        mEntities = list;
    }

    @Override
    public int getCount() {
        return mEntities == null ? 0 : mEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

        final BookEntity entity = (BookEntity)mEntities.get(position);

        int index = Convert.toInt(entity.getPagenumber()) + 1;

        viewHolder.item1.setText(mContext.getString(R.string.page_num) + " " + (index - actualPageIndex));
        viewHolder.item2.setText((String)entity.getInsertDate());
        viewHolder.imageView2.setImageResource(R.drawable.ic_empty_bookmark);

        File files = new File(getPageImagePath(index - 1));
        if(!files.exists()){
            viewHolder.imageView2.setImageResource(R.drawable.ic_empty_bookmark);
        }else{
            Picasso.get()
                    .load(new File(getPageImagePath(index - 1)))
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

    public String getPageImagePath(int position) {
        return mBookListItem.get(position).getValue("PAGE_IMAGE_PATH").toString();
    }

    public String getImageFrom(int position) {
        return String.valueOf(mBookListItem.get(position).getValue("IMAGE_FROM"));
    }
}

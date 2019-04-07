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
import com.microasset.saiful.model.BookSolutionModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.web.WebApiModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ToturialListAdapter extends BaseAdapter {
    Activity mContext;
    ArrayList<DataObject> mEntities = null;
    int width = 120;
    int height = 90;

    public ToturialListAdapter(Activity context, ArrayList<DataObject> list) {
        mContext = context;
        mEntities = list;

    }

    public void setDataList(ArrayList<DataObject> list) {
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
        ImageView iv_page_thum;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tutorial_item, null);
            tv_page = (TextView) view.findViewById(R.id.tv1);
            tv_date = (TextView) view.findViewById(R.id.tv2);
            iv_page_thum = (ImageView) view.findViewById(R.id.iv_pg_thum);
            viewHolder.item1 = tv_page;
            viewHolder.item2 = tv_date;
            viewHolder.imageView2 = iv_page_thum;
            view.setTag(viewHolder);

            viewHolder.item2.setVisibility(View.GONE);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final DataObject entity = (DataObject) mEntities.get(position);

        //viewHolder.item1.setText("Chapter--> " + entity.getString(BookSolutionModel.chapter));
        viewHolder.item1.setText(entity.getString(BookSolutionModel.title));
        viewHolder.imageView2.setImageResource(R.drawable.ic_empty_bookmark);

        // Picasso
        String yt = "https://img.youtube.com/vi/";
        String link = yt + entity.getString(BookSolutionModel.href) + "/mqdefault.jpg";
        //String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=UxbwKz05bwc&key=AIzaSyC1jpC-l5wyJgXyVFGgvmLDp4ZkqlLMmWs";

        Picasso.get()
                .load(link)
                .resize(width, height)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(viewHolder.imageView2);

        /*WebApiModel fileDownloadModel = new WebApiModel(mContext);
        fileDownloadModel.loadVideoInfo(viewHolder.item1, viewHolder.item2,
                entity.getString(BookSolutionModel.href), Constants.YOUTUBE_API_KEY);
                */

        return view;
    }

}

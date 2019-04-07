package com.microasset.saiful.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.entity.RecentlyOpenEntity;
import com.microasset.saiful.util.Constants;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by farukhossain on 2019/01/10.
 */

public class RecentlyOpenListAdapter extends RecyclerView.Adapter<RecentlyOpenListAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    List<DataObject> mListItem = new ArrayList<>();

    NotifyObserver mNotifyObserver;
    Context mContext = null;

    // data is passed into the constructor
    public RecentlyOpenListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }


    public void setmNotifyObserver(NotifyObserver notifyObserver) {
        this.mNotifyObserver = notifyObserver;
    }

    public void setDataList(ArrayList<DataObject> list){
        Collections.sort(list, new SortByDate());
        mListItem = list;
    }

    private class SortByDate implements Comparator<DataObject> {

        @Override
        public int compare(DataObject obj1, DataObject obj2) {
            String str1 = ((RecentlyOpenEntity)obj1).getInsertDate();
            String str2 = ((RecentlyOpenEntity)obj2).getInsertDate();
            Date date1 = null;
            Date date2 = null;
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            try {
                date1 = format.parse(str1);
                date2 = format.parse(str2);
                if (date1 != null && date2 != null){
                    return date2.compareTo(date1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public RecentlyOpenListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recently_open_item_layout, parent, false);
        return new RecentlyOpenListAdapter.ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull RecentlyOpenListAdapter.ViewHolder holder, final int position) {
        RecentlyOpenEntity object = (RecentlyOpenEntity) mListItem.get(position);
        String path = "file:///android_asset/"+ object.getImagePath();
        Picasso.get()
                .load(path)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.imgThumbnail);
        holder.tvPageNumber.setText(mContext.getString(R.string.STR_LAST_OPEN) + object.getInsertDate());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Jump
                RecentlyOpenEntity item = (RecentlyOpenEntity) mListItem.get(position);
                DataObject object = new DataObject(-1);
                object.setValue("NAME", item.getBookId());
                object.setValue("IMAGE_LOCATION", item.getImagePath());
                ResponseObject responseObject = new ResponseObject();
                responseObject.setDataObject(object);
                responseObject.setResponseMsg("BookShelfAdapter->setOnClickListener");
                mNotifyObserver.update(responseObject);
            }
        });
    }

    public String getImage(int position) {
        return String.valueOf(mListItem.get(position).getValue("IMAGE_LOCATION"));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mListItem == null){
            return 0;
        }
        if (mListItem.size() < Constants.RECENTLY_OPEN_MAX_COUNT) {
            return mListItem.size();
        }
        return Constants.RECENTLY_OPEN_MAX_COUNT;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView tvPageNumber;
        ImageView imgThumbnail;

        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tvPageNumber = itemView.findViewById(R.id.tvPageNumber);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }

}

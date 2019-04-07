package com.microasset.saiful.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.microasset.saiful.appfrw.ViewHolder;
import com.microasset.saiful.easyreader.R;

import java.util.ArrayList;

public class AvailableToturialListAdapter extends BaseAdapter {
    Activity mContext;
    ArrayList<String> mEntities = null;

    public AvailableToturialListAdapter(Activity context, ArrayList<String> list) {
        mContext = context;
        mEntities = list;

    }

    public void setDataList(ArrayList<String> list) {
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
        TextView tv_page;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.available_tutorial_item, null);
            tv_page = (TextView) view.findViewById(R.id.tv1);
            viewHolder.item1 = tv_page;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final String entity = (String) mEntities.get(position);

        viewHolder.item1.setText("Chapter "+entity);
        return view;
    }

}

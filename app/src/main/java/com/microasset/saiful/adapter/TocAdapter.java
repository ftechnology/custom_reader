package com.microasset.saiful.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microasset.saiful.appfrw.AbsBaseAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.ViewHolder;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Convert;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TocAdapter extends AbsBaseAdapter {
    String mClassName = "";
    int mHighlightPos = -1;
    BookInfoModel bookInfoModel = null;

    List<DataObject> mBookListItem = new ArrayList<>();
    int width = 0;
    int height = 0;

    public TocAdapter(Context context, int highlightPos) {
        super(context);
        mHighlightPos = highlightPos;
        bookInfoModel = BookInfoModel.getInstance(mContext);

        BaseActivity activity = (BaseActivity) context;
        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        width = size.x / 3;
        height = (size.y / 3) + 80;
    }

    @Override
    protected void init() {

    }

    public void setClass(String className) {
        mClassName = className;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        TextView textView = null;
        TextView textChapter = null;
        ImageView imageView = null;
        ImageView imageView2 = null;
        ProgressBar progressBar = null;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.toc_gridview_item, null);
            textView = (TextView) view.findViewById(R.id.tv1);
            textChapter = (TextView) view.findViewById(R.id.tv2);
            imageView = (ImageView) view.findViewById(R.id.cp_icon);
            imageView2 = (ImageView) view.findViewById(R.id.iv_download);
            progressBar = (ProgressBar) view.findViewById(R.id.probress_bar);
            view.setTag(viewHolder);
            viewHolder.item1 = textView;
            viewHolder.item2 = textChapter;
            viewHolder.imageView = imageView;
            viewHolder.imageView2 = imageView2;
            viewHolder.progressBar = progressBar;
            view.setTag(viewHolder);
            //
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //object.setValue(att.getNodeName(), att.getNodeValue());
        DataObject object = (DataObject) this.getItem(position);
        String index = (String) object.getValue("index");
        int pos = Convert.toInt(index.split(",")[0]);
        if (index != null) {
            viewHolder.item2.setText("" + pos);
            viewHolder.item1.setText((String) object.getValue("title"));
            viewHolder.imageView2.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);
        }

        //FIXME TODO CRASH...Here...WHY??
        if (isChapterAvailable(object)) {
            pos = Convert.toInt(index.split(",")[0]);
            int start = Convert.toInt(BookInfoModel.getInstance(mContext).getmMetaData().StartPage);
            viewHolder.imageView.setImageResource(R.drawable.ic_empty_bookmark);
            Picasso.get()
                    .load(new File(getPageImagePath((pos + start) - 1)))
                    .resize(width, height)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(viewHolder.imageView);
            viewHolder.item1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ic_empty_bookmark);
            viewHolder.imageView2.setVisibility(View.VISIBLE);

            viewHolder.progressBar.setVisibility(View.GONE);
        }

        String str = object.getString(ResponseObject.mStatus);

        if (str != null && str.length() > 0) {
            int itemPos = object.getInt(ResponseObject.mItemPosition);
            if (itemPos == position) {
                if (str.equalsIgnoreCase(ResponseObject.mComplete)) {
                    viewHolder.item1.setText((String) object.getValue("title"));
                    viewHolder.imageView2.setVisibility(View.GONE);
                    viewHolder.progressBar.setVisibility(View.GONE);
                } else if (str.equalsIgnoreCase(ResponseObject.mContinue)) {
                    viewHolder.item1.setText(mContext.getString(R.string.STR_DOWNLOADING));
                    viewHolder.imageView2.setVisibility(View.GONE);
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.item1.setText((String) object.getValue("title"));
                    viewHolder.imageView2.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }

    public String getImageFrom(int position) {
        return String.valueOf(mBookListItem.get(position).getValue("IMAGE_FROM"));
    }

    public String getPageImagePath(int position) {
        return mBookListItem.get(position).getValue("PAGE_IMAGE_PATH").toString();
    }

    public ResponseObject loadData() {
        this.mListItem = BookInfoModel.getInstance(mContext).getmBookIndex().getListItem();
        mBookListItem = BookInfoModel.getInstance(mContext).getListItem();
        return null;
    }

    public boolean isChapterAvailable(DataObject object) {
        //DataObject object = (DataObject) getItem(position);
        if (object != null) {
            String index = (String) object.getValue("index");
            final int start = Convert.toInt(index.split(",")[0]);
            final int end = Convert.toInt(index.split(",")[1]);
            if (bookInfoModel.isChapterAvailable(start, end)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDownloadOngoing() {
        for (int i = 0; i < this.getCount(); i++) {
            DataObject object = (DataObject) this.getItem(i);
            String str = object.getString(ResponseObject.mStatus);
            if (str != null && (str.equalsIgnoreCase(ResponseObject.mContinue))) {
                return true;
            }
        }
        return false;
    }
}

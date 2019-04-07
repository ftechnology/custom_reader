package com.microasset.saiful.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ImageCacheManager;
import com.microasset.saiful.appfrw.ImageLoader;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.easyreader.ReadingViewActivity;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.view.ZoomImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadingViewAdapter extends PagerAdapter implements GestureDetector.OnDoubleTapListener {

    public static interface OnClickBookMark{
            void OnClick(View view);
    }

    Context mContext;
    ImageLoader mImageLoader;
    List<DataObject> mListItem = new ArrayList<>();
    ImageCacheManager mImageCacheManager;
    OnClickBookMark mOnClickBookMark;
    ReadingViewActivity mReadingActivity = null;
    ZoomImageView photoView = null;

    String mBookName = "";

    public ReadingViewAdapter(Context context) {
        if(context instanceof ReadingViewActivity){
            mReadingActivity = (ReadingViewActivity)context;
        }
        mContext = context;
        mImageLoader = new ImageLoader(context);
        mImageCacheManager = new ImageCacheManager(mContext);

        BaseActivity activity = (BaseActivity) context;
        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        mImageCacheManager.setMaxImageHeight(height);
        mImageCacheManager.setMaxImageWidth(width);
        mImageCacheManager.setmKeepMinSize(true);

    }

    public  void setmOnClickBookMark(OnClickBookMark listener){
        mOnClickBookMark = listener;
    }

    public  void setBookName(String bookName){
        mBookName = bookName;
    }

    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {

        // TODO fix me ....Reuse same view....
        LayoutInflater inflater;
        inflater = LayoutInflater.from(mContext);
        final View imageLayout = inflater.inflate(R.layout.reading_view_item, null, false);

        photoView = imageLayout.findViewById(R.id.reading_view);

        photoView.setmImagePath(getPageImagePath(position));
        photoView.setmPageIndex(position);
        photoView.setmImageCacheManager(mImageCacheManager);

        // Now just add PhotoView to ViewPager and return it
        container.addView(imageLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        final  ReadingViewAdapter readingViewAdapter = this;
        imageLayout.setTag(position);

        //mImageCacheManager.loadImage(getImageFrom(position), getPageImagePath(position), photoView);
        /*Bitmap bitmap = mImageCacheManager.loadImage(getImageFrom(position), getPageImagePath(position), new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                readingViewAdapter.notifyDataSetChanged();
                imageLayout.setTag(-1);
            }
        });
        */

        Bitmap bitmap = mImageCacheManager.loadImage(getImageFrom(position), getPageImagePath(position), photoView);

        //
        ImageView textView = imageLayout.findViewById(R.id.img_download);
        textView.setVisibility(View.GONE);

        File files = new File(getPageImagePath(position));
        if(!files.exists()){
            textView.setVisibility(View.VISIBLE);
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mReadingActivity.showOverlayUx();
                    return false;
                }
            });

        }

        photoView.setOnDoubleTapListener(this);
        //
        return imageLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public  void loadData(){
        File files = new File(Constants.PATH_BOOK + "/"+ mBookName);
        LogUtil.d(files.getAbsolutePath());
        if (files == null) {
            return;
        }

        //File[] list = files.listFiles();
        //if (list == null || list.length == 0) {
        //    return;
        //}

        mListItem = BookInfoModel.getInstance(mContext).getListItem();
    }

    public boolean hasBookMark(int position) {
        //return (boolean)mListItem.get(position).getValue("HAS_BOOKMARKED");
        BookEntity entity =  BookmarkDbModel.getInstance(mContext).hasBookMark(BookInfoModel.getInstance(mContext).getmSelectedBook(), String.valueOf(position));
        if(entity != null){
            return true;
        }
        return false;
    }

    public String getTitle(int position) {
        return mListItem.get(position).getValue("NAME").toString();
    }

    public String getPageImagePath(int position) {
        return mListItem.get(position).getValue("PAGE_IMAGE_PATH").toString();
    }

    public String getId(int position) {
        return String.valueOf(mListItem.get(position).getValue("ID"));
    }

    public String getImageFrom(int position) {
        return String.valueOf(mListItem.get(position).getValue("IMAGE_FROM"));
    }

    public String getPageNumber(int position) {
        return String.valueOf(mListItem.get(position).getValue("PAGE_NUM"));
    }

    public String getPageNumberByIndex(int position) {
        return String.valueOf(mListItem.get(position).getValue("PAGE_NUM_BY_INDEX"));
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {

        if(mReadingActivity != null && photoView != null){
                mReadingActivity.showOverlayUx();
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }
}

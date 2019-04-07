package com.microasset.saiful.appfrw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.util.NetworkUtil;

import java.lang.ref.WeakReference;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Observer;

public class ImageCacheManager {
    private Context mContext;
    private int mMaxImageWidth = dpToPx(150);
    private int mMaxImageHeight = dpToPx(200);
    private Bitmap mBitmapImagethumbnail = null;
    Handler mHandler = new Handler();
    private boolean mKeepMinSize = false;

    public ImageCacheManager(Context context) {
        this.mContext = context;
    }

    public void setmKeepMinSize(boolean keepMinSize){
        mKeepMinSize = keepMinSize;
    }

    public boolean ismKeepMinSize() {
        return mKeepMinSize;
    }

    public void setMaxImageWidth(int width){
        mMaxImageWidth = width;
    }

    public void setMaxImageHeight(int height){
        mMaxImageHeight = height;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public Bitmap loadImage(String loadImageFrom, String imageKey, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(imageKey, loadImageFrom);
        return mBitmapImagethumbnail;
    }

    public Bitmap loadImage(String loadImageFrom, String imageKey) {

        Bitmap bitmap = null;
        ImageLoader imageLoader = new ImageLoader(mContext);
        //thumbnailImage is fetched from LRU cache first
        if (loadImageFrom == "ASSET") {
            bitmap = imageLoader.loadBitmapFromAsset(imageKey, mMaxImageWidth, mMaxImageHeight);
        } else if (loadImageFrom == "FILE") {
            bitmap = imageLoader.loadBitmapFromFile(imageKey, mMaxImageWidth, mMaxImageHeight);
        } else if (loadImageFrom == "RESOURCE" || imageKey == null) {
            bitmap = imageLoader.loadBitmapFromResource(Integer.parseInt(imageKey), mMaxImageWidth, mMaxImageHeight);
        }

        return bitmap;
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        //private final WeakReference<ImageView> imageViewReference;
        //NotifyObserver notifyObserver;
        ImageView mImageView;

        public BitmapWorkerTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //you can load Images from different folder according to your need.
            ImageLoader imageLoader = new ImageLoader(mContext);
            Bitmap bitmap = null;
            if (params[1] == "ASSET") {
                bitmap = imageLoader.loadBitmapFromAsset(params[0], mMaxImageWidth, mMaxImageHeight);
            } else if (params[1] == "FILE") {
                bitmap = imageLoader.loadBitmapFromFile(params[0], mMaxImageWidth, mMaxImageHeight);
            } else if (params[1] == "RESOURCE" || params[1] == null) {
                bitmap = imageLoader.loadBitmapFromResource(Integer.parseInt(params[0]), mMaxImageWidth, mMaxImageHeight);
            }
            if (bitmap != null) {
                //addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        //  onPostExecute() sets the bitmap fetched by doInBackground();
        @Override
        synchronized protected void onPostExecute(Bitmap bitmap) {
            try {
                if (mImageView != null) {
                    ResponseObject response = new ResponseObject();
                    DataObject object = new DataObject(0);
                    object.setValue("mImageView", mImageView);
                    object.setValue("bitmap", bitmap);
                    response.setDataObject(object);
                    notifyObserver(response);
                }
            }catch (Exception e){
                LogUtil.d(e.getMessage());
            }
        }
    }


    public synchronized void notifyObserver(final ResponseObject response) {
        //final ResponseObject tmpResponse = response;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DataObject object = (DataObject) response.getDataObject();
                ImageView imageView = (ImageView) object.getValue("mImageView");
                imageView.setImageBitmap((Bitmap) object.getValue("bitmap"));
                imageView.invalidate();
            }
        });
    }

}


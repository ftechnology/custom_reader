package com.microasset.saiful.appfrw;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author Mohammad Saiful Alam
 *         ImageLoader, load imge from asset, resourceid, file..as well.
 *         Load image in efficient way.
 */
public class ImageLoader {
    public static final String PATH = Environment.getExternalStorageDirectory() + "/images/";
    private Context mContext;
    private boolean mMaintainAspectRatio = true;


    public ImageLoader(Context context) {
        this.mContext = context;
    }

    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(reqWidth > width || reqHeight > height){
            return inSampleSize;
        }

        if (height > reqHeight && width > reqWidth) {

            for (;;){
                inSampleSize *= 2;
                int theight = height / inSampleSize;
                int twidth = width / inSampleSize;
                if(theight< reqHeight && twidth < reqWidth) {

                    // Hmm...We have to back one step
                    inSampleSize = inSampleSize/2;

                    break;
                }
            }
        }

        return inSampleSize;
    }

    /**
     * @return the mMaintainAspectRatio
     */
    public boolean ismMaintainAspectRatio() {
        return mMaintainAspectRatio;
    }

    /**
     * @param mMaintainAspectRatio the mMaintainAspectRatio to set
     */
    public void setmMaintainAspectRatio(boolean mMaintainAspectRatio) {
        this.mMaintainAspectRatio = mMaintainAspectRatio;
    }

    /**
     * Use createScaledBitmap to resize the bitmap and delete old one.
     * Creates a new bitmap, scaled from an existing bitmap, when possible.
     * If the specified width and height are the same as the current width and height of the source bitmap,
     * the source bitmap is returned and no new bitmap is created.
     *
     * @param bmp
     * @return
     */
    public Bitmap cutBitmap(Bitmap bmp, int w, int h) {

        if (bmp == null) return bmp;

        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, w, h, false);
        if (bitmap != null) {
            // Delete old one.
            bmp.recycle();
            bmp = null;

        } else {
            return bmp;// Return old one.
        }

        return bitmap;
    }

    public Bitmap cropBitmap(Bitmap bmp, int x, int y, int w, int h) {
        if (bmp == null) return bmp;
        Bitmap bitmap = Bitmap.createBitmap(bmp,x, y, w, h);
        return bitmap;
    }

    /**
     * Load image from asset for given  image path: image. Pass new width and height.
     * Use loadBitmapFromAsset(String image) to load same size image.
     *
     * @param image
     * @return
     */
    public Bitmap loadBitmapFromAsset(String image, int reqWidth, int reqHeight) {

        // load image
        try {
            InputStream ims = mContext.getAssets().open(image);
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            Bitmap bitmap = drawableToBitmap(d, reqWidth, reqHeight);
            return bitmap;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Load iamge from asset.
     *
     * @param image
     * @return
     */
    public Bitmap loadBitmapFromAsset(String image) {
        return this.loadBitmapFromAsset(image, 0, 0);
    }

    /**
     * @param id
     * @return
     */
    public Bitmap loadBitmapFromResource(int id, int reqWidth, int reqHeight) {
        // WE DONT WANR TO LOAD LARGE IMAGE ..TRY TO LOAD AS REQUIRED.
        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
        btmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(mContext.getResources(), id, btmapOptions);
        btmapOptions.inJustDecodeBounds = false;
        btmapOptions.inSampleSize = 1;
        // Calculate inSampleSize
        if (reqWidth > 1 && reqHeight > 1) {
            btmapOptions.inSampleSize = calculateInSampleSize(btmapOptions, reqWidth, reqHeight);
        }
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id, btmapOptions);

        if (!mMaintainAspectRatio) {
            return this.cutBitmap(bmp, reqWidth, reqHeight);
        }
        return bmp;
    }

    /**
     * Load image from resource.
     *
     * @param id
     * @return
     */
    public Bitmap loadBitmapFromResource(int id) {
        return this.loadBitmapFromResource(id, 0, 0);
    }

    /**
     * Example: loadDataFromFile(/sdcard/Images/test_image.jpg)
     *
     * @param imagePath
     * @return
     */
    public Bitmap loadBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
        //String imageFilePath = PATH + "/" + imagePath;
        String imageFilePath = imagePath;
        File imgFile = new File(imageFilePath);

        if (imgFile.exists()) {
            try {
                // WE DONT WANR TO LOAD LARGE IMAGE ..TRY TO LOAD AS REQUIRED.
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                btmapOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, btmapOptions);
                btmapOptions.inJustDecodeBounds = false;
                btmapOptions.inSampleSize = 1;
                // SO that we can pass this in Canvas..
                btmapOptions.inMutable = true;
                // Calculate inSampleSize
                Bitmap bmp = null;
                if (reqWidth > 1 && reqHeight > 1) {
                    btmapOptions.inSampleSize = calculateInSampleSize(btmapOptions, reqWidth, reqHeight);
                }
                bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), btmapOptions);
                if (!mMaintainAspectRatio) {
                    return this.cutBitmap(bmp, reqWidth, reqHeight);
                }
                return bmp;
            } catch (OutOfMemoryError e) {

                LogUtil.d(e.getMessage());

            } catch (Exception e){
                LogUtil.d(e.getMessage());
            }
        }
        return null;
    }

    /**
     * @param imagePath
     * @return
     */
    public Bitmap loadBitmapFromFile(String imagePath) {
        return this.loadBitmapFromFile(imagePath, 0, 0);
    }

    /**
     * Load image from url.
     *
     * @param url
     * @return
     */
    public Bitmap loadBitmapFromUrl(String url, ImageView imageView) {
        return null;
    }

    /**
     * @param drawable
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable, int reqWidth, int reqHeight) {
        int width = 1;
        int height = 1;

        if (reqWidth < 1) reqWidth = 1;
        if (reqHeight < 1) reqHeight = 1;

        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (reqWidth > 1 && reqHeight > 1) {
                return cutBitmap(bmp, reqWidth, reqHeight);

            } else {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }

        width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        if (reqWidth > 1 && reqHeight > 1) {
            return cutBitmap(bmp, reqWidth, reqHeight);
        }
        return bmp;
    }
}


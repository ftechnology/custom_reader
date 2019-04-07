package com.microasset.saiful.drawings;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.microasset.saiful.util.Convert;

public class Indicator extends Rectangle {

    boolean mShowImage = false;
    int mCounter = 0;
    /**
     * default constructor
     */
    public Indicator(String type)
    {
        super(type);
        mDrawBorder = true;
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {

        if(points.size() == 2) {

            paint.setColor(mLineColor);
            if(mDrawBorder) {
                //super.render(canvas, paint, matrix);

                float cx = (points.get(1).x - points.get(0).x)/2;
                float cy = (points.get(1).y - points.get(0).y)/2;

                paint.setColor(mLineColor);
                paint.setStrokeWidth(mStrokeWidth);
                paint.setAlpha(mAlpha);

                canvas.drawCircle(cx + points.get(0).x , points.get(0).y +cy, cx, paint);

            }

            if(mImageCacheManager != null && mShowImage){
                Bitmap bitmap = mImageCacheManager.loadImage("RESOURCE",Convert.toString( getmImageIndex()) );
                if(bitmap != null){
                    canvas.drawBitmap(bitmap, points.get(0).x, points.get(0).y,null);
                }
            }
        }
    }

    @Override
    public void touchMove(int dx, int dy){

    }

    public boolean hitTest(int x, int y)
    {
        if(points.size() == 2) {
            //Point pt = new Point(x, y);
            int padding = 20;
            Rect r = new Rect();
            r.left = (int) points.get(0).x - padding;
            r.top = (int) points.get(0).y-padding;
            r.right = (int) points.get(1).x + padding;
            r.bottom = (int) points.get(1).y + padding;
            return (r.contains(x, y));
        }
        return false;
    }

    public void resize(int w, int h){
    }

    @Override
    public void onUpdate(){

        if(mCounter  == 0){
            setLineColor(Color.GREEN);
            mCounter = 1;
            return;
        }
        setLineColor(Color.RED);
        mCounter = 0;
    }
}

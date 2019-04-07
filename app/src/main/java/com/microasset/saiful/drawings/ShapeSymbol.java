package com.microasset.saiful.drawings;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.microasset.saiful.util.Convert;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.Utill;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShapeSymbol extends Rectangle {

    /**
     * default constructor
     */

    public ShapeSymbol(String type) {
        super(type);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {
        //
        if(mImageCacheManager != null){
            Bitmap bitmap = mImageCacheManager.loadImage("RESOURCE",Convert.toString( getmImageIndex()) );
            if(bitmap != null){
                //canvas.drawBitmap(bitmap, points.get(0).x, points.get(0).y,null);
            }
        }
        //if(mState == State.STATE_MOVE_OBJECT) {
            super.render(canvas, paint, matrix);
        //}

        if(mShapeDecorator != null){
            mShapeDecorator.render(canvas, paint, matrix);
        }
    }

}

package com.microasset.saiful.drawings;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.microasset.saiful.util.Convert;

public class ShapeYes extends Rectangle {

    /**
     * default constructor
     */

    public ShapeYes(String type) {
        super(type);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {


        if(points.size() == 2) {
            float x = points.get(0).x;
            float y = points.get(0).y;
            float w = points.get(1).x - x;
            float h = points.get(1).y - y;

            Point p1 = new Point(x, y+h/3);
            Point p2 = new Point(x + w/3, y+h );
            Point p3 = new Point(x + w/3, y+h );
            Point p4 = new Point(x + w, y );

            //
            paint.setColor(mLineColor);
            Path path = new Path();
            paint.setStrokeWidth(mStrokeWidth);
            paint.setAlpha(mAlpha);

            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            //canvas.drawPath(path, paint);
            //
            path.moveTo(p3.x, p3.y);
            path.lineTo(p4.x, p4.y);
            canvas.drawPath(path, paint);
            //
        }

        if(mState == State.STATE_MOVE_OBJECT){
            super.render(canvas, paint, matrix);
        }

        if(mShapeDecorator != null){
            mShapeDecorator.render(canvas, paint, matrix);
        }
    }

}

package com.microasset.saiful.drawings;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.MotionEvent;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.Utill;

import java.security.cert.PolicyNode;

public class Rectangle extends Shape {

    /**
     * default constructor
     */
    public Rectangle(String type)
    {
        super(type);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {
        if(points.size() == 2) {
            //matrix.mapPoints(pointsArray);
            paint.setColor(mLineColor);
            paint.setStrokeWidth(mStrokeWidth);
            paint.setAlpha(mAlpha);
            //Path mPath = new Path();
            //mPath.addRect(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y, Path.Direction.CCW);
            canvas.drawRect(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y, paint);
            //canvas.drawPath(mPath, paint);
        }
        if(mShapeDecorator != null){
            mShapeDecorator.render(canvas, paint, matrix);
        }
    }

    @Override
    public void touchMove(int dx, int dy){
        if(points.size() == 2){

            if(points.get(0).x + dx <0){
                return;//CLIP LEFT..
            }
            if(points.get(0).y + dy <0){
                return;//CLIP TOP..
            }

            if(points.get(1).x + dx > mCanvasWidth - mClipPadding){
                return;//CLIP RIGHT
            }
            if(points.get(1).y + dy > mCanvasHeight - mClipPadding){
                return;//CLIP BOTTOM
            }

            points.get(0).x = points.get(0).x + dx;
            points.get(0).y = points.get(0).y + dy;
            points.get(1).x = points.get(1).x + dx;
            points.get(1).y = points.get(1).y + dy;
        }
    }

    public boolean hitTest(int x, int y)
    {
        if(points.size() < 2){
            return false;
        }
        //Point pt = new Point(x, y);
        Rect r = new Rect();
        r.left = (int)points.get(0).x;
        r.top = (int)points.get(0).y;
        r.right = (int)points.get(1).x;
        r.bottom = (int)points.get(1).y;
        //
        return (r.contains(x, y));
    }

    public void resize(int w, int h){
        //
        points.get(1).x = points.get(0).x + w;
        points.get(1).y = points.get(1).x + h;

    }

    public void adjustBoundary(){
        if(points.get(0).y > points.get(1).y){
            float t = points.get(0).y;
            points.get(0).y = points.get(1).y;
            points.get(1).y = t;
        }
        if(points.get(0).x > points.get(1).x){
            float t = points.get(0).x;
            points.get(0).x = points.get(1).x;
            points.get(1).x = t;
        }
    }

    public DataObject toEntity(){
        DrawingObjectEntity drawingObjectEntity = new DrawingObjectEntity(this.mPageIndex);
        drawingObjectEntity.setBookId(mBookId);
        drawingObjectEntity.setPageNumber(Convert.toString(mPageIndex));
        drawingObjectEntity.setType(mType);
        DataObject object = new DataObject(0);
        object.setValue("l", points.get(0).x);
        object.setValue("t", points.get(0).y);
        object.setValue("r", points.get(1).x);
        object.setValue("b", points.get(1).y);
        //
        object.setValue("mStrokeWidth", mStrokeWidth);
        object.setValue("mAlpha", mAlpha);
        object.setValue("mLineColor", mLineColor);
        object.setValue("mTextColor", mTextColor);
        //
        String str  = object.toString();
        drawingObjectEntity.setInsertDate(Utill.getFormattedDate());
        drawingObjectEntity.setJsonData(str);
        drawingObjectEntity.setId(this.getId());

        return drawingObjectEntity;
    }
}

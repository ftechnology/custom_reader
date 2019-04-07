package com.microasset.saiful.drawings;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.microasset.saiful.easyreader.R;

import java.util.ArrayList;

public class ShapeDecorator extends Rectangle {

    /**
     * default constructor
     */

    Shape mShape;
    Indicator mLeftTop;
    Indicator mRightTop;
    Indicator mLeftBottom;
    Indicator mRightBottom;
    //
    Indicator mSelected = null;

    //
    ArrayList mList = new ArrayList();

    public ShapeDecorator(String type, Shape shape)
    {
        super(type);
        mShape = shape;
        mLeftTop = new Indicator("ShapeDecorator->mLeftTop");
        mRightTop = new Indicator("ShapeDecorator->mRightTop");
        mLeftBottom = new Indicator("ShapeDecorator->mLeftBottom");
        mRightBottom = new Indicator("ShapeDecorator->mRightBottom");
        //
        mList.add(mLeftTop);
        mList.add(mRightTop);
        mList.add(mLeftBottom);
        mList.add(mRightBottom);
        //
        mLeftTop.setmImageIndex(R.drawable.ic_zoom_out);
        mRightTop.setmImageIndex(R.drawable.ic_zoom_in);
        mLeftBottom.setmImageIndex(R.drawable.ic_delete_color);
        mRightBottom.setmImageIndex(R.drawable.ic_zoom_out_map);

        mLeftTop.setmImageCacheManager(mShape.getmImageCacheManager());
        mRightTop.setmImageCacheManager(mShape.getmImageCacheManager());
        mLeftBottom.setmImageCacheManager(mShape.getmImageCacheManager());
        mRightBottom.setmImageCacheManager(mShape.getmImageCacheManager());
    }

    public void reloadPoint(){
        float x = mShape.points.get(0).x;
        float y = mShape.points.get(0).y;
        float w = mShape.points.get(1).x - x;
        float h = mShape.points.get(1).y - y;
        int size = 72;

        //mLeftTop.points.get(0).x = x;
        mLeftTop.points.clear();
        mRightTop.points.clear();
        mLeftBottom.points.clear();
        mRightBottom.points.clear();
        // TRACKER OUTSIDE SHAPE..
        /*mLeftTop.points.add(new Point(x-size , y-size ));
        mLeftTop.points.add(new Point(x , y));
        //
        mRightTop.points.add(new Point(x+ w , y-size ));
        mRightTop.points.add(new Point(x + w+size , y));
        //
        mLeftBottom.points.add(new Point(x -size, y + h ));
        mLeftBottom.points.add(new Point(x , y +h + size));*/
        //
        mRightBottom.points.add(new Point(x+w-5 , y + h -5 ));
        mRightBottom.points.add(new Point(x + w + size-5 , y+h+size-5));
        //

        //mRightBottom.points.add(new Point(x+w-size , y + h-size ));
        //mRightBottom.points.add(new Point(x + w , y+h));

    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {
        //
        if(mShape.points.size() == 2){

            reloadPoint();

            mLeftTop.render(canvas, paint, matrix);
            mRightTop.render(canvas, paint, matrix);
            mLeftBottom.render(canvas, paint, matrix);
            mRightBottom.render(canvas, paint, matrix);
        }
    }

    @Override
    public void touchMove(int dx, int dy){

        if(mShape.points.size() == 2){
            //points.get(0).x = points.get(0).x + dx;
            //points.get(0).y = points.get(0).y + dy;
            mShape.points.get(1).x = mShape.points.get(1).x + dx;
            mShape.points.get(1).y = mShape.points.get(1).y + dy;
        }
    }

    public void resize(int w, int h){

    }

    public void touchUp(float x, float y){

    }

    public boolean hitTest(int x, int y){
        for(int i = 0; i< mList.size(); i++){
            Indicator s = (Indicator) mList.get(i);
            if(s.hitTest((int)x, (int)y) ){
                mSelected = s;
                return true;
            }
        }
        return false;
    }

    public Shape getmSelected(){
        return mSelected;
    }

    public void onUpdate(){
        for(int i = 0; i< mList.size(); i++){
            Indicator s = (Indicator) mList.get(i);
            s.onUpdate();
        }
    }
}

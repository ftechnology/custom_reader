package com.microasset.saiful.drawings;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Line extends Rectangle {

    /**
     * default constructor
     */
    public Line(String type) {
        super(type);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {
        if(points.size() == 2) {
            float x = points.get(0).x;
            float y = points.get(0).y;
            float w = points.get(1).x - x;
            float h = points.get(1).y - y;

            Point p1 = new Point(x+2, y);
            Point p2 = new Point(x + w-2, y+h );
            //
            paint.setColor(mLineColor);
            Path path = new Path();
            paint.setStrokeWidth(mStrokeWidth);
            paint.setAlpha(mAlpha);
            path.moveTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
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

    @Override
    public boolean hitTest(int x, int y)
    {
        int padding = 20;
        Rect r = new Rect();
        r.left = (int)points.get(0).x -padding;
        r.top = (int)points.get(0).y - padding;
        r.right = (int)points.get(1).x + padding;
        r.bottom = (int)points.get(1).y + padding;
        //
        return (r.contains(x, y));
    }
}

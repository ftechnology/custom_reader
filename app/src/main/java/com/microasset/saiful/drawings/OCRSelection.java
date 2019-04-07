package com.microasset.saiful.drawings;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;


public class OCRSelection extends Rectangle {
    int mCounter = 0;
    static OCRSelection ocrSelection;

    public static OCRSelection getOCRSelection(){
        if(ocrSelection == null){
            ocrSelection = new OCRSelection("OCRSelection");
        }
        return ocrSelection;
    }

    public static void release(){
        ocrSelection.clearPoints();
        ocrSelection.setmVisible(false);
        ocrSelection = null;
    }

    /**
     * default constructor
     */
    public OCRSelection(String type)
    {
        super(type);
        this.setLineColor(Color.GREEN);
        mVisible = false;
        this.mAlpha = 200;
        mStrokeWidth = 5;
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix matrix) {
        if(!mVisible){
            return;
        }
        super.render(canvas, paint, matrix);
    }

    @Override
    public void touchStart(int x, int y){
        //OCRSelection.getOCRSelection().points.clear();
    }

    public void createShape(float x, float y, int w, int h, int pageIndex, String bookId, String type){
        //
        points.clear();
        //
        if(x - w/2 < mClipPadding){
            x = x + w/2;
        }
        if(y - h/2 < mClipPadding){
            y = y + h/2;
        }

        if(x + w > mCanvasWidth - mClipPadding){
            x = x - w;
        }
        if(y+h > mCanvasHeight - mClipPadding){
            y = y - h;
        }

        points.add(new Point(x - w/2, y - h/2));
        points.add(new Point(x + w, y + h/2));
        setmBookId(bookId);
        setmPageIndex(pageIndex);
        this.setmVisible(true);
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

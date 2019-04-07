package com.microasset.saiful.drawings;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ImageCacheManager;

import java.util.ArrayList;


/**
 * super class for drawable Shapes
 * contains different properties to render the shape
 * contains list of points
 *
 * @author ashru
 *
 */
public abstract class Shape  extends DataObject {
    ImageCacheManager mImageCacheManager;
    public static enum State { STATE_IDLE, STATE_DRAW_OBJECT, STATE_MOVE_OBJECT };
    protected State mState;

    String mType = "";
    ShapeDecorator mShapeDecorator;
    protected int mCanvasWidth = 0;
    protected int mCanvasHeight = 0;
    protected int mClipPadding = 10;//PiX
    //
    protected boolean mVisible;

    /**
     * number of point chosen already for this object
     */
    int pointChosen = 0;
    //
    int mPageIndex = -1;
    //
    int mImageIndex = -1;
    //
    int mMinSize = 36;
    /**
     * list of points added to the shape
     * ArrayList is allocated when object is created (no new points are added or removed)
     */
    protected ArrayList<Point> points;

    int mAlpha = 128;
    /**
     * color to draw outlines
     */
    int mLineColor = Color.RED;

    /**
     * line or outline width in pixel
     */
    int mStrokeWidth = 7;

    /**
     * color to draw endpoint circle
     */
    int circleColor = Color.BLUE;

    /**
     * radius of endpoint circles
     */
    int circleRadius = 4;

    /**
     * screen to real unit (mm) multiplier
     */
    float unitMultiplier = 1.0f;

    /**
     * color to render text
     */
    int mTextColor = Color.BLACK;
    //
    protected String mBookId;
    //
    protected boolean mDrawBorder = false;

    /**
     * don't let anyone instantiate this class.
     */
    public Shape(String type) {
        super(0);
        mType = type;
        this.points = new ArrayList<>();
        mState = State.STATE_IDLE;
        mVisible = true;
    }

    public String getmType(){
        return mType;
    }

    public void setmBookId(String bookId){
        mBookId = bookId;
    }
    /**
     * render the shape
     * @param matrix transformation matrix
     */
    protected abstract void render(Canvas canvas, Paint paint, Matrix matrix);

    public void touchStart(int x, int y){}
    public void touchMove(int dx, int dy){}
    public void touchUp(float x, float y){}

    public boolean hitTest(int x, int y){ return false;}

    public void setmPageIndex(int mPageIndex) {
        this.mPageIndex = mPageIndex;
    }

    public int getmPageIndex(){
        return mPageIndex;
    }
    //
    public int getmImageIndex(){return  mImageIndex;}
    //
    public void setmImageIndex(int index){ mImageIndex = index;}
    //
    public void resize(int w, int h){}

    public void setmState(State state) {
        this.mState = state;
        if(state == State.STATE_MOVE_OBJECT){
            setmShapeDecorator(new ShapeDecorator("ShapeDecorator",this));
        } else{
            setmShapeDecorator(null);
        }
    }

    public void setmImageCacheManager(ImageCacheManager mImageCacheManager) {
        this.mImageCacheManager = mImageCacheManager;
    }

    public String getmBookId(){
        return mBookId;
    }

    //
    public DataObject toEntity(){ return this;}
    //

    public void setmShapeDecorator(ShapeDecorator mShapeDecorator) {
        this.mShapeDecorator = mShapeDecorator;
    }

    public ShapeDecorator getmShapeDecorator() {
        return mShapeDecorator;
    }

    public ImageCacheManager getmImageCacheManager(){
        return mImageCacheManager;
    }

    public void adjustBoundary(){}

    public void setLineColor(int color){
        mLineColor = color;
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setmTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public int getmTextColor(){
        return mTextColor;
    }

    public void setmAlpha(int alpha){
        mAlpha = alpha;
    }

    public int getmAlpha(){
        return mAlpha;
    }

    public void setmStrokeWidth(int strokeWidth){
        mStrokeWidth = strokeWidth;
    }

    public int getmStrokeWidth(){
        return mStrokeWidth;
    }

    public  void setCanvasSize(int w, int h){
        mCanvasHeight = h;
        mCanvasWidth = w;
    }
    public void onUpdate(){}

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setmVisible(boolean show){
        mVisible = show;
    }

    public boolean ismVisible() {
        return mVisible;
    }

    public void clearPoints(){ this.points.clear();}
}

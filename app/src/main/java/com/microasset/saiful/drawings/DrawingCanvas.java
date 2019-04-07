package com.microasset.saiful.drawings;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.FactoryAdapter;
import com.microasset.saiful.appfrw.ImageCacheManager;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.color.PaletteBar;
import com.microasset.saiful.database.BaseEntity;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.entity.DrawingObjectEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.DrawingObjectDbModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DrawingCanvas extends BaseModel {

    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Paint mPaint;
    //private ArrayList mListObject = new ArrayList<>();
    private View mView;
    private Shape mSelected;
    static DrawingCanvas mDrawingCanvas;
    NotifyObserver mNotifyObserver;

    @Override
    public ResponseObject execute() {
        return null;
    }
    public static enum State { NONE, DRAW_OBJECT, MOVE_OBJECT };
    private State mState;
    ImageCacheManager mImageCacheManager;
    PaletteBar mPaletteBar;
    int mScaleFactor = 1;
    int mShapeWidth = 72;
    int mShapeHeight = 72;
    //
    int mCanvasWidth = 0;
    int mCanvasHeight = 0;
    //
    Timer mTimer = null;
    private String mSelectedShape  = "";
    //

    public void setSelectedShape(String shape){
         mSelectedShape = shape;
    }

    public void setCanvasSize(int w, int h){
        mCanvasHeight = h;
        mCanvasWidth = w;
        //
        mShapeHeight = (int) (w * Constants.SHAPE_SCALE_TO_IMAGE);
        mShapeWidth = (int) (h * Constants.SHAPE_SCALE_TO_IMAGE);
    }

    public int getmShapeWidth(){
        return mShapeWidth;
    }

    public int getmShapeHeight(){
        return mShapeHeight;
    }

    public static DrawingCanvas getInstance(){
        if(mDrawingCanvas == null){
            mDrawingCanvas = new DrawingCanvas();
        }

        return mDrawingCanvas;
    }

    public void setmNotifyObserver(NotifyObserver notifyObserver) {
        this.mNotifyObserver = notifyObserver;
        if(notifyObserver instanceof View){
            mView = (View) notifyObserver;
        }
    }

    public View getmView(){
        return mView;
    }

    public DrawingCanvas() {
        mState = State.NONE;
        //mView = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        mDrawingCanvas = this;
    }


    public void loadItems(Context context) {
        mContext = context;
        mListItem.clear();
        onCreateTimer();
		//
        //OCR item.
        this.add(OCRSelection.getOCRSelection());
        OCRSelection.getOCRSelection().setmBookId(BookInfoModel.getInstance(null).getmSelectedBook());
        //
        String bookId = BookInfoModel.getInstance(context).getmSelectedBook();
        ArrayList<DataObject>  list = DrawingObjectDbModel.getInstance(context).getDrawingList(bookId);
        if(list != null){
            for (int i = 0; i<list.size(); i++){
               DrawingObjectEntity drawingObjectEntity = (DrawingObjectEntity) list.get(i);
                Shape s = ShapeFactory.createSymbol(drawingObjectEntity);
                if(s != null){
                    this.add(s);
                }
            }
        }
        mImageCacheManager = new ImageCacheManager(context);
    }

    public void addItem(Shape shape){
        this.add(shape);
        shape.setmImageCacheManager(mImageCacheManager);
        long id = DrawingObjectDbModel.getInstance(mContext).insert((BaseEntity)shape.toEntity());
        shape.setId(id);
        // TODO testig code...
        DrawingObjectDbModel.getInstance(mContext).getDrawingList(shape.getmBookId(),Convert.toString(shape.getmPageIndex()));
    }

    public void setState(State state){
        mState = state;
    }

    public State getmState(){
        return mState;
    }

    public void onDraw(Canvas canvas, int pageIndex) {
        String bookId = BookInfoModel.getInstance(mContext).getmSelectedBook();
        for(int i = 0; i< this.getCount(); i++){
            Shape s = (Shape) this.getItem(i);
            if(s.getmPageIndex() == pageIndex && s.getmBookId().equals(bookId) ){
                //Bitmap bitmap = mImageCacheManager.loadImage("RESOURCE",Convert.toString( s.getmImageIndex()) );
                mPaint.setMaskFilter(null);
                s.setmImageCacheManager(mImageCacheManager);
                s.setCanvasSize(mCanvasWidth, mCanvasHeight);
                s.render(canvas, mPaint, null);
            }
        }
    }

    private boolean touchStart(float x, float y, int pageIndex) {

        mX = x;
        mY = y;
        // We need to check which corner user touch...

        if(mSelected != null) {

            if(mSelected.getmShapeDecorator() != null){
                if (mSelected.getmShapeDecorator().hitTest((int) x, (int) y)) {
                    return true;
                }
            }
        }

        if(OCRSelection.getOCRSelection().mState == Shape.State.STATE_DRAW_OBJECT){
            String bookId = BookInfoModel.getInstance(null).getmSelectedBook();
            OCRSelection.getOCRSelection().setCanvasSize(mCanvasWidth, mCanvasHeight);
            OCRSelection.getOCRSelection().createShape(x, y, mShapeWidth*5, mShapeHeight, pageIndex, bookId, mSelectedShape);
            OCRSelection.getOCRSelection().mState = Shape.State.STATE_IDLE;
            return true ;
        }

        this.hitTest(x, y, pageIndex);
        if(mSelected != null){
            mSelected.touchStart((int) x, (int) y);
            return true;
        }

        return false;
    }

    private boolean touchMove(float x, float y, int pageIndex) {
        float dx = x - mX;
        float dy = y - mY;

        if(mSelected != null){
            if(mSelected.getmShapeDecorator() != null){
                if(mSelected.getmShapeDecorator().getmSelected() != null){
                    mSelected.getmShapeDecorator().touchMove((int)dx, (int)dy);

                    mX = x;
                    mY = y;
                    return true;
                }
            }

            //
            mSelected.touchMove((int)dx, (int)dy);
            //
            mX = x;
            mY = y;

            return true;
        }

        return false;
    }

    private boolean touchUp(float x, float y, int pageIndex) {
        if(mState == DrawingCanvas.State.DRAW_OBJECT && !mSelectedShape.isEmpty()){
            String bookId = BookInfoModel.getInstance(null).getmSelectedBook();
            Shape shape = ShapeFactory.createShape(x, y, mShapeWidth, mShapeHeight, pageIndex, bookId, mSelectedShape);
            addItem(shape);
            mSelected = shape;
            setState(DrawingCanvas.State.MOVE_OBJECT);
            shape.setmState(Shape.State.STATE_MOVE_OBJECT);
            if(mPaletteBar != null){
                mPaletteBar.setmCurrentColor(mSelected.getLineColor());
            }
            mSelectedShape = "";
            return true;
        }

        if(mSelected !=null){
            mSelected.adjustBoundary();
        }

        this.hitTest(x, y, pageIndex);

        if(mSelected !=null){
            if( !(mSelected instanceof OCRSelection)){
                DrawingObjectDbModel.getInstance(mContext).update((BaseEntity)mSelected.toEntity());
            }
            if(mSelected.getmShapeDecorator() != null){
                mSelected.getmShapeDecorator().touchUp(x, y);
            }
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event, float x, float y, int pageIndex) {
        //float x = event.getX();
        //float y = event.getY();
        boolean handle = false;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                handle = touchStart(x, y, pageIndex);
                break;
            case MotionEvent.ACTION_MOVE :
                handle = touchMove(x, y, pageIndex);
                break;
            case MotionEvent.ACTION_UP :
                handle = touchUp(x, y, pageIndex);
                break;
        }

        mView.invalidate();

        return handle;
    }

    public Shape hitTest(float x, float y, int pageIndex){
        mSelected = null;

        for(int i = 0; i< this.getCount(); i++){
            Shape s = (Shape) this.getItem(i);
            s.setmState(Shape.State.STATE_IDLE);
            s.setmShapeDecorator(null);
            mPaint.setMaskFilter(null);
            if(pageIndex == s.getmPageIndex() && null == mSelected) {
                if (s.hitTest((int) x, (int) y)) {
                    mSelected = s;
                    // FIXME Need to move this code from here...
                    mSelected.setmState(Shape.State.STATE_MOVE_OBJECT);
                    if (mPaletteBar != null) {
                        mPaletteBar.setmCurrentColor(mSelected.getLineColor());
                    }
                }
            }
        }

        return mSelected;
    }

    public int deleteSelectedItem(){
        if(mSelected !=null){
            int r = DrawingObjectDbModel.getInstance(mContext).delete(mSelected.getId());

            if(mNotifyObserver != null){
                // Remove from This list..
                this.remove(mSelected);
                updateView();
            }

            return r;
        }

        return -1;
    }

    public long delteAllDrawingItemFromPage(int pageNumber){
        int count = 0;
        String bookId = BookInfoModel.getInstance(null).getmSelectedBook();

        ArrayList<DataObject> list = new ArrayList<>();

        for(int i = 0; i< this.getCount(); i++){
            Shape s = (Shape) this.getItem(i);
            if(s.getmPageIndex() == pageNumber && s.getmBookId().equals(bookId) ){
                int r = DrawingObjectDbModel.getInstance(mContext).delete(s.getId());
                if(r>=0){
                    count++;
                    list.add(s);
                }
            }
        }

        //Remove the items
        for(int i = 0; i<list.size(); i++){
            mListItem.remove(list.get(i));
        }

        if(count > 0){
            updateView();
        }

        return count;
    }

    public Shape getmSelected(){
        return mSelected;
    }

    public void updateProperty(int color){
        if(mSelected != null){
            mSelected.setLineColor(color);
            mView.invalidate();
        }
    }

    public void updateView(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setResponseMsg("deleteSelectedItem");
        responseObject.setDataObject(null);
        mNotifyObserver.update(responseObject);
    }

    public void setPaletteBar(PaletteBar paletteBar){
        mPaletteBar = paletteBar;
        if(mPaletteBar != null){

            Shape shape =  getmSelected();
            if(shape != null){
                mPaletteBar.setmCurrentColor(shape.getLineColor());
            }

            mPaletteBar.setListener(new PaletteBar.PaletteBarListener() {
                @Override
                public void onColorSelected(int color, boolean isFinal) {
                    updateProperty(color);
                    if(isFinal && mSelected != null){
                        DrawingObjectDbModel.getInstance(mContext).update((BaseEntity)mSelected.toEntity());
                    }
                }
            });
        }
    }

    public void onCreateTimer() {
        if(mTimer != null){
            return;
        }
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 500);
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        ((Activity)mContext).runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            if(OCRSelection.getOCRSelection().ismVisible()){
                OCRSelection.getOCRSelection().onUpdate();
                mView.invalidate();
                return;
            }

            //This method runs in the same thread as the UI.
            if(mSelected != null) {
                if (mSelected.getmShapeDecorator() != null) {
                    mSelected.getmShapeDecorator().onUpdate();
                    mView.invalidate();
                }
            }
            //Do something to the UI thread here
        }
    };

    public void createOCR(int x, int y, int pageIndex){
        String bookId = BookInfoModel.getInstance(null).getmSelectedBook();
        OCRSelection.getOCRSelection().setCanvasSize(mCanvasWidth, mCanvasHeight);
        OCRSelection.getOCRSelection().createShape(x, y, mShapeWidth*7, mShapeHeight, pageIndex, bookId, mSelectedShape);
        OCRSelection.getOCRSelection().mState = Shape.State.STATE_IDLE;
        DrawingCanvas.getInstance().updateView();
    }
}
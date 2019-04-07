package com.microasset.saiful.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.microasset.saiful.drawings.DrawingCanvas;
import com.microasset.saiful.drawings.OCRSelection;

/**
 * Created by farukhossain on 2018/12/03.
 */

public class HackyViewPager extends ViewPager {

    public HackyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {

            if(event.getAction() == MotionEvent.ACTION_MOVE){
                if(DrawingCanvas.getInstance().getmSelected() != null){
                    return false;
                }
                if(OCRSelection.getOCRSelection().ismVisible()){
                    return false;
                }
            }
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

}

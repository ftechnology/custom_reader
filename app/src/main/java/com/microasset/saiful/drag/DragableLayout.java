
package com.microasset.saiful.drag;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.microasset.saiful.easyreader.R;

public class DragableLayout extends FrameLayout {

    private static final float PARALLAX_FACTOR = 0.2f;

    private static DecelerateInterpolator mDecelerator = new DecelerateInterpolator();

    private float mParallaxFactor;

    private int mBottomPanelPeekHeight;

    private float mTouchY;

    private boolean mTouching;

    private boolean mOpened = false;

    private VelocityTracker mVelocityTracker = null;

    private View mBottomPanel;

    private View mSlidingPanel;

    private Drawable mShadowDrawable;

    private boolean mAnimating = false;

    private boolean mWillDrawShadow = false;

    private int mTouchSlop;

    private boolean mIsBeingDragged = false;

    public DragableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initAttrs(context, attrs);
    }

    public DragableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);
    }

    public DragableLayout(Context context) {
        super(context);

        mBottomPanelPeekHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources()
                .getDisplayMetrics());
        mParallaxFactor = PARALLAX_FACTOR;

        if (!isInEditMode()) {
            mShadowDrawable = getResources().getDrawable(R.drawable.shadow_np);
            mWillDrawShadow = true;
        }

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DraggedPanelLayout, 0, 0);

        try {
            mParallaxFactor = a.getFloat(R.styleable.DraggedPanelLayout_parallax_factor, PARALLAX_FACTOR);
            if (mParallaxFactor < 0.1 || mParallaxFactor > 0.9) {
                mParallaxFactor = PARALLAX_FACTOR;
            }

            int defaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources()
                    .getDisplayMetrics());
            mBottomPanelPeekHeight = a.getDimensionPixelSize(R.styleable.DraggedPanelLayout_bottom_panel_height,
                    defaultHeight);
            int shadowDrawableId = a.getResourceId(R.styleable.DraggedPanelLayout_shadow_drawable, -1);
            if (shadowDrawableId != -1) {
                mShadowDrawable = getResources().getDrawable(shadowDrawableId);
                mWillDrawShadow = true;
                setWillNotDraw(!mWillDrawShadow);
            }
        } finally {
            a.recycle();
        }

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (!isInEditMode() && mWillDrawShadow) {
            int top = (int) (mSlidingPanel.getTop() + mSlidingPanel.getTranslationY());
            mShadowDrawable.setBounds(0, top - mShadowDrawable.getIntrinsicHeight(), getMeasuredWidth(), top);
            mShadowDrawable.draw(canvas);

        }
        if (mAnimating) {
            ViewCompat.postInvalidateOnAnimation(DragableLayout.this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getChildCount() != 2) {
            throw new IllegalStateException("DraggedPanelLayout must have 2 children!");
        }

        mBottomPanel = getChildAt(0);
        mBottomPanel.layout(left, top, right, bottom - mBottomPanelPeekHeight);

        mSlidingPanel = getChildAt(1);
        if (!mOpened) {
            int panelMeasuredHeight = mSlidingPanel.getMeasuredHeight();
            mSlidingPanel.layout(left, bottom - mBottomPanelPeekHeight, right, bottom - mBottomPanelPeekHeight
                    + panelMeasuredHeight);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(mTouchY - event.getY()) > mTouchSlop) {
                mIsBeingDragged = true;
                startDragging(event);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
        }

        return mIsBeingDragged;
    }

    public void startDragging(MotionEvent event) {
        mTouchY = event.getY();
        mTouching = true;

        mBottomPanel.setVisibility(View.VISIBLE);

        obtainVelocityTracker();
        mVelocityTracker.addMovement(event);
        allowShadow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int st = mSlidingPanel.getTop();
            //int bt = bottomPanel.getTop();

            // I want movement only when user touch on sliding panel..
            if (st > event.getY()) {
                return true;
            }
            startDragging(event);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (mTouching) {
                mVelocityTracker.addMovement(event);

                float translation = event.getY() - mTouchY;
                translation = boundTranslation(translation);

                mSlidingPanel.setTranslationY(translation);
                mBottomPanel
                        .setTranslationY((float) (mOpened ? -(getMeasuredHeight() - mBottomPanelPeekHeight - translation)
                                * mParallaxFactor : translation * mParallaxFactor));

                if (mWillDrawShadow) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            mTouching = false;

            mVelocityTracker.addMovement(event);
            mVelocityTracker.computeCurrentVelocity(1);
            float velocityY = mVelocityTracker.getYVelocity();
            mVelocityTracker.recycle();
            mVelocityTracker = null;

            finishAnimateToFinalPosition(velocityY);
        }

        return true;
    }

    public float boundTranslation(float translation) {
        if (!mOpened) {
            if (translation > 0) {
                translation = 0;
            }
            if (Math.abs(translation) >= mSlidingPanel.getMeasuredHeight() - mBottomPanelPeekHeight) {
                translation = -mSlidingPanel.getMeasuredHeight() + mBottomPanelPeekHeight;
            }
        } else {
            if (translation < 0) {
                translation = 0;
            }
            if (translation >= mSlidingPanel.getMeasuredHeight() - mBottomPanelPeekHeight) {
                translation = mSlidingPanel.getMeasuredHeight() - mBottomPanelPeekHeight;
            }
        }
        return translation;
    }

    public void obtainVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    public void finishAnimateToFinalPosition(float velocityY) {
        final boolean flinging = Math.abs(velocityY) > 0.5;

        boolean opening;
        float distY;
        long duration;

        if (flinging) {
            // If fling velocity is fast enough we continue the motion starting
            // with the current speed

            opening = velocityY < 0;

            distY = calculateDistance(opening);
            duration = Math.abs(Math.round(distY / velocityY));

            animatePanel(opening, distY, duration);
        } else {
            // If user motion is slow or stopped we check if half distance is
            // traveled and based on that complete the motion

            boolean halfway = Math.abs(mSlidingPanel.getTranslationY()) >= (getMeasuredHeight() - mBottomPanelPeekHeight) / 2;
            opening = mOpened ? !halfway : halfway;

            distY = calculateDistance(opening);
            duration = Math.round(300 * (double) Math.abs((double) mSlidingPanel.getTranslationY())
                    / (double) (getMeasuredHeight() - mBottomPanelPeekHeight));

        }

        animatePanel(opening, distY, duration);
    }

    public float calculateDistance(boolean opening) {
        float distY;
        if (mOpened) {
            distY = opening ? -mSlidingPanel.getTranslationY() : getMeasuredHeight() - mBottomPanelPeekHeight
                    - mSlidingPanel.getTranslationY();
        } else {
            distY = opening ? -(getMeasuredHeight() - mBottomPanelPeekHeight + mSlidingPanel.getTranslationY())
                    : -mSlidingPanel.getTranslationY();
        }

        return distY;
    }

    public void animatePanel(final boolean opening, float distY, long duration) {
        ObjectAnimator slidingPanelAnimator = ObjectAnimator.ofFloat(mSlidingPanel, View.TRANSLATION_Y,
                mSlidingPanel.getTranslationY(), mSlidingPanel.getTranslationY() + distY);
        ObjectAnimator bottomPanelAnimator = ObjectAnimator.ofFloat(mBottomPanel, View.TRANSLATION_Y,
                mBottomPanel.getTranslationY(), mBottomPanel.getTranslationY() + (float) (distY * mParallaxFactor));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(slidingPanelAnimator, bottomPanelAnimator);
        set.setDuration(duration);
        set.setInterpolator(mDecelerator);
        set.addListener(new MyAnimListener(opening));
        set.start();
    }

    class MyAnimListener implements AnimatorListener {

        int oldLayerTypeOne;

        int oldLayerTypeTwo;

        boolean opening;

        public MyAnimListener(boolean opening) {
            super();
            this.opening = opening;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            oldLayerTypeOne = mSlidingPanel.getLayerType();
            oldLayerTypeOne = mBottomPanel.getLayerType();

            mSlidingPanel.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            mBottomPanel.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            mBottomPanel.setVisibility(View.VISIBLE);

            if (mWillDrawShadow) {
                mAnimating = true;
                ViewCompat.postInvalidateOnAnimation(DragableLayout.this);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setOpenedState(opening);

            mBottomPanel.setTranslationY(0);
            mSlidingPanel.setTranslationY(0);

            mSlidingPanel.setLayerType(oldLayerTypeOne, null);
            mBottomPanel.setLayerType(oldLayerTypeTwo, null);

            requestLayout();

            if (mWillDrawShadow) {
                mAnimating = false;
                ViewCompat.postInvalidateOnAnimation(DragableLayout.this);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (mWillDrawShadow) {
                mAnimating = false;
                ViewCompat.postInvalidateOnAnimation(DragableLayout.this);
            }
        }

    }

    ;

    private void setOpenedState(boolean opened) {
        this.mOpened = opened;
        mBottomPanel.setVisibility(opened ? View.GONE : View.VISIBLE);
        hideShadowIfNotNeeded();
    }

    private void allowShadow() {
        mWillDrawShadow = mShadowDrawable != null;
        setWillNotDraw(!mWillDrawShadow);
    }

    private void hideShadowIfNotNeeded() {
        mWillDrawShadow = mShadowDrawable != null && !mOpened;
        setWillNotDraw(!mWillDrawShadow);
    }

}

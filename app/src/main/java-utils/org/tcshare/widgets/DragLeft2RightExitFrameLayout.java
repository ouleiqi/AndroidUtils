package org.tcshare.widgets;

import android.content.Context;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


/**
 *
 * 拖拽到边缘，退出当前Activity
 */
public class DragLeft2RightExitFrameLayout extends LinearLayout {
    private static final String TAG = DragLeft2RightExitFrameLayout.class.getSimpleName();
    private View view;
    private ViewDragHelper dragHelper;
    private DragExitListner listener;
    private GestureDetectorCompat gesture;

    public DragLeft2RightExitFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLeft2RightExitFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        dragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.d(TAG, "captured View  :" + (child == view));
                return child == view;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                Log.d(TAG, "clampViewPositionHorizontal left :" + left);
                return left > 0 ? left : super.clampViewPositionHorizontal(child, left, dx);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                int pWidth = getWidth();
                float xPos = view.getX();
                int end = xPos > pWidth / 2 ? pWidth : 0;
                if (dragHelper.smoothSlideViewTo(releasedChild, end, 0)) {
                    ViewCompat.postInvalidateOnAnimation(DragLeft2RightExitFrameLayout.this);
                }

                Log.d(TAG, String.format("xPos %f end %d pWidth %d", xPos, end, pWidth));
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                switch (state) {
                    case ViewDragHelper.STATE_DRAGGING:
                        break;
                    case ViewDragHelper.STATE_IDLE:
                        // float xPos = ViewHelper.getX(view);
                        float xPos = ViewCompat.getX(view);
                        int pWidth = getWidth();
                        if (listener != null && xPos > 0.99f * pWidth && xPos < 1.01f * pWidth) {
                            // float 有误差
                            listener.onExit();
                        }
                        break;
                    case ViewDragHelper.STATE_SETTLING:
                        break;
                }

                Log.d(TAG, String.format("viewDragStateChanged state %d", state));
            }
        });

        gesture = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "X directory: " + (Math.abs(distanceX) > Math.abs(distanceY)));
                return Math.abs(distanceX) > Math.abs(distanceY);
            }
        });
    }

    public void setDragExitListner(DragExitListner listener) {
        this.listener = listener;
    }


    public interface DragExitListner {
        void onExit();
    }

    @Override
    public void computeScroll() {
        //super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.v(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent");
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new ExceptionInInitializerError("Only support one child!");
        }
        view = getChildAt(0);
    }


}

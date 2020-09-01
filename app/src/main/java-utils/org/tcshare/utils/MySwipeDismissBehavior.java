package org.tcshare.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.behavior.SwipeDismissBehavior;


/**
 * Created by FallRain on 2017/5/15.
 */

public class MySwipeDismissBehavior extends SwipeDismissBehavior {


    public MySwipeDismissBehavior(final Context context, AttributeSet attrs) {
        setSwipeDirection(SWIPE_DIRECTION_START_TO_END);
        int view_width = context.getResources().getDisplayMetrics().widthPixels;
        setStartAlphaSwipeDistance(view_width);
        setDragDismissDistance(view_width /3f);
        setSensitivity(0.25f);
        setListener(new OnDismissListener() {
            @Override
            public void onDismiss(View view) {
                if(context instanceof Activity){
                    ((Activity)context).finish();
                }
            }

            @Override
            public void onDragStateChanged(int state) {

            }
        });
    }


}

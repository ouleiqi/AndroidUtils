package org.tcshare.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by 于小嘿 on 2018/4/25.
 */

public class DensityUtil {
    /**
     * convert dp to its equivalent px
     */
    public static int dp2px(Context ctx, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,ctx.getResources().getDisplayMetrics());
    }
    /**
     * convert px to its equivalent dp
     */
    public static int px2dp(Context ctx, int px){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,ctx.getResources().getDisplayMetrics());
    }

    /**
     * convert sp to its equivalent px
     */
    public static int sp2px(Context ctx,int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,ctx.getResources().getDisplayMetrics());
    }

    public static DisplayMetrics calcScreenSize(Activity ctx) {
        DisplayMetrics dm = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}

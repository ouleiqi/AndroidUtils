package org.tcshare.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

/**
 * 沉浸式工具
 */
public class ImmerseUtil {
    public static void hidden(Window window) {
       hidden(window,true);
    }
    public static void hidden(Window window, boolean keepScreenOn) {
        if(keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        // 导航栏，状态栏
        if(Build.VERSION.SDK_INT < 30) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }else {
          /*  WindowInsetsController ic = window.getInsetsController();
            if (ic != null) {
                ic.hide(WindowInsets.Type.statusBars());
                ic.hide(WindowInsets.Type.navigationBars());
            }*/
        }
    }
}

package org.tcshare.androidutils;

import android.app.Application;
import android.content.Context;

/**
 * Created by FallRain on 2017/8/21.
 */

public class App extends Application {
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
    }
}

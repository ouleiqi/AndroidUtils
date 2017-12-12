package org.tcshare.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

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

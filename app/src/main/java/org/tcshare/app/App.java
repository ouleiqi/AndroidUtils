package org.tcshare.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.tcshare.network.HttpApi;
import org.tcshare.network.HttpLogInterceptor;
import org.tcshare.network.cookie.CookieJarImpl;
import org.tcshare.network.cookie.MemoryCookieStore;
import org.tcshare.network.cookie.PersistentCookieStore;

import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by FallRain on 2017/8/21.
 */

public class App extends Application {
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        CookieJar cookieJar = new CookieJarImpl(new PersistentCookieStore(ctx));
        //CookieJar cookieJar = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HttpLogInterceptor())
                                                        .cookieJar(cookieJar)
                                                        .build();
        HttpApi.setClient(client);
    }
}

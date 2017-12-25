package org.tcshare.network;

import android.util.Log;

import org.tcshare.utils.BuildConfig;

import java.io.IOException;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by kaiyim on 2017/7/9 0009.
 */

public class HttpLogInterceptor implements okhttp3.Interceptor {
    private static final String TAG = HttpLogInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        req.headers().newBuilder().add("abc", new Random().nextDouble() +"").build();
        Response res = chain.proceed(req);
        res.newBuilder().addHeader("","").build();
        try {
            if(BuildConfig.DEBUG) Log.d(TAG, "req:" + req.toString());
            ResponseBody copyRes = res.peekBody(Long.MAX_VALUE);
            String resStr = copyRes.string();
            if(BuildConfig.DEBUG) Log.d(TAG, "res:" + resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}

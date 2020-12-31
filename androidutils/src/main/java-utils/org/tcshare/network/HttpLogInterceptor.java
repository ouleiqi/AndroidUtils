package org.tcshare.network;

import android.util.Log;


import com.google.gson.Gson;


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
    private final boolean debug;

    public HttpLogInterceptor(boolean debug) {
        this.debug = debug;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        Response res = chain.proceed(req);

        if(debug){
            try {
                String reqHeader = res.networkResponse().request().headers().toString();
                String resHeader = res.headers() .toString();
                Log.d(TAG,"reqHeaderStr:\n" + reqHeader);
                Log.d(TAG,"resHeaderStr:\n"+ resHeader);
                Log.d(TAG, "req:\n" + req.toString() +"\n" + new Gson().toJson(req.body()));
                ResponseBody copyRes = res.peekBody(Long.MAX_VALUE);
                String resStr = copyRes.string();
                Log.d(TAG, "res:\n" + resStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

}

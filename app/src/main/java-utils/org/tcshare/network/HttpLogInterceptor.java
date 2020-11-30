package org.tcshare.network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

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
        Response res = chain.proceed(req);

        if(HttpApi.DEBUG){
            try {
                String reqHeader = res.networkResponse().request().headers().toString();
                String resHeader = res.headers() .toString();
                Log.d(TAG,"reqHeaderStr" + reqHeader);
                Log.d(TAG,"resHeaderStr"+ resHeader);
                Log.d(TAG, "req:" + req.toString() + new Gson().toJson(req.body()));
                ResponseBody copyRes = res.peekBody(Long.MAX_VALUE);
                String resStr = copyRes.string();
                Log.d(TAG, "res:" + resStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

}

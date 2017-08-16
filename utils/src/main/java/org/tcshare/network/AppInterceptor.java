package org.tcshare.network;

import android.util.Log;

import org.tcshare.Constant;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by kaiyim on 2017/7/9 0009.
 */

public class AppInterceptor implements okhttp3.Interceptor {
    private static final String TAG = AppInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        Response res = chain.proceed(req);
        try {
            if(Constant.DEBUG) Log.d(TAG, "req:" + req.toString());
            ResponseBody copyRes = res.peekBody(Long.MAX_VALUE);
            String resStr = copyRes.string();
            if(Constant.DEBUG) Log.d(TAG, "res:" + resStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}

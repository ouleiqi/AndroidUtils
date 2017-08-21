package org.tcshare.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 不确定用户提交数据的类型，及方式，只提供 sendRequest 方法.
 */
public class BaseApiService {

    private static OkHttpClient client;
    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 提供修改client的方法。
     * @param client
     */
    public static void setClient(OkHttpClient client) {
        BaseApiService.client = client;
    }

    public static OkHttpClient getOkHttpClient() {
        if (client == null) {
            synchronized (BaseApiService.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                            .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
                            .writeTimeout(60, TimeUnit.SECONDS)//设置写入超时时间
                            .addInterceptor(new AppInterceptor())
                            .build();
                }
            }
        }
        return client;
    }

    public static void sendRequest(Request request, Callback callback) {
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static void sendRequest(Request request, MyCallBack callback) {
        getOkHttpClient().newCall(request).enqueue(callback);
    }

    public static class MyCallBack<T> implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            nextUI(call, e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            T obj = processResponce(call, response);
            nextUI(call, obj);

        }

        protected void nextUI(final Call call, final IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onFailureUI(call, e);
                }
            });
        }

        protected void nextUI(final Call call, final T response) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onResponseUI(call, response);
                }
            });

        }

        protected T processResponce(Call call, Response response) {
            T result = null;
            try {
                String str = response.body().string();
                result = new Gson().fromJson(str, (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public void onFailureUI(Call call, IOException e) {
            e.printStackTrace();
        }

        public void onResponseUI(Call call, T processObj) {

        }
    }
}

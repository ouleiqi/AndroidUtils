package org.tcshare.network;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 不确定用户提交数据的类型，及方式，只提供 sendRequest 方法.
 */
public class BaseApiService {

    private static OkHttpClient client;
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static final long CONECT_TIMEOUT= 30; // seconds
    private static final long READ_TIMEOUT= 30; // seconds
    private static final long WRITE_TIMEOUT= 90; // seconds

    /**
     * 提供修改client的方法。
     */
    public static void setClient(OkHttpClient client) {
        BaseApiService.client = client;
    }

    public static OkHttpClient getOkHttpClient() {
        if (client == null) {
            synchronized (BaseApiService.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder().connectTimeout(CONECT_TIMEOUT, TimeUnit.SECONDS)//设置超时时间
                                                       .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                                                       .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写入超时时间
                                                       .addInterceptor(new AppInterceptor())
                                                       .build();
                }
            }
        }
        return client;
    }

    public static OkHttpClient getProxyOkHttpClient(String proxyHost, int proxyPort, final String username, final String password) {
        if (client == null) {
            synchronized (BaseApiService.class) {
                if (client == null) {
                    Authenticator proxyAuthenticator = new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            String credential = Credentials.basic(username, password);
                            return response.request()
                                           .newBuilder()
                                           .header("Proxy-Authorization", credential)
                                           .build();
                        }
                    };
                    OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(CONECT_TIMEOUT, TimeUnit.SECONDS)//设置超时时间
                                                                             .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                                                                             .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写入超时时间
                                                                             .addInterceptor(new AppInterceptor())
                                                                             .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                                                                             .proxyAuthenticator(proxyAuthenticator);
                    client = builder.build();
                }
            }
        }
        return client;
    }

    public static void sendRequest(Request request, Callback callback) {
        getOkHttpClient().newCall(request)
                         .enqueue(callback);
    }

    public static void sendRequest(Request request, MyCallBack callback) {
        getOkHttpClient().newCall(request)
                         .enqueue(callback);
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
                String str = response.body()
                                     .string();
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

package org.tcshare.network;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 不确定用户提交数据的类型，及方式，只提供 sendRequest 方法.
 */
public class HttpApi {

    private static OkHttpClient client;
    private static final long CONECT_TIMEOUT= 60; // seconds
    private static final long READ_TIMEOUT= 90; // seconds
    private static final long WRITE_TIMEOUT= 300; // seconds

    /**
     * 提供修改client的方法。
     */
    public static void setClient(OkHttpClient client) {
        HttpApi.client = client;
    }

    public static OkHttpClient getOkHttpClient() {
        if (client == null) {
            synchronized (HttpApi.class) {
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
            synchronized (HttpApi.class) {
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

    private static Map<String, String> beanToMap(Object bean) {
        if (bean == null) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        Field[] fields = bean.getClass()
                             .getFields();
        if (fields == null || fields.length == 0) {
            return result;
        }

        for (Field field : fields) {
            //获取属性名称及值存入Map
            String key = field.getName();
            try {
                Object object = field.get(bean);
                if (object != null) {
                    String value = String.valueOf(field.get(bean));
                    result.put(key, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //获取父类属性
        fields = bean.getClass()
                     .getSuperclass()
                     .getFields();
        if (fields == null || fields.length == 0) {
            return result;
        }

        for (Field field : fields) {
            //获取属性名称及值存入Map
            String key = field.getName();
            try {
                Object object = field.get(bean);
                if (object != null) {
                    String value = String.valueOf(field.get(bean));
                    result.put(key, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Object sendRequest(Request request, Callback callback) {
        getOkHttpClient().newCall(request)
                         .enqueue(callback);
        return request.tag();
    }

    public static Object sendRequest(Request request, ResponseJSON callback) {
        callback.beforeStart();
        getOkHttpClient().newCall(request)
                         .enqueue(callback);
        return request.tag();
    }

    public static <T extends HttpResponse> void get(String url, T callBack){

    }
    public static <T extends HttpResponse> void get(String url, Map<String, String> params, T callBack){

    }
    public static <T extends HttpResponse> void get(String url, Object params, T callBack){
        get(url, beanToMap(params), callBack);
    }
    public static <T extends HttpResponse> void post(String url, Object params, T callBack){
        post(url, beanToMap(params), callBack);
    }
    public static <T extends HttpResponse> void post(String url, Map<String, String> params, T callBack){

    }
    public static <T extends HttpResponse> void postMultiForm(String url, Map<String, String> params,String fileKey, Map<String,File> fileMap, T callBack){

    }
    public static <T extends HttpResponse> void postMultiForm(String url, Map<String, String> params, Map<String,File> fileMap, T callBack){
        postMultiForm(url, params, "file[]", fileMap, callBack);
    }
    public static <T extends HttpResponse> void upload(String url, String fileKey, Map<String, File> fileMap, T callBack){

    }
    public static <T extends HttpResponse> void upload(String url, String fileKey, File file, T callBack){

    }
    public static <T extends HttpResponse> void upload(String url, File file, T callBack){
        upload(url, "file", file, callBack);
    }
    public static <T extends HttpResponse> void upload(String url, String fileKey, File[] file, T callBack){
        Map<String, File> map = new HashMap<>();
        for(File f : file){
            map.put(f.getName(), f);
        }
        upload(url, fileKey, map, callBack);
    }
    public static <T extends HttpResponse> void upload(String url,  File[] file, T callBack){
        upload(url, "file[]", file, callBack);
    }
}

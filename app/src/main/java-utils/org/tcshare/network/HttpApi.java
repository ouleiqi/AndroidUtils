package org.tcshare.network;

import com.google.gson.Gson;

import org.tcshare.network.cookie.CookieJarImpl;
import org.tcshare.network.cookie.MemoryCookieStore;
import org.tcshare.network.cookie.PersistentCookieStore;

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
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * 不确定用户提交数据的类型，及方式，只提供 sendRequest 方法.
 */
public class HttpApi {

    private static OkHttpClient client;
    private static final long   CONECT_TIMEOUT        = 60; // seconds
    private static final long   READ_TIMEOUT          = 90; // seconds
    private static final long   WRITE_TIMEOUT         = 300; // seconds
    public static final  String UPLOAD_FILE_KEY       = "file";
    public static final  String UPLOAD_MULTI_FILE_KEY = "file[]";

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
                                                       .build();
                }
            }
        }
        return client;
    }


    public static OkHttpClient.Builder getProxyAuthBuilder(String proxyHost, int proxyPort, final String username, final String password) {
        Authenticator proxyAuthenticator = getProxyHeaderAuth(username, password);
        return new OkHttpClient.Builder().connectTimeout(CONECT_TIMEOUT, TimeUnit.SECONDS)//设置超时时间
                                                                 .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                                                                 .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写入超时时间
                                                                 .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                                                                 .proxyAuthenticator(proxyAuthenticator);
    }

    public static Authenticator getProxyHeaderAuth(final String username, final String password){
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request()
                               .newBuilder()
                               .header("Proxy-Authorization", credential)
                               .build();
            }
        };
    }
    public static Authenticator getHeaderAuth(final String username, final String password){
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request()
                               .newBuilder()
                               .header("Authorization", credential)
                               .build();
            }
        };
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

    public static <T extends AResponse> Object sendRequest(Request request, T callback) {
        callback.onStart();
        getOkHttpClient().newCall(request)
                         .enqueue(callback);
        return request.tag();
    }

    public static <T extends AResponse> void get(String url, T callBack) {
        get(url, null, callBack);
    }

    public static <T extends AResponse> void get(String url, Object params, T callBack) {
        get(url, beanToMap(params), callBack);
    }

    public static <T extends AResponse> void get(String url, Map<String, String> params, T callBack) {
        Request.Builder getBuilder = RequestBuilderFactory.createGetRequestBuilder(url, params);
        sendRequest(getBuilder.build(), callBack);
    }

    public static <T extends AResponse> void post(String url, Object params, T callBack) {
        post(url, beanToMap(params), callBack);
    }

    public static <T extends AResponse> void post(String url, Map<String, String> params, T callBack) {
        Request.Builder formBuilder = RequestBuilderFactory.createPostRequestBuilder(url, params);
        sendRequest(formBuilder.build(), callBack);
    }

    public static <T extends AResponse> void post(String url, Map<String, String> params, String fileKey, Map<String, File> fileMap, T callBack) {
        Request.Builder multiFormBuilder = RequestBuilderFactory.createMultiPostRequestBuilder(url, fileKey, params, fileMap);
        sendRequest(multiFormBuilder.build(), callBack);
    }

    public static <T extends AResponse> void post(String url, Map<String, String> params, Map<String, File> fileMap, T callBack) {
        post(url, params, UPLOAD_MULTI_FILE_KEY, fileMap, callBack);
    }



    public static <T extends AResponse> void postJSON(String url, String json, T callBack) {
        Request.Builder jsonRequestBuilder = RequestBuilderFactory.createPostJsonRequestBuilder(url, json);
        sendRequest(jsonRequestBuilder.build(), callBack);

    }
    public static <T extends AResponse> void postJSON(String url, Object obj, T callBack) {
       postJSON(url, new Gson().toJson(obj), callBack);
    }




    public static <T extends AResponse> void upload(String url, String fileKey, Map<String, File> fileMap, T callBack) {
        post(url, null, fileKey, fileMap, callBack);
    }

    public static <T extends AResponse> void upload(String url, Map<String, File> fileMap, T callBack) {
        post(url, null, UPLOAD_MULTI_FILE_KEY, fileMap, callBack);
    }

    public static <T extends AResponse> void upload(String url, String fileKey, File file, T callBack) {
        Map<String, File> map = new HashMap<>();
        map.put(fileKey, file);
        post(url, null, map, callBack);
    }

    public static <T extends AResponse> void upload(String url, File file, T callBack) {
        upload(url, UPLOAD_FILE_KEY, file, callBack);
    }

    public static <T extends AResponse> void upload(String url, String fileKey, File[] file, T callBack) {
        Map<String, File> map = new HashMap<>();
        for (File f : file) {
            map.put(f.getName(), f);
        }
        upload(url, fileKey, map, callBack);
    }

    public static <T extends AResponse> void upload(String url, File[] file, T callBack) {
        upload(url, UPLOAD_MULTI_FILE_KEY, file, callBack);
    }

}

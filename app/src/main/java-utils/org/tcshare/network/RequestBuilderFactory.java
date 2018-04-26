package org.tcshare.network;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by FallRain on 2017/8/7.
 */

public class RequestBuilderFactory {
    // 允许修改header
    private static Headers headers = Headers.of();

    public static Headers getHeaders() {
        return headers;
    }

    public static void setHeaders(Headers headers) {
        RequestBuilderFactory.headers = headers;
    }

    /**
     * 多文件，多个字段
     */
    public static Request.Builder createMultiPostRequestBuilder(String targetUrl, String fileKey, Map<String, String> map, Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if(files != null && fileKey != null) {
            for (Map.Entry<String, File> entry : files.entrySet()) {
                builder.addFormDataPart(fileKey, entry.getValue()
                                                      .getName(), RequestBody.create(MediaType.parse(entry.getKey()), entry.getKey()));
            }
        }
        if(map  != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .post(builder.build()).headers(headers);
    }


    /**
     * post 表单，多个字段
     */
    public static Request.Builder createPostRequestBuilder(String targetUrl, Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if(map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .post(builder.build()).headers(headers);
    }
    public static Request.Builder createGetRequestBuilder(String targetUrl, Map<String, String> map) {
        if(map != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append("&")
                  .append(entry.getKey())
                  .append("=")
                  .append(entry.getValue());
            }
            targetUrl += targetUrl.contains("?") ? sb.toString() : "?" + sb.toString()
                                                                           .replaceFirst("&", "");
        }
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .get().headers(headers);
    }
    public static Request.Builder createPostJsonRequestBuilder(String targetUrl, String json) {
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .post(jsonBody).header("content-type", "application/json;charset:utf-8").headers(headers);
    }



}

package org.tcshare.network;

import android.support.v4.util.Pair;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by FallRain on 2017/8/7.
 */

public class RequestBuilderFactory {

    public static Request.Builder getPostRequestBuilder(String targetUrl, Map<String, String> map, Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            builder.addFormDataPart("file[]", entry.getValue().getName(), RequestBody.create(MediaType.parse(entry.getKey()), entry.getKey()));
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        return new Request.Builder()
                .url(targetUrl)
                .post(builder.build());
    }

    public static Request.Builder getPostRequestBuilder(String targetUrl, Map<String, String> map, Pair<String, File> filePair) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("file", filePair.second.getName(), RequestBody.create(MediaType.parse(filePair.first), filePair.second));
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        return new Request.Builder()
                .url(targetUrl)
                .post(builder.build());
    }

    public static Request.Builder getPostRequestBuilder(String targetUrl, Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return new Request.Builder()
                .url(targetUrl)
                .post(builder.build());
    }
}

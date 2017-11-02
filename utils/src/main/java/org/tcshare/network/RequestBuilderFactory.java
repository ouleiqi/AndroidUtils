package org.tcshare.network;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by FallRain on 2017/8/7.
 */

public class RequestBuilderFactory {

    /**
     * 多文件，多个字段
     */
    public static Request.Builder createMultiPostRequestBuilder(String targetUrl, String fileKey, Map<String, String> map, Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            builder.addFormDataPart(fileKey, entry.getValue()
                                                  .getName(), RequestBody.create(MediaType.parse(entry.getKey()), entry.getKey()));
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .post(builder.build());
    }


    /**
     * post 表单，多个字段
     */
    public static Request.Builder createPostRequestBuilder(String targetUrl, Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return new Request.Builder().url(targetUrl)
                                    .tag(UUID.randomUUID())
                                    .post(builder.build());
    }


}

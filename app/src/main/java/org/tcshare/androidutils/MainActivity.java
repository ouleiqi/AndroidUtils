package org.tcshare.androidutils;

import android.app.Activity;
import android.os.Bundle;

import org.tcshare.network.ApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, String> map = new HashMap<>();
        Request request = ApiService.getPostRequestBuilder("", map).build();
        ApiService.postRequest(request, new ApiService.MyCallBack() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

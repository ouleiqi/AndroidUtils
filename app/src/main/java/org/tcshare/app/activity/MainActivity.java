package org.tcshare.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tcshare.app.R;
import org.tcshare.app.network.ApiService;
import org.tcshare.network.HttpApi;
import org.tcshare.network.HttpResponse;
import org.tcshare.network.ResponseJSON;
import org.tcshare.network.ResponseString;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpApi.post("", null, new ResponseJSON<ApiService>() {
            @Override
            public void onResponseUI(Call call, ApiService processObj) {

            }
        });
    }

}

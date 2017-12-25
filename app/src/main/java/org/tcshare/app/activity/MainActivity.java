package org.tcshare.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.tcshare.app.R;
import org.tcshare.app.network.FosungNet;
import org.tcshare.network.AResponse;
import org.tcshare.network.HttpApi;
import org.tcshare.network.ResponseString;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpApi.post("http://47.104.110.144/card/api/member/login.do?username=18754151703&password=123", null, new ResponseString() {

            @Override
            public void onResponseUI(Call call, String processObj) {
                Log.e("login", processObj);
                HttpApi.post("http://47.104.110.144/card/api/member/send-code.do?phone=15866799753", null, new ResponseString() {

                    @Override
                    public void onResponseUI(Call call, String processObj) {
                        Log.e("send-code", processObj);
                        HttpApi.post("http://47.104.110.144/card/api/member/register.do?phone=15866799753&password=123123&code=123123", null, new ResponseString() {

                            @Override
                            public void onResponseUI(Call call, String processObj) {
                                Log.e("register", processObj);
                            }
                        });
                    }
                });

            }
        });

    }

}

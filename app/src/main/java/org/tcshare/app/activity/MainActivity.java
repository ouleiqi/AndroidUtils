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


    }

}

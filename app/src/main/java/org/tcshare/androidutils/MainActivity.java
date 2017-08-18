package org.tcshare.androidutils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.tcshare.network.ApiService;
import org.tcshare.network.RequestBuilderFactory;
import org.tcshare.permission.PermissionHelper;
import org.tcshare.utils.DownloadUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionHelper.request(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233, new PermissionHelper.Callback() {
                    @Override
                    public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                        Log.e("tmp", requestCode + "---" + permissions.toString() + "----" + grantResult.toString());
                    }
                });
            }
        });
        PermissionHelper.request(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 233, new PermissionHelper.Callback() {
            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                Log.e("tmp", requestCode + "---" + permissions.toString() + "----" + grantResult.toString());
            }
        });
    }

}

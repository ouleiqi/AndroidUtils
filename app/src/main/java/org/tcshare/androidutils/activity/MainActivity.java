package org.tcshare.androidutils.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.tcshare.androidutils.R;
import org.tcshare.androidutils.beans.AA;
import org.tcshare.androidutils.utils.SettingsSPUtils;
import org.tcshare.permission.PermissionHelper;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}

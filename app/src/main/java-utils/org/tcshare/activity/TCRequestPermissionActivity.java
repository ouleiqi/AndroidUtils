package org.tcshare.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import org.tcshare.permission.PermissionHelper;

/**
 * Created by FallRain on 2017/8/18.
 */

public class TCRequestPermissionActivity extends Activity {
    public static final String RESULT_RECEIVER = "result_receiver";
    public static final String PERMISSIONS_ARRAY = "permission_array";
    public static final String REQUEST_CODE = "req_code";
    public static final int DEFAULT_CODE = 2330;
    public static final String GRANT_RESULT = "grunt_result";
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        if(getIntent() != null) {
            resultReceiver = getIntent().getParcelableExtra(RESULT_RECEIVER);
            String[] permissionsArray = getIntent().getStringArrayExtra(PERMISSIONS_ARRAY);
            int requestCode = getIntent().getIntExtra(REQUEST_CODE, DEFAULT_CODE);
            if(!PermissionHelper.hasPermissions(this,permissionsArray)) {
                ActivityCompat.requestPermissions(this, permissionsArray, requestCode);
            }else {
                onComplete(requestCode, permissionsArray, new int[]{PackageManager.PERMISSION_GRANTED});
            }
        }else {
            finish();
        }
    }
    private void onPix(){
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }
    private void onComplete(int requestCode, String[] permissions, int[] grantResults) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSIONS_ARRAY, permissions);
        bundle.putIntArray(GRANT_RESULT, grantResults);
        resultReceiver.send(requestCode, bundle);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onComplete(requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}

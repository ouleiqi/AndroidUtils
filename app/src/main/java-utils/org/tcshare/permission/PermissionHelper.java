package org.tcshare.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.core.content.ContextCompat;

import org.tcshare.activity.TCRequestPermissionActivity;

import java.util.Arrays;
import java.util.Observable;

/**
 * Created by FallRain on 2017/8/18.
 */

public class PermissionHelper extends Observable {


    public static void request(Context ctx, String[] permissions, int requestCode, final Callback callback) {
        if(hasPermissions(ctx, permissions)){
            int[] result = new int[permissions.length];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            callback.onResult(requestCode, permissions,result);
        }else {
            Intent intent = new Intent();
            intent.putExtra(TCRequestPermissionActivity.RESULT_RECEIVER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    super.onReceiveResult(resultCode, resultData);
                    int[] grantResult = resultData.getIntArray(TCRequestPermissionActivity.GRANT_RESULT);
                    String[] permissions = resultData.getStringArray(TCRequestPermissionActivity.PERMISSIONS_ARRAY);
                    callback.onResult(resultCode, permissions, grantResult);
                }
            });
            intent.putExtra(TCRequestPermissionActivity.REQUEST_CODE, requestCode);
            intent.putExtra(TCRequestPermissionActivity.PERMISSIONS_ARRAY, permissions);
            intent.setClass(ctx, TCRequestPermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(intent);
        }
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public interface Callback {
        void onResult(int requestCode, String[] permissions, int[] grantResult);
    }
}

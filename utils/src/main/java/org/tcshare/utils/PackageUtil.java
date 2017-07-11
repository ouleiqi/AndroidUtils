package org.tcshare.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by FallRain on 2017/7/3.
 */

public class PackageUtil {
    public static boolean isAppInstalled(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static void startAPP(Context ctx, String appPackageName) {
        try {
            Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(appPackageName);
            ctx.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ctx, ctx.getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
        }
    }
}

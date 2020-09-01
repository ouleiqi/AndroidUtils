package org.tcshare.app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.tcshare.app.amodule.activity.TCSplashActivity;


public class CommonBroadCast extends BroadcastReceiver {
    private static final String TAG = CommonBroadCast.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Intent sayHelloIntent=new Intent(context, TCSplashActivity.class);
            sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sayHelloIntent);

        }else  if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
            Log.e(TAG, "挂载：" + intent.getData());
        }else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
            Log.e(TAG, "卸载：" + intent.getData());
        }else if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
            Log.e(TAG, "弹出：" + intent.getData());
            //String sourcePath = intent.getData().getPath();

        }
    }
}

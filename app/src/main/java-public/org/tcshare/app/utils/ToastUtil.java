package org.tcshare.app.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.tcshare.app.R;


/**
 * author:于小嘿
 * date: 2020/7/21
 * desc:
 */
public class ToastUtil {

    public static void showToastLong(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showToastShort(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, String msg, int duration) {
        Toast toast = Toast.makeText(context, msg, duration);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.bg_toast);
        TextView message = ((TextView) view.findViewById(android.R.id.message));
        message.setTextColor(Color.WHITE);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}

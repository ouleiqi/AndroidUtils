package org.tcshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tcshare.app.R;

/**
 * Created by yuxiaohei on 2018/4/24.
 */

public class LoadingDialog {

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, @Nullable String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        ImageView img =  view.findViewById(R.id.img);
        TextView tipText = view.findViewById(R.id.tipTextView);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_loading);
        img.startAnimation(animation);
        tipText.setVisibility(TextUtils.isEmpty(msg) ? View.GONE : View.VISIBLE);
        tipText.setText(msg);

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        return loadingDialog;
    }
}
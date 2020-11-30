package org.tcshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import org.tcshare.app.R;
import org.tcshare.utils.ImmerseUtil;

/**
 * Created by yuxiaohei on 2018/4/24.
 */

public class LoadingDialog {

    public static Dialog createLoadingDialog(final Context context, @Nullable String msg) {
        return createLoadingDialog(context, msg, false);
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @param immerse
     * @return
     */
    public static Dialog createLoadingDialog(final Context context, @Nullable String msg, boolean immerse) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        LinearLayout layout = view.findViewById(R.id.dialog_view);
        ImageView img = view.findViewById(R.id.img);
        TextView tipText = view.findViewById(R.id.tipTextView);

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_loading);
        img.startAnimation(animation);
        tipText.setVisibility(TextUtils.isEmpty(msg) ? View.GONE : View.VISIBLE);
        tipText.setText(msg);

        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        if (immerse) {
            loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ImmerseUtil.hidden(loadingDialog.getWindow());
                }
            });
        }
        return loadingDialog;
    }
}
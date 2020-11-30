package org.tcshare.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.tcshare.activity.TCSelectOnePictureActivity;
import org.tcshare.permission.PermissionHelper;
import org.tcshare.widgets.BottomListDialog;

/**
 * Created by yuxiaohei on 2018/5/2.
 */

public class SelectOnePicture {

    public static void doSelect(final Context ctx, final CallBack callBack) {
        PermissionHelper.request(ctx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 9999, new PermissionHelper
                .Callback() {
            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                for (int result : grantResult) {
                    if (PackageManager.PERMISSION_GRANTED != result) {
                        ToastUtil.showToastLong(ctx, "请授予选择照片、拍照的权限");
                        return;
                    }
                }
                showSelectPic(ctx,callBack);
            }
        });
    }

    private static void showSelectPic(final Context ctx, final CallBack callBack) {
        BottomListDialog.showSimpleDialog(ctx, new BottomListDialog.OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, BottomSheetDialog dialog) {
                if(pos == 0 || pos == 1) {
                    Intent intent = new Intent();
                    intent.putExtra(TCSelectOnePictureActivity.RESULT_RECEIVER, new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            super.onReceiveResult(resultCode, resultData);
                            String pic = resultData.getString(TCSelectOnePictureActivity.SELECT_PICTURE);
                            callBack.onResult(resultCode, pic);
                        }
                    });
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(ctx, TCSelectOnePictureActivity.class);
                    intent.putExtra(TCSelectOnePictureActivity.ACT_TYPE, pos);
                    ctx.startActivity(intent);
                }
                dialog.dismiss();
            }
        }, new String[]{"拍照", "从相册选择", "取消"});
    }

    public interface CallBack{
        void onResult(int resultCode, String path);
    }
}

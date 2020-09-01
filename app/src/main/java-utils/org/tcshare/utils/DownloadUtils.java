package org.tcshare.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import org.tcshare.app.R;
import org.tcshare.permission.PermissionHelper;

import java.io.File;

/**
 * Created by dell on 2017/4/7.
 */
public class DownloadUtils {
    private DownloadManager mDownloadManager;
    private Context mContext;
    private long downloadId;
    private String apkName;

    public DownloadUtils(Context context) {
        mContext = context;
    }
    public void download(String url){
        download(url, System.currentTimeMillis()+"");
    }
    public void download(String url, String name){
        download(url, name, null, null);
    }
    public void download(final String url, final String name, @Nullable final String title, @Nullable final String desc) {
        PermissionHelper.request(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10, new PermissionHelper.Callback() {
            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                if(grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    final String packageName = "com.android.providers.downloads";
                    int state = mContext.getPackageManager().getApplicationEnabledSetting(packageName);
                    //检测下载管理器是否被禁用
                    if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                            || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle(R.string.info).setMessage
                                (R.string.warn_download_manager_disable).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + packageName));
                                    mContext.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                    mContext.startActivity(intent);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    } else {
                        //正常下载流程
                        apkName = name;
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setAllowedOverRoaming(false);
                        //通知栏显示
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setTitle(TextUtils.isEmpty(title) ? mContext.getString(R.string.download) : title);
                        request.setDescription(TextUtils.isEmpty(desc) ? mContext.getString(R.string.downloading ): desc);
                        request.setVisibleInDownloadsUi(true);
                        //设置下载的路径
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
                        //获取DownloadManager
                        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadId = mDownloadManager.enqueue(request);
                        mContext.registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }else{
                    Toast.makeText(mContext,R.string.no_write_permission, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    /**
     * 检查下载状态
     */
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    installAPK();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, R.string.download_failed, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        cursor.close();
    }

    /**
     * 7.0兼容
     */
    private void installAPK() {
        File apkFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), apkName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }
}
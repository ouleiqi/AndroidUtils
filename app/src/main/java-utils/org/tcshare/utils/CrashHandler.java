package org.tcshare.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * author:于小嘿
 * date: 2020/7/31
 * desc:
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private final Context ctx;

    /** 系统默认的UncaughtException处理类 **/
    private Thread.UncaughtExceptionHandler mDefaultHandler  = Thread.getDefaultUncaughtExceptionHandler();
    /** 用来存储设备信息和异常信息 **/
    private Map<String, String> mInfos = new HashMap<String, String>();

    private DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Class restartActivity;
    public static File CRASH_DIR;

    public CrashHandler(Context ctx) {
        this.ctx = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
        
    }

    public void setRestartActivity(Class restartActivity){
        this.restartActivity = restartActivity;
    }


    /**
     * @描述: 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex)) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(this.getClass().toString(), e == null ? "" : e.toString());
            }

            // 重新启动应用
            if(restartActivity != null) {
                Intent intent = new Intent(ctx, restartActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
/*                PendingIntent restartIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用*/
            }

            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * @return true:如果处理了该异常信息;否则返回false.
     * @throws
     * @描述:自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    private boolean handleException(Throwable ex) {
        if (null == ex) {
            return false;
        }
        Toast.makeText(ctx, "程序异常，1秒后重启！", Toast.LENGTH_LONG).show();
        // 收集设备参数信息
        collectDeviceInfo();
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * @描述:收集设备参数信息
     */
    private void collectDeviceInfo() {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mInfos.put("versionName", versionName);
                mInfos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * @throws
     * @描述:保存错误信息到文件中 目录/sdcard/qfangadtv/crash/
     * @返回类型 void 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : mInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = mFormatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if(CRASH_DIR == null) {
                    CRASH_DIR = new File(Environment.getExternalStorageDirectory() + "/crash/");
                    if (!CRASH_DIR.exists()) {
                        CRASH_DIR.mkdirs();
                    }
                }
                FileOutputStream fos = new FileOutputStream(new File(CRASH_DIR, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}

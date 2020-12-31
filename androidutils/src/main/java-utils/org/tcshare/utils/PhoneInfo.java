package org.tcshare.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 获取手机信息
 */
public class PhoneInfo {
    private static final String TAG = PhoneInfo.class.getSimpleName();
    private static final String FILE_MEMORY = "/proc/meminfo";
    private static final String FILE_CPU = "/proc/cpuinfo";
    public String mIMEI;
    public int mPhoneType;
    public int mSysVersion;
    public String mNetWorkCountryIso;
    public String mNetWorkOperator;
    public String mNetWorkOperatorName;
    public int mNetWorkType;
    public boolean mIsOnLine;
    public String mConnectTypeName;
    public long mFreeMem;
    public long mTotalMem;
    public String mCupInfo;
    public String mProductName;
    public String mModelName;
    public String mManufacturerName;

    private PhoneInfo() {

    }


    public static String getIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        // check if has the permission
        if (PackageManager.PERMISSION_GRANTED == context.getPackageManager()
                .checkPermission(Manifest.permission.READ_PHONE_STATE,
                        context.getPackageName())) {
            return manager.getDeviceId();
        } else {
            return null;
        }
    }


    public static int getPhoneType(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getPhoneType();
    }


    public static int getSysVersion() {
        return Build.VERSION.SDK_INT;
    }


    public static String getNetWorkCountryIso(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkCountryIso();
    }


    public static String getNetWorkOperator(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkOperator();
    }


    public static String getNetWorkOperatorName(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }


    public static int getNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkType();
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }


    public static String getConnectTypeName(Context context) {
        if (!isOnline(context)) {
            return "OFFLINE";
        }
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.getTypeName();
        } else {
            return "OFFLINE";
        }
    }


    public static long getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        long free = info.availMem / 1024 / 1024;
        return free;
    }


    public static long getTotalMem(Context context) {
        try {
            FileReader fr = new FileReader(FILE_MEMORY);
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split("\\s+");
            Log.w(TAG, text);
            return Long.valueOf(array[1]) / 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getCpuInfo() {
        try {
            FileReader fr = new FileReader(FILE_CPU);
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
                Log.w(TAG, " .....  " + array[i]);
            }
            Log.w(TAG, text);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getProductName() {
        return Build.PRODUCT;
    }


    public static String getModelName() {
        return Build.MODEL;
    }


    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    public static PhoneInfo getPhoneInfo(Context context) {
        PhoneInfo result = new PhoneInfo();
        result.mIMEI = getIMEI(context);
        result.mPhoneType = getPhoneType(context);
        result.mSysVersion = getSysVersion();
        result.mNetWorkCountryIso = getNetWorkCountryIso(context);
        result.mNetWorkOperator = getNetWorkOperator(context);
        result.mNetWorkOperatorName = getNetWorkOperatorName(context);
        result.mNetWorkType = getNetworkType(context);
        result.mIsOnLine = isOnline(context);
        result.mConnectTypeName = getConnectTypeName(context);
        result.mFreeMem = getFreeMem(context);
        result.mTotalMem = getTotalMem(context);
        result.mCupInfo = getCpuInfo();
        result.mProductName = getProductName();
        result.mModelName = getModelName();
        result.mManufacturerName = getManufacturerName();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("IMEI : "+mIMEI+"\n");
        builder.append("mPhoneType : "+mPhoneType+"\n");
        builder.append("mSysVersion : "+mSysVersion+"\n");
        builder.append("mNetWorkCountryIso : "+mNetWorkCountryIso+"\n");
        builder.append("mNetWorkOperator : "+mNetWorkOperator+"\n");
        builder.append("mNetWorkOperatorName : "+mNetWorkOperatorName+"\n");
        builder.append("mNetWorkType : "+mNetWorkType+"\n");
        builder.append("mIsOnLine : "+mIsOnLine+"\n");
        builder.append("mConnectTypeName : "+mConnectTypeName+"\n");
        builder.append("mFreeMem : "+mFreeMem+"M\n");
        builder.append("mTotalMem : "+mTotalMem+"M\n");
        builder.append("mCupInfo : "+mCupInfo+"\n");
        builder.append("mProductName : "+mProductName+"\n");
        builder.append("mModelName : "+mModelName+"\n");
        builder.append("mManufacturerName : "+mManufacturerName+"\n");
        return builder.toString();
    }

}
package org.tcshare.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.tcshare.app.App;

/**
 * Created by FallRain on 2017/5/19.
 * 配置文件本地sp存储
 */

public class SettingsSPUtils {
    private static final String SETTING = "setting";
    private static final String APP_KEY = "app_key";
    private static final String APP_CERT = "app_cert";
    private static final SharedPreferences sp = App.ctx.getSharedPreferences(SETTING, Context.MODE_PRIVATE);


    public static String getAppkey() {
        return sp.getString(APP_KEY, "");
    }

    public static void setAppkey(String appkey) {
        sp.edit().putString(APP_KEY, appkey).apply();
    }

    public static String getAppSert() {
        return sp.getString(APP_CERT, "");
    }

    public static void setAppSert(String appSert) {
        sp.edit().putString(APP_CERT, appSert).apply();
    }

    /**
     * 为避免过度封装，调用时指定class
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public static <T>  T getObject(String key, Class<T> type) {
        T result = null;
        try {
            String objStr = sp.getString(key, "{}");
            result = new Gson().fromJson(objStr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveObject(String key, Object obj) {
        Gson gson = new Gson();
        String str = gson.toJson(obj);
        sp.edit().putString(key, str).apply();
    }
}

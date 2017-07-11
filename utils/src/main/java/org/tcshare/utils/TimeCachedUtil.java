package org.tcshare.utils;

import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FallRain on 2017/5/23.
 * 初始化的时候回阻塞线程
 */

public class TimeCachedUtil {
    protected static final String TAG = TimeCachedUtil.class.getSimpleName();
    private static TimeCachedUtil instance;
    protected Map<String, ArrayList<String>> dicMap = new HashMap<>();
    protected final String mapFile = "mapfile";

    private TimeCachedUtil() {
        // 为支持多线程，操作同一个文件，单例化。
    }
    public static TimeCachedUtil getInstance(){
        if(instance == null){
            instance = new TimeCachedUtil();
        }
        return instance;
    }

    public void init(File cacheDir){
        readDicMap(new File(cacheDir, mapFile));
    }

    protected void readDicMap(File file) {
        LineNumberReader reader;
        try {
            reader = new LineNumberReader(new FileReader(file));
            String line = null;
            dicMap.clear();
            while ((line = reader.readLine()) != null) {
                try {
                    final String[] fields = line.split(",", 4);
                    // URL, UUID, 缓存计时开始时间,  超时时间 ms
                    if (validCache(Long.parseLong(fields[2]), Integer.parseInt(fields[3]))) {
                        dicMap.put(fields[0], new ArrayList<String>(){
                            {
                                add(fields[1]);
                                add(fields[2]);
                                add(fields[3]);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.w(TAG, "invalid line");
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getCachedContent(String urlKey){
        ArrayList<String> info = dicMap.get(urlKey);
        if(info != null){

        }
        return null;
    }

    protected boolean validCache(long startDate, int duration) {
        // calendar 本地化，避免使用 synchronized， 关于性能平衡性有待测试
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        calendar.add(Calendar.MILLISECOND, duration);

        return Calendar.getInstance().compareTo(calendar) <= 0 ;
    }
}

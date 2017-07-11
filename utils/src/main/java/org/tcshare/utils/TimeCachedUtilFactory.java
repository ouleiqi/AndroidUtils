package org.tcshare.utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by FallRain on 2017/5/23.
 */

public class TimeCachedUtilFactory {
    private static TimeCachedUtilFactory instance;

    public static TimeCachedUtilFactory getInstance(){
        if(instance == null){
            instance = new TimeCachedUtilFactory();
        }
        return instance;
    }
    public TimeCachedUtil getInternalCache(Context ctx){
        return TimeCachedUtil.getInstance();
    }
}

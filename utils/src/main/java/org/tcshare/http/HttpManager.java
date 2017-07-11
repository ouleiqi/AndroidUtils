package org.tcshare.http;

/**
 * Created by FallRain on 2017/5/27.
 */

public class HttpManager {
    private static HttpManager instance;
    public static HttpManager getInstance(){
        if(instance == null){
            synchronized (HttpManager.class) {
                instance = new HttpManager();
            }
        }
        return instance;
    }
}

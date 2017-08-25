package org.tcshare.app.network;

import org.tcshare.app.utils.SettingsSPUtils;
import org.tcshare.utils.MD5;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by FallRain on 2017/8/21.
 */

public class FosungNet {

    public static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Map<String, String> initMap(String method){
        Map<String, String> map = new TreeMap<>();
        map.put("method", method);
        map.put("timestamp",mFormatter.format(new Date()));
        map.put("timezone","Asia/Shanghai");
        map.put("format", "json");
        map.put("appkey", SettingsSPUtils.getAppkey());
        map.put("ver", "1.0");
        map.put("sign_method", "md5");
        return map;
    }

    /**
     *
     * @param mp
     * @return
     */
    public static Map<String, String> signMap(Map<String, String>  mp){
        StringBuilder buffer = new StringBuilder();
        buffer.append(SettingsSPUtils.getAppSert());
        Set<String> keySet = mp.keySet();
        for (String key : keySet) {
            buffer.append(key).append(mp.get(key));
        }
        buffer.append(SettingsSPUtils.getAppSert());
        String info= MD5.toMd5(buffer.toString().getBytes());
        mp.put("sign",info);
        return mp;
    }
}

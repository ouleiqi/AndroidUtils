package org.tcshare.network.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * <pre>
 *     OkHttpClient client = new OkHttpClient.Builder()
 *             .cookieJar(new JavaNetCookieJar(new CookieManager(
 *                     new PersistentCookieStore(getApplicationContext()),
 *                             CookiePolicy.ACCEPT_ALL))
 *             .build();
 *
 * </pre>
 * <p>
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 * <p>
 *
 * A persistent cookie store which implements the Apache HttpClient CookieStore interface.
 * Cookies are stored and will persist on the user's device between application sessions since they
 * are serialized and stored in SharedPreferences. Instances of this class are
 * designed to be used with AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
public class PersistentCookieStore implements CookieStore {

    private static final String LOG_TAG            = "PersistentCookieStore";
    private static final String COOKIE_PREFS       = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        restoreAll();
    }

    protected String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null)
            return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }

        return cookie;
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString()
                 .toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

//--------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    protected HashMap<String, List<Cookie>> allCookies = new HashMap<>();
    protected SharedPreferences      cookiePrefs;

    public  void saveAll(){
        SharedPreferences.Editor edit = cookiePrefs.edit();
        for(Map.Entry<String,List<Cookie>> entry : allCookies.entrySet()){
            String host = entry.getKey();
            StringBuilder sb = new StringBuilder();
            for(Cookie cookie : entry.getValue()) {
                sb.append(encodeCookie(new SerializableHttpCookie(cookie))).append(",");
            }
            sb.substring(0, sb.length() - ",".length());
            edit.putString(host, sb.toString());
        }
        edit.apply();
    }
    public void restoreAll(){
        Map<String,String> map = (Map<String, String>) cookiePrefs.getAll();
        for(Map.Entry<String,String> entry : map.entrySet()){
            List<Cookie> cookies = new ArrayList<>();
            if(entry.getValue() != null){
                List<String> cookieStr = Arrays.asList(entry.getValue().split(","));
                for(String cStr : cookieStr){
                    cookies.add(decodeCookie(cStr));
                }
            }
            allCookies.put(entry.getKey(), cookies);
        }
    }

    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = allCookies.get(url.host());

        if (oldCookies != null) {
            Iterator<Cookie> itNew = cookies.iterator();
            Iterator<Cookie> itOld = oldCookies.iterator();
            while (itNew.hasNext()) {
                String va = itNew.next() .name();
                while (va != null && itOld.hasNext()) {
                    String v = itOld.next() .name();
                    if (v != null && va.equals(v)) {
                        itOld.remove();
                    }
                }
            }
            oldCookies.addAll(cookies);
        } else {
            allCookies.put(url.host(), cookies);
        }
        saveAll();
    }

    @Override
    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = allCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(uri.host(), cookies);
        }
        return cookies;

    }

    @Override
    public boolean removeAll() {
        allCookies.clear();
        saveAll();
        return true;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = allCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(allCookies.get(url));
        }
        return cookies;
    }


    @Override
    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(uri.host());
        if (cookie != null) {
            boolean removed = cookies.remove(cookie);
            if(removed) saveAll();
            return removed;
        }
        return false;
    }

}
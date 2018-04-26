package org.tcshare.network;

import android.content.Context;

import org.tcshare.app.BuildConfig;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FallRain on 2017/11/2.
 */

public abstract class ResponseString extends AResponse<String> {

    public ResponseString(){
        super();
    }

    public ResponseString(Context ctx){
        super(ctx);
    }

    @Override
    protected String processResponce(Call call, Response response) {
        String result = null;
        try {
            result = response.body().string();
        } catch (Exception e){
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
        return result;
    }

}
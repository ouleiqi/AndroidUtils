package org.tcshare.network;

import org.tcshare.Constant;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FallRain on 2017/11/2.
 */

public abstract class ResponseString extends HttpResponse<String> {

    @Override
    protected String processResponce(Call call, Response response) {
        String result = null;
        try {
            result = response.body().string();
        } catch (Exception e){
            if(Constant.DEBUG) e.printStackTrace();
        }
        return result;
    }

}
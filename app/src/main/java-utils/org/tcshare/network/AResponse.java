package org.tcshare.network;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.tcshare.app.BuildConfig;
import org.tcshare.app.R;
import org.tcshare.dialog.LoadingDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by FallRain on 2017/11/2.
 */

public abstract class AResponse<T> implements Callback {
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected       Context ctx;
    private Dialog  loadingDialog;

    public AResponse(){

    }
    public AResponse(Context ctx){
        this.ctx = ctx;
        if(ctx != null) {
            loadingDialog = LoadingDialog.createLoadingDialog(ctx, ctx.getString(R.string.loading));
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if(BuildConfig.DEBUG) e.printStackTrace();
        nextUI(call, e);
    }

    @Override
    public void onResponse(Call call, okhttp3.Response response) throws IOException {
        T obj = processResponce(call, response);
        nextUI(call, obj);
    }

    protected abstract T processResponce(Call call, okhttp3.Response response);

    private void nextUI(final Call call, final T body) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onResponseUI(call, body);
                onFinished();
            }
        });
    }

    protected void nextUI(final Call call, final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailureUI(call, e);
                onFinished();
            }
        });
    }



    public void onFailureUI(Call call, IOException e) {

    }

    public abstract void onResponseUI(Call call, T processObj);

    public void onFinished(){
        if(loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void onStart() {
        if(loadingDialog != null) {
            loadingDialog.show();
        }
    }
}
package org.tcshare.app.network;

import org.tcshare.app.beans.FosungDeviceRegBean;
import org.tcshare.network.HttpApi;
import org.tcshare.network.ResponseJSON;
import org.tcshare.network.RequestBuilderFactory;

import java.util.Map;
import java.util.Observable;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by FallRain on 2017/7/7.
 */

public class FosungDeviceRegPresenter extends Observable {

    public void getKey(Map<String, String> info) {
        Map<String, String> map = FosungNet.initMap("xiangcun.register");
        map = FosungNet.signMap(map);
        map.putAll(info);
        Request request = RequestBuilderFactory.createPostRequestBuilder("",map).build();
        HttpApi.sendRequest(request, new ResponseJSON<FosungDeviceRegBean>(){

            @Override
            public void onResponseUI(Call call, FosungDeviceRegBean processObj) {
                setChanged();
                notifyObservers(processObj);
            }
        });
        HttpApi.post("", map, new ResponseJSON<ApiService>() {
            @Override
            public void onResponseUI(Call call, ApiService processObj) {

            }
        });
    }
}

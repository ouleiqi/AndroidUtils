package org.tcshare.app.beans;

/**
 * Created by FallRain on 2017/7/7.
 */

public class FosungDeviceRegBean {

    /**
     * errorcode : 1
     * error : 设备注册成功
     * data : {"appkey":"17070711444700000004","appcert":"de444d44d9ede73ae2df6a257be0c908"}
     */

    private int errorcode;
    private String error;
    private DataBean data;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appkey : 17070711444700000004
         * appcert : de444d44d9ede73ae2df6a257be0c908
         */

        private String appkey;
        private String appcert;

        public String getAppkey() {
            return appkey;
        }

        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public String getAppcert() {
            return appcert;
        }

        public void setAppcert(String appcert) {
            this.appcert = appcert;
        }
    }
}

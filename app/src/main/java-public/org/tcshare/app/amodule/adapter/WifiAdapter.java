package org.tcshare.app.amodule.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tcshare.app.R;
import org.tcshare.utils.ToastUtil;
import org.tcshare.utils.WiFiUtil;

import java.util.List;

/**
 * Created by Jerry.Zou
 */
public class WifiAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ScanResult> datas;
    private WifiManager wifiManager;
    private WiFiUtil wiFiUtil;

    public WifiAdapter(Context context, List<ScanResult> datas, WifiManager wifiManager) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.datas = datas;
        this.wifiManager = wifiManager;
        wiFiUtil = WiFiUtil.getInstance(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ScanResult getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.wifi_list_item, parent, false);
        }
        TextView mName = (TextView) convertView.findViewById(R.id.name);
        mName.setText(datas.get(position).SSID);

        TextView mAddress = (TextView) convertView.findViewById(R.id.address);
        mAddress.setText(datas.get(position).BSSID);

        Button connBtn = (Button) convertView.findViewById(R.id.connBtn);

        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScanResult item = getItem(position);

                final Context ctx = v.getContext();
                final EditText inputServer = new EditText(ctx);
                inputServer.setFocusable(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("请输入WIFI密码").setView(inputServer).setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String passWord = inputServer.getText().toString();
                        int result = -1;
                        if (item.capabilities.contains("WPA2") || item.capabilities.contains("WPA-PSK")) {
                            result = wiFiUtil.addWiFiNetwork(item.SSID, passWord, WiFiUtil.Data.WIFI_CIPHER_WPA2);
                        } else if (item.capabilities.contains("WPA")) {
                            result = wiFiUtil.addWiFiNetwork(item.SSID, passWord, WiFiUtil.Data.WIFI_CIPHER_WPA);
                        } else if (item.capabilities.contains("WEP")) {
                            /* WIFICIPHER_WEP 加密 */
                            result = wiFiUtil.addWiFiNetwork(item.SSID, passWord, WiFiUtil.Data.WIFI_CIPHER_WEP);
                        } else {
                            /* WIFICIPHER_OPEN NOPASSWORD 开放无加密 */
                            result = wiFiUtil.addWiFiNetwork(item.SSID, "", WiFiUtil.Data.WIFI_CIPHER_NOPASS);
                        }
                        ToastUtil.showToastLong(ctx, result == -1 ? "网络连接失败！" : "网络连接成功！");
                    }
                });
                builder.show();


            }
        });

        return convertView;
    }
}

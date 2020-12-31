package org.tcshare.app.amodule.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.tcshare.app.R;
import org.tcshare.app.amodule.fragment.EmptyFragment;
import org.tcshare.app.amodule.fragment.RVListFragment;
import org.tcshare.fragment.WebViewFragment;
import org.tcshare.permission.PermissionHelper;
import org.tcshare.utils.RS485SerialPortUtilNew;
import org.tcshare.utils.RandomUtils;
import org.tcshare.utils.ToastUtil;
import org.tcshare.widgets.BottomListDialog;
import org.tcshare.widgets.RadarView;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;


public class TCMainActivity extends AppCompatActivity {

    private EditText serialPortText;
    private EditText serialPortBand;
    private RadarView radarView;

    private void showBottomListDialog() {
        BottomListDialog.showSimpleDialog(TCMainActivity.this, new BottomListDialog.OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, BottomSheetDialog dialog) {
                Toast.makeText(TCMainActivity.this, "You clicked pos is "+ pos + " and view id is " + view.getId(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }, new String[]{"Group 1 Item 0","Group 1 Item 1","Group 1 Item 2",}, new String[]{"Group 2 Item 0","Group 2 Item 1","Group 2 Item 2",});
    }


    private void requestPermissionMethod() {
        // 一次请求单个或多个权限,
        // 1. 申请的权限必须先在manifest中配置， 否则申请结果总是失败
        // 2. ACCESS_FINE_LOCATION 权限包含 ACCESS_COARSE_LOCATION 粗略定位权限
        // 3. 按照权限请求顺序进行申请，遇到失败则返回，之后权限则处于未申请状态: -1
        String permissions[] = new String[]{ Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};
        int requestCode = 7777;
        PermissionHelper.request(TCMainActivity.this, permissions, requestCode,new PermissionHelper.Callback(){

            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                if(PackageManager.PERMISSION_GRANTED == grantResult[0]){
                    //android.permission.CAMERA 授权成功
                }
                // 授权结果
                Toast.makeText(TCMainActivity.this, "请求权限：" +Arrays.toString(permissions) + "  授权结果:" + Arrays.toString(grantResult), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc_main);
        serialPortText = findViewById(R.id.serialPortText);
        serialPortBand = findViewById(R.id.serialPortBand);

        radarView = findViewById(R.id.radarView);
        radarView.setSearching(true);
        addPoint();

    }
    private void addPoint(){
        radarView.postDelayed(new Runnable() {
            @Override
            public void run() {
                radarView.addPoint(RandomUtils.getRandomFloat(1));
                addPoint();
            }
        }, 50);

    }

    public void onItemClick(View view){
        int id = view.getId();
        if (id == R.id.qrCode) {
        } else if (id == R.id.bottomDialog) {//底部对话框
            showBottomListDialog();
        } else if (id == R.id.dragExit) {
            startActivity(new Intent(TCMainActivity.this, TCDrag2RightExitActivity.class));
        } else if (id == R.id.fragmentContainer) {
            TCContainerActivity.openSelf(TCMainActivity.this, EmptyFragment.class, "Fragment Container");
        } else if (id == R.id.recyclerView) {
            TCContainerActivity.openSelf(TCMainActivity.this, RVListFragment.class, "recyclerView默认样式");
        } else if (id == R.id.recyclerView1) {
        } else if (id == R.id.permission) {// 请求权限
            requestPermissionMethod();
        } else if (id == R.id.faceManager) {
            ToastUtil.showToastLong(this, "已移除");
        } else if (id == R.id.facePlus) {
            ToastUtil.showToastLong(this, "已移除");
        } else if (id == R.id.facePlusRGB) {
            ToastUtil.showToastLong(this, "已移除");
        } else if (id == R.id.ttsPlay) {
            ToastUtil.showToastLong(this, "已移除");
        } else if (id == R.id.livelike) {
            startActivity(new Intent(TCMainActivity.this, TCLiveLikeActivity.class));
        } else if (id == R.id.jsBridge) {
            Bundle bundle = new Bundle();
            bundle.putString("url", "http://www.imiduoduo.com");
            TCContainerActivity.openSelf(TCMainActivity.this, WebViewFragment.class, bundle);
        } else if (id == R.id.btnSerialPort) {
            String port = serialPortText.getText().toString().trim();
            String band = serialPortBand.getText().toString().trim();
            if ("".equals(port) || "".equals(band)) {
                Toast.makeText(this, "串口及波特率不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(this, TCSerialPortActivity.class);
            intent.putExtra("port", port);
            intent.putExtra("band", band);
            startActivity(intent);
        }
    }

}

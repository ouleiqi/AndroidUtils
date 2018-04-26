package org.tcshare.app.amodule.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.tcshare.app.R;
import org.tcshare.app.amodule.fragment.EmptyFragment;
import org.tcshare.app.amodule.fragment.RVListFragment;
import org.tcshare.app.zxing.CaptureActivity;
import org.tcshare.permission.PermissionHelper;
import org.tcshare.widgets.BottomListDialog;

import java.security.Permission;
import java.security.Permissions;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity{
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.qrCode:
                    startActivity(new Intent(MainActivity.this, CaptureActivity.class));
                    break;
                case R.id.bottomDialog:
                    //底部对话框
                    showBottomListDialog();
                    break;
                case R.id.dragExit:
                    startActivity(new Intent(MainActivity.this, Drag2RightExitActivity.class));
                    break;
                case R.id.fragmentContainer:
                    ContainerActivity.openSelf(MainActivity.this, EmptyFragment.class, "Fragment Container");
                    break;
                case R.id.recyclerView:
                    ContainerActivity.openSelf(MainActivity.this, RVListFragment.class, "recyclerView默认样式");
                    break;
                case R.id.recyclerView1:
                    break;
                case R.id.permission:
                    // 请求权限
                    requestPermissionMethod();
                    break;

            }
        }
    };

    private void showBottomListDialog() {
        BottomListDialog.showSimpleDialog(MainActivity.this, new BottomListDialog.OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, BottomSheetDialog dialog) {
                Toast.makeText(MainActivity.this, "You clicked pos is "+ pos + " and view id is " + view.getId(), Toast.LENGTH_SHORT).show();
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
        PermissionHelper.request(MainActivity.this, permissions, requestCode,new PermissionHelper.Callback(){

            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                if(PackageManager.PERMISSION_GRANTED == grantResult[0]){
                    //android.permission.CAMERA 授权成功
                }
                // 授权结果
                Toast.makeText(MainActivity.this, "请求权限：" +Arrays.toString(permissions) + "  授权结果:" + Arrays.toString(grantResult), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.qrCode).setOnClickListener(listener);
        findViewById(R.id.bottomDialog).setOnClickListener(listener);
        findViewById(R.id.dragExit).setOnClickListener(listener);
        findViewById(R.id.recyclerView).setOnClickListener(listener);
        findViewById(R.id.fragmentContainer).setOnClickListener(listener);
        findViewById(R.id.recyclerView1).setOnClickListener(listener);
        findViewById(R.id.permission).setOnClickListener(listener);
    }

}

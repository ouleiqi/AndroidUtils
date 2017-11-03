package org.tcshare.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.tcshare.app.R;
import org.tcshare.app.beans.FosungDeviceRegBean;
import org.tcshare.app.entity.TabEntity;
import org.tcshare.app.network.FosungNet;
import org.tcshare.fragment.WebViewFragment;
import org.tcshare.network.HttpApi;
import org.tcshare.network.ResponseString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import okhttp3.Call;

public class NormalActivity extends AppCompatActivity implements Observer {
    private long exitTime;
    private Fragment mTempFragment;
    private SparseArray<Fragment> cachedFragment = new SparseArray<>();
    private ArrayList<CustomTabEntity> menus = new ArrayList<CustomTabEntity>(4) {
        {
            add(new TabEntity("首页", R.mipmap.menu1_press, R.mipmap.menu1));
            add(new TabEntity("公告", R.mipmap.menu1_press, R.mipmap.menu1));
            add(new TabEntity("资讯", R.mipmap.menu1_press, R.mipmap.menu1));
            add(new TabEntity("我的", R.mipmap.menu1_press, R.mipmap.menu1));
        }
    };
    private OnTabSelectListener onTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelect(int position) {
            Fragment frag = cachedFragment.get(position);
            if (frag == null) {
                switch (position) {
                    case 0:
                        frag = WebViewFragment.newInstance("", "");
                        break;
                    case 1:
                        frag = WebViewFragment.newInstance("", "");
                        break;
                    case 2:
                        frag = WebViewFragment.newInstance("", "");
                        break;
                    case 3:
                    default:
                        frag = WebViewFragment.newInstance("", "");
                        break;
                }

                cachedFragment.put(position, frag);
            }

            switchFragment(frag);
        }

        @Override
        public void onTabReselect(int position) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);


        CommonTabLayout tabLayout = (CommonTabLayout) findViewById(R.id.tabTitle);
        tabLayout.setTabData(menus);
        tabLayout.setOnTabSelectListener(onTabSelectListener);

        tabLayout.setCurrentTab(0);
        switchFragment(WebViewFragment.newInstance("", ""));
  /*      PermissionHelper.request(this, new String[]{Manifest.permission.CAMERA}, 10, new PermissionHelper.Callbac() {
            @Override
            public void onResult(int requestCode, String[] permissions, int[] grantResult) {
                if(grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(NormalActivity.this, CaptureActivity.class));
                }
            }
        });*/

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> map = FosungNet.initMap("fosung.app.register");
                map = FosungNet.signMap(map);
                map.put("brand", "aaa");
                map.put("model", "aaa");
                map.put("screen_width", "");
                map.put("screen_height", "");
                map.put("serial_no", "");
                map.put("os_name", "");
                map.put("os_ver", "");
                map.put("app_ver", "");
                map.put("ios_type", "0");
                FosungDeviceRegBean obj = new FosungDeviceRegBean();
                obj.setError("sdfsdfsfsd");
                obj.setErrorcode(989889);
                FosungDeviceRegBean.DataBean dataBean = new FosungDeviceRegBean.DataBean();
                dataBean.setAppcert("llllllkkkkkkkkkkkkkk");
                obj.setData(dataBean);
                String url = "http://192.168.102.195/rest.php";
                HttpApi.postJSON(url, obj, new ResponseString() {
                    @Override
                    public void onResponseUI(Call call, String processObj) {
                        Log.e("tmp", "Post JSON onResponseUI:" + processObj);
                    }
                });
              /*  HttpApi.post(url, obj, new ResponseString() {

                    @Override
                    public void onResponseUI(Call call, String processObj) {
                        Log.e("tmp", "Post onResponseUI:" + processObj);
                    }
                });
                HttpApi.get(url, obj, new ResponseString() {

                    @Override
                    public void onResponseUI(Call call, String processObj) {
                        Log.e("tmp", "get onResponseUI:" + processObj);
                    }
                });*/
            }
        });

    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.press_back_again_exit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        //super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update(Observable o, Object result) {

    }


    private void switchFragment(Fragment fragment) {
        if (fragment != mTempFragment) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            if (mTempFragment != null) {
                trans.hide(mTempFragment);
            }
            if (!fragment.isAdded()) {
                trans.add(R.id.container, fragment);
            } else {
                trans.show(fragment);
            }
            trans.commit();
            mTempFragment = fragment;
        }


    }
}

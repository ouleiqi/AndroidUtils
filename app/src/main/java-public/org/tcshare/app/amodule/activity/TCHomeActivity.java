package org.tcshare.app.amodule.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.tcshare.app.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TCHomeActivity extends AppCompatActivity {
    private int tabs[] = new int[]{R.id.ic_back};
    private long exitTime = 0;
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment mTempFragment;
    private View.OnClickListener tabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeSelect(view.getId());
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc_home);
        initTabs();
       // changeSelect(R.id.tab_home);
    }
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_back_again_exit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    private void initTabs() {
        for (int tab : tabs) {
            findViewById(tab).setOnClickListener(tabClickListener);
        }
    }
    private void changeSelect(int id) {
        Fragment frag = fragmentMap.get(id);
        if (frag == null) {
            if (id == tabs[0]) {
            }
            fragmentMap.put(id, frag);
        }
        switchFragment(id, frag);
    }
    private void switchFragment(int id, Fragment fragment) {
        for(int tab : tabs){
            findViewById(tab).setSelected(id == tab);
        }

        if (fragment == null) return;

        if (fragment != mTempFragment) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            if (mTempFragment != null) {
                trans.hide(mTempFragment);
            }
            if (!fragment.isAdded() && null == getSupportFragmentManager().findFragmentByTag(id + "")) {
                trans.add(R.id.container, fragment, id + "");
            } else {
                trans.show(fragment);
            }
            trans.commitAllowingStateLoss();
            mTempFragment = fragment;
        }
        getSupportFragmentManager().executePendingTransactions();


    }
}

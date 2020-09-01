package org.tcshare.app.amodule.activity;

import android.os.Bundle;
import android.view.MenuItem;

import org.tcshare.app.R;
import org.tcshare.widgets.DragLeft2RightExitFrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TCDrag2RightExitActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tc_drag2rightexit);

        // 向右拖拽退出activity
        DragLeft2RightExitFrameLayout dragExit = findViewById(R.id.dragExit);
        dragExit.setDragExitListner(new DragLeft2RightExitFrameLayout.DragExitListner() {
            @Override
            public void onExit() {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        // 1. 设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // 2. toolbar 点击了返回监听
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_example_menu, menu);
        // 搜索
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();


        // 分享
        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        // 分享intent示例
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.putExtra(Intent.EXTRA_TEXT,"http://tcshare.org");
        myShareIntent.setType("text/plain");
  */
/*      myShareIntent.setType("image*//*
*/
/*");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, R.mipmap.ic_launcher);*//*

        myShareActionProvider.setShareIntent(myShareIntent);
        return true;
    }
*/


}

package org.tcshare.app.amodule.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.tcshare.app.R;
import org.tcshare.widgets.FavorLayout;

import java.util.ArrayList;
import java.util.List;

public class LiveLikeActivity extends AppCompatActivity {

    private FavorLayout favor;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                handler.sendEmptyMessageDelayed(1, 100);
                favor.addFavor();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_like);

        favor = findViewById(R.id.flavorLayout);
        List<Drawable> items = new ArrayList<Drawable>(){
            {
                add(getResources().getDrawable(R.mipmap.gift_10));
                add(getResources().getDrawable(R.mipmap.gift_1));
                add(getResources().getDrawable(R.mipmap.gift_2));
                add(getResources().getDrawable(R.mipmap.gift_3));
                add(getResources().getDrawable(R.mipmap.gift_4));
                add(getResources().getDrawable(R.mipmap.gift_5));
                add(getResources().getDrawable(R.mipmap.gift_6));
                add(getResources().getDrawable(R.mipmap.gift_7));
                add(getResources().getDrawable(R.mipmap.gift_8));
                add(getResources().getDrawable(R.mipmap.gift_9));
                add(getResources().getDrawable(R.mipmap.gift_11));
            }
        };
        // 使用自定义的效果图标
        favor.setFavors(items);
        // 设置效果图标，左右飘动的范围，以及终止点的范围
        favor.setFavorWidthHeight(100, 400);
        // 设置AncherView，效果图标会从该AncherView的中心点飘出
        favor.setAncher(findViewById(R.id.ancher));


        // 自动添加效果图标
        handler.sendEmptyMessageDelayed(1, 200);
    }
}

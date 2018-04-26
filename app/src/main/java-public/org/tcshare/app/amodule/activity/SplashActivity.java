package org.tcshare.app.amodule.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.tcshare.app.R;

import java.util.Observable;
import java.util.Observer;


public class SplashActivity extends AppCompatActivity implements Observer {
    private static final int DISMISS_SPLASH = 1000;
    private static final int SCALE_SPLASH = 1001;
    private static final int END_SPLASH = 1002;
    private float scale = 1.0f;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null) return;
            if (msg.what == DISMISS_SPLASH) {
                handler.sendEmptyMessageDelayed(SCALE_SPLASH, 5);
            } else if (msg.what == SCALE_SPLASH) {
                scale += 0.001f;
                splash.setScaleX(scale);
                splash.setScaleY(scale);
                splash.setAlpha(2 - scale);
                if (scale > 1.15f) {
                    handler.removeMessages(SCALE_SPLASH);
                    //splash.setVisibility(View.GONE);
                    handler.sendEmptyMessage(END_SPLASH);
                } else {
                    handler.sendEmptyMessageDelayed(SCALE_SPLASH, 5);
                }
            } else if (msg.what == END_SPLASH) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    private View splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash = findViewById(R.id.splash);
        handler.sendEmptyMessageDelayed(END_SPLASH, 2000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void update(Observable observable, Object o) {

    }
}

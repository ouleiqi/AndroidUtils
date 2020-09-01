package org.tcshare.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import org.tcshare.app.BuildConfig;
import org.tcshare.app.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 优先加载 url ，没有url 则加载 content
 */
public class WebViewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = WebViewFragment.class.getSimpleName();
    protected WebView mWebView;
    protected String url, content;
    private View loading;
    private View retry;

    public static WebViewFragment newInstance(String url, String content) {
        WebViewFragment frag = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("content", content);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            content = bundle.getString("content");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = (WebView) view.findViewById(R.id.webView);
        loading = view.findViewById(R.id.loading);
        retry = view.findViewById(R.id.retry);
        retry.setOnClickListener(this);
        initSettings(mWebView);

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // 屏蔽长按事件
                return true;
            }
        });

        mWebView.setHorizontalScrollBarEnabled(true);//水平不显示
        mWebView.setVerticalScrollBarEnabled(true); //垂直不显示
        mWebView.setDownloadListener(new MyWebViewDownLoadListener()); // 下载功能
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("tmp", "errorCode:" + errorCode + "-" + description + "-" + failingUrl);
                retry.setVisibility(View.VISIBLE);
                mWebView.loadData("", "text/html; charset=UTF-8", null);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if(BuildConfig.DEBUG) Log.d(TAG, "error:" + error);
                retry.setVisibility(View.VISIBLE);
                mWebView.loadData("", "text/html; charset=UTF-8", null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                // 防止重定向，启动网页
                return super.shouldOverrideUrlLoading(view, url);
            }

        });
        WebChromeClient wvcc = new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);


            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    mWebView.getSettings().setBlockNetworkImage(false);
                    loading.setVisibility(View.GONE);
                }
            }
        };
        // 设置setWebChromeClient对象
        mWebView.setWebChromeClient(wvcc);
        for(Map.Entry<String, Object> entry : jsScripts.entrySet()){
            mWebView.addJavascriptInterface(entry.getValue(), entry.getKey());
        }

        if (!TextUtils.isEmpty(url)) {
            mWebView.getSettings().setBlockNetworkImage(true);
            mWebView.loadUrl(url);
        } else if (!TextUtils.isEmpty(content)) {
            mWebView.loadData(content, "text/html; charset=UTF-8", null);
        }
        return view;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings(WebView mWebView) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置 缓存模式
        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本
        webSettings.setAllowFileAccess(true);   //设置可以访问文件
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);

        //标示 手机、pc
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.20 Mobile Safari/537.36");
        webSettings.setGeolocationEnabled(true); // 地理位置开启
        webSettings.setUseWideViewPort(true);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //NARROW_COLUMNS ：适应内容大小,  SINGLE_COLUMN : 适应屏幕，内容将自动缩放
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // mWebView.setInitialScale(100); // 初始时页面的缩放比例
        // webSettings.setDefaultFontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
    }
    protected Map<String, Object> jsScripts = new HashMap<>();
    public void addJavascriptInterface(Object obj, String name){
            jsScripts.put(name, obj);
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }


    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    public WebView getmWebView() {
        return mWebView;
    }

    public void clearSession() {
        CookieManager.getInstance().removeSessionCookie();
    }

    public void clearCacheAndHistory() {
        if (mWebView == null) return;
        mWebView.clearCache(true);
        mWebView.clearHistory();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.retry) {
            reLoad();
        }
    }

    private void reLoad() {
        loading.setVisibility(View.VISIBLE);
        retry.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(url)) {
            mWebView.getSettings().setBlockNetworkImage(true);
            mWebView.loadUrl(url);
        } else if (!TextUtils.isEmpty(content)) {
            mWebView.loadData(content, "text/html; charset=UTF-8", null);
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

}
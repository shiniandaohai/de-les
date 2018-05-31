package com.boer.delos.activity.settings;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;

/**
 * @author PengJiYang
 * @Description: "关于家卫士"界面
 * create at 2016/4/6 09:58
 */
public class AboutJiaListeningActivity extends BaseListeningActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_jia);

        initTopBar("关于我们", null, true, false);
        mWebView = (WebView) findViewById(R.id.webview);


        loadingData();
    }

    private void loadingData() {
        String url = "http://h5.boericloud.com:18080/delos/ios/about/index.html";//http://h5.boericloud.com:18080/function/about.html";
        mWebView.loadUrl(url);

        mWebView.getSettings().setJavaScriptEnabled(true);// 开启jacascript
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);// 支持自动加载图片
        mWebView.getSettings().setCacheMode(mWebView.getSettings().LOAD_NO_CACHE);
        mWebView.requestFocusFromTouch();
        mWebView.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页，如果没有这个在低分辨率手机上显示会异常
        //不显示webview缩放按钮
        mWebView.getSettings().setDisplayZoomControls(false);
    }


}

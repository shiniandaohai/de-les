package com.boer.delos.activity.login;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;


/**
 * @author wangkai
 * @Description: 用户协议
 * create at 2015/11/19 9:25
 */
public class RegisterAgreementListeningActivity extends BaseListeningActivity {


    private WebView wvAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
    }

    private void initView() {
        initTopBar(getString(R.string.title_Agreement), null, true, false);
        this.wvAgreement = (WebView) findViewById(R.id.wvAgreement);
        // WebSettings webSettings = this.wvAgreement.getSettings();
        this.wvAgreement.getSettings().setJavaScriptEnabled(true);// 开启jacascript
        this.wvAgreement.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        this.wvAgreement.getSettings().setSupportZoom(true);
        this.wvAgreement.getSettings().setLoadsImagesAutomatically(true);// 支持自动加载图片
        // 默认不加载缓存
        this.wvAgreement.getSettings().setCacheMode(this.wvAgreement.getSettings().LOAD_NO_CACHE);
        // this.wvAgreement.requestFocus();// 使WebView内的输入框等获得焦点
        this.wvAgreement.requestFocusFromTouch();
        this.wvAgreement.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
        this.wvAgreement.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页，如果没有这个在低分辨率手机上显示会异常

        String url = "http://h5.boericloud.com:18080/PrivacyPolicy/index.html";
        this.wvAgreement.loadUrl(url);

        //不显示webview缩放按钮
        wvAgreement.getSettings().setDisplayZoomControls(false);
    }


}

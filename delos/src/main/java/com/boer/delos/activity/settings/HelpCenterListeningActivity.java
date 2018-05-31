package com.boer.delos.activity.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.constant.Constant;

/**
 * hzj
 *
 * @Description: "帮助中心"界面
 * create at 2016/4/5 16:52
 */
public class HelpCenterListeningActivity extends BaseListeningActivity {

    private WebView mWebView;
    private TextView mtvErrorShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        mWebView = (WebView) findViewById(R.id.id_webView);
        mtvErrorShow = (TextView) findViewById(R.id.error_show);
        mtvErrorShow.setText("请连接因特网后重试");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //没外网直连
        if (!Constant.IS_INTERNET_CONN && Constant.IS_LOCAL_CONNECTION) {
            mtvErrorShow.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        } else {
            mWebView.setVisibility(View.VISIBLE);
            loadingHtml5();
        }
    }

    private void loadingHtml5() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                toastUtils.showProgress("加载网页中...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toastUtils.dismiss();
                mtvErrorShow.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mtvErrorShow.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
            }
        });

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.loadUrl("http://h5.boericloud.com:18080/help/iphone/index.html");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        mWebView.setOnKeyListener(new View.OnKeyListener() {        // webview can go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 自定义的Android代码和JavaScript代码之间的桥梁类
     *
     * @author 1
     */
    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void closeWindows() {
            finish();
        }
    }

}

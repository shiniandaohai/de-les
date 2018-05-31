package com.boer.delos.activity.main.adv;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.boer.delos.R;
import com.boer.delos.commen.LazyFragment;

/**
 * @author wangkai
 * @Description: 图片fragment
 * create at 2016/1/22 11:29
 */
public class SlidePageFragment extends LazyFragment {
    private static final String PIC_URL = "pic_url";
    WebView wvAgreement;

    public static SlidePageFragment newInstance(@NonNull final String picUrl) {
        Bundle arguments = new Bundle();
        arguments.putString(PIC_URL, picUrl);

        SlidePageFragment fragment = new SlidePageFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_slide_page, container, false);

        wvAgreement = (WebView) rootView.findViewById(R.id.wv);
        loadingH5Data();
        return rootView;
    }

    @Override
    protected void lazyLoad() {
        if (wvAgreement != null) {
            loadingH5Data();
        }
    }

    private void loadingH5Data() {
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

        final Bundle arguments = getArguments();
        if (arguments != null) {
            String url = arguments.getString(PIC_URL);
            wvAgreement.loadUrl(url);
        }

//        wvAgreement.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                ((BaseListeningActivity) getContext()).toastUtils.showProgress("加载数据中...");
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                ((BaseListeningActivity) getContext()).toastUtils.dismiss();
//            }
//
//        });
    }
}

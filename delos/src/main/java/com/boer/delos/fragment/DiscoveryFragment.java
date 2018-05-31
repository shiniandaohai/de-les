package com.boer.delos.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.TitleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DiscoveryFragment extends Fragment {
    WebView webContent;
    @Bind(R.id.tv_aboutline)
    TextView tvAboutline;
    @Bind(R.id.tv_sampleline)
    TextView tvSampleline;
    @Bind(R.id.tv_featureline)
    TextView tvFeatureline;
    @Bind(R.id.id_left)
    ImageView idLeft;
    private WebView websample, webfeature;
    public TitleLayout tlTitleLayout;
    int page = 0;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.img_bar)
    ImageView imgBar;
    @Bind(R.id.tv_about)
    TextView tvAbout;
    // 滚动条图片
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;
    private ToastUtils toastUtils;
    private List<View> viewList = new ArrayList<View>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        ButterKnife.bind(this, view);
        tlTitleLayout = (TitleLayout) view.findViewById(R.id.tl_title_layout);
        tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
        tlTitleLayout.setTitle(R.string.discovery_tab);


        initView();
        return view;
    }

    private void initView() {
        idLeft.setImageResource(R.mipmap.ic_nav_back);
        idLeft.setVisibility(View.INVISIBLE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view1 = inflater.inflate(R.layout.item_viewpager_discovery, null);
        View view2 = inflater.inflate(R.layout.item_viewpager_discovery, null);
        View view3 = inflater.inflate(R.layout.item_viewpager_discovery, null);
        webContent = (WebView) view1.findViewById(R.id.web_content);
        webfeature = (WebView) view2.findViewById(R.id.web_content);
        websample = (WebView) view3.findViewById(R.id.web_content);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        //适配器
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView(viewList.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        //添加切换界面的监听器
        viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.up).getWidth();
        Log.i("gwq", "w=" + bmpW);
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 3 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        imgBar.setImageMatrix(matrix);
        tvAboutline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));

        toastUtils = new ToastUtils(getActivity());
        loadingHtml5();
    }

    private void resetColor() {
        tvAboutline.setBackgroundColor(0);
        tvSampleline.setBackgroundColor(0);
        tvFeatureline.setBackgroundColor(0);
    }

    private void loadingHtml5() {
        webContent.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                toastUtils.showProgress("");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toastUtils.dismiss();

                if (webContent != null && webContent.canGoBack()) {
                    idLeft.setVisibility(View.VISIBLE);
                } else if(idLeft!=null){
                    idLeft.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                toastUtils.dismiss();

            }
        });


        tlTitleLayout.linearLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webContent.canGoBack()) {
                    webContent.goBack();
                    idLeft.setVisibility(View.GONE);
                }else
                    tlTitleLayout.linearLeft.setVisibility(View.GONE);
            }
        });

        idLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webContent.canGoBack()){
                    webContent.goBack();
                }
                if(webfeature.canGoBack()){
                    webfeature.goBack();
                }
                if(websample.canGoBack()){
                    websample.goBack();
                }
            }
        });

        webContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webContent.getSettings().setAppCacheEnabled(false);
        webContent.loadUrl("http://h5.boericloud.com:18080/delos/public/news/kitchen-sink-ios/aboutus/aboutus.html");

        webContent.getSettings().setJavaScriptEnabled(true);
        webContent.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        webContent.setOnKeyListener(new View.OnKeyListener() {        // webview can go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webContent.canGoBack()) {
                    webContent.goBack();
                    return true;
                }
                return false;
            }
        });

        websample.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

                toastUtils.showProgress("");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toastUtils.dismiss();

                if (websample != null && websample.canGoBack()) {
                    idLeft.setVisibility(View.VISIBLE);
                } else if(idLeft!=null){
                    idLeft.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                toastUtils.dismiss();

            }
        });

        websample.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        websample.getSettings().setAppCacheEnabled(false);
        websample.loadUrl("http://h5.boericloud.com:18080/delos/public/news/kitchen-sink-ios/case/case.html");

        websample.getSettings().setJavaScriptEnabled(true);
        websample.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        websample.setOnKeyListener(new View.OnKeyListener() {        // webview can go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webContent.canGoBack()) {
                    websample.goBack();
                    return true;
                }
                return false;
            }
        });


        webfeature.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

                toastUtils.showProgress("");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toastUtils.dismiss();

                if (webfeature != null && webfeature.canGoBack()) {
                    idLeft.setVisibility(View.VISIBLE);
                } else if(idLeft!=null){
                    idLeft.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                toastUtils.dismiss();

            }
        });

        webfeature.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webfeature.getSettings().setAppCacheEnabled(false);
        webfeature.loadUrl("http://h5.boericloud.com:18080/delos/public/news/kitchen-sink-ios/product/product.html");

        webfeature.getSettings().setJavaScriptEnabled(true);
        webfeature.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");

        webfeature.setOnKeyListener(new View.OnKeyListener() {        // webview can go back
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webContent.canGoBack()) {
                    webfeature.goBack();
                    return true;
                }
                return false;
            }
        });

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            resetColor();
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    tvAboutline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
                case 1:
                    if (currIndex == 0)
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    else if (currIndex == 2)
                        animation = new TranslateAnimation(one * 2, one, 0, 0);
                    tvSampleline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
                case 2:
                    animation = new TranslateAnimation(one, one * 2, 0, 0);
                    tvFeatureline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            //imgBar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }


    @OnClick({R.id.tv_about, R.id.tv_sample, R.id.tv_feature})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_about:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_sample:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tv_feature:
                viewpager.setCurrentItem(2);
                break;
        }
    }


    public class WebAppInterface {
        Context mContext;


        WebAppInterface(Context c) {
            mContext = c;
        }


        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public void closeWindows() {
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public void onBackPressed() {


        if (toastUtils != null && toastUtils.isShowing()) {

            toastUtils.dismiss();

            return;

        }
    }


}

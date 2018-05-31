package com.boer.delos.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.mall.MallDetailActivity;
import com.boer.delos.utils.ToastUtils;
import com.boer.delos.view.TitleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MallFragment extends Fragment {


    @Bind(R.id.web_content)
    WebView webContent;
    public TitleLayout tlTitleLayout;
    int page = 0;
    @Bind(R.id.tv_clean)
    TextView tvClean;
    @Bind(R.id.tv_light)
    TextView tvLight;
    @Bind(R.id.tv_intelligence)
    TextView tvIntelligence;
    @Bind(R.id.tv_sleep)
    TextView tvSleep;
    @Bind(R.id.tv_cleanline)
    TextView tvCleanline;
    @Bind(R.id.tv_lightline)
    TextView tvLightline;
    @Bind(R.id.tv_smartline)
    TextView tvSmartline;
    @Bind(R.id.tv_sleepline)
    TextView tvSleepline;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    private ToastUtils toastUtils;
    private List<View> viewList = new ArrayList<View>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mall, container, false);
        ButterKnife.bind(this, view);
        tlTitleLayout = (TitleLayout) view.findViewById(R.id.tl_title_layout);
        //tlTitleLayout.setLinearLeftImage(R.mipmap.ic_nav_back);
        tlTitleLayout.setTitle(R.string.mall_tab);


        initView();
        return view;
    }

    private void initView() {

        toastUtils = new ToastUtils(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view1 = inflater.inflate(R.layout.item_viewpager_mall1, null);
        View view2 = inflater.inflate(R.layout.item_viewpager_mall2, null);
        View view3 = inflater.inflate(R.layout.item_viewpager_mall3, null);
        View view4 = inflater.inflate(R.layout.item_viewpager_mall4, null);
        ImageView homeairclean = (ImageView) view1.findViewById(R.id.img_homeairclean);
        ImageView cleanimg = (ImageView) view1.findViewById(R.id.img_airclean);
        ImageView radiumimg = (ImageView) view1.findViewById(R.id.img_radium);
        ImageView aromatherimg = (ImageView) view1.findViewById(R.id.img_aromatherapy);
        ImageView homewatercleanimg = (ImageView) view1.findViewById(R.id.img_homewaterclean);
        ImageView tablewaterimg = (ImageView) view1.findViewById(R.id.img_tablewater);
        ImageView floorwaterimg = (ImageView) view1.findViewById(R.id.img_floorwater);


        ImageView lightimg = (ImageView) view2.findViewById(R.id.img_phythmlight);
        ImageView energylightimg = (ImageView) view2.findViewById(R.id.img_energylight);
        ImageView mirrorimg = (ImageView) view3.findViewById(R.id.img_mirror);
        ImageView mattressimg = (ImageView) view4.findViewById(R.id.img_mattress);
        ImageView indoorcurtainimg = (ImageView) view4.findViewById(R.id.img_incurtain);
        ImageView nightlightimg = (ImageView) view4.findViewById(R.id.img_nightlight);
        ImageView outcurtainimg = (ImageView) view4.findViewById(R.id.img_outcurtain);

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        homeairclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",0);
                startActivity(intent);
            }
        });
        cleanimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",1);
                startActivity(intent);
            }
        });
        radiumimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",2);
                startActivity(intent);
            }
        });
        aromatherimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",3);
                startActivity(intent);
            }
        });
        homewatercleanimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",4);
                startActivity(intent);
            }
        });
        tablewaterimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",5);
                startActivity(intent);
            }
        });
        floorwaterimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",6);
                startActivity(intent);
            }
        });

        lightimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",7);
                startActivity(intent);
            }
        });
        energylightimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",8);
                startActivity(intent);
            }
        });
        mirrorimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",9);
                startActivity(intent);
            }
        });

        mattressimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",10);
                startActivity(intent);
            }
        });
        indoorcurtainimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",11);
                startActivity(intent);
            }
        });
        nightlightimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",12);
                startActivity(intent);
            }
        });
        outcurtainimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MallDetailActivity.class);
                intent.putExtra("tag",13);
                startActivity(intent);
            }
        });

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
        //loadingHtml5();
    }

    @OnClick({R.id.tv_clean, R.id.tv_light, R.id.tv_intelligence, R.id.tv_sleep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clean:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_light:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tv_intelligence:
                viewpager.setCurrentItem(2);
                break;
            case R.id.tv_sleep:
                viewpager.setCurrentItem(3);
                break;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            resetColor();
            switch (position) {
                case 0:
                    tvCleanline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
                case 1:
                    tvLightline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
                case 2:
                    tvSmartline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
                case 3:
                    tvSleepline.setBackgroundColor(getResources().getColor(R.color.blue_btn_bg));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void resetColor() {
        tvCleanline.setBackgroundColor(0);
        tvLightline.setBackgroundColor(0);
        tvSmartline.setBackgroundColor(0);
        tvSleepline.setBackgroundColor(0);
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
                    tlTitleLayout.linearLeft.setVisibility(View.VISIBLE);
                } else {
                    tlTitleLayout.linearLeft.setVisibility(View.GONE);
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
                if (webContent.canGoBack())
                    webContent.goBack();
                else
                    tlTitleLayout.linearLeft.setVisibility(View.GONE);
            }
        });


        webContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webContent.getSettings().setAppCacheEnabled(false);
        webContent.loadUrl("https://jintanglangfw.m.tmall.com");

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

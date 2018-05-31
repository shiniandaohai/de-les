package com.boer.delos.activity.greenlive;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.GreenLivePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 绿色生活
 * create at 2016/4/8 10:13
 *
 */
public class GreenLiveListeningActivity extends BaseListeningActivity {

    List<String> pageTitles = new ArrayList<>();// 子界面的标题集合
    List<Fragment> fragments = new ArrayList<>();
    private WaterFragment waterFragment;
    private ElectricityFragment electricityFragment;
    private GasFragment gasFragment;

    private GreenLivePagerAdapter adapter;
    private TabLayout tlGreenLive;
    private ViewPager vpGreenLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_live);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
    }

    private void initView() {
        initTopBar(R.string.green_live_title, null, true, true);
        this.vpGreenLive = (ViewPager) findViewById(R.id.vpGreenLive);
        this.tlGreenLive = (TabLayout) findViewById(R.id.tlGreenLive);

        initViewPager();
    }

    private void initViewPager() {
        pageTitles.add(getString(R.string.water_text));// 设置tab标题
        pageTitles.add(getString(R.string.electric_text));
        pageTitles.add(getString(R.string.gas_text));

        waterFragment = new WaterFragment();
        electricityFragment = new ElectricityFragment();
        gasFragment = new GasFragment();
        fragments.add(waterFragment);
        fragments.add(electricityFragment);
        fragments.add(gasFragment);
        adapter = new GreenLivePagerAdapter(getSupportFragmentManager(), fragments, pageTitles);

        this.vpGreenLive.setAdapter(adapter);
        this.tlGreenLive.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式
        this.tlGreenLive.setupWithViewPager(this.vpGreenLive);
        this.tlGreenLive.setTabsFromPagerAdapter(adapter);// 从adapter中获取tab的文本
        this.vpGreenLive.setCurrentItem(1);// 初始化时，首次看到的是第二个子界面
    }

}

package com.boer.delos.activity.healthylife.urine;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.GreenLivePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengJiYang
 * @Description: 白细胞分析界面
 * create at 2016/5/5 20:14
 */
public class CellAnalysisListeningActivity extends BaseListeningActivity implements View.OnClickListener{

    private android.support.v4.view.ViewPager vpCellAnalysis;
    private TabLayout tlCellAnalysis;

    List<Fragment> fragments = new ArrayList<>();
    List<String> pageTitles = new ArrayList<>();
    private WhiteCellFragment whiteCellFragment;
    private NitriteFragment nitriteFragment;
    private UroFragment uroFragment;
    private ProteinFragment proteinFragment;

    private GreenLivePagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_analysis);

        initView();
    }

    private void initView() {
        initTopBar(R.string.cell_analysis_title, null, true, true);
        ivRight.setImageResource(R.drawable.ic_health_live_more);
        this.vpCellAnalysis = (ViewPager) findViewById(R.id.vpCellAnalysis);
        this.tlCellAnalysis = (TabLayout) findViewById(R.id.tlCellAnalysis);
        ivRight.setOnClickListener(this);
        initViewPager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivRight:
                startActivity(new Intent(CellAnalysisListeningActivity.this, UrinalysisListeningActivity.class));
                break;
        }
    }

    private void initViewPager() {
        pageTitles.add("白细胞");// 添加fragment的title
        pageTitles.add("亚硝酸盐");
        pageTitles.add("尿胆原");
        pageTitles.add("蛋白质");
        whiteCellFragment = new WhiteCellFragment();
        nitriteFragment = new NitriteFragment();
        uroFragment = new UroFragment();
        proteinFragment = new ProteinFragment();
        fragments.add(whiteCellFragment);
        fragments.add(nitriteFragment);
        fragments.add(uroFragment);
        fragments.add(proteinFragment);
        adapter = new GreenLivePagerAdapter(getSupportFragmentManager(), fragments, pageTitles);

        this.vpCellAnalysis.setAdapter(adapter);
        this.tlCellAnalysis.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式
        this.tlCellAnalysis.setupWithViewPager(this.vpCellAnalysis);
        this.tlCellAnalysis.setTabsFromPagerAdapter(adapter);// 从adapter中获取tab的文本
    }
}

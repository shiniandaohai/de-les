package com.boer.delos.activity.settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.ExternalStorageUtils;
import com.boer.delos.widget.LanguagePopupWindow;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingsActivity extends CommonBaseActivity {

    @Bind(R.id.llayout_setting_home_page)
    LinearLayout llayoutSettingHomePage;
    @Bind(R.id.llayout_setting_msg)
    LinearLayout llayoutSettingMsg;
    @Bind(R.id.llayout_setting_language)
    LinearLayout llayoutSettingLanguage;
    @Bind(R.id.llayout_about)
    LinearLayout llayoutAbout;
    @Bind(R.id.llayout_clear_disk)
    LinearLayout llayoutClearDisk;
    @Bind(R.id.tv_language)
    TextView tvLanguage;
    @Bind(R.id.tv_used_space)
    TextView tvUsedSpace;
    @Bind(R.id.llayout_setting_sys)
    LinearLayout llayoutSettingSys;

    //
    LanguagePopupWindow languagePopupWindow;
    String language_simple_chinese;
    String language_english;

    @Override
    protected int initLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(R.string.my_center_setting);


        try {
            tvUsedSpace.setText(ExternalStorageUtils.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void initData() {
        language_simple_chinese = getString(R.string.language_simple_chinese);
        language_english = getString(R.string.language_english);
        ArrayList<String> list = new ArrayList<>();
        list.add(language_simple_chinese);
        list.add(language_english);
        languagePopupWindow = new LanguagePopupWindow(this, llayoutSettingLanguage, list);

    }

    @Override
    protected void initAction() {
        languagePopupWindow.setOnItemClick(new LanguagePopupWindow.OnItemClick() {
            @Override
            public void select(String txt, int pos) {

                updateLanguage(txt);

                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            }
        });

    }


    @OnClick({R.id.llayout_setting_home_page, R.id.llayout_setting_msg, R.id.llayout_setting_language, R.id.llayout_about,R.id.llayout_gate, R.id.llayout_clear_disk, R.id.llayout_setting_sys})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llayout_setting_home_page:

                startActivity(new Intent(SettingsActivity.this, HomePageSettingActivity.class));
                break;
            case R.id.llayout_setting_msg:
                startActivity(new Intent(SettingsActivity.this, MessageSettingActivity.class));
                break;
            case R.id.llayout_setting_language:

                languagePopupWindow.showView();
                break;
            case R.id.llayout_about:
                startActivity(new Intent(SettingsActivity.this, AboutJiaListeningActivity.class));//AboutUsListeningActivity.class));
                break;
            case R.id.llayout_gate:
                startActivity(new Intent(SettingsActivity.this, GatewayInfoListeningActivity.class));//AboutUsListeningActivity.class));
                break;
            case R.id.llayout_clear_disk:

                toastUtils.showProgress(getString(R.string.clear_disk));
                ExternalStorageUtils.clearAllCache(this);
                toastUtils.dismiss();
                tvUsedSpace.setText("");


                break;
            case R.id.llayout_setting_sys:
                startActivity(new Intent(SettingsActivity.this, SysSettingActivity.class));

                break;
        }
    }

    private void updateLanguage(String languageToLoad) {

        Locale locale = new Locale(languageToLoad);

        Locale.setDefault(locale);

        Configuration config = getResources().getConfiguration();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        String language = "";
        switch (languageToLoad) {
            case "zh":
                language = language_simple_chinese;
                break;
            case "en":
                language = language_english;
                break;
        }

        tvLanguage.setText(language);


        if (languageToLoad.equals("zh"))
            config.locale = Locale.SIMPLIFIED_CHINESE;
        else if (languageToLoad.equals("en"))
            config.locale = Locale.US;

        getResources().updateConfiguration(config, metrics);

    }


}

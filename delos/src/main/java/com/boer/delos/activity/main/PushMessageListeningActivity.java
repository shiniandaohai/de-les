package com.boer.delos.activity.main;

import android.os.Bundle;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;

/**
 * @author PengJiYang
 * @Description: "消息推送"界面
 * create at 2016/3/11 11:56
 *
 */
public class PushMessageListeningActivity extends BaseListeningActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);

        initTopBar(R.string.push_message_title, null, true, false);
    }
}

package com.boer.delos.activity.settings;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.commen.BaseListeningActivity;
import com.boer.delos.adapter.SoundAdapter;
import com.boer.delos.constant.Constant;
import com.boer.delos.model.Sound;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.jpush.JpushController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.L;
import com.boer.delos.utils.ScreenUtils;
import com.boer.delos.utils.sharedPreferences.SoundAndVibratorPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * 声音提醒界面
 */
public class SetPushSoundListeningActivity extends BaseListeningActivity {
    private ListView mListView;
    private SoundAdapter mAdapter;
    private List<Sound> mSounds;
    String[] SOUNDS = new String[]{"清新", "叮咚", "滚动", "梦幻", "商务"};
    String[] value = null;
    SoundAndVibratorPreferences soundAndVibratorPreferences;
    MediaPlayer mediaPlayer = null;//播放音乐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_push_sound);
        mListView = (ListView) findViewById(R.id.activity_main_list);
        vTitle = findViewById(R.id.vTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.height = ScreenUtils.getStatusHeight(this);
            vTitle.setLayoutParams(params);
        } else {
            vTitle.setVisibility(View.GONE);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvRight = (TextView) findViewById(R.id.tvRight);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRight = (ImageView) findViewById(R.id.ivRight);

        tvTitle.setText(R.string.sound_notice);
        ivRight.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
        soundAndVibratorPreferences = new SoundAndVibratorPreferences();
        value = soundAndVibratorPreferences.readSoundAndVibratorXml(SetPushSoundListeningActivity.this).split(",");
        int checkID = 0;
        try {
            checkID = Integer.parseInt(value[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createSoundsList();
        mAdapter = new SoundAdapter(this, -1, mSounds);
        mListView = (ListView) findViewById(R.id.activity_main_list);
        mListView.setAdapter(mAdapter);
        //设置本地保存音乐，在列表中显示选中
        mListView.setItemChecked(checkID, true);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playMusicAndSetVibrator(SetPushSoundListeningActivity.this, position);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = mListView.getCheckedItemPosition();

                if (ListView.INVALID_POSITION != pos) {
                    if (!(pos + "").equals(value[0])) {//选中字段与本地保存一致，不进行操作
                        toastUtils.showProgress("正在保存设置...");
                        updateExtend(SetPushSoundListeningActivity.this, pos + "", value[1]);
                    } else {
                        finish();
                    }

                } else {
                    finish();
                }
            }
        });

    }

    //列表选择音乐，进行播放，预听功能
    private void playMusicAndSetVibrator(Context context, int musicIndex) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer = null;
        }
        try {
            switch (musicIndex) {
                case 0:
                    mediaPlayer = MediaPlayer.create(context, R.raw.qingxin);
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(context, R.raw.dingdong);
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(context, R.raw.gundong);
                    break;
                case 3:
                    mediaPlayer = MediaPlayer.create(context, R.raw.menghuan);
                    break;
                case 4:
                    mediaPlayer = MediaPlayer.create(context, R.raw.shangwu);
                    break;
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.start();//播放音乐
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createSoundsList() {
        mSounds = new ArrayList<Sound>(SOUNDS.length);
        for (int i = 0; i < SOUNDS.length; i++) {
            Sound country = new Sound();
            country.setName(SOUNDS[i]);
            mSounds.add(country);
        }
    }

    /**
     * 更新铃声和震动请求
     */
    private void updateExtend(Context mContext, final String tone, final String vibration) {
        //本地直连不更新
        if (Constant.IS_LOCAL_CONNECTION && !Constant.IS_INTERNET_CONN) {
            toastUtils.dismiss();
            finish();
        }
        JpushController.getInstance().updateExtend(mContext, tone, vibration,
                new RequestResultListener() {
                    @Override
                    public void onSuccess(String Json) {
                        L.e("update_extend json===" + Json);
                        String ret = JsonUtil.parseString(Json, "ret");
                        if ("0".equals(ret)) {
                            toastUtils.dismiss();
                            L.e("成功");
                            //保存在本地
                            new SoundAndVibratorPreferences().writeSoundAndVibratorDataXml(SetPushSoundListeningActivity.this, tone, vibration);
                            finish();
                        } else {
                            toastUtils.dismiss();
                            L.e("失败");
                            finish();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        try {
                            //toastUtils.showErrorWithStatus(readString(R.string.unknow_exception));
                            toastUtils.dismiss();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = null;
            }
            int pos = mListView.getCheckedItemPosition();
            if (ListView.INVALID_POSITION != pos) {
                if (!(pos + "").equals(value[0])) {//选中字段与本地保存一致，不进行操作
                    toastUtils.showProgress("正在保存设置...");
                    updateExtend(SetPushSoundListeningActivity.this, pos + "", value[1]);
                } else {
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        super.onDestroy();
    }
}

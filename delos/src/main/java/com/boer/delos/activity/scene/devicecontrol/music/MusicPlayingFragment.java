package com.boer.delos.activity.scene.devicecontrol.music;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.ControlBytes;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.IMusicUpdateListener;
import com.boer.delos.adapter.MusicModeLinkSettingAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.model.ModeAct;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.link.LinkManageController;
import com.boer.delos.utils.JsonUtil;
import com.boer.delos.utils.StringUtil;
import com.boer.delos.utils.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sunzhibin on 2017/8/15.
 */

public class MusicPlayingFragment extends Fragment implements IMusicUpdateListener, SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.tv_music_name)
    TextView tvMusicName;
    @Bind(R.id.tv_music_author)
    TextView tvMusicAuthor;
    @Bind(R.id.music_seekBar)
    SeekBar musicSeekBar;
    @Bind(R.id.tv_current_playtime)
    TextView tvCurrentPlaytime;
    @Bind(R.id.tv_total_time)
    TextView tvTotalTime;
    @Bind(R.id.iv_play_bar_pre)
    ImageView ivPlayBarPre;
    @Bind(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @Bind(R.id.iv_voice_down)
    ImageView ivVoiceDown;
    @Bind(R.id.iv_play_mode)
    ImageView ivPlayMode;
    @Bind(R.id.tv_music_type)
    TextView tvMusicType;
    @Bind(R.id.exLv_model)
    ExpandableListView exLvModel;
    @Bind(R.id.ll_bottom_bar)
    LinearLayout llBottomBar;
    @Bind(R.id.tv_music_type_1)
    TextView tvMusicType1;
    @Bind(R.id.tv_music_type_2)
    TextView tvMusicType2;
    @Bind(R.id.tv_music_type_3)
    TextView tvMusicType3;
    @Bind(R.id.tv_music_type_4)
    TextView tvMusicType4;
    @Bind(R.id.tv_music_type_5)
    TextView tvMusicType5;
    @Bind(R.id.tv_music_type_6)
    TextView tvMusicType6;
    @Bind(R.id.ll_music_types)
    LinearLayout llMusicTypes;
    @Bind(R.id.tv_loading)
    TextView tvLoading;
    @Bind(R.id.tv_loading_fail)
    TextView tvLoadingFail;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_link_mode)
    ImageView ivLinkMode;
    @Bind(R.id.iv_album)
    ImageView ivAlbum;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private View mRootView;
    private HashMap<String, List<ModeAct>> mModelMaps;
    private ObjectAnimator objectAnimator = null;
    private MusicModeLinkSettingAdapter mAdapter;
    private ObjectAnimator objectAnimatorLeft;
//    private ObjectAnimator objectAnimatorAlpha;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_music_playing, null);
        ButterKnife.bind(this, mRootView);
        initData();
        initListener();
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        musicSeekBar.setOnSeekBarChangeListener(this);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        musicSeekBar.setMax(123456);
    }

    private void initData() {
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        mAdapter = new MusicModeLinkSettingAdapter(getActivity(), mModelMaps);
        exLvModel.setAdapter(mAdapter);
        exLvModel.setGroupIndicator(null);
        queryAllModesInfo();
    }

    private void initListener() {
        exLvModel.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ModeAct modeAct = mAdapter.getChild(groupPosition, childPosition);
                String modeId = modeAct.getModeId();
//                mControlValue.setCmd("5");
//                mControlValue.setData("1");
//                mControlValue.setModeId(modeId);
//                mControlValue.setVolume(mDeviceStatus.getValue().getVolume().toString());
//                mControlDevice.setValue(mControlValue);
//                controlMusic(mControlDevice);
                ((MusicWiseActivity) getActivity()).controlMusic(modeId);
                return true;
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                tvLoadingFail.setClickable(true);
            }
        });

    }

    @OnClick({R.id.iv_play_bar_pre, R.id.iv_play_bar_play, R.id.iv_play_bar_next,
            R.id.iv_voice_down, R.id.iv_voice_up, R.id.iv_play_mode, R.id.tv_music_type,
            R.id.tv_music_type_1, R.id.tv_music_type_2, R.id.tv_music_type_3,
            R.id.tv_music_type_4, R.id.tv_music_type_5, R.id.tv_music_type_6,
            R.id.tv_music_type_7, R.id.tv_music_type_8,
            R.id.tv_loading_fail, R.id.iv_back, R.id.iv_link_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_play_bar_pre:
                tvMusicName.setText(getString(R.string.music_unknown));
                tvMusicAuthor.setText(getString(R.string.music_unknown));
                musicSeekBar.setMax(0);
                musicSeekBar.setProgress(0);
                tvCurrentPlaytime.setText("00:00");
                tvTotalTime.setText("00:00");
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().wiseMusicPrevious());

                tranceXAnimator(true);
                break;
            case R.id.iv_play_bar_play:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().wiseMusicPlayOrPause());
                break;
            case R.id.iv_play_bar_next:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().wiseMusicNext());
                tvMusicName.setText(getString(R.string.music_unknown));
                tvMusicAuthor.setText(getString(R.string.music_unknown));
                musicSeekBar.setMax(0);
                musicSeekBar.setProgress(0);
                tvCurrentPlaytime.setText("00:00");
                tvTotalTime.setText("00:00");
                tranceXAnimator(false);
                break;
            case R.id.iv_voice_down:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicVolume(0));
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().wiseMusicGetVolume());

                break;
            case R.id.iv_voice_up:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicVolume(1));
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().wiseMusicGetVolume());

                break;
            case R.id.iv_play_mode:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setOrGetLoopMode(false));
                break;
            case R.id.tv_music_type:
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffectOpen(0));

                llMusicTypes.setVisibility(View.VISIBLE);
                llBottomBar.setVisibility(View.GONE);
                break;
            case R.id.tv_music_type_1: //古典 6
//                1->"普通/Normal"
//                2->"摇滚/Rock"
//                3->"流行/Pop"
//                4->"舞曲/Dance"
//                5->"嘻哈/Hip-Hop"
//                6->"古典/Classic"
//                7->"超重低音/Bass"
//                8->"人声/Vocal"
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(0));
                break;
            case R.id.tv_music_type_2: //现代 1
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(1));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_3://摇滚 2
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(2));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_4://流行 3
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(3));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_5: //舞曲 4
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(4));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_6://原声 8
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(5));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_7://原声 8
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(6));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_music_type_8://原声 8
                ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicEffect(7));
                llMusicTypes.setVisibility(View.GONE);
                llBottomBar.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_loading_fail:
                tvLoadingFail.setClickable(false);
                queryAllModesInfo();
                break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.iv_link_mode:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
        }
    }

    private void queryAllModesInfo() {
        LinkManageController.getInstance().queryAllMode2CunrrentGateWay(getActivity(),
                null, new RequestResultListener() {
                    @Override
                    public void onSuccess(String json) {
//                toastUtils.dismiss();

                        try {
                            int ret = JsonUtil.parseInt(json, "ret");
                            if (ret != 0) {
                                showTextHint("1");
                                return;
                            }
                            showTextHint("2");

                            String temp = JsonUtil.parseString(json, "response");
                            if (!StringUtil.isEmpty(temp)) parseJson(temp);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String json) {
                        showTextHint("");
                    }
                });
    }


    private void showTextHint(String isShow) {
        if (tvLoading == null && tvLoadingFail == null) {
            return;
        }
        if (TextUtils.isEmpty(isShow)) {
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.GONE);
        } else if (isShow.equals("0")) { //loading
            tvLoading.setVisibility(View.VISIBLE);
            tvLoadingFail.setVisibility(View.GONE);
        } else if (isShow.equals("1")) { // loading error
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.VISIBLE);
        } else if (isShow.equals("2")) {//loading success
            tvLoading.setVisibility(View.GONE);
            tvLoadingFail.setVisibility(View.GONE);
        }


    }

    //TODO 模式的配置
    private void parseJson(String Json) {
        if (mModelMaps == null) mModelMaps = new HashMap<>();
        String key = null;
        try {
            JSONObject jsonObject = new JSONObject(Json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                key = iterator.next();
                List<ModeAct> modeActList = JsonUtil.parseDataList(Json, ModeAct.class, key);
                mModelMaps.put(key, modeActList);
            }
            mAdapter.setmListData(mModelMaps, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void rotateImageView(boolean play) {
//        RotateAnimation an = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        an.setInterpolator(new LinearInterpolator());//不停顿
//        an.setRepeatCount(1);//重复次数
//        an.setFillAfter(true);//停在最后
//        an.setDuration(3600);
//        ivAlbum.startAnimation(an);
//        && TextUtils.isEmpty((String) ivAlbum.getTag())
        if (play && (objectAnimator == null || !objectAnimator.isRunning())) {
            objectAnimator = ObjectAnimator.ofFloat(ivAlbum, "rotation", 0, 360);
            objectAnimator.setDuration(3600);
            objectAnimator.setRepeatCount(ValueAnimator.RESTART);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(-1);
            objectAnimator.start();

        } else if (!play) {
            if (objectAnimator != null)
                objectAnimator.cancel();
        }
    }

    private void tranceXAnimator(boolean leftOut) {
        if (objectAnimatorLeft == null) {
            objectAnimatorLeft = ObjectAnimator.ofFloat(ivAlbum, "translationX", 0, 0);
            objectAnimatorLeft.setDuration(1000);
            objectAnimatorLeft.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    objectAnimatorAlpha();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (leftOut) {
            if (objectAnimator != null)
                objectAnimator.cancel();
            objectAnimatorLeft.setFloatValues(0, -1000);
            objectAnimatorLeft.start();

        } else {
            if (objectAnimator != null)
                objectAnimator.cancel();
            objectAnimatorLeft.setFloatValues(0, 1000);
            objectAnimatorLeft.start();
        }
    }

    private void objectAnimatorAlpha() {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), R.animator.music_play);
        anim.setTarget(ivAlbum);
        anim.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (objectAnimator != null)
            objectAnimator.cancel();
    }

    @Override
    public void musicName(String name) {
        if (tvMusicName != null)
            tvMusicName.setText(name);
    }

    @Override
    public void musicDuration(int duration) {
        if (musicSeekBar != null) {
            musicSeekBar.setMax(duration);
            float second = duration / 1000f;
            int min = (int) (second / 60);
            int sec = (int) (second - min * 60);
            String time = ((min < 10) ? "0" + min : min) + ":" + ((sec < 10) ? "0" + sec : sec);
            tvTotalTime.setText(time);
        }
    }

    @Override
    public void musicPosition(int position) {
        if (musicSeekBar != null) {
            musicSeekBar.setProgress(position);
            float second = position / 1000f; //毫秒转秒
            int min = (int) (second / 60);
            int sec = (int) (second - min * 60);
            String time = ((min < 10) ? "0" + min : min) + ":" + ((sec < 10) ? "0" + sec : sec);
            tvCurrentPlaytime.setText(time);

        }
    }

    @Override
    public void musicArticle(String article) {
        if (tvMusicAuthor != null)
            tvMusicAuthor.setText(article);
    }

    @Override
    public void musicMode(String level) { //音效
        if (TextUtils.isEmpty(level) || tvMusicType == null) {
            return;
        }
        switch (level) {
            case "0":
                tvMusicType.setText(getString(R.string.music_type_1));
                break;
            case "1":
                tvMusicType.setText(getString(R.string.music_type_2));
                break;
            case "2":
                tvMusicType.setText(getString(R.string.music_type_3));
                break;
            case "3":
                tvMusicType.setText(getString(R.string.music_type_4));
                break;
            case "4":
                tvMusicType.setText(getString(R.string.music_type_5));
                break;
            case "5":
                tvMusicType.setText(getString(R.string.music_type_6));
                break;
            case "6":
                tvMusicType.setText(getString(R.string.music_type_7));
                break;
            case "7":
                tvMusicType.setText(getString(R.string.music_type_8));
                break;

        }
    }

    @Override
    public void musicLoopMode(String level) {//循环
        if (TextUtils.isEmpty(level) || ivPlayMode == null) {
            return;
        }
        int MPM_ERROR = -1; // error
        int MPM_NORMAL = 0; // normal
        int MPM_REPEAT_ALL = 1; // repeat all
        int MPM_REPEAT_ONE = 2; // repeat one
        int MPM_SHUFFLE = 3; // shuffle
        switch (level) {
            case "-1":
                break;
            case "0":
                ivPlayMode.setImageResource(R.mipmap.music_order);
                break;
            case "1":
                ivPlayMode.setImageResource(R.mipmap.music_loop);
                break;
            case "2":
                ivPlayMode.setImageResource(R.mipmap.music_single);
                break;
            case "3":
                ivPlayMode.setImageResource(R.mipmap.music_random);

                break;
        }

    }

    @Override
    public void musicPlayState(boolean play) {
        if (ivPlayBarPlay != null) {
            ivPlayBarPlay.setSelected(play);
            rotateImageView(play);
        }

    }

    @Override
    public void musicVolume(String volume) {
//        ToastHelper.showShortMsg(volume);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ((MusicWiseActivity) getActivity()).sendMsg(ControlBytes.getInstance().setMusicProgress(seekBar.getProgress()));

    }
}

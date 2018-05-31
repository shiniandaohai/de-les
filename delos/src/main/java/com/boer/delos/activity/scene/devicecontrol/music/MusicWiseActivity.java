package com.boer.delos.activity.scene.devicecontrol.music;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.boer.delos.R;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.BaseMsg;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.ControlBytes;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.IWirseUdpCMD;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.SendMsgPeriod;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.TargetInfo;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpClientConfig;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpClientListener.SimpleUdpClientListener;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.UdpMsg;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.XUdp;
import com.boer.delos.activity.scene.devicecontrol.music.wirseudp.utils.ByteUtils;
import com.boer.delos.adapter.MusicPlayListAdapter;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.constant.DeviceUpdateStatus;
import com.boer.delos.model.ControlDevice;
import com.boer.delos.model.ControlDeviceValue;
import com.boer.delos.model.Device;
import com.boer.delos.model.DeviceRelate;
import com.boer.delos.model.MusicResult;
import com.boer.delos.request.RequestResultListener;
import com.boer.delos.request.device.DeviceController;
import com.boer.delos.utils.GsonUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunzhibin on 2017/8/15.
 */

public class MusicWiseActivity extends CommonBaseActivity implements IWirseUdpCMD {
    @Bind(R.id.lv_music)
    PullToRefreshListView pullToRefreshListView;
    @Bind(R.id.iv_play_bar_cover)
    ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_pre)
    ImageView ivPlayBarPre;
    @Bind(R.id.iv_play_bar_play)
    ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    ImageView ivPlayBarNext;
    @Bind(R.id.seekBar_play)
    SeekBar seekBar_play;
    @Bind(R.id.fl_play_bar)
    FrameLayout flPlayBar;
    private int lastIndex = 0;
    private static int MUSIC_LIST_COUNT = 20;
    private static int PALPITATE_PERIOD = 1000 * 3;///
    private Device mDevice;
    private ControlDeviceValue mControlValue;
    private ControlDevice mControlDevice;
    private MusicPlayListAdapter mMusicPlayListAdapter;
    private List<MusicResult.ResponseBean.MusicBean> mMusicLists;
    private XUdp mXUdp;
    private MyUdpClientListener listener;
    private TargetInfo mTargetInfo;
    private ListView lvMusic;

    private MyHandler mHandler;
    private String mMusicServerIP = "";
    private MusicResult.ResponseBean.MusicBean mCurrentMusic;
    private int SEND_PERIOD = 1000 * 5;
    private boolean isPlayFragmentShow; //Fragment is showing
    private MusicPlayingFragment mPlayFragment;
    private int mVolume = -1;//音量默认
    private String mFavoriteTag;
    private List<Device> devices;

    @Override
    protected int initLayout() {
        return R.layout.activity_background_music;
    }


    @Override
    protected void initView() {
        tlTitleLayout.setTitle(getString(R.string.music_background_music));
        Bundle bundle = getIntent().getExtras();
        DeviceRelate deviceRelate = (DeviceRelate) bundle.getSerializable("device");
        mDevice = deviceRelate.getDeviceProp();
        devices = new ArrayList<>();
        mFavoriteTag = mDevice.getFavorite();
        if (mDevice != null && !TextUtils.isEmpty(mDevice.getFavorite())
                && mDevice.getFavorite().equals("1")) {
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        } else
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        lvMusic = pullToRefreshListView.getRefreshableView();
        mHandler = new MyHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryMusicInfo();
    }

    @Override
    protected void initData() {

        mControlDevice = new ControlDevice();
        mControlDevice.setAddr(mDevice.getAddr());
        mControlDevice.setAreaName(mDevice.getAreaname());
        mControlDevice.setRoomName(mDevice.getRoomname());
        mControlDevice.setDeviceName(mDevice.getName());
        mControlDevice.setType(mControlDevice.getType());
        mControlDevice.setBrand(mDevice.getBrand());

        mControlValue = new ControlDeviceValue();
        mControlValue.setCmd("5");
        mControlValue.setData("1");
        mControlValue.setDataLen("1");
        mControlDevice.setValue(mControlValue);
        mControlValue.setBrand(mDevice.getBrand());

        mMusicLists = new ArrayList<>();
        mMusicPlayListAdapter = new MusicPlayListAdapter(this, mMusicLists, R.layout.item_music_play);
        lvMusic.setAdapter(mMusicPlayListAdapter);
        getMusicList(lastIndex, MUSIC_LIST_COUNT);
        sendMsg(ControlBytes.getInstance().getWiseDevice());//查找设备

    }

    @Override
    protected void initAction() {
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                mMusicLists.clear();
                lastIndex = 0;
                getMusicList(lastIndex, MUSIC_LIST_COUNT);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMusicList(lastIndex, lastIndex + 10);

            }
        });
        lvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentMusic = mMusicLists.get(position - 1);
                Log.d("json", "position: " + position);
                byte[] cmd = ControlBytes.getInstance().wiseMusicPlayChooseSong((position - 1)+1);
                sendMsg(cmd);
                tvPlayBarArtist.setText(mCurrentMusic.getArtist());
                tvPlayBarTitle.setText(mCurrentMusic.getTitle());
                seekBar_play.setMax(mCurrentMusic.getDuration());
                mMusicPlayListAdapter.setSelectedPosition(position - 1);
            }
        });
    }

    @OnClick({R.id.iv_play_bar_pre, R.id.iv_play_bar_play, R.id.iv_play_bar_next, R.id.fl_play_bar})
    public void onViewClicked(View view) {
        int i = -1;
        switch (view.getId()) {
            case R.id.iv_play_bar_pre:
                sendMsg(ControlBytes.getInstance().wiseMusicPrevious());
                tvPlayBarTitle.setText(getString(R.string.music_unknown));
                tvPlayBarArtist.setText(getString(R.string.music_unknown));
                break;
            case R.id.iv_play_bar_play:
                sendMsg(ControlBytes.getInstance().wiseMusicPlayOrPause());
                ivPlayBarPlay.setSelected(!ivPlayBarPlay.isSelected());
                break;
            case R.id.iv_play_bar_next:
                sendMsg(ControlBytes.getInstance().wiseMusicNext());
                tvPlayBarTitle.setText(getString(R.string.music_unknown));
                tvPlayBarArtist.setText(getString(R.string.music_unknown));

                break;
            case R.id.fl_play_bar:
                // TODO
//                sendMsg(ControlBytes.getInstance().getWiseDevice());
                showPlayingFragment();
//                sendMsg(ControlBytes.getInstance().wiseMusicPosition());//播放进度
//                mHandler.sendEmptyMessage(4);
                return;
        }

    }

    @Override
    public void rightViewClick() {
        super.rightViewClick();
        if (mFavoriteTag.equals("1")) {
            mFavoriteTag = "0";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_collect_nor);
        } else if (mFavoriteTag.equals("0")) {
            mFavoriteTag = "1";
            tlTitleLayout.setLinearRightImage(R.mipmap.nav_red_collect);
        }
        //保存状态，并发送后台
        if (mDevice != null) {
            devices.clear();
            mDevice.setFavorite(mFavoriteTag);
            devices.add(mDevice);
            DeviceUpdateStatus.setCommonDevice(this, devices, toastUtils);
        }
    }

    /**
     * 发送控制命令
     *
     * @param cmd 命令数组 byte[]
     */
    public void sendMsg(byte[] cmd) {
        if (mXUdp == null) {
            mXUdp = XUdp.getUdpClient();
            if (listener == null) listener = new MyUdpClientListener();
            mXUdp.addUdpClientListener(listener);
            mXUdp.config(new UdpClientConfig.Builder()
                    .setLocalPort(11000).create());
        }
        if (TextUtils.isEmpty(mMusicServerIP)) {
            mTargetInfo = new TargetInfo("255.255.255.255", 11000);
        } else {
            mTargetInfo.setIp(mMusicServerIP);
        }

        mXUdp.sendMsg(new UdpMsg(cmd, mTargetInfo, BaseMsg.MsgType.Send), true);

    }

    class MyUdpClientListener extends SimpleUdpClientListener {
        @Override
        public void onSended(XUdp XUdp, UdpMsg udpMsg) {
            Log.i("XUdp  onSended", Arrays.toString(udpMsg.getSourceDataBytes()));
        }

        @Override
        public void onReceive(XUdp client, UdpMsg udpMsg) {
            Log.i("XUdp  onReceive", Arrays.toString(udpMsg.getSourceDataBytes()));
            Log.i("XUdp  onReceive IP: ", udpMsg.getTarget().getIp() + " port: " + udpMsg.getTarget().getPort());
            if (TextUtils.isEmpty(mMusicServerIP)
                    && !TextUtils.isEmpty(udpMsg.getSourceDataString())) {
                mMusicServerIP = udpMsg.getTarget().getIp();
            } else {
                sendMsg(ControlBytes.getInstance().getWiseDevice());
            }
            if (!TextUtils.isEmpty(mMusicServerIP)
                    && !TextUtils.isEmpty(udpMsg.getTarget().getIp())
                    && mMusicServerIP.equals(udpMsg.getTarget().getIp())) {

                try {
                    dealWithBytes(udpMsg);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * 查询音乐列表
     *
     * @param from
     * @param to
     */
    private void getMusicList(int from, int to) {
        DeviceController.getInstance().queryWiseMediaList(this, from, to, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
//                Log.d("json", json);
                MusicResult result = GsonUtil.getObject(json, MusicResult.class);
                if (result.getRet() != 0) {
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    return;
                }
                List<MusicResult.ResponseBean.MusicBean> list = result.getResponse().getSongList();

                if (list == null) {
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    return;
                }
                if (lastIndex == 0) mMusicLists.clear();
                lastIndex += list.size();
                mMusicLists.addAll(list);
                mMusicPlayListAdapter.setDatas(mMusicLists);
                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void onFailed(String json) {
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(0, 1000 * 2);
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.what;
            if (type == 0) {
                if (pullToRefreshListView != null)
                    pullToRefreshListView.onRefreshComplete();
            } else if (type == 1) {
                sendMsg(ControlBytes.getInstance().getWiseDevice());//查找设备
                mHandler.sendEmptyMessageDelayed(type, 5 * 1000);
            } else if (type == 2) {
                sendMsg(ControlBytes.getInstance().wisePlayStatus());//播放状态
                mHandler.sendEmptyMessageDelayed(type, 2 * 1000);
            } else if (type == 3) {
                sendMsg(ControlBytes.getInstance().wiseMusicDuration());//音乐总长
                mHandler.sendEmptyMessageDelayed(type, 10 * 1000);
            } else if (type == 4) {
                sendMsg(ControlBytes.getInstance().wiseMusicPosition());//播放进度
                mHandler.sendEmptyMessageDelayed(type, 1000);

            } else if (type == 5) {
                sendMsg(ControlBytes.getInstance().wiseMusicSongName());//音乐名
                mHandler.sendEmptyMessageDelayed(type, 1000 * 10);

            } else if (type == 6) {
                sendMsg(ControlBytes.getInstance().wiseMusicGetArtist());//歌手
                mHandler.sendEmptyMessageDelayed(type, 1000 * 10);

            } else if (type == 7) { //模式
                sendMsg(ControlBytes.getInstance().setOrGetLoopMode(true));//循环模式
                mHandler.sendEmptyMessageDelayed(type, 1000 * 10);

            } else if (type == 8) {//音效
                sendMsg(ControlBytes.getInstance().wiseMusicGetEffect());//音效
                mHandler.sendEmptyMessageDelayed(type, 1000 * 10);

            } else if (type == 9) {//获取当前音量
                sendMsg(ControlBytes.getInstance().wiseMusicGetVolume());//音量
                mHandler.sendEmptyMessageDelayed(type, 1000 * 30);
            }
        }

    }

    public void queryMusicInfo() {
        //状态、时长、进度、歌名、艺术家
//        int[] cmds = new int[]{TCPCMD_PLAYSTATUS, TCPCMD_DURATION,
//                TCPCMD_POSITION, TCPCMD_SONGNAME, TCPCMD_ARTIST};
        mHandler.sendEmptyMessage(1);
        mHandler.sendEmptyMessageDelayed(2, 200);
        mHandler.sendEmptyMessageDelayed(3, 400);
        mHandler.sendEmptyMessageDelayed(4, 600);
        mHandler.sendEmptyMessageDelayed(5, 800);
        mHandler.sendEmptyMessageDelayed(6, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mXUdp != null) {
            mXUdp.clear();
            mXUdp = null;
        }
    }

    /**
     * 处理UDP返回的结果
     *
     * @param udpMsg
     * @return
     */
    public String dealWithBytes(UdpMsg udpMsg) {
        byte cmd = udpMsg.getSourceDataBytes()[4];//
        String result = "";
        String msg = udpMsg.getSourceDataString();
        Log.d("result ", udpMsg.getSourceDataString());
        Log.d("result ", "code: " + Arrays.toString(udpMsg.getSourceDataBytes()));
        Log.d("result ", "string: " + new String(udpMsg.getSourceDataBytes()));

        String hex = ByteUtils.bytesToHexString(cmd);
        Log.d("result cmd", hex);
        switch (cmd) {
            case TCPCMD_PALPITANT:
                //暂不做处理、返回设备名--BOER-7C-TFANBTR
                break;
            case TCPCMD_PLAY_PAUSE:
                //不做处理
                break;
            case TCPCMD_PRE:
                //收到数据不做处理
//                sendMsg(ControlBytes.getInstance().wiseMusicSongName());
//                mHandler.removeCallbacksAndMessages(null);
                queryMusicInfo();
                break;
            case TCPCMD_NEXT:
                //
                queryMusicInfo();
//                sendMsg(ControlBytes.getInstance().wiseMusicSongName());
                break;
            case TCPCMD_LOOPMODE:
                if (TextUtils.isEmpty(msg)) {
                    return msg;
                }
                switch (msg) {
                    case "-1": //error
                        break;
                    case "0"://normal
                        break;
                    case "1"://repeat all
                        break;
                    case "2"://repeat one
                        break;
                    case "3":// shuffle
                        break;

                }
                if (mPlayFragment != null && isPlayFragmentShow)
                    mPlayFragment.musicLoopMode(msg);
                return result;
            case TCPCMD_VOL_CTRL:
                //0: '-'
                //1:'+'
                if (TextUtils.isEmpty(msg)) {
                    return "";
                }
                if (mVolume == -1) {
                    if (mHandler != null)
                        mHandler.sendEmptyMessage(9);
                    return "";
                }
                if (mPlayFragment != null) {
                    if (msg.equals("1")) {
                        mVolume++;
                        if (mVolume >= 15) mVolume = 15;
                    } else {
                        mVolume--;
                        if (mVolume <= 1) mVolume = 1;
                    }
                    mPlayFragment.musicVolume(mVolume + "");
                }
                break;
            case TCPCMD_PLAYSTATUS:
                if (TextUtils.isEmpty(msg)) {
                    return result;
                }
                switch (msg) {
                    case "-1":// no file
                        toastUtils.showInfoWithStatus(getString(R.string.music_no_file));
                        ivPlayBarPlay.setSelected(false);
                        break;
                    case "0":// invalid
                        ivPlayBarPlay.setSelected(false);
                        break;
                    case "1":// play
                        ivPlayBarPlay.setSelected(true);
                        break;
                    case "2":// pause
                    case "3":// pause
                    case "4":// paresync
                    case "5":// parecomplete
                        ivPlayBarPlay.setSelected(false);
                        break;
                    case "6":// complete
                        queryMusicInfo();
                        ivPlayBarPlay.setSelected(false);

                        break;
                }
                if (mPlayFragment != null && isPlayFragmentShow)
                    mPlayFragment.musicPlayState(msg.equals("1"));
                break;
            case TCPCMD_SHUTDOWN:
                //暂时无此功能

                break;
            case TCPCMD_DURATION: //获取当前播放歌曲时长
                byte[] bytes = udpMsg.getResultDataBytes();
                String durationHex = ByteUtils.bytesToHexString(bytes);
//                String d = ByteUtils.bytesToHexString(bytes);
                // 带参数返回毫秒级时间数字
                if (TextUtils.isEmpty(durationHex)) {
                    return "";
                }
//                if (seekBar_play.getMax() != 0) {
//                    stop(TCPCMD_DURATION);
//                }
                int duration = Integer.parseInt(durationHex, 16);
                Log.d(" 总时间：", "" + msg + " 打他 " + duration + " " + Arrays.toString(udpMsg.getResultDataBytes()));
//                seekBar_play.setMax(duration);
                if (mPlayFragment != null && isPlayFragmentShow)
                    mPlayFragment.musicDuration(duration);
                if (mHandler != null && mHandler.hasMessages(TCPCMD_DURATION)) {
                    mHandler.removeMessages(TCPCMD_DURATION);
                }
                break;
            case TCPCMD_POSITION:
                //   带参数返回毫秒级时间数字
                byte[] bytesPosition = udpMsg.getResultDataBytes();
                String positionHex = ByteUtils.bytesToHexString(bytesPosition);
//                String d = ByteUtils.bytesToHexString(bytes);
                // 带参数返回毫秒级时间数字
                if (TextUtils.isEmpty(positionHex)) {
                    return "";
                }
                if (mHandler != null && mHandler.hasMessages(TCPCMD_PALPITANT))
                    mHandler.removeMessages(TCPCMD_PALPITANT);
                int position = Integer.parseInt(positionHex, 16);
                Log.d("进度：", "16进制 " + positionHex);
                Log.d("进度：", "" + msg + " 打他 " + position + " " + Arrays.toString(udpMsg.getResultDataBytes()));
                if (mPlayFragment != null && isPlayFragmentShow) {
                    mPlayFragment.musicPosition(position);
                    mPlayFragment.musicArticle(tvPlayBarArtist.getText().toString());
                    mPlayFragment.musicName(tvPlayBarTitle.getText().toString());
                }
                break;
            case TCPCMD_SONGNAME:
//                带参数返回歌曲名字
                if (TextUtils.isEmpty(msg) && TextUtils.isEmpty(tvPlayBarTitle.getText())) {
                    tvPlayBarTitle.setText(getString(R.string.music_unknown));
                } else if (TextUtils.isEmpty(msg)) {
                    return "";
                } else{
                    tvPlayBarTitle.setText(msg);
                    setCurSelectedItem();
                }
                if (!TextUtils.isEmpty(tvPlayBarTitle.getText())
                        && !TextUtils.isEmpty(msg)
                        && !tvPlayBarTitle.getText().toString().equals(msg)) {
                    queryMusicInfo();
                }
                if (mPlayFragment != null && isPlayFragmentShow)
                    mPlayFragment.musicName(msg);

                break;
            case TCPCMD_ROOMNAME:
//                带参数返回房间名和房间号，用"::"分隔

                break;
            case TCPCMD_PROGRESS://设置进度条,需带参数，参数为时间，毫秒级。
                Log.d("进度条控制", Arrays.toString(udpMsg.getSourceDataBytes()));
                break;
            case TCPCMD_MEDIATYPE:
                break;

            case TCPCMD_MEDIA_LIST_SIZE:
                break;

            case TCPCMD_GET_MEDIA_LIST:
                break;
            case TCPCMD_PLAY_POS:
                //播放制定歌曲
                tvPlayBarTitle.setText(mCurrentMusic.getTitle());
                tvPlayBarArtist.setText(mCurrentMusic.getArtist());
                seekBar_play.setMax(mCurrentMusic.getDuration());
                break;
            case TCPCMD_ARTIST://艺术家
                if (TextUtils.isEmpty(msg) && TextUtils.isEmpty(tvPlayBarArtist.getText())) {
                    tvPlayBarArtist.setText(getString(R.string.music_unknown));
                } else if (TextUtils.isEmpty(msg)) {
                    return "";
                } else{
                    tvPlayBarArtist.setText(msg);
                    setCurSelectedItem();
                }
                if (mPlayFragment != null && isPlayFragmentShow)
                    mPlayFragment.musicArticle(msg);

                break;
            case TCPCMD_VOL_SET://原指令返回
                if (udpMsg.getSourceDataBytes().length != 0) {
                    mPlayFragment.musicVolume(udpMsg.getResultDataBytes()[0] + "");
                }
                break;
            case TCPCMD_VOL_GET:
                if (TextUtils.isEmpty(msg)) {
                    return "";
                }
                int volume = 0;
                try {
                    volume = Integer.valueOf(msg);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (volume == 0) return "";
                mVolume = volume;
//                mPlayFragment.musicVolume(mVolume + "");
                break;
            case TCPCMD_UPDATE_ROOMNAME:
                break;
            case TCPCMD_UPDATE_ROOMNO:
                break;
            case TCPCMD_SIWTCH_SOUND_SOURCE:
                break;
            case TCPCMD_GET_TIMER_SIZE:
                break;
            case TCPCMD_GET_TIMER_DATA:
                break;
            case TCPCMD_SET_TIMER_DATA:
                break;
            case TCPCMD_GET_TIMER_SWITCH:
                break;
            case TCPCMD_SET_TIMER_SWITCH:
                break;
            case TCPCMD_GET_SUBAREA_CONTROL:
                break;
            case TCPCMD_SET_SUBAREA_CONTROL:
                break;
            case TCPCMD_GET_EQ://获取音效
                if (TextUtils.isEmpty(msg)) {
                    return "";
                }
                Log.d("音效 1", Arrays.toString(udpMsg.getResultDataBytes()) + " msg " + msg);
                mPlayFragment.musicMode(msg);
                break;
            case TCPCMD_SET_EQ:
                Log.d("音效 2 ", Arrays.toString(udpMsg.getResultDataBytes()) + " msg " + msg);
                if (TextUtils.isEmpty(msg)) {
                    return "";
                }
                mPlayFragment.musicMode(msg);
                break;

        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        super.onBackPressed();
    }

    protected void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new MusicPlayingFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;

        mPlayFragment.musicName(tvPlayBarTitle.getText().toString());
        mPlayFragment.musicArticle(tvPlayBarArtist.getText().toString());
        mPlayFragment.musicDuration(seekBar_play.getMax());
        mPlayFragment.musicPosition(seekBar_play.getProgress());
        mPlayFragment.musicPlayState(ivPlayBarPlay.isSelected());

        mHandler.sendEmptyMessage(7);
        mHandler.sendEmptyMessage(8);
        mHandler.sendEmptyMessage(9);

    }

    public void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
        mHandler.removeMessages(7);
        mHandler.removeMessages(8);
        mHandler.removeMessages(9);
    }

    /**
     * 关联背景音乐
     *
     * @param modeId
     */
    public void controlMusic(String modeId) {
        mControlDevice.getValue().setModeId(modeId);
        mControlDevice.getValue().setData("1");
        mControlDevice.getValue().setVolume(mVolume + "");
        DeviceController.getInstance().deviceControl(this, mControlDevice, new RequestResultListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("json", json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int ret = jsonObject.getInt("ret");
                    if (ret == 0 && !TextUtils.isEmpty(mControlDevice.getValue().getVolume())) {
                        toastUtils.showSuccessWithStatus(getString(R.string.music_link_success));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(String json) {

            }
        });
    }

    private void setCurSelectedItem(){
        int position=-1;
        for(int i=0;i<mMusicLists.size();i++){
            MusicResult.ResponseBean.MusicBean musicBean=mMusicLists.get(i);
            if(musicBean.getArtist().equals(tvPlayBarArtist.getText().toString())&&
                    musicBean.getTitle().equals(tvPlayBarTitle.getText().toString())){
                position=i;
                break;
            }
        }
        mMusicPlayListAdapter.setSelectedPosition(position);
    }
}

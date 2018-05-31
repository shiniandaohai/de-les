package com.boer.delos.activity.camera;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.boer.delos.R;
import com.boer.delos.commen.CommonBaseActivity;
import com.boer.delos.utils.camera.SystemBarTintManager;
import com.boer.delos.utils.camera.TranslucentStatus;
import com.boer.delos.view.TitleLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LocalPlaybackActivity extends CommonBaseActivity implements OnClickListener, GestureDetector.OnGestureListener, OnTouchListener {

    private static final int FLING_MIN_DISTANCE = 20;
    private static final int FLING_MIN_VELOCITY = 0;
    private GestureDetector mGestureDetector;

    private String path;
    private Handler handler = new Handler();
    private VideoView mVideoView;
    private SeekBar mSeekBar;

    private ImageButton btnPlayPause;

    private TextView tvCurrentTime;
    private TextView tvTotalTime;

    private RelativeLayout layoutSeekingBar;
    private LinearLayout layoutButtonBar;
    private TitleLayout titleLayout;

    private boolean mIsPlaying = false;
    private boolean mIsFling = false;
    private boolean mIsBarShowing = true;

    private static final int DEFAULT_LIST_SIZE = 1;
    private List<String> VIDEO_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
    private int mPosition;
    private int mSize;
    private int mCurrentPosition;

    @Override
    protected int initLayout() {
        return R.layout.local_playback;
    }

    @Override
    protected void initView() {

        tlTitleLayout.setTitle(getString(R.string.camera_video_play));
        Bundle extras = this.getIntent().getExtras();
        VIDEO_FILES = extras.getStringArrayList("videos");
        mPosition = extras.getInt("position");
        mSize = extras.getInt("size");
        path = VIDEO_FILES.get(mPosition);
        initialWidgets();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(r);
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handler.removeCallbacks(r);
        initialWidgets();
    }

    @SuppressWarnings("deprecation")
    private void initialWidgets() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            TranslucentStatus.setTranslucentStatus(true, this);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.top_bar);//通知栏所需颜色
//        }
//        setContentView(R.layout.local_playback);

        mVideoView = (VideoView) findViewById(R.id.videoView);
        mSeekBar = (SeekBar) findViewById(R.id.sbVideo);
        btnPlayPause = (ImageButton) findViewById(R.id.btn_playpause);
        tvCurrentTime = (TextView) findViewById(R.id.txt_current);
        tvTotalTime = (TextView) findViewById(R.id.txt_total);
        layoutButtonBar = (LinearLayout) findViewById(R.id.button_bar);
        layoutSeekingBar = (RelativeLayout) findViewById(R.id.seeking_bar);
        titleLayout = (TitleLayout) findViewById(R.id.title);
        titleLayout.setTitle(getString(R.string.video_play));

        mGestureDetector = new GestureDetector(this);
        mVideoView.setOnTouchListener(this);
        mVideoView.setLongClickable(true);
        mGestureDetector.setIsLongpressEnabled(true);

        btnPlayPause.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mVideoView.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(FormatTime(progress));
                    mVideoView.seekTo(progress);
                }
            }
        });

        mVideoView.setVideoPath(path);
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return true;
            }
        });
        if (mIsPlaying) {
            mVideoView.seekTo(mCurrentPosition);
        }
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                tvTotalTime.setText(FormatTime(mVideoView.getDuration()));
                mVideoView.start();
                mVideoView.pause();
                //mIsPlaying = true;
//                btnPlayPause.setBackgroundResource(R.drawable.btn_play);
                btnPlayPause.setSelected(true);
                mSeekBar.setMax(mVideoView.getDuration());
                updateSeekBar();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(r);
                mSeekBar.setProgress(mVideoView.getDuration());
//                btnPlayPause.setBackgroundResource(R.drawable.btn_play);
                btnPlayPause.setSelected(true);
            }
        });
    }

    private void updateSeekBar() {
        handler.post(r);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            mCurrentPosition = mVideoView.getCurrentPosition();
            mSeekBar.setProgress(mCurrentPosition);
            tvCurrentTime.setText(FormatTime(mCurrentPosition));

            if (!mVideoView.isPlaying() && mCurrentPosition == 0) {
                player_not_support();
                return;
            }

            //没有更新到状态
            if (!mVideoView.isPlaying()) {
//                btnPlayPause.setBackgroundResource(R.drawable.btn_play);
                btnPlayPause.setSelected(true);
                mIsPlaying = false;

                return;
            }

            Log.i("gwq", "postion=" + mCurrentPosition);
            handler.postDelayed(r, 100);
        }
    };

    private String FormatTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;

        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_playpause:

                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    btnPlayPause.setSelected(true);
                    mIsPlaying = false;
                    handler.removeCallbacks(r);
                } else {
                    mVideoView.start();
                    btnPlayPause.setSelected(false);
                    mIsPlaying = true;
                    updateSeekBar();


                }
                break;
        }
    }


    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        mIsFling = false;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mIsFling = true;
        handler.removeCallbacks(r);
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling left
            if (mPosition == (mSize - 1)) {
                mPosition = 0;
                path = VIDEO_FILES.get(mPosition);
                mVideoView.setVideoPath(path);
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        tvTotalTime.setText(FormatTime(mVideoView.getDuration()));
                        mVideoView.start();
                        mIsPlaying = true;
                        btnPlayPause.setSelected(true);
                        mSeekBar.setMax(mVideoView.getDuration());
                        updateSeekBar();
                    }
                });
            } else {
                mPosition += 1;
                path = VIDEO_FILES.get(mPosition);
                mVideoView.setVideoPath(path);
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        tvTotalTime.setText(FormatTime(mVideoView.getDuration()));
                        mVideoView.start();
                        mIsPlaying = true;
                        btnPlayPause.setSelected(false);
                        mSeekBar.setMax(mVideoView.getDuration());
                        updateSeekBar();
                    }
                });
            }
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling right
            if (mPosition == 0) {
                mPosition = mSize - 1;
                path = VIDEO_FILES.get(mPosition);
                mVideoView.setVideoPath(path);
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        tvTotalTime.setText(FormatTime(mVideoView.getDuration()));
                        mVideoView.start();
                        mIsPlaying = true;
//                        btnPlayPause.setBackgroundResource(R.drawable.btn_pause);
                        btnPlayPause.setSelected(false);

                        mSeekBar.setMax(mVideoView.getDuration());
                        updateSeekBar();
                    }
                });
            } else {
                mPosition -= 1;
                path = VIDEO_FILES.get(mPosition);
                mVideoView.setVideoPath(path);
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        tvTotalTime.setText(FormatTime(mVideoView.getDuration()));
                        mVideoView.start();
                        mIsPlaying = true;
//                        btnPlayPause.setBackgroundResource(R.drawable.btn_pause);
                        btnPlayPause.setSelected(false);


                        mSeekBar.setMax(mVideoView.getDuration());
                        updateSeekBar();
                    }
                });
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        Configuration cfg = getResources().getConfiguration();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE && !mIsFling) {
                    if (mIsBarShowing) {
                        layoutSeekingBar.startAnimation(AnimationUtils.loadAnimation(LocalPlaybackActivity.this, R.anim.topbar_slide_hide));
                        layoutSeekingBar.setVisibility(View.INVISIBLE);
                        layoutButtonBar.startAnimation(AnimationUtils.loadAnimation(LocalPlaybackActivity.this, R.anim.bottombar_slide_hide));
                        layoutButtonBar.setVisibility(View.INVISIBLE);
                    } else {
                        layoutSeekingBar.startAnimation(AnimationUtils.loadAnimation(LocalPlaybackActivity.this, R.anim.topbar_slide_show));
                        layoutSeekingBar.setVisibility(View.VISIBLE);
                        layoutButtonBar.startAnimation(AnimationUtils.loadAnimation(LocalPlaybackActivity.this, R.anim.bottombar_slide_show));
                        layoutButtonBar.setVisibility(View.VISIBLE);
                    }
                    mIsBarShowing = !mIsBarShowing;
                }
                break;
        }
        return true;
    }

    private void player_not_support() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.mxtech.videoplayer.ad");
        if (intent == null) {
            Builder adbNoPlayer = new Builder(LocalPlaybackActivity.this);
            adbNoPlayer.setTitle(getString(R.string.dialog_no_app));
            adbNoPlayer.setMessage(getString(R.string.txt_intent_to_app));
            adbNoPlayer.setPositiveButton(R.string.ok, intent_to_googleplay);
            adbNoPlayer.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            //adbNoPlayer.show();
        } else {
            Toast.makeText(LocalPlaybackActivity.this, getString(R.string.txt_intent_to_app), Toast.LENGTH_LONG).show();
            intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(path);
            intent.setDataAndType(Uri.fromFile(file), "video/*");

            startActivity(intent);
            finish();
//			startActivityForResult(intent, RESULT_OK);
        }
    }

    private DialogInterface.OnClickListener intent_to_googleplay = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Uri uri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            finish();
        }
    };
}

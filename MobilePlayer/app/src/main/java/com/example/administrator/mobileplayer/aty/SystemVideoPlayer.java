package com.example.administrator.mobileplayer.aty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.LocalVideo;
import com.example.administrator.mobileplayer.util.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIA_CONTROLLER = 2;
    private RelativeLayout mediaController;
    private VideoView vvVideoView;
    private LinearLayout llTop;
    private TextView tvLocalVideoName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnSoundSet;
    private SeekBar sbSoundSet;
    private Button btnPlayerSwitcher;
    private LinearLayout llBottom;
    private Button btnExitPlay;
    private Button btnPreviousPlay;
    private Button btnStartOrPausePlay;
    private Button btnIsFullOfScreen;
    private Button btnNextPlay;
    private TextView tvLocalVideoCurrentTime;
    private SeekBar sbVideocurrentTime;
    private TextView tvLocalVideoDuration;
    private DateUtil dateUtil;
    private BatteryReceiver batteryReceiver;
    private List<LocalVideo> localVideos;
    private int position;
    private GestureDetector gestureDetector;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_MEDIA_CONTROLLER:
                    mediaController.setVisibility(View.GONE);
                    isMediaControllerShow = false;
                    break;

                case PROGRESS:
                    int videoCurrentPosition = vvVideoView.getCurrentPosition();
                    sbVideocurrentTime.setProgress(videoCurrentPosition);
                    tvLocalVideoCurrentTime.setText(dateUtil.stringForTime(videoCurrentPosition));
                    //当前进度更新之后才撤回消息延时一秒重新发送。
                    setSystemTime();
                    //利用一秒间隔，刚好可以更新时间

                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1 * 1000);
                    break;
            }
        }
    };
    private Uri uri;
    private boolean isMediaControllerShow;

    private void setSystemTime() {
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        tvSystemTime.setText(simpleDateFormat.format(new Date()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();//设置电量等基础信息

        getVideoData();

        setVideoData();

        setPlayer();//监听最好放在得到数据和设置数据之后。
    }

    private void setBtnStatus() {
        if (localVideos.size() == 1) {
            setBtnPreviousVideoGray();
            setBtnNextVideoGray();
        } else if (localVideos.size() == 2) {
            if (position == 0) {
                setBtnPreviousVideoGray();
                setBtnNextVideoLigth();
            } else if (position == 1) {
                setBtnNextVideoGray();
                setBtnPreviousVideoLigth();
            }
        } else {
            if (position == 0) {
                setBtnPreviousVideoGray();
                setBtnNextVideoLigth();
            } else if (position == localVideos.size() - 1) {
                setBtnNextVideoGray();
                setBtnPreviousVideoLigth();
            } else {
                setBtnPreviousVideoLigth();
                setBtnNextVideoLigth();
            }
        }
    }

    private void setPlayer() {
        //设置系统默认的控制面板，根据手机而有所不同，不稳定
//        vvVideoView.setMediaController(new MediaController(this));
        vvVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vvVideoView.start();
                int duration = vvVideoView.getDuration();
                tvLocalVideoDuration.setText(dateUtil.stringForTime(duration));
                sbVideocurrentTime.setMax(duration);
                tvLocalVideoName.setText(localVideos.get(position).getName());
                handler.sendEmptyMessage(PROGRESS);
            }
        });

        vvVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(SystemVideoPlayer.this, "播放出错！", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        vvVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_start_play_selector);

                new AlertDialog.Builder(SystemVideoPlayer.this).setTitle("提示：播放结束！").setMessage("是否播放下一个？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (position == localVideos.size() - 1) {
                            Toast.makeText(SystemVideoPlayer.this, "后面没有视频了！", Toast.LENGTH_SHORT).show();
                        } else {
                            playNextVideo();
                        }
                    }
                }).setNegativeButton("取消", null).show();
            }
        });


        sbVideocurrentTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    vvVideoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4*1000);
            }
        });

    }

    private void initView() {
//        在Activity的onCreate方法中的setContentView(myview)调用之前添加下面代码
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.aty_system_video_player);
        vvVideoView = (VideoView) findViewById(R.id.vv_videoView);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvLocalVideoName = (TextView) findViewById(R.id.tv_localVideoName);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_systemTime);
        btnSoundSet = (Button) findViewById(R.id.btn_soundSet);
        sbSoundSet = (SeekBar) findViewById(R.id.sb_soundSet);
        btnPlayerSwitcher = (Button) findViewById(R.id.btn_playerSwitcher);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        btnExitPlay = (Button) findViewById(R.id.btn_exitPlay);
        btnPreviousPlay = (Button) findViewById(R.id.btn_forwardPlay);
        btnStartOrPausePlay = (Button) findViewById(R.id.btn_startOrPausePlay);
        btnIsFullOfScreen = (Button) findViewById(R.id.btn_isFullOfScreen);
        tvLocalVideoCurrentTime = (TextView) findViewById(R.id.tv_localVideoCurrentTime);
        sbVideocurrentTime = (SeekBar) findViewById(R.id.sb_VideocurrentTime);
        tvLocalVideoDuration = (TextView) findViewById(R.id.tv_localVideoDuration);
        btnNextPlay = (Button) findViewById(R.id.btn_nextPlay);
        mediaController = (RelativeLayout) findViewById(R.id.media_controller);

        btnNextPlay.setOnClickListener(this);
        btnSoundSet.setOnClickListener(this);
        btnPlayerSwitcher.setOnClickListener(this);
        btnExitPlay.setOnClickListener(this);
        btnPreviousPlay.setOnClickListener(this);
        btnStartOrPausePlay.setOnClickListener(this);
        btnIsFullOfScreen.setOnClickListener(this);
    }

    private void setVideoData() {
        if (localVideos.size() > 0 && localVideos != null) {
            localVideos.get(position);
            vvVideoView.setVideoPath(localVideos.get(position).getData());

        } else if (uri != null) {
            vvVideoView.setVideoURI(uri);
        } else {
            Toast.makeText(this, "没有数据！", Toast.LENGTH_SHORT).show();
        }

        setBtnStatus();
    }

    private void getVideoData() {
        //放在监听之后和之前都可以，得到当前视频的uri，可以是网上的，也可是本地的
        uri = getIntent().getData();

        //得到视频列表和当前播放的位置
        localVideos = (List<LocalVideo>) getIntent().getSerializableExtra("localVideos");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {   //设置电量、工具类初始化，变量初始化等基本信息，放在onCreate()放法中，且在initView后面安全些。
        dateUtil = new DateUtil();
        batteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(SystemVideoPlayer.this, "长按", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                playOrPause();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                showOrHideMediaController();
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void showOrHideMediaController() {
        if (isMediaControllerShow) {
            isMediaControllerShow = false;
            mediaController.setVisibility(View.GONE);
        }else {
            isMediaControllerShow = true;
            mediaController.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4*1000);
        }
    }

    //gestureDetector需要得到触摸事件！
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);//设置电量
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 30) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 50) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 70) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 90) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSoundSet) {
        } else if (v == btnPlayerSwitcher) {

        } else if (v == btnExitPlay) {
            finish();
        } else if (v == btnPreviousPlay) {
            playPreviousVideo();
        } else if (v == btnNextPlay) {
            playNextVideo();
        } else if (v == btnStartOrPausePlay) {
            playOrPause();
        } else if (v == btnIsFullOfScreen) {

        }
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4*1000);
    }

    private void playPreviousVideo() {
        setBtnStatus();
        if (localVideos.size() > 0 && localVideos != null) {
            if (position - 1 >= 0) {
                position--;
                vvVideoView.setVideoPath(localVideos.get(position).getData());
            }
            setBtnStatus();
        } else if (uri != null) {
            setBtnNextVideoGray();
            setBtnPreviousVideoGray();
        }
    }

    private void playNextVideo() {
        setBtnStatus();
        if (localVideos.size() > 0 && localVideos != null) {
            if (position + 1 <= localVideos.size() - 1) {
                position++;
                vvVideoView.setVideoPath(localVideos.get(position).getData());
            }
            setBtnStatus();
        } else if (uri != null) {
            setBtnNextVideoGray();
            setBtnPreviousVideoGray();
        }
    }

    private void setBtnPreviousVideoLigth() {
        btnPreviousPlay.setBackgroundResource(R.drawable.btn_previous_play_selector);
        btnPreviousPlay.setEnabled(true);
    }

    private void setBtnNextVideoLigth() {
        btnNextPlay.setBackgroundResource(R.drawable.btn_next_play_selector);
        btnNextPlay.setEnabled(true);
    }

    private void setBtnNextVideoGray() {
        btnNextPlay.setBackgroundResource(R.drawable.btn_next_gray);
        btnNextPlay.setEnabled(false);
    }

    private void setBtnPreviousVideoGray() {
        btnPreviousPlay.setBackgroundResource(R.drawable.btn_pre_gray);
        btnPreviousPlay.setEnabled(false);
    }

    private void playOrPause() {
        if (vvVideoView.isPlaying()) {
            vvVideoView.pause();
            btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_start_play_selector);
        } else {
            vvVideoView.start();
            btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_pause_play_selector);
        }
    }

    @Override
    protected void onDestroy() {
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
        super.onDestroy();
    }
}

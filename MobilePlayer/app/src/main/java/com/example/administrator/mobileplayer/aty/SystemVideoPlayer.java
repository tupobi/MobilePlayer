package com.example.administrator.mobileplayer.aty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
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

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.LocalVideo;
import com.example.administrator.mobileplayer.util.DateUtil;
import com.example.administrator.mobileplayer.util.GetNetSpeed;
import com.example.administrator.mobileplayer.view.MyVideoView;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIA_CONTROLLER = 2;
    private static final int SHOW_SPEED_PROMPT = 3;
    private boolean isFullOfScreen = true;
    private RelativeLayout mediaController;
    private MyVideoView vvVideoView;
    private LinearLayout llTop;
    private TextView tvLocalVideoName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnMute;
    private SeekBar sbSoundSet;
    private Button btnPlayerSwitcher;
    private LinearLayout llBottom, ll_loadingVideoPrompt, ll_loadingVideoBackground;
    private Button btnExitPlay;
    private Button btnPreviousPlay;
    private Button btnStartOrPausePlay;
    private Button btnIsFullOfScreen;
    private Button btnNextPlay;
    private TextView tvLocalVideoCurrentTime;
    private TextView tvNetSpeedPrompt, tvLoadingVideoBgPrompt;
    private SeekBar sbVideocurrentTime;
    private TextView tvLocalVideoDuration;
    private DateUtil dateUtil;
    private BatteryReceiver batteryReceiver;
    private List<LocalVideo> localVideos;
    private int position;
    private GestureDetector gestureDetector;
    private DisplayMetrics displayMetrics;
    private int screenWidth, screenHeight;
    private AudioManager audioManager;
    private int currentVolume, maxValume;
    private boolean isUsingSystenLoadingVideo = true;
    private GetNetSpeed getNetSpeed;


    private int preVideoCurrentPosition;
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

                    //如果是网络视频开启缓冲
                    if (uri != null && localVideos == null) {
                        int buffer = vvVideoView.getBufferPercentage();
                        int totalBuffer = buffer * sbVideocurrentTime.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        sbVideocurrentTime.setSecondaryProgress(secondaryProgress);
                    }

                    if (!isUsingSystenLoadingVideo){
                    int buffer = videoCurrentPosition - preVideoCurrentPosition;
                    if (buffer < 500){
                        ll_loadingVideoPrompt.setVisibility(View.VISIBLE);
                    }else {
                        ll_loadingVideoPrompt.setVisibility(View.GONE);
                    }
                }
                    preVideoCurrentPosition = videoCurrentPosition;


                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1 * 1000);
                    break;

                case SHOW_SPEED_PROMPT:
                    String netSpeed = getNetSpeed.getNetSpeed(SystemVideoPlayer.this);
                    tvNetSpeedPrompt.setText("玩命加载中.." + netSpeed);
                    tvLoadingVideoBgPrompt.setText("玩命加载中.." + netSpeed);

                    removeMessages(SHOW_SPEED_PROMPT);
                    sendEmptyMessageDelayed(SHOW_SPEED_PROMPT, 2 * 1000);
                    break;
            }
        }
    };
    private Uri uri;
    private boolean isMediaControllerShow;
    private int videoWidth;
    private int videoHeight;
    private boolean isMute;

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
        if (localVideos != null) {
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
        } else if (uri != null) {
            setBtnPreviousVideoGray();
            setBtnNextVideoGray();
        }
    }

    private void setPlayer() {
        //设置系统默认的控制面板，根据手机而有所不同，不稳定
//        vvVideoView.setMediaController(new MediaController(this));
        vvVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vvVideoView.start();
                if (vvVideoView.isPlaying()) {
                    btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_pause_play_selector);
                }
                int duration = vvVideoView.getDuration();
                tvLocalVideoDuration.setText(dateUtil.stringForTime(duration));
                sbVideocurrentTime.setMax(duration);
                if (localVideos != null) {
                    sbVideocurrentTime.setSecondaryProgress(duration);
                    tvLocalVideoName.setText(localVideos.get(position).getName());
                } else if (uri != null) {
                    tvLocalVideoName.setText(uri.toString());
                }
                handler.sendEmptyMessage(PROGRESS);

                videoWidth = mediaPlayer.getVideoWidth();
                videoHeight = mediaPlayer.getVideoWidth();
                setVideoScreenType();
                ll_loadingVideoBackground.setVisibility(View.GONE);
            }
        });

        vvVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(SystemVideoPlayer.this, "播放出错！", Toast.LENGTH_SHORT).show();
                return false;//false显示对话框
            }
        });

        vvVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_start_play_selector);
                if (localVideos != null) {

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
                } else if (uri != null) {
                    Toast.makeText(SystemVideoPlayer.this, "播放结束！", Toast.LENGTH_SHORT).show();
                }
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
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
            }
        });

        sbSoundSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (i > 0) {
                        isMute = false;
                    } else {
                        isMute = true;
                    }
                    updateVolume(i, isMute);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
            }
        });

        if (isUsingSystenLoadingVideo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                vvVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                        switch (i) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                ll_loadingVideoPrompt.setVisibility(View.VISIBLE);
                                break;
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                ll_loadingVideoPrompt.setVisibility(View.GONE);
                                break;
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void updateVolume(int progress, boolean isMute) {
        if (isMute) {
            btnMute.setBackgroundResource(R.drawable.btn_mute_set_selector);
        } else {
            btnMute.setBackgroundResource(R.drawable.btn_sound_set_selector);
        }
//        currentVolume = progress;
        sbSoundSet.setProgress(progress);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    private void initView() {
//        在Activity的onCreate方法中的setContentView(myview)调用之前添加下面代码
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.aty_system_video_player);
        vvVideoView = (MyVideoView) findViewById(R.id.vv_videoView);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvLocalVideoName = (TextView) findViewById(R.id.tv_localVideoName);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_systemTime);
        btnMute = (Button) findViewById(R.id.btn_mute);
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
        ll_loadingVideoPrompt = (LinearLayout) findViewById(R.id.ll_loadingVideoPrompt);
        ll_loadingVideoBackground = (LinearLayout) findViewById(R.id.ll_loadingVideoBackground);
        tvNetSpeedPrompt = (TextView) findViewById(R.id.tv_netSpeedPrompt);
        tvLoadingVideoBgPrompt = (TextView) findViewById(R.id.tv_loadingVideoBgPrompt);

        btnNextPlay.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        btnPlayerSwitcher.setOnClickListener(this);
        btnExitPlay.setOnClickListener(this);
        btnPreviousPlay.setOnClickListener(this);
        btnStartOrPausePlay.setOnClickListener(this);
        btnIsFullOfScreen.setOnClickListener(this);
    }

    private void setVideoData() {
        if (localVideos != null && localVideos.size() > 0) {
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

    private void initData() {   //设置电量、音量、工具类初始化，变量初始化等基本信息，放在onCreate()放法中，且在initView后面安全些。
        dateUtil = new DateUtil();
        batteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
                Toast.makeText(SystemVideoPlayer.this, "长按", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
                playOrPause();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
                showOrHideMediaController();
                return super.onSingleTapConfirmed(e);
            }
        });

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxValume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sbSoundSet.setMax(maxValume);
        sbSoundSet.setProgress(currentVolume);
        getNetSpeed = new GetNetSpeed();
        handler.sendEmptyMessage(SHOW_SPEED_PROMPT);

    }

    private void showOrHideMediaController() {
        if (isMediaControllerShow) {
            isMediaControllerShow = false;
            mediaController.setVisibility(View.GONE);
        } else {
            isMediaControllerShow = true;
            mediaController.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
        }
    }

    private float startY, touchRang;
    private int mVolume;

    //只能放在外面。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //绑定手势识别器
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                startY = event.getY();
                mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);

                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                float distanceY = startY - endY;
                float volumeDelta = (distanceY / touchRang) * maxValume;
//                int volume = (int) Math.min(Math.max(mVolume + volumeDelta, 0), maxValume);
                currentVolume = (int) Math.min(Math.max(mVolume + volumeDelta, 0), maxValume);
                if (volumeDelta != 0) {
                    if (currentVolume != 0) {
                        isMute = false;
                    } else {
                        isMute = true;
                    }
                    updateVolume(currentVolume, isMute);
                }
                break;

            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
                break;
        }
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
        if (v == btnMute) {
            isMute = !isMute;
            updateVolume(currentVolume, isMute);
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
            isFullOfScreen = !isFullOfScreen;
            setVideoScreenType();
        }
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4 * 1000);
    }

    private void setVideoScreenType() {
        //            2、最新方法

//        LogUtil.e("screenWidth1:" + screenWidth + "screenHeight1:" + screenHeight);
        if (isFullOfScreen) {
            //得到屏幕的宽和高：
//            1、过时方法
//            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            vvVideoView.setVideoSize(screenWidth, screenHeight);
            btnIsFullOfScreen.setBackgroundResource(R.drawable.btn_default_screen_selector);
        } else {
            int width = screenWidth;
            int height = screenHeight;
            if (videoWidth * height < width * videoHeight) {
                width = height * videoWidth / videoHeight;
            } else {
                height = width * videoHeight / videoWidth;
            }
            vvVideoView.setVideoSize(width, height);
            btnIsFullOfScreen.setBackgroundResource(R.drawable.btn_full_screen_selector);

//            LogUtil.e("screenWidth:" + screenWidth + "screenHeight:" + screenHeight);
//            LogUtil.e("videoWidth:" + videoWidth + "videoHeight:" + videoHeight);


//            vvVideoView.setVideoSize(videoWidth, videoHeight);
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            if (currentVolume <= 0) {
                currentVolume = 0;
            } else {
            }
            if (currentVolume == 0) {
                isMute = true;
            } else {
                isMute = false;
            }
            updateVolume(currentVolume, isMute);
            return false;
            //return true,不显示系统音量
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            isMute = false;
            if (currentVolume >= maxValume) {
                currentVolume = maxValume;
            }
            updateVolume(currentVolume, isMute);
            return false;
            //return true,不显示系统音量
        }
        return super.onKeyDown(keyCode, event);
        //少了这个return系统无法监听一些按钮，比如回退
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

package com.example.administrator.mobileplayer.aty;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.util.DateUtil;

/**
 * Created by Administrator on 2017/5/13.
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRESS = 1;
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
    private Button btnForwardPlay;
    private Button btnStartOrPausePlay;
    private Button btnIsFullOfScreen;
    private Button btnNextPlay;
    private TextView tvLocalVideoCurrentTime;
    private SeekBar sbVideocurrentTime;
    private TextView tvLocalVideoDuration;
    DateUtil dateUtil;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    int videoCurrentPosition = vvVideoView.getCurrentPosition();
                    sbVideocurrentTime.setProgress(videoCurrentPosition);
                    tvLocalVideoCurrentTime.setText(dateUtil.stringForTime(videoCurrentPosition));
                    //当前进度更新之后才撤回消息延时一秒重新发送。

                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1*1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setPlayer();
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
                Toast.makeText(SystemVideoPlayer.this, "播放结束！", Toast.LENGTH_SHORT).show();
                btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_start_play_selector);
            }
        });


        sbVideocurrentTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    vvVideoView.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void initView() {
//        在Activity的onCreate方法中的setContentView(myview)调用之前添加下面代码
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        dateUtil = new DateUtil();

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
        btnForwardPlay = (Button) findViewById(R.id.btn_forwardPlay);
        btnStartOrPausePlay = (Button) findViewById(R.id.btn_startOrPausePlay);
        btnIsFullOfScreen = (Button) findViewById(R.id.btn_isFullOfScreen);
        tvLocalVideoCurrentTime = (TextView) findViewById(R.id.tv_localVideoCurrentTime);
        sbVideocurrentTime = (SeekBar) findViewById(R.id.sb_VideocurrentTime);
        tvLocalVideoDuration = (TextView) findViewById(R.id.tv_localVideoDuration);
        btnNextPlay = (Button) findViewById(R.id.btn_nextPlay);

        btnNextPlay.setOnClickListener(this);
        btnSoundSet.setOnClickListener(this);
        btnPlayerSwitcher.setOnClickListener(this);
        btnExitPlay.setOnClickListener(this);
        btnForwardPlay.setOnClickListener(this);
        btnStartOrPausePlay.setOnClickListener(this);
        btnIsFullOfScreen.setOnClickListener(this);

        //放在监听之后和之前都可以，得到当前视频的uri
        Uri uri = getIntent().getData();
        if (uri != null) {
            vvVideoView.setVideoURI(uri);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSoundSet) {
        } else if (v == btnPlayerSwitcher) {

        } else if (v == btnExitPlay) {

        } else if (v == btnForwardPlay) {

        } else if (v == btnNextPlay) {

        } else if (v == btnStartOrPausePlay) {
            if (vvVideoView.isPlaying()){
                vvVideoView.pause();
                btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_start_play_selector);
            }else {
                vvVideoView.start();
                btnStartOrPausePlay.setBackgroundResource(R.drawable.btn_pause_play_selector);
            }
        } else if (v == btnIsFullOfScreen) {

        }
    }

}

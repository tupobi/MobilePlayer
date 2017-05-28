package com.example.administrator.mobileplayer.aty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mobileplayer.IMusicPlayerService;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.entity.LocalAudio;
import com.example.administrator.mobileplayer.service.MusicPlayerService;
import com.example.administrator.mobileplayer.util.CacheUtils;
import com.example.administrator.mobileplayer.util.DateUtil;
import com.example.administrator.mobileplayer.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/22.
 */

public class SystemAudioPlayer extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private TextView tvAudioArtist;
    private TextView tvAudioName, tvAudioDuration;
    private LinearLayout llBottomAudioController;
    private Button btnAudioPlayMode;
    private Button btnAudioPre;
    private Button btnAudioPlayOrPause;
    private Button btnAudioNext;
    private Button btnAudioLyrics;
    private SeekBar sbAudioCurrentTime;
    private ImageView ivNowPlayingMatrix;
    private int position;
    private IMusicPlayerService iMusicPlayerService;    //服务的代理类，通过该方法调用服务的方法
    private ArrayList<LocalAudio> localAudios;
//    private SetAudioDataReceiver registerReceiver;
//    private PlayCompleteReceiver playCompleteReceiver;
    private boolean isPlaying = true;
    private DateUtil dateUtil;
    /**
     * true从状态栏进入的，不需要重新播放
     * false从列表进入，需要切换播放
     */
    private boolean notification;

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMusicPlayerService = IMusicPlayerService.Stub.asInterface(iBinder);
            if (iMusicPlayerService != null) {
                try {
                    if (!notification) {//不从通知栏进来，1、选中列表中其他歌曲需要重新播放，2、相同歌曲，不重新播放
                        if (position == CacheUtils.getCurrentPlayingMusicPosition(SystemAudioPlayer.this, CacheUtils.CURRENT_PLAYING_MUSIC_POSITION)) {
                            //存在bug！！！！！！！！！！判断进程是否被杀死，被杀死的话记录播放位置或者重新播放，否则会崩溃。
                            //已解决，localAudio没有数据，没有数据musicName返回null，重新播放
                            //这样的话，不能记录上次播放的位置，只能重新播放
                            if (iMusicPlayerService.getMusicName() != null) {
                                setAudioData();
                            } else {
                                iMusicPlayerService.openAudio(position);
                                CacheUtils.putCurrentPlayingMusicPosition(SystemAudioPlayer.this, CacheUtils.CURRENT_PLAYING_MUSIC_POSITION, position);
                            }

                        } else {
                            iMusicPlayerService.openAudio(position);
                            CacheUtils.putCurrentPlayingMusicPosition(SystemAudioPlayer.this, CacheUtils.CURRENT_PLAYING_MUSIC_POSITION, position);
                        }
                    } else {
                        setAudioData();//在主线程中执行
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                if (iMusicPlayerService != null) {
                    iMusicPlayerService.stop();
                    iMusicPlayerService = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v == btnAudioPlayMode) {
            // Handle clicks for btnAudioPlayMode
            setPlayMode();
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            if (iMusicPlayerService != null){
                try {
                    iMusicPlayerService.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioPlayOrPause) {
            // Handle clicks for btnAudioPlayOrPause
            if (iMusicPlayerService != null) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    setPlayOrPauseBtn();
                } else {
                    setPlayOrPauseBtn();
                }
            }
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            if (iMusicPlayerService != null) {
                try {
                    iMusicPlayerService.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioLyrics) {
            // Handle clicks for btnAudioLyrics
        }
    }

    private void setPlayMode() {
        if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.ORDER_NOMAL) {
            CacheUtils.putPlayMode(this, CacheUtils.PLAY_MODE, MusicPlayerService.REPEAT_ALL);
            btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_allrepeat_mode_selector);
            Toast.makeText(this, "全部循环", Toast.LENGTH_SHORT).show();
        } else if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPEAT_ALL) {
            CacheUtils.putPlayMode(this, CacheUtils.PLAY_MODE, MusicPlayerService.REPAET_SINGLE);
            btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_singlerepeat_mode_selector);
            Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
        } else if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPAET_SINGLE) {
            CacheUtils.putPlayMode(this, CacheUtils.PLAY_MODE, MusicPlayerService.ORDER_NOMAL);
            btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_order_mode_selector);
            Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPlayOrPauseBtn() {
        if (isPlaying) {
            btnAudioPlayOrPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            try {
                iMusicPlayerService.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            btnAudioPlayOrPause.setBackgroundResource(R.drawable.btn_audio_play_selector);
            try {
                iMusicPlayerService.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getData();
        initData();
        bindAndStartService();
    }

    private void initData() {

        isPlaying = true;
        btnAudioPlayOrPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);

        dateUtil = new DateUtil();
//        registerReceiver = new SetAudioDataReceiver();
//        playCompleteReceiver = new PlayCompleteReceiver();

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPEN_AUDIO);

//        IntentFilter playCompleteIntentFilter = new IntentFilter();
//        playCompleteIntentFilter.addAction(MusicPlayerService.PLAY_COMPLETE);

//        registerReceiver(registerReceiver, intentFilter);
//        registerReceiver(playCompleteReceiver, playCompleteIntentFilter);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 0)
    public void onReceiveSetData(LocalAudio localAudio){        //参数为订阅标志
        setAudioData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 1)
    public void onReceiveSetPlayOrPauseBtn(ArrayList<LocalAudio> localAudios){
        LogUtil.e("1111");
        isPlaying = false;
        btnAudioPlayOrPause.setBackgroundResource(R.drawable.btn_audio_play_selector);
    }

//    class SetAudioDataReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            setAudioData();//列表进入开始播放并且设置歌曲信息
//        }
//    }

//    class PlayCompleteReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            isPlaying = false;
//            btnAudioPlayOrPause.setBackgroundResource(R.drawable.btn_audio_play_selector);
//        }
//    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = iMusicPlayerService.getCurrentPostion();
                        sbAudioCurrentTime.setProgress(currentPosition);
                        tvAudioDuration.setText(dateUtil.stringForTime(currentPosition) + "/" + dateUtil.stringForTime(iMusicPlayerService.getDuration()));

                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS, 1 * 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private void setAudioData() {
        try {
            tvAudioName.setText(iMusicPlayerService.getMusicName());
            tvAudioArtist.setText(iMusicPlayerService.getArtist());
            tvAudioDuration.setText(dateUtil.stringForTime(iMusicPlayerService.getDuration()));
            sbAudioCurrentTime.setMax(iMusicPlayerService.getDuration());

            if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.ORDER_NOMAL) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_order_mode_selector);
            } else if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_allrepeat_mode_selector);
            } else if (CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPAET_SINGLE) {
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_play_singlerepeat_mode_selector);
            }

            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.Jay.mobilePlayer_OPEN_AUDIO");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);   //多次调用服务不会重复启动
    }

    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
//        localAudios = (ArrayList<LocalAudio>) getIntent().getSerializableExtra("localAudios");

    }


    private void initView() {
        setContentView(R.layout.aty_system_audio_player);
        ivNowPlayingMatrix = (ImageView) findViewById(R.id.iv_nowPlayingMatrix);
        ivNowPlayingMatrix.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivNowPlayingMatrix.getBackground();
        animationDrawable.start();

        tvAudioArtist = (TextView) findViewById(R.id.tv_audioArtist);
        tvAudioName = (TextView) findViewById(R.id.tv_audioName);
        llBottomAudioController = (LinearLayout) findViewById(R.id.ll_bottomAudioController);
        btnAudioPlayMode = (Button) findViewById(R.id.btn_audio_playMode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioPlayOrPause = (Button) findViewById(R.id.btn_audioPlayOrPause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnAudioLyrics = (Button) findViewById(R.id.btn_audioLyrics);
        sbAudioCurrentTime = (SeekBar) findViewById(R.id.sb_audioCurrentTime);
        tvAudioDuration = (TextView) findViewById(R.id.tv_audioDuration);

        btnAudioPlayMode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioPlayOrPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioLyrics.setOnClickListener(this);

        sbAudioCurrentTime.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

//        if (registerReceiver != null) {
//            unregisterReceiver(registerReceiver);
//            registerReceiver = null;        //将广播接收器置为null，便于垃圾回收器优先回收。
//        }

//        if (playCompleteReceiver != null){
//            unregisterReceiver(playCompleteReceiver);
//            playCompleteReceiver = null;
//        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        //解除绑定服务
        if (con != null){
            unbindService(con);
            con = null;
        }
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                try {
                    iMusicPlayerService.seekTo(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}

package com.example.administrator.mobileplayer.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.administrator.mobileplayer.IMusicPlayerService;
import com.example.administrator.mobileplayer.R;
import com.example.administrator.mobileplayer.aty.SystemAudioPlayer;
import com.example.administrator.mobileplayer.entity.LocalAudio;
import com.example.administrator.mobileplayer.util.CacheUtils;
import com.example.administrator.mobileplayer.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/22.
 */

public class MusicPlayerService extends Service {
    /**
     * 播放模式
     */
    public static final int ORDER_NOMAL = 1;
    public static final int REPEAT_ALL = 2;
    public static final int REPAET_SINGLE = 3;
    /**
     * 广播提示，从列表打开一个音乐
     */
    public static final String OPEN_AUDIO = "com.Jay.OPEN_AUDIO";
    public static final String PLAY_COMPLETE = "PLAY_COMPLETE";

    ArrayList<LocalAudio> localAudios;
    private int position;
    private LocalAudio localAudio;
    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //加载音乐列表

        localAudios = new ArrayList<>();
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频名
                        MediaStore.Audio.Media.DURATION,//时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//视频的播放地址
                        MediaStore.Audio.Media.ARTIST//艺术家
                };
                Cursor cursor = contentResolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        localAudios.add(new LocalAudio(cursor.getString(0), cursor.getLong(1), cursor.getLong(2),
                                cursor.getString(3), cursor.getString(4)));
                    }
                    LogUtil.e(localAudios.size() + "");
                    cursor.close();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPostion() throws RemoteException {
            return service.getCurrentPostion();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getMusicName() throws RemoteException {
            return service.getMusicName();
        }

        @Override
        public String getMusicPath() throws RemoteException {
            return service.getMusicPath();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

//        @Override
//        public void setPlayMode(int playMode) throws RemoteException {
//            service.setPlayMode(playMode);
//        }
//
//        @Override
//        public int getPlayMode() throws RemoteException {
//            return service.getPlayMode();
//        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }
    };

    private void openAudio(int position) {
        this.position = position;
        if (localAudios != null && localAudios.size() > 0) {
            localAudio = localAudios.get(position);

            if (mediaPlayer != null) {
//                mediaPlayer.release();  //播放一个，释放掉之前的
                mediaPlayer.reset();//和上面重复了
            }

            mediaPlayer = new MediaPlayer();

            setPlayListener();
        } else {
            Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPlayListener() {
        try {
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());

            mediaPlayer.setOnErrorListener(new MyOnErrorListener());

            mediaPlayer.setDataSource(localAudio.getData());
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyAudioData(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //通知播放页面获取音频信息。
//            notifyAudioData(OPEN_AUDIO);

            EventBus.getDefault().post(localAudio);
            start();
        }
    }

    private void notifyPlayComplete(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (CacheUtils.getPlayMode(MusicPlayerService.this, CacheUtils.PLAY_MODE) == MusicPlayerService.ORDER_NOMAL){
                //如果不是最后一个，播放下一个，是最后一个，停止播放并发送播放完成广播，将播放按钮设置为暂停
                if (position == localAudios.size()-1){
//                    notifyPlayComplete(PLAY_COMPLETE);
                    boolean tag = false;
                    EventBus.getDefault().post(localAudios);
                }else {
                    next();
                }
            }else if (CacheUtils.getPlayMode(MusicPlayerService.this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPEAT_ALL){
                next();
            }else if (CacheUtils.getPlayMode(MusicPlayerService.this, CacheUtils.PLAY_MODE) == MusicPlayerService.REPAET_SINGLE){
                openAudio(position);
            }
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();
            return true;
        }
    }

    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        mediaPlayer.start();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, SystemAudioPlayer.class);
        intent.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this).
                setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("Jay音乐")
                .setContentText("正在播放：" + getMusicName())
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(1, notification);
    }


    private void pause() {
        mediaPlayer.pause();
        notificationManager.cancel(1);
    }

    private void stop() {

    }

    private int getCurrentPostion() {
        return mediaPlayer.getCurrentPosition();
    }

    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    private String getArtist() {
        return localAudio.getArtist();
    }

    private String getMusicName() {
        if (localAudio != null) {
            return localAudio.getName();
        }else {
            return null;
        }
    }

    private String getMusicPath() {
        return localAudio.getData();
    }

    private void pre() {
        if (position == 0){
            position = localAudios.size()-1;
        }else {
            position--;
        }
        CacheUtils.putCurrentPlayingMusicPosition(this, CacheUtils.CURRENT_PLAYING_MUSIC_POSITION, position);
        openAudio(position);
    }

    private void next() {
        if (position == localAudios.size()-1){
            position = 0;
        }else {
            position++;
        }
        CacheUtils.putCurrentPlayingMusicPosition(this, CacheUtils.CURRENT_PLAYING_MUSIC_POSITION, position);
        openAudio(position);
    }

//    private void setPlayMode(int playMode) {
//        CacheUtils.putPlayMode(this, CacheUtils.PLAY_MODE, playMode);
//    }
//
//    private int getPlayMode() {
//        return CacheUtils.getPlayMode(this, CacheUtils.PLAY_MODE);
//    }

    private boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }



}

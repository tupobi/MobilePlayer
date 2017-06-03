package com.example.administrator.mobileplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.mobileplayer.service.MusicPlayerService;

/**
 * Created by Administrator on 2017/5/24.
 */

public class CacheUtils {
    public static final String PLAY_MODE = "PLAY_MODE";//SharedPreferences的key
    public static final String CURRENT_PLAYING_MUSIC_POSITION = "CURRENT_PLAYING_MUSIC_POSITION"; //当前正在播放的音乐的位置的key
    public static final String NET_AUDIO_JSON_DATA = "NET_AUDIO_JSON_DATA";

    public static void putPlayMode(Context context, String key, int playMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, playMode).commit();
    }

    public static int getPlayMode(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, MusicPlayerService.ORDER_NOMAL);//第二个参数为默认的情况得到的值
    }

    public static void putCurrentPlayingMusicPosition(Context context, String key, int currentPlayingMusicPosition) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, currentPlayingMusicPosition).commit();
    }

    public static int getCurrentPlayingMusicPosition(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);//第二个参数为默认的情况得到的值
    }

    public static void putNetAudioJsonData(Context context, String key, String netAudioJsonData){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, netAudioJsonData).commit();
    }

    public static String getNetAudioJsonData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Jay", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");//第二个参数为默认的情况得到的值
    }

}

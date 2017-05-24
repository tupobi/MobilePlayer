// IMusicPlayerService.aidl
package com.example.administrator.mobileplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

    void openAudio(int position);

    void start();

    void pause();

    void stop();

    int  getCurrentPostion();

    int getDuration();

    String getArtist();

    String getMusicName();

    String getMusicPath();

    void pre();

    void next();

//    void setPlayMode(int playMode);
//
//    int getPlayMode();

    boolean isPlaying();

    void seekTo(int position);

}

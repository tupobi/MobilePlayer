package com.example.administrator.mobileplayer.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/13.
 */

public class NetVideo implements Serializable {
    private String name;
    private long duration;
    private long size;
    private String data;
    private String artist;
    private String summary, coverImgUrl;

    public NetVideo(){

    }

    public NetVideo(String name, long duration, long size, String data, String artist) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "NetVideo{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", summary='" + summary + '\'' +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                '}';
    }

}

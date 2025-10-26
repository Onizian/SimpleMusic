package com.example.myapplication.ui.musichome.POJO;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class MusicInfo extends LitePalSupport implements Serializable {

    private long id;
    private String title;//音乐名称
    private String data;//音乐文件
    private String album;//专辑
    private String artist;//艺人
    private int duration;//音乐时长
    private long size;//音乐文件大小
    private boolean isCheck;//选中情况


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean getCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck() {
        return isCheck;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", isCheck=" + isCheck +
                '}';
    }
}

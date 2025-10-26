package com.example.myapplication.ui.musichome.POJO;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicListInfo extends LitePalSupport implements Serializable {
    long id;//歌单的id
    String tablename;//歌单名称
    String tableprofile;//歌单简介
    String tableimg;//歌单图片
    List<SongListInfo> songListInfoList = new ArrayList<>();//歌单内容


    public String getTableimg() {
        return tableimg;
    }

    public void setTableimg(String tableimg) {
        this.tableimg = tableimg;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTableprofile() {
        return tableprofile;
    }

    public void setTableprofile(String tableprofile) {
        this.tableprofile = tableprofile;
    }

    public List<SongListInfo> getSonglistInfoList() {
        return songListInfoList;
    }

    public void setSonglistInfoList(List<SongListInfo> songListInfoList) {
       this.songListInfoList = songListInfoList;
    }

    @Override
    public String toString() {
        return "MusicListInfo{" +
                "id=" + id +
                ", tablename='" + tablename + '\'' +
                ", tableprofile='" + tableprofile + '\'' +
                ", tableimg='" + tableimg + '\'' +
                ", songlistInfoList=" + songListInfoList +
                '}';
    }
}

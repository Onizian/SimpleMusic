package com.example.myapplication.ui.musichome.utils;

import com.example.myapplication.ui.musichome.POJO.MusicInfo;

import org.litepal.LitePal;

import java.util.List;

public class SearchUtils {

    public List<MusicInfo> findTitle(String searchtext) {
        List<MusicInfo> musicInfoList = LitePal.where("title like ?", "%"+searchtext+"%").find(MusicInfo.class);
        return musicInfoList;
    }

    public List<MusicInfo> findAlbum(String searchtext) {
        List<MusicInfo> musicInfoList = LitePal.where("album like ?", "%"+searchtext+"%").find(MusicInfo.class);
        return musicInfoList;
    }

    public List<MusicInfo> findartist(String searchtext) {
        List<MusicInfo> musicInfoList = LitePal.where("artist like ?", "%"+searchtext+"%").find(MusicInfo.class);
        return musicInfoList;
    }


}

package com.example.myapplication.ui.musichome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.musichome.POJO.MusicInfo;

import java.util.List;

public class MusicHomeViewModel extends ViewModel {
    //当前位置
    private final MutableLiveData<Integer> mCurrentposition;
    //当前是否播放
    private final MutableLiveData<Boolean> isplay;

    private final MutableLiveData<List<MusicInfo>> musicInfoList;

    public MusicHomeViewModel() {
        mCurrentposition = new MutableLiveData<>();
        isplay = new MutableLiveData<>();
        musicInfoList = new MutableLiveData<>();
        mCurrentposition.setValue(0);
        isplay.setValue(false);
    }

    public void setMusicInfoList(List<MusicInfo> musicInfos) {
        musicInfoList.setValue(musicInfos);
    }

    public LiveData<List<MusicInfo>> getMusicListInfo() {
        return musicInfoList;
    }

    /**
    * 设置当前位置
    * */
    public void setmCurrentposition(int position) {
        mCurrentposition.setValue(position);
        System.out.println("modelview.mCurrentposition==" + mCurrentposition.getValue() +"----"+position);
    }

    /**
    * 设置当前是否播放
    * */
    public void setIsplay(boolean b) {
        isplay.setValue(b);
        System.out.println("modelview.isplay==" + isplay.getValue()+ "----" + b);
    }

    /**
    * 得到当前位置
    * */
    public LiveData<Integer> getmCurrentposition() {
        return mCurrentposition;
    }

    /**
    * 得到当前播放状态
    * */
    public LiveData<Boolean> getIsplay() {
        return isplay;
    }

}

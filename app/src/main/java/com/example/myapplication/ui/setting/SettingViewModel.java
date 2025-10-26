package com.example.myapplication.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    private final MutableLiveData<Boolean> timeisplay;

    public SettingViewModel() {
        mText = new MutableLiveData<>();
        timeisplay = new MutableLiveData<>();
    }

    public void setaddmText(String mtime) {
        int time = Integer.parseInt(mtime);
        if (time<9) {
            mText.setValue(time+1+"");
        } else {
            mText.setValue(0+"");
        }
    }

    public void setdownmText(String mtime) {
        int time = Integer.parseInt(mtime);
        if (time>0) {
            mText.setValue(time-1 + "");
        } else {
            mText.setValue(9 + "");
        }
    }

    public void setTimeisplay(boolean t) {
        timeisplay.setValue(t);
    }

    public LiveData<Boolean> gettimeisplay() {
        return timeisplay;
    }




    public LiveData<String> getText() {
        return mText;
    }
}

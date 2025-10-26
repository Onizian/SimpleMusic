package com.example.myapplication.ui.softversion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SoftVersionViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SoftVersionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("这是软件版本");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

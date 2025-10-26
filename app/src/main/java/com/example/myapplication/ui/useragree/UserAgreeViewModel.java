package com.example.myapplication.ui.useragree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserAgreeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UserAgreeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("这是用户协议");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

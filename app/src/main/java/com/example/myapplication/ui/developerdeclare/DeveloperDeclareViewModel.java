package com.example.myapplication.ui.developerdeclare;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeveloperDeclareViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DeveloperDeclareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("这是开发者声明");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

package com.example.myapplication.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.user.POJO.User;
import com.example.myapplication.ui.user.Utils.SqlUtils;

public class UserViewModel extends ViewModel {
    //当前登录的账号信息
    private final MutableLiveData<User> user;

    public UserViewModel() {
        user = new MutableLiveData<>();
    }

    /**
    * 获得当前登录的账号信息
    * */
    public LiveData<User> getUser() {
        SqlUtils sqlUtils = new SqlUtils();
        user.setValue(sqlUtils.SelectloginUser());
        return user;
    }
}

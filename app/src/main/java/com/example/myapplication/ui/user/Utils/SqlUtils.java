package com.example.myapplication.ui.user.Utils;

import com.example.myapplication.ui.user.POJO.User;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SqlUtils {
    /**
    * 查询账号对应的数据
    * */
    public List<User> findNumber(String UserNumber) {
        List<User> userList = LitePal.where("UserNumber=? ", UserNumber).find(User.class);
        return userList;
    }
    /**
    * 查询账号和密码对应的数据
    * */
    public List<User> findPassword(String UserPassword, String UserNumber) {
        List<User> userList = LitePal.where("UserNumber=? and UserPassword=? ", UserNumber, UserPassword).find(User.class);
        return userList;
    }
    /**
    * 查找密保问题
    * */
    public String findSoftQuestion(String UserNumber) {
        List<User> userList = findNumber(UserNumber);
        String question = userList.get(0).getSoftQuestion();
        return question;
    }

    /**
    * 更新密码
    * */
    public boolean UpdataPassword(String UserNumber, String UserPassword) {
        List<User> userList = findNumber(UserNumber);
        User user = LitePal.find(User.class, userList.get(0).getId());
        user.setUserPassword(UserPassword);
        user.save();
        return user.isSaved();
    }

    /**
    * 查询目前登录的账号
    * */
    public User SelectloginUser() {
        List<User> userList = LitePal.where("islogin = ?", "1").find(User.class);
        return userList.get(0);
    }

    /**
    * 判断当前是否有账号登录
     * false 默认为0； true 默认为1；
    * */
    public boolean Selectislogin() {
        List<User> userList = LitePal.where("islogin = ?", "1").find(User.class);
        if (userList.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }

}

package com.example.myapplication.ui.user.POJO;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class User extends LitePalSupport implements Serializable {

    private long id;//id
    private String UserNumber;//账号
    private String UserPassword;//密码
    private String Email;//邮箱
    private String SoftQuestion;//密保问题
    private String SoftAnswer;//密保答案
    private String Sex;//性别
    private String photoURL;//头像
    private String usersignature;//个性签名
    private boolean islogin;//是否登录

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", UserNumber='" + UserNumber + '\'' +
                ", UserPassword='" + UserPassword + '\'' +
                ", Email='" + Email + '\'' +
                ", SoftQuestion='" + SoftQuestion + '\'' +
                ", SoftAnswer='" + SoftAnswer + '\'' +
                ", Sex='" + Sex + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", usersignature='" + usersignature + '\'' +
                ", islogin=" + islogin +
                '}';
    }

    public String getUsersignature() {
        return usersignature;
    }

    public void setUsersignature(String usersignature) {
        this.usersignature = usersignature;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIslogin() {
        return islogin;
    }

    public void setIslogin(Boolean islogin) {
        this.islogin = islogin;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getSoftQuestion() {
        return SoftQuestion;
    }

    public void setSoftQuestion(String softQuestion) {
        SoftQuestion = softQuestion;
    }

    public String getSoftAnswer() {
        return SoftAnswer;
    }

    public void setSoftAnswer(String softAnswer) {
        SoftAnswer = softAnswer;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}

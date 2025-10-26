package com.example.myapplication.ui.user;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.user.POJO.User;
import com.example.myapplication.ui.user.Utils.SqlUtils;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText UserNumber;//账号
    EditText UserPassword;//密码
    Button Login;//登录
    Button CreateUser;//注册

    TextView findPassword;//找回密码

    List<User> userList = new ArrayList<>();//存放查找到的用户信息

    MusicService musicService;//服务

    List<MusicInfo> musicInfoList = new ArrayList<>();//存放当前播放歌单

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        context = this;
        LitePal.initialize(this);/*配置litepel*/

        //获取控件id
        UserNumber = (EditText) findViewById(R.id.et_number);
        UserPassword = (EditText) findViewById(R.id.et_password);
        Login = (Button) findViewById(R.id.bt_login);
        CreateUser = (Button) findViewById(R.id.bt_createuser);
        findPassword = (TextView) findViewById(R.id.tv_findpassword);

        Login.setOnClickListener(this);
        CreateUser.setOnClickListener(this);
        findPassword.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("id",1);
                if (musicService != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                System.out.println("点击了back");
            }
        });
        /*
         * 获取当前播放的歌单并且保存起来以便传到其他页面
         * */
        musicInfoList.clear();
        musicInfoList.addAll((List<MusicInfo>) getIntent().getSerializableExtra("musiclist"));

    }
    /**
     * 绑定组件
     * */
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    //开启服务
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);//绑定Service
        System.out.println("onStart");
    }
    //关闭服务
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection); //取消绑定的service
    }

    /*
    * 判断输入内容是否为空
    * */
    public boolean ETisNull() {
        if (UserNumber.getText().toString().equals("")) {
            return false;
        }
        if (UserPassword.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_login:
                if (ETisNull()) {
                    SqlUtils sqlUtils = new SqlUtils();
                    userList.clear();
                    userList = sqlUtils.findNumber(UserNumber.getText().toString());
                    if (userList.size() <= 0) {
                        /*判断该账号是否存在*/
                        Toast.makeText(this, "该用户不存在", Toast.LENGTH_SHORT).show();
                    } else {
                        userList.clear();
                        userList = sqlUtils.findPassword(UserPassword.getText().toString(), UserNumber.getText().toString());
                        if (userList.size() <= 0) {
                            /*判断密码是否正确*/
                            Toast.makeText(this, "该用户账号或密码不正确", Toast.LENGTH_SHORT).show();
                        } else {
                            /*
                            * 将当前登录账号设置为登录中状态
                            * */
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    User user = LitePal.find(User.class, userList.get(0).getId());
                                    user.setIslogin(true);
                                    user.save();
                                }
                            });
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("id",1);
                            if (musicService != null) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                                bundle.putBoolean("isplay", musicService.getisplay());
                                intent.putExtra("musiclist", (Serializable)musicInfoList);
                                intent.putExtras(bundle);
                            }
                            startActivity(intent);
                            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "请输入正确的账号和密码，不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_createuser:
                /*
                * 跳转到创建User界面
                * */
                Intent intent = new Intent(context, CreateUserActivity.class);
                if (musicService != null) {
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
                }
                startActivity(intent);
                break;
            case R.id.tv_findpassword:
                /*
                 * 跳转到找回密码界面
                 * */
                Intent intent2 = new Intent(context, FindPasswordActivity.class);
                if (musicService != null) {
                    intent2.putExtra("musiclist", (Serializable)musicInfoList);
                }
                startActivity(intent2);
                break;
        }
    }
}

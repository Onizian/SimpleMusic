package com.example.myapplication.ui.user;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.user.Utils.SqlUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    EditText password;
    EditText password2;
    Button btchange;
    Button cancel;

    MusicService musicService;
    /*
     * 用于存放当前播放的歌单列表
     * */
    private List<MusicInfo> musicInfoList = new ArrayList<>();

    String number;//存放要更改的账号

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword_main);
        context = this;

        /*获取控件id*/
        password = (EditText) findViewById(R.id.et_changepassword);
        password2 = (EditText) findViewById(R.id.et_changepassword2);
        btchange = (Button) findViewById(R.id.bt_changepassword);
        cancel = (Button) findViewById(R.id.bt_changecancel);

        btchange.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.changepassword_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                if (musicService != null) {
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
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
        number = getIntent().getStringExtra("number");
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
    }
    //关闭服务
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection); //取消绑定的service
    }

    public boolean ETisNull() {
        if (password.getText().toString().equals("")) {
            return false;
        }
        if (password2.getText().toString().equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_changepassword:
                SqlUtils sqlUtils = new SqlUtils();
                if (ETisNull()) {
                    if (password.getText().toString().equals(password2.getText().toString())) {
                        if (sqlUtils.UpdataPassword(number, password.getText().toString())) {
                            //若输入两次密码正确则将密码修改且跳转到登录页面
                            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            if (musicService != null) {
                                intent.putExtra("musiclist", (Serializable)musicInfoList);
                            }
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context, "两次密码不正确，请输入两次相同的密码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "不能为空，请输入两次密码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_changecancel:
                /*
                 * 取消按钮
                 * */
                Dialog dialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.remove)
                        .setTitle("确定取消?")
                        .setMessage("您确定取消找回密码吗？")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                password.setText("");
                                password2.setText("");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                //设置对话框居中
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                break;
        }
    }
}

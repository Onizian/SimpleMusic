package com.example.myapplication.ui.user;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.ConditionVariable;
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

public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText number;
    private EditText email;
    private EditText question;
    private EditText answer;
    private Button bt_find;
    private Button bt_cancel;
    private Button bt_findquestion;

    private Context context;
    private MusicService musicService;
    List<MusicInfo> musicInfoList = new ArrayList<>();//存放当前播放歌单


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_findpassword);
        context = this;
        LitePal.initialize(this);/*配置litepel*/

        /*获取控件id*/
        number = (EditText) findViewById(R.id.et_findnumber);
        email = (EditText) findViewById(R.id.et_findemail);
        question = (EditText) findViewById(R.id.et_findquestion);
        answer = (EditText) findViewById(R.id.et_findanswer);
        bt_find = (Button) findViewById(R.id.bt_findpassword);
        bt_cancel = (Button) findViewById(R.id.bt_findcancel);
        bt_findquestion = (Button) findViewById(R.id.bt_findsoftquestion);

        bt_find.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_findquestion.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.findpassword_toolbar);
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

    public boolean ETisNull() {
        if (number.getText().toString().equals("")) {
            Toast.makeText(context, "账号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.getText().toString().equals("")) {
            Toast.makeText(context, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (question.getText().toString().equals("")) {
            Toast.makeText(context, "请点击获取密保问题", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (answer.getText().toString().equals("")) {
            Toast.makeText(context, "密保答案错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        SqlUtils sqlUtils = new SqlUtils();
        switch (v.getId()) {
            case R.id.bt_findpassword:
                if (ETisNull()) {
                    List<User> userList = sqlUtils.findNumber(number.getText().toString());
                    if (userList.size() <= 0) {
                        Toast.makeText(context, "不存在该账号", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (userList.get(0).getSoftAnswer().equals(answer.getText().toString())
                                && userList.get(0).getEmail().equals(email.getText().toString())) {
                            /*
                            * 邮箱和密保答案都正确则跳转到更改密码界面
                            * */
                            Intent intent = new Intent(context, ChangePasswordActivity.class);
                            if (musicService != null) {
                                intent.putExtra("musiclist", (Serializable)musicInfoList);
                                intent.putExtra("number", userList.get(0).getUserNumber());
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "答案错误，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.bt_findcancel:
                /*
                 * 取消按钮
                 * */
                Dialog dialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.remove)
                        .setTitle("确定清空?")
                        .setMessage("您确定清空密码吗？")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                number.setText("");
                                email.setText("");
                                answer.setText("");
                                question.setText("");
                            }
                        })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                //设置对话框居中
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.bt_findsoftquestion:
                if (!number.getText().toString().equals("")) {
                    question.setText(sqlUtils.findSoftQuestion(number.getText().toString()));
                } else {
                    Toast.makeText(context, "请输入账号再查询密保问题", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

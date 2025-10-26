package com.example.myapplication.ui.user;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText number;
    private EditText password;
    private EditText password2;
    private EditText email;
    private EditText softquestion;
    private EditText softanswer;
    private RadioGroup radioGroup;
    private RadioButton selectRadioButton;
    private Button create;
    private Button cancel;

    private MusicService musicService;
    /*
    * 用于存放当前播放的歌单列表
    * */
    private List<MusicInfo> musicInfoList = new ArrayList<>();

    private Context context;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createuser_main);
        context = this;
        LitePal.initialize(this);/*配置litepel*/

        /*获取控件id*/
        number = (EditText) findViewById(R.id.et_createnumber);
        password = (EditText) findViewById(R.id.et_createpassword);
        password2 = (EditText) findViewById(R.id.et_createpassword2);
        email = (EditText) findViewById(R.id.et_createemail);
        softquestion = (EditText) findViewById(R.id.et_createquestion);
        softanswer = (EditText) findViewById(R.id.et_createanswer);
        radioGroup = (RadioGroup) findViewById(R.id.rg_createuser);
        create = (Button) findViewById(R.id.bt_createuser_create);
        cancel = (Button) findViewById(R.id.bt_createcancel);

        create.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.createuser_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题
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

    /**
    * 判断是否是正确的邮箱
    * */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        // 使用 Pattern类compile()方法将一个 regex 封装成 Pattern 对象
        Pattern pattern = Pattern.compile(emailRegex);
        // 使用 Matcher 类的 Matcher()方法将 Pattern 对象编译成 Matcher 对象
        Matcher matcher = pattern.matcher(email);
        // 使用 matches() 方法测试字符串是否匹配给定的正则表达式
        return matcher.matches();
    }

    /**
    * 将新建的User账号加入数据表中
    * */
    public void addlocalDB() {
       new Handler().post(new Runnable() {
           @Override
           public void run() {
               User user = new User();
               user.setUserNumber(number.getText().toString());
               user.setUserPassword(password.getText().toString());
               user.setEmail(email.getText().toString());
               user.setSex(selectRadioButton.getText().toString());
               user.setSoftQuestion(softquestion.getText().toString());
               user.setSoftAnswer(softanswer.getText().toString());
               user.setIslogin(true);
               user.save();
           }
       });
    }

    /**
    * 判断输入内容是否为空
    * */
    public boolean ETisNull() {
        if (number.getText().toString().equals("")) {
            return false;
        }
        if (password.getText().toString().equals("")) {
            return false;
        }
        if (password2.getText().toString().equals("")) {
            return false;
        }
        if (email.getText().toString().equals("")) {
            return false;
        }
        if (softquestion.getText().toString().equals("")) {
            return false;
        }
        if (softanswer.getText().toString().equals("")) {
            return false;
        }
        if (radioGroup.getCheckedRadioButtonId() != -1) {
            //判断是否选中性别
            selectRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        } else {
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_createuser_create:
                /*
                * 创建按钮
                * */
                if (ETisNull()) {
                    List<User> userList = new ArrayList<>();
                    SqlUtils sqlUtils = new SqlUtils();
                    userList = sqlUtils.findNumber(number.getText().toString());
                    if (userList.size() > 0) {
                        //判断账号是否存在
                        Toast.makeText(this, "该账户已经存在", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.getText().toString().equals(password2.getText().toString())) {
                            //判断两次密码是否正确
                            if (isValidEmail(email.getText().toString())) {
                                //判断邮箱是否正确
                                //如果正确则添加到数据表进行存储
                                addlocalDB();
                                /*
                                * 创建成功后跳转到主页
                                * */
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
                                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "两次密码输入不正确", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "还有地方没填，请填写完毕", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_cancel:
                /*
                * 取消按钮
                * */
                Dialog dialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.remove)
                        .setTitle("确定取消?")
                        .setMessage("您确定取消创建用户吗？")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                number.setText("");
                                password.setText("");
                                password2.setText("");
                                email.setText("");
                                softanswer.setText("");
                                softquestion.setText("");
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

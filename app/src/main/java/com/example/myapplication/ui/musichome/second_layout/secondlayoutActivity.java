package com.example.myapplication.ui.musichome.second_layout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.POJO.MusicListInfo;
import com.example.myapplication.ui.musichome.POJO.SongListInfo;
import com.example.myapplication.ui.musichome.service.MusicService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class secondlayoutActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_name;
    private EditText et_profile;
    private Button bt_create;
    private Button bt_cancel;

    /*实现跳转页面数据同步*/
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    private MusicService musicService;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createsonglist_layout);
        context = this;

        et_name = (EditText) findViewById(R.id.et_name);
        et_profile = (EditText) findViewById(R.id.et_profile);
        bt_create = (Button) findViewById(R.id.bt_create);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.createlist_toolbar);
        setSupportActionBar(toolbar);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题


        bt_create.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
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

    public void addlocalsonglist() {
        MusicListInfo musicListInfo = new MusicListInfo();
        musicListInfo.setTablename(et_name.getText().toString());
        musicListInfo.setTableprofile(et_profile.getText().toString());
        /*musicListInfo.setTablename("我的收藏");
        musicListInfo.setTableprofile("官方歌单--------->我的最爱歌单");*/
        /*List<SongListInfo> songListInfoList = new ArrayList<>();
        musicListInfo.setSonglistInfoList(songListInfoList);*/
        musicListInfo.save();
        System.out.println("-------->歌单创建成功！！！");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_create:
                if (et_name.getText().toString().equals("")) {
                    Toast.makeText(this, "歌单名称不能为空", Toast.LENGTH_LONG).show();
                } else {
                    addlocalsonglist();
                    System.out.println("创建成功");
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
                }
                break;
            case R.id.bt_cancel:
                et_profile.setText("");
                et_name.setText("");
                Toast.makeText(context, "取消所有输入的数据", Toast.LENGTH_LONG).show();
                break;
        }
    }
}

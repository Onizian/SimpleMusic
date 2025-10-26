package com.example.myapplication.ui.musichome;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelStore;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;
import com.example.myapplication.ui.musichome.utils.ScanMusicUtils;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SongMusicInfoActivity extends AppCompatActivity implements View.OnClickListener {
    //进度条
    private SeekBar seekBar;
    //歌曲名称
    private TextView title;
    //歌手名称+专辑名称
    private TextView name;
    //专辑图片
    private ImageView imageView;
    //上一首按钮
    private Button bt_lastsong;
    //下一首按钮
    private Button bt_nextsong;
    //点击播放
    private Button bt_play;
    //总时长
    private TextView tv_showduration;
    //目前时长
    private TextView tv_showtime;
    //当前歌单列表
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    //MusicService服务
    private MusicService musicService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songplayerhome);

        //控件获取id
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        title = (TextView) findViewById(R.id.musicshow_title);
        name = (TextView) findViewById(R.id.musicshow_artist);
        imageView = (ImageView) findViewById(R.id.musicshow_image);
        bt_lastsong = (Button) findViewById(R.id.bt_lastsong);
        bt_nextsong = (Button) findViewById(R.id.bt_nextsong);
        bt_play = (Button) findViewById(R.id.bt_play);
        tv_showduration = (TextView) findViewById(R.id.tv_showduration);
        tv_showtime = (TextView) findViewById(R.id.tv_showtime);

        bt_play.setOnClickListener(this);
        bt_nextsong.setOnClickListener(this);
        bt_lastsong.setOnClickListener(this);

        //进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //滑块滑动时调用的
                if (fromUser) {
                    //fromuser判断是用户改变的滑块的值
                    //滑动改变音乐的进度
                    musicService.musicseeTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //滑块开始滑动时调用的
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //滑动停止时调用的
            }
        });

        //返回按钮
        Toolbar toolbar = (Toolbar) findViewById(R.id.musicinfoshow_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongMusicInfoActivity.this, MainActivity.class);
                intent.putExtra("id",1);
                if (musicService != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
                    intent.putExtras(bundle);
                    System.out.println("songmusicshow.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题
        //启动广播
        registerReceiver();
        //获得当前歌单数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt("mCurrentPosition");
            musicInfoList.clear();
            musicInfoList.addAll((List<MusicInfo>) getIntent().getSerializableExtra("musiclist"));
            changeSonginfo(position);
            if (bundle.getBoolean("isplay", false)) {
                bt_play.setBackground(getDrawable(R.drawable.stop));
            }
        }
    }

    /**
     * 更改下边播放栏的音乐信息
     * */
    public void changeSonginfo(int position){
        //设置歌曲所在专辑的封面图片
        imageView.setImageBitmap(ScanMusicUtils.getAlbumPicture(this, musicInfoList.get(position).getData(), 1));
        //设置播放的歌手,专辑名
        name.setText(musicInfoList.get(position).getArtist() + " - " + musicInfoList.get(position).getAlbum());
        //如果内容超过控件，则启用跑马灯效果
        name.setSelected(true);
        //设置播放的歌曲名
        title.setText(musicInfoList.get(position).getTitle());
        //如果内容超过控件，则启用跑马灯效果
        title.setSelected(true);
        //设置时长
        tv_showduration.setText(DateTimeUtils.formatTime(musicInfoList.get(position).getDuration()));
        //获得歌曲的长度并设置成播放进度条的最大值
        seekBar.setMax(musicInfoList.get(position).getDuration());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_lastsong:
                if (musicInfoList != null) {
                    musicService.previousMusic();
                    changeSonginfo(musicService.getmCurrentPosition());
                }
                break;
            case R.id.bt_nextsong:
                if (musicInfoList != null) {
                    musicService.nextMusic();
                    changeSonginfo(musicService.getmCurrentPosition());
                }
                break;
            case R.id.bt_play:
                //控制音乐 播放和暂停
                //media为空时为true
                if (musicService.getisnull()) {
                    if (musicInfoList.size() == 0) {
                        Toast.makeText(this, "没有音乐可播放",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    musicService.playmusic(0, musicInfoList);
                    bt_play.setBackground(getDrawable(R.drawable.stop));
                } else {
                    //播放音乐 暂停或播放
                    if (musicService.getisplay()) {
                        musicService.pauseOrcontinueMusic(1);
                        bt_play.setBackground(getDrawable(R.drawable.play));
                    } else {
                        musicService.pauseOrcontinueMusic(2);
                        bt_play.setBackground(getDrawable(R.drawable.stop));
                    }
                }
                break;
        }
    }

    /**
     * 注册广播
     * */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("allmusic.msg");
        intentFilter.addAction("songshow");
        intentFilter.addAction("mediaisplay");
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 接收广播
     * */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("allmusic.msg")) {
                int position = intent.getIntExtra("msg",0);
                changeSonginfo(position);
                System.out.println("allmusic接收到了广播");
            }
            if (action.equals("songshow")) {
                //一秒更新一次进度条
                int mtime = intent.getIntExtra("musictime", 0);
                seekBar.setProgress(mtime);
                tv_showtime.setText(DateTimeUtils.formatTime(mtime));
            }
            if (action.equals("mediaisplay")) {
                if (intent.getBooleanExtra("isplay", false)){
                    bt_play.setBackground(getDrawable(R.drawable.stop));
                }
            }
        }
    };

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
            System.out.println("RankMusic.onServiceDisconnected");
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

}

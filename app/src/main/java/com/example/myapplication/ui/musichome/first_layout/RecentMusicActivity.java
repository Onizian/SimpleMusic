package com.example.myapplication.ui.musichome.first_layout;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRankInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRecentInfo;
import com.example.myapplication.ui.musichome.SongMusicInfoActivity;
import com.example.myapplication.ui.musichome.adapter.MusicItemClickListener;
import com.example.myapplication.ui.musichome.adapter.MusicRecentListAdapter;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.musichome.utils.Constant;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;
import com.example.myapplication.ui.musichome.utils.SPUtils;
import com.example.myapplication.ui.musichome.utils.ScanMusicUtils;

import org.litepal.LitePal;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RecentMusicActivity extends AppCompatActivity implements View.OnClickListener {
    /*
     * 歌曲列表
     *
     * */
    private RecyclerView rvMusic;
    /*
     * 扫描歌曲布局
     *
     * */
    private LinearLayout layout;
    /*
     *歌曲适配器
     * */
    private MusicRecentListAdapter mAdapter;
    /*
     * 歌曲列表
     * */
    private List<MusicRecentInfo> musicRecentInfoList = new ArrayList<>();
    /*
     * 本地音乐数据
     * */
    private boolean localMusicData = false;

    /*
     * 专辑图片
     * */
    private ImageView iV_Logo;
    /*
     * 歌曲名称
     * */
    private TextView tv_title;
    /*
     * 歌手名+专辑名
     * */
    private TextView tv_songinfo;
    /*
     * 播放暂停按钮
     * */
    private Button bt_song;
    /*
     * 上一次点击位置
     * */
    private int oldPosition = -1;
    /*
     * 播放次数
     * */
    private TextView tv_playnum;
    /*    *//*
     * 音频播放器
     * *//*
    private MediaPlayer mediaPlayer;*/
    /*
     * 记录当前播放歌曲的位置
     * */
    private int mCurrentPosition = -1;
    /*
     * 音乐播放服务
     * */
    private MusicService musicService;
    /*
     * 是否绑定信号
     * */
    private boolean bound = false;
    /*
     * 转成MusicInfo歌单形式
     * */
    List<MusicInfo> musicInfoList = new ArrayList<>();


    private static final int REQUEST_READ_EXTERNAL = 1;

    private Context context;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recentmusic_main);
        context = this;
        LitePal.initialize(this);/*配置litepel*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.recentmusic_toolbar);
        setSupportActionBar(toolbar);
        /*设置返回按钮的事件*/
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
                    System.out.println("recentmusic.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                startActivity(intent);
                System.out.println("点击了back");
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题
        /*----------获得权限-------------*/
        checkStorageManagerPermission();
        /*----------获取控件id-----------*/
        iV_Logo = (ImageView) findViewById(R.id.iv_alubum);
        tv_title = (TextView) findViewById(R.id.music_name);
        tv_songinfo = (TextView) findViewById(R.id.music_info);
        bt_song = (Button) findViewById(R.id.song_bt);

        bt_song.setOnClickListener(this::onClick);
        iV_Logo.setOnClickListener(this::onClick);

        /*
         * 启动广播
         * */
        registerReceiver();

        /*判断是否是再一次启动activity然后更新音乐栏数据*/
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt("mCurrentPosition");
            musicInfoList.clear();
            musicInfoList.addAll((List<MusicInfo>) getIntent().getSerializableExtra("musiclist"));
            changeSonginfo(position);
            oldPosition = position - 1;
            playPositionControl(position);
            if (bundle.getBoolean("isplay", false)) {
                bt_song.setBackground(getDrawable(R.drawable.stop));
            }
        }
    }

    /**
     * 绑定组件
     * */
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
            bound = true;//已经绑定
            System.out.println("RecentMusic.onServiceConnected==" + bound);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("RecentMusic.onServiceDisconnected");
            bound = false;
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
        if (bound) {
            unbindService(connection); //取消绑定的service
            bound = false;
        }
    }

    /*-------------获取权限--------------*/
    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                Environment.isExternalStorageManager()) {
//            Toast.makeText(this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();
            getMusicList();/*扫描音乐*/

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION},
                    REQUEST_READ_EXTERNAL);
        }
    }

    /**
     * 获取音乐列表
     * */
    private void getMusicList() {
        localMusicData = SPUtils.getBoolean(Constant.LOCAL_MUSIC_DB, false, context);
        //清除列表数据
        musicRecentInfoList.clear();
        //将扫描到的音乐赋值给音乐列表
        if (localMusicData) {
            //有数据读取本地数据库的数据
            musicRecentInfoList = LitePal.findAll(MusicRecentInfo.class);
            //使用类内部比较器进行从高到低排序
            List<MusicRecentInfo> musicRecentInfoList1 = new ArrayList<>();
            musicRecentInfoList1.addAll(MusicRecentListComparator(musicRecentInfoList));
            musicRecentInfoList.clear();
            musicRecentInfoList.addAll(musicRecentInfoList1);
            //清空当前的musicinfolist用于存放musicrecentlist转换后的musicinfolist
            musicInfoList.clear();
            musicInfoList.addAll(getMusicInfoList(musicRecentInfoList));

            /*----------测试是否读取了数据库数据----------------*/
            System.out.println("recentmusic读取数据库数据中");
        }
        if (musicRecentInfoList != null && musicRecentInfoList.size() > 0) {
            showLocalMusicData();
        } else {
            Toast.makeText(context,"没有音乐", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 对歌曲进行播放次数排名
     * */
    public List<MusicRecentInfo> MusicRecentListComparator(List<MusicRecentInfo> musicRecentInfoList) {
        //冒泡排序
        for (int i = musicRecentInfoList.size(); i > 0; i--) {
            for (int j = i-1; j > 0; j--) {
                if (musicRecentInfoList.get(j-1).getPlaytime() < musicRecentInfoList.get(j).getPlaytime()) {
                    MusicRecentInfo musicRecentInfo = musicRecentInfoList.get(j);//获得当前最近播放的数据
                    musicRecentInfoList.remove(j);//删除当前位置的数值
                    musicRecentInfoList.add(0,musicRecentInfo);//将数据放到第一位
                }
            }
        }
        System.out.println("执行完recent排序");
        return musicRecentInfoList;
    }

    /**
     * 进行歌单类型转换
     * */
    private List<MusicInfo> getMusicInfoList(List<MusicRecentInfo> musicRecentInfoList) {
        for (int i = 0; i < musicRecentInfoList.size(); i++) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setTitle(musicRecentInfoList.get(i).getTitle());
            musicInfo.setData(musicRecentInfoList.get(i).getData());
            musicInfo.setSize(musicRecentInfoList.get(i).getSize());
            musicInfo.setDuration(musicRecentInfoList.get(i).getDuration());
            musicInfo.setAlbum(musicRecentInfoList.get(i).getAlbum());
            musicInfo.setArtist(musicRecentInfoList.get(i).getArtist());
            musicInfo.setCheck(false);
            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    /**
     * 更改播放次数
     * */
    private void addLocalRankMusic(int mCurrentPosition) {
        //查询数据表是否存在信息
        List<MusicRankInfo> mrList = LitePal.where("title=? and artist=?", musicRecentInfoList.get(mCurrentPosition).getTitle() + "", musicRecentInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRankInfo.class);
        if (mrList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRankInfo musicRankInfo = LitePal.find(MusicRankInfo.class, mrList.get(0).getId());
                    musicRankInfo.setPlaynum(musicRankInfo.getPlaynum()+1);
                    musicRankInfo.save();
                    System.out.println(musicRankInfo.getTitle() + "播放"+ musicRankInfo.getPlaynum() + "次");
                }
            });
        }
    }

    /**
    * 设置播放的时间并将其更新到最近列表歌单中
    * */
    private void addLocalRecentMusic(int mCurrentPosition) {
        //查询数据表是否存在信息
        List<MusicRecentInfo> mrList = LitePal.where("title=? and artist=?", musicRecentInfoList.get(mCurrentPosition).getTitle() + "", musicRecentInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRecentInfo.class);
        if (mrList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRecentInfo musicRecentInfo = LitePal.find(MusicRecentInfo.class, mrList.get(0).getId());
                    try {
                        musicRecentInfo.setPlaytime(timechange());//设置最近播放时间
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    musicRecentInfo.save();//保存更新
                    System.out.println("当前歌曲播放的日期时间段："+musicRecentInfo.getPlaytime());
                }
            });
        }
    }

    /**
    * 时间转换+获取时间
    * */
    public long timechange() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = sdf.parse(DateTimeUtils.getNowDateTime());
        return time.getTime();
    }


    /**
     * 控制播放位置
     * */
    public void playPositionControl(int position) {
        /*position位置默认从0开始*/
        if (oldPosition == -1) {
            //未点击过 第一次点击
            oldPosition = position;
            /*设置点击到的item的check值*/
            musicRecentInfoList.get(position).setCheck(true);
        } else {
            //大于1次
            if (oldPosition != position) {
                /*设置上一个song为false去掉选择，现在的song为true选择中*/
                musicRecentInfoList.get(oldPosition).setCheck(false);
                musicRecentInfoList.get(position).setCheck(true);
                //重新设置位置，当下一次点击是position又会和oldposition不一样
                oldPosition = position;
            }
        }
        //刷新数据
        mAdapter.changState();
    }

    /**
     * 显示本地音乐数据
     * */
    private void showLocalMusicData() {

        //指定适配器的布局和数据源
        mAdapter = new MusicRecentListAdapter(context,musicRecentInfoList);
        rvMusic = (RecyclerView) findViewById(R.id.rv_recentmusic);
        //线性布局管理器，可以设置横向还是纵向，RecyclerView默认是纵向的，所以不用处理
        rvMusic.setLayoutManager(new LinearLayoutManager(context));
        //设置适配器
        rvMusic.setAdapter(mAdapter);
        //item的点击事件
        mAdapter.setItemClickListener(new MusicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*Toast.makeText(context, "点击了" + position, Toast.LENGTH_SHORT).show();*/
                if (view.getId() == R.id.item_recentmusic) {
                    //控制当前播放位置
                    getMusicList();
                    playPositionControl(position);
                    mCurrentPosition = position;//设置当前位置
                    changeSong(mCurrentPosition);
                    addLocalRankMusic(position);
                    addLocalRecentMusic(position);
                    //播放按钮控制
                    if (musicService.getisplay()) {
                        bt_song.setBackground(getDrawable(R.drawable.stop));
                    } else {
                        bt_song.setBackground(getDrawable(R.drawable.play));
                    }
                }
            }
        });
    }

    /**
     * 切换歌曲
     * */
    public void changeSong(int position) {
        //调用服务中的音乐播放
        musicService.playmusic(position,musicInfoList);
        changeSonginfo(position);
    }

    /**
     * 更改下边播放栏的音乐信息
     * */
    public void changeSonginfo(int position){
        //设置歌曲所在专辑的封面图片
        iV_Logo.setImageBitmap(ScanMusicUtils.getAlbumPicture(context, musicInfoList.get(position).getData(), 1));
        //设置播放的歌手,专辑名
        tv_songinfo.setText(musicInfoList.get(position).getArtist() + " - " + musicInfoList.get(position).getAlbum());
        //如果内容超过控件，则启用跑马灯效果
        tv_songinfo.setSelected(true);
        //设置播放的歌曲名
        tv_title.setText(musicInfoList.get(position).getTitle());
        //如果内容超过控件，则启用跑马灯效果
        tv_title.setSelected(true);
    }

    /**
     * 注册广播
     * */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("allmusic.msg");
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
                playPositionControl(position);
                System.out.println("allmusic接收到了广播");
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.song_bt:
                //控制音乐 播放和暂停
                if (musicService.getisnull()) {
                    if (musicInfoList.size() == 0) {
                        Toast.makeText(context, "没有音乐可播放",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //没播放过音乐，点击之后播放第一首
                    oldPosition = 0;//上一首位置
                    mCurrentPosition = 0;//现在位置
                    musicInfoList.get(mCurrentPosition).setCheck(true);//设置选择当前歌曲
                    mAdapter.changState();
                    changeSong(mCurrentPosition);
                    addLocalRankMusic(mCurrentPosition);
                    addLocalRecentMusic(mCurrentPosition);
                } else {
                    //播放过音乐 暂停或播放
                    if (musicService.getisplay()) {
                        musicService.pauseOrcontinueMusic(1);
                        bt_song.setBackground(getDrawable(R.drawable.play));
                    } else {
                        musicService.pauseOrcontinueMusic(2);
                        bt_song.setBackground(getDrawable(R.drawable.stop));
                    }
                }
                break;
            case R.id.iv_alubum:
                Intent intent = new Intent(context, SongMusicInfoActivity.class);
                if (musicService != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                System.out.println("点击了歌曲栏进行展示歌曲");
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        System.out.println("退出service和广播");
    }
}

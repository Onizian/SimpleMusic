package com.example.myapplication.ui.musichome.first_layout;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.POJO.MusicListInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRankInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRecentInfo;
import com.example.myapplication.ui.musichome.POJO.SongListInfo;
import com.example.myapplication.ui.musichome.SongMusicInfoActivity;
import com.example.myapplication.ui.musichome.adapter.MusicItemAddsongClickListener;
import com.example.myapplication.ui.musichome.adapter.MusicItemClickListener;
import com.example.myapplication.ui.musichome.adapter.MusicListAdapter;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.musichome.utils.Constant;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;
import com.example.myapplication.ui.musichome.utils.SPUtils;
import com.example.myapplication.ui.musichome.utils.ScanMusicUtils;
import com.example.myapplication.ui.musichome.utils.SearchUtils;

import org.litepal.LitePal;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AllMusicActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    * 歌曲列表
    *
    * */
    private RecyclerView rvMusic;
    /*
    *歌曲适配器
    * */
    private MusicListAdapter mAdapter;
    /*
    * 歌曲列表
    * */
    private List<MusicInfo> musicInfoList = new ArrayList<>();

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
    * 获取从主页传来的数据
    * */
    private Bundle bundle;
    /*
    * 判断是否是搜索的歌曲
    * */
    private boolean isSearch = false;
    /*
    * 搜索的歌曲歌单
    * */
    private List<MusicInfo> searchsonglist = new ArrayList<>();

    private static final int REQUEST_READ_EXTERNAL = 1;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allmusic_main);
        context = this;
        LitePal.initialize(this);/*配置litepel*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.allmusic_toolbar);
        setSupportActionBar(toolbar);
        /*--------------------toolbar事件-------------------------------*/
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
                    System.out.println("allmusic.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                startActivity(intent);
                System.out.println("点击了back");
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题
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

        /*判断是否是再一次启动activity然后更新音乐栏数据
         * */
        bundle = this.getIntent().getExtras();
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

    /*设置搜索*/

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        //找到searchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        /*searchView.setIconified(false);//设置searchView处于展开状态*/
        /*searchView.onActionViewExpanded();//当展开无输入内容的时候，没有关闭的图标*/
        /*searchView.setIconifiedByDefault(false);//默认为true在框内，设置false则在框外*/
        searchView.setSubmitButtonEnabled(true);//显示提交按钮
        searchView.setQueryHint("请输入歌曲名称");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交按钮的点击事件
                Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                musicInfoList.clear();
                SearchUtils searchUtils = new SearchUtils();
                if (searchUtils.findTitle(query).size() > 0) {
                    musicInfoList = searchUtils.findTitle(query);
                }
                isSearch = true;
                showLocalMusicData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当输入框内容改变的时候回调
                /*System.out.println("------->"+newText);
                if (newText == null) {
                    return true;
                }
                musicInfoList.clear();
                SearchUtils searchUtils = new SearchUtils();
                if (searchUtils.findTitle(newText).size() > 0) {
                    musicInfoList = searchUtils.findTitle(newText);
                }
                showLocalMusicData();
                isSearch = true;*/
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
            System.out.println("AllMusic.onServiceConnected==" + bound);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("AllMusic.onServiceDisconnected");
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
        musicInfoList.clear();
        //将扫描到的音乐赋值给音乐列表
        if (localMusicData) {
            //有数据读取本地数据库的数据
            musicInfoList = LitePal.findAll(MusicInfo.class);
            /*----------测试是否读取了数据库数据----------------*/
            System.out.println("allmusic读取数据库数据中");
        } else {
            ScanMusicUtils scanMusicUtils = new ScanMusicUtils();
            musicInfoList = scanMusicUtils.getMusicInfo(this);
        }
        if (musicInfoList != null && musicInfoList.size() > 0) {
            showLocalMusicData();
            if (!localMusicData) {
                //添加到本地数据库中
                addLocalDB();
                /*---------测试是否添加数据到数据库中------------*/
                System.out.println("addLocalDB添加到本地数据库中");
            }
        } else {
            Toast.makeText(context,"没有音乐", Toast.LENGTH_LONG).show();
        }
    }
    /**
    * 添加到本地数据库中
    * */
    private void addLocalDB() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < musicInfoList.size(); i++) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setArtist(musicInfoList.get(i).getArtist());
                    musicInfo.setAlbum(musicInfoList.get(i).getAlbum());
                    musicInfo.setData(musicInfoList.get(i).getData());
                    musicInfo.setDuration(musicInfoList.get(i).getDuration());
                    musicInfo.setSize(musicInfoList.get(i).getSize());
                    musicInfo.setCheck(musicInfoList.get(i).getCheck());
                    musicInfo.setTitle(musicInfoList.get(i).getTitle());
                    musicInfo.save();
                    System.out.println("执行第"+i+"次");
                }
                List<MusicInfo> list = LitePal.findAll(MusicInfo.class);
                if (list.size() > 0) {
                    SPUtils.putBoolean(Constant.LOCAL_MUSIC_DB, true, context);
                    System.out.println("添加到本地数据库的音乐：" + list.size() + "首");
                }
            }
        });
    }
    /**
    * 记录歌曲的播放次数
    * */
    private void addLocalRankMusic(int mCurrentPosition) {
        List<MusicRankInfo> musicRankInfoList = LitePal.where("title=? and artist=?", musicInfoList.get(mCurrentPosition).getTitle() + "", musicInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRankInfo.class);
        if (musicRankInfoList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRankInfo musicRankInfo = LitePal.find(MusicRankInfo.class, musicRankInfoList.get(0).getId());
                    musicRankInfo.setPlaynum(musicRankInfo.getPlaynum()+1);
                    musicRankInfo.save();
                    System.out.println(musicRankInfo.getTitle() + "播放"+ musicRankInfo.getPlaynum() + "次");
                }
            });
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRankInfo musicRankInfo = new MusicRankInfo();
                    musicRankInfo.setArtist(musicInfoList.get(mCurrentPosition).getArtist());
                    musicRankInfo.setAlbum(musicInfoList.get(mCurrentPosition).getAlbum());
                    musicRankInfo.setData(musicInfoList.get(mCurrentPosition).getData());
                    musicRankInfo.setDuration(musicInfoList.get(mCurrentPosition).getDuration());
                    musicRankInfo.setSize(musicInfoList.get(mCurrentPosition).getSize());
                    musicRankInfo.setCheck(false);
                    musicRankInfo.setTitle(musicInfoList.get(mCurrentPosition).getTitle());
                    musicRankInfo.setPlaynum(1);
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
        List<MusicRecentInfo> mrList = LitePal.where("title=? and artist=?", musicInfoList.get(mCurrentPosition).getTitle() + "", musicInfoList.get(mCurrentPosition).getArtist() + "").find(MusicRecentInfo.class);
        if (mrList.size() != 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRecentInfo musicRecentInfo = LitePal.find(MusicRecentInfo.class, mrList.get(0).getId());
                    try {
                        musicRecentInfo.setPlaytime(timechange());//设置最近播放时间
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicRecentInfo.save();//保存更新
                    System.out.println("当前歌曲播放的日期时间段："+musicRecentInfo.getPlaytime());
                }
            });
        }else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    MusicRecentInfo musicRecentInfo = new MusicRecentInfo();
                    musicRecentInfo.setArtist(musicInfoList.get(mCurrentPosition).getArtist());
                    musicRecentInfo.setAlbum(musicInfoList.get(mCurrentPosition).getAlbum());
                    musicRecentInfo.setData(musicInfoList.get(mCurrentPosition).getData());
                    musicRecentInfo.setDuration(musicInfoList.get(mCurrentPosition).getDuration());
                    musicRecentInfo.setSize(musicInfoList.get(mCurrentPosition).getSize());
                    musicRecentInfo.setCheck(false);
                    musicRecentInfo.setTitle(musicInfoList.get(mCurrentPosition).getTitle());
                    try {
                        musicRecentInfo.setPlaytime(timechange());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    musicRecentInfo.save();
                    System.out.println(musicRecentInfo.getTitle() + "最近播放时间："+ musicRecentInfo.getPlaytime() + "秒");
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
            musicInfoList.get(position).setCheck(true);
        } else {
            //大于1次
            if (oldPosition != position) {
                /*设置上一个song为false去掉选择，现在的song为true选择中*/
                musicInfoList.get(oldPosition).setCheck(false);
                musicInfoList.get(position).setCheck(true);
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
        mAdapter = new MusicListAdapter(context,musicInfoList);
        rvMusic = (RecyclerView) findViewById(R.id.rv_music);
        //线性布局管理器，可以设置横向还是纵向，RecyclerView默认是纵向的，所以不用处理
        rvMusic.setLayoutManager(new LinearLayoutManager(context));
        //设置适配器
        rvMusic.setAdapter(mAdapter);
        //item的点击事件
        mAdapter.setItemClickListener(new MusicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*Toast.makeText(context, "点击了" + position, Toast.LENGTH_SHORT).show();*/
                if (view.getId() == R.id.item_music) {
                    //控制当前播放位置
                    if (!isSearch) {
                        getMusicList();//若点击歌曲则将之前传进来的其他歌单清掉
                    }
                    playPositionControl(position);
                    mCurrentPosition = position;//设置当前位置
                    changeSong(mCurrentPosition);
                    addLocalRankMusic(position);
                    addLocalRecentMusic(position);
                    System.out.println(musicInfoList);
                    //播放按钮控制
                    if (musicService.getisplay()) {
                        bt_song.setBackground(getDrawable(R.drawable.stop));
                    } else {
                        bt_song.setBackground(getDrawable(R.drawable.play));
                    }
                }
            }
        });
        //设置item的收藏功能
        mAdapter.setMusicItemAddsongClickListener(new MusicItemAddsongClickListener() {
            @Override
            public void addsong(View v, int position) {
                if (v.getId() == R.id.ib_love) {
                    addLoveMusicList(position);
                }
            }
        });
    }

    /**
    *添加数据到我的收藏里面
    * */
    private void addLoveMusicList(int mCurrentPosition) {
        //查询数据表是否存在该信息
        MusicListInfo musicListInfo = LitePal.find(MusicListInfo.class, 21);
        List<SongListInfo> songListInfoList = LitePal.where("musiclistinfo_id=?", String.valueOf(21)).find(SongListInfo.class);
        if (songListInfoList != null) {
            for (SongListInfo song : songListInfoList) {
                if (song.getArtist().equals(musicInfoList.get(mCurrentPosition).getArtist())
                        && song.getTitle().equals(musicInfoList.get(mCurrentPosition).getTitle())
                        && song.getAlbum().equals(musicInfoList.get(mCurrentPosition).getAlbum())) {
                    Toast.makeText(context, "已存在收藏歌单", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        Dialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.love)
                .setTitle("加入收藏歌单")
                .setMessage("是否添加该歌曲到收藏歌单里？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                SongListInfo song = new SongListInfo();

                                song.setId(musicInfoList.get(mCurrentPosition).getId());
                                song.setAlbum(musicInfoList.get(mCurrentPosition).getAlbum());
                                song.setArtist(musicInfoList.get(mCurrentPosition).getArtist());
                                song.setData(musicInfoList.get(mCurrentPosition).getData());
                                song.setDuration(musicInfoList.get(mCurrentPosition).getDuration());
                                song.setCheck(false);
                                song.setTitle(musicInfoList.get(mCurrentPosition).getTitle());
                                song.setSize(musicInfoList.get(mCurrentPosition).getSize());
                                song.setMusicListInfo(musicListInfo);

                                song.save();

                                musicListInfo.getSonglistInfoList().add(song);
                                musicListInfo.save();

                                System.out.println(LitePal.findAll(MusicListInfo.class));
                                System.out.println(LitePal.findAll(SongListInfo.class));

                                Toast.makeText(context, "收藏成功", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "取消收藏", Toast.LENGTH_LONG).show();
                            }
                        }).create();
        //设置对话框居中
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
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
            /*case R.id.allmusic_toolbar:
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);
                System.out.println("点击了back");
                break;*/
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

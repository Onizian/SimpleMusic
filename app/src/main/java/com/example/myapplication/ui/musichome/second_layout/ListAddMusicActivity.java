package com.example.myapplication.ui.musichome.second_layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
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
import com.example.myapplication.ui.musichome.POJO.MusicListInfo;
import com.example.myapplication.ui.musichome.POJO.SongListInfo;
import com.example.myapplication.ui.musichome.adapter.AddMusicAdatper;
import com.example.myapplication.ui.musichome.adapter.MusicItemAddsongClickListener;
import com.example.myapplication.ui.musichome.adapter.MusicItemClickListener;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.musichome.utils.Constant;
import com.example.myapplication.ui.musichome.utils.SPUtils;
import com.example.myapplication.ui.musichome.utils.ScanMusicUtils;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListAddMusicActivity extends AppCompatActivity {
    /*
     * 歌曲列表
     *
     * */
    private RecyclerView rv_addmusic;
    /*
     * 传入的歌曲列表
     * */
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    /*
     * 本地音乐数据
     * */
    private boolean localMusicData = false;
    /*
     * 音乐播放服务
     * */
    private MusicService musicService;
    /*
    * 适配器
    * */
    AddMusicAdatper mAdapter;
    /*
    * 歌单信息
    * */
    private List<MusicListInfo> musicListInfoList = new ArrayList<>();
    /*
    * 本activity所处理的musicinfolist
    * */
    private List<MusicInfo> musicInfoList1 = new ArrayList<>();

    private static final int REQUEST_READ_EXTERNAL = 1;



    /*
    * 上下文
    * */
    Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listaddmusic_main);
        context = this;
        LitePal.initialize(this);/*配置litepel*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.addmusic_toolbar);
        setSupportActionBar(toolbar);
        /*设置返回按钮的事件*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id",1);
                if (musicService != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable)musicInfoList);
                    intent.putExtras(bundle);
                }
                intent.putExtra("musiclistinfolist", (Serializable) musicListInfoList);
                intent.setClass(context, ShowMusicListActivity.class);
                startActivity(intent);
                System.out.println("点击了back");
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);//去除toolbar的标题
        checkStorageManagerPermission();//获取权限
        /*判断是否是再一次启动activity然后更新音乐栏数据*/
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            musicInfoList.clear();
            musicInfoList.addAll((List<MusicInfo>) getIntent().getSerializableExtra("musiclist"));
            musicListInfoList.clear();
            musicListInfoList.addAll((List<MusicListInfo>) getIntent().getSerializableExtra("musiclistinfolist"));
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
        musicInfoList1.clear();
        //将扫描到的音乐赋值给音乐列表
        if (localMusicData) {
            //有数据读取本地数据库的数据
            musicInfoList1 = LitePal.findAll(MusicInfo.class);
        } else {
            ScanMusicUtils scanMusicUtils = new ScanMusicUtils();//使用扫描音乐工具类
            musicInfoList1 = scanMusicUtils.getMusicInfo(this);
        }
        if (musicInfoList1 != null && musicInfoList1.size() > 0) {
            showLocalMusicData();
            if (!localMusicData) {
                //添加到本地数据库中
                addLocalDB();
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
                for (int i = 0; i < musicInfoList1.size(); i++) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setArtist(musicInfoList1.get(i).getArtist());
                    musicInfo.setAlbum(musicInfoList1.get(i).getAlbum());
                    musicInfo.setData(musicInfoList1.get(i).getData());
                    musicInfo.setDuration(musicInfoList1.get(i).getDuration());
                    musicInfo.setSize(musicInfoList1.get(i).getSize());
                    musicInfo.setCheck(musicInfoList1.get(i).getCheck());
                    musicInfo.setTitle(musicInfoList1.get(i).getTitle());
                    musicInfo.save();
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
     * 显示本地音乐数据
     * */
    private void showLocalMusicData() {
        //指定适配器的布局和数据源
        mAdapter = new AddMusicAdatper(context, musicInfoList1);
        rv_addmusic = (RecyclerView) findViewById(R.id.rv_addmusic);
        //线性布局管理器，可以设置横向还是纵向，RecyclerView默认是纵向的，所以不用处理
        rv_addmusic.setLayoutManager(new LinearLayoutManager(context));
        //设置适配器
        rv_addmusic.setAdapter(mAdapter);
        //item的点击事件
        mAdapter.setItemClickListener(new MusicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.item_addmusic) {
                    //选择的话该歌曲会变色以标识已经选中
                    if (musicInfoList1.get(position).isCheck()) {
                        musicInfoList1.get(position).setCheck(false);
                    } else {
                        musicInfoList1.get(position).setCheck(true);
                    }
                }
            }
        });
        mAdapter.setMusicItemAddsongClickListener(new MusicItemAddsongClickListener() {
            @Override
            public void addsong(View v, int position) {
                if (v.getId() == R.id.ib_add) {
                    addMusicList(position);
                }
            }
        });
    }

    /**
    *
    * 添加数据到我的歌单里面
    * */
    private void addMusicList(int mCurrentPosition) {
        //查询数据表是否存在该信息
        MusicListInfo musicListInfo = LitePal.find(MusicListInfo.class, musicListInfoList.get(0).getId());
        List<SongListInfo> songListInfoList = LitePal.where("musiclistinfo_id=?", String.valueOf(musicListInfoList.get(0).getId())).find(SongListInfo.class);
        if (songListInfoList != null) {
            for (SongListInfo song : songListInfoList) {
                if (song.getArtist().equals(musicInfoList1.get(mCurrentPosition).getArtist())
                        && song.getTitle().equals(musicInfoList1.get(mCurrentPosition).getTitle())
                        && song.getAlbum().equals(musicInfoList1.get(mCurrentPosition).getAlbum())) {
                    Toast.makeText(context, "已存在歌单里", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        Dialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.love)
                .setTitle("加入歌单")
                .setMessage("是否添加该歌曲到歌单里？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                SongListInfo song = new SongListInfo();

                                song.setId(musicInfoList1.get(mCurrentPosition).getId());
                                song.setAlbum(musicInfoList1.get(mCurrentPosition).getAlbum());
                                song.setArtist(musicInfoList1.get(mCurrentPosition).getArtist());
                                song.setData(musicInfoList1.get(mCurrentPosition).getData());
                                song.setDuration(musicInfoList1.get(mCurrentPosition).getDuration());
                                song.setCheck(false);
                                song.setTitle(musicInfoList1.get(mCurrentPosition).getTitle());
                                song.setSize(musicInfoList1.get(mCurrentPosition).getSize());
                                song.setMusicListInfo(musicListInfo);

                                song.save();

                                musicListInfo.getSonglistInfoList().add(song);
                                musicListInfo.save();

                                Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "取消添加", Toast.LENGTH_LONG).show();
                            }
                        }).create();
        //设置对话框居中
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}

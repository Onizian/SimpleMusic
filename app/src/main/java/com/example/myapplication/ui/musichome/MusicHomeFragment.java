package com.example.myapplication.ui.musichome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMusichomeBinding;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.POJO.MusicListInfo;
import com.example.myapplication.ui.musichome.adapter.ListshowAdatper;
import com.example.myapplication.ui.musichome.adapter.MusicItemClickListener;
import com.example.myapplication.ui.musichome.adapter.MusicItemRemoveClickListener;
import com.example.myapplication.ui.musichome.first_layout.AllMusicActivity;
import com.example.myapplication.ui.musichome.first_layout.RankMusicActivity;
import com.example.myapplication.ui.musichome.first_layout.RecentMusicActivity;
import com.example.myapplication.ui.musichome.second_layout.ShowMusicListActivity;
import com.example.myapplication.ui.musichome.second_layout.secondlayoutActivity;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.musichome.utils.Constant;
import com.example.myapplication.ui.musichome.utils.SPUtils;
import com.example.myapplication.ui.musichome.utils.ScanMusicUtils;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicHomeFragment extends Fragment implements View.OnClickListener {
    private ImageButton ib_allmusic;
    private ImageButton ib_rankmusic;
    private ImageButton ib_recentmusic;
    private FragmentMusichomeBinding binding;

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
    * 音乐列表
    */
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    //判断数据库是否有信息
    private boolean localMusicData = false;
    /*
     * 音乐播放服务
     * */
    private MusicService musicService;
    /*
    * 现在位置
    * */
    private int mCurrentPosition = 0;

    /*
    * 获取传来的值
    * */
    private Bundle bundle;

    /*
    * 避免多次出现oncreateview
    * */
    private View root;
    /*
    * fragment的数据模型
    * */
    private MusicHomeViewModel musicHomeViewModel;
    /*----------------第二部分展示-----------------*/
    /*
    * 创建歌单
    * */
    private ImageButton ib_createlist;
    /*
    * 歌单展示
    * */
    private RecyclerView rvsonglist;
    /*
    * 歌单展示适配器
    * */
    private ListshowAdatper lsAdapter;
    /*
    * 歌单集合
    * */
    private List<MusicListInfo> musicListInfoList;


    private static final int REQUEST_READ_EXTERNAL = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != root) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (null != parent) {
                parent.removeView(root);
                System.out.println("清除缓存");
            }
        } else {
            binding = FragmentMusichomeBinding.inflate(inflater, container, false);
            root = binding.getRoot();
        }
        LitePal.initialize(getActivity());/*配置litepel*/

        /*音乐主页的使用*/
        //获取主页的first部分的控件
        ib_allmusic = (ImageButton) root.findViewById(R.id.all_music);
        ib_rankmusic = (ImageButton) root.findViewById(R.id.rank_music);
        ib_recentmusic = (ImageButton) root.findViewById(R.id.recent_music);

        //设置监听器
        ib_allmusic.setOnClickListener(this::onClick);
        ib_rankmusic.setOnClickListener(this::onClick);
        ib_recentmusic.setOnClickListener(this::onClick);

        /*页面下边的音乐栏信息控件获取*/
        iV_Logo = (ImageView) root.findViewById(R.id.iv_alubum);
        tv_title = (TextView) root.findViewById(R.id.music_name);
        tv_songinfo = (TextView) root.findViewById(R.id.music_info);
        bt_song = (Button) root.findViewById(R.id.song_bt);

        bt_song.setOnClickListener(this::onClick);
        iV_Logo.setOnClickListener(this::onClick);

        /*second部分的控件id获取*/
        ib_createlist = (ImageButton) root.findViewById(R.id.ib_createtable);//歌单创建功能
        rvsonglist = (RecyclerView) root.findViewById(R.id.rv_secondlayout);//歌单展示控件

        ib_createlist.setOnClickListener(this::onClick);

        /*绑定服务*/
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().getApplicationContext().bindService(intent,connection, Context.BIND_AUTO_CREATE);

        /*获得权限*/
        checkStorageManagerPermission();



        /*判断是否是再一次启动activity然后更新音乐栏数据
         * */
        if (musicInfoList.size() != 0) {
            musicHomeViewModel = new ViewModelProvider(getActivity()).get(MusicHomeViewModel.class);
            musicHomeViewModel.getmCurrentposition().observe(getViewLifecycleOwner(), this::changeSonginfo);
            System.out.println("从modleview获得数值===="+musicHomeViewModel.getmCurrentposition().getValue() + "-----" +musicHomeViewModel.getIsplay().getValue() );
            musicHomeViewModel.getIsplay().observe(this, isplay-> {
                if (isplay) {
                    bt_song.setBackground(getActivity().getDrawable(R.drawable.stop));
                }
            });
            musicHomeViewModel.getMusicListInfo().observe(this, musicInfos -> {
                //清除列表数据
                musicInfoList.clear();
                //获得传进来的歌单列表
                musicInfoList.addAll(musicInfos);
            });
        }

        /*
        * 启动广播
        * */
        registerReceiver();

        return root;
    }


    //创建service connection
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
            System.out.println("Music.onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Music.onServiceDisconnected");
        }
    };


    /*-------------获取权限--------------*/
    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                Environment.isExternalStorageManager()) {
            /*Toast.makeText(getActivity(), "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();*/

            getMusicList();/*扫描音乐*/
            getSongShowList();/*展示歌单*/

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION},
                    REQUEST_READ_EXTERNAL);
        }
    }


    /**
    * 获取数据库中的音乐信息
    * */
    private void getMusicList() {
        localMusicData = SPUtils.getBoolean(Constant.LOCAL_MUSIC_DB, false, getActivity());
        //清除列表数据
        musicInfoList.clear();
        //将扫描到的音乐赋值给音乐列表
        if (localMusicData) {
            //有数据读取本地数据库的数据
            musicInfoList = LitePal.findAll(MusicInfo.class);
            /*----------测试是否读取了数据库数据----------------*/
            System.out.println("musichome读取数据库数据中");
        }else {
            ScanMusicUtils scanMusicUtils = new ScanMusicUtils();
            musicInfoList = scanMusicUtils.getMusicInfo(getActivity());
        }
        if (musicInfoList != null && musicInfoList.size() > 0) {
            if (!localMusicData) {
                //添加到本地数据库中
                addLocalDB();
                /*---------测试是否添加数据到数据库中------------*/
                System.out.println("addLocalDB添加到本地数据库中");

            }
        } else {
            Toast.makeText(getActivity(),"没有音乐", Toast.LENGTH_LONG).show();
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
                    System.out.println("musichonme.执行第"+i+"次");
                }
                List<MusicInfo> list = LitePal.findAll(MusicInfo.class);
                if (list.size() > 0) {
                    SPUtils.putBoolean(Constant.LOCAL_MUSIC_DB, true, getActivity());
                    System.out.println("musichome.添加到本地数据库的音乐：" + list.size() + "首");
                }
            }
        });
    }

    @Override
    //控件的点击事件
    public void onClick(View v) {
        //监听按钮，如果点击，就跳转到相应的页面
        Intent intent = new Intent();
        switch (v.getId()) {
            /*默认音乐歌单跳转事件*/
            case R.id.all_music:
                if (musicService != null && musicInfoList.size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable) musicInfoList);
                    intent.putExtras(bundle);
                    System.out.println("music.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                intent.setClass(getActivity(), AllMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.rank_music:
                if (musicService != null && musicInfoList.size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable) musicInfoList);
                    intent.putExtras(bundle);
                    System.out.println("music.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                intent.setClass(getActivity(), RankMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.recent_music:
                if (musicService != null && musicInfoList.size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable) musicInfoList);
                    intent.putExtras(bundle);
                    System.out.println("music.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                intent.setClass(getActivity(), RecentMusicActivity.class);
                startActivity(intent);
                break;

                /*歌曲栏信息播放按钮*/
            case R.id.song_bt:
                //控制音乐 播放和暂停
                if (musicService.getisnull()) {
                    if (musicInfoList.size() == 0) {
                        Toast.makeText(getActivity(), "没有音乐可播放",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //没播放过音乐，点击之后播放第一首
                    musicService.playmusic(mCurrentPosition,musicInfoList);
                    changeSonginfo(mCurrentPosition);
                    musicService.addLocalRankMusic(mCurrentPosition);
                    musicService.addLocalRecentMusic(mCurrentPosition);
                    bt_song.setBackground(getActivity().getDrawable(R.drawable.stop));
                } else {
                    //播放过音乐 暂停或播放
                    if (musicService.getisplay()) {
                        musicService.pauseOrcontinueMusic(1);
                        bt_song.setBackground(getActivity().getDrawable(R.drawable.play));
                    } else {
                        musicService.pauseOrcontinueMusic(2);
                        bt_song.setBackground(getActivity().getDrawable(R.drawable.stop));
                    }
                }
                break;
            case R.id.iv_alubum:
                //显示详细的歌曲操作页面
                if (musicService != null && musicInfoList.size() != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                    bundle.putBoolean("isplay", musicService.getisplay());
                    intent.putExtra("musiclist", (Serializable) musicInfoList);
                    intent.putExtras(bundle);
                    System.out.println("music.mcurrentposition==" + musicService.getmCurrentPosition()+ "----" + musicService.getisplay()) ;
                }
                intent.setClass(getActivity(), SongMusicInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_createtable:
                //创建歌单
                if (musicService != null && musicInfoList.size() != 0) {
                    intent.putExtra("musiclist", (Serializable) musicInfoList);
                }
                intent.setClass(getActivity(), secondlayoutActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 更改下边播放栏的音乐信息
     * */
    public void changeSonginfo(int position){
        System.out.println("--------------------");
        System.out.println("启动了changeSongInfo===="+position);
        System.out.println(musicInfoList.size());
        System.out.println("--------------------");
        musicHomeViewModel.getMusicListInfo().observe(this, musicInfos -> {
            System.out.println("测试是否找到音乐"+musicInfos);
            if (musicInfos.size()<=0){
                return;
            } else {
                //清除列表数据
                musicInfoList.clear();
                //获得传进来的歌单列表
                musicInfoList.addAll(musicInfos);
                System.out.println("执行读取其他歌单");
                System.out.println("modle传进来的musiclist大小："+musicInfoList.size());
            }
        });
        System.out.println(musicInfoList.toString());
        if (musicInfoList.size() != 0) {
            //设置歌曲所在专辑的封面图片
            iV_Logo.setImageBitmap(ScanMusicUtils.getAlbumPicture(getActivity(), musicInfoList.get(position).getData(), 1));
            //设置播放的歌手,专辑名
            tv_songinfo.setText(musicInfoList.get(position).getArtist() + " - " + musicInfoList.get(position).getAlbum());
            //如果内容超过控件，则启用跑马灯效果
            tv_songinfo.setSelected(true);
            //设置播放的歌曲名
            tv_title.setText(musicInfoList.get(position).getTitle());
            //如果内容超过控件，则启用跑马灯效果
            tv_title.setSelected(true);
        }
    }

    /**
     * 注册广播
     * */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("allmusic.msg");
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 接收广播
     * */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("allmusic.msg")) {
                mCurrentPosition = intent.getIntExtra("msg",0);
                changeSonginfo(mCurrentPosition);
                System.out.println("musichome---->接收到了广播");
            }
        }
    };

    private void getSongShowList() {
        localMusicData = SPUtils.getBoolean(Constant.LOCAL_MUSIC_DB, false, getActivity());
        //清除列表数据
        if (musicListInfoList != null) {
            musicListInfoList.clear();
        }
        //将扫描到的歌单赋值给当前的歌单列表对象
        if (localMusicData) {
            //有数据读取本地数据库的数据
            musicListInfoList = LitePal.findAll(MusicListInfo.class);
        }
        if (musicListInfoList != null) {
            showLocalMusicListData();
        } else {
            Toast.makeText(getActivity(),"没有歌单", Toast.LENGTH_LONG).show();
        }
    }

    private void showLocalMusicListData() {
        //指定适配器的布局和数据源
        lsAdapter = new ListshowAdatper(getActivity(), musicListInfoList);
        rvsonglist = (RecyclerView) root.findViewById(R.id.rv_secondlayout);
        //线性布局管理器，可以设置横向还是纵向，RecyclerView默认是纵向的，所以不用处理
        rvsonglist.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置适配器
        rvsonglist.setAdapter(lsAdapter);
        //item的点击事件
        lsAdapter.setMusicItemClickListener(new MusicItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.item_listshow) {
                    Toast.makeText(getActivity(), "点击了" + musicListInfoList.get(position).getId(), Toast.LENGTH_SHORT).show();
                    //设置歌单的点击事件
                    List<MusicListInfo> musicListInfos = LitePal
                            .where("id = ?", String.valueOf(musicListInfoList.get(position).getId()))
                            .find(MusicListInfo.class);
                    Intent intent = new Intent();
                    if (musicService != null && musicInfoList.size() != 0) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("mCurrentPosition", musicService.getmCurrentPosition());
                        bundle.putBoolean("isplay", musicService.getisplay());
                        intent.putExtra("musiclist", (Serializable) musicInfoList);
                        intent.putExtras(bundle);
                    }
                    intent.putExtra("musiclistinfolist", (Serializable) musicListInfos);
                    intent.setClass(getActivity(), ShowMusicListActivity.class);
                    startActivity(intent);
                }
            }
        });
        lsAdapter.setMusicItemRemoveClickListener(new MusicItemRemoveClickListener() {
            /*
            * 实现删除歌单操作
            * */
            @Override
            public void removeitem(View v, int position) {
                if (v.getId() == R.id.ib_delete) {
                    if (position == 0) {
                        Toast.makeText(getActivity(), "官方歌单不可以删除", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Dialog dialog = new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.remove)
                                .setTitle("确定删除？")
                                .setMessage("您确定要删除此歌单吗？")
                                .setPositiveButton("删除",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getActivity(), "删除了" + musicListInfoList.get(position).getId(), Toast.LENGTH_SHORT).show();
                                                LitePal.delete(MusicListInfo.class, musicListInfoList.get(position).getId());//删除当前位置的歌单
                                                getSongShowList();//刷新显示
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getActivity(), "取消删除", Toast.LENGTH_LONG).show();
                                            }
                                        }).create();
                        //设置对话框居中
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                    }

                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        System.out.println("musichome---->退出service和广播");

    }
}

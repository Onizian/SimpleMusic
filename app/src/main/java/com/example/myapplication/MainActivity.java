package com.example.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.musichome.MusicHomeFragment;
import com.example.myapplication.ui.musichome.MusicHomeViewModel;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.setting.SettingFragment;
import com.example.myapplication.ui.setting.SettingViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private MusicService musicService;

    /*所有音乐模块*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());/*获取当前layout*/

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawer;
        NavigationView navigationView = binding.navView;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_developerdeclare, R.id.nav_setting, R.id.nav_softversion, R.id.nav_useragree, R.id.nav_musichome, R.id.nav_user)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        int id = getIntent().getIntExtra("id", 0);
        if (id == 1) {
            /*
             * allmusic给musichomefragment传值
             * */
            Bundle bundle = getIntent().getExtras();
            /*
            * 获得当前的歌曲位置，歌曲播放状态和歌单列表,并将数据传递到主页
            * */
            ViewModelProvider.Factory factory = (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
            MusicHomeViewModel musicHomeViewModel = new ViewModelProvider(this, factory).get(MusicHomeViewModel.class);
            musicHomeViewModel.setmCurrentposition(bundle.getInt("mCurrentPosition"));
            musicHomeViewModel.setIsplay(bundle.getBoolean("isplay"));
            musicHomeViewModel.setMusicInfoList((List<MusicInfo>) getIntent().getSerializableExtra("musiclist"));
            System.out.println(musicHomeViewModel.getMusicListInfo().getValue().toString());
            MusicHomeFragment musicHomeFragment = new MusicHomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_linearlayout, musicHomeFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
            System.out.println("MainActivity.onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("MainActivity.onServiceDisconnected");
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
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}

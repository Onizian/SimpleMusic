package com.example.myapplication.ui.setting;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSettingBinding;
import com.example.myapplication.ui.musichome.service.MusicService;

public class SettingFragment extends Fragment implements View.OnClickListener{


    private View root;
    private FragmentSettingBinding binding;

    /*
    * 定时关闭音乐的控件
    * */
    private TextView tv_timetenhour;
    private TextView tv_timehour;
    private TextView tv_timetenmin;
    private TextView tv_timemin;

    private Button bt_tenhour_add;
    private Button bt_tenhour_down;
    private Button bt_hour_add;
    private Button bt_hour_down;
    private Button bt_tenmin_add;
    private Button bt_tenmin_down;
    private Button bt_min_add;
    private Button bt_min_down;

    private Button bt_controller;
    /*
    * 定时关闭软件的控件
    * */
    private TextView tv_closetenhour;
    private TextView tv_closehour;
    private TextView tv_closetenmin;
    private TextView tv_closemin;

    private Button bt_closetenhour_add;
    private Button bt_closetenhour_down;
    private Button bt_closehour_add;
    private Button bt_closehour_down;
    private Button bt_closetenmin_add;
    private Button bt_closetenmin_down;
    private Button bt_closemin_add;
    private Button bt_closemin_down;

    private Button bt_controller_close;

    private MusicService musicService;

    private SettingViewModel settingViewModel;


    //创建service connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
            System.out.println("Setting.onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Setting.onServiceDisconnected");
        }
    };

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
            binding = FragmentSettingBinding.inflate(inflater, container, false);
            root = binding.getRoot();
        }

        settingViewModel = new ViewModelProvider(getActivity()).get(SettingViewModel.class);
        /*
        * 获取控件id
        * */
            /*
            * 定时关闭音乐部分
            * */
        tv_timetenhour = (TextView) root.findViewById(R.id.time_tenhour);
        tv_timehour = (TextView) root.findViewById(R.id.time_hour);
        tv_timetenmin = (TextView) root.findViewById(R.id.time_tenmin);
        tv_timemin = (TextView) root.findViewById(R.id.time_min);

        bt_tenhour_add = (Button) root.findViewById(R.id.bt_tenhour_add);
        bt_tenhour_down = (Button) root.findViewById(R.id.bt_tenhour_down);
        bt_hour_add = (Button) root.findViewById(R.id.bt_hour_add);
        bt_hour_down = (Button) root.findViewById(R.id.bt_hour_down);
        bt_tenmin_add = (Button) root.findViewById(R.id.bt_tenmin_add);
        bt_tenmin_down = (Button) root.findViewById(R.id.bt_tenmin_down);
        bt_min_add = (Button) root.findViewById(R.id.bt_min_add);
        bt_min_down = (Button) root.findViewById(R.id.bt_min_down);

        bt_controller  = (Button) root.findViewById(R.id.bt_time_controller);
            /*
            * 定时关闭软件部分
            * */
        tv_closetenhour = (TextView) root.findViewById(R.id.close_tenhour);
        tv_closehour = (TextView) root.findViewById(R.id.close_hour);
        tv_closetenmin = (TextView) root.findViewById(R.id.close_tenmin);
        tv_closemin = (TextView) root.findViewById(R.id.close_min);

        bt_closetenhour_add = (Button) root.findViewById(R.id.bt_closetenhour_add);
        bt_closetenhour_down = (Button) root.findViewById(R.id.bt_closetenhour_down);
        bt_closehour_add = (Button) root.findViewById(R.id.bt_closehour_add);
        bt_closehour_down = (Button) root.findViewById(R.id.bt_closehour_down);
        bt_closetenmin_add = (Button) root.findViewById(R.id.bt_closetenmin_add);
        bt_closetenmin_down = (Button) root.findViewById(R.id.bt_closetenmin_down);
        bt_closemin_add = (Button) root.findViewById(R.id.bt_closemin_add);
        bt_closemin_down = (Button) root.findViewById(R.id.bt_closemin_down);

        bt_controller_close  = (Button) root.findViewById(R.id.bt_time_controller_close);

        /*
        * 添加监听事件
        * */
            /*
            * 音乐定时部分
            * */
        bt_tenhour_add.setOnClickListener(this::onClick);
        bt_tenhour_down.setOnClickListener(this::onClick);
        bt_hour_add.setOnClickListener(this::onClick);
        bt_hour_down.setOnClickListener(this::onClick);
        bt_tenmin_add.setOnClickListener(this::onClick);
        bt_tenmin_down.setOnClickListener(this::onClick);
        bt_min_add.setOnClickListener(this::onClick);
        bt_min_down.setOnClickListener(this::onClick);

        bt_controller.setOnClickListener(this::onClick);
            /*
            * app定时部分
            * */
        bt_closetenhour_add.setOnClickListener(this::onClick);
        bt_closetenhour_down.setOnClickListener(this::onClick);
        bt_closehour_add.setOnClickListener(this::onClick);
        bt_closehour_down.setOnClickListener(this::onClick);
        bt_closetenmin_add.setOnClickListener(this::onClick);
        bt_closetenmin_down.setOnClickListener(this::onClick);
        bt_closemin_add.setOnClickListener(this::onClick);
        bt_closemin_down.setOnClickListener(this::onClick);

        bt_controller_close.setOnClickListener(this::onClick);

        /*绑定服务*/
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().getApplicationContext().bindService(intent,connection, Context.BIND_AUTO_CREATE);

        registerReceiver();

        return root;
    }



    /**
    * 将显示的时间转化为毫秒
    * */
    public long timechange() {
        int tenhour = Integer.parseInt(tv_timetenhour.getText().toString());
        int hour = Integer.parseInt(tv_timehour.getText().toString());
        int tenmin = Integer.parseInt(tv_timetenmin.getText().toString());
        int min = Integer.parseInt(tv_timemin.getText().toString());
        long time = (tenhour*10*60+hour*60+tenmin*10+min)*60*1000;
        return time;
    }

    public long closeapptimechange() {
        int tenhour = Integer.parseInt(tv_closetenhour.getText().toString());
        int hour = Integer.parseInt(tv_closehour.getText().toString());
        int tenmin = Integer.parseInt(tv_closetenmin.getText().toString());
        int min = Integer.parseInt(tv_closemin.getText().toString());
        long time = (tenhour*10*60+hour*60+tenmin*10+min)*60*1000;
        return time;
    }




    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_tenhour_add:
                settingViewModel.setaddmText(tv_timetenhour.getText().toString());
                tv_timetenhour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_tenhour_down:
                settingViewModel.setdownmText(tv_timetenhour.getText().toString());
                tv_timetenhour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_hour_add:
                settingViewModel.setaddmText(tv_timehour.getText().toString());
                tv_timehour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_hour_down:
                settingViewModel.setdownmText(tv_timehour.getText().toString());
                tv_timehour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_tenmin_add:
                settingViewModel.setaddmText(tv_timetenmin.getText().toString());
                tv_timetenmin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_tenmin_down:
                settingViewModel.setdownmText(tv_timetenmin.getText().toString());
                tv_timetenmin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_min_add:
                settingViewModel.setaddmText(tv_timemin.getText().toString());
                tv_timemin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_min_down:
                settingViewModel.setdownmText(tv_timemin.getText().toString());
                tv_timemin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_time_controller:
                if (musicService.getisplay()) {
                    musicService.timetodo(timechange());
                    bt_controller.setText("停止定时");
                } else {
                    Toast.makeText(getActivity(), "目前没有音乐在播放", Toast.LENGTH_SHORT).show();
                    bt_controller.setText("开启定时");
                }
                break;

            case R.id.bt_closetenhour_add:
                settingViewModel.setaddmText(tv_closetenhour.getText().toString());
                tv_closetenhour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closetenhour_down:
                settingViewModel.setdownmText(tv_closetenhour.getText().toString());
                tv_closetenhour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closehour_add:
                settingViewModel.setaddmText(tv_closehour.getText().toString());
                tv_closehour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closehour_down:
                settingViewModel.setdownmText(tv_closehour.getText().toString());
                tv_closehour.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closetenmin_add:
                settingViewModel.setaddmText(tv_closetenmin.getText().toString());
                tv_closetenmin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closetenmin_down:
                settingViewModel.setdownmText(tv_closetenmin.getText().toString());
                tv_closetenmin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closemin_add:
                settingViewModel.setaddmText(tv_closemin.getText().toString());
                tv_closemin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_closemin_down:
                settingViewModel.setdownmText(tv_closemin.getText().toString());
                tv_closemin.setText(settingViewModel.getText().getValue());
                break;
            case R.id.bt_time_controller_close:
                if (musicService.getclosesignal()) {
                    bt_controller_close.setText("开启定时");
                    musicService.stopcloseapp();
                } else {
                    musicService.closeapptodo(closeapptimechange());
                    bt_controller_close.setText("停止定时");
                }
                break;

        }
    }

    /**
     * 注册广播
     * */
    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("timerplay");
        intentFilter.addAction("closeappplay");
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 接收广播
     * */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("timerplay")) {
                if (intent.getBooleanExtra("timer", false)) {
                    bt_controller.setText("停止定时");
                } else {
                    bt_controller.setText("开启定时");
                }
            }
            if (action.equals("closeappplay")) {
                if (intent.getBooleanExtra("closeapp", false)) {
                    bt_controller_close.setText("停止定时");
                    System.out.println("<----------app正常结束---------->");
                }
            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*退出广播*/
        getActivity().unregisterReceiver(mReceiver);
        binding = null;
    }

}

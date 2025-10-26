package com.example.myapplication.ui.user;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentUserBinding;
import com.example.myapplication.ui.musichome.SongMusicInfoActivity;
import com.example.myapplication.ui.musichome.service.MusicService;
import com.example.myapplication.ui.user.POJO.User;
import com.example.myapplication.ui.user.Utils.SqlUtils;

import org.litepal.LitePal;

import java.io.Serializable;

public class UserFragment extends Fragment implements View.OnClickListener {

    private View root;
    private FragmentUserBinding binding;
    private ImageView iv_sex;//性别
    private TextView usernumebr;//账号
    private TextView userid;//用户id
    private TextView useremail;//邮箱
    private ImageButton ib_edit;//编辑个签的按钮
    private EditText usertext;//个签显示
    private TextView changeuserinfo;//更改用户邮箱、密保
    private TextView changeuserpassword;//更改用户密码
    private TextView logout;//退出登录

    private UserViewModel userViewModel;
    private User user;
    private MusicService musicService;


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
            binding = FragmentUserBinding.inflate(inflater, container, false);
            root = binding.getRoot();
        }
        LitePal.initialize(getActivity());/*配置litepel*/

        /*获取控件id*/
        iv_sex = (ImageView) root.findViewById(R.id.iv_sex);
        usernumebr = (TextView) root.findViewById(R.id.tv_usernumber);
        userid = (TextView) root.findViewById(R.id.tv_userid);
        useremail = (TextView) root.findViewById(R.id.tv_useremail);
        ib_edit = (ImageButton) root.findViewById(R.id.ib_edit);
        usertext = (EditText) root.findViewById(R.id.et_usertext);
        changeuserinfo = (TextView) root.findViewById(R.id.tv_changeuserinfo);
        changeuserpassword = (TextView) root.findViewById(R.id.tv_changeuserpassword);
        logout = (TextView) root.findViewById(R.id.tv_logout);

        ib_edit.setOnClickListener(this);
        changeuserinfo.setOnClickListener(this);
        changeuserpassword.setOnClickListener(this);
        logout.setOnClickListener(this);

        /*绑定服务*/
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().getApplicationContext().bindService(intent,connection, Context.BIND_AUTO_CREATE);

        /*判断目前是否有账号是否已经登录了*/
        SqlUtils sqlUtils = new SqlUtils();
        if (sqlUtils.Selectislogin()) {
            userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
            userViewModel.getUser().observe(this, user -> {
                /*获取目前登录的用户信息*/
                this.user = user;
                System.out.println(this.user);
                userid.setText(String.valueOf(user.getId()));
                useremail.setText(user.getEmail());
                usernumebr.setText(user.getUserNumber());
                usertext.setText(user.getUsersignature());
                if (user.getSex().equals("男性")) {
                    iv_sex.setImageResource(R.drawable.sex_man);
                } else {
                    iv_sex.setImageResource(R.drawable.sex_woman);
                }
            } );
        }
        return root;
    }

    //创建service connection
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();//获取service实例
            if (!(new SqlUtils().Selectislogin())) {
                Intent intent = new Intent(getActivity(), MusicService.class);
                /*如果没有登录则跳转到登录页面*/
                intent.putExtra("musiclist", (Serializable) musicService.getMusicInfoList());
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

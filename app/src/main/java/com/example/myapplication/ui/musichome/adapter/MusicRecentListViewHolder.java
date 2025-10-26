package com.example.myapplication.ui.musichome.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class MusicRecentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView songname;
    TextView singer;
    TextView duration;
    TextView position;

    private MusicItemClickListener musicItemClickListener;

    public MusicRecentListViewHolder(View itemView, MusicItemClickListener musicItemClickListener) {
        super(itemView);
        /*获取控件id*/
        songname = (TextView) itemView.findViewById(R.id.tv_recentsong_name);
        singer = (TextView) itemView.findViewById(R.id.tv_recentsinger);
        duration = (TextView) itemView.findViewById(R.id.tv_recentduration_time);
        position = (TextView) itemView.findViewById(R.id.tv_recentposition);
        //将全局的监听赋值给接口
        this.musicItemClickListener = musicItemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (musicItemClickListener != null) {
            musicItemClickListener.onItemClick(v, getPosition());
        }
    }
}

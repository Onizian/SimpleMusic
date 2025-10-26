package com.example.myapplication.ui.musichome.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class MusicRankListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView songname;
    TextView singer;
    TextView duration;
    TextView position;
    TextView playnum;

    private MusicItemClickListener musicItemClickListener;
    public MusicRankListViewHolder(View itemView, MusicItemClickListener musicItemClickListener) {
        super(itemView);
        /*获取控件id*/
        songname = (TextView) itemView.findViewById(R.id.tv_ranksong_name);
        singer = (TextView) itemView.findViewById(R.id.tv_ranksinger);
        duration = (TextView) itemView.findViewById(R.id.tv_rankduration_time);
        position = (TextView) itemView.findViewById(R.id.tv_rankposition);
        playnum = (TextView) itemView.findViewById(R.id.tv_playnum);
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

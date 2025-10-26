package com.example.myapplication.ui.musichome.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;

public class MusicListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView songname;
    TextView singer;
    TextView duration;
    TextView position;

    ImageButton love;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemAddsongClickListener musicItemAddsongClickListener;

    public MusicListViewHolder(View itemView, MusicItemClickListener musicItemClickListener, MusicItemAddsongClickListener musicItemAddsongClickListener) {
        super(itemView);
        /*获取控件id*/
        songname = (TextView) itemView.findViewById(R.id.tv_song_name);
        singer = (TextView) itemView.findViewById(R.id.tv_singer);
        duration = (TextView) itemView.findViewById(R.id.tv_duration_time);
        position = (TextView) itemView.findViewById(R.id.tv_position);
        love = (ImageButton) itemView.findViewById(R.id.ib_love);

        //将全局的监听赋值给接口
        this.musicItemClickListener = musicItemClickListener;
        itemView.setOnClickListener(this);
        this.musicItemAddsongClickListener = musicItemAddsongClickListener;
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicItemClickListener != null) {
                    musicItemAddsongClickListener.addsong(love, getPosition());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (musicItemClickListener != null) {
            musicItemClickListener.onItemClick(v, getPosition());
        }
    }
}


package com.example.myapplication.ui.musichome.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class AddMusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView songname;
    TextView singer;
    TextView duration;
    TextView position;

    ImageButton addmusic;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemAddsongClickListener musicItemAddsongClickListener;

    public AddMusicViewHolder(View itemView, MusicItemClickListener musicItemClickListener, MusicItemAddsongClickListener musicItemAddsongClickListener) {
        super(itemView);
        /*获取控件id*/
        songname = (TextView) itemView.findViewById(R.id.tv_addsong_name);
        singer = (TextView) itemView.findViewById(R.id.tv_addsinger);
        duration = (TextView) itemView.findViewById(R.id.tv_addduration_time);
        position = (TextView) itemView.findViewById(R.id.tv_add_position);
        addmusic = (ImageButton) itemView.findViewById(R.id.ib_add);

        //将全局的监听赋值给接口
        this.musicItemClickListener = musicItemClickListener;
        itemView.setOnClickListener(this);
        this.musicItemAddsongClickListener = musicItemAddsongClickListener;
        addmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicItemClickListener != null) {
                    musicItemAddsongClickListener.addsong(addmusic, getPosition());
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

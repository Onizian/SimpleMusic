package com.example.myapplication.ui.musichome.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;


public class ShowMusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView position;

    TextView songname;

    TextView singer;

    TextView duration;

    ImageButton remove;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemRemoveClickListener musicItemRemoveClickListener;

    public ShowMusicViewHolder(@NonNull View itemView, MusicItemClickListener musicItemClickListener, MusicItemRemoveClickListener musicItemRemoveClickListener) {
        super(itemView);
        /*获取控件id*/
        position = (TextView) itemView.findViewById(R.id.tv_itemposition);
        songname = (TextView) itemView.findViewById(R.id.tv_item_song_name);
        singer = (TextView) itemView.findViewById(R.id.tv_item_singer);
        duration = (TextView) itemView.findViewById(R.id.tv_item_duration_time);
        remove = (ImageButton) itemView.findViewById(R.id.ib_itemdelete);

        /*将全局的监听赋值给接口*/
        this.musicItemClickListener = musicItemClickListener;
        this.musicItemRemoveClickListener = musicItemRemoveClickListener;
        itemView.setOnClickListener(this);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicItemClickListener != null) {
                    musicItemRemoveClickListener.removeitem(remove, getPosition());
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

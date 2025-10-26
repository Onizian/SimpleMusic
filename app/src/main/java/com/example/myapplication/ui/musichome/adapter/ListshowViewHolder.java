package com.example.myapplication.ui.musichome.adapter;


import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class ListshowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView iv_listshow;
    TextView tv_listshow_name;
    TextView tv_listshow_profile;

    ImageButton ib_delete;

    private MusicItemClickListener musicItemClickListener;
    private MusicItemRemoveClickListener musicItemRemoveClickListener;

    public ListshowViewHolder(@NonNull View itemView, MusicItemClickListener musicItemClickListener, MusicItemRemoveClickListener musicItemRemoveClickListener) {
        super(itemView);
        /*获取控件id*/
        /*iv_listshow = (ImageView) itemView.findViewById(R.id.iv_showlist);*/
        tv_listshow_name = (TextView) itemView.findViewById(R.id.tv_showlist_name);
        tv_listshow_profile = (TextView) itemView.findViewById(R.id.tv_showlist_profile);
        ib_delete = (ImageButton) itemView.findViewById(R.id.ib_delete);

        /*将全局的监听赋值给接口*/
        this.musicItemClickListener = musicItemClickListener;
        this.musicItemRemoveClickListener = musicItemRemoveClickListener;
        itemView.setOnClickListener(this);
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicItemClickListener != null) {
                    musicItemRemoveClickListener.removeitem(ib_delete, getPosition());
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

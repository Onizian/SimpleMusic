package com.example.myapplication.ui.musichome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicInfo;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class AddMusicAdatper extends RecyclerView.Adapter<AddMusicViewHolder> {

    private List<MusicInfo> musicInfoList = new ArrayList<>();

    private Context context;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemAddsongClickListener musicItemAddsongClickListener;

    public AddMusicAdatper(Context context, List<MusicInfo> musicInfoList) {
        this.context = context;
        if (musicInfoList != null) {
            this.musicInfoList.clear();
            this.musicInfoList.addAll(musicInfoList);
            notifyDataSetChanged();
        }
    }

    /*加载布局*/
    @NonNull
    @Override
    public AddMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.addmusic_list, parent, false);
        return new AddMusicViewHolder(view, musicItemClickListener, musicItemAddsongClickListener);
    }

    /*给控件赋值*/
    @Override
    public void onBindViewHolder(@NonNull AddMusicViewHolder holder, int position) {
        holder.songname.setText(musicInfoList.get(position).getTitle());
        holder.singer.setText(musicInfoList.get(position).getArtist()+"-"+musicInfoList.get(position).getAlbum());
        holder.duration.setText(DateTimeUtils.formatTime(musicInfoList.get(position).getDuration()));
        holder.position.setText(holder.getAdapterPosition() + 1 + "");
        /*点击后改变文字颜色*/
        if (musicInfoList.get(position).isCheck()) {
            holder.songname.setTextColor(Color.parseColor("#000000"));
            holder.singer.setTextColor(Color.parseColor("#000000"));
            holder.duration.setTextColor(Color.parseColor("#000000"));
            holder.position.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.songname.setTextColor(Color.parseColor("#53565c"));
            holder.singer.setTextColor(Color.parseColor("#53565c"));
            holder.duration.setTextColor(Color.parseColor("#53565c"));
            holder.position.setTextColor(Color.parseColor("#53565c"));
        }
    }



    /*加载条数*/
    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }

    public void setItemClickListener(MusicItemClickListener musicItemClickListener) {
        this.musicItemClickListener = musicItemClickListener;
    }

    public void setMusicItemAddsongClickListener(MusicItemAddsongClickListener musicItemAddsongClickListener) {
        this.musicItemAddsongClickListener = musicItemAddsongClickListener;
    }
    /*--------刷新数据---------*/
    public void changState() {
        notifyDataSetChanged();
    }
}

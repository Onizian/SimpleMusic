package com.example.myapplication.ui.musichome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.SongListInfo;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowMusicAdapter extends RecyclerView.Adapter<ShowMusicViewHolder> {

    private List<SongListInfo> songListInfoList = new ArrayList<>();

    private Context context;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemRemoveClickListener musicItemRemoveClickListener;

    public ShowMusicAdapter(Context context, List<SongListInfo> songListInfoList) {
        this.context = context;
        if (songListInfoList != null) {
            this.songListInfoList.clear();
            this.songListInfoList.addAll(songListInfoList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ShowMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.musiclistshow_item, parent, false);
        //将全局的监听传递给holder
        return new ShowMusicViewHolder(view, musicItemClickListener, musicItemRemoveClickListener);
    }

    @Override
    public void onBindViewHolder(ShowMusicViewHolder holder, int position) {
        holder.position.setText(holder.getAdapterPosition() + 1 + "");
        holder.singer.setText(songListInfoList.get(position).getArtist()+"-"+ songListInfoList.get(position).getAlbum());
        holder.duration.setText(DateTimeUtils.formatTime(songListInfoList.get(position).getDuration()));
        holder.songname.setText(songListInfoList.get(position).getTitle());
        /*点击后改变文字颜色*/
        if (songListInfoList.get(position).isCheck()) {
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

    @Override
    public int getItemCount() {
        return songListInfoList.size();
    }

    public void setMusicItemClickListener(MusicItemClickListener musicItemClickListener) {
        this.musicItemClickListener = musicItemClickListener;
    }

    public void setMusicItemRemoveClickListener(MusicItemRemoveClickListener musicItemRemoveClickListener) {
        this.musicItemRemoveClickListener = musicItemRemoveClickListener;
    }

    /*--------刷新数据---------*/
    public void changState() {
        notifyDataSetChanged();
    }
}

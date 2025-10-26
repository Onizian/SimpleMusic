package com.example.myapplication.ui.musichome.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicRankInfo;
import com.example.myapplication.ui.musichome.POJO.MusicRecentInfo;
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicRecentListAdapter extends RecyclerView.Adapter<MusicRecentListViewHolder>  {

    private List<MusicRecentInfo> musicRecentInfoList = new ArrayList<>();

    private Context context;

    private MusicItemClickListener musicItemClickListener;

    public MusicRecentListAdapter(Context context, List<MusicRecentInfo> musicRecentInfoList) {
        this.context = context;
        if (musicRecentInfoList != null) {
            this.musicRecentInfoList.clear();
            this.musicRecentInfoList.addAll(musicRecentInfoList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MusicRecentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recentmusic_list, parent, false);
        return new MusicRecentListViewHolder(view, musicItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecentListViewHolder holder, int position) {
        holder.songname.setText(musicRecentInfoList.get(position).getTitle());
        holder.singer.setText(musicRecentInfoList.get(position).getArtist()+"-"+musicRecentInfoList.get(position).getAlbum());
        holder.duration.setText(DateTimeUtils.formatTime(musicRecentInfoList.get(position).getDuration()));
        holder.position.setText(holder.getAdapterPosition() + 1 + "");
        /*点击后改变文字颜色*/
        if (musicRecentInfoList.get(position).isCheck()) {
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
        return musicRecentInfoList.size();
    }

    public void setItemClickListener(MusicItemClickListener musicItemClickListener) {
        this.musicItemClickListener = musicItemClickListener;
    }

    /*--------刷新数据---------*/
    public void changState() {
        notifyDataSetChanged();
    }

}

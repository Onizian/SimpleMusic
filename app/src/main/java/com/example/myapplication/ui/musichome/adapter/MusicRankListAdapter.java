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
import com.example.myapplication.ui.musichome.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicRankListAdapter extends RecyclerView.Adapter<MusicRankListViewHolder> {

    private List<MusicRankInfo> musicRankInfoList = new ArrayList<>();

    private Context context;

    private MusicItemClickListener musicItemClickListener;

    public MusicRankListAdapter(Context context, List<MusicRankInfo> musicRankInfoList) {
        this.context = context;
        if (musicRankInfoList != null) {
            this.musicRankInfoList.clear();
            this.musicRankInfoList.addAll(musicRankInfoList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MusicRankListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rankmusic_list,parent,false);
        return new MusicRankListViewHolder(view, musicItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRankListViewHolder holder, int position) {
        holder.songname.setText(musicRankInfoList.get(position).getTitle());
        holder.singer.setText(musicRankInfoList.get(position).getArtist()+"-"+musicRankInfoList.get(position).getAlbum());
        holder.duration.setText(DateTimeUtils.formatTime(musicRankInfoList.get(position).getDuration()));
        holder.position.setText(holder.getAdapterPosition() + 1 + "");
        holder.playnum.setText(musicRankInfoList.get(position).getPlaynum() + "");
        /*点击后改变文字颜色*/
        if (musicRankInfoList.get(position).isCheck()) {
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
        return musicRankInfoList.size();
    }

    public void setItemClickListener(MusicItemClickListener musicItemClickListener) {
        this.musicItemClickListener = musicItemClickListener;
    }

    /*--------刷新数据---------*/
    public void changState() {
        notifyDataSetChanged();
    }
}

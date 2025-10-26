package com.example.myapplication.ui.musichome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.musichome.POJO.MusicListInfo;

import java.util.ArrayList;
import java.util.List;

public class ListshowAdatper extends RecyclerView.Adapter<ListshowViewHolder> {

    private List<MusicListInfo> musicListInfoList = new ArrayList<>();

    private Context context;

    private MusicItemClickListener musicItemClickListener;

    private MusicItemRemoveClickListener musicItemRemoveClickListener;

    public ListshowAdatper(Context context, List<MusicListInfo> musicListInfoList) {
        this.context = context;
        if (musicListInfoList != null) {
            this.musicListInfoList.clear();
            this.musicListInfoList.addAll(musicListInfoList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ListshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.secondlistshow_list, parent, false);
        //将全局的监听传递给holder
        return new ListshowViewHolder(view, musicItemClickListener, musicItemRemoveClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListshowViewHolder holder, int position) {
       /* holder.iv_listshow.setImageBitmap();*/
        holder.tv_listshow_name.setText(musicListInfoList.get(position).getTablename());
        holder.tv_listshow_profile.setText(musicListInfoList.get(position).getTableprofile());
    }

    @Override
    public int getItemCount() {
        return musicListInfoList.size();
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
